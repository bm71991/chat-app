package com.bm.android.chat.user_access.models

import com.google.firebase.firestore.Exclude

data class FirebaseUserInfo(var email:String = "", var uid:String = "", @Exclude var id:String = "")