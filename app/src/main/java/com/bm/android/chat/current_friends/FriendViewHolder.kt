package com.bm.android.chat.current_friends

import android.view.View
import android.widget.TextView
import com.bm.android.chat.R

class FriendViewHolder(itemView:View):FriendsFragment.FriendItemViewHolder(itemView) {
    val friendNameTextView = itemView.findViewById<TextView>(R.id.friend)

    override fun bindData(model: FriendsFragment.FriendsListItem) {
        friendNameTextView.text = model.getDisplayTitle()
    }
}