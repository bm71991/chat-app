package com.bm.android.chat.conversations.conversation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.ConvoRepository
import com.bm.android.chat.conversations.models.Chat
import com.bm.android.chat.conversations.models.DataLoading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class ChatViewModel: ViewModel() {
    var chatId:String = ""
    private val convoRepository = ConvoRepository()
    private val chatStatus = MutableLiveData<String>()
    var chatData:Chat? = null

    fun getChatStatus():LiveData<String> = chatStatus

    fun clearChatStatus() {
        chatStatus.value = null
    }

    fun getUsernames():List<String>?  {
        return chatData?.members?.keys?.toList()
    }

    fun getChatMessages(): Query {
        Log.d("chatLogging", "chatId = $chatId")
        return convoRepository.getChatMessages(chatId)
    }

    fun getChatInfo()   {
        convoRepository.getChatMetaData(chatId)
            .addOnSuccessListener {
                if (it.exists())    {
                    chatData = it.toObject(Chat::class.java)
                    chatStatus.value = "LOADED"
                } else {
                    Log.d("uids", "chat document does not exist")
                }
            }
    }

    fun addChatMessage(message:String)   {
        convoRepository.addMessage(chatId, message,
            FirebaseAuth.getInstance().currentUser!!.displayName!!)
            .addOnFailureListener {
                Log.d("chatTest", "error: $it")
            }
    }
}