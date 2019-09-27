package com.bm.android.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bm.android.chat.user_access.StartFragment
import com.bm.android.chat.user_access.fragments.LoginFragment
import com.bm.android.chat.user_access.fragments.EmailSignupFragment
import com.bm.android.chat.user_access.fragments.EmailSignupSuccessFragment
import com.bm.android.chat.user_access.fragments.UsernameFragment
import com.google.firebase.auth.FirebaseAuth


class ChatActivity : AppCompatActivity(),
                     LoginFragment.LoginFragmentInterface,
                     EmailSignupFragment.SignupFragmentInterface {
    private val TAG = "mainLog"
    private val fm: FragmentManager by lazy {
        supportFragmentManager
    }
    private val FIRST_FRAGMENT = "loginFragment"
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)
//        val fragment = fm.findFragmentById(R.id.fragment_container)
//
//
//        if (fragment == null)   {
//            addFirstFragment(LoginFragment())
//        }


        if (currentUser != null)  {
            Log.i(TAG, "display name = ${currentUser.displayName}")
            if (currentUser.displayName == "")    {
                addFirstFragment(UsernameFragment())
            } else {
                addFirstFragment(StartFragment())
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

    override fun onStartSignupSuccessFragment() {
        /* pop/reverse onStartSignupFragment() transaction */
        fm.popBackStack()
        replaceFragmentAddToStack(EmailSignupSuccessFragment())
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

    /*Used to trigger */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
