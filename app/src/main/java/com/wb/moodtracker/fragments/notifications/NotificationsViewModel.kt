package com.wb.moodtracker.fragments.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wb.moodtracker.data.models.Notification
import com.wb.moodtracker.managers.DataManager
import com.wb.moodtracker.managers.NotificationCallback

class NotificationsViewModel : ViewModel() {

    private val dataManager = DataManager()

    private val _notificationList = MutableLiveData<List<Notification>>()
    val notificationList: LiveData<List<Notification>> = _notificationList

    init {
        readNotifications()
    }


    private fun readNotifications() {
        dataManager.readNotifications(object : NotificationCallback {
            override fun onNotificationLoaded(notifications: List<Notification>) {
                _notificationList.postValue(notifications)
            }
        })
    }
}
