package com.wb.moodtracker.fragments.addMood

import SymptomsAdapter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jinatonic.confetti.CommonConfetti
import com.google.firebase.auth.FirebaseAuth
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Mood
import com.wb.moodtracker.data.models.Symptom
import com.wb.moodtracker.databinding.FragmentAddMoodsBinding
import com.wb.moodtracker.managers.ErrorCode
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class AddMoodFragment : Fragment() {

    private var _binding: FragmentAddMoodsBinding? = null
    private val binding get() = _binding!!

    private lateinit var addMoodViewModel: AddMoodViewModel

    private lateinit var descriptionInput: EditText
    private lateinit var recyclerViewSymptoms: RecyclerView
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var symptomsAdapter: SymptomsAdapter
    private lateinit var showSymptomsCheckbox: CheckBox
    private lateinit var showPersonalDetailsCheckbox: CheckBox
    private lateinit var descriptionTextViewNonEditable: TextView
    private lateinit var ratingTextView: TextView
    private lateinit var customRatingBar: CustomRatingBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddMoodsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigation_moods)
            return root
        }
        descriptionInput = binding.descriptionInput
        recyclerViewSymptoms = binding.recyclerViewSymptoms
        showSymptomsCheckbox = binding.checkboxShowSymptoms
        descriptionTextViewNonEditable = binding.descriptionTextViewNonEditable
        showPersonalDetailsCheckbox = binding.checkboxShowPersonalDetails
        submitButton = binding.submitButton
        deleteButton = binding.deleteButton
        ratingTextView = binding.ratingTextView
        customRatingBar = binding.customRatingBar

        addMoodViewModel = ViewModelProvider(this)[AddMoodViewModel::class.java]
        addMoodViewModel.symptomList.observe(viewLifecycleOwner) { symptoms ->
            refreshRecycler(symptoms)
        }

        setCheckboxListeners()

        if (arguments?.isEmpty != true) {
            val args: AddMoodFragmentArgs? = arguments?.let { AddMoodFragmentArgs.fromBundle(it) }
            args?.selectedMood?.let { selectedMood ->
                selectedMoodUpgradeUI(selectedMood)
                val timeDifference = Date().time - selectedMood.createDate.time
                if (TimeUnit.MILLISECONDS.toMinutes(timeDifference) < 15) {
                    submitButton.visibility = View.VISIBLE
                    submitButton.setOnClickListener {
                        if (customRatingBar.getRating() in 1..5)
                        {
                            val moodUpdate = Mood(
                                descriptionInput.text.toString(),
                                selectedMood.createDate,
                                selectedMood.timeZone,
                                customRatingBar.getRating(),
                                addMoodViewModel.selectedSymptomList.toList(),
                                selectedMood.key
                            )

                            addMoodViewModel.updateMood(moodUpdate,
                                successCallback = {
                                    createConfettiEffect()
                                    Toast.makeText(requireContext(), getString(R.string.mood_updated_successfully), Toast.LENGTH_SHORT).show()
                                },
                                errorCallback = { error ->
                                    Toast.makeText(requireContext(), getErrorMessage(error), Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        else {
                            Toast.makeText(requireContext(), getString(R.string.set_rating_value_from_between_1_and_5), Toast.LENGTH_SHORT).show()
                        }
                    }
                    deleteButton.setOnClickListener {
                        addMoodViewModel.deleteMood(selectedMood,
                            successCallback = {
                                Toast.makeText(requireContext(), getString(R.string.mood_deleted_successfully), Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            },
                            errorCallback = { error ->
                                Toast.makeText(requireContext(), getErrorMessage(error), Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                } else {
                    customRatingBar.setOnRatingClickListener(object : CustomRatingBar.OnRatingClickListener {
                        override fun onRatingClicked(rating: Int) {
                            customRatingBar.setRating(selectedMood.rating)
                        }
                    })
                    descriptionInput.isFocusable = false
                    descriptionInput.isClickable = false
                    descriptionInput.isLongClickable = false
                    descriptionInput.isCursorVisible = false
                    descriptionInput.hint=selectedMood.description
                    descriptionInput.setHintTextColor(Color.BLACK)
                    descriptionInput.setText(selectedMood.description,TextView.BufferType.EDITABLE)
                    descriptionInput.invalidate()
                    deleteButton.visibility= GONE
                }
            }
        } else {
            deleteButton.visibility=GONE
            submitButton.setOnClickListener {
                if (customRatingBar.getRating() in 1..5){
                    val moodCreate = Mood(
                        descriptionInput.text.toString(),
                        Calendar.getInstance().time,
                        TimeZone.getTimeZone(ZoneId.systemDefault()).toZoneId(),
                        customRatingBar.getRating(),
                        addMoodViewModel.selectedSymptomList.toList()
                    )

                    addMoodViewModel.createMood(moodCreate,
                        successCallback = {
                            createConfettiEffect()
                            Toast.makeText(requireContext(), getString(R.string.mood_added_successfully), Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        },
                        errorCallback = { error ->
                            Toast.makeText(requireContext(), getErrorMessage(error), Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    )
                }
                else {
                    Toast.makeText(requireContext(), getString(R.string.set_rating_value_from_between_1_and_5), Toast.LENGTH_SHORT).show()
                }
            }

        }

        return root
    }

    private fun selectedMoodUpgradeUI(selectedMood: Mood) {
        showSymptomsCheckbox.apply {
            visibility = View.VISIBLE
            text = context.getString(R.string.show_symptoms_previously_checked)
        }
        recyclerViewSymptoms.visibility = View.VISIBLE
        showPersonalDetailsCheckbox.apply {
            visibility = View.VISIBLE
            text = context.getString(R.string.show_description)
        }
        descriptionTextViewNonEditable.visibility = GONE
        submitButton.apply {
            text = context.getString(R.string.change_selected_mood)
            visibility = GONE
        }
        descriptionInput.apply {
            visibility = View.VISIBLE
            hint = context.getString(R.string.edit_description)
            setText(selectedMood.description)
        }
        ratingTextView.visibility = GONE
        recyclerViewSymptoms.visibility = GONE

        customRatingBar.setRating(selectedMood.rating)
        selectedMood.list?.let { symptoms ->
            addMoodViewModel.updateSymptomList(symptoms)
        }
    }

    private fun createConfettiEffect() {
        CommonConfetti.rainingConfetti(binding.root, intArrayOf(Color.RED, Color.YELLOW, Color.GREEN))
            .infinite()
            .setVelocityY(200f, 1000f)
            .setEmissionDuration(500L)
            .setEmissionRate(1000f)
            .animate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //resetViews()
        _binding = null
        addMoodViewModel.selectedSymptomList.clear()
    }

    override fun onResume() {
        super.onResume()
        resetViews()
        onDestroy()
    }

    private fun resetViews() {
        if (_binding != null) {
            descriptionInput.setText("")
            recyclerViewSymptoms.visibility = GONE
            showSymptomsCheckbox.isChecked = false
            showPersonalDetailsCheckbox.isChecked = false
            descriptionInput.visibility = GONE
        }
    }


    private fun refreshRecycler(symptomList: List<Symptom>) {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerViewSymptoms.layoutManager = layoutManager
        symptomsAdapter = SymptomsAdapter(requireContext().applicationContext ,symptomList, addMoodViewModel.selectedSymptomList) { symptom , isChecked ->
            if (isChecked)
                addMoodViewModel.selectedSymptomList.add(symptom)
            else
                addMoodViewModel.selectedSymptomList.remove(symptom)
        }
        recyclerViewSymptoms.adapter = symptomsAdapter
    }

    private fun setCheckboxListeners() {
        showSymptomsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else GONE
            recyclerViewSymptoms.visibility = visibility
        }

        showPersonalDetailsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else GONE
            descriptionInput.visibility = visibility
        }
    }

    private fun getErrorMessage(errorCode: ErrorCode): String {
        return when (errorCode) {
            ErrorCode.EMR -> getString(R.string.failed_to_retrieve_data)
            ErrorCode.EMA -> getString(R.string.user_not_logged_in)
            ErrorCode.EMC -> getString(R.string.failed_to_add_mood)
            ErrorCode.EMU -> getString(R.string.failed_to_update_mood)
            ErrorCode.EMAU -> getString(R.string.user_not_logged_in_or_mood_key_is_null)
            ErrorCode.EMC15 -> getString(R.string.mood_cannot_be_created_you_can_only_create_a_mood_once_every_15_minutes)
            ErrorCode.EMD -> getString(R.string.mood_can_t_be_deleted)
            else -> ""

        }
    }
}
