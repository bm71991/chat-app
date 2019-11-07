package com.bm.android.chat.conversations.conversation

import android.view.View
import android.widget.TextView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage

class OtherUserViewHolder(itemView: View, val sameSenderAsLast:Boolean)
    : MessageViewHolder(itemView) {
    private val messageView = itemView.findViewById<TextView>(R.id.message)
    private val senderText = itemView.findViewById<TextView>(R.id.sender)
    private val dateText = itemView.findViewById<TextView>(R.id.date)

    override fun bindData(model: ChatMessage) {
        messageView.text = model.message
        if (sameSenderAsLast) {
            senderText.visibility = View.GONE
        } else {
            senderText.text = model.sentBy
        }

        dateText.text = getDateString(model.timeSent.toDate())
    }
}