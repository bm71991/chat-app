package com.bm.android.chat.conversations

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.friend_requests.models.Friend
import com.bm.android.chat.conversations.ProspectiveRecipientViewHolder.ProspectiveRecipientInterface

class ProspectiveRecipientAdapter(private val mDataset: List<Friend>,
                                  clickAction: ProspectiveRecipientInterface) :
    RecyclerView.Adapter<ProspectiveRecipientViewHolder>() {
    val mClickAction = clickAction
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ProspectiveRecipientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipient_dialog_item, parent, false)
        return ProspectiveRecipientViewHolder(view, mClickAction)
    }

    override fun onBindViewHolder(holder: ProspectiveRecipientViewHolder, position: Int) {
        val data = mDataset[position]
        holder.bindData(data)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }
}