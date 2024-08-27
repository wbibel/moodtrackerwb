package com.wb.moodtracker.adapters.viewHolders

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Symptom

class MoodSymptomViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val symptomTextView: TextView = itemView.findViewById(R.id.moodSymptom2TextView)

    fun bind(symptom: Symptom) {
        symptomTextView.text = bindSymptomName(symptom.id)
        val roundDrawable = GradientDrawable()
        roundDrawable.shape = GradientDrawable.RECTANGLE
        roundDrawable.cornerRadius = itemView.resources.getDimension(com.google.android.material.R.dimen.mtrl_bottomappbar_fab_cradle_rounded_corner_radius)
        roundDrawable.setColor(Color.RED)
        itemView.background = roundDrawable
    }

    private fun bindSymptomName(id: String): String {
        return when (id) {
            "Extreme restriction of food intake unrelated to illness" -> context.getString(R.string.symptom_1)
            "Thinking about food, weight, and body appearance" -> context.getString(R.string.symptom_2)
            "Unrealistic perception of own body" -> context.getString(R.string.symptom_3)
            "Consuming a large amount of food in a short period" -> context.getString(R.string.symptom_4)
            "Vomiting" -> context.getString(R.string.symptom_5)
            "Use of laxatives" -> context.getString(R.string.symptom_6)
            "Taking actions to revert to previous weight" -> context.getString(R.string.symptom_7)
            "Doing everything to avoid gaining weight" -> context.getString(R.string.symptom_8)
            "Stress related to participating in social eating experiences" -> context.getString(R.string.symptom_9)
            "Loss of control over the amount of food consumed " -> context.getString(R.string.symptom_10)
            "Strong feeling of shame after a large meal" -> context.getString(R.string.symptom_11)
            "Frequent checking of body weight in a short period" -> context.getString(R.string.symptom_12)
            "Checking body measurements using a measuring tape or mirror reflections" -> context.getString(R.string.symptom_13)
            "Avoiding certain food groups or categories" -> context.getString(R.string.symptom_14)
            "Consuming substances of low nutritional value or unusual items" -> context.getString(R.string.symptom_15)
            "Difficulty swallowing food unrelated to illness" -> context.getString(R.string.symptom_16)
            "Feeling of blockage in the throat while eating unrelated to illness" -> context.getString(R.string.symptom_17)
            "Coughing or choking during meals unrelated to illness" -> context.getString(R.string.symptom_18)
            "Restricting meals to drink alcohol" -> context.getString(R.string.symptom_19)
            "Eliminating highly processed foods from the diet" -> context.getString(R.string.symptom_20)
            else -> ""
        }
    }
}
