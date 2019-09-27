package com.bm.android.chat.user_access.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bm.android.chat.R

class SignupFragment : Fragment() {

    interface SignupFragmentInterface  {
        fun onStartSignupSuccessFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_signup, container, false)
        return v
    }
}