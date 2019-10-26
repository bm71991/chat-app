package com.bm.android.chat.chat_messaging

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

import java.util.Date

class ChatMessage {
//    var name: String? = null
//    var message: String? = null
//    var uid: String? = null
//    @get:ServerTimestamp
//    var timestamp: Date? = null
//
//    constructor() {} // Needed for Firebase
//
//    constructor(name: String, message: String, uid: String) {
//        this.name = name
//        this.message = message
//        this.uid = uid
//    }
    var senderUid:String? = null
    var senderUsername:String? = null
    var timeRequestSent: Timestamp? = null
    constructor() {} // Needed for Firebase

        constructor(senderUid:String, senderUsername:String, timeRequestSent:Timestamp) {
        this.senderUid = senderUid
        this.senderUsername = senderUsername
        this.timeRequestSent = timeRequestSent
    }
}