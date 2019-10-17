package com.bm.android.chat.conversations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.bm.android.chat.R

class ConvosFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        Log.i("mainLog", "onCreateView of ConvosFragment called")
        return inflater.inflate(
            R.layout.fragment_convos,
            container, false
        )
    }
}
