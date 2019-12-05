package com.bm.android.chat.current_friends

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FriendsRepository {
    val db = FirebaseFirestore.getInstance()


    /***********************************************
     * Get chat document which has only the users
     * in ArrayList 'members'
     */
    fun getChat(members:ArrayList<String>):Task<QuerySnapshot>  {
        var query = db.collection("chats") as Query

        for (member in members) {
            query = query.whereEqualTo("members.$member", true)
        }
        query = query.whereEqualTo("memberCount", members.size)
        return query.get()
    }


}