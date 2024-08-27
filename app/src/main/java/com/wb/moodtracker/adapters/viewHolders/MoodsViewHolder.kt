import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Mood
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MoodsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    private val symptomsRecyclerView: RecyclerView = itemView.findViewById(R.id.symptomsRecyclerView)
    private val moodSymptomAdapter: MoodSymptomAdapter = MoodSymptomAdapter(context)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val createOrUpdateTextView: TextView = itemView.findViewById(R.id.CreatedOrUpdatedTextView)
    private val ratingTextView: TextView = itemView.findViewById(R.id.RatingTextView)
    private val ratingImageView: ImageView = itemView.findViewById(R.id.ratingImageView)


    init {
        symptomsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
        symptomsRecyclerView.adapter = moodSymptomAdapter
    }

    fun bind(mood: Mood) {
        val roundDrawable = GradientDrawable()
        roundDrawable.shape = GradientDrawable.RECTANGLE
        roundDrawable.cornerRadius = itemView.resources.getDimension(com.google.android.material.R.dimen.mtrl_bottomappbar_fab_cradle_rounded_corner_radius)
        roundDrawable.setColor(Color.parseColor("#f7f7f7"))
        itemView.background = roundDrawable
        val symptoms = mood.list
        if (symptoms.isNullOrEmpty()) {
            symptomsRecyclerView.visibility = View.GONE
            symptoms?.let { moodSymptomAdapter.updateSymptomsList(it) }
        } else {
            symptomsRecyclerView.visibility = View.VISIBLE
            moodSymptomAdapter.updateSymptomsList(symptoms)
        }
        descriptionTextView.text = mood.description
        var formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localDateTime = LocalDateTime.ofInstant(mood.createDate.toInstant(), ZoneId.systemDefault())
        val formattedDate = formatter.format(localDateTime)
        if (mood.timeZone != ZoneId.systemDefault()) {
            formatter = DateTimeFormatter.ofPattern("z dd/MM/yyyy\n HH:mm:ss").withZone(mood.timeZone)
            val preformattedDate = formatter.format(LocalDateTime.ofInstant(mood.createDate.toInstant(), mood.timeZone))
            createOrUpdateTextView.text = formattedDate.toString() + "\n(${preformattedDate} ${mood.timeZone.toString()})"
        } else {
            createOrUpdateTextView.text = formattedDate.toString()
        }

        ratingTextView.text = ""
        ratingTextView.textSize = 3f
        val color = when (mood.rating) {
            1 -> {
                ratingImageView.setImageResource(R.drawable.ic_rating1)
                Color.RED
            }
            2 -> {
                ratingImageView.setImageResource(R.drawable.ic_rating2)
                Color.parseColor("#FFA500")
            }
            3 -> {
                ratingImageView.setImageResource(R.drawable.ic_rating3)
                Color.YELLOW
            }
            4 -> {
                ratingImageView.setImageResource(R.drawable.ic_rating4)
                Color.parseColor("#90EE90")
            }
            5 -> {
                ratingImageView.setImageResource(R.drawable.ic_rating5)
                Color.GREEN
            }
            else -> Color.TRANSPARENT
        }

        ratingTextView.background = color.toDrawable()
    }
}

