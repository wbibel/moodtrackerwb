package com.wb.moodtracker.managers

import com.wb.moodtracker.data.models.Profile

interface ProfileCallback {
    fun onProfileLoaded(profile: Profile?)
    fun onError(error: ErrorCode)
}