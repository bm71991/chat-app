package com.bm.android.chat.conversations

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.Chat
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatViewHolder(itemView:View, val chatItemCallback:
        ConvosAdapter.ConvoAdapterInterface) :RecyclerView.ViewHolder(itemView) {
    private val membersView = itemView.findViewById<TextView>(R.id.members)
    private val dateView = itemView.findViewById<TextView>(R.id.date)
    private val lastMessage = itemView.findViewById<TextView>(R.id.last_message)
    private val chatId:String = ""


    fun bindData(chat: Chat, position:Int)   {
        membersView.text = getMembersString(chat.members)
        val memberList = chat.members.keys.toMutableList() as ArrayList<String>

        itemView.setOnClickListener {
            chatItemCallback.onClickConvo(memberList, position)
        }
    }

    private fun getMembersString(chatMembers:HashMap<String, Boolean>):String   {
        val currentUsername = FirebaseAuth.getInstance().currentUser!!.displayName!!
        chatMembers.remove(currentUsername)
        return chatMembers.keys.joinToString()
    }

    private fun getDateString(date: Date):String    {
        val dateFormat = SimpleDateFormat("EEE, h:mm a")
        return dateFormat.format(date)
    }
}