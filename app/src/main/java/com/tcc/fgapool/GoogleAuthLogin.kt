package com.tcc.fgapool

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.properties.Delegates

class GoogleAuthLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_auth_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("577000861442-eok56j6815m6fjek1kufvpmj6bafq0ki.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        val loginButton : Button = findViewById(R.id.btn_login)
        loginButton.setOnClickListener {
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        hasRegistered(currentUser)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    //updateUI(user)
                    hasRegistered(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null){
            val intent = Intent(this, BottomNavigation::class.java)
            startActivity(intent)
            finish()
            }
    }

    private fun hasRegistered(user: FirebaseUser?){
        val currentUser = auth.currentUser
        var intent: Intent
        var isRegistrationCompleted: Boolean?
        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("signup_info/" + currentUser?.uid)

        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                isRegistrationCompleted = snapshot.child("registrationComplete").value as Boolean?

                if (user!=null) {
                    if (isRegistrationCompleted == true){
                        intent = Intent(baseContext, BottomNavigation::class.java)
                        startActivity(intent)
                        finish()
                    } else{
                        intent = Intent(baseContext, CompleteRegistration::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Não foi possível recuperar o dados, cheque sua internet e tente novamente.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}