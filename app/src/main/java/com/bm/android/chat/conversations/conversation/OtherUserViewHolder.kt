package com.bm.android.chat.conversations.conversation

import android.view.View
import android.widget.TextView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage

class OtherUserViewHolder(itemView: View, private val sameSenderAsLast:Boolean)
    : MessageViewHolder(itemView) {
//    private val messageView = itemView.findViewById<TextView>(R.id.message)
    private val senderTextView = itemView.findViewById<TextView>(R.id.sender)
//    private val dateTextView = itemView.findViewById<TextView>(R.id.date)

    fun bindData(model: ChatMessage) {
        messageView.text = model.message
        if (sameSenderAsLast) {
            senderTextView.visibility = View.GONE
        } else {
            senderTextView.text = model.sentBy
        }

        setDate(model)
    }
}