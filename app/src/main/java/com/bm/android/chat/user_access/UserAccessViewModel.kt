package com.bm.android.chat.user_access

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserAccessViewModel: ViewModel() {
    private var nameRegisterStatus = MutableLiveData<String>()
    private val mAccessRepository = UserAccessRepository()
    private val mAuth = FirebaseAuth.getInstance()
    private val TAG = "mainLog"
    val USERNAME_AVAILABLE = "username is not in firestore"
    val USERNAME_REGISTERED = "username has been registered"

    fun getNameRegisterStatus(): LiveData<String> = nameRegisterStatus

    fun checkFirestore(username:String, email:String)  {
        mAccessRepository.checkFirestore(username, email).addOnSuccessListener {
            Log.d(TAG, USERNAME_AVAILABLE)
            setUsernameAsDisplayName(username, mAuth.currentUser!!)
        }
            .addOnFailureListener   {
                nameRegisterStatus.value = it.message.toString()
            }
    }

    private fun setUsernameAsDisplayName(username:String, currentUser: FirebaseUser)    {
        mAccessRepository.setUsernameAsDisplayName(username, currentUser)
            .addOnSuccessListener {
                Log.d(TAG, USERNAME_REGISTERED)
                nameRegisterStatus.value = USERNAME_REGISTERED
        }
            .addOnFailureListener   {
                nameRegisterStatus.value = it.message.toString()
            }
    }

}