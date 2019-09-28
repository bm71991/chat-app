package com.bm.android.chat.user_access

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserAccessViewModel: ViewModel() {
    private var nameRegisterStatus = MutableLiveData<String>()
    private var emailSignupStatus =  MutableLiveData<String>()
    private var emailLoginStatus = MutableLiveData<String>()

    private val mAccessRepository = UserAccessRepository()
    private val mAuth = FirebaseAuth.getInstance()
    private val TAG = "mainLog"
    val USERNAME_AVAILABLE = "username is not in firestore"
    val USERNAME_REGISTERED = "username has been registered"
    val EMAIL_REGISTERED = "email has been registered in firebase"
    val EMAIL_LOGIN_SUCCESS = "login was successful"

    fun getNameRegisterStatus(): LiveData<String> = nameRegisterStatus
    fun getEmailSignupStatus(): LiveData<String> = emailSignupStatus
    fun getEmailLoginStatus(): LiveData<String> = emailLoginStatus

    fun clearEmailLoginStatus() {
        emailLoginStatus = MutableLiveData()
    }

    fun clearEmailSignupStatus()    {
        emailSignupStatus = MutableLiveData()
    }

    fun clearNameRegisterStatus()   {
        nameRegisterStatus = MutableLiveData()
    }


    fun accountLacksUsername():Boolean  {
        return mAuth.currentUser!!.displayName.isNullOrEmpty()
    }

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

    fun signupUser(email:String, password:String)  {
        mAccessRepository.registerFirebaseUser(email, password, mAuth)
            .addOnSuccessListener {
                Log.i(TAG, "signupUser successful for email:${mAuth.currentUser}")
                mAuth.currentUser?.sendEmailVerification()
                mAuth.signOut()
                emailSignupStatus.value = EMAIL_REGISTERED
            }
            .addOnFailureListener {
                Log.i(TAG, "signupUser failed Listener current user = ${mAuth.currentUser}")
                emailSignupStatus.value = it.message.toString()
            }
    }

    fun loginWithEmail(email:String, password:String)   {
        mAccessRepository.signInWithEmail(email, password, mAuth)
            .addOnSuccessListener {
                if (mAuth.currentUser!!.isEmailVerified)    {
                    emailLoginStatus.value = EMAIL_LOGIN_SUCCESS
                } else {
                    mAuth.signOut()
                    emailLoginStatus.value = "Login denied. Check your email to verify the account"
                }
            }
            .addOnFailureListener {
                emailLoginStatus.value = it.message.toString()
            }
    }
}