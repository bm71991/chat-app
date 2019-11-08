package com.bm.android.chat.current_friends

import com.bm.android.chat.current_friends.FriendsFragment.FriendsListItem.Companion.TYPE_FRIEND

class FriendItem(private val username:String):FriendsFragment.FriendsListItem {
    override fun getType(): Int {
        return TYPE_FRIEND
    }

    override fun getDisplayTitle(): String {
        return username
    }
}