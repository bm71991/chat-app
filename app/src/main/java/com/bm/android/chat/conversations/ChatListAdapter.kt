package com.bm.android.chat.conversations

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

class ChatListAdapter(options:FirestoreRecyclerOptions<ChatMessage>)
    : FirestoreRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
    private val TAG = "adapterLog"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: ChatMessage) {
        holder.bindData(model)
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.d(TAG, e.toString())
    }
}