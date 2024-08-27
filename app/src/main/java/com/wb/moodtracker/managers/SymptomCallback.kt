package com.wb.moodtracker.managers

import com.wb.moodtracker.data.models.Symptom

interface SymptomCallback {
    fun onSymptomsLoaded(symptomList: List<Symptom>)
    fun onError(error: ErrorCode)
}
