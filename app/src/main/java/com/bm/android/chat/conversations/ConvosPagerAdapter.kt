package com.bm.android.chat.conversations

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.lang.NullPointerException

class ConvosPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
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
