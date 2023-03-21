package com.tcc.fgapool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tcc.fgapool.databinding.ActivityEditRideBinding
import com.tcc.fgapool.databinding.ActivityRideDetailBinding

class RideDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRideDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}