package com.tcc.fgapool.ui.rides

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tcc.fgapool.OfferRide
import com.tcc.fgapool.OfferRideAdapter
import com.tcc.fgapool.databinding.FragmentRidesBinding
import com.tcc.fgapool.models.RideItem


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
            ViewModelProvider(this)[RidesViewModel::class.java]

        _binding = FragmentRidesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textDashboard
        ridesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        val recyclerView = binding.rideRecyclerview
        recyclerView.adapter = OfferRideAdapter(rides())
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager


        binding.fab.setOnClickListener {
            updateUI()
        }

        return root
    }

    private fun rides(): List<RideItem> {
        return listOf(
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
            RideItem("FGA", "Taguatinga", "04/01/2023", "16:00",
                "Andreia", "Eng. de Energia", "0"),
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
            RideItem("Recanto das Emas", "FGA", "02/01/2023", "08:00",
                "Vinicius B.", "Eng. de Software", "2"),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(){
        val intent = Intent(context, OfferRide::class.java)
        startActivity(intent)
    }
}