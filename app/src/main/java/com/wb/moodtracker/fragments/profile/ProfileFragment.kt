package com.wb.moodtracker.fragments.profile

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Profile
import com.wb.moodtracker.managers.ErrorCode
import com.wb.moodtracker.managers.ProfileCallback
import java.util.Locale

class ProfileFragment : Fragment() {
    private lateinit var email: TextView
    private lateinit var detailedName: TextView
    private lateinit var userAge: TextView
    private lateinit var firstEntry: TextView
    private lateinit var weightTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var profilePhoto: ImageView
    private lateinit var editButton: Button
    private lateinit var saveButton: Button
    private var selectedWeight: Int = 0
    private var selectedHeight: Int = 0

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigation_moods)
            return rootView
        }
        profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]
        email = rootView.findViewById(R.id.emailTextviewProfile)
        detailedName = rootView.findViewById(R.id.detailedTextviewProfile)
        userAge = rootView.findViewById(R.id.userAgeTextviewProfile)
        firstEntry = rootView.findViewById(R.id.firstEntryTextviewProfile)
        weightTextView = rootView.findViewById(R.id.WeightTextviewProfile)
        heightTextView = rootView.findViewById(R.id.HeightTextviewProfile)
        profilePhoto = rootView.findViewById(R.id.imageViewAccountImageProfile)
        editButton = rootView.findViewById(R.id.editButtonProfile)
        saveButton = rootView.findViewById(R.id.saveButtonProfile)
        val weightNumberPicker: NumberPicker = rootView.findViewById(R.id.weightNumberPicker)
        weightNumberPicker.minValue = 30
        weightNumberPicker.maxValue = 200
        weightNumberPicker.setOnValueChangedListener { _, _, newVal ->
            selectedWeight = newVal
        }

        val heightNumberPicker: NumberPicker = rootView.findViewById(R.id.heightNumberPicker)
        heightNumberPicker.minValue = 100
        heightNumberPicker.maxValue = 250
        heightNumberPicker.setOnValueChangedListener { _, _, newVal ->
            selectedHeight = newVal
        }
        editButton.setOnClickListener {
            if (weightNumberPicker.visibility == View.VISIBLE && heightNumberPicker.visibility == View.VISIBLE) {
                weightNumberPicker.visibility = View.GONE
                heightNumberPicker.visibility = View.GONE
                weightTextView.text = getString(R.string.weight) + selectedWeight + getString(
                    R.string.kg)
                heightTextView.text = getString(R.string.height) + selectedHeight + getString(
                    R.string.cm)
                saveButton.visibility = View.GONE
                editButton.text = getString(R.string.edit)
            } else {
                weightNumberPicker.visibility = View.VISIBLE
                heightNumberPicker.visibility = View.VISIBLE
                saveButton.visibility = View.VISIBLE
                editButton.text = getString(R.string.hide_edit)
            }
        }
        saveButton.setOnClickListener {
            profileViewModel.updateProfile(selectedHeight, selectedWeight,
                {},{}
            )
        }
        updateUI()
        weightNumberPicker.value= selectedHeight-77
        heightNumberPicker.value = selectedWeight+30
        return rootView
    }

    private fun updateUI() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            profileViewModel.readProfile(object : ProfileCallback {
                override fun onProfileLoaded(profile: Profile?) {
                    if (profile != null) {
                        selectedWeight=profile.weight
                        selectedHeight=profile.height
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault())
                        val formattedDate = dateFormat.format(profile.firstDate)
                        firstEntry.text = getString(R.string.last_update) + formattedDate
                        weightTextView.text = getString(R.string.weight) + profile.weight + getString(
                                                    R.string.kg)
                        heightTextView.text = getString(R.string.height) + profile.height + getString(
                                                    R.string.cm)
                    } else {
                        Toast.makeText(context, getString(R.string.profile_is_not_available), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(error: ErrorCode) {
                }
            })
            email.text = getString(R.string.email_prof) + currentUser.email
            detailedName.text = getString(R.string.detailed_name_prof) + currentUser.displayName
            profilePhoto.apply { Picasso.get().load(currentUser.photoUrl).into(this) }
        }
    }
}
