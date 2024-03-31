package com.cs388.socialsync.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs388.socialsync.R
import com.cs388.socialsync.adapter.EventAdapter
import com.cs388.socialsync.model.EventModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var btnSubmit: Button
    lateinit var tvEventName: EditText
    lateinit var tvAddress: EditText
    lateinit var rvMain: RecyclerView
    lateinit var tvLogout: TextView

    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var userReference: DatabaseReference
    lateinit var eventReference: DatabaseReference

    fun init() {
        btnSubmit = findViewById(R.id.btnSubmit)
        tvEventName = findViewById(R.id.tvEventName)
        tvAddress = findViewById(R.id.tvAddress)
        rvMain = findViewById(R.id.rvMain)
        tvLogout = findViewById(R.id.tvLogout)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userReference = database.getReference("USERS").child(auth.currentUser!!.uid)
        eventReference = database.getReference("EVENTS")

        btnSubmit.setOnClickListener {
            uploadData()
        }

        tvLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }



        userReference.child("email").setValue(auth.currentUser!!.email.toString())
        userReference.child("displayName").setValue(auth.currentUser!!.displayName.toString())
        userReference.child("image").setValue(auth.currentUser!!.photoUrl.toString())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()


        adapter = EventAdapter(eventList,object :EventAdapter.OnItemDeleteClickListener{
            override fun onItemDeleteClick(key: String) {
                deleteData(key)
            }
        })

        rvMain.layoutManager = LinearLayoutManager(this)
        rvMain.adapter = adapter

        readData()

    }

    private fun uploadData() {
        val name = tvEventName.text.toString()
        val address = tvAddress.text.toString()

        val list = eventReference.push()
        Log.e("CUSTOM---->", auth.currentUser!!.uid)
        list.setValue(EventModel(name, address, auth.currentUser!!.uid))
    }

    val eventList  = ArrayList<EventModel>();

    lateinit var adapter : EventAdapter


    fun deleteData(key: String) {
        eventReference.child(key).removeValue()
    }
    fun readData() {
        val readEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("CUSTOM----> onChildAdded", snapshot.toString())

                val value = firebaseToJson(snapshot) as JSONObject
                val event = EventModel(
                    value.optString("eventName"),
                    value.optString("address"),
                    value.optString("createdBy",""),
                    snapshot.key.toString(),
                )
                eventList.add(event)
                adapter.notifyDataSetChanged()

                Log.e("CUSTOM----> VALUE", value.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                val key = snapshot.key.toString()
                eventList.forEach {
                    if (it.key == key){
                        val json = firebaseToJson(snapshot) as JSONObject
                        it.eventName = json.optString("eventName")
                        it.address = json.optString("address")
                    }
                }
                adapter.notifyDataSetChanged()

                Log.e("CUSTOM----> onChildChanged", snapshot.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

                val key = snapshot.key.toString() as String

                for (i in 0..eventList.size) {
                    if (eventList[i].key == key){
                        eventList.removeAt(i)
                        break;
                    }
                }

                adapter.notifyDataSetChanged()

                Log.e("CUSTOM----> onChildRemoved", snapshot.toString())
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("CUSTOM----> onChildMoved", snapshot.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CUSTOM----> Error",error.message)
            }
        }

        eventReference.addChildEventListener(readEventListener)
    }


    private fun firebaseToJson(snapshot: DataSnapshot): Any {
        val json = JSONObject()
        val jsonArray = JSONArray()

        if (snapshot.childrenCount == 0L) {
            return snapshot.value!!
        }

        var flag = 0

        for (postSnapshot in snapshot.children) {
            val post = postSnapshot.key!!
            val aa = post.toIntOrNull()
            if (aa != null) {
                flag = 1
                break
            }
            json.put(post, firebaseToJson(postSnapshot))
        }

        if (flag == 1) {
            for (postSnapshot in snapshot.children) {
                if (postSnapshot.hasChildren()) {
                    jsonArray.put(firebaseToJson(postSnapshot))
                } else {
                    val postValue = postSnapshot.value
                    jsonArray.put(postValue)
                }
            }
        }

        if (jsonArray.length() > 0) {
            return jsonArray
        }
        return json
    }
}