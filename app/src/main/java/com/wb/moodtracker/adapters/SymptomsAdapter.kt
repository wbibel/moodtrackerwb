
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Symptom

class SymptomsAdapter(private val context: Context,
    private val symptomList: List<Symptom>,
    private var selectedSymptomList: MutableCollection<Symptom>? = null,
    private val onCheckboxClickListener: ((Symptom, Boolean) -> Unit)? = null
) : RecyclerView.Adapter<SymptomsViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomsViewHolder {
        val symptomsView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_symptom, parent, false)
        return SymptomsViewHolder(symptomsView, context)
    }

    override fun onBindViewHolder(holder: SymptomsViewHolder, position: Int) {
        val item = symptomList[position]
        val isSelected = selectedSymptomList?.contains(item) ?: false
        holder.bind(item, isSelected, onCheckboxClickListener)
        if (isSelected)
            holder.itemView.setBackgroundColor(Color.GREEN)
        else
            holder.itemView.setBackgroundColor(Color.WHITE)

    }

    override fun getItemCount(): Int {
        return symptomList.size
    }


}
