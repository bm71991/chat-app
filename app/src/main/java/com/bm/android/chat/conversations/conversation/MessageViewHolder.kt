package com.bm.android.chat.conversations.conversation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bm.android.chat.conversations.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

abstract class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bindData(model: ChatMessage)

    fun getDateString(date: Date):String    {
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