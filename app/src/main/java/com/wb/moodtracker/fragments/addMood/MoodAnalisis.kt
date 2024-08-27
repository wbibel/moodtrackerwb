package com.wb.moodtracker.fragments.addMood

import com.wb.moodtracker.data.models.Mood
import com.wb.moodtracker.data.models.Notification
import com.wb.moodtracker.data.models.Profile
import com.wb.moodtracker.managers.DataManager
import com.wb.moodtracker.managers.ErrorCode
import com.wb.moodtracker.managers.MoodsCallback
import com.wb.moodtracker.managers.ProfileCallback
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class MoodAnalisis {
    private val dataManager = DataManager()

    fun analyzeMoods() {
        dataManager.readMoodsLastMonth(object : MoodsCallback {
            override fun onMoodsLoaded(moods: List<Mood>) {
                if (moods.isNotEmpty()) {
                    val disorderCounts = analyzeSymptomsForEatingDisorders(moods)
                    val totalDisorderCounts = sumUpDisorderCounts(disorderCounts)
                    val disease = findMostCommonDisorder(totalDisorderCounts)
                    when (disease) {
                        "Anorexia Nervosa" -> {
                            dataManager.readProfile(object : ProfileCallback {
                                override fun onProfileLoaded(profile: Profile?) {
                                    if (profile != null) {
                                        val bmi = calculateBMI(profile)
                                        if (bmi < 18.5) {
                                            val notification= Notification(disease)
                                            dataManager.createNotification(notification)
                                        }
                                    }
                                }
                                override fun onError(error: ErrorCode) {
                                }
                            })
                        }
                        "Bulimia Nervosa" -> {
                            val symptomIdsToCheck = setOf(
                                "Consuming a large amount of food in a short period",
                                "Loss of control over the amount of food consumed"
                            )
                            val symptomCounts = mutableMapOf<String, Int>()
                            val oneMonthAgo = LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                            val oneMonthAgoDate = Date.from(oneMonthAgo)
                            for (mood in moods) {
                                if (mood.createDate >= oneMonthAgoDate) {
                                    mood.list?.forEach { symptom ->
                                        if (symptom.id in symptomIdsToCheck) {
                                            symptomCounts[symptom.id] = (symptomCounts[symptom.id] ?: 0) + 1
                                        }
                                    }
                                }
                            }
                            val weeklySymptoms = listOf(
                                "Consuming a large amount of food in a short period",
                                "Vomiting",
                                "Use of laxatives"
                            )
                            val recurringWeeklySymptoms = weeklySymptoms.any { symptomId ->
                                symptomCounts[symptomId] ?: 0 >= 4
                            }
                            if (recurringWeeklySymptoms) {
                                val notification= Notification(disease)
                                dataManager.createNotification(notification)
                            }
                        }
                        else -> {
                            val notification= Notification(disease)
                            dataManager.createNotification(notification)
                        }
                    }

                }
            }
            override fun onError(error: ErrorCode) {
            }
        })
    }

    private fun analyzeSymptomsForEatingDisorders(moods: List<Mood>): Map<String, Int> {
        val symptomMapping = mapOf(
            "Extreme restriction of food intake unrelated to illness" to listOf("Anorexia Nervosa"),
            "Thinking about food, weight, and body appearance" to listOf("Anorexia Nervosa"),
            "Unrealistic perception of own body" to listOf("Anorexia Nervosa","Bulimia Nervosa"),
            "Consuming a large amount of food in a short period" to listOf("Binge Eating Disorder","Bulimia Nervosa"),
            "Vomiting" to listOf("Bulimia Nervosa","Drunkorexia","Anorexia Nervosa"),
            "Use of laxatives" to listOf("Bulimia Nervosa","Drunkorexia"),
            "Taking actions to revert to previous weight" to listOf("Anorexia Nervosa","Bulimia Nervosa","Drunkorexia"),
            "Doing everything to avoid gaining weight" to listOf("Anorexia Nervosa","Bulimia Nervosa","Drunkorexia"),
            "Stress related to participating in social eating experiences" to listOf("Avoidant/Restrictive Food Intake Disorder (ARFID)"),
            "Loss of control over the amount of food consumed" to listOf("Binge Eating Disorder","Bulimia Nervosa" ),
            "Strong feeling of shame after a large meal" to listOf("Binge Eating Disorder"),
            "Frequent checking of body weight in a short period" to listOf("Bulimia Nervosa"),
            "Checking body measurements using a measuring tape or mirror reflections" to listOf("Anorexia Nervosa"),
            "Avoiding certain food groups or categories" to listOf("Avoidant/Restrictive Food Intake Disorder (ARFID)"),
            "Consuming substances of low nutritional value or unusual items" to listOf("Pica"),
            "Difficulty swallowing food unrelated to illness" to listOf("Avoidant/Restrictive Food Intake Disorder (ARFID)"),
            "Feeling of blockage in the throat while eating unrelated to illness" to listOf("Avoidant/Restrictive Food Intake Disorder (ARFID)"),
            "Coughing or choking during meals unrelated to illness" to listOf("Avoidant/Restrictive Food Intake Disorder (ARFID)"),
            "Restricting meals to drink alcohol" to listOf("Drunkorexia")
        )
        val disorderCounts = mutableMapOf<String, Int>()
        for (mood in moods) {
            val symptoms = mood.list ?: emptyList()
            for (symptom in symptoms) {
                val disorders = symptomMapping[symptom.id]
                if (disorders != null) {
                    for (disorder in disorders) {
                        disorderCounts[disorder] = (disorderCounts[disorder] ?: 0) + 1
                    }
                }
            }
        }
        return disorderCounts
    }

    private fun sumUpDisorderCounts(disorderCounts: Map<String, Int>): Map<String, Int> {
        val totalDisorderCounts = mutableMapOf<String, Int>()
        for ((disorder, count) in disorderCounts) {
            val disorderName = disorder.substringBefore("(").trim()
            totalDisorderCounts[disorderName] = (totalDisorderCounts[disorderName] ?: 0) + count
        }
        return totalDisorderCounts
    }

    private fun findMostCommonDisorder(disorderCounts: Map<String, Int>): String {
        return disorderCounts.entries.maxByOrNull { it.value }?.key ?: ""
    }
    private fun calculateBMI(profile: Profile): Double {
        val height = profile.height
        val weight = profile.weight
        if (height > 0 && weight > 0) {
            val heightInMeters = height.toDouble() / 100.0
            return weight.toDouble() / (heightInMeters * heightInMeters)
        }
        return 0.0
    }

}
