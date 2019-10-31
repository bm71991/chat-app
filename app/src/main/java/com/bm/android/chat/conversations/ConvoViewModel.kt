package com.bm.android.chat.conversations

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bm.android.chat.friend_requests.models.Friend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firestore.v1.Document

class ConvoViewModel: ViewModel()   {
    interface UpdateListInterface {
        fun notifyRecipientListChange()
    }

    private val TAG = "convoTag"
    private val convoRepository = ConvoRepository()
    //list of recipients for the new message
    var recipientList = ArrayList<Friend>()
    //keeps checkbox state in RecipientDialog
    var namesChecked = HashSet<Friend>()
    //for indicating when getProspectiveRecipients() is done calling Firestore
    private var prospectiveRecipsStatus = MutableLiveData<String>()
    var prospectiveRecipients = ArrayList<Friend>()

    fun getProspectiveRecipsStatus():LiveData<String> = prospectiveRecipsStatus
    fun clearProspectiveRecipsStatus() {
        prospectiveRecipsStatus.value = null
    }

    /******************************************
     * return list of friend objects where the friends are:
     * 1. friends with the user
     * 2. not already displayed in the NewConvoFragment recyclerview (contained in recipientList)
     * shown in RecipientDialog
     */
    fun getProspectiveRecipients()   {
        Log.d("friends", "CALLED")
        val auth = FirebaseAuth.getInstance()
        val userId = auth.uid!!
        val prospectiveRecips = ArrayList<Friend>()
        convoRepository.getAllCurrentFriends(userId)
            .addOnSuccessListener {
                for (document in it)    {
                    if (shouldBeAdded(document))    {
                        prospectiveRecips.add(Friend(document.get("uid") as String,
                                                 document.get("username") as String))
                    }
                }
                prospectiveRecipients.addAll(prospectiveRecips)
                prospectiveRecipsStatus.value = "LOADED"
            }
            .addOnFailureListener {
                prospectiveRecipsStatus.value = it.toString()
            }
    }

    private fun shouldBeAdded(friendDoc:QueryDocumentSnapshot):Boolean  {
        val friendUid = friendDoc.get("uid") as String
        Log.d("friends", "friendUid = $friendUid")
        var index = 0
        var alreadyInList = false
        while (index < recipientList.size && !alreadyInList)   {
            if (recipientList[index].uid == friendUid) {
                alreadyInList = true
            }
            index++
        }

        Log.d("friends", "alreadyInList = $alreadyInList")
        return !alreadyInList
    }

    fun removeFriendInConvo(friendToRemove:Friend) {
        recipientList.remove(friendToRemove)
    }
}