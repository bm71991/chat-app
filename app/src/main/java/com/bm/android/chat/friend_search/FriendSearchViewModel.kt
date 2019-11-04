package com.bm.android.chat.friend_search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.user_access.models.FriendInfo
import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import com.bm.android.chat.user_access.models.SentFriendRequest
import com.bm.android.chat.user_access.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.type.Date

class FriendSearchViewModel : ViewModel() {
    private val friendSearchRepo = FriendSearchRepository()
    private val TAG = "mainLog"
    private val usernameSearchStatus = MutableLiveData<String>()
    private val sendFriendRequestStatus = MutableLiveData<String>()
    private var queriedUserId = ""
    private var queriedUsername = "";

    fun getUsernameSearchStatus(): LiveData<String> = usernameSearchStatus
    fun clearUsernameSearchStatus() {
        usernameSearchStatus.value = null
    }

    fun getSendFriendRequestStatus(): LiveData<String> = sendFriendRequestStatus
    fun clearSendFriendRequestStatus()  {
        sendFriendRequestStatus.value = null
    }

    /*********************************************************
     * Methods called when friendSearchBtn is clicked:
     */

    fun checkIfUserExists(username:String)  {
        friendSearchRepo.getUserInfo(username)
            /*****************************************************************
             * If user exists,
             * 1. deserialize result into User object
             * 2. extract the uid of the user and pass into checkIfFriendable
             */
            .addOnSuccessListener {
                if (it.exists())    {
                    val searchedUserDocument = it.toObject(User::class.java)
                    queriedUserId = searchedUserDocument!!.uid!!
                    queriedUsername = username
                    checkIfFriendable()
                } else    {
                    usernameSearchStatus.value = USERNAME_NOT_FOUND
                }
            }
            .addOnFailureListener   {
                usernameSearchStatus.value = it.toString()
            }
    }

    /**********************************************************
     * Method chain checks if:
     * 1. User has already sent a request to this username
     * 2. User has a pending friend request from this username
     * 3. User is already friends with this username
     */
    private fun checkIfFriendable() {
                checkIfSentRequest(queriedUserId)
    }
    //1.
    private fun checkIfSentRequest(prospectiveFriendId: String) {
        friendSearchRepo.checkIfSentRequest(prospectiveFriendId)
            .addOnSuccessListener {
                if (it.isEmpty)    {
                    checkIfReceivedRequest(queriedUserId)
                } else {
                    usernameSearchStatus.value = REQUEST_ALREADY_SENT
                }
            }
    }
    //2.
    private fun checkIfReceivedRequest(prospectiveFriendId: String) {
        Log.d(TAG, "in checkIfReceivedRequest friend Id = $prospectiveFriendId")
        friendSearchRepo.checkIfReceivedRequest(prospectiveFriendId)
            .addOnSuccessListener {
                if (it.isEmpty)    {
                    checkIfAlreadyFriends(prospectiveFriendId)
                } else {
                    usernameSearchStatus.value = REQUEST_ALREADY_RECEIVED
                }
            }
    }
    //3.
    private fun checkIfAlreadyFriends(prospectiveFriendId: String) {
        friendSearchRepo.getFriends(FirebaseAuth.getInstance().uid!!)
            .addOnSuccessListener {
                var alreadyFriends = false
                val documents = it.documents
                if (documents.isNotEmpty()) {
                    var index = 0
                    while (index < documents.size && !alreadyFriends)   {
                        var docId = documents[index]["uid"]
                        Log.d(TAG,"prospectiveFriendId = $prospectiveFriendId, docId = $docId")
                        if (docId == prospectiveFriendId) alreadyFriends = true
                        index++
                    }
                }
                if (alreadyFriends) {
                    usernameSearchStatus.value = ALREADY_FRIENDS
                } else {
                    usernameSearchStatus.value = OK_TO_DISPLAY
                }
            }
    }

    /*********************************************************
     * Methods called when friendRequestBtn is clicked:
     */

    fun sendFriendRequest() {
        updateSentFriendRequests()
    }

    private fun updateSentFriendRequests()  {
        val sentFriendRequest = SentFriendRequest(queriedUserId, queriedUsername)
        Log.d(TAG, "QUERIED USER ID = $queriedUserId, CURRENT USER ID = ${FirebaseAuth.getInstance().uid!!}")
        friendSearchRepo.updateSentFriendRequests(FirebaseAuth.getInstance().uid!!, sentFriendRequest)
            .addOnSuccessListener {
                updateReceivedFriendRequests()
            }
            .addOnFailureListener {
                sendFriendRequestStatus.value = it.toString()
            }
    }

    private fun updateReceivedFriendRequests()  {
        val receivedFriendRequest = ReceivedFriendRequest(
            FirebaseAuth.getInstance().uid!!,
            FirebaseAuth.getInstance().currentUser!!.displayName!!,
            Timestamp(java.util.Date()))
        friendSearchRepo.updateReceivedFriendRequests(queriedUserId, receivedFriendRequest)
            .addOnSuccessListener {
                sendFriendRequestStatus.value = REQUEST_SENT
            }
            .addOnFailureListener {
                sendFriendRequestStatus.value = it.toString()
            }
    }

    /******************************************************************************
     * String constants propagated to FriendSearchFragment by usernameSearchStatus
     */
    companion object    {
        const val USERNAME_NOT_FOUND = "Username entered does not exist."
        const val REQUEST_ALREADY_SENT = "You have already sent a friend request to this user."
        const val REQUEST_ALREADY_RECEIVED = "You have already received a pending friend " +
                "request from this user."
        const val ALREADY_FRIENDS = "You are already friends with this user."
        const val OK_TO_DISPLAY = "User exists and is friendable."
        const val REQUEST_SENT = "Friend request was successfully sent."
    }
}