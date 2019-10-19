package com.bm.android.chat.user_access.models

import java.sql.Timestamp

/**************************************
 * Contains data about a friend request
 * received by a user
 */
data class ReceivedFriendRequest(var senderUid:String, var timeRequestSent: Timestamp)