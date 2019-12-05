package com.bm.android.chat.current_friends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.models.DataLoading
import com.bm.android.chat.friend_requests.FriendRequestsRepository
import com.bm.android.chat.friend_requests.models.Friend
import com.bm.android.chat.friend_search.FriendSearchRepository
import com.bm.android.chat.user_access.models.FriendInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration


class FriendsViewModel:ViewModel() {
    private val friendSearchRepo = FriendSearchRepository()
    private val friendsRepository = FriendsRepository()
    private val friendRequestsRepo = FriendRequestsRepository()
    var friendsFragmentIsVisible = false
    var newFriendsCount = 0
    var newFriendsCountListener:ListenerRegistration? = null
    /***************************************************
     * Keeps track of how many friends the user has
     * which start with a certain letter
     * key = a letter, value = the number of friends
     * whose username starts with the given letter
     */
    val firstLetterCountMap = HashMap<String, Int>()
    val friendLoadingStatus = MutableLiveData<DataLoading<ArrayList<FriendsFragment.FriendsListItem>>>()
    fun getFriendLoadingStatus():LiveData<DataLoading<ArrayList<FriendsFragment.FriendsListItem>>>
     = friendLoadingStatus

    fun clearFriendLoadingStatus()  {
        friendLoadingStatus.value = null
    }

    private val newFriendStatus = MutableLiveData<FriendItem>()
    fun getNewFriendStatus():LiveData<FriendItem> = newFriendStatus
    fun clearNewFriendStatus()  {
        newFriendStatus.value = null
    }

    private val chatIdStatus = MutableLiveData<DataLoading<String>>()
    fun getChatIdStatus():LiveData<DataLoading<String>> = chatIdStatus
    fun clearChatIdStatus() {
        chatIdStatus.value = null
    }

    private val newFriendsCountStatus = MutableLiveData<DataLoading<Int>>()
    fun getNewFriendsCountStatus():LiveData<DataLoading<Int>> = newFriendsCountStatus
    fun clearNewFriendsCountStatus()    {
        newFriendsCountStatus.value = null
    }

    fun getFriends()    {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null)    {
            friendSearchRepo.getFriends(uid)
                .addOnSuccessListener {
                    val documents = it.documents
                    if (documents.isNotEmpty()) {
                        val friendSet = HashSet<FriendsFragment.FriendsListItem>()
                        for (document in documents) {
                            val friend = document.toObject(Friend::class.java)
                            val username = friend!!.username
                            Log.d("friendsDebug", " initial load: friend in getFriends: $username")
                            friendSet.add(FriendItem(username))
                        }
                        friendLoadingStatus.value = DataLoading("LOADED", friendSet.toTypedArray().toCollection(ArrayList()))
                    } else {
                        friendLoadingStatus.value = DataLoading("LOADED", ArrayList())
                    }
                }
                .addOnFailureListener {
                    friendLoadingStatus.value = DataLoading("ERROR: $it")
                }
        }
    }

    fun mapContainsLetter(letter:String):Boolean {
        return firstLetterCountMap.containsKey(letter.toUpperCase())
    }

    fun incrementLetterCount(letter:String) {
        firstLetterCountMap[letter] = firstLetterCountMap[letter]!!.plus(1)
    }

    fun startLetterCount(letter:String) {
        firstLetterCountMap[letter] = 0
    }

    fun listenForFriendChanges():ListenerRegistration?   {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null)    {
            return friendSearchRepo.getFriendsReference(uid)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot!!.documentChanges.isNotEmpty()) {
                        for(documentChange in querySnapshot.documentChanges)    {
                            when (documentChange.type)  {
                                DocumentChange.Type.ADDED ->    {
                                    val newFriend = documentChange.document.toObject(Friend::class.java)
                                    newFriendStatus.value = FriendItem(newFriend.username)
                                }
                            }
                        }
                    }
                }
        } else return null
    }

    fun getChatId(members:ArrayList<String>)   {
        friendsRepository.getChat(members)
            .addOnSuccessListener {
                val documents = it.documents
                if (documents.isNotEmpty())  {
                    for (document in documents) {
                        chatIdStatus.value = DataLoading("CHAT EXISTS", document.id)
                    }
                } else {
                      chatIdStatus.value = DataLoading("CHAT DOES NOT EXIST", "")
                }
            }
            .addOnFailureListener {
                chatIdStatus.value = DataLoading("ERROR" , it.toString())
            }
    }

    fun setNewFriendsCountListener():ListenerRegistration?  {
        val currentUid = FirebaseAuth.getInstance().uid
        if (currentUid == null) return null

        newFriendsCountListener = friendRequestsRepo.getFriendsDocument(currentUid)
            .addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot != null)   {
                    val friendObject = documentSnapshot.toObject(FriendInfo::class.java)
                    newFriendsCount = friendObject?.newFriendCount ?: 0
                    Log.d("friendsCountListener", "in listener: IS VISIBLE = $friendsFragmentIsVisible")
                    if (friendsFragmentIsVisible && newFriendsCount > 0)   {
                        Log.d("friendsCountListener", "is visible: CLEARING FRIEND COUNT")
                        friendRequestsRepo.clearNewFriendsCount(currentUid)
                    } else {
                        Log.d("friendsCountListener", "is visible == $friendsFragmentIsVisible: new count = $newFriendsCount")
                        newFriendsCountStatus.value = DataLoading("NEW FRIENDS COUNT", newFriendsCount)
                    }
                }
            }
        return newFriendsCountListener
    }

    fun clearNewFriendsCount()  {
        val currentUid = FirebaseAuth.getInstance().uid
        if (currentUid != null) {
            friendRequestsRepo.clearNewFriendsCount(currentUid)
        }

    }

    fun clearNewFriendsCountListener() {
        newFriendsCountListener?.remove()
        newFriendsCountListener = null
        newFriendsCount = 0
    }
}