package com.wb.moodtracker.fragments.statistics

import android.content.res.Configuration
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.google.android.material.button.MaterialButtonToggleGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.wb.moodtracker.R
import com.wb.moodtracker.databinding.FragmentStatisticsBinding
import java.util.Locale

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var statisticsViewModel: StatisticsViewModel

    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        statisticsViewModel =
            ViewModelProvider(this)[StatisticsViewModel::class.java]

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigation_moods)
            return root
        }
        lineChart = root.findViewById(R.id.lineChart)
        lineChart.description=null
        barChart = root.findViewById(R.id.barChart)
        toggleButtonGroup = root.findViewById(R.id.toggleButtonGroup)

        toggleButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (isChecked) {
                lineChart.visibility= VISIBLE
                barChart.visibility= VISIBLE
                binding.averageLayout.visibility= VISIBLE
                binding.maxLayout.visibility= VISIBLE
                binding.minLayout.visibility= VISIBLE
                when (checkedId) {
                    R.id.btnYear -> refreshFragmentBasedOnButton("Year")
                    R.id.btnMonth -> refreshFragmentBasedOnButton("Month")
                    R.id.btnWeek -> refreshFragmentBasedOnButton("Week")
                }
            } else {
                if (toggleButtonGroup.checkedButtonId == View.NO_ID) {
                    lineChart.visibility= GONE
                    barChart.visibility= GONE
                    binding.averageLayout.visibility= GONE
                    binding.maxLayout.visibility= GONE
                    binding.minLayout.visibility= GONE
                }
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun refreshBarChart(barGraphEntry: BarData) {
        barChart.xAxis.granularity=1f
        barChart.axisRight.isEnabled = false
        val barDataSet = barGraphEntry.getDataSetByIndex(0) as? BarDataSet
        barDataSet?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
        barChart.axisLeft
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.granularity = 1f
        barChart.description=null
        val colors = mutableListOf<Int>()
        for (i in 0 until (barDataSet?.entryCount ?: 0)) {
            val color = when (i % 5) {
                0 -> Color.RED // First bar is red
                1 -> Color.parseColor("#FFA500")
                2 -> Color.YELLOW
                3 -> Color.parseColor("#90EE90")
                4 -> Color.GREEN
                else -> Color.parseColor("#FFA500")
            }
            colors.add(color)
        }
        barDataSet?.colors = colors
        barChart.apply { data=barGraphEntry }.invalidate()
    }

    private fun refreshFragmentBasedOnButton(selectedButton: String) {
        when (selectedButton) {
            "Year" -> {
                statisticsViewModel.getYearLineGraphEntry({ entries ->
                    updateChartWithEntriesLastYear(entries)
                    showAverageMinMax(entries)
                }, { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                })
                statisticsViewModel.getYearBarGraphEntry { barEntries ->
                    val barDataSet = BarDataSet(barEntries, getString(R.string.rating_counts))
                    barDataSet.valueTextColor = Color.BLACK
                    refreshBarChart(BarData(barDataSet))
                }
            }
            "Month" -> {
                statisticsViewModel.getMonthLineGraphEntry({ entries ->
                    updateChartWithEntriesLastMonth(entries)
                    showAverageMinMax(entries)
                }, { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                })
                statisticsViewModel.getMonthBarGraphEntry { barEntries ->
                    val barDataSet = BarDataSet(barEntries, getString(R.string.rating_counts))
                    barDataSet.valueTextColor = Color.BLACK
                    refreshBarChart(BarData(barDataSet))
                }

            }
            "Week" -> {
                statisticsViewModel.getWeekLineGraphEntry({ entries ->
                    updateChartWithEntriesLastYear(entries)
                    showAverageMinMax(entries)
                }, { error ->
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()

                })
                statisticsViewModel.getWeekBarGraphEntry { barEntries ->
                    val barDataSet = BarDataSet(barEntries as MutableList<BarEntry>? , getString(R.string.rating_counts))
                    barDataSet.valueTextColor = Color.BLACK
                    refreshBarChart(BarData(barDataSet))
                }
            }
            else -> {

            }
        }
    }


    private fun showAverageMinMax(entries: List<Entry>) {
        val nonZeroValues = entries.filter { it.y != 0f }.map { it.y }
        if (nonZeroValues.isNotEmpty()) {
            val average = nonZeroValues.average()
            val min = nonZeroValues.minOrNull()
            val max = nonZeroValues.maxOrNull()

            binding.averageTextview.text = getString(R.string.average_value_2f).format(average)
            binding.minTextview.text = getString(R.string.minimum_value_2f).format(min)
            binding.maxTextview.text = getString(R.string.maximum_value_2f).format(max)
        } else {
            binding.averageTextview.text = getString(R.string.average_value_n_a)
            binding.minTextview.text = getString(R.string.minimum_value_n_a)
            binding.maxTextview.text = getString(R.string.maximum_value_n_a)
        }
    }
    private fun updateChartWithEntriesLastYear(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, getString(R.string.moods_entry)).apply {
            color = Color.GREEN
            valueTextColor = Color.BLACK
            lineWidth = 2.5f
        }
        val lineData = LineData(dataSet)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false
        val yAxis: YAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 5f
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return (entries.getOrNull(index)?.data ?: "").toString()
            }
        }
        lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val monthYearLabel = e.data as? String
                    val value = e.y.toString()
                    val toastMessage = buildString {
        append(monthYearLabel)
        append(getString(R.string.mood_average_value))
        append(value)
    }
                    Toast.makeText(
                        context,
                        toastMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onNothingSelected() {}
        })
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun updateChartWithEntriesLastMonth(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, getString(R.string.moods))
        dataSet.color = Color.GREEN
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2.5f
        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false
        val lineData = LineData(dataSet)
        val xAxis: XAxis = lineChart.xAxis

        val yAxis: YAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 5f
        yAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormatter = SimpleDateFormat("dd MMM", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                val currentDate = Calendar.getInstance().time
                val startDate = Calendar.getInstance().apply {
                    time = currentDate
                    add(Calendar.MONTH, -1)
                }.time
                val formattedDate = Calendar.getInstance().apply {
                    time = startDate
                    add(Calendar.DAY_OF_MONTH, index)
                }.time
                return dateFormatter.format(formattedDate)
            }
        }
        lineChart.data = lineData
        lineChart.invalidate()
    }
}
