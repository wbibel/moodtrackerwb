package com.wb.moodtracker.fragments.addMood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.wb.moodtracker.data.models.Mood
import com.wb.moodtracker.data.models.Symptom
import com.wb.moodtracker.managers.DataManager
import com.wb.moodtracker.managers.ErrorCode
import com.wb.moodtracker.managers.SymptomCallback
import java.util.Date
import java.util.concurrent.TimeUnit

class AddMoodViewModel : ViewModel() {
    private var dataManager = DataManager()

    var selectedSymptomList: MutableCollection<Symptom> = mutableSetOf()
    private val _symptomList = MutableLiveData<List<Symptom>>()
    var symptomList: LiveData<List<Symptom>> = _symptomList

    init {
        readSymptoms()
    }

    private fun readSymptoms() {
        dataManager.readSymptoms(object : SymptomCallback {
            override fun onSymptomsLoaded(symptomList: List<Symptom>) {
                _symptomList.value = symptomList
            }

            override fun onError(error: ErrorCode) {
            }
        })
    }



    fun updateSymptomList(symptoms: List<Symptom>) {
        selectedSymptomList = symptoms.toMutableList()
    }

    fun updateMood(moodUpdate: Mood, successCallback: () -> Unit, errorCallback: (ErrorCode) -> Unit) {
        dataManager.updateMood(moodUpdate,
            onSuccess = {
                successCallback()
            },
            onError = { error ->
                errorCallback(error)
            }
        )
    }
    fun deleteMood( selectedMood: Mood, successCallback: () -> Unit, errorCallback: (ErrorCode) -> Unit){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            val dataManager = DataManager()
            if (!selectedMood.key.isNullOrEmpty()){
                dataManager.deleteMood(
                    selectedMood.key,
                    onSuccess = {

                        successCallback()

                    },
                    onError = { error ->
                        errorCallback(error)
                    }
                )
            }
        }
    }

    fun createMood(
        moodCreate: Mood,
        successCallback: () -> Unit,
        errorCallback: (ErrorCode) -> Unit
    ) {
        val dataManager = DataManager()
        dataManager.getLastMoodAddedTime { lastAddedTime ->
            if (lastAddedTime != null) {
                val currentTime = Date()
                val timeDifference = currentTime.time - lastAddedTime.time
                val minutesDifference = TimeUnit.MILLISECONDS.toMinutes(timeDifference)
                if (minutesDifference >= 15) {
                    dataManager.createMood(
                        moodCreate,
                        onSuccess = {
                            val moodAnalisis = MoodAnalisis()
                            moodAnalisis.analyzeMoods()
                            successCallback()
                        },
                        onError = { error ->
                            errorCallback(error)
                        }
                    )
                } else {
                    errorCallback(ErrorCode.EMC15)
                }
            } else {
                dataManager.createMood(
                    moodCreate,
                    onSuccess = {
                        successCallback()
                    },
                    onError = { error ->
                        errorCallback(error)
                    }
                )
            }
        }

    }





}
