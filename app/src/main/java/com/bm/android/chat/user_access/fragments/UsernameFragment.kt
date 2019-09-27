package com.bm.android.chat.user_access.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.bm.android.chat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class UsernameFragment : Fragment() {
    private val TAG = "mainLog"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_username, container, false)
        val changeUsernameBtn = v.findViewById<Button>(R.id.change_username_btn)
        val newUsernameEditText = v.findViewById<EditText>(R.id.new_username)

        changeUsernameBtn.setOnClickListener {
            val newName = newUsernameEditText.text.toString()
            changeDisplayName(FirebaseAuth.getInstance().currentUser, newName)
        }
        return v
    }

    private fun changeDisplayName(user: FirebaseUser?, newName:String)  {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "changed display name to $newName")
                }
            }
    }
}