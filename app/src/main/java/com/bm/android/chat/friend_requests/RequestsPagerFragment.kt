package com.bm.android.chat.friend_requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

import com.bm.android.chat.R
import com.bm.android.chat.conversations.ConvosPagerFragment
import com.google.android.material.tabs.TabLayout

class RequestsPagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
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

