package com.wb.moodtracker.managers
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.wb.moodtracker.data.models.Mood
import com.wb.moodtracker.data.models.Notification
import com.wb.moodtracker.data.models.Profile
import com.wb.moodtracker.data.models.Symptom
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Random
import java.util.concurrent.TimeUnit

class DataManager {
    private var firestore: FirebaseFirestore

    init {
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = firestoreSettings
    }


    fun readProfile(callback: ProfileCallback) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val profileRef = firestore.collection("Profiles").document(currentUser.uid)
            profileRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profile =  this.mapDocumentToProfile(document)
                        callback.onProfileLoaded(profile)
                    } else {
                        callback.onProfileLoaded(null)
                    }
                }
                .addOnFailureListener {
                    callback.onError(ErrorCode.EPR)
                }
        } else {
            callback.onError(ErrorCode.EMA)
        }
    }

    fun updateProfile(
        updatedProfile: Profile,
        onSuccess: () -> Unit,
        onError: (ErrorCode) -> Unit
    ) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val profileRef = firestore.collection("Profiles").document(currentUser.uid)
            profileRef.set(updatedProfile, SetOptions.merge())
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onError(ErrorCode.EPU)
                }
        } else {
            onError(ErrorCode.EMA)
        }
    }


    fun readMoods(callback: MoodsCallback) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val moodsRef = firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            moodsRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val moods = mutableListOf<Mood>()
                    for (document in querySnapshot) {
                        val mood = mapDocumentToMood(document)
                        moods.add(mood)
                    }
                    callback.onMoodsLoaded(moods)
                }
                .addOnFailureListener {
                    callback.onError(ErrorCode.EMR)
                }
        } else {
            callback.onError(ErrorCode.EMA)
        }
    }
    fun readMoodsLastMonth(callback: MoodsCallback) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val moodsRef = firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)
            val startOfMonth = calendar.timeInMillis
            val endOfMonth = System.currentTimeMillis()
            moodsRef
                .whereGreaterThanOrEqualTo("createDate", Timestamp(startOfMonth / 1000, 0))
                .whereLessThanOrEqualTo("createDate", Timestamp(endOfMonth / 1000, 999_999_999))
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val moods = mutableListOf<Mood>()
                    for (document in querySnapshot) {
                        val mood = mapDocumentToMood(document)
                        moods.add(mood)
                    }
                    callback.onMoodsLoaded(moods)
                }
                .addOnFailureListener {
                    callback.onError(ErrorCode.EMR)
                }
        } else {
            callback.onError(ErrorCode.EMA)
        }
    }
    fun readMoodsLastWeek(callback: MoodsCallback) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val moodsRef = firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -7)
            val startOfWeek = calendar.timeInMillis
            val endOfWeek = System.currentTimeMillis()
            moodsRef
                .whereGreaterThanOrEqualTo("createDate", Timestamp(startOfWeek / 1000, 0))
                .whereLessThanOrEqualTo("createDate", Timestamp(endOfWeek / 1000, 999_999_999))
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val moods = mutableListOf<Mood>()
                    for (document in querySnapshot) {
                        val mood = mapDocumentToMood(document)
                        moods.add(mood)
                    }
                    callback.onMoodsLoaded(moods)
                }
                .addOnFailureListener {
                    callback.onError(ErrorCode.EMR)
                }
        } else {
            callback.onError(ErrorCode.EMA)
        }
    }
    fun readMoodsLastYear(callback: MoodsCallback) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val moodsRef = firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -12)
            val startOfYearPeriod = calendar.timeInMillis
            val endOfYearPeriod = System.currentTimeMillis()
            moodsRef
                .whereGreaterThanOrEqualTo("createDate", Timestamp(startOfYearPeriod / 1000, 0))
                .whereLessThanOrEqualTo("createDate", Timestamp(endOfYearPeriod / 1000, 999_999_999))
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val moods = mutableListOf<Mood>()
                    for (document in querySnapshot) {
                        val mood = mapDocumentToMood(document)
                        moods.add(mood)
                    }
                    callback.onMoodsLoaded(moods)
                }
                .addOnFailureListener {
                    callback.onError(ErrorCode.EMR)
                }
        } else {
            callback.onError(ErrorCode.EMA)
        }
    }


    fun readMoodFromDay(date: LocalDate, timeZone: ZoneId, callback: (List<Mood>) -> Unit) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val moodsRef = firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            val startOfDay = date.atStartOfDay(timeZone).toInstant().toEpochMilli()
            val endOfDay = date.atTime(23, 59, 59, 999_999_999).atZone(timeZone).toInstant().toEpochMilli()
            moodsRef
                .whereGreaterThanOrEqualTo("createDate", Timestamp(startOfDay / 1000, 0))
                .whereLessThanOrEqualTo("createDate", Timestamp(endOfDay / 1000, 999_999_999))
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val moods = mutableListOf<Mood>()
                    for (document in querySnapshot) {
                        val mood = mapDocumentToMood(document)
                        moods.add(mood)
                    }
                    callback(moods)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    callback(emptyList())
                }
        }
    }






    fun getLastMoodAddedTime(callback: (Date?) -> Unit) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val moodsRef = firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            val query = moodsRef.orderBy("createDate", Query.Direction.DESCENDING).limit(1)
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.documents.firstOrNull() != null){
                        val lastMood = mapDocumentToMood(querySnapshot.documents.firstOrNull()!!)
                        callback(lastMood.createDate)
                    }
                    else {
                        callback(null)
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    fun createMood(moodCreate: Mood , onSuccess: () -> Unit , onError: (ErrorCode) -> Unit) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val moodsRef =
                firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            moodsRef.add(moodCreate)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onError(ErrorCode.EMC)
                }

        } else {
            onError(ErrorCode.EMA)
        }
    }

    fun deleteMood(id: String , onSuccess: () -> Unit , onError: (ErrorCode) -> Unit) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val moodsRef =
                firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries").document(id)
            moodsRef.delete()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onError(ErrorCode.EMD)
                }
        } else {
            onError(ErrorCode.EMA)
        }
    }



    fun updateMood(moodUpdate: Mood, onSuccess: () -> Unit, onError: (ErrorCode) -> Unit) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null && moodUpdate.key != null) {
            val moodsRef =
                firestore.collection("Moods").document(currentUser.uid).collection("MoodEntries")
            moodsRef.document(moodUpdate.key).set(moodUpdate)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onError(ErrorCode.EMU)
                }
        } else {
            onError(ErrorCode.EMA)
        }
    }

    fun readSymptoms(callback: SymptomCallback) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val symptomRef = firestore.collection("Symptoms")
            symptomRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val symptoms = mutableListOf<Symptom>()
                    for (document in querySnapshot) {
                        val symptom = mapDocumentToSymptom(document)
                        symptoms.add(symptom)
                    }
                    callback.onSymptomsLoaded(symptoms)
                }
                .addOnFailureListener {
                    callback.onError(ErrorCode.EMR)
                }
        } else {
            callback.onError(ErrorCode.EMA)
        }
    }

    private fun mapDocumentToSymptom(document: DocumentSnapshot): Symptom {
        val text = document.getString("text") ?: ""
        val id = document.getString("id") ?: ""
        return Symptom(text, id)
    }
    private fun mapDocumentToMood(document: DocumentSnapshot): Mood {
        val description = document.getString("description") ?: ""
        val createDate = document.getDate("createDate") ?: Date()
        val timeZoneMap = document.get("timeZone") as? Map<String, Any>
        val timeZoneId = timeZoneMap?.get("id") as? String ?: "UTC"
        val timeZone = ZoneId.of(timeZoneId)
        val rating = document.getLong("rating")?.toInt() ?: 0
        val symptomsSnapshot = document.get("list") as? List<Map<String, Any>>?
        val symptomsList = symptomsSnapshot?.mapNotNull { symptomMap ->
            val text = symptomMap["text"] as? String
            val id = symptomMap["id"] as? String
            text?.let { Symptom(it, id ?: "") }
        }
        val key = document.id

        return Mood(description, createDate, timeZone, rating, symptomsList, key)
    }
    private fun mapDocumentToProfile(document: DocumentSnapshot): Profile {
        val height = document.getLong("height")?.toInt() ?: 0
        val weight = document.getLong("weight")?.toInt() ?: 0
        val firstDate = document.getDate("firstDate") ?: Date()
        return Profile(height,weight,firstDate)
    }
    private fun mapDocumentToNotification(document: DocumentSnapshot): Notification {
        val message = document.getString("message") ?: ""
        val createDate = document.getDate("createDate") ?: Date()
        return Notification(message,createDate)
    }
    fun readNotifications(callback: NotificationCallback) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val notifacationRef = firestore.collection("Notifications").document(currentUser.uid).collection("NotificationEntries")
            notifacationRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val notifications = mutableListOf<Notification>()
                    for (document in querySnapshot) {
                        val symptom = mapDocumentToNotification(document)
                        notifications.add(symptom)
                    }
                    callback.onNotificationLoaded(notifications)
                }
        }
    }

    fun createNotification(disease: Notification) {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val notifacationRef = firestore.collection("Notifications").document(currentUser.uid).collection("NotificationEntries")
            notifacationRef.add(disease)
        } else {
        }
    }
}



