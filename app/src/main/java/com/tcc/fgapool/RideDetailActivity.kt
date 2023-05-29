package com.tcc.fgapool

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import java.util.Vector

class RideDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRideDetailBinding
    private lateinit var driverId: String
    private lateinit var rideKey: String
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userNumber: String
    private lateinit var rideRequestButton: MaterialButton
    private lateinit var buttonProgressBar: ProgressBar
    private lateinit var passenger1: String
    private lateinit var passenger2: String
    private lateinit var passenger3: String
    private lateinit var passenger4: String
    private var isPassenger: Boolean = false
    private var counter = 0
    private lateinit var driverPhoneNumber: String
    private val passengersPicUrl = Vector<String>()
    private val passengersName = Vector<String>()
    private val passengers = Vector<String>()

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
            passenger1 = mListItem.passenger1.toString()
            passenger2 = mListItem.passenger2.toString()
            passenger3 = mListItem.passenger3.toString()
            passenger4 = mListItem.passenger4.toString()
        }

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        userId = currentFirebaseUser?.uid.toString()
        userName = currentFirebaseUser?.displayName.toString()

        getPassengerPhoneNumber()

        recoverUserData()
        checkIfPassenger(rideRequestButton)

        showCarInfo(binding.carDetailCard)
        showWppButton()
        getDriverPhoneNumber()
        handleWppBtnClick()
        getPassengerDetail()
    }

    private fun getPassengerPhoneNumber() {
        if (IsDriver.isDriver == false) {
            val database = Firebase.database
            val databaseRef = database.getReference("signup_info").child(userId)

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
        if (IsDriver.isDriver != true && !isPassenger) card.visibility = GONE
    }

    private fun showWppButton() {
        if (isPassenger) binding.wppBtn.visibility = VISIBLE
    }

    private fun handleWppBtnClick() {
        binding.wppBtn.setOnClickListener {
            val urlIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse("https://wa.me/+55$driverPhoneNumber?")
            )
            startActivity(urlIntent)
        }
    }

    private fun getDriverPhoneNumber() {
        val database = Firebase.database
        val databaseRef = database.getReference("signup_info").child(driverId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                driverPhoneNumber = snapshot.child("phoneNumber").value as String
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }

        })
    }

    private fun setPassengerDetail() {
        var counter = 1
        if (passenger1 != "null" || passenger2 != "null" || passenger3 != "null" || passenger4 != "null") {
            binding.passengersDisclaimer.visibility = GONE
            for (url in passengersPicUrl) {
                when (counter) {
                    1 -> setPassengerInfo(
                        url,
                        binding.person1Image,
                        binding.person1Name,
                        transformName(passengersName.elementAt(0))
                    )
                    2 -> setPassengerInfo(
                        url,
                        binding.person2Image,
                        binding.person2Name,
                        transformName(passengersName.elementAt(1))
                    )
                    3 -> setPassengerInfo(
                        url,
                        binding.person3Image,
                        binding.person3Name,
                        transformName(passengersName.elementAt(2))
                    )
                    4 -> setPassengerInfo(
                        url,
                        binding.person4Image,
                        binding.person4Name,
                        transformName(passengersName.elementAt(3))
                    )
                }
                counter++
            }
        } else {
            binding.passengersDisclaimer.visibility = VISIBLE
        }
    }

    private fun setPassengerInfo(
        driverPhotoURL: String, imageView: ImageView, textView: TextView, name: String
    ) {
        imageView.visibility = VISIBLE
        textView.visibility = VISIBLE
        textView.text = name
        Picasso.get().load(driverPhotoURL).transform(CircleTransformation()).into(imageView)
    }

    private fun getPassengerDetail() {
        val database = Firebase.database
        passengers.removeAllElements()
        passengersPicUrl.removeAllElements()
        passengersName.removeAllElements()
        passengers.addAll(listOf(passenger1, passenger2, passenger3, passenger4))

        for (passenger in passengers) {
            if (passenger != "null") {
                val databaseRef = database.getReference("signup_info").child(passenger)
                databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        passengersPicUrl.add(snapshot.child("photoURL").value as String)
                        passengersName.add(snapshot.child("name").value as String)

                        setPassengerDetail()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(ContentValues.TAG, "onCancelled", error.toException())
                    }

                })
            } else {
                binding.passengersDisclaimer.visibility = VISIBLE
            }
        }
    }

    private fun transformName(name: String): String {
        return name.replace(" ", "\n")
    }

    private fun leaveRide() {
        when (userId) {
            passenger1 -> deletePassenger(1)
            passenger2 -> deletePassenger(2)
            passenger3 -> deletePassenger(3)
            passenger4 -> deletePassenger(4)
        }
    }

    private fun deletePassenger(passenger: Int) {
        val database = Firebase.database
        val databaseRef = database.getReference("rides/").child(rideKey)

        databaseRef.child("passenger$passenger").setValue("null").addOnSuccessListener {
            updateSeats()
            requestRideButton(rideRequestButton)
            binding.carDetailCard.visibility = GONE
            binding.wppBtn.visibility = GONE
            hideAllPassengerInfo()
            getPassengers()
        }
    }

    private fun hideAllPassengerInfo() {
        binding.person1Image.visibility = GONE
        binding.person2Image.visibility = GONE
        binding.person3Image.visibility = GONE
        binding.person4Image.visibility = GONE
        binding.person1Name.visibility = GONE
        binding.person2Name.visibility = GONE
        binding.person3Name.visibility = GONE
        binding.person4Name.visibility = GONE
    }

    private fun getPassengers() {
        val database = Firebase.database
        val databaseRef = database.getReference("rides/").child(rideKey)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                passenger1 = snapshot.child("passenger1").value.toString()
                passenger2 = snapshot.child("passenger2").value.toString()
                passenger3 = snapshot.child("passenger3").value.toString()
                passenger4 = snapshot.child("passenger4").value.toString()

                getPassengerDetail()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }

        })
    }

    private fun updateSeats() {
        val database = Firebase.database
        val databaseRef = database.getReference("rides/").child(rideKey)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val seats = snapshot.child("seatsAvailable").value as String
                val sumSeats = (seats.toInt() + 1).toString()
                databaseRef.child("seatsAvailable").setValue(sumSeats)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }

        })
    }

    private fun checkIfPassenger(button: MaterialButton) {
        if (userId == passenger1 || userId == passenger2 || userId == passenger3 || userId == passenger4) run {
            changeButtonStyle(button, "Sair da carona", R.color.red)
            isPassenger = true
            button.setOnClickListener {
                leaveRide()
            }
        } else {
            checkRequest(userId, rideKey, rideRequestButton)
        }
    }

    private fun checkRequest(userId: String, rideKey: String, requestButton: MaterialButton) {
        val database: FirebaseDatabase = Firebase.database
        val databaseRef: DatabaseReference = database.getReference("ride_request/")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds: DataSnapshot in snapshot.children) {
                    if (userId == ds.child("passengerID").value as String && rideKey == ds.child("rideKey").value as String) {

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
                    if (userId == ds.child("passengerID").value as String && rideKey == ds.child("rideKey").value as String) {

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
            if (passenger1 == "null" && passenger2 == "null" && passenger3 == "null" && passenger4 == "null"){
                changeButtonStyle(button, "Iniciar", R.color.grey)
                button.setOnClickListener {
                    Toast.makeText(this, "Não há passageiros para iniciar a corrida!", Toast.LENGTH_SHORT).show()
                }
            } else {
                changeButtonStyle(button, "Iniciar", R.color.theme_color_light)
                button.setOnClickListener {
                    Toast.makeText(this, "Corrida iniciada", Toast.LENGTH_SHORT).show()
                }
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