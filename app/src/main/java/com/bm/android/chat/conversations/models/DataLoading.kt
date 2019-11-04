package com.bm.android.chat.conversations.models

/******************************************************
 * Used to store the status and payload resulting from
 * a LiveData change event
 */
class DataLoading<T>(var status:String = "", var payload:T? = null)