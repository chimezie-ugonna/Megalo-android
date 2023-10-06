package invest.megalo.model

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ListItemDecoration(
    private val divider: Drawable?,
    private val paddingLeft: Int = 0,
    private val paddingRight: Int = 0,
    private val showLastDivider: Boolean = false
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
        val childCount = if (showLastDivider) {
            parent.childCount
        } else {
            parent.childCount - 1
        }
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + (divider?.intrinsicHeight ?: 0)
            divider?.setBounds(left, top, right, bottom)
            divider?.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (parent.adapter != null && position == parent.adapter!!.itemCount - 1) {
            outRect[0, 0, 0] = divider?.intrinsicHeight ?: 0
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}