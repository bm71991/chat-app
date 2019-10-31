package com.bm.android.chat.conversations.models

import com.google.firebase.Timestamp
import java.util.Date

/************************************************************************
 * Model of document to be included in subcollection Chat.chatThread
 */
data class ChatMessage(var message:String = "", var sentBy:String = "",
                       var timeSent:Timestamp = Timestamp(Date()))