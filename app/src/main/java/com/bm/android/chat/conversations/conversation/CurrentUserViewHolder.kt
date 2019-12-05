package com.bm.android.chat.conversations.conversation

import android.view.View
import com.bm.android.chat.conversations.models.ChatMessage

class CurrentUserViewHolder(itemView: View)
    : MessageViewHolder(itemView) {
//    private val messageView = itemView.findViewById<TextView>(R.id.message)
//    private val dateTextView = itemView.findViewById<TextView>(R.id.date)

    fun bindData(model: ChatMessage,
                        currentUserMessageCallback: ChatFragment.CurrentUserMessageCallback,
                        messageId:String) {
        messageView.text = model.message

        setDate(model)

        itemView.setOnLongClickListener{
            currentUserMessageCallback.changeMessage(messageView.text as String, messageId)
            true
        }
    }
}