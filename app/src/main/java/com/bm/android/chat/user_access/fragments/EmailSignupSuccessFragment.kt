package com.bm.android.chat.user_access.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bm.android.chat.R

class EmailSignupSuccessFragment: Fragment() {
    private val mCallback by lazy {
        context as EmailSignupSuccessFragmentInterface
    }

    interface EmailSignupSuccessFragmentInterface {
        fun emailSuccessToLogin()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_signup_success, container, false)
        val backToLoginButton = v.findViewById<Button>(R.id.back_to_login_button)

        backToLoginButton.setOnClickListener {
            mCallback.emailSuccessToLogin()
        }
        return v
    }
}