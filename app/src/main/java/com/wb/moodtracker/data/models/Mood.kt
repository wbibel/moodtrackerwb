package com.wb.moodtracker.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZoneId
import java.util.*

@Parcelize
data class Mood(
    val description: String,
    val createDate: Date,
    val timeZone: ZoneId?,
    val rating: Int,
    val list: List<Symptom>?,
    val key: String? = null
) : Parcelable {
}
