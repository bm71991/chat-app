package com.bm.android.chat.current_friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import com.bm.android.chat.R

class FriendsFragment : Fragment() {
    interface FriendsInterface  {
        fun changeActionbarTitle(title:String)
    }
    private val mCallback by lazy {
        context as FriendsInterface
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mCallback.changeActionbarTitle(getString(R.string.friends_title))

        return inflater.inflate(R.layout.fragment_friends, container, false)
    }
}
