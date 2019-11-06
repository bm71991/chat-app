package com.bm.android.chat.user_access.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bm.android.chat.R

class UsernameRegisteredFragment : Fragment() {
    private val mCallback by lazy {
        context as UsernameRegisteredFragmentInterface
    }

    interface UsernameRegisteredFragmentInterface   {
        fun onStartConvosFragment()
        fun changeActionbarTitle(title:String)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_username_registered,
            container, false)
        val toConvosButton = v.findViewById<Button>(R.id.to_convos_btn)
        mCallback.changeActionbarTitle(getString(R.string.username_registered_title))
        toConvosButton.setOnClickListener {
            mCallback.onStartConvosFragment()
        }
        return v
    }

}