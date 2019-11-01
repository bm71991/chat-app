package com.bm.android.chat.conversations.models

/******************************************************
 * Used to store the status and payload resulting from
 * a LiveData change event
 */
class DataLoading<T>(val status:String, val payload:T?)