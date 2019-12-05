package com.bm.android.chat.conversations.conversation

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.R
import com.bm.android.chat.conversations.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

abstract class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    protected val dateTextView = itemView.findViewById<TextView>(R.id.date)
    protected val messageView = itemView.findViewById<TextView>(R.id.message)

    fun setDate(model: ChatMessage)   {
        //check to see whether the date is for today
        val itemCalendarInstance = Calendar.getInstance()
        val nowCalendarInstance = Calendar.getInstance()

        val date = if (model.updated)
            model.updateTime!!.toDate()
            else
            model.timeSent.toDate()

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

        val formattedDate = dateFormat.format(date)
        dateTextView.text = if (model.updated)   {
            dateTextView.setTextColor(Color.RED)
            "updated $formattedDate"
        } else
            formattedDate
    }


}