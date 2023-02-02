package com.tcc.fgapool.ui.rides

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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


class RidesFragment : Fragment() {

    private lateinit var ridesViewModel: RidesViewModel
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
        ridesViewModel = ViewModelProvider(this)[RidesViewModel::class.java]

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
        searchRide.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredList(newText)
                return true
            }
        })

        if (IsDriver.isDriver == false) binding.fab.isVisible = false

        binding.fab.setOnClickListener {
            updateUI()
        }

        return root
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
                    if (IsDriver.isDriver == true) {
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
                                    driverName
                                )
                            )
                        }
                    } else {
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
                                driverName
                            )
                        )
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

    private fun updateUI() {
        val intent = Intent(context, OfferRide::class.java)
        startActivity(intent)
    }
}