package com.tcc.fgapool.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.BottomNavigation
import com.tcc.fgapool.GoogleAuthLogin
import com.tcc.fgapool.R
import com.tcc.fgapool.databinding.FragmentProfileBinding

//import com.tcc.fgapool.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("577000861442-eok56j6815m6fjek1kufvpmj6bafq0ki.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!
        auth = Firebase.auth

        binding.btnLogout2.setOnClickListener {
            signOut()
        }

        return root
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