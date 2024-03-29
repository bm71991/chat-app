package com.bm.android.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStore
import com.bm.android.chat.conversations.conversation.ChatFragment
import com.bm.android.chat.conversations.conversation.ChatViewModel
import com.bm.android.chat.conversations.conversation.MessageChangeDialog
import com.bm.android.chat.conversations.convo_list.ConvosFragment
import com.bm.android.chat.conversations.convo_list.ConvosViewModel
import com.bm.android.chat.conversations.new_conversation.NewConvoFragment
import com.bm.android.chat.conversations.new_conversation.RecipientDialog
import com.bm.android.chat.current_friends.FriendsFragment
import com.bm.android.chat.current_friends.FriendsViewModel
import com.bm.android.chat.friend_requests.FriendRequestsViewModel
import com.bm.android.chat.friend_requests.ReceivedRequestsFragment
import com.bm.android.chat.friend_requests.RequestsPagerFragment
import com.bm.android.chat.friend_search.FriendSearchFragment
import com.bm.android.chat.user_access.fragments.*
import com.google.firebase.firestore.DocumentChange
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import kotlinx.android.synthetic.main.fragment_container.*


class ChatActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    LoginFragment.LoginFragmentInterface,
    EmailSignupFragment.EmailSignupFragmentInterface,
    UsernameFragment.UsernameFragmentInterface,
    UsernameRegisteredFragment.UsernameRegisteredFragmentInterface,
    EmailSignupSuccessFragment.EmailSignupSuccessFragmentInterface,
    ConvosFragment.ConvosFragmentInterface,
    NewConvoFragment.NewConvoFragmentInterface,
    ChatFragment.ChatFragmentInterface,
    RequestsPagerFragment.RequestsPagerInterface,
    FriendSearchFragment.FriendSearchInterface,
    FriendsFragment.FriendsInterface,
    ReceivedRequestsFragment.ReceivedRequestsFragmentInterface {
    private val TAG = "mainLog"
    private val fm: FragmentManager by lazy {
        supportFragmentManager
    }
    private val FIRST_FRAGMENT = "loginFragment"
    private val chatViewModel by lazy {
        ViewModelProviders.of(this).get(ChatViewModel::class.java)
    }
    private val friendRequestsViewModel by lazy {
        ViewModelProviders.of(this).get(FriendRequestsViewModel::class.java)
    }
    private val friendsViewModel by lazy {
        ViewModelProviders.of(this).get(FriendsViewModel::class.java)
    }
    private lateinit var badgeDrawable:BadgeDrawerArrowDrawable
    private lateinit var mTwitterAuthConfig: TwitterAuthConfig
    private val mAuth = FirebaseAuth.getInstance()
    private val CONVO_TAG = "convoTag"


    private val toggle by lazy {
        ActionBarDrawerToggle(this, drawer_layout,toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeTwitter()
        setContentView(R.layout.fragment_container)
        configureNavigationView()
        val currentUser = mAuth.currentUser
        setNewChatMessageObserver()
        setNewReceivedRequestsObserver()
        setNewFriendsCountObserver()


        if (currentUser != null)  {
            if (currentUser.displayName.isNullOrEmpty())    {
                addFirstFragment(UsernameFragment())
            } else {
                addFirstFragmentWithTag(ConvosFragment(), CONVO_TAG)
            }
        } else {
            addFirstFragment(LoginFragment())
        }
    }

    private fun setNewFriendsCountObserver() {
        friendsViewModel.getNewFriendsCountStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                friendsViewModel.clearNewFriendsCountStatus()
                if (result.status == "NEW FRIENDS COUNT") {
                    setNavDrawerItemCount(R.id.current_friends, result.payload)
                    setTotalNewMessages()
                }
            }
        })
    }

    private fun setNewReceivedRequestsObserver()    {
        friendRequestsViewModel.getNewRequestCountStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                friendRequestsViewModel.clearNewRequestCountStatus()
                if (result.status == "NEW REQUEST COUNT") {
                    setNavDrawerItemCount(R.id.friend_requests, result.payload)
                    setTotalNewMessages()
                }
            }
        })
    }

    private fun setNewChatMessageObserver() {
        chatViewModel.getNewChatMessageCountStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                chatViewModel.clearNewChatMessageCountStatus()
                if (result.status == "NEW_CHAT_MESSAGE_COUNT") {
                    Log.d("newMessageCount", "payload: ${result.payload}")
                    setNavDrawerItemCount(R.id.conversations, result.payload)
                    setTotalNewMessages()
                }
            }
        })
    }


    /******************************************
     * Methods for configuring taskbar menu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_convo, menu)
        val currentFragment = fm.findFragmentById(R.id.fragment_container)

        if (currentFragment != null && currentFragment.tag == CONVO_TAG)    {
            menu.setGroupVisible(R.id.add_convo_group, true)
        } else {
            menu.setGroupVisible(R.id.add_convo_group, false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.make_convo -> onStartNewConvoFragment()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onStartNewConvoFragment()   {
        replaceFragmentAddToStack(NewConvoFragment())
    }


    private fun addFirstFragment(fragment: Fragment) {
        fm.beginTransaction()
            .add(R.id.fragment_container, fragment, FIRST_FRAGMENT)
            .commit()
    }

    private fun addFirstFragmentWithTag(fragment: Fragment, tag:String) {
        fm.beginTransaction()
            .add(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        fm.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun replaceFragmentWithTag(fragment:Fragment, tag:String)   {
        fm.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun replaceFragmentAddToStack(fragment: Fragment) {
        fm.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /*************************************************************
     * Navigation methods used in fragments. Abstracts the
     * fragment manager transactions from the fragments themselves
     */

    /*Used in LoginFragment*/
    override fun onStartSignupFragment()    {
        replaceFragmentAddToStack(EmailSignupFragment())
    }

    override fun onStartUsernameFragment() {
        /*Should not be able to hit back button to go back to Login - signed in at this point */
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        replaceFragment(UsernameFragment())
    }

    override fun disableNavDrawer() {
        enableNavDrawer(false)
    }

    /* Used in UsernameFragment */
    override fun onStartUsernameRegisteredFragment() {
        replaceFragment(UsernameRegisteredFragment())
    }

    /*Used in UsernameRegisteredFragment*/
    override fun onStartConvosFragment() {
        /*Should not be able to hit back button and go back to login/signin flow */
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }

        replaceFragmentWithTag(ConvosFragment(), CONVO_TAG)
    }

    /*Used in EmailSignupFragment*/
    override fun onStartSignupSuccessFragment() {

        /* pop/reverse onStartSignupFragment() transaction */
        fm.popBackStack()
        /*replace LoginFragment with EmailSignupSuccessFragment and add transaction to stack*/
        replaceFragmentAddToStack(EmailSignupSuccessFragment())
    }

    /*On navigating from EmailSignupSuccessFragment to LoginFragment*/
    override fun emailSuccessToLogin() {
        /*reverses replaceFragmentAddToStack(EmailSignupSuccessFragment())*/
        fm.popBackStack()
    }

    /*Used in ConvosFragment*/
    override fun showNavDrawer()    {
        enableNavDrawer(true)
    }

    override fun setUsernameInNavDrawer() {
        val headerView = nav_view.getHeaderView(0)
        val userTextView = headerView.findViewById<TextView>(R.id.username_header_text)
        userTextView.text = mAuth.currentUser?.displayName
    }

    override fun setNavDrawerItemCount(itemId:Int, newCount:Int?)    {
        if (newCount != null)   {
            val drawerText = if (newCount == 0) "" else newCount.toString()
            val v = nav_view.menu.findItem(itemId).actionView as TextView
            v.text = drawerText
        }
    }

    /*Used in NewConvoFragment*/
    override fun showProspectiveRecipientDialog()    {
        val recipientDialog = RecipientDialog()
        val convoFragment = fm.findFragmentById(R.id.fragment_container)
        recipientDialog.setTargetFragment(convoFragment, 1)
        recipientDialog.show(fm, "recipientDialog")
    }

    override fun onStartChatFragment()   {
        replaceFragment(ChatFragment())
    }

    override fun changeActionbarTitle(title: String) {
        supportActionBar?.title = title
    }

    /*Used in ChatFragment*/
    override fun toMessageChangeDialog() {
        val chatFragment = fm.findFragmentById(R.id.fragment_container)
        val messageChangeDialog = MessageChangeDialog()
        messageChangeDialog.setTargetFragment(chatFragment, 1)
        messageChangeDialog.show(fm, "MessageChangeDialog")
    }


    /*Used in Navigation Drawer*/
    private fun onStartRequestsPagerFragment()    {
        replaceFragmentWithTag(ReceivedRequestsFragment(), "RECEIVED_REQUESTS")
    }

    override fun isFragmentVisible(tag:String):Boolean {
        val fragment = fm.findFragmentById(R.id.fragment_container)
        Log.d("fragmentTest","fragment = RECEIVED_REQUESTS ${fragment?.tag == tag}")
        return (fragment?.tag == tag)
    }

    private fun onStartLoginFragment()  {
        replaceFragment(LoginFragment())
    }

    private fun onStartFriendSearchFragment()    {
        replaceFragment(FriendSearchFragment())
    }

    private fun onStartFriendsFragment()    {
        replaceFragment(FriendsFragment())
    }

    override fun onBackPressed() {
        closeNavDrawer()
        /*******************************************************
         * pop fragment transaction stack if it is not empty, else
         * finish the activity.
         */
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            this.finish()
        }
    }

    /*Used to propagate data from [Activity].onActivityResult to [Fragment].onActivityResult */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("test", "requestCode == $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    /****************************************************************************
     * Required for Firebase login via Twitter
     */
    private fun initializeTwitter()  {
        mTwitterAuthConfig = TwitterAuthConfig(getString(R.string.twitter_consumer_key),
            getString(R.string.twitter_consumer_secret))

        val twitterConfig = TwitterConfig.Builder(this)
            .twitterAuthConfig(mTwitterAuthConfig)
            .build()
        Twitter.initialize(twitterConfig)
    }

    /***************************************************************************
     * NavigationView Settings
     */
    private fun configureNavigationView()   {
        setSupportActionBar(toolbar)
        nav_view.setNavigationItemSelectedListener(this)

        drawer_layout.addDrawerListener(toggle)
        badgeDrawable = BadgeDrawerArrowDrawable(supportActionBar?.themedContext!!)
        toggle.syncState()

        toggle.drawerArrowDrawable = badgeDrawable
        badgeDrawable.isEnabled = false

        Log.d(TAG, "nav drawer disabled")
    }

    private fun setTotalNewMessages()  {
        val newCount = chatViewModel.newChatMessageCountTotal +
                       friendRequestsViewModel.newRequestCount +
                       friendsViewModel.newFriendsCount
        if (newCount < 1) {
            badgeDrawable.isEnabled = false
        } else {

            val textToDisplay = newCount.toString()
            badgeDrawable.text = textToDisplay
            badgeDrawable.isEnabled = true
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        closeNavDrawer()
        invalidateOptionsMenu()
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }

        when (item.itemId)  {
            R.id.friend_search -> onStartFriendSearchFragment()
            R.id.friend_requests -> onStartRequestsPagerFragment()
            R.id.conversations -> onStartConvosFragment()
            R.id.current_friends -> onStartFriendsFragment()
            R.id.log_out -> {
                //Log out for Firebase
                mAuth.signOut()
                //Log out for Facebook
                LoginManager.getInstance().logOut()

                chatViewModel.clearNewMessageListenerAndCount()
                friendRequestsViewModel.clearReceivedRequestsListenerAndCount()
                friendsViewModel.clearNewFriendsCountListener()
                ViewModelStore().clear()
                onStartLoginFragment()
            }
        }
        return true
    }

    /*****************************************************************************
     * Disables or enables the NavDrawer
     */
    private fun enableNavDrawer(enabled:Boolean)   {
        val mode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED
        else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawer_layout.setDrawerLockMode(mode)
        toggle.setDrawerIndicatorEnabled(enabled)
    }

    private fun closeNavDrawer()    {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))   {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
    }
}
