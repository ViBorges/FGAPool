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
import com.tcc.fgapool.IsDriver
import com.tcc.fgapool.databinding.FragmentNotificationsBinding
import com.tcc.fgapool.models.RideRequest
import com.tcc.fgapool.NotificationAdapter
import com.tcc.fgapool.RequestResponseAdapter
import com.tcc.fgapool.models.RequestResponse

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var requestsList: List<RideRequest>
    private lateinit var mListener: ValueEventListener
    private lateinit var userId: String
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var databaseRefResponse: DatabaseReference
    private lateinit var requestResponsesList: List<RequestResponse>


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        userId = currentFirebaseUser?.uid.toString()

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("ride_request/")
        databaseRefResponse = database.getReference("request_response/")

        setRecyclerViewAdapter()

        return root
    }

    private fun setRecyclerViewAdapter(){
        recyclerView = binding.notificationRecyclerView
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        if (IsDriver.isDriver == true){
            getRequests()
        } else {
            getRequestResponses()
        }
    }

    private fun getRequestResponses(){

        requestResponsesList = emptyList()

        mListener = databaseRefResponse.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestResponsesList = emptyList()
                for (ds: DataSnapshot in snapshot.children) {
                    if (userId == ds.child("passengerID").value as String){
                        val requestResponseKey = ds.key as String
                        val rideKey = ds.child("rideKey").value as String
                        val passengerID = ds.child("passengerID").value as String
                        val message = ds.child("message").value as String

                        requestResponsesList = requestResponsesList + listOf(
                            RequestResponse(
                                passengerID,
                                rideKey,
                                message,
                                requestResponseKey
                            )
                        )
                    }
                }
                recyclerView.adapter = RequestResponseAdapter(requestResponsesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }
        })
    }

    private fun getRequests(){

        requestsList = emptyList()

        mListener = databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                requestsList = emptyList()
                for (ds: DataSnapshot in snapshot.children){
                    if (userId == ds.child("driverID").value as String){
                        val requestKey = ds.key as String
                        val rideKey = ds.child("rideKey").value as String
                        val passengerID = ds.child("passengerID").value as String
                        val driverID = ds.child("driverID").value as String
                        val passengerName = ds.child("passengerName").value as String
                        val passengerNumber = ds.child("passengerNumber").value as String

                        requestsList = requestsList + listOf(
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

                recyclerView.adapter = NotificationAdapter(requestsList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }

        })

    }

    private fun removeListener(){
        if(IsDriver.isDriver == true){
            databaseRef.removeEventListener(mListener)
        } else {
            databaseRefResponse.removeEventListener(mListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        removeListener()
    }
}