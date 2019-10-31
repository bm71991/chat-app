package com.bm.android.chat.conversations

import com.bm.android.chat.DbConstants
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class ConvoRepository {
    private val db = FirebaseFirestore.getInstance()
    //getAllFriends

    fun getAllCurrentFriends(uid:String):Task<QuerySnapshot>    {
        return db.collection(DbConstants.FRIENDS_COLLECTION)
            .document(uid)
            .collection(DbConstants.CURRENT_FRIENDS)
            .get()
    }
}