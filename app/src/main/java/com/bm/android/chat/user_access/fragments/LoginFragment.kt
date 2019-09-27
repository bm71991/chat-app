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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bm.android.chat.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private val mCallback by lazy {
        context as LoginFragmentInterface
    }

    interface LoginFragmentInterface  {
        fun onStartSignupFragment()
//        fun onStartLoginSuccessFragment()
    }

    private lateinit var mSignupLink:TextView
    private lateinit var mFacebookButton: LoginButton
    private val mCallbackManager = CallbackManager.Factory.create()
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_login, container, false)


        val loginButton = v.findViewById<Button>(R.id.login_button)
        val usernameTextView = v.findViewById<TextView>(R.id.username_input)
        val passwordTextView = v.findViewById<TextView>(R.id.password_input)

        auth = FirebaseAuth.getInstance()
        mFacebookButton = v.findViewById(R.id.fb_login_button)
        mFacebookButton.setPermissions(listOf("email", "public_profile"))
        mFacebookButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("test", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("test", "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d("test", "facebook:onError", error)
                // ...
            }
        })

        mSignupLink = v.findViewById(R.id.go_to_signup)
        setLinkOnTextView()

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun setLinkOnTextView() {
        val spannableString = SpannableString(getString(R.string.go_to_signup))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                mCallback.onStartSignupFragment()
            }
        }
        spannableString.setSpan(clickableSpan, 31, 35,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSignupLink.text = spannableString
        mSignupLink.highlightColor = Color.TRANSPARENT
        mSignupLink.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("test", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("test", "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.d("test", "current user: ${auth.currentUser!!.uid}")
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("test", "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }




    }