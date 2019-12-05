package com.bm.android.chat.conversations.convo_list

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.Chat
import com.bm.android.chat.conversations.models.LastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatViewHolder(itemView:View, private val chatItemCallback:
        ConvosAdapter.ConvoAdapterInterface) :RecyclerView.ViewHolder(itemView) {
    private val membersView = itemView.findViewById<TextView>(R.id.members)
    private val dateView = itemView.findViewById<TextView>(R.id.date)
    private val lastMessage = itemView.findViewById<TextView>(R.id.last_message)
    private val newMessageCount = itemView.findViewById<TextView>(R.id.new_message_count)
    private val chatId:String = ""
    private val currentUser:String? = FirebaseAuth.getInstance().currentUser?.displayName
    private var lastMessageListener:ListenerRegistration? = null

    fun bindData(chat: Chat, position:Int, chatId:String)   {
        membersView.text = getMembersString(chat.members)
        val memberList = chat.members.keys.toMutableList() as ArrayList<String>

        //needed for when this viewholder is reused
        removeListener()

        itemView.setOnClickListener {
            chatItemCallback.onClickConvo(memberList, position)
        }

        val date = chat.lastMessage.timeSent.toDate()
        dateView.text = getDateString(date)
        lastMessage.text = chat.lastMessage.message
        setNewMessageCount(chat.newMessageCount[currentUser])

        lastMessageListener = FirebaseFirestore.getInstance()
            .collection("chats")
            .document(chatId)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val chatObject = documentSnapshot?.toObject(Chat::class.java)
                if (chatObject != null) {
                    Log.d("snapshotTest", "SNAPSHOT FIRED: ${chatObject.lastMessage}")
                    lastMessage.text = chatObject.lastMessage.message
                    dateView.text = getDateString(chatObject.lastMessage.timeSent.toDate())
                }
                setNewMessageCount(chatObject?.newMessageCount?.get(currentUser))
            }

    }

    private fun setNewMessageCount(newCount:Int?)    {
        newMessageCount.text = if (newCount == null || newCount < 1) "" else newCount.toString()
    }

    private fun getMembersString(chatMembers:HashMap<String, Boolean>):String   {
        val currentUsername = FirebaseAuth.getInstance().currentUser!!.displayName!!
        chatMembers.remove(currentUsername)
        val chatMembersStringBuilder =
            StringBuilder(chatMembers.keys.joinToString()
                          .take(19))

        if (chatMembersStringBuilder.count() == 19) {
            chatMembersStringBuilder.append("...(${chatMembers.size})")
        }
        return chatMembersStringBuilder.toString()
    }


    fun removeListener()    {
        lastMessageListener?.remove()
        lastMessageListener = null
    }

    private fun getDateString(date: Date):String    {
        //check to see whether the date is for today
        val itemCalendarInstance = Calendar.getInstance()
        val nowCalendarInstance = Calendar.getInstance()
        itemCalendarInstance.time = date
        nowCalendarInstance.time = Date()

        /*returns true if the last message sent in the chat was sent today*/
        val lastUpdateWasToday =
            (itemCalendarInstance.get(Calendar.DAY_OF_YEAR) ==
             nowCalendarInstance.get(Calendar.DAY_OF_YEAR))
                    &&
            (itemCalendarInstance.get(Calendar.YEAR) ==
             nowCalendarInstance.get(Calendar.YEAR))

        val dateFormat = if (lastUpdateWasToday) {
            //show only the time
             SimpleDateFormat("h:mm a")
        } else {
            //show only the month and day
            SimpleDateFormat("MMM dd")
        }

        return dateFormat.format(date)
    }
}