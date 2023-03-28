package com.tcc.fgapool

import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tcc.fgapool.databinding.ActivityRideDetailBinding
import com.tcc.fgapool.models.Ride
import com.tcc.fgapool.models.RideRequest
import com.tcc.fgapool.utils.CircleTransformation

class RideDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRideDetailBinding
    private lateinit var driverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val mListItem = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra<Ride>("listItem", Ride::class.java)
        } else {
            intent.getParcelableExtra<Ride>("listItem")
        }

        if (mListItem != null) {
            binding.destination.text = mListItem.destination
            binding.date.text = mListItem.date
            binding.textRoute.text = mListItem.route
            binding.origin.text = mListItem.origin
            binding.time.text = mListItem.time
            driverId = mListItem.userId.toString()

        }

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser

        recoverUserData()

        binding.rideRequestButton.setOnClickListener {
            requestRide(mListItem?.rideKey.toString(), currentFirebaseUser?.uid.toString())
        }

    }

    private fun requestRide(ridekey: String, userID: String){
        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("ride_request").push()

        val rideResquest = RideRequest(ridekey, userID)

        databaseRef.setValue(rideResquest)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show()
        }   .addOnFailureListener {
                Toast.makeText(this, R.string.DB_on_send_failure, Toast.LENGTH_SHORT).show()
            }
    }

    private fun recoverUserData() {

        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("signup_info/").child(driverId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.driverName.text = snapshot.child("name").value as String
                binding.driverCourse.text = snapshot.child("course").value as String
                binding.model.text = snapshot.child("carModel").value as String
                binding.color.text = snapshot.child("carColor").value as String
                binding.plate.text = snapshot.child("carPlate").value as String
                val driverPhotoURL = snapshot.child("photoURL").value as String

                setDriverPhoto(driverPhotoURL)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }

        })
    }

    private fun setDriverPhoto(driverPhotoURL: String) {
        Picasso.get().load(driverPhotoURL).transform(CircleTransformation())
            .into(binding.profilePhoto)
    }
}