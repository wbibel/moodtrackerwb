package com.wb.moodtracker.adapters

import MoodsViewHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Mood
import com.wb.moodtracker.fragments.mood.MoodFragmentDirections

class MoodsAdapter(private var moodList: List<Mood>, context: Context) : RecyclerView.Adapter<MoodsViewHolder>() {
    private val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodsViewHolder {
        val moodView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_mood, parent, false)
        return MoodsViewHolder(moodView, context)
    }

    override fun onBindViewHolder(holder: MoodsViewHolder, position: Int) {
        val mood = moodList[position]
        holder.bind(mood)
        holder.itemView.setOnClickListener {
            val action = MoodFragmentDirections.actionNavigationMoodsToNavigationAddMood(mood)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return moodList.size
    }

    fun updateMoodList(newMoodList: List<Mood>) {
        moodList = newMoodList
        notifyDataSetChanged()
    }
}
