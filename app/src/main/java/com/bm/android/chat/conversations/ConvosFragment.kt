package com.bm.android.chat.conversations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.bm.android.chat.R
import com.google.firebase.auth.FirebaseAuth

class ConvosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        Log.i("mainLog", "onCreateView of ConvosFragment called: ${FirebaseAuth.getInstance().uid!!}")
        return inflater.inflate(
            R.layout.fragment_convos,
            container, false
        )
    }
}
