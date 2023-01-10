package com.tcc.fgapool.ui.rides

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.OfferRide
import com.tcc.fgapool.OfferRideAdapter
import com.tcc.fgapool.databinding.FragmentRidesBinding
import com.tcc.fgapool.models.Ride
import java.text.FieldPosition


class RidesFragment : Fragment() {

    private lateinit var ridesViewModel: RidesViewModel
    private var _binding: FragmentRidesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    private lateinit var origin: String
    private lateinit var destination: String
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var driverName: String
    private lateinit var seatsAvailable: String

    private lateinit var mListener: ValueEventListener

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ridesViewModel =
            ViewModelProvider(this)[RidesViewModel::class.java]

        _binding = FragmentRidesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("rides/")

        //Set RecyclerView
        recyclerView = binding.rideRecyclerview
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recoverRideData()


        binding.fab.setOnClickListener {
            updateUI()
        }

        return root
    }


    private fun recoverRideData(){

        var rideList: List<Ride> = emptyList()

        mListener = databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds: DataSnapshot in snapshot.children){
                    origin = ds.child("origin").value as String
                    destination = ds.child("destination").value as String
                    date = ds.child("date").value as String
                    time = ds.child("time").value as String
                    seatsAvailable = ds.child("seatsAvailable").value as String
                    driverName = ds.child("driverName").value as String

                    rideList = rideList + listOf(
                        Ride(origin, destination, null, date, time, seatsAvailable,
                            null, null, null, driverName)
                    )

                }

                binding.nothingToShow.isVisible = rideList.isEmpty()
                recyclerView.adapter = OfferRideAdapter(rideList.reversed(),
                    object: OfferRideAdapter.OptionsMenuClickListener{
                    override fun onOptionsMenuClicked(position: Int) {
                        performOptionsMenuClick(position)
                    }

                })
                rideList = emptyList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun performOptionsMenuClick(position: Int){

        val popupMenu = PopupMenu(context , binding.rideRecyclerview[position].findViewById(com.tcc.fgapool.R.id.rideItemMenu))

        popupMenu.inflate(com.tcc.fgapool.R.menu.ride_item_menu)

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item?.itemId){
                    com.tcc.fgapool.R.id.editRide -> {
                        // here are the logic to delete an item from the list
                        Toast.makeText(context , "edit clicked" , Toast.LENGTH_SHORT).show()
                        return true
                    }
                    // in the same way you can implement others
                    com.tcc.fgapool.R.id.deleteRide -> {
                        // define
                        Toast.makeText(context , "delete clicked" , Toast.LENGTH_SHORT).show()
                        return true
                    }

                }
                return false
            }
        })
        popupMenu.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        databaseRef.removeEventListener(mListener)
    }

    private fun updateUI(){
        val intent = Intent(context, OfferRide::class.java)
        startActivity(intent)
    }
}