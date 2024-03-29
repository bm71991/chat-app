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

    fun getChatQuery(recipientList: ArrayList<String>):Query  {
        var query = chatCollection as Query

        for (recipient in recipientList)    {
            query = query.whereEqualTo("members.$recipient", true)
        }
        query = query.whereEqualTo("memberCount", recipientList.size)
        return query
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

    fun getAllChats(username:String):Query    {
        return chatCollection
            .whereEqualTo("members.$username", true)
    }

    fun addChat(nameList:ArrayList<String>, newMessageCountMap:HashMap<String, Int>):Task<DocumentReference>   {
        val members = addMembersToLookupHashMap(nameList)
        val newChat = Chat(members, members.size, newMessageCountMap)
        return chatCollection.add(newChat)
    }

    private fun addMembersToLookupHashMap(recipientList:ArrayList<String>):HashMap<String, Boolean>   {
        val members = HashMap<String, Boolean>()
        for (recipient in recipientList)    {
            members[recipient] = true
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

    /***************************************************
     * Get all chat documents for which the current user
     * is a member
     */
    fun getChats():Query  {
        val currentUsername = FirebaseAuth.getInstance().currentUser?.displayName
        return chatCollection
            .whereEqualTo("members.${currentUsername}", true)
//            .orderBy("lastMessage.timeSent", Query.Direction.DESCENDING)
    }

    /********************************************
     * updates the lastMessage field of a chat
     * object. Used in ConvosFragment to set
     * the last message of a chat item
     */
    fun setLastMessage(message:String, timeSent:Timestamp, chatId:String, messageId:String):Task<Void>  {
        return chatCollection
            .document(chatId)
            .update("lastMessage", hashMapOf("message" to message,
                                        "timeSent" to timeSent,
                                         "id" to messageId))
    }

    fun createNewMessageCount(chatId:String, messageCountMap:HashMap<String, Int>):Task<Void>   {
        return db.collection(DbConstants.CHATS_COLLECTION)
            .document(chatId)
            .update("newMessageCount", messageCountMap)
    }


    fun incrementNewMessageCount(chatId:String, usernames:ArrayList<String>):Task<Void>  {
        val chatRef = db.collection(DbConstants.CHATS_COLLECTION).document(chatId)
        val currentUsername = FirebaseAuth.getInstance().currentUser!!.displayName!!
        return db.runBatch { batch ->
            for (username in usernames) {
                if (username != currentUsername)    {
                    batch.update(chatRef, "newMessageCount.$username", FieldValue.increment(1))
                }
            }
        }
    }

    fun clearNewMessageCount(chatId:String, username:String):Task<Void>   {
        return db.collection(DbConstants.CHATS_COLLECTION)
            .document(chatId)
            .update("newMessageCount.$username", 0)
    }

    fun changeNewMessageCount(chatId:String, username:String, amount:Int):Task<Void>   {
        return db.collection(DbConstants.CHATS_COLLECTION)
            .document(chatId)
            .update("newMessageCount.$username", amount)
    }

    fun getNewMessageCount():Query {
        val currentUsername = FirebaseAuth.getInstance().currentUser!!.displayName!!
        return db.collection(("newMessageCount"))
            .whereEqualTo("username", currentUsername)
    }

    fun updateMessage(messageId:String, newMessage:String, chatId:String):Task<Void>  {
        return db.collection(DbConstants.CHATS_COLLECTION)
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .update(mapOf(
                "message" to newMessage,
                "updated" to true,
                "updateTime" to Timestamp.now()
            ))
    }

    fun updateMostRecentMessage(newMessage:String, chatId:String):Task<Void>    {
        return chatCollection
            .document(chatId)
            .update("lastMessage.message", newMessage)
    }
}