package com.tcc.fgapool

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.models.RequestResponse

class RequestResponseAdapter(private val dataSet: List<RequestResponse>) :
    RecyclerView.Adapter<RequestResponseAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val textView: TextView
        val message: TextView
        private val acceptButton: ImageView
        private val infoButton: ImageView
        lateinit var requestResponseKey: String


        init {
            // Define click listener for the ViewHolder's View.
            //textView = view.findViewById(R.id.textView)
            message = view.findViewById(R.id.msg_text)
            acceptButton = view.findViewById(R.id.accept_btn)
            infoButton = view.findViewById(R.id.ride_info)

            acceptButton.setOnClickListener {
                deleteResponse()
            }
        }

        private fun deleteResponse(){
            val database = Firebase.database
            val databaseRef = database.getReference("request_response/")
            databaseRef.child(requestResponseKey).removeValue()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.passenger_notification_recyclerview_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.textView.text = dataSet[position]
        viewHolder.message.text = dataSet[position].message
        viewHolder.requestResponseKey = dataSet[position].requestResponseKey.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}