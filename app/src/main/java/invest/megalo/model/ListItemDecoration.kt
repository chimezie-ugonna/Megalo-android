package invest.megalo.model

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ListItemDecoration(
    private val divider: Drawable?,
    private val paddingLeft: Int = 0,
    private val paddingRight: Int = 0
) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = if (paddingLeft == 0) {
            parent.paddingLeft
        } else {
            paddingLeft
        }
        val right = if (paddingRight == 0) {
            parent.width - parent.paddingRight
        } else {
            paddingRight
        }
        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + (divider?.intrinsicHeight ?: 0)
            divider?.setBounds(left, top, right, bottom)
            divider?.draw(canvas)
        }
    }
}