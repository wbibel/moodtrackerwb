package com.wb.moodtracker.fragments.profile

import androidx.lifecycle.ViewModel
import com.wb.moodtracker.data.models.Profile
import com.wb.moodtracker.managers.DataManager
import com.wb.moodtracker.managers.ErrorCode
import com.wb.moodtracker.managers.ProfileCallback
import java.util.Date

class ProfileViewModel : ViewModel() {
    private val dataManager = DataManager()
    fun readProfile(callback: ProfileCallback) {
        dataManager.readProfile(callback)
    }
    fun updateProfile(selectedHeight: Int, selectedWeight: Int, onSuccess: () -> Unit, onError: (ErrorCode) -> Unit) {
        val updatedProfile = Profile(selectedHeight, selectedWeight, Date())

        dataManager.updateProfile(updatedProfile, {
            onSuccess()
        }, { errorCode ->
            onError(errorCode)
        })
    }





}