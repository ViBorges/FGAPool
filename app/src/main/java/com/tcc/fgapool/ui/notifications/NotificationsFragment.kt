package com.tcc.fgapool.ui.notifications

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.databinding.FragmentNotificationsBinding
import com.tcc.fgapool.models.RideRequest
import com.tcc.fgapool.NotificationAdapter

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var requetsList: List<RideRequest>
    private lateinit var mListener: ValueEventListener
    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        userId = currentFirebaseUser?.uid.toString()

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("ride_request/")

        //Set RecyclerView
        recyclerView = binding.notificationRecyclerView
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        getRequests()


        return root
    }

    private fun getRequests(){

        requetsList = emptyList()

        mListener = databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                requetsList = emptyList()
                for (ds: DataSnapshot in snapshot.children){
                    if (userId == ds.child("driverID").value as String){
                        val requestKey = ds.key as String
                        val rideKey = ds.child("rideKey").value as String
                        val passengerID = ds.child("passengerID").value as String
                        val driverID = ds.child("driverID").value as String
                        val passengerName = ds.child("passengerName").value as String
                        val passengerNumber = ds.child("passengerNumber").value as String

                        requetsList = requetsList + listOf(
                            RideRequest(
                                rideKey,
                                passengerID,
                                driverID,
                                passengerName,
                                passengerNumber,
                                requestKey
                            )
                        )
                    }
                }

                recyclerView.adapter = NotificationAdapter(requetsList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }

        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        databaseRef.removeEventListener(mListener)
    }
}