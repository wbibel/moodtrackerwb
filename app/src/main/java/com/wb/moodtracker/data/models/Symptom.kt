package com.wb.moodtracker.data.models

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

@Parcelize
data class Symptom(val text: String, val id: String = UUID.randomUUID().toString()) : Parcelable

