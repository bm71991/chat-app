package com.bm.android.chat.friend_search

import com.bm.android.chat.DbConstants
import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import com.bm.android.chat.user_access.models.SentFriendRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class FriendSearchRepository   {
    private val db = FirebaseFirestore.getInstance()
    private val USER_COLLECTION = "users"
    private val FRIENDS_COLLECTION = "friends"
    private val TAG = "mainLog"

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
                                 sentFriendRequest: SentFriendRequest): Task<DocumentReference> {
        return db.collection(FRIENDS_COLLECTION)
            .document(sendingUserId)
            .collection("sentRequests")
            .add(sentFriendRequest)
    }
    /**************************************************
     * Adds an item detailing a friend request
     * to the receivedRequests field of the receiving
     * user's document in collection 'friends'
     */
    fun updateReceivedFriendRequests(receivingUserId:String,
                                     receivedFriendRequest: ReceivedFriendRequest):Task<DocumentReference>  {
        return db.collection(FRIENDS_COLLECTION)
            .document(receivingUserId)
            .collection("receivedRequests")
            .add(receivedFriendRequest)
    }

    fun checkIfSentRequest(friendUid:String):Task<QuerySnapshot>    {
        return db.collection(FRIENDS_COLLECTION)
            .document(FirebaseAuth.getInstance().uid!!)
            .collection("sentRequests")
            .whereEqualTo("recipientUid", friendUid)
            .get()
    }

    fun checkIfReceivedRequest(friendUid:String):Task<QuerySnapshot>    {
        return db.collection(FRIENDS_COLLECTION)
            .document(FirebaseAuth.getInstance().uid!!)
            .collection("receivedRequests")
            .whereEqualTo("senderUid", friendUid)
            .get()
    }

    fun getFriends(userId:String):Task<QuerySnapshot>   {
        return db.collection(FRIENDS_COLLECTION)
            .document(userId)
            .collection(DbConstants.CURRENT_FRIENDS)
            .get()
    }
}