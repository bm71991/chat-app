package com.bm.android.chat.friend_search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.user_access.models.FriendInfo
import com.bm.android.chat.user_access.models.ReceivedFriendRequest
import com.bm.android.chat.user_access.models.SentFriendRequest
import com.bm.android.chat.user_access.models.User
import com.google.firebase.auth.FirebaseAuth

class FriendSearchViewModel : ViewModel() {
    private val friendSearchRepo = FriendSearchRepository()
    private val TAG = "mainLog"
    private val usernameSearchStatus = MutableLiveData<String>()
    private val sendFriendRequestStatus = MutableLiveData<String>()
    private val mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.uid!!
    private var queriedUserId = ""

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
        friendSearchRepo.getFriendInfo(userId)
            .addOnSuccessListener {
                val friendInfo = it.toObject(FriendInfo::class.java)
                checkIfSentRequest(friendInfo!!, queriedUserId)
            }
    }
    //1.
    private fun checkIfSentRequest(friendInfo: FriendInfo,
                                   prospectiveFriendId: String) {
        var i = 0
        var idMatch = false
        val sentRequests = friendInfo.sentRequests

        while (i < sentRequests.size && !idMatch)    {
            if (sentRequests[i].recipientUid == prospectiveFriendId)    {
                usernameSearchStatus.value = REQUEST_ALREADY_SENT
                idMatch = true
            }
            i++
        }
        if (!idMatch) checkIfReceivedRequest(friendInfo, prospectiveFriendId)
    }
    //2.
    private fun checkIfReceivedRequest(friendInfo: FriendInfo,
                                       prospectiveFriendId: String) {
        var i = 0
        var idMatch = false
        val receivedRequests = friendInfo.receivedRequests

        while (i < receivedRequests.size && !idMatch) {
            if (receivedRequests[i].senderUid == prospectiveFriendId)   {
                usernameSearchStatus.value = REQUEST_ALREADY_RECEIVED
                idMatch = true
            }
            i++
        }
        if (!idMatch) checkIfAlreadyFriends(friendInfo, prospectiveFriendId)
    }
    //3.
    private fun checkIfAlreadyFriends(friendInfo: FriendInfo,
        prospectiveFriendId: String) {
        var i = 0
        var idMatch = false
        val currentFriends = friendInfo.currentFriends

        while (i < currentFriends.size && !idMatch) {
            if (currentFriends[i] == prospectiveFriendId)   {
                usernameSearchStatus.value = ALREADY_FRIENDS
                idMatch = true
            }
            i++
        }
        if (!idMatch) usernameSearchStatus.value = OK_TO_DISPLAY
    }

    /*********************************************************
     * Methods called when friendRequestBtn is clicked:
     */

    fun sendFriendRequest() {
        updateSentFriendRequests()
    }

    private fun updateSentFriendRequests()  {
        val sentFriendRequest = SentFriendRequest(queriedUserId)
        friendSearchRepo.updateSentFriendRequests(userId, sentFriendRequest)
            .addOnSuccessListener {
                updateReceivedFriendRequests()
            }
            .addOnFailureListener {
                sendFriendRequestStatus.value = it.toString()
            }
    }

    private fun updateReceivedFriendRequests()  {
        val receivedFriendRequest = ReceivedFriendRequest(userId)
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