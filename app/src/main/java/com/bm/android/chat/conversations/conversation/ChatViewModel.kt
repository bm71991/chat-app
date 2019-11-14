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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatViewModel: ViewModel() {
    var chatId:String = ""
    private val convoRepository = ConvoRepository()
    private val chatStatus = MutableLiveData<String>()
    val memberNames = HashSet<String>()
    var chatData:Chat? = null
    var newMessageListener:ListenerRegistration? = null

    val newChatMessageCountMap = HashMap<String, Int>()
    var newChatMessageCountTotal = 0
    private val newChatMessageCountStatus = MutableLiveData<DataLoading<Int>>()

    fun getNewChatMessageCountStatus():LiveData<DataLoading<Int>> = newChatMessageCountStatus
    fun clearNewChatMessageCountStatus()    {
        newChatMessageCountStatus.value = null
    }


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
                convoRepository.incrementNewMessageCount(chatId, getMemberNameArray())
            }
    }

    fun addChat(message:String)   {
        convoRepository.addChat(getMemberNameArray(), createNewMessageCountMap())
            .addOnSuccessListener {
            chatId = it.id
            addChatMessage(message)
            getChatInfo()
        }
    }

    private fun createNewMessageCountMap():HashMap<String, Int>    {
        val countMap = HashMap<String, Int>()
        for (member in memberNames)    {
            countMap[member] = 0
        }
        return countMap
    }

    fun getMemberNameArray():ArrayList<String>    {
        return memberNames.toTypedArray().toCollection(ArrayList())
    }

    /**************************************************************
     * For the edge case when the current user is preparing
     * to start a chat with another user, but this other user
     * sends a message (and creates a new chat document)
     * before the current user does. chatId will be set to the chat
     * document id that the other user has created.
     */
    fun setNewChatListener():ListenerRegistration    {
        for (member in getMemberNameArray())    {
            Log.d("listenerTest", "member: $member")
        }
        return convoRepository.getChatQuery(getMemberNameArray())
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (querySnapshot!!.documentChanges.isNotEmpty()) {
                    for(documentChange in querySnapshot.documentChanges)    {
                        when (documentChange.type)  {
                            DocumentChange.Type.ADDED ->    {
                                val newChatId = documentChange.document.id
                                chatId = newChatId
                                getChatInfo()
                            }
                        }
                    }
                }
            }
    }

    fun clearNewMessageCount() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.displayName!!
        convoRepository.clearNewMessageCount(chatId, currentUser)
    }

    fun setNewMessageListener():ListenerRegistration? {
        val currentUser = getCurrentUsername()
        if (currentUser == null) return null

        newMessageListener = convoRepository.getAllChats(currentUser)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (querySnapshot != null && querySnapshot.documentChanges.isNotEmpty()) {
                    for(documentChange in querySnapshot.documentChanges) {
                        var doc = documentChange.document
                        var chatObject = doc.toObject(Chat::class.java)
                        var newCountValue = chatObject.newMessageCount[currentUser]
                        var chatId = doc.id

                        when (documentChange.type)  {
                            DocumentChange.Type.ADDED ->    {
                                if (newCountValue != null) {
                                    //add the message count to the previous number of new messages
                                    newChatMessageCountTotal += newCountValue
                                    newChatMessageCountStatus.value = DataLoading("NEW_CHAT_MESSAGE_COUNT", newChatMessageCountTotal)
                                    newChatMessageCountMap[chatId] = newCountValue
                                    Log.d("newMessageListener", "ADDED: total new message count = $newChatMessageCountTotal")
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                var previousCountValue = newChatMessageCountMap[chatId]
                                if (newCountValue != null && previousCountValue != null)    {
                                    if (previousCountValue > newCountValue)  {
                                        val difference = previousCountValue - newCountValue
                                        newChatMessageCountTotal -= difference
                                        Log.d("newMessageListener", "MODIFIED: DECREASED total new message count = $newChatMessageCountTotal")
                                    }
                                    /*ignore when previousCountValue == newCountValue
                                      if messageCount has increased subtract the difference and add to
                                      newChatMessageCountTotal
                                     */
                                    if (previousCountValue < newCountValue) {
                                        val difference = newCountValue - previousCountValue
                                        newChatMessageCountTotal += difference
                                        Log.d("newMessageListener", "MODIFIED: INCREASED total new message count = $newChatMessageCountTotal")
                                    }

                                    if (previousCountValue != newCountValue) {
                                        Log.d("newMessageListener", "previous Count != newCount $newChatMessageCountTotal")
                                        newChatMessageCountStatus.value =
                                            DataLoading("NEW_CHAT_MESSAGE_COUNT", newChatMessageCountTotal)
                                        newChatMessageCountMap[chatId] = newCountValue
                                    }
                                }
                            }
                        }
                        Log.d("newMessageListener", "END: $chatId now = ${newChatMessageCountMap[chatId]}")
                    }
                }
            }
        return newMessageListener
    }

    fun clearNewMessageListenerAndCount()  {
        Log.d("newMessageListener", "clearNewMessageListener is being called")
        newMessageListener?.remove()
        newMessageListener = null
        newChatMessageCountMap.clear()
        newChatMessageCountTotal = 0
    }

    fun getCurrentUsername():String?   {
        return FirebaseAuth.getInstance().currentUser?.displayName
    }
}