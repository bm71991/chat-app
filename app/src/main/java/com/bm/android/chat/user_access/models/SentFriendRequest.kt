package com.bm.android.chat.user_access.models

import java.sql.Timestamp


/*************************************
 * Contains data about a friend request
 * initiated by a user
 */
data class SentFriendRequest(var recipientUid:String, var timeRequestSent:Timestamp)