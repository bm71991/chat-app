package com.bm.android.chat.conversations

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ConvosPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        Log.i("mainLog", "getItem in ConvosPagerAdapter called")
        when (position) {
            0 -> return ConvosFragment()
            1 -> return FriendsFragment()
        }
        //never occurs
        return Fragment()
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var pageTitle: CharSequence = ""
        when (position) {
            0 -> pageTitle = "Conversations"
            1 -> pageTitle = "Friends"
        }

        return pageTitle
    }
}
