package com.bm.android.chat.user_access.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bm.android.chat.R
import com.bm.android.chat.user_access.UserAccessViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class UsernameFragment : Fragment() {
    private val TAG = "mainLog"
    private lateinit var mProgressBar : ProgressBar
    private lateinit var mUsernameLayout : LinearLayout
    private val mCallback by lazy {
        context as UsernameFragmentInterface
    }

    interface UsernameFragmentInterface {
        fun onStartUsernameRegisteredFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_username, container, false)

        val changeUsernameBtn = v.findViewById<Button>(R.id.register_username_btn)
        val newUsernameEditText = v.findViewById<EditText>(R.id.username_input)
        val mViewModel = ViewModelProviders.of(activity!!).get(UserAccessViewModel::class.java)
        val mUsernameRegisterStatus = mViewModel.getNameRegisterStatus()

        mProgressBar = v.findViewById(R.id.username_progress_bar)
        mUsernameLayout = v.findViewById(R.id.username_layout)

        changeUsernameBtn.setOnClickListener {
            val username = newUsernameEditText.text.toString()
            showProgressBar()
            mViewModel.checkFirestore(username, "")
        }

        mUsernameRegisterStatus.observe(this, Observer {
            val result = it
            Log.d(TAG, "result = $result")
            if (result != null) {
                mViewModel.clearNameRegisterStatus()
                if (result == mViewModel.USERNAME_REGISTERED)   {
                    mCallback.onStartUsernameRegisteredFragment()
                } else {
                    Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show()
                }
                hideProgressBar()
            }
        })

        return v
    }

    private fun showProgressBar()   {
        mProgressBar.visibility = View.VISIBLE
        mUsernameLayout.visibility = View.GONE
    }

    private fun hideProgressBar()   {
        mProgressBar.visibility = View.GONE
        mUsernameLayout.visibility = View.VISIBLE
    }
}