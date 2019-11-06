package com.bm.android.chat.friend_requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

import com.bm.android.chat.R
import com.google.android.material.tabs.TabLayout

class RequestsPagerFragment : Fragment() {
    interface RequestsPagerInterface  {
        fun changeActionbarTitle(title:String)
    }
    private val mCallback by lazy   {
        context as RequestsPagerInterface
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mCallback.changeActionbarTitle(getString(R.string.friend_requests_title))
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(false)
        val v = inflater.inflate(
            R.layout.requests_pager,
            container, false
        )
        val viewPager = v.findViewById<ViewPager>(R.id.view_pager)
        val tabLayout = v.findViewById<TabLayout>(R.id.tab_layout)
        viewPager.adapter = RequestsPagerAdapter(activity!!.supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        return v
    }
}

