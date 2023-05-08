package com.tcc.fgapool

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.models.RideRequest

class NotificationAdapter(private val dataSet: List<RideRequest>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val messageText: TextView
        private val rideInfo: ImageView
        private val wppButton: ImageView
        private val telegramButton: ImageView
        private val acceptButton: ImageView
        private val rejectButton: ImageView
        lateinit var rideKey: String
        lateinit var passengerID: String
        lateinit var driverID: String
        lateinit var passengerName: String
        lateinit var passengerNumber: String
        lateinit var requestKey: String

        init {
            // Define click listener for the ViewHolder's View.
            messageText = view.findViewById(R.id.msg_text)
            rideInfo = view.findViewById(R.id.ride_info)
            wppButton = view.findViewById(R.id.wpp_btn)
            telegramButton = view.findViewById(R.id.telegram_btn)
            acceptButton = view.findViewById(R.id.accept_btn)
            rejectButton = view.findViewById(R.id.reject_btn)

            wppButton.setOnClickListener {
                val urlIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/+55$passengerNumber?text=Posso confirmar sua carona?"))
                view.context.startActivity(urlIntent)
            }

            telegramButton.setOnClickListener {
                val urlIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/+55$passengerNumber"))
                view.context.startActivity(urlIntent)
            }

            acceptButton.setOnClickListener {
                acceptRequest(view)
            }

            rejectButton.setOnClickListener {
                deleteRequest()
            }
        }

        private fun deleteRequest(){
            val database = Firebase.database
            val databaseRef = database.getReference("ride_request/")
            databaseRef.child(requestKey).removeValue()
        }

        private fun acceptRequest(v: View){
            val database = Firebase.database
            val databaseRef = database.getReference("rides/").child(rideKey)
            databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in 1..4){
                        if (!snapshot.child("passenger$i").exists()){
                            databaseRef.child("passenger$i").setValue(passengerID)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        v.context, "Solicitação aceita.", Toast.LENGTH_SHORT
                                    ).show()
                                    val seats = snapshot.child("seatsAvailable").value as String
                                    val subtractSeats = (seats.toInt() - 1).toString()
                                    databaseRef.child("seatsAvailable").setValue(subtractSeats)
                                    deleteRequest()
                                }
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.rider_notification_recyclerview_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.textView.text = dataSet[position].toString()
        viewHolder.messageText.text = dataSet[position].passengerName+"\nSolicitou uma carona"
        viewHolder.passengerID = dataSet[position].passengerID.toString()
        viewHolder.rideKey = dataSet[position].rideKey.toString()
        viewHolder.driverID = dataSet[position].driverID.toString()
        viewHolder.passengerName = dataSet[position].passengerName.toString()
        viewHolder.passengerNumber = dataSet[position].passengerNumber.toString()
        viewHolder.requestKey = dataSet[position].requestKey.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}