package com.wb.moodtracker.managers

import com.wb.moodtracker.data.models.Mood

interface MoodsCallback {
    fun onMoodsLoaded(moods: List<Mood>)
    fun onError(error: ErrorCode)
}