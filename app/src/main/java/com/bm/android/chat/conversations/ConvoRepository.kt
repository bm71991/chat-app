package com.bm.android.chat.conversations

import android.util.Log
import com.bm.android.chat.DbConstants
import com.bm.android.chat.conversations.models.Chat
import com.bm.android.chat.conversations.models.ChatMessage
import com.bm.android.chat.friend_requests.models.Friend
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ConvoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val chatCollection = db.collection(DbConstants.CHATS_COLLECTION)

    fun getAllCurrentFriends(uid:String):Task<QuerySnapshot>    {
        return db.collection(DbConstants.FRIENDS_COLLECTION)
            .document(uid)
            .collection(DbConstants.CURRENT_FRIENDS)
            .get()
    }

    /**************************************************************************
     * (Used in NewConvoViewModel)
     * Get all documents in collection 'chat' where:
     * 1. field 'members' contains the uid of every person in RecipientList,
     * including the current user.
     * 2. members.memberCount = RecipientList.length
     */
    fun getChat(recipientList:ArrayList<Friend>):Task<QuerySnapshot>    {
        var query = chatCollection as Query

        for (recipient in recipientList)    {
            query = query.whereEqualTo("members.${recipient.username}", true)
        }
        query = query.whereEqualTo("memberCount", recipientList.size)
        return query.get()
    }

    /***********************************************************************************
     * Returns all message documents from subcollection 'messages' of the chat document
     * whose id = chatId. Firestore does not support retrieving a subcollection along
     * with the other fields of a document, so these two retrievals must be done
     * separately (see getChatMetaData())
     */
    fun getChatMessages(chatId:String):Query  {
        return chatCollection
            .document(chatId)
            .collection("messages")
            .orderBy("timeSent")
    }

    /***********************************************************************
     *Retrieves all fields of a chat document other than subcollection
     * 'messages'
     */
    fun getChatMetaData(chatId: String):Task<DocumentSnapshot> {
        return chatCollection
            .document(chatId)
            .get()
    }

    fun addChat(recipientList:ArrayList<Friend>):Task<DocumentReference>   {
        val members = addMembersToLookupHashMap(recipientList)
        val newChat = Chat(members, members.size)
        return chatCollection.add(newChat)
    }

    private fun addMembersToLookupHashMap(recipientList:ArrayList<Friend>):HashMap<String, Boolean>   {
        val members = HashMap<String, Boolean>()
        for (recipient in recipientList)    {
            members[recipient.username] = true
        }
        return members
    }


    /****************************************************************
     * Adds ChatMessage document to the subcollection of a specified
     * Chat document
     */
    fun addMessage(chatId:String, message:String, currentUsername:String):Task<DocumentReference>  {
        return chatCollection
               .document(chatId)
               .collection(DbConstants.MESSAGE_COLLECTION)
               .add(ChatMessage(message, currentUsername, Timestamp.now()))
    }

//    fun getChatUsername(uid:String):Task<QuerySnapshot> {
//        return db.collection(DbConstants.USERS_COLLECTION)
//            .whereEqualTo("uid", uid)
//            .get()
//    }

    /***************************************************
     * Get all chat documents for which the current user
     * is a member
     */
    fun getChats():Query  {
        val currentUsername = FirebaseAuth.getInstance().currentUser!!.displayName!!
        Log.d("convosTest", "current username = $currentUsername")
        return chatCollection
            .whereEqualTo("members.${currentUsername}", true)
    }


    /********************************************
     * updates the lastMessage field of a chat
     * object. Used in ConvosFragment to set
     * the last message of a chat item
     */
    fun setLastMessage(message:String, timeSent:Timestamp, chatId:String):Task<Void>  {
        return chatCollection
            .document(chatId)
            .update("lastMessage", hashMapOf("message" to message, "timeSent" to timeSent))
    }
}