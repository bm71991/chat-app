package com.bm.android.chat.friend_requests

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import java.text.SimpleDateFormat
import java.util.*


class RequestHolder(itemView: View, clickAction:RequestHolderClick) : RecyclerView.ViewHolder(itemView) {
    interface RequestHolderClick    {
        fun onClickAcceptBtn(senderId:String, senderUsername:String)
    }

    private val senderText: TextView = itemView.findViewById(R.id.senderName)
    private val timeSentText: TextView = itemView.findViewById(R.id.time_received)
    private val acceptBtn:Button = itemView.findViewById(R.id.accept_btn)
    private var senderId:String = ""

    init {
        acceptBtn.setOnClickListener {
            Log.d("mainLog", "In RequestHolder: $senderId ${senderText.text}")
            //progressbar visible
            clickAction.onClickAcceptBtn(senderId, senderText.text.toString())
        }
    }

    fun bindData(request: ReceivedFriendRequest) {
        senderText.text = request.senderUsername
        val parsedTimestamp = parseTimestamp(request.timeRequestSent.toDate())
        timeSentText.text = parsedTimestamp
        senderId = request.senderUid
    }

    private fun parseTimestamp(timestamp: Date):String    {
        val formatter = SimpleDateFormat("EEE, MMM d yyyy 'at' hh:mm aaa")
        return formatter.format(timestamp)
    }

    fun onClickAccept() {
        //remove receivedRequest document from collection receivedRequests for the current user
        //remove sentRequest document from collection sentRequests from the sending user
        //add Friend document to both the current and the sending user's Friends collection
    }
}
