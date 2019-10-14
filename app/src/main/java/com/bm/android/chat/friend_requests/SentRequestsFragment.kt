package com.bm.android.chat.friend_requests

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.bm.android.chat.R

class SentRequestsFragment : Fragment() {
    private val TAG = "mainLog"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(
            R.layout.fragment_sent_requests,
            container, false
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called - SentRequests")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called - SentRequests")
    }

}
