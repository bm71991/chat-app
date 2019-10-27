package com.bm.android.chat.friend_requests

import android.util.Log
import com.bm.android.chat.DbConstants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

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
}