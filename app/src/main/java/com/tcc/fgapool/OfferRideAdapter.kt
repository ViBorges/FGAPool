package com.tcc.fgapool

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.models.Ride

class OfferRideAdapter(private var dataSet: List<Ride>) : Adapter<OfferRideAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val origin: TextView
        val destination: TextView
        val date: TextView
        val time: TextView
        val driverName: TextView
        val seatsAvailable: TextView
        val rideItemMenu: ImageView

        init {
            // Define click listener for the ViewHolder's View.
            origin = view.findViewById(R.id.originText)
            destination = view.findViewById(R.id.destinationText)
            date = view.findViewById(R.id.originDateText)
            time = view.findViewById(R.id.originTimeText)
            driverName = view.findViewById(R.id.driverName)
            seatsAvailable = view.findViewById(R.id.seatsAvailable)
            rideItemMenu = view.findViewById(R.id.rideItemMenu)
            rideItemMenu.setOnClickListener { popupMenu(it) }

            view.setOnClickListener {
                val intent = Intent(view.context, RideDetailActivity::class.java)
                intent.putExtra("listItem", dataSet[absoluteAdapterPosition])
                view.context.startActivity(intent)
            }
        }



        private fun popupMenu(v: View?) {
            val popupMenu = PopupMenu(v?.context, v)
            popupMenu.inflate(R.menu.ride_item_menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.editRide -> {
                        if (v != null) {
                            editRide(v)
                        }
                        true
                    }
                    R.id.deleteRide -> {
                        if (v != null) {
                            deleteRide(v)
                        }
                        true
                    }
                    else -> true
                }
            }
            popupMenu.show()
        }

        private fun editRide(v: View) {
            val intent = Intent(v.context, EditRideActivity::class.java)
            intent.putExtra("listItem", dataSet[absoluteAdapterPosition])
            v.context?.startActivity(intent)
        }

        private fun deleteRide(v: View) {

            val tempRideList = dataSet[absoluteAdapterPosition]
            val database = Firebase.database
            val databaseRef = database.getReference("rides/")
            val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
            val uid = currentFirebaseUser?.uid.toString()

            if (tempRideList.userId == uid) {
                tempRideList.rideKey?.let { databaseRef.child(it) }
                    ?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.ref.removeValue().addOnSuccessListener {
                                toast(v.context.getString(R.string.DB_on_remove_success), v)
                            }.addOnFailureListener {
                                toast(v.context.getString(R.string.not_possible), v)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(ContentValues.TAG, "onCancelled", error.toException())
                        }
                    })
            } else {
                toast(v.context.getString(R.string.not_authorized), v)
            }
        }

        private fun toast(msg: String, v: View) {
            Toast.makeText(
                v.context, msg, Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ride_recyclerview_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.origin.text = dataSet[position].origin
        viewHolder.destination.text = dataSet[position].destination
        viewHolder.date.text = dataSet[position].date
        viewHolder.time.text = dataSet[position].time
        viewHolder.driverName.text = dataSet[position].driverName
        viewHolder.seatsAvailable.text = dataSet[position].seatsAvailable

        if (IsDriver.isDriver == false) {
            viewHolder.rideItemMenu.isVisible = false
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}

