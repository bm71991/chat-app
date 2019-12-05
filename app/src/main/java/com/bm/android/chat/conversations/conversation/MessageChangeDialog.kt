package com.bm.android.chat.conversations.conversation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.bm.android.chat.ChatActivity
import com.bm.android.chat.R

class MessageChangeDialog:DialogFragment() {
    private val chatViewModel by lazy {
        ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = LayoutInflater.from(activity)
            .inflate(R.layout.message_change_dialog, null)
        val messageEditText = v.findViewById<EditText>(R.id.message_change_edit_text)
        messageEditText.setText(chatViewModel.messageToChangeText)
        chatViewModel.messageToChangeText = ""

        return AlertDialog.Builder(activity)
            .setView(v)
            .setTitle(getString(R.string.change_message))
            .setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                val newMessage = messageEditText.text.toString()
                val messageToChangeId = chatViewModel.messageToChangeId
                chatViewModel.messageToChangeId = ""
                chatViewModel.updateMessage(messageToChangeId, newMessage)
            }
            .setNegativeButton(android.R.string.cancel, {dialog, whichButton -> })
            .create()
    }
}