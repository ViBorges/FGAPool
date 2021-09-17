package com.tcc.fgapool.ui.rides

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.tcc.fgapool.MapsActivity
import com.tcc.fgapool.OfferRide
import com.tcc.fgapool.databinding.FragmentRidesBinding

class RidesFragment : Fragment() {

    private lateinit var ridesViewModel: RidesViewModel
    private var _binding: FragmentRidesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ridesViewModel =
            ViewModelProvider(this).get(RidesViewModel::class.java)

        _binding = FragmentRidesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textDashboard
        ridesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        binding.fab.setOnClickListener {
            updateUI()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(){
        val intent = Intent(context, MapsActivity::class.java)
        startActivity(intent)
    }
}