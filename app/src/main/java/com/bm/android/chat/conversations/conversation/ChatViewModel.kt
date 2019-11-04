package com.bm.android.chat.conversations.conversation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.ConvoRepository
import com.bm.android.chat.conversations.models.Chat
import com.bm.android.chat.conversations.models.DataLoading
import com.google.firebase.firestore.Query

class ChatViewModel: ViewModel() {
    var chatId:String = ""
    private val convoRepository = ConvoRepository()
    private val uidMappingStatus = MutableLiveData<DataLoading<Map<String, String>>>()

    fun getUidMappingStatus():LiveData<DataLoading<Map<String, String>>> = uidMappingStatus

    fun clearUidMappingStatus() {
        uidMappingStatus.value = null
    }

    fun getChatMessages(): Query {
        return convoRepository.getChatMessages(chatId)
    }

    fun getChatInfo()   {
        convoRepository.getChatMetaData(chatId)
            .addOnSuccessListener {
                if (it.exists())    {
                    val chatData = it.toObject(Chat::class.java)
                    val ids = chatData!!.members.keys
                    getUidMappings(ids)
                } else {
                    Log.d("uids", "chat document does not exist")
                }
            }
    }

    private fun getUidMappings(uids:Set<String>) {
        val uidToUsernameMap = HashMap<String, String>()
        for (uid in uids)   {
            convoRepository.getChatUsername(uid)
                .addOnSuccessListener {
                    for (document in it.documents)  {
                        val userUid= document["uid"] as String
                        val username = document.id
                        uidToUsernameMap[userUid] = username
                    }
                    uidMappingStatus.value = DataLoading("LOADED", uidToUsernameMap)
                }
                .addOnFailureListener {
                    uidMappingStatus.value = DataLoading("ERROR: $it")
                }
        }
    }

}