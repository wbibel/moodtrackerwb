package com.wb.moodtracker.fragments.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Notification
import com.wb.moodtracker.adapters.NotificationsAdapter

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var recyclerViewNotification: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigation_moods)
            return root
        }
        recyclerViewNotification = root.findViewById(R.id.recyclerViewNotification)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationsViewModel.notificationList.observe(viewLifecycleOwner) { notifications ->
            refreshRecycler(notifications)
        }
    }

    private fun refreshRecycler(notificationsList: List<Notification>) {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerViewNotification.layoutManager = layoutManager
        recyclerViewNotification.adapter = NotificationsAdapter(requireContext().applicationContext,notificationsList)
    }
}
