package com.tcc.fgapool

import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tcc.fgapool.databinding.ActivityRideDetailBinding
import com.tcc.fgapool.models.Ride
import com.tcc.fgapool.models.RideRequest
import com.tcc.fgapool.utils.CircleTransformation
import kotlinx.coroutines.delay

class RideDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRideDetailBinding
    private lateinit var driverId: String
    private lateinit var rideKey: String
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userNumber: String
    private lateinit var rideRequestButton: MaterialButton
    private lateinit var buttonProgressBar: ProgressBar
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        rideRequestButton = binding.rideRequestButton
        buttonProgressBar = binding.progressBarButton

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
            rideKey = mListItem.rideKey.toString()

        }

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        userId = currentFirebaseUser?.uid.toString()
        userName = currentFirebaseUser?.displayName.toString()

        getPassengerData()

        recoverUserData()
        checkRequest(userId, rideKey, rideRequestButton)

        showCarInfo(binding.carDetailCard)
    }

    private fun getPassengerData(){
        if (IsDriver.isDriver == false){
            val database = Firebase.database
            val databaseRef = database.getReference("signup_info").child(userId)

            databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    userNumber = snapshot.child("phoneNumber").value as String
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "onCancelled", error.toException())
                }

            })
        }
    }

    private fun showCarInfo(card: CardView) {
        if (IsDriver.isDriver != true) card.visibility = GONE
    }

    private fun checkRequest(userId: String, rideKey: String, requestButton: MaterialButton) {
        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("ride_request/")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds: DataSnapshot in snapshot.children) {
                    if (userId == ds.child("passengerID").value as String
                        && rideKey == ds.child("rideKey").value as String) {

                        counter += 1
                    }
                }
                requestButtonBehavior(requestButton, counter)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }
        })
    }



    private fun deleteRequest() {
        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("ride_request/")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds: DataSnapshot in snapshot.children) {
                    if (userId == ds.child("passengerID").value as String
                        && rideKey == ds.child("rideKey").value as String) {

                        databaseRef.child(ds.key as String).removeValue().addOnSuccessListener {
                                Toast.makeText(
                                    baseContext, "Pedido cancelado com sucesso!", Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }
        })
    }

    private fun requestButtonBehavior(button: MaterialButton, counter: Int) {
        if (driverId == userId) {
            changeButtonStyle(button, "Iniciar", R.color.grey)
            button.setOnClickListener {
                Toast.makeText(this, "Não implementado", Toast.LENGTH_SHORT).show()
            }
        } else if (counter == 0) {
            requestRideButton(button)
        } else {
            cancelRequestButton(button)
        }
    }

    private fun requestRideButton(button: MaterialButton) {
        changeButtonStyle(button, "Solicitar", R.color.theme_color_light)
        button.setOnClickListener {
            requestRide(rideKey, userId, button)
        }
    }

    private fun cancelRequestButton(button: MaterialButton) {
        changeButtonStyle(button, "Cancelar Pedido", R.color.red)
        button.setOnClickListener {
            deleteRequest()
            requestRideButton(button)
        }
    }

    private fun changeButtonStyle(button: MaterialButton, buttonText: String, resource: Int) {
        button.setBackgroundColor(resources.getColor(resource))
        buttonProgressBar.visibility = GONE
        button.visibility = VISIBLE
        button.text = buttonText
    }

    private fun requestRide(rideKey: String, userID: String, button: MaterialButton) {
        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("ride_request").push()

        val rideRequest = RideRequest(rideKey, userID, driverId, userName, userNumber)

        databaseRef.setValue(rideRequest).addOnSuccessListener {
                cancelRequestButton(button)
                Toast.makeText(this, "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
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