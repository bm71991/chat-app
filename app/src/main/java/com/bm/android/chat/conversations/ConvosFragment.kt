package com.bm.android.chat.conversations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.bm.android.chat.R
import com.bm.android.chat.friend_requests.models.Friend
import com.google.firebase.auth.FirebaseAuth

class ConvosFragment : Fragment() {
    interface ConvosFragmentInterface {
        /********************************************
         * Since it is the entry point of the app,
         * this fragment enables the NavDrawer
         */
        fun showNavDrawer()
        fun setUsernameInNavDrawer()
    }

    private val mCallback by lazy {
        context as ConvosFragmentInterface
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_convos, container, false)
        mCallback.showNavDrawer()
        mCallback.setUsernameInNavDrawer()
        setHasOptionsMenu(true)
        val f = Friend("foo", "bar")
        val d = Friend("foo", "bar")

        Log.d("friends", "ARE EQUAL: ${f.equals(d)}")
        return v
    }
}
