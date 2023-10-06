package invest.megalo.adapter

import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import invest.megalo.R
import invest.megalo.controller.activity.HomeActivity
import invest.megalo.controller.activity.PropertyDetailActivity
import invest.megalo.controller.fragment.HomeFragment
import invest.megalo.data.PropertyListData
import java.text.DecimalFormat

class PropertyListAdapter(
    private val context: Context, private val data: ArrayList<PropertyListData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var loadMoreView = 0
    private var isLoadMoreViewAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == loadMoreView) {
            LoadMoreViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.content_property_load_more, parent, false)
            )
        } else {
            ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_property, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = data[position]
        if (context is HomeActivity && getItemViewType(position) == 1) {
            val viewHolder = (holder as ViewHolder)
            val imageUrls = dataItem.imageUrls.split(", ")
            val firstImageUrl = if (imageUrls.isNotEmpty()) {
                imageUrls[0].split("+ ")[0]
            } else {
                ""
            }
            viewHolder.img.strokeColor = ContextCompat.getColorStateList(
                context, R.color.darkGrey_lightGrey
            )
            if (firstImageUrl != "") {
                Glide.with(context).load(firstImageUrl).centerCrop()
                    .placeholder(R.drawable.img_placeholder).into(viewHolder.img)
            }

            viewHolder.description.setTextColor(
                ContextCompat.getColor(
                    context, R.color.darkGrey_lightGrey
                )
            )
            viewHolder.description.text = dataItem.description
            viewHolder.description.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
            val formatter = DecimalFormat("#,###.##")
            if (dataItem.percentageAvailable > 0) {
                viewHolder.percentageAvailableImg.setImageResource(R.drawable.success_green_home_outline)
                viewHolder.percentageAvailable.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.successGreen
                    )
                )
                viewHolder.percentageAvailable.text = context.getString(
                    R.string._percentage_available, formatter.format(dataItem.percentageAvailable)
                )
            } else {
                viewHolder.percentageAvailableImg.setImageResource(R.drawable.dark_red_light_red_home_outline)
                viewHolder.percentageAvailable.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.darkRed_lightRed
                    )
                )
                viewHolder.percentageAvailable.text = context.getString(
                    R.string._percentage_available, formatter.format(dataItem.percentageAvailable)
                )
            }
            viewHolder.percentageAvailable.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
            val formatter2 = DecimalFormat("#,##0.0#")
            formatter2.minimumFractionDigits = 2
            viewHolder.propertyValueImg.setImageResource(R.drawable.dollar_sign_square)
            viewHolder.propertyValue.setTextColor(
                ContextCompat.getColor(
                    context, R.color.darkGrey_lightGrey
                )
            )
            viewHolder.propertyValue.text = Html.fromHtml(
                context.getString(R.string.property_value_, formatter2.format(dataItem.valueUsd)),
                Html.FROM_HTML_MODE_LEGACY
            )
            viewHolder.propertyValue.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )

            viewHolder.addressImg.setImageResource(R.drawable.address)
            viewHolder.address.setTextColor(
                ContextCompat.getColor(
                    context, R.color.darkGrey_lightGrey
                )
            )
            viewHolder.address.text = dataItem.address
            viewHolder.address.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )

            viewHolder.monthlyEarningImg.setImageResource(R.drawable.dollar_sign_return)
            viewHolder.monthlyEarning.setTextColor(
                ContextCompat.getColor(
                    context, R.color.darkGrey_lightGrey
                )
            )
            viewHolder.monthlyEarning.text = Html.fromHtml(
                context.getString(
                    R.string.monthly_earning_, formatter2.format(dataItem.monthlyEarningUsd)
                ), Html.FROM_HTML_MODE_LEGACY
            )
            viewHolder.monthlyEarning.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )

            viewHolder.parent.setOnClickListener {
                val intent = Intent(context, PropertyDetailActivity::class.java)
                intent.putExtra("property_id", dataItem.propertyId)
                intent.putExtra("address", dataItem.address)
                intent.putExtra("image_urls", dataItem.imageUrls)
                intent.putExtra("description", dataItem.description)
                intent.putExtra("value_usd", dataItem.valueUsd)
                intent.putExtra("percentage_available", dataItem.percentageAvailable)
                intent.putExtra("monthly_earning_usd", dataItem.monthlyEarningUsd)
                intent.putExtra("size_sf", dataItem.sizeSf)
                intent.putExtra(
                    "value_average_annual_change_percentage",
                    dataItem.valueAverageAnnualChangePercentage
                )
                context.startActivity(intent)
            }
        } else {
            val viewHolder = (holder as LoadMoreViewHolder)
            viewHolder.shimmerImg.setImageResource(R.color.darkGrey_lightGrey)
            viewHolder.shimmerText.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
            viewHolder.shimmerText2.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
            viewHolder.shimmerText3.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
            viewHolder.shimmerText4.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
            viewHolder.shimmerText5.background = ContextCompat.getDrawable(
                context, R.drawable.dark_grey_light_grey_solid_curved_corners
            )
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.size - 1 && isLoadMoreViewAdded) {
            loadMoreView
        } else {
            1
        }
    }

    fun addLoadMoreView() {
        data.add(PropertyListData("null", "", "", "", 0.0, 0.0, 0.0, 0, 0.0))
        isLoadMoreViewAdded = true
        notifyItemInserted(data.size - 1)
        if (context is HomeActivity) {
            context.fetchProperties(
                context.propertiesLimit, context.propertiesCurrentPage + 1
            )
        }
        println("Reached the end of list now!")
    }

    fun removeLoadMoreView() {
        if (context is HomeActivity) {
            if (!context.fragmentManager.isDestroyed) {
                if (context.homeFragment.isAdded) {
                    val index =
                        (context.fragmentManager.findFragmentByTag(context.getString(R.string.home)) as HomeFragment?)?.getIndexById(
                            "null"
                        )
                    if (index != null && index != -1) {
                        data.removeAt(index)
                        notifyItemRemoved(index)
                    }
                    isLoadMoreViewAdded = false
                }
            }
        }
    }

    class LoadMoreViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var shimmerImg: ShapeableImageView = v.findViewById(R.id.shimmer_img)
        var shimmerText: TextView = v.findViewById(R.id.shimmer_text)
        var shimmerText2: TextView = v.findViewById(R.id.shimmer_text_2)
        var shimmerText3: TextView = v.findViewById(R.id.shimmer_text_3)
        var shimmerText4: TextView = v.findViewById(R.id.shimmer_text_4)
        var shimmerText5: TextView = v.findViewById(R.id.shimmer_text_5)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var parent: RelativeLayout = v.findViewById(R.id.parent)
        var img: ShapeableImageView = v.findViewById(R.id.img)
        var description: TextView = v.findViewById(R.id.description)
        var percentageAvailableImg: ImageView = v.findViewById(R.id.percentage_available_img)
        var percentageAvailable: TextView = v.findViewById(R.id.percentage_available)
        var propertyValueImg: ImageView = v.findViewById(R.id.property_value_img)
        var propertyValue: TextView = v.findViewById(R.id.property_value)
        var addressImg: ImageView = v.findViewById(R.id.address_img)
        var address: TextView = v.findViewById(R.id.address)
        var monthlyEarningImg: ImageView = v.findViewById(R.id.monthly_earning_img)
        var monthlyEarning: TextView = v.findViewById(R.id.monthly_earning)
    }
}