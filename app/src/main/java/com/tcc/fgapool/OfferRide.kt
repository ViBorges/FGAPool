package com.tcc.fgapool

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.tcc.fgapool.databinding.ActivityOfferRideBinding
import java.util.*

class OfferRide : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {

    lateinit var binding: ActivityOfferRideBinding
    lateinit var dateEditText: EditText
    lateinit var timeEditText: EditText
    lateinit var originEditText: EditText
    lateinit var destinationEditText: EditText
    lateinit var routeEditText: EditText
    lateinit var seatsEditText: EditText
    lateinit var sameSexSwitch: SwitchCompat
    lateinit var offerRideBtn: Button

    var sameSexPassengers: Boolean = false

    var mDay = 0
    var mMonth = 0
    var mYear = 0
    var mHour = 0
    var mMinute = 0
    var savedHour = 0
    var savedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfferRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                Toast.makeText(this, "Enviado!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isEmpty(editText: EditText): Boolean {
        return editText.text.isEmpty()
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!")
            .setMessage("Todos os campos são obrigatórios!")
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
        builder.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        sameSexPassengers = isChecked
    }

}