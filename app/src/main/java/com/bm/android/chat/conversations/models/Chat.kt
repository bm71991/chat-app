package com.bm.android.chat.conversations.models

/*****************************************************
 * subcollection chatThread is added programmatically
 * after first adding the Chat document to Firestore.
 * memberCount is required since Firestore does not
 * have querying capabilities such as Hashmap.length
 * at the moment.
 */
data class Chat(var members:HashMap<String, Boolean> = HashMap(),
                var memberCount:Int = 0,
                var lastMessage:LastMessage = LastMessage())
