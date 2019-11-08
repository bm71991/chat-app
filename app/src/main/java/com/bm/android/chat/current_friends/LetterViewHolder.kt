package com.bm.android.chat.current_friends

import android.view.View
import android.widget.TextView
import com.bm.android.chat.R

class LetterViewHolder(itemView:View):FriendsFragment.FriendItemViewHolder(itemView) {
    val letterTextView = itemView.findViewById<TextView>(R.id.letter)

    override fun bindData(model: FriendsFragment.FriendsListItem) {
        letterTextView.text = model.getDisplayTitle()
    }
}