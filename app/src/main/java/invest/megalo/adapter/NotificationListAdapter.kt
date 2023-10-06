package invest.megalo.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import invest.megalo.R
import invest.megalo.controller.activity.VerticalListActivity
import invest.megalo.data.NotificationListData

class NotificationListAdapter(
    private val context: Context, private val data: ArrayList<NotificationListData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var loadMoreView = 0
    private var isLoadMoreViewAdded = false
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == loadMoreView) {
            LoadMoreViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.content_title_subtitle_load_more, parent, false)
            )
        } else {
            ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_title_subtitle, parent, false)
            )
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val holder = viewHolder as ViewHolder
        } else {
            val holder = viewHolder as LoadMoreViewHolder
            holder.title.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
            holder.title.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
            )

            holder.subtitle.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
            holder.subtitle.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )

            holder.endItem.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
            holder.endItem.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.size - 1 && isLoadMoreViewAdded) {
            loadMoreView
        } else {
            1
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addLoadMoreView() {
        data.add(
            NotificationListData(
                "null",
                "",
                "",
                "",
                "",
                "",
                "",
                seen = false,
                tappable = false,
                tapped = false,
                period = ""
            )
        )
        isLoadMoreViewAdded = true
        notifyItemInserted(data.size - 1)
        if (context is VerticalListActivity) {
            context.fetchReferree(
                context.pageLimit, context.currentPage + 1
            )
        }
    }

    fun removeLoadMoreView() {
        if (context is VerticalListActivity) {
            val index = data.map { x -> x.notificationId }.indexOf("null")
            if (index != -1) {
                data.removeAt(index)
                notifyItemRemoved(index)
            }
            isLoadMoreViewAdded = false
        }
    }

    class LoadMoreViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val subtitle: TextView = v.findViewById(R.id.sub_title)
        val endItem: TextView = v.findViewById(R.id.end_item)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val parent: RelativeLayout = v.findViewById(R.id.parent)
        val title: TextView = v.findViewById(R.id.title)
        val subtitle: TextView = v.findViewById(R.id.sub_title)
        val endText: TextView = v.findViewById(R.id.end_text)
        val forward: ImageView = v.findViewById(R.id.forward)
        val notificationDot: TextView = v.findViewById(R.id.notifications_dot)
    }
}