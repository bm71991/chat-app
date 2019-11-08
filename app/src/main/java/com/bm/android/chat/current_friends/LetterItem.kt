package com.bm.android.chat.current_friends

import com.bm.android.chat.current_friends.FriendsFragment.FriendsListItem.Companion.TYPE_LETTER

class LetterItem(private val letter:String):FriendsFragment.FriendsListItem {
    override fun getType(): Int {
        return TYPE_LETTER
    }

    override fun getDisplayTitle(): String {
        return letter
    }
}