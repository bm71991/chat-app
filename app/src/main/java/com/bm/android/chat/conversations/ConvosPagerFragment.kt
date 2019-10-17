package com.bm.android.chat.conversations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

import com.bm.android.chat.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.convos_pager.*

class ConvosPagerFragment : Fragment() {
    private val TAG = "mainLog"
    private val mCallback by lazy {
        context as ConvosPagerFragmentInterface
    }

    interface ConvosPagerFragmentInterface {
        /********************************************
         * Sine it is the entry point of the app,
         * this fragment enables the NavDrawer
         */
        fun showNavDrawer()
    }

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
        Log.d(TAG, "onCreateView called in ConvosPagerFragment")
        val viewPager = v.findViewById<ViewPager>(R.id.view_pager)
        val tabLayout = v.findViewById<TabLayout>(R.id.tab_layout)
        viewPager.adapter = ConvosPagerAdapter(activity!!.supportFragmentManager)
        Log.d(TAG, "${viewPager.adapter}")
        tabLayout.setupWithViewPager(viewPager)

        mCallback.showNavDrawer()
        return v
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called in ConvosPagerFragment")
    }
}
