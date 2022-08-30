package invest.megalo.model

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat

class ColorResCompat(private val context: Context, private val id: Int) {
    fun get(): Int {
        val resolvedAttr = TypedValue()
        context.theme.resolveAttribute(id, resolvedAttr, true)
        val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
        return ContextCompat.getColor(context, colorRes)
    }
}