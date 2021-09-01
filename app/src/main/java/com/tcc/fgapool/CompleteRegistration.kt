package com.tcc.fgapool

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.squareup.picasso.Picasso
import com.tcc.fgapool.databinding.ActivityCompleteRegistrationBinding
import com.tcc.fgapool.utils.CircleTransformation

class CompleteRegistration : AppCompatActivity(), AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: ActivityCompleteRegistrationBinding

    private lateinit var carPlate : EditText
    private lateinit var carModel : EditText
    private lateinit var carColor : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteRegistrationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val spinner: Spinner = binding.spinner
        val switch: SwitchCompat = binding.isDriver
        val nameTextView : TextView = binding.personName
        val profilePicture : ImageView = binding.profilePicture
        carPlate = binding.carPlate
        carModel = binding.carModel
        carColor = binding.carColor

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.courses_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        switch.setOnCheckedChangeListener(this)
        spinner.onItemSelectedListener = this

        val currentUser : GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        nameTextView.text = currentUser.displayName
        Picasso.get()
            .load(currentUser.photoUrl)
            .transform(CircleTransformation())
            .into(profilePicture)

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        Toast.makeText(this, parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show()

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
            // Another interface callback
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked){
            carPlate.visibility = View.VISIBLE
            carModel.visibility = View.VISIBLE
            carColor.visibility = View.VISIBLE

            //Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show()
        } else {
            carPlate.visibility = View.GONE
            carModel.visibility = View.GONE
            carColor.visibility = View.GONE

            //Toast.makeText(this, "Unchecked", Toast.LENGTH_SHORT).show()
        }

    }
}