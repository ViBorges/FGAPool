package com.tcc.fgapool

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import com.tcc.fgapool.databinding.ActivityOfferRideBinding
import java.text.SimpleDateFormat
import java.util.*

lateinit var binding: ActivityOfferRideBinding
lateinit var dateEditText: EditText
lateinit var timeEditText: EditText
var mDay = 0
var mMonth = 0
var mYear = 0
var mHour = 0
var mMinute = 0
var savedHour = 0
var savedMinute =0

class OfferRide : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOfferRideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dateEditText = binding.editTextDate
        timeEditText = binding.editTextTime

        datePicker()
        timePicker()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

       dateEditText.setText(String.format("%02d/%02d/%d", dayOfMonth, month+1, year))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        savedHour = hourOfDay
        savedMinute = minute

        timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute))
    }

    private fun getCurrentTimeDate(){
        val cal = Calendar.getInstance(TimeZone.getDefault())
        mDay = cal.get(Calendar.DAY_OF_MONTH)
        mMonth = cal.get(Calendar.MONTH)
        mYear = cal.get(Calendar.YEAR)
        mHour = cal.get(Calendar.HOUR_OF_DAY)
        mMinute = cal.get(Calendar.MINUTE)
    }

    private fun datePicker(){
        dateEditText.setOnClickListener {
            getCurrentTimeDate()

            DatePickerDialog(this, this, mYear, mMonth, mDay).show()
        }
    }

    private fun timePicker(){
        timeEditText.setOnClickListener {

            if (savedHour == 0 && savedMinute == 0){
                getCurrentTimeDate()
                TimePickerDialog(this, this, mHour, mMinute, true).show()
            } else {
                TimePickerDialog(this, this, savedHour, savedMinute, true).show()
            }

        }
    }

}