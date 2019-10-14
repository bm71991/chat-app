package com.bm.android.chat.conversations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

import com.bm.android.chat.R
import com.google.android.material.tabs.TabLayout

class ConvosPagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(
            R.layout.convos_pager,
            container, false
        )
        val viewPager = v.findViewById<ViewPager>(R.id.view_pager)
        val tabLayout = v.findViewById<TabLayout>(R.id.tab_layout)
        viewPager.adapter = ConvosPagerAdapter(activity!!.supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        return v
    }
}
