package com.bm.android.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bm.android.chat.chat_messaging.StartFragment
import com.bm.android.chat.conversations.ConvosPagerFragment
import com.bm.android.chat.friend_requests.RequestsPagerFragment
import com.bm.android.chat.user_access.fragments.*
import com.google.firebase.auth.FirebaseAuth
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig


class ChatActivity : AppCompatActivity(),
                     LoginFragment.LoginFragmentInterface,
                     EmailSignupFragment.EmailSignupFragmentInterface,
                     UsernameFragment.UsernameFragmentInterface,
                     UsernameRegisteredFragment.UsernameRegisteredFragmentInterface,
                     EmailSignupSuccessFragment.EmailSignupSuccessFragmentInterface {
    private val TAG = "mainLog"
    private val fm: FragmentManager by lazy {
        supportFragmentManager
    }
    private val FIRST_FRAGMENT = "loginFragment"
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var mTwitterAuthConfig: TwitterAuthConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeTwitter()
        setContentView(R.layout.fragment_container)

        if (currentUser != null)  {
            Log.i(TAG, "display name ChatActivity onCreate = ${currentUser.displayName}")
            if (currentUser.displayName.isNullOrEmpty())    {
                addFirstFragment(UsernameFragment())
            } else {
                addFirstFragment(RequestsPagerFragment())
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

    /* CALLBACKS */
    /* used in LoginFragment */
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

    /* Used in UsernameFragment */
    override fun onStartUsernameRegisteredFragment() {
        replaceFragment(UsernameRegisteredFragment())
    }

    override fun onStartConvoFragment() {
        /*Should not be able to hit back button and go back to login/signin flow */
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
        replaceFragment(StartFragment())
    }

    /*Used in EmailSignupFragment*/
    override fun onStartSignupSuccessFragment() {
        /* pop/reverse onStartSignupFragment() transaction */
        fm.popBackStack()
        /*replace LoginFragment with EmailSignupSuccessFragment and add transaction to stack*/
        replaceFragmentAddToStack(EmailSignupSuccessFragment())
    }

    override fun emailSuccessToLogin() {
        /*reverses replaceFragmentAddToStack(EmailSignupSuccessFragment())*/
        fm.popBackStack()
    }

    /* Override back button event: pop fragment transaction stack if it is not empty, else
       finish the activity.
     */
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            this.finish()
        }
    }

    /*Used to pass result of [Fragment].startActivityResult to [Fragment].onActivityResult */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("test", "requestCode == $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initializeTwitter()  {
        mTwitterAuthConfig = TwitterAuthConfig(getString(R.string.twitter_consumer_key),
            getString(R.string.twitter_consumer_secret))

        val twitterConfig = TwitterConfig.Builder(this)
            .twitterAuthConfig(mTwitterAuthConfig)
            .build()
        Twitter.initialize(twitterConfig)
    }
}
