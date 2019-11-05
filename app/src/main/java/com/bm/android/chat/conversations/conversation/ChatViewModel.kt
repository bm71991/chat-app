package com.bm.android.chat.conversations.conversation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.ConvoRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class ChatViewModel: ViewModel() {
    var chatId:String = ""
    private val convoRepository = ConvoRepository()

    fun getChatMessages(): Query {
        return convoRepository.getChatMessages(chatId)
    }

    fun addChatMessage(message:String)   {
        convoRepository.addMessage(chatId, message, FirebaseAuth.getInstance().uid!!)
            .addOnFailureListener {
                Log.d("chatTest", "error: $it")
            }
    }
}