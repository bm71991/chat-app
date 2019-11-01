package com.bm.android.chat.conversations

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query

class ChatViewModel: ViewModel() {
    var chatId:String = ""
    val convoRepository = ConvoRepository()


    fun getChatMessages(): Query {
        return convoRepository.getChatMessages(chatId)
    }

}