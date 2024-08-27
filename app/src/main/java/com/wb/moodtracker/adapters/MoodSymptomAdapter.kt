import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wb.moodtracker.R
import com.wb.moodtracker.adapters.viewHolders.MoodSymptomViewHolder
import com.wb.moodtracker.data.models.Symptom

class MoodSymptomAdapter(private val context: Context) : RecyclerView.Adapter<MoodSymptomViewHolder>() {
    private var symptomList: List<Symptom> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodSymptomViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_mood_symptom, parent, false)
        return MoodSymptomViewHolder(itemView, context)
    }

    override fun onBindViewHolder(holder: MoodSymptomViewHolder, position: Int) {
        val symptom = symptomList[position]
        holder.bind(symptom)
    }

    override fun getItemCount(): Int {
        return symptomList.size
    }

    fun updateSymptomsList(newSymptomList: List<Symptom>) {
        symptomList = newSymptomList
    }
}
