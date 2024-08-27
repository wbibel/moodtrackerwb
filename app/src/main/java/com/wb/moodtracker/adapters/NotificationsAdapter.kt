
package com.wb.moodtracker.adapters

import NotificationViewHolder
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Notification


class NotificationsAdapter(private val context: Context ,private val notificationList: List<Notification>) : RecyclerView.Adapter<NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val notificationView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_notification, parent, false)
        return NotificationViewHolder(notificationView, context)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.bind(notification)
        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}