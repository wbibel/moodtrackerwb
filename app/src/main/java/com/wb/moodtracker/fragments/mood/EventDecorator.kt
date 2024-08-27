import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.*

class EventDecorator(private val color: Int, dates: List<CalendarDay>) : DayViewDecorator {

    private val eventDates: HashSet<CalendarDay> = HashSet(dates)
    private val selectedDayColor: Int = Color.parseColor("#FF4081")

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return eventDates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(6f, selectedDayColor))
        val backgroundDrawable = createCircularDrawable(color)
        view.setBackgroundDrawable(backgroundDrawable)
        view.addSpan(ForegroundColorSpan(Color.WHITE))
        view.addSpan(RelativeSizeSpan(1.2f))
        view.addSpan(StyleSpan(Typeface.BOLD))
    }

    private fun createCircularDrawable(color: Int): Drawable {
        val shapeDrawable = GradientDrawable()
        shapeDrawable.shape = GradientDrawable.OVAL
        shapeDrawable.setColor(color)
        return shapeDrawable
    }
}
