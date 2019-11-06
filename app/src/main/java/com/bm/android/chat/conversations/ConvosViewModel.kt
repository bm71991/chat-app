package com.bm.android.chat.conversations


import androidx.lifecycle.ViewModel
import com.bm.android.chat.conversations.ConvoRepository
import com.google.firebase.firestore.Query

class ConvosViewModel:ViewModel() {
    private val convoRepository = ConvoRepository()

    fun getChats():Query    {
        return convoRepository.getChats()
    }
}