package com.wb.moodtracker.fragments.mood

import EventDecorator
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.wb.moodtracker.R
import com.wb.moodtracker.adapters.MoodsAdapter
import com.wb.moodtracker.managers.ErrorCode

class MoodFragment : Fragment() {
    private lateinit var moodViewModel: MoodViewModel
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var moodsAdapter: MoodsAdapter

    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDayOfMonth: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var root = inflater.inflate(R.layout.fragment_moods, container, false)
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            root = inflater.inflate(R.layout.fragment_moods_land, container, false)
        } else {
            root = inflater.inflate(R.layout.fragment_moods, container, false)
        }

        setupViews(root)
        return root
    }

    private fun setupViews(root: View) {
        calendarView = root.findViewById(R.id.calendarView)
        recyclerView = root.findViewById(R.id.recyclerView)
        val initialDate = calendarView.currentDate
        selectedYear = initialDate.year
        selectedMonth = initialDate.month
        selectedDayOfMonth = initialDate.day
        calendarView.selectedDate=initialDate
        moodViewModel = ViewModelProvider(this)[MoodViewModel::class.java]
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.moods)

        moodViewModel.readAllRatings(
            successCallback = { array ->
                val eventDecorators = mutableListOf<EventDecorator>()
                for (rating in 1..5) {
                    val eventDates = array[rating].toMutableList()
                    val color = when (rating) {
                        1 -> Color.RED
                        2 -> Color.parseColor("#FFA500")
                        3 -> Color.parseColor("#FFD300")
                        4 -> Color.parseColor("#90EE90")
                        5 -> Color.GREEN
                        else -> Color.TRANSPARENT
                    }
                    val eventDecorator = EventDecorator(color, eventDates)
                    eventDecorators.add(eventDecorator)
                }
                calendarView.addDecorators(eventDecorators)
            },
            errorCallback = { error ->
                Toast.makeText(requireContext(), getErrorMessage(error), Toast.LENGTH_SHORT).show()
            }
        )


        calendarView.setOnDateChangedListener { _, date, _ ->
            selectedYear = date.year
            selectedMonth = date.month + 1
            selectedDayOfMonth = date.day

            moodViewModel.readSelectedDateMood(selectedYear, selectedMonth, selectedDayOfMonth) { moods ->
                if (::moodsAdapter.isInitialized) {
                    moodsAdapter.updateMoodList(moods)
                } else {
                    moodsAdapter = MoodsAdapter(moods, requireContext().applicationContext)
                    recyclerView.adapter = moodsAdapter
                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMoodListObserver()
        setupBackPressedCallback()
    }

    private fun setupMoodListObserver() {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
    }

    private fun setupBackPressedCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null
    }

    override fun onResume() {
        super.onResume()
        if (::moodsAdapter.isInitialized) {
            recyclerView.adapter = moodsAdapter
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
            else -> {""}
        }
    }

}
