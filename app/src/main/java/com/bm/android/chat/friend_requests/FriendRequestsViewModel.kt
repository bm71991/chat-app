package com.bm.android.chat.friend_requests

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.models.DataLoading
import com.bm.android.chat.user_access.models.FriendInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class FriendRequestsViewModel:ViewModel()   {
    private val TAG = "mainLog"
    private val friendRequestsRepo = FriendRequestsRepository()
    var newRequestCount = 0
    var receivedRequestsListener:ListenerRegistration? = null
    private val newRequestCountStatus = MutableLiveData<DataLoading<Int>>()

    fun getNewRequestCountStatus():LiveData<DataLoading<Int>> = newRequestCountStatus
    fun clearNewRequestCountStatus()    {
        newRequestCountStatus.value = null
    }

    fun addToCurrentFriends(senderId:String, senderUsername:String)   {
        val mAuth = FirebaseAuth.getInstance()
        val recipientId = mAuth.uid!!
        val recipientUsername = mAuth.currentUser!!.displayName!!
        //Add sender to current user's friend list
        friendRequestsRepo.addFriend(recipientId,
            senderId, senderUsername)
            .addOnSuccessListener {
                //add current user to the sender's friend list
                friendRequestsRepo.addFriend(senderId, recipientId, recipientUsername)
                    .addOnSuccessListener {
                        Log.d(TAG, "both friends lists are updated.")
                        removeReceivedFriendRequests(senderId)
                    }
            }
    }

    private fun removeReceivedFriendRequests(senderUid:String)  {
        friendRequestsRepo.getReceivedFriendRequest(senderUid)
            .addOnSuccessListener {
                for (document in it)    {
                    friendRequestsRepo.receivedRequests()
                        .document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "deleted received request document")
                            removeSentFriendRequest(senderUid)
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "failed at removeReceivedFriendRequests")
                        }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    fun setReceivedRequestsListener():ListenerRegistration? {
        val currentUid = FirebaseAuth.getInstance().uid
        if (currentUid == null) return null

        receivedRequestsListener = friendRequestsRepo.getFriendsDocument(currentUid)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot != null)   {
                    val friendObject = documentSnapshot.toObject(FriendInfo::class.java)
                    newRequestCount = friendObject!!.newRequestCount
                    newRequestCountStatus.value = DataLoading("NEW REQUEST COUNT", newRequestCount)
                }
            }
        return receivedRequestsListener
    }

    fun clearReceivedRequestsListenerAndCount() {
        receivedRequestsListener?.remove()
        receivedRequestsListener = null
        newRequestCount = 0
    }

    fun removeReceivedRequestCount()    {
        friendRequestsRepo.clearReceivedFriendRequestCount()
            .addOnSuccessListener {
                newRequestCount = 0
            }
    }

    private fun removeSentFriendRequest(senderId: String)  {
        Log.d(TAG, "in removeSentFriendRequests")
        friendRequestsRepo.getSentFriendRequest(senderId)
        .addOnSuccessListener {
            for (document in it)    {
                friendRequestsRepo.deleteSentRequest(senderId, document.id)
                    .addOnSuccessListener {
                        Log.d(TAG, "deleted sent request document")
                    }
            }
        }
        .addOnFailureListener {
            Log.d(TAG, it.toString())
        }
    }

    fun getReceivedRequests():Query   {
        return friendRequestsRepo.receivedRequests()
    }
}