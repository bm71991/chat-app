package com.bm.android.chat.friend_requests

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class RequestsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return ReceivedRequestsFragment()
            1 -> return SentRequestsFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var pageTitle: CharSequence = ""
        when (position) {
            0 -> pageTitle = "Received Requests"
            1 -> pageTitle = "Sent Requests"
        }
        return pageTitle
    }
}
