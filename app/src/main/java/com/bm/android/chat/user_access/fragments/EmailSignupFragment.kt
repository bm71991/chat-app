package com.bm.android.chat.user_access.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bm.android.chat.R
import com.bm.android.chat.user_access.UserAccessViewModel

class EmailSignupFragment : Fragment() {
    private val mCallback by lazy {
        context as EmailSignupFragmentInterface
    }

    private lateinit var mSignupLayout: LinearLayout
    private lateinit var mProgressBar: ProgressBar

    interface EmailSignupFragmentInterface  {
        fun onStartSignupSuccessFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val mViewModel = ViewModelProviders.of(activity!!).get(UserAccessViewModel::class.java)
        val signupStatus = mViewModel.getEmailSignupStatus()

        val v = inflater.inflate(R.layout.fragment_signup, container, false)
        val signupButton = v.findViewById<Button>(R.id.signup_button)
        val emailTextView = v.findViewById<TextView>(R.id.email_input)
        val passwordTextView = v.findViewById<TextView>(R.id.password_input)

        mSignupLayout = v.findViewById(R.id.signup_layout)
        mProgressBar = v.findViewById(R.id.signup_progress_bar)

        signupButton.setOnClickListener {
            val password = passwordTextView.text.toString()
            val email = emailTextView.text.toString()

            showProgressBar()
            mViewModel.signupUser(email, password)
        }

        signupStatus.observe(this, Observer {
            if (it == mViewModel.EMAIL_REGISTERED)  {
                mCallback.onStartSignupSuccessFragment()
            } else {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            hideProgressBar()
        })

        return v
    }

    private fun showProgressBar()   {
        mProgressBar.visibility = View.VISIBLE
        mSignupLayout.visibility = View.GONE
    }

    private fun hideProgressBar()   {
        mProgressBar.visibility = View.GONE
        mSignupLayout.visibility = View.VISIBLE
    }
}