package com.bm.android.chat.friend_requests

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import java.text.SimpleDateFormat
import java.util.*


class RequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val senderText: TextView = itemView.findViewById(R.id.senderName)
    private val timeSentText: TextView = itemView.findViewById(R.id.time_received)
    private var senderId:String? = null

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
}
