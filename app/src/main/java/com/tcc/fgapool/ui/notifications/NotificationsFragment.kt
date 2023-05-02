package com.tcc.fgapool.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tcc.fgapool.databinding.FragmentNotificationsBinding
import com.tcc.fgapool.models.Notification
import com.tcc.fgapool.utils.NotificationAdapter

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationList: List<Notification>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Set RecyclerView
        recyclerView = binding.notificationRecyclerView
        recyclerView.adapter = NotificationAdapter(notifications())
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager


        return root
    }

    private fun notifications(): List<Notification> {
        return listOf(
            Notification("Fernanda Borges\nSolicitou uma carona", null, null, null),
            Notification("Vinicius Borges\nSolicitou uma carona", null, null, null),
            Notification("Brenda Caroline\nSolicitou uma carona", null, null, null)

        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}