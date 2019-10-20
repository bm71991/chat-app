package com.bm.android.chat.friend_search

import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import com.bm.android.chat.user_access.models.SentFriendRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FriendSearchRepository   {
    private val db = FirebaseFirestore.getInstance()
    private val USER_COLLECTION = "users"
    private val FRIENDS_COLLECTION = "friends"
    private val TAG = "mainLog"

    /***************************************************************
     * Get the document from collection 'friends' whose id == userId
     */
    fun getFriendInfo(userId:String): Task<DocumentSnapshot>   {
        return db.collection(FRIENDS_COLLECTION)
            .document(userId).get()
    }

    fun getUserInfo(username:String):Task<DocumentSnapshot>    {
        return db.collection(USER_COLLECTION)
            .document(username).get()
    }

    /*************************************************
     * Adds an item detailing a friend request
     * to the sentRequests field of the sending
     * user's document in collection 'friends'
     */
    fun updateSentFriendRequests(sendingUserId:String,
                                 sentFriendRequest: SentFriendRequest): Task<Void> {
        return db.collection(FRIENDS_COLLECTION).document(sendingUserId)
            .update("sentRequests", FieldValue.arrayUnion(sentFriendRequest))
    }
    /**************************************************
     * Adds an item detailing a friend request
     * to the receivedRequests field of the receiving
     * user's document in collection 'friends'
     */
    fun updateReceivedFriendRequests(receivingUserId:String,
                                     receivedFriendRequest: ReceivedFriendRequest):Task<Void>  {
        return db.collection(FRIENDS_COLLECTION).document(receivingUserId)
            .update("receivedRequests", FieldValue.arrayUnion(receivedFriendRequest))
    }
}