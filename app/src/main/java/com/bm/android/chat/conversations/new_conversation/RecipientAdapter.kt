package com.bm.android.chat.conversations.new_conversation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.new_conversation.RecipientViewHolder
import com.bm.android.chat.friend_requests.models.Friend

class RecipientAdapter(private val mDataset: List<Friend>,
                       viewHolderCallback: RecipientViewHolder.RecipientViewHolderInterface) :
    RecyclerView.Adapter<RecipientViewHolder>() {
    private val mViewHolderCallback = viewHolderCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecipientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipient_item, parent, false)
        return RecipientViewHolder(
            view,
            mViewHolderCallback
        )
    }

    override fun onBindViewHolder(holder: RecipientViewHolder, position: Int) {
        val data = mDataset[position]
        holder.bindData(data)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }
}