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
     * Get all documents in collection 'chat' where:
     * 1. field 'members' contains the uid of every person in RecipientList,
     * including the current user.
     * 2. members.memberCount = RecipientList.length
     */
    fun getChat(recipientList:ArrayList<Friend>):Task<QuerySnapshot>    {
        var query = chatCollection as Query

        for (recipient in recipientList)    {
            Log.d("chatLog", "${recipient.uid}")
            query = query.whereEqualTo("members.${recipient.uid}", true)
        }
        query = query.whereEqualTo("memberCount", recipientList.size)
        return query.get()
    }

    fun addChat(recipientList:ArrayList<Friend>):Task<DocumentReference>   {
        val members = addMembersToHashMap(recipientList)
        val newChat = Chat(members, members.size)
        return chatCollection.add(newChat)
    }

    private fun addMembersToHashMap(recipientList:ArrayList<Friend>):HashMap<String, Boolean>   {
        val members = HashMap<String, Boolean>()
        for (recipient in recipientList)    {
            members[recipient.uid] = true
        }
        return members
    }

    /****************************************************************
     * Adds ChatMessage document to the subcollection of a specified
     * Chat document
     */
    fun addMessage(chatId:String, message:String, currentUserId:String):Task<DocumentReference>  {
        return chatCollection
               .document(chatId)
               .collection(DbConstants.MESSAGE_COLLECTION)
               .add(ChatMessage(message, currentUserId, Timestamp.now()))
    }
}