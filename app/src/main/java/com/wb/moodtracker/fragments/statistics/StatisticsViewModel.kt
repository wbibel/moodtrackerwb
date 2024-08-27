package com.wb.moodtracker.fragments.statistics

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.wb.moodtracker.data.models.Mood
import com.wb.moodtracker.managers.DataManager
import com.wb.moodtracker.managers.ErrorCode
import com.wb.moodtracker.managers.MoodsCallback
import java.util.Date

class StatisticsViewModel : ViewModel() {


    private val dataManager = DataManager()


    fun getYearLineGraphEntry(
        successCallback: (List<Entry>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        dataManager.readMoodsLastYear(object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                if (moods.isNotEmpty()) {
                    val calendar = Calendar.getInstance()
                    calendar.time = Date()
                    calendar.add(Calendar.YEAR, -1)

                    val entries = mutableListOf<Entry>()
                    for (i in 0 until 12) {
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startDate = calendar.time
                        calendar.add(Calendar.MONTH, 1)
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        calendar.set(Calendar.MILLISECOND, 999)
                        val endDate = calendar.time

                        val ratings = mutableListOf<Float>()
                        var totalRating = 0f
                        var totalDays = 0
                        for (mood in moods) {
                            if (mood.createDate >= startDate && mood.createDate <= endDate) {
                                ratings.add(mood.rating.toFloat())
                                totalRating += mood.rating.toFloat()
                                totalDays++
                            }
                        }
                        val averageRating = if (totalDays != 0) totalRating / totalDays else 0f
                        entries.add(Entry(i.toFloat(), averageRating))
                        calendar.add(Calendar.MILLISECOND, 1)
                        val monthLabel = SimpleDateFormat("MMM").format(startDate)
                        val yearLabel = SimpleDateFormat("yyyy").format(startDate)
                        entries.last().data = "$monthLabel $yearLabel"
                    }
                    successCallback(entries)
                }
            }

            override fun onError(error: ErrorCode) {
            }
        })
    }
    fun getMonthLineGraphEntry(
        successCallback: (List<Entry>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        dataManager.readMoodsLastMonth(object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                if (moods.isNotEmpty()) {
                    val currentDate = Date()
                    val calendar = Calendar.getInstance()
                    calendar.time = currentDate
                    calendar.add(Calendar.DAY_OF_MONTH, -30)

                    val entries = mutableListOf<Entry>()
                    for (i in 0 until 30) {
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

                        val ratings = mutableListOf<Float>()
                        var totalRating = 0f
                        var totalDays = 0
                        for (mood in moods) {
                            if (mood.createDate >= startDate && mood.createDate <= endDate) {
                                ratings.add(mood.rating.toFloat())
                                totalRating += mood.rating.toFloat()
                                totalDays++
                            }
                        }
                        val averageRating = if (totalDays != 0) totalRating / totalDays else 0f
                        entries.add(Entry(i.toFloat(), averageRating))
                        calendar.add(Calendar.MILLISECOND, 1)
                        val dayLabel = SimpleDateFormat("dd").format(startDate)
                        val monthLabel = SimpleDateFormat("MMM").format(startDate)
                        entries.last().data = "$dayLabel $monthLabel "
                    }
                    successCallback(entries)
                }
            }

            override fun onError(error: ErrorCode) {
            }
        })
    }
    fun getWeekLineGraphEntry(
        successCallback: (List<Entry>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        dataManager.readMoodsLastWeek(object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                if (moods.isNotEmpty()) {
                    val currentDate = Date()
                    val calendar = Calendar.getInstance()
                    calendar.time = currentDate
                    calendar.add(Calendar.DAY_OF_MONTH, -6)
                    val entries = mutableListOf<Entry>()
                    for (i in 0 until 7) {
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

                        val ratings = mutableListOf<Float>()
                        var totalRating = 0f
                        var totalDays = 0
                        for (mood in moods) {
                            if (mood.createDate >= startDate && mood.createDate <= endDate) {
                                ratings.add(mood.rating.toFloat())
                                totalRating += mood.rating.toFloat()
                                totalDays++
                            }
                        }
                        val averageRating = if (totalDays != 0) totalRating / totalDays else 0f
                        entries.add(Entry(i.toFloat(), averageRating))
                        calendar.add(Calendar.MILLISECOND, 1)
                        val dayLabel = SimpleDateFormat("EEE dd").format(startDate)
                        val monthLabel = SimpleDateFormat("MMM").format(startDate)
                        entries.last().data = "$dayLabel $monthLabel"
                    }
                    successCallback(entries)
                }
            }

            override fun onError(error: ErrorCode) {
                //errorCallback(error)
            }
        })
    }
    fun getYearBarGraphEntry(
        successCallback: (List<BarEntry>) -> Unit
    ) {
        val dataManager= DataManager()
        dataManager.readMoodsLastYear(object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                val ratings = moods.map { it.rating }
                val ratingCounts = IntArray(5)
                for (rating in ratings) {
                    if (rating in 1..5) {
                        ratingCounts[rating - 1]++
                    }
                }
                val barEntries = ratingCounts.mapIndexed { index, count ->
                    BarEntry(index.toFloat() + 1, count.toFloat())
                }


                successCallback(barEntries)
            }
            override fun onError(errorCode: ErrorCode) {
            }
        })
    }
    fun getMonthBarGraphEntry(
        successCallback: (List<BarEntry>) -> Unit
    ) {
        val dataManager= DataManager()
        dataManager.readMoodsLastMonth(object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                val ratings = moods.map { it.rating }
                val ratingCounts = IntArray(5)
                for (rating in ratings) {
                    if (rating in 1..5) {
                        ratingCounts[rating - 1]++
                    }
                }
                val barEntries = ratingCounts.mapIndexed { index, count ->
                    BarEntry(index.toFloat() + 1, count.toFloat())
                }


                successCallback(barEntries)
            }
            override fun onError(errorCode: ErrorCode) {
            }
        })
    }
    fun getWeekBarGraphEntry(
        successCallback: (List<Entry>) -> Unit
    ) {
        val dataManager= DataManager()
        dataManager.readMoodsLastWeek (object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                val ratings = moods.map { it.rating }
                val ratingCounts = IntArray(5)
                for (rating in ratings) {
                    if (rating in 1..5) {
                        ratingCounts[rating - 1]++
                    }
                }
                val barEntries = ratingCounts.mapIndexed { index, count ->
                    BarEntry(index.toFloat() + 1, count.toFloat())
                }
                successCallback(barEntries)
            }
            override fun onError(errorCode: ErrorCode) {
            }
        })
    }





}