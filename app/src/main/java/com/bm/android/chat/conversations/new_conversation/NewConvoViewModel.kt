package com.bm.android.chat.conversations.new_conversation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.ConvoRepository
import com.bm.android.chat.conversations.models.DataLoading
import com.bm.android.chat.friend_requests.models.Friend
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot

class NewConvoViewModel: ViewModel()   {
    val mConvoRepository = ConvoRepository()

    private val TAG = "convoTag"
    private val convoRepository = ConvoRepository()
    //list of recipients for the new message
    var recipientList = ArrayList<Friend>()
    //keeps checkbox state in RecipientDialog
    var namesChecked = HashSet<Friend>()
    //for indicating when getProspectiveRecipients() is done calling Firestore
    private var prospectiveRecipsStatus = MutableLiveData<DataLoading<ArrayList<Friend>>>()
    var prospectiveRecipients = ArrayList<Friend>()
    private var newConvoStatus = MutableLiveData<DataLoading<String>>()

    fun getNewConvoStatus():LiveData<DataLoading<String>> = newConvoStatus
    fun clearNewConvoStatus()   {
        newConvoStatus.value = null
    }

    fun getProspectiveRecipsStatus():LiveData<DataLoading<ArrayList<Friend>>>
            = prospectiveRecipsStatus
    fun clearProspectiveRecipsStatus() {
        prospectiveRecipsStatus.value = null
    }

    /******************************************
     * return list of friend objects where the friends are:
     * 1. friends with the user
     * 2. not already displayed in the NewConvoFragment recyclerview (contained in recipientList)
     * shown in RecipientDialog
     */
    fun getProspectiveRecipients()   {
        Log.d("friends", "CALLED")
        val auth = FirebaseAuth.getInstance()
        val userId = auth.uid!!
        val prospectiveRecipients = ArrayList<Friend>()
        convoRepository.getAllCurrentFriends(userId)
            .addOnSuccessListener {
                for (document in it)    {
                    if (shouldBeAdded(document))    {
                        prospectiveRecipients.add(Friend(document.get("uid") as String,
                                                 document.get("username") as String))
                    }
                }
                this.prospectiveRecipients.addAll(prospectiveRecipients)
                prospectiveRecipsStatus.value = DataLoading("LOADED", prospectiveRecipients)
            }
            .addOnFailureListener {
                prospectiveRecipsStatus.value = DataLoading("ERROR", null)
            }
    }

    private fun shouldBeAdded(friendDoc:QueryDocumentSnapshot):Boolean  {
        val friendUid = friendDoc.get("uid") as String
        Log.d("friends", "friendUid = $friendUid")
        var index = 0
        var alreadyInList = false
        while (index < recipientList.size && !alreadyInList)   {
            if (recipientList[index].uid == friendUid) {
                alreadyInList = true
            }
            index++
        }

        return !alreadyInList
    }

    fun removeFriendInConvo(friendToRemove:Friend) {
        recipientList.remove(friendToRemove)
    }

    fun checkIfChatExists(message:String) {
        /************************************
         * add current user to recipient list
         */
        val auth = FirebaseAuth.getInstance()
        val currentUserId = auth.uid!!
        val currentUsername = auth.currentUser!!.displayName!!
        recipientList.add(Friend(currentUserId, currentUsername))

        mConvoRepository.getChat(recipientList)
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    Log.d("chatLog", "this chat does not exist yet")
                    /**********************************************************
                     * A chat group with this combination of users does not
                     * already exist: create a new chat document and
                     * subsequently add the message
                     */
                    addChat(message)
                } else {
                    Log.d("chatLog", "this chat already exists")
                    /**********************************************************
                     * A chat group with this combination of users does
                     * already exists: add the message to the existing chat
                     * document
                     */
                    val existingChatId = it.documents[0].id
                    addMessage(existingChatId, message)
                }
            }
            .addOnFailureListener {
                newConvoStatus.value = DataLoading("ERROR", it.toString())
            }
    }

    private fun addChat(message:String)   {
        val nameList = ArrayList<String>()

        for (recipient in recipientList)    {
            nameList.add(recipient.username)
        }

        mConvoRepository.addChat(nameList, createNewMessageCount())
            .addOnSuccessListener {
                //get id of newly added doc
                addMessage(it.id, message)
            }
            .addOnFailureListener {
                newConvoStatus.value = DataLoading("ERROR", it.toString())
            }
    }


    private fun addMessage(chatId:String, message:String)    {
        mConvoRepository.addMessage(chatId, message,
            FirebaseAuth.getInstance().currentUser!!.displayName!!)
            .addOnSuccessListener {
                setLastMessage(message, chatId, it.id)
            }
            .addOnFailureListener {
                newConvoStatus.value = DataLoading("ERROR", it.toString())
            }
    }

    private fun setLastMessage(message:String, chatId:String, messageId:String)   {
        convoRepository.setLastMessage(message, Timestamp.now(), chatId, messageId)
            .addOnSuccessListener {
                incrementNewMessageCount(chatId, getRecipientNames())
            }
            .addOnFailureListener {
                newConvoStatus.value = DataLoading("ERROR", it.toString())
            }
    }

    private fun createNewMessageCount():HashMap<String, Int>    {
        val countMap = HashMap<String, Int>()

        for (recipient in recipientList) {
            countMap[recipient.username] = 0
        }
        return countMap
    }

    private fun incrementNewMessageCount(chatId:String, usernames:ArrayList<String>)    {
        convoRepository.incrementNewMessageCount(chatId, getRecipientNames())
            .addOnSuccessListener {
                newConvoStatus.value = DataLoading("MESSAGE_ADDED", chatId)
            }
    }

    private fun getRecipientNames():ArrayList<String>   {
        var recipientNames = ArrayList<String>()
        for (recipient in recipientList)    {
            recipientNames.add(recipient.username)
        }
        return recipientNames
    }
}