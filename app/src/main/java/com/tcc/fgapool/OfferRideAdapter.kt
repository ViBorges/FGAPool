package com.tcc.fgapool

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.tcc.fgapool.models.RideItem

class OfferRideAdapter(private val dataSet: List<RideItem>) :
    RecyclerView.Adapter<OfferRideAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val textView: TextView
        val origin: TextView
        val destination: TextView
        val date: TextView
        val time: TextView
        val driverName: TextView
        val driverCourse: TextView
        val seatsAvailable: TextView

        init {
            // Define click listener for the ViewHolder's View.
            //textView = view.findViewById(R.id.textView)
            origin = view.findViewById(R.id.originText)
            destination = view.findViewById(R.id.destinationText)
            date = view.findViewById(R.id.originDateText)
            time = view.findViewById(R.id.originTimeText)
            driverName = view.findViewById(R.id.driverName)
            driverCourse = view.findViewById(R.id.driverCourse)
            seatsAvailable = view.findViewById(R.id.seatsAvailable)
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
        //viewHolder.textView.text = dataSet[position]
        //val ride = dataSet[position]
        viewHolder.origin.text = dataSet[position].origin
        viewHolder.destination.text = dataSet[position].destination
        viewHolder.date.text = dataSet[position].date
        viewHolder.time.text = dataSet[position].time
        viewHolder.driverName.text = dataSet[position].driverName
        viewHolder.driverCourse.text = dataSet[position].driverCourse
        viewHolder.seatsAvailable.text = dataSet[position].seatsAvailable

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}