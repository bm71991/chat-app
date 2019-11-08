package com.bm.android.chat.conversations.conversation

import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException


class ChatListAdapter(options:FirestoreRecyclerOptions<ChatMessage>)
    : FirestoreRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
    private val TAG = "adapterLog"
    private val currentUsername = FirebaseAuth.getInstance().currentUser!!.displayName!!
    private var lastSender = ""
    private var sameSenderAsLast = false

    override fun getItemViewType(position: Int): Int {
        val currentSender = snapshots[position].sentBy
        return if (currentSender == currentUsername)   {
            CURRENT_USER
        } else {
            sameSenderAsLast = (currentSender == lastSender)
            lastSender = currentSender
            OTHER_USER
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == CURRENT_USER)   {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.current_user_message, parent, false)
            CurrentUserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message, parent, false)
            OtherUserViewHolder(view,sameSenderAsLast)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int,
                                  model: ChatMessage) {
        holder.bindData(model)
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.d(TAG, e.toString())
    }

    companion object {
        const val CURRENT_USER = 0
        const val OTHER_USER = 1
    }
}