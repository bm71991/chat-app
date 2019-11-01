package com.bm.android.chat.conversations.new_conversation

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.friend_requests.models.Friend

class RecipientViewHolder(itemView:View, viewHolderCallback: RecipientViewHolderInterface)
    : RecyclerView.ViewHolder(itemView) {
    interface RecipientViewHolderInterface  {
        fun onClickDeleteBtn(friend: Friend)
    }

    private val nameTextView = itemView.findViewById<TextView>(R.id.recipient_name)
    private val deleteBtn = itemView.findViewById<ImageButton>(R.id.delete_btn)
    private var recipient:Friend? = null

    init {
        deleteBtn.setOnClickListener    {
            viewHolderCallback.onClickDeleteBtn(recipient!!)
        }
    }

    fun bindData(recipient: Friend) {
        nameTextView.text = recipient.username
        this.recipient = recipient
    }
}