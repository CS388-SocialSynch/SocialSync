package com.cs388.socialsync.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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


class LoginActivity : AppCompatActivity() {

    lateinit var ivGoogleLogin: MaterialButton

    lateinit var auth: FirebaseAuth


    lateinit var googleSignInClient: GoogleSignInClient
    fun init() {
        ivGoogleLogin = findViewById(R.id.ivGoogleLogin)

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
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

        auth.signInWithCredential(credential)
            .addOnSuccessListener { authRes ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount : ${authRes.user}")

//                if (authRes.additionalUserInfo!!.isNewUser) {
//                    /** Create Account */
//                    updateUI(true)
//
//                } else {
//                    /** Logged In Account */
//                    updateUI(false)
//                }
                val intent = Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { err ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount : ${err.message}")
                Toast.makeText(this, "${err.message}", Toast.LENGTH_SHORT).show()
            }
    }
}