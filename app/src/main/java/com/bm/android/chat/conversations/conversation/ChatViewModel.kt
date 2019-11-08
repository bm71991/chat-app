package com.bm.android.chat.conversations.conversation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.ConvoRepository
import com.bm.android.chat.conversations.models.Chat
import com.bm.android.chat.conversations.models.DataLoading
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class ChatViewModel: ViewModel() {
    var chatId:String = ""
    private val convoRepository = ConvoRepository()
    private val chatStatus = MutableLiveData<String>()
    val memberNames = HashSet<String>()
    var chatData:Chat? = null

    fun getChatStatus():LiveData<String> = chatStatus

    fun clearChatStatus() {
        chatStatus.value = null
    }

    fun getUsernames():HashSet<String>  {
        return memberNames
    }

    fun getChatMessages(): Query {
        Log.d("chatLogging", "chatId = $chatId")
        return convoRepository.getChatMessages(chatId)
    }

    fun getChatInfo()   {
        if (chatId.isNotBlank())    {
            convoRepository.getChatMetaData(chatId)
                .addOnSuccessListener {
                    if (it.exists())    {
                        chatData = it.toObject(Chat::class.java)
                        memberNames.addAll(chatData!!.members.keys.toList())
                        chatStatus.value = "LOADED"
                    } else {
                        chatStatus.value = "CHAT DOES NOT EXIST"
                    }
                }
        }
    }

    fun addChatMessage(message:String)   {
            convoRepository.addMessage(chatId, message,
                FirebaseAuth.getInstance().currentUser!!.displayName!!)
                .addOnSuccessListener {
                    setLastMessage(message, chatId)
                }
                .addOnFailureListener {
                    Log.d("chatTest", "error: $it")
                }
    }

    private fun setLastMessage(message:String, chatId:String)   {
        convoRepository.setLastMessage(message, Timestamp.now(), chatId)
            .addOnSuccessListener {
            }
    }

    fun addChat(message:String)   {
//        val memberList = arrayListOf<String>()
//        for (name in memberNames) memberList.add(name)
        convoRepository.addChat(getMemberNameArray())
            .addOnSuccessListener {
            chatId = it.id
            addChatMessage(message)
            getChatInfo()
        }
    }

    fun getMemberNameArray():ArrayList<String>    {
        return memberNames.toTypedArray().toCollection(ArrayList())
    }
}