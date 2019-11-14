package com.bm.android.chat.friend_requests

import android.util.Log
import com.bm.android.chat.DbConstants
import com.bm.android.chat.friend_requests.models.Friend
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class FriendRequestsRepository  {
    private val db = FirebaseFirestore.getInstance()

    fun receivedRequests() = db.collection(DbConstants.FRIENDS_COLLECTION)
                            .document(FirebaseAuth.getInstance().uid!!)
                            .collection(DbConstants.RECEIVED_REQUESTS)

    fun getReceivedFriendRequest(senderId: String):Task<QuerySnapshot> {
        return receivedRequests()
               .whereEqualTo("senderUid", senderId)
               .get()
    }

    fun getSentFriendRequestCollection(senderId: String): CollectionReference {
        return db.collection(DbConstants.FRIENDS_COLLECTION)
                .document(senderId)
                .collection(DbConstants.SENT_REQUESTS)
    }

    fun getSentFriendRequest(senderId: String):Task<QuerySnapshot>  {
        return getSentFriendRequestCollection(senderId)
            .whereEqualTo("recipientUid", FirebaseAuth.getInstance().uid!!)
            .get()
    }

    fun getFriendsDocument(userId:String):DocumentReference  {
        return db.collection(DbConstants.FRIENDS_COLLECTION)
            .document(userId)
    }

    fun clearReceivedFriendRequestCount():Task<Void>   {
        val currentUserId = FirebaseAuth.getInstance().uid!!
        return db.collection("friends")
            .document(currentUserId)
            .update("newRequestCount", 0)
    }

    fun deleteSentRequest(senderId:String, docId:String):Task<Void>    {
        return getSentFriendRequestCollection(senderId)
            .document(docId)
            .delete()
    }

    /*********************************************************
     userId = id of user for whom the friend will be added
     friendId = id of new friend
     friendUsername = username of new friend
     */
    fun addFriend(userId:String, friendId:String, friendUsername:String):Task<DocumentReference>   {
        return db.collection(DbConstants.FRIENDS_COLLECTION)
            .document(userId)
            .collection(DbConstants.CURRENT_FRIENDS)
            .add(Friend(friendId, friendUsername))
    }
}