package com.wb.moodtracker.fragments.mood

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.wb.moodtracker.data.models.Mood
import com.wb.moodtracker.managers.DataManager
import com.wb.moodtracker.managers.ErrorCode
import com.wb.moodtracker.managers.MoodsCallback
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.floor

class MoodViewModel : ViewModel() {
    private val dataManager = DataManager()

    fun readSelectedDateMood(
        selectedYear: Int,
        selectedMonth: Int,
        selectedDayOfMonth: Int,
        callback: (List<Mood>) -> Unit
    ) {
        val selectedDate = LocalDate.of(selectedYear, selectedMonth, selectedDayOfMonth)
        val timeZone = ZoneId.systemDefault()

        dataManager.readMoodFromDay(selectedDate, timeZone) { moods ->
            val filteredMoods = moods.sortedByDescending { it.createDate }
            callback(filteredMoods)
        }
    }


    fun readAllRatings(
        successCallback: (Array<ArrayList<CalendarDay>>) -> Unit,
        errorCallback: (ErrorCode) -> Unit
    ) {
        dataManager.readMoods(object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                if (moods.isNotEmpty()) {
                    val array = Array(6) { ArrayList<CalendarDay>() }
                    val earliestMoodDate = moods.minByOrNull { it.createDate }?.createDate
                    val calendar = Calendar.getInstance()
                    calendar.time = earliestMoodDate

                    val daysDifference =
                        ChronoUnit.DAYS.between(calendar.time.toInstant(), Instant.now()) + 1
                    for (i in 0 until daysDifference + 1) {
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startDate = calendar.time

                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        calendar.set(Calendar.MILLISECOND, 999)
                        val endDate = calendar.time

                        val ratings = moods.filter { mood ->
                            mood.createDate >= startDate && mood.createDate <= endDate && mood.rating != 0
                        }.map { it.rating.toFloat() }

                        val totalRating = ratings.sum()
                        val totalMoods = ratings.size
                        val averageRating =
                            if (totalMoods != 0) floor(totalRating / totalMoods).toInt() else 0
                        if (averageRating in 0..5) {
                            array[averageRating].add(
                                CalendarDay(
                                    startDate.year + 1900,
                                    startDate.month,
                                    startDate.date
                                )
                            )
                        }
                        calendar.add(Calendar.MILLISECOND, 1)
                    }
                    successCallback(array)
                }
            }

            override fun onError(error: ErrorCode) {
                errorCallback(error)
            }
        })
    }



}
