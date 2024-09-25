package com.tcc.fgapool.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.IsDriver
import com.tcc.fgapool.OfferRide
import com.tcc.fgapool.OfferRideAdapter
import com.tcc.fgapool.databinding.FragmentRidesBinding
import com.tcc.fgapool.models.Ride


class RidesFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private var _binding: FragmentRidesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String

    private lateinit var origin: String
    private lateinit var destination: String
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var driverName: String
    private lateinit var seatsAvailable: String
    private lateinit var searchRide: androidx.appcompat.widget.SearchView

    private lateinit var rideList: List<Ride>
    private lateinit var mListener: ValueEventListener
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRidesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        uid = currentFirebaseUser?.uid.toString()

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("rides/")

        //Set RecyclerView
        recyclerView = binding.rideRecyclerview
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recoverRideData()

        searchRide = binding.searchRides
        searchRide.clearFocus()

        if (IsDriver.isDriver == false) binding.fab.isVisible = false

        binding.fab.setOnClickListener {
            updateUI()
        }

        showMyRidesButton()
        binding.myRidesBtn.setOnCheckedChangeListener(this)

        return root
    }

    private fun showMyRidesButton() {
        if (IsDriver.isDriver == false) {
            binding.myRidesBtn.visibility = VISIBLE
            binding.divider.visibility = GONE
        }
    }

    private fun filteredList(newText: String?) {
        var filteredList: List<Ride> = emptyList()
        for (ride: Ride in rideList) {
            if (newText != null) {
                if (ride.destination?.lowercase()?.contains(newText.lowercase()) == true) {
                    filteredList = filteredList + listOf(ride)
                }
            }
        }

        binding.notFound.isVisible = filteredList.isEmpty()
        setAdapter(filteredList)

    }

    private fun setAdapter(list: List<Ride>) {
        recyclerView.adapter = OfferRideAdapter(list.reversed())
    }

    private fun recoverRideData() {

        rideList = emptyList()

        mListener = databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rideList = emptyList()
                for (ds: DataSnapshot in snapshot.children) {
                    val userId = ds.child("userId").value as String
                    val pass1 = ds.child("passenger1").value.toString()
                    val pass2 = ds.child("passenger2").value.toString()
                    val pass3 = ds.child("passenger3").value.toString()
                    val pass4 = ds.child("passenger4").value.toString()
                    val rideState = ds.child("status").value.toString()
                    val seats = ds.child("seatsAvailable").value.toString()
                    if (IsDriver.isDriver == true && rideState != "finished") {
                        if (userId == uid) {
                            val rideKey = ds.key as String
                            val userKey = ds.child("userId").value as String
                            val route = ds.child("route").value as String
                            val sameSex = ds.child("sameSexPassengers").value as Boolean
                            val isActive = ds.child("active").value as Boolean
                            origin = ds.child("origin").value as String
                            destination = ds.child("destination").value as String
                            date = ds.child("date").value as String
                            time = ds.child("time").value as String
                            seatsAvailable = ds.child("seatsAvailable").value as String
                            driverName = ds.child("driverName").value as String
                            val passenger1 = ds.child("passenger1").value as String?
                            val passenger2 = ds.child("passenger2").value as String?
                            val passenger3 = ds.child("passenger3").value as String?
                            val passenger4 = ds.child("passenger4").value as String?
                            val status = ds.child("status").value as String?

                            rideList = rideList + listOf(
                                Ride(
                                    rideKey,
                                    origin,
                                    destination,
                                    route,
                                    date,
                                    time,
                                    seatsAvailable,
                                    sameSex,
                                    isActive,
                                    userKey,
                                    driverName,
                                    passenger1,
                                    passenger2,
                                    passenger3,
                                    passenger4,
                                    status
                                )
                            )
                        }
                    } else if (!binding.myRidesBtn.isChecked) {
                        if (seats != "0") {
                            if (rideState != "finished") {
                                val rideKey = ds.key as String
                                val userKey = ds.child("userId").value as String
                                val route = ds.child("route").value as String
                                val sameSex = ds.child("sameSexPassengers").value as Boolean
                                val isActive = ds.child("active").value as Boolean
                                origin = ds.child("origin").value as String
                                destination = ds.child("destination").value as String
                                date = ds.child("date").value as String
                                time = ds.child("time").value as String
                                seatsAvailable = ds.child("seatsAvailable").value as String
                                driverName = ds.child("driverName").value as String
                                val passenger1 = ds.child("passenger1").value as String?
                                val passenger2 = ds.child("passenger2").value as String?
                                val passenger3 = ds.child("passenger3").value as String?
                                val passenger4 = ds.child("passenger4").value as String?
                                val status = ds.child("status").value as String?

                                rideList = rideList + listOf(
                                    Ride(
                                        rideKey,
                                        origin,
                                        destination,
                                        route,
                                        date,
                                        time,
                                        seatsAvailable,
                                        sameSex,
                                        isActive,
                                        userKey,
                                        driverName,
                                        passenger1,
                                        passenger2,
                                        passenger3,
                                        passenger4,
                                        status
                                    )
                                )
                            }
                        }
                    } else {
                        if (uid == pass1 || uid == pass2 || uid == pass3 || uid == pass4) {
                            if (rideState != "finished") {
                                val rideKey = ds.key as String
                                val userKey = ds.child("userId").value as String
                                val route = ds.child("route").value as String
                                val sameSex = ds.child("sameSexPassengers").value as Boolean
                                val isActive = ds.child("active").value as Boolean
                                origin = ds.child("origin").value as String
                                destination = ds.child("destination").value as String
                                date = ds.child("date").value as String
                                time = ds.child("time").value as String
                                seatsAvailable = ds.child("seatsAvailable").value as String
                                driverName = ds.child("driverName").value as String
                                val passenger1 = ds.child("passenger1").value as String?
                                val passenger2 = ds.child("passenger2").value as String?
                                val passenger3 = ds.child("passenger3").value as String?
                                val passenger4 = ds.child("passenger4").value as String?
                                val status = ds.child("status").value as String?

                                rideList = rideList + listOf(
                                    Ride(
                                        rideKey,
                                        origin,
                                        destination,
                                        route,
                                        date,
                                        time,
                                        seatsAvailable,
                                        sameSex,
                                        isActive,
                                        userKey,
                                        driverName,
                                        passenger1,
                                        passenger2,
                                        passenger3,
                                        passenger4,
                                        status
                                    )
                                )
                            }
                        }
                    }

                }

                binding.nothingToShow.isVisible = rideList.isEmpty()
                recyclerView.adapter = OfferRideAdapter(rideList.reversed())
                binding.progressBarRecyclerView.isVisible = false
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled", error.toException())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        databaseRef.removeEventListener(mListener)
    }

    override fun onResume() {
        super.onResume()
        searchRide.setQuery("", false) // clear the text
        //searchRide.isIconified = true;

        searchRide.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (rideList.isNotEmpty())
                    filteredList(newText)
                return true
            }
        })
    }

    private fun updateUI() {
        val intent = Intent(context, OfferRide::class.java)
        startActivity(intent)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked)
            recoverRideData()
        else
            recoverRideData()
    }
}