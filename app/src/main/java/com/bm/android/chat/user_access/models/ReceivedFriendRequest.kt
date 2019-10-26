package com.bm.android.chat.user_access.models

import java.util.Date
import com.google.firebase.Timestamp

/**************************************
 * Contains data about a friend request
 * received by a user
 */
data class ReceivedFriendRequest(var senderUid:String = "",
                                 var senderUsername:String = "",
                                 var timeRequestSent: Timestamp = Timestamp(Date()) )