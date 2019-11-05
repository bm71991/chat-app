package com.bm.android.chat.conversations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

import com.bm.android.chat.R
class ConvosFragment : Fragment() {
    private val TAG = "convosTag"
    interface ConvosFragmentInterface {
        /********************************************
         * Since it is the entry point of the app,
         * this fragment enables the NavDrawer
         */
        fun showNavDrawer()
        fun setUsernameInNavDrawer()
        /*********************************************************
         *setMappings() sets the uid to user hashmap for all of the
         * current user's friends in ChatActivityViewModel.
         * This hashmap will be globally used throughout the
         * application.
         */
        fun setMappings()
        fun clearUidMappingStatus()
        fun getUidMappingStatus():LiveData<String>
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
        mCallback.setMappings()
        mCallback.getUidMappingStatus().observe(this, Observer {
            val result = it
            if (result != null) {
                mCallback.clearUidMappingStatus()
            }
        })

        setHasOptionsMenu(true)
        return v
    }
}
