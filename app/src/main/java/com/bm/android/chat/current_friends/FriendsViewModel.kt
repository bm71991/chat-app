package com.bm.android.chat.current_friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.models.DataLoading
import com.bm.android.chat.friend_requests.models.Friend
import com.bm.android.chat.friend_search.FriendSearchRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class FriendsViewModel:ViewModel() {
    private val friendSearchRepo = FriendSearchRepository()
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

    fun getFriends()    {
        friendSearchRepo.getFriends(FirebaseAuth.getInstance().uid!!)
            .addOnSuccessListener {
                val documents = it.documents
                if (documents.isNotEmpty()) {
                    val friendList = ArrayList<FriendsFragment.FriendsListItem>()
                    for (document in documents) {
                        val friend = document.toObject(Friend::class.java)
                        val username = friend!!.username
                        friendList.add(FriendItem(username))
                    }
                    friendLoadingStatus.value = DataLoading("LOADED", friendList)
                }
            }
            .addOnFailureListener {
                friendLoadingStatus.value = DataLoading("ERROR: $it")
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

    fun listenForFriendChanges():ListenerRegistration   {
        return friendSearchRepo.getFriendsReference(FirebaseAuth.getInstance().uid!!)
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
    }
}