package com.bm.android.chat.conversations.convo_list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.Chat
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class ConvosAdapter(options:FirestoreRecyclerOptions<Chat>,
        private val chatItemCallback: ConvoAdapterInterface
)
    : FirestoreRecyclerAdapter<Chat, ChatViewHolder>(options) {
    private val TAG = "adapterLog"

    interface ConvoAdapterInterface  {
        fun onClickConvo(members: ArrayList<String>, position: Int)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Chat) {
        Log.d("convosTag", "in onBindViewHolder ${model.memberCount}")
        val chatId = snapshots.getSnapshot(position).id


        holder.bindData(model, position, chatId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view, chatItemCallback)
    }

    override fun onViewDetachedFromWindow(holder: ChatViewHolder) {
        holder.removeListener()
    }
}