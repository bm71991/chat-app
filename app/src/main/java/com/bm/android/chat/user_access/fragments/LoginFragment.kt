package com.bm.android.chat.user_access.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bm.android.chat.R
import com.bm.android.chat.user_access.UserAccessViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton

class LoginFragment : Fragment() {
    private val TAG = "mainLog"
    val FACEBOOK_REQUEST_CODE = 64206
    val TWITTER_REQUEST_CODE = 140
    private val mCallback by lazy {
        context as LoginFragmentInterface
    }

    interface LoginFragmentInterface  {
        fun onStartSignupFragment()
        fun onStartUsernameFragment()
        fun onStartConvosFragment()
        fun changeActionbarTitle(title:String)
        fun disableNavDrawer()
    }

    private lateinit var mSignupLink:TextView
    private lateinit var mFacebookButton: LoginButton
    private lateinit var mLoginLinearLayout: LinearLayout
    private lateinit var mProgressBar: ProgressBar
    private val mCallbackManager = CallbackManager.Factory.create()
    private lateinit var auth: FirebaseAuth
    private lateinit var mTwitterLoginButton: TwitterLoginButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mCallback.disableNavDrawer()
        mCallback.changeActionbarTitle(getString(R.string.welcome_title))

        auth = FirebaseAuth.getInstance()
        val mViewModel = ViewModelProviders.of(activity!!).get(UserAccessViewModel::class.java)
        val emailLoginStatus = mViewModel.getEmailLoginStatus()

        val v = inflater.inflate(R.layout.fragment_login, container, false)
        val emailLoginButton = v.findViewById<Button>(R.id.email_login_button)
        val emailInputTextView = v.findViewById<EditText>(R.id.email_input)
        val passwordTextView = v.findViewById<TextView>(R.id.password_input)
        mLoginLinearLayout = v.findViewById(R.id.login_layout)
        mProgressBar = v.findViewById(R.id.login_progress_bar)
        mTwitterLoginButton = v.findViewById(R.id.twitter_login_button)

        mTwitterLoginButton.callback = object: Callback<TwitterSession>()   {
            override fun success(result: Result<TwitterSession>?) {
                showProgressBar()
                val session = result!!.data
                val credential = TwitterAuthProvider.getCredential(session.authToken.token,
                session.authToken.secret)
                val auth = FirebaseAuth.getInstance()
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        Log.d(TAG, "signed in via twitter: ${auth.uid}, displayName == " +
                                "${auth.currentUser!!.displayName}")
                        val isNewUser = it.additionalUserInfo!!.isNewUser
                        handleUsernameFlow(isNewUser)
                    }
                    .addOnFailureListener   {
                        Log.d(TAG, "sign in fail to Twitter: $it")
                    }
            }

            override fun failure(exception: TwitterException?) {
                Log.d(TAG, exception.toString())
            }
        }

        mSignupLink = v.findViewById(R.id.go_to_signup)
        setLinkOnTextView()

        mFacebookButton = v.findViewById(R.id.fb_login_button)
        mFacebookButton.setPermissions(listOf("email", "public_profile"))
        mFacebookButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                showProgressBar()
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })

        emailLoginButton.setOnClickListener {
            val emailInput = emailInputTextView.text.toString()
            val password = passwordTextView.text.toString()

            showProgressBar()
            mViewModel.loginWithEmail(emailInput, password)
        }

        emailLoginStatus.observe(this, Observer {
            val result = it
            Log.d(TAG, "result = $result")
            if (result != null) {
                mViewModel.clearEmailLoginStatus()
                if (result == mViewModel.EMAIL_LOGIN_SUCCESS)   {
                    if (mViewModel.accountLacksUsername())  {
                        mCallback.onStartUsernameFragment()
                    } else {
                        mCallback.onStartConvosFragment()
                    }
                } else {
                    Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show()
                }
                hideProgressBar()
            }
        })

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)  {
            FACEBOOK_REQUEST_CODE -> {
                // Pass the activity result back to the Facebook SDK
                mCallbackManager.onActivityResult(requestCode, resultCode, data)
            }
            TWITTER_REQUEST_CODE -> {
                mTwitterLoginButton.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun setLinkOnTextView() {
        val spannableString = SpannableString(getString(R.string.go_to_signup))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                mCallback.onStartSignupFragment()
            }
        }
        spannableString.setSpan(clickableSpan, 0, 23,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSignupLink.text = spannableString
        mSignupLink.highlightColor = Color.TRANSPARENT
        mSignupLink.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val isNewUser = task.result!!.additionalUserInfo!!.isNewUser
                    handleUsernameFlow(isNewUser)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }

    private fun changeDisplayName(user: FirebaseUser?, newName:String)  {
        val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Changed display name to $newName")
                    mCallback.onStartUsernameFragment()
                }
          }
    }

    private fun showProgressBar()   {
        mProgressBar.visibility = View.VISIBLE
        mLoginLinearLayout.visibility = View.GONE
    }

    private fun hideProgressBar()   {
        mProgressBar.visibility = View.GONE
        mLoginLinearLayout.visibility = View.VISIBLE
    }

    private fun handleUsernameFlow(isNewUser:Boolean)   {
        val user = auth.currentUser
        //if this is a new user: clear display name and send them to UsernameFragment
        if (isNewUser)  {

            Log.d(TAG, "IS NEW USER")
            changeDisplayName(user, "")
        }
        else {
            Log.d(TAG, "IS NOT NEW USER")
            //If the user logged in before but has not set their username:
            if (user!!.displayName == "")   {
                Log.d(TAG, "current displayname: ${auth.currentUser!!.displayName}")
                mCallback.onStartUsernameFragment()
            } else {
                mCallback.onStartConvosFragment()
            }
        }
    }
}