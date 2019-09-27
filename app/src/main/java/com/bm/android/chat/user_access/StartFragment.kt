package com.bm.android.chat.user_access

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.bm.android.chat.R
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_start.*

/*******************
 * This Fragment is used as a placeholder entry point to the app proper, will be
 * replaced with the Chat fragment later on.
 *******************/
class StartFragment : Fragment() {
    private val TAG = "mainLog"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_start, container, false)
        val logoutButton = v.findViewById<Button>(R.id.logout_button)
        val deleteButton = v.findViewById<Button>(R.id.delete_button)

            logoutButton.setOnClickListener {
                logOutUser()
        }

        deleteButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User account deleted.")
                        logOutUser()
                        Log.d(TAG, "Logged out user.")
                    } else {
                        Log.d(TAG, "User account not deleted.")
                    }
                }
        }

        return v
    }

    private fun logOutUser()    {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
    }
}