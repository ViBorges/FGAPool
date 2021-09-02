package com.tcc.fgapool

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tcc.fgapool.databinding.ActivityCompleteRegistrationBinding
import com.tcc.fgapool.utils.CircleTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CompleteRegistration : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityCompleteRegistrationBinding

    private lateinit var carPlate : EditText
    private lateinit var carModel : EditText
    private lateinit var carColor : EditText

    private var isDriver : Boolean = false

    private lateinit var database:FirebaseDatabase
    private lateinit var databaseRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("signup_info/"+currentFirebaseUser?.uid)

        //View objects
        val courseDropdown: Spinner = binding.courseDropdown
        val genderDropdown: Spinner = binding.genderDropdown
        val isDriverSwitch: SwitchCompat = binding.isDriver
        val profileName : TextView = binding.profileName
        val profilePicture : ImageView = binding.profilePicture
        val sendButton : Button = binding.buttonSend
        val registrationNumber : EditText = binding.editTextMatricula
        val phoneNumber : EditText = binding.editTextPhone
        carPlate = binding.carPlate
        carModel = binding.carModel
        carColor = binding.carColor

        // Create an ArrayAdapter using the string array and a default spinner layout
        spinnerAdapter(courseDropdown, R.array.courses_array)
        spinnerAdapter(genderDropdown, R.array.gender_array)

        //Listeners
        isDriverSwitch.setOnCheckedChangeListener(this)
        courseDropdown.onItemSelectedListener = this
        genderDropdown.onItemSelectedListener = this

        //Profile picture and name setting
        val currentUser : GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        profileName.text = currentUser.displayName
        Picasso.get()
            .load(currentUser.photoUrl)
            .transform(CircleTransformation())
            .into(profilePicture)

        sendButton.setOnClickListener {
            if (isDriver){
                databaseRef.child("registrationNumber").setValue(registrationNumber.text.toString())
                databaseRef.child("course").setValue(courseDropdown.selectedItem.toString())
                databaseRef.child("gender").setValue(genderDropdown.selectedItem.toString())
                databaseRef.child("phoneNumber").setValue(phoneNumber.text.toString())
                databaseRef.child("isDriver").setValue(isDriver)
                databaseRef.child("carPlate").setValue(carPlate.text.toString())
                databaseRef.child("carModel").setValue(carModel.text.toString())
                databaseRef.child("carColor").setValue(carColor.text.toString())
                databaseRef.child("registrationComplete").setValue(true)
            } else {
                databaseRef.child("registrationNumber").setValue(registrationNumber.text.toString())
                databaseRef.child("course").setValue(courseDropdown.selectedItem.toString())
                databaseRef.child("gender").setValue(genderDropdown.selectedItem.toString())
                databaseRef.child("phoneNumber").setValue(phoneNumber.text.toString())
                databaseRef.child("isDriver").setValue(isDriver)
                databaseRef.child("registrationComplete").setValue(true)
            }

        }

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        parent.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked){

            carPlate.visibility = View.VISIBLE
            carModel.visibility = View.VISIBLE
            carColor.visibility = View.VISIBLE
            isDriver = true

        } else {

            carPlate.visibility = View.GONE
            carModel.visibility = View.GONE
            carColor.visibility = View.GONE
            isDriver = false

        }

    }

    private fun spinnerAdapter(spinner:Spinner, arrayRes:Int){
        ArrayAdapter.createFromResource(
            this,
            arrayRes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }
}