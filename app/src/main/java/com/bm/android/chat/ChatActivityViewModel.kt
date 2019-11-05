package com.bm.android.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.ConvoRepository
import com.bm.android.chat.friend_requests.models.Friend
import com.bm.android.chat.friend_search.FriendSearchRepository
import com.google.firebase.auth.FirebaseAuth

class ChatActivityViewModel:ViewModel() {
    private val convoRepository = ConvoRepository()
    private val friendSearchRepo = FriendSearchRepository()
    private val uidMappingStatus = MutableLiveData<String>()
    /************************************************
     * contains all uid to username mappings for the
     * current user's friends.
     */
    val uidUsernameMap = HashMap<String, String>()

    fun getUidMappingStatus():LiveData<String> = uidMappingStatus
    fun clearUidMappingStatus() {
        uidMappingStatus.value = null
    }

    fun getMappings()  {
        Log.d("convosTag", "userMap is empty = ${uidUsernameMap.isEmpty()}")
        if (uidUsernameMap.isNotEmpty()) {
            uidMappingStatus.value = "id map has already been loaded"
        }
        else {
            val currentUserId = FirebaseAuth.getInstance().uid!!
            val uids = ArrayList<String>()
            friendSearchRepo.getFriends(currentUserId)
                .addOnSuccessListener {
                    val docs = it.documents
                    if (docs.isNotEmpty())  {
                        for (doc in docs)   {
                            var friendObject = doc.toObject(Friend::class.java)
                            Log.d("convosTag", "${friendObject?.uid}")
                            uids.add(friendObject!!.uid)
                        }
                        createUidUsernameMap(uids)
                    }
                }
                .addOnFailureListener {
                    uidMappingStatus.value = "ERROR: $it"
                }
        }
    }

    private fun createUidUsernameMap(uids:ArrayList<String>) {
        for (uid in uids) {
            convoRepository.getChatUsername(uid)
                .addOnSuccessListener {
                    for (document in it.documents) {
                        val userUid = document["uid"] as String
                        val username = document.id
                        uidUsernameMap[userUid] = username
                    }
                    ////ERASE
                    val usernames = uidUsernameMap.keys
                    for (user in usernames) {
                        Log.d("convosTag", "key = $user, value = ${uidUsernameMap[user]}")
                    }
                    ////ERASE
                    uidMappingStatus.value = "id map is loaded"
                }
                .addOnFailureListener {
                    uidMappingStatus.value = "ERROR: $it"
                }
        }
    }

}