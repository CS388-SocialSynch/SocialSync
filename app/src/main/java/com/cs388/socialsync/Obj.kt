package com.cs388.socialsync

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object Obj {

    lateinit var USER_DB: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var user: Obj.User

    fun uploadUserData(user: User) {
        USER_DB.child("displayName").setValue(user.displayName)
        USER_DB.child("email").setValue(user.email)
        USER_DB.child("image").setValue(user.image)


    }

    fun getUserData(listener: UserDataListener) {

        val aaa = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener.onUserDataLoad(
                    User(
                        dataSnapshot.child("displayName").value.toString(),
                        dataSnapshot.child("email").value.toString(),
                        dataSnapshot.child("image").value.toString(),
                    )
                )
                USER_DB.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        USER_DB.addValueEventListener(aaa)
    }

    interface UserDataListener {
        fun onUserDataLoad(user: User)
    }

    class User(var displayName: String, var email: String, var image: String)
}