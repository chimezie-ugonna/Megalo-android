package invest.megalo.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import invest.megalo.R
import invest.megalo.controller.activity.VerticalListActivity
import invest.megalo.data.TitleSubtitleListData
import java.text.DecimalFormat

class TitleSubtitleListAdapter(
    private val context: Context, private val data: ArrayList<TitleSubtitleListData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var loadMoreView = 0
    private var isLoadMoreViewAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
            val dataItem = data[position]

            if (dataItem.titleResource != null) {
                holder.title.text = context.getString(dataItem.titleResource)
            } else if (dataItem.titleValue != null) {
                holder.title.text = dataItem.titleValue
            } else {
                holder.title.text = ""
            }
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.black_white))
            holder.title.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
            )

            if (dataItem.subtitleResource != null) {
                holder.subtitle.text = context.getString(dataItem.subtitleResource)
            } else if (dataItem.subtitleValue != null) {
                holder.subtitle.text = dataItem.subtitleValue
            } else {
                holder.subtitle.text = ""
            }
            holder.subtitle.setTextColor(
                ContextCompat.getColor(
                    context, R.color.darkGrey_lightGrey
                )
            )
            holder.subtitle.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )

            if (dataItem.endTextResource != null || dataItem.endTextValue != null) {
                holder.endText.visibility = View.VISIBLE
                holder.forward.visibility = View.GONE
                holder.notificationDot.visibility = View.GONE

                holder.endText.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
                )
                holder.endText.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.darkGrey_lightGrey
                    )
                )
                if (dataItem.endTextResource != null && dataItem.endTextValue != null) {
                    if (context is VerticalListActivity && context.heading != R.string.notifications) {
                        if (dataItem.endTextValue != "0") {
                            holder.endText.setTextColor(
                                ContextCompat.getColor(
                                    context, R.color.successGreen
                                )
                            )
                        } else {
                            holder.endText.setTextColor(
                                ContextCompat.getColor(
                                    context, R.color.darkRed_lightRed
                                )
                            )
                        }
                    }

                    if (context is VerticalListActivity && context.heading == R.string.total_referrals || context is VerticalListActivity && context.heading == R.string.total_completed || context is VerticalListActivity && context.heading == R.string.total_pending) {
                        val df = DecimalFormat("#,##0.0#")
                        df.minimumFractionDigits = 2
                        holder.endText.text = context.getString(
                            dataItem.endTextResource, df.format(dataItem.endTextValue.toDouble())
                        )
                    } else {
                        holder.endText.text = context.getString(
                            dataItem.endTextResource, dataItem.endTextValue
                        )
                    }
                } else if (dataItem.endTextResource != null) {
                    holder.endText.text = context.getString(dataItem.endTextResource)
                } else {
                    holder.endText.text = dataItem.endTextValue
                }
            } else if (dataItem.showArrowForwardIcon) {
                holder.endText.visibility = View.GONE
                holder.notificationDot.visibility = View.GONE
                holder.forward.visibility = View.VISIBLE
                holder.forward.contentDescription = context.getString(R.string.go_forward)
                holder.forward.setImageResource(R.drawable.arrow_forward)
            } else if (dataItem.showNotificationDot) {
                holder.endText.visibility = View.GONE
                holder.forward.visibility = View.GONE
                holder.notificationDot.visibility = View.VISIBLE
                holder.notificationDot.background = ContextCompat.getDrawable(
                    context, R.drawable.notification_dot
                )
            }
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
        data.add(TitleSubtitleListData(null, null, null, null, null, null, null))
        isLoadMoreViewAdded = true
        notifyItemInserted(data.size - 1)
        if (context is VerticalListActivity) {
            Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
            context.fetchReferree(
                context.pageLimit, context.currentPage + 1
            )
        }
    }

    fun removeLoadMoreView() {
        if (context is VerticalListActivity) {
            val index = data.map { x -> x.id }.indexOf(null)
            if (index != -1) {
                data.removeAt(index)
                notifyItemRemoved(index)
            }
            Toast.makeText(context, index, Toast.LENGTH_SHORT).show()
            isLoadMoreViewAdded = false
        }
    }

    class LoadMoreViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val subtitle: TextView = v.findViewById(R.id.sub_title)
        val endItem: TextView = v.findViewById(R.id.end_item)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val subtitle: TextView = v.findViewById(R.id.sub_title)
        val endText: TextView = v.findViewById(R.id.end_text)
        val forward: ImageView = v.findViewById(R.id.forward)
        val notificationDot: TextView = v.findViewById(R.id.notifications_dot)
    }
}