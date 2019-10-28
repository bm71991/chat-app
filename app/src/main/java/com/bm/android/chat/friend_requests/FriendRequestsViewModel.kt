package com.bm.android.chat.friend_requests

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class FriendRequestsViewModel:ViewModel()   {
    private val TAG = "mainLog"
    private val friendRequestsRepo = FriendRequestsRepository()

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

    private fun removeSentFriendRequest(senderId: String)  {
        Log.d(TAG, "in removeSentFriendRequests")
        friendRequestsRepo.getSentFriendRequest(senderId)
        .addOnSuccessListener {
            for (document in it)    {
                friendRequestsRepo.getSentFriendRequestCollection(senderId)
                    .document(document.id)
                    .delete()
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