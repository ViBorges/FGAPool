package com.tcc.fgapool

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tcc.fgapool.databinding.ActivityEditRideBinding
import com.tcc.fgapool.models.Ride
import java.util.*

class EditRideActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityEditRideBinding

    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var originEditText: EditText
    private lateinit var destinationEditText: EditText
    private lateinit var routeEditText: EditText
    private lateinit var seatsEditText: EditText
    private lateinit var sameSexSwitch: SwitchCompat
    private lateinit var editRideBtn: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var driverName: String
    private lateinit var status: String
    private lateinit var passenger1: String
    private lateinit var passenger2: String
    private lateinit var passenger3: String
    private lateinit var passenger4: String

    private lateinit var rideId: String

    private var sameSexPassengers: Boolean? = null

    private var mDay = 0
    private var mMonth = 0
    private var mYear = 0
    private var mHour = 0
    private var mMinute = 0
    private var savedHour = 0
    private var savedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRideBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val mListItem = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra<Ride>("listItem", Ride::class.java)
        } else {
            intent.getParcelableExtra<Ride>("listItem")
        }

        if (mListItem != null) {
            binding.editTextOrigin.setText(mListItem.origin)
            binding.editTextDestination.setText(mListItem.destination)
            binding.editTextRoute.setText(mListItem.route)
            binding.editTextDate.setText(mListItem.date)
            binding.editTextTime.setText(mListItem.time)
            binding.editTextSeatsAmount.setText(mListItem.seatsAvailable)
            mListItem.sameSexPassengers?.let { binding.sameSexSwitch.isChecked = it }
            rideId = mListItem.rideKey.toString()
            driverName = mListItem.driverName.toString()
            uid = mListItem.userId.toString()
            status = mListItem.status.toString()
            passenger1 = mListItem.passenger1.toString()
            passenger2 = mListItem.passenger2.toString()
            passenger3 = mListItem.passenger3.toString()
            passenger4 = mListItem.passenger4.toString()

            if (sameSexPassengers == null)
                sameSexPassengers = mListItem.sameSexPassengers
        }

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("rides/").child(rideId)


        dateEditText = binding.editTextDate
        timeEditText = binding.editTextTime
        originEditText = binding.editTextOrigin
        destinationEditText = binding.editTextDestination
        routeEditText = binding.editTextRoute
        seatsEditText = binding.editTextSeatsAmount
        sameSexSwitch = binding.sameSexSwitch
        editRideBtn = binding.offerRideBtn

        datePicker(dateEditText)
        timePicker(timeEditText)
        editRide()

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

    private fun editRide() {
        editRideBtn.setOnClickListener {
            if (isEmpty(originEditText) || isEmpty(destinationEditText) || isEmpty(routeEditText)
                || isEmpty(dateEditText) || isEmpty(timeEditText) || isEmpty(seatsEditText)
            ) {
                alertDialog()
            } else {
                sameSexPassengers?.let { it1 ->
                    updateRide(null, originEditText.text.toString(), destinationEditText.text.toString(),
                        routeEditText.text.toString(), dateEditText.text.toString(), timeEditText.text.toString(),
                        seatsEditText.text.toString(), it1, true, uid, driverName, status)
                }
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

    private fun updateRide(
        rideKey: String?,
        origin: String,
        destination: String,
        route: String,
        date: String,
        time: String,
        seatsAvailable: String,
        sameSexPassengers: Boolean,
        isActive: Boolean,
        userId: String,
        driverName: String,
        status: String
    ) {

        val ride = Ride(
            rideKey,
            origin,
            destination,
            route,
            date,
            time,
            seatsAvailable,
            sameSexPassengers,
            isActive,
            userId,
            driverName,
            passenger1,
            passenger2,
            passenger3,
            passenger4,
            status
        )

        databaseRef.setValue(ride)
            .addOnSuccessListener {
                Toast.makeText(this, R.string.DB_on_edit_success, Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.DB_on_send_failure, Toast.LENGTH_SHORT).show()
            }
    }


}