package com.tcc.fgapool

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.databinding.ActivityOfferRideBinding
import com.tcc.fgapool.models.Ride
import java.util.*

class OfferRide : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityOfferRideBinding
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var originEditText: EditText
    private lateinit var destinationEditText: EditText
    private lateinit var routeEditText: EditText
    private lateinit var seatsEditText: EditText
    private lateinit var sameSexSwitch: SwitchCompat
    private lateinit var offerRideBtn: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var driverName: String

    private var sameSexPassengers: Boolean = false

    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0
    private var mHour = 0
    private var mMinute = 0
    private var savedHour = 0
    private var savedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfferRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        uid = currentFirebaseUser?.uid.toString()
        driverName = currentFirebaseUser?.displayName.toString()

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("rides/").push()


        dateEditText = binding.editTextDate
        timeEditText = binding.editTextTime
        originEditText = binding.editTextOrigin
        destinationEditText = binding.editTextDestination
        routeEditText = binding.editTextRoute
        seatsEditText = binding.editTextSeatsAmount
        sameSexSwitch = binding.sameSexSwitch
        offerRideBtn = binding.offerRideBtn

        datePicker(dateEditText)
        timePicker(timeEditText)
        offerRide()

        sameSexSwitch.setOnCheckedChangeListener(this)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        dateEditText.setText(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        savedHour = hourOfDay
        savedMinute = minute

        timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute))
    }

    private fun getCurrentTimeDate() {

        val cal = Calendar.getInstance(TimeZone.getDefault())
        mDay = cal.get(Calendar.DAY_OF_MONTH)
        mMonth = cal.get(Calendar.MONTH)
        mYear = cal.get(Calendar.YEAR)
        mHour = cal.get(Calendar.HOUR_OF_DAY)
        mMinute = cal.get(Calendar.MINUTE)
    }

    private fun datePicker(editText: EditText) {
        editText.setOnClickListener {
            getCurrentTimeDate()

            DatePickerDialog(this, this, mYear, mMonth, mDay).show()
        }
    }

    private fun timePicker(editText: EditText) {
        editText.setOnClickListener {
            if (savedHour == 0 && savedMinute == 0) {
                getCurrentTimeDate()
                TimePickerDialog(this, this, mHour, mMinute, true).show()
            } else {
                TimePickerDialog(this, this, savedHour, savedMinute, true).show()
            }
        }
    }

    private fun offerRide() {
        offerRideBtn.setOnClickListener {
            if (isEmpty(originEditText) || isEmpty(destinationEditText) || isEmpty(routeEditText)
                || isEmpty(dateEditText) || isEmpty(timeEditText) || isEmpty(seatsEditText)
            ) {
                alertDialog()
            } else {
                writeNewRide(originEditText.text.toString(), destinationEditText.text.toString(),
                routeEditText.text.toString(), dateEditText.text.toString(), timeEditText.text.toString(),
                seatsEditText.text.toString(), sameSexPassengers, true, uid, driverName)
            }
        }
    }

    private fun isEmpty(editText: EditText): Boolean {
        return editText.text.isEmpty()
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!")
            .setMessage("Todos os campos devem ser preenchidos!")
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()
            }
        builder.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        sameSexPassengers = isChecked
    }

    private fun writeNewRide(
        origin: String,
        destination: String,
        route: String,
        date: String,
        time: String,
        seatsAvailable: String,
        sameSexPassengers: Boolean,
        isActive: Boolean,
        userId: String,
        driverName: String
    ) {

        val ride = Ride(
            origin,
            destination,
            route,
            date,
            time,
            seatsAvailable,
            sameSexPassengers,
            isActive,
            userId,
            driverName
        )

        databaseRef.setValue(ride)
            .addOnSuccessListener {
                Toast.makeText(this, R.string.DB_on_send_success, Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.DB_on_send_failure, Toast.LENGTH_SHORT).show()
            }
    }

}