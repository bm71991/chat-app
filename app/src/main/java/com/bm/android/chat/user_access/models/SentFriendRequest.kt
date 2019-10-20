package com.bm.android.chat.user_access.models

import com.google.firebase.Timestamp
import java.util.*


/*************************************
 * Contains data about a friend request
 * initiated by a user
 */
data class SentFriendRequest(var recipientUid:String = "",
                             var timeRequestSent: Timestamp = Timestamp(Date()) )