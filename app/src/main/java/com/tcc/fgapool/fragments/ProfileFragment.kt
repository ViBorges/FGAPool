package com.tcc.fgapool.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tcc.fgapool.GoogleAuthLogin
import com.tcc.fgapool.databinding.FragmentProfileBinding
import com.tcc.fgapool.utils.CircleTransformation

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding: FragmentProfileBinding? = null

    private lateinit var currentFirebaseUser: FirebaseUser

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("577000861442-eok56j6815m6fjek1kufvpmj6bafq0ki.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!
        auth = Firebase.auth

        binding.btnLogout2.setOnClickListener {
            signOut()
        }

        currentFirebaseUser = FirebaseAuth.getInstance().currentUser!!

        setProfilePhoto(currentFirebaseUser.photoUrl.toString())
        getUserData()

        return root
    }

    private fun getUserData(){
        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("signup_info/").child(currentFirebaseUser.uid)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val course = snapshot.child("course").value as String
                val isDriver = snapshot.child("isDriver").value as Boolean

                setUserData(course, isDriver)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }

        })
    }

    private fun setUserData(course: String, isDriver: Boolean){
        binding.profileName.text = currentFirebaseUser.displayName
        binding.profileCourse.text = course
        if (isDriver){
            binding.role.text = "Motorista"
        } else {
            binding.role.text = "Passageiro"
        }
    }

    private fun setProfilePhoto(driverPhotoURL: String) {
        Picasso.get().load(driverPhotoURL).transform(CircleTransformation())
            .into(binding.profilePhoto)
    }

    private fun signOut(){
        auth.signOut()
        activity?.let {
            googleSignInClient.signOut().addOnCompleteListener(it, OnCompleteListener {
                Toast.makeText(context, "Deslogado", Toast.LENGTH_SHORT).show()
                updateUI()
            })
        }
    }

    private fun updateUI() {
        val intent = Intent(context, GoogleAuthLogin::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}