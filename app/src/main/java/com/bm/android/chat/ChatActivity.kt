package com.bm.android.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelStore
import com.bm.android.chat.conversations.ConvosPagerFragment
import com.bm.android.chat.friend_requests.RequestsPagerFragment
import com.bm.android.chat.friend_search.FriendSearchFragment
import com.bm.android.chat.user_access.fragments.*
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
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
                     ConvosPagerFragment.ConvosPagerFragmentInterface {
    private val TAG = "mainLog"
    private val fm: FragmentManager by lazy {
        supportFragmentManager
    }
    private val FIRST_FRAGMENT = "loginFragment"
    private lateinit var mTwitterAuthConfig: TwitterAuthConfig
    private val mAuth = FirebaseAuth.getInstance()
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

        if (currentUser != null)  {
            Log.i(TAG, "display name ChatActivity onCreate = ${currentUser.displayName}")
            if (currentUser.displayName.isNullOrEmpty())    {
                addFirstFragment(UsernameFragment())
            } else {
                addFirstFragment(ConvosPagerFragment())
            }
        } else {
            addFirstFragment(LoginFragment())
        }
    }

    private fun addFirstFragment(fragment: Fragment) {
        fm.beginTransaction()
            .add(R.id.fragment_container, fragment, FIRST_FRAGMENT)
            .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        fm.beginTransaction()
            .replace(R.id.fragment_container, fragment)
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
    override fun onStartConvosPagerFragment() {
        /*Should not be able to hit back button and go back to login/signin flow */
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        replaceFragment(ConvosPagerFragment())
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

    /*Used in ConvosPagerFragment*/
    override fun showNavDrawer()    {
        enableNavDrawer(true)
    }

    override fun setUsernameInNavDrawer() {
        val headerView = nav_view.getHeaderView(0)
        val userTextView = headerView.findViewById<TextView>(R.id.username_header_text)
        userTextView.text = mAuth.currentUser!!.displayName
    }

    /*Used in Navigation Drawer*/
    private fun onStartRequestsPagerFragment()    {
        replaceFragment(RequestsPagerFragment())
    }

    private fun onStartLoginFragment()  {
        replaceFragment(LoginFragment())
    }

    private fun onStartFriendSearchFragment()    {
        replaceFragment(FriendSearchFragment())
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
        toggle.syncState()
        Log.d(TAG, "nav drawer disabled")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        closeNavDrawer()
        when (item.itemId)  {
            R.id.friend_search -> onStartFriendSearchFragment()
            R.id.friend_requests -> onStartRequestsPagerFragment()
            R.id.conversations -> onStartConvosPagerFragment()
            R.id.log_out -> {
                //Log out for Firebase
                mAuth.signOut()
                //Log out for Facebook
                LoginManager.getInstance().logOut()
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
