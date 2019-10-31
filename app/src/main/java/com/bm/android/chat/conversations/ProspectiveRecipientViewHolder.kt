package com.bm.android.chat.conversations

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.friend_requests.models.Friend
import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import kotlinx.android.synthetic.main.recipient_dialog_item.view.*

class ProspectiveRecipientViewHolder(itemView:View, clickAction:ProspectiveRecipientInterface)
    : RecyclerView.ViewHolder(itemView) {
    interface ProspectiveRecipientInterface {
        fun onClickCheckbox(isChecked:Boolean, friend: Friend?)
    }
    private var nameText = itemView.findViewById<TextView>(R.id.recipient_name)
    var friend:Friend? = null
    private val mCheckBox = itemView.findViewById<CheckBox>(R.id.checkbox)

    init {
        mCheckBox.setOnCheckedChangeListener { checkBox, isChecked ->
            clickAction.onClickCheckbox(isChecked, friend)
        }
    }

    fun bindData(prospectiveRecipient: Friend) {
        nameText.text = prospectiveRecipient.username
        friend = prospectiveRecipient
    }
}