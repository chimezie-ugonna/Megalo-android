package invest.megalo.model

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NoDividerListItemDecoration(
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int,
    private val topSpacing: Int,
    private val bottomSpacing: Int
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = horizontalSpacing
        outRect.left = horizontalSpacing
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = topSpacing
        }
        if (parent.getChildLayoutPosition(view) == state.itemCount - 1) {
            outRect.bottom = bottomSpacing
        } else {
            outRect.bottom = verticalSpacing
        }
    }
}