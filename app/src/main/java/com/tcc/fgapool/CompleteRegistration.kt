package com.tcc.fgapool

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tcc.fgapool.databinding.ActivityCompleteRegistrationBinding
import com.tcc.fgapool.utils.CircleTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CompleteRegistration : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityCompleteRegistrationBinding

    private lateinit var carPlateLayout: TextInputLayout
    private lateinit var carModelLayout: TextInputLayout
    private lateinit var carColorLayout: TextInputLayout

    private var isDriver: Boolean = false

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Firebase user reference
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser

        //Database reference
        database = Firebase.database
        databaseRef = database.getReference("signup_info/" + currentFirebaseUser?.uid)

        //View objects
        val courseDropdown: Spinner = binding.courseDropdown
        val genderDropdown: Spinner = binding.genderDropdown
        val isDriverSwitch: SwitchCompat = binding.isDriver
        val profileName: TextView = binding.profileName
        val profilePicture: ImageView = binding.profilePicture
        val sendButton: Button = binding.buttonSend
        val registrationNumber: EditText = binding.editTextMatricula
        val phoneNumber: EditText = binding.editTextPhone
        val carPlate: EditText = binding.carPlate
        val carModel: EditText = binding.carModel
        val carColor: EditText = binding.carColor
        carPlateLayout = binding.carPlateLayout
        carModelLayout = binding.carModelLayout
        carColorLayout = binding.carColorLayout

        // Create an ArrayAdapter using the string array and a default spinner layout
        spinnerAdapter(courseDropdown, R.array.courses_array)
        spinnerAdapter(genderDropdown, R.array.gender_array)

        //Listeners
        isDriverSwitch.setOnCheckedChangeListener(this)

        //Profile picture and name setting
        val currentUser: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        profileName.text = currentUser.displayName
        Picasso.get()
            .load(currentUser.photoUrl)
            .transform(CircleTransformation())
            .into(profilePicture)

        sendButton.setOnClickListener {
            if (isDriver){
                if (checkDriverInputs(registrationNumber, phoneNumber, carPlate, carModel, carColor, courseDropdown, genderDropdown)){
                    alertDialog()
                } else {
                    databaseRef.child("registrationNumber").setValue(registrationNumber.text.toString())
                    databaseRef.child("course").setValue(courseDropdown.selectedItem.toString())
                    databaseRef.child("gender").setValue(genderDropdown.selectedItem.toString())
                    databaseRef.child("phoneNumber").setValue(phoneNumber.text.toString())
                    databaseRef.child("isDriver").setValue(isDriver)
                    databaseRef.child("carPlate").setValue(carPlate.text.toString())
                    databaseRef.child("carModel").setValue(carModel.text.toString())
                    databaseRef.child("carColor").setValue(carColor.text.toString())
                    databaseRef.child("registrationComplete").setValue(true)
                }
            } else if (checkPassengerInputs(registrationNumber, phoneNumber, courseDropdown, genderDropdown)){
                alertDialog()
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

    private fun checkDriverInputs(registrationNumber:EditText, phoneNumber:EditText, carPlate:EditText,
                                  carModel:EditText, carColor:EditText, courseDropdown:Spinner,
                                  genderDropdown:Spinner) : Boolean{

        return isLessThan(registrationNumber, 9) || isLessThan(phoneNumber, 11) ||
                isLessThan(carPlate, 7) || isEmpty(carModel) || isEmpty(carColor) ||
                spinnerAtZeroPos(courseDropdown) || spinnerAtZeroPos(genderDropdown)

    }

    private fun checkPassengerInputs(registrationNumber:EditText, phoneNumber:EditText,
                            courseDropdown:Spinner, genderDropdown:Spinner) : Boolean{

        return spinnerAtZeroPos(courseDropdown) || spinnerAtZeroPos(genderDropdown) ||
                isLessThan(registrationNumber, 9) || isLessThan(phoneNumber, 11)


    }

    private fun isEmpty(editText:EditText) : Boolean {
        return editText.text.isEmpty()
    }

    private fun isLessThan(editText:EditText, length:Int) : Boolean {
        return editText.text.length < length
    }

    private fun spinnerAtZeroPos(spinner: Spinner):Boolean{
        //Toast.makeText(this, spinner.selectedItemPosition.toString(), Toast.LENGTH_SHORT).show()
        return spinner.selectedItemPosition == 0
    }

    private fun alertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!")
            .setMessage("Todos os campos são obrigatórios!")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })
        builder.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {

            carPlateLayout.visibility = View.VISIBLE
            carModelLayout.visibility = View.VISIBLE
            carColorLayout.visibility = View.VISIBLE
            isDriver = true

        } else {

            carPlateLayout.visibility = View.GONE
            carModelLayout.visibility = View.GONE
            carColorLayout.visibility = View.GONE
            isDriver = false

        }

    }

    private fun spinnerAdapter(spinner: Spinner, arrayRes: Int) {
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
