package com.bm.android.chat.user_access.models

/*******************************************************
 * Upon creating an account, a document with the structure
 * of this class will be added to the 'friends' collection
 * in Firestore. Its id will be the uid of the newly created
 * user.
 */
data class FriendInfo(var currentFriends:ArrayList<String> = arrayListOf(),
                      var receivedRequests:ArrayList<ReceivedFriendRequest> = arrayListOf(),
                      var sentRequests:ArrayList<SentFriendRequest> = arrayListOf())