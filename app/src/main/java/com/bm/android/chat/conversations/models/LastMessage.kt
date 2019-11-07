package com.bm.android.chat.conversations.models

import com.google.firebase.Timestamp

data class LastMessage(var message:String = "", var timeSent:Timestamp = Timestamp.now())