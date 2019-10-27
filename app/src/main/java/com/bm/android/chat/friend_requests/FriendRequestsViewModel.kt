package com.bm.android.chat.friend_requests

import android.util.Log
import androidx.lifecycle.ViewModel

class FriendRequestsViewModel:ViewModel()   {
    private val friendRequestsRepo = FriendRequestsRepository()


    fun removedReceivedAndSentRequests(senderId:String)    {
        removeReceivedFriendRequests(senderId)
    }

    private fun removeReceivedFriendRequests(senderUid:String)  {
        Log.d("mainLog", "in removeReceivedFriendRequests senderUid == $senderUid")

        friendRequestsRepo.getReceivedFriendRequest(senderUid)
            .addOnSuccessListener {
                Log.d("mainLog", "is empty = ${it.isEmpty}")
                for (document in it)    {
                    friendRequestsRepo.receivedRequests()
                        .document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("mainLog", "deleted received request document")
                            removeSentFriendRequest(senderUid)
                        }
                        .addOnFailureListener {
                            Log.d("mainLog", "failed at removeReceivedFriendRequests")
                        }
                }
            }
            .addOnFailureListener {
                Log.d("mainLog", it.toString())
            }
    }

    private fun removeSentFriendRequest(senderId: String)  {
        Log.d("mainLog", "in removeSentFriendRequests")
        friendRequestsRepo.getSentFriendRequest(senderId)
        .addOnSuccessListener {
            for (document in it)    {
                friendRequestsRepo.getSentFriendRequestCollection(senderId)
                    .document(document.id)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("mainLog", "deleted sent request document")
                    }
                    .addOnFailureListener {
                        Log.d("mainLog", it.toString())
                    }
            }
        }
        .addOnFailureListener {
            Log.d("mainLog", it.toString())
        }
    }
}