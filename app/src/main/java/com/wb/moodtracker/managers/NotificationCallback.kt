package com.wb.moodtracker.managers

import com.wb.moodtracker.data.models.Notification

interface NotificationCallback {
    fun onNotificationLoaded(notification: List<Notification>)
}
