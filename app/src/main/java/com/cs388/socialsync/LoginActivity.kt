package com.cs388.socialsync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.cs388.socialsync.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.values


class LoginActivity : AppCompatActivity() {

    lateinit var ivGoogleLogin: AppCompatButton

    lateinit var googleSignInClient: GoogleSignInClient
    fun init() {
        ivGoogleLogin = findViewById(R.id.ivGoogleLogin)
        //Get Firebase auth instance
        Obj.auth = FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()

        if (Obj.auth.currentUser != null) {
            startNewActivity()
        }

        ivGoogleLogin.setOnClickListener { loginWithGoogle() }

    }

    fun loginWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        val intent = googleSignInClient.signInIntent
        googleSignInActivityResultLauncher.launch(intent)

    }

    val TAG = "CUSTOM---->"

    private val googleSignInActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult : ${result.data!!.extras}")

                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = accountTask.getResult(ApiException::class.java)
                    Log.d(TAG, "onActivityResult : $account")

                    firebaseAuthWithGoogleAccount(account)
                } catch (e: ApiException) {
                    Log.w(TAG, "onActivityResult : ${e.message}")
                }
            } else {
                Log.w(TAG, "onActivityResult : ${result.data}")
            }
        }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        Obj.auth.signInWithCredential(credential)
            .addOnSuccessListener { authRes ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount : ${authRes.user}")

                startNewActivity(true)
            }
            .addOnFailureListener { err ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount : ${err.message}")
                Toast.makeText(this, "${err.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun startNewActivity(flags: Boolean = false) {
        Obj.createNotificationChannel(this@LoginActivity)

        Obj.USER_DB =
            Firebase.database.getReference("USERS").child(Obj.auth.currentUser!!.uid)

        Obj.USERS_DB = Firebase.database.getReference("USERS")
        Obj.EVENTS_DB = Firebase.database.reference.child("EVENTS")
        Obj.getUserData(object : Obj.UserDataListener {
            override fun onUserDataLoad(user: Obj.User) {
                Log.e("CUSTOM---->", "onUserDataLoad")
                Obj.loggedUserID = Obj.USER_DB.key.toString()
                if (user.image == "null") {
                    Log.e("CUSTOM---->", "onUserDataLoad")
                    val user = Obj.User(
                        Obj.auth.currentUser!!.displayName.toString(),
                        Obj.auth.currentUser!!.email.toString(),
                        Obj.auth.currentUser!!.photoUrl.toString(),
                        user.events

                    )
                    Obj.user = user
                    Obj.uploadUserData(user)
                } else {
                    Obj.user = user
                }

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        })


    }
}