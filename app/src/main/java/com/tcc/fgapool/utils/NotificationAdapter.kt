package com.tcc.fgapool.utils

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tcc.fgapool.R
import com.tcc.fgapool.models.Notification

class NotificationAdapter(private val dataSet: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val messageText: TextView
        val rideInfo: ImageView
        val wppButton: ImageView
        val telegramButton: ImageView
        val acceptButton: ImageView
        val rejectButton: ImageView

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
                    Uri.parse("https://wa.me/+5561numero?text=Posso confirmar sua carona?"))
                view.context.startActivity(urlIntent)
            }

            telegramButton.setOnClickListener {
                val urlIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/+5561numero"))
                view.context.startActivity(urlIntent)
            }
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
        viewHolder.messageText.text = dataSet[position].message
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}