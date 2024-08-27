import android.content.Context
import android.icu.text.SimpleDateFormat
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wb.moodtracker.R
import com.wb.moodtracker.data.models.Notification
import java.util.Locale

class NotificationViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    private val messageTextView: TextView = itemView.findViewById(R.id.notificationMessageTextView)
    private val dateTextView: TextView = itemView.findViewById(R.id.notificationDateTextView)
    private val context = context

    fun bind(notification: Notification) {
        messageTextView.text = bindNotificationName(notification.message)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(notification.createDate)
        dateTextView.text = formattedDate
    }

    private fun bindNotificationName(id: String): String {
        return when (id) {
            "Anorexia Nervosa" -> Html.fromHtml(context.getString(R.string.anorexia)).toString()
            "Bulimia Nervosa" -> context.getString(R.string.bulimia)
            "Binge Eating Disorder" -> context.getString(R.string.binge)
            "Drunkorexia" -> context.getString(R.string.drunkorexia)
            "Avoidant/Restrictive Food Intake Disorder (ARFID)" -> context.getString(R.string.arfid)
            "Pica" -> context.getString(R.string.pica)
            else -> id
        }
    }
}