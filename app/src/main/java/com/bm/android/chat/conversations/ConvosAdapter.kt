package com.bm.android.chat.conversations

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.Chat
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.util.ArrayList

class ConvosAdapter(options:FirestoreRecyclerOptions<Chat>,
        private val chatItemCallback:ConvoAdapterInterface)
    : FirestoreRecyclerAdapter<Chat, ChatViewHolder>(options) {
    private val TAG = "adapterLog"

    interface ConvoAdapterInterface  {
        fun onClickConvo(members: ArrayList<String>, position: Int)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Chat) {
        Log.d("convosTag", "in onBindViewHolder ${model.memberCount}")
        holder.bindData(model, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view, chatItemCallback)
    }
}