package com.bm.android.chat.user_access

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Transaction

class UserAccessRepository {
    private val db = FirebaseFirestore.getInstance()
    private val USER_COLLECTION = "users"
    private val TAG = "mainLog"

    /*********************
     * User Signup Methods
     ********************/
    fun registerFirebaseUser(email:String, password:String, auth:FirebaseAuth):Task<AuthResult>   {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    /*Checks to determine whether a document already exists with the username requested by a
    prospective user. If it does not, a document will be created whose key is the new
    username. These steps are done atomically due to being run in a transaction.
     */
    fun checkFirestore(username:String, email:String): Task<Transaction> {
        return db.runTransaction { transaction ->
            val usernameRef = db.collection("users").document(username)
            val snapshot = transaction.get(usernameRef)

            if (!snapshot.exists()) {
                Log.i(TAG, "snapshot does not exist")
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                transaction.set(usernameRef, User(uid, email))
            } else {
                Log.i(TAG, "snapshot exists")
                throw FirebaseFirestoreException(
                    "Username requested is already taken, please choose another.",
                    FirebaseFirestoreException.Code.ABORTED)
            }
        }
    }

    fun setUsernameAsDisplayName(username:String, currentUser: FirebaseUser): Task<Void>   {
        val usernameUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(username).build()
        return currentUser.updateProfile(usernameUpdate)
    }

    /* Used in checkFirestore to add a user document to collection users */
    class User(var uid:String?, var email:String)
}