package invest.megalo.controller.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import invest.megalo.R
import invest.megalo.adapter.PropertyListAdapter
import invest.megalo.controller.activity.HomeActivity
import invest.megalo.controller.activity.VerticalListActivity
import invest.megalo.data.PropertyListData
import org.json.JSONObject


@Suppress("BooleanMethodIsAlwaysInverted")
class HomeFragment : Fragment() {
    private lateinit var greeting: TextView
    private lateinit var notificationsDot: TextView
    private lateinit var errorTitle: TextView
    private lateinit var errorText: TextView
    private lateinit var emptyTitle: TextView
    private lateinit var emptyText: TextView
    private lateinit var notificationsIcon: ImageView
    private lateinit var errorIcon: ImageView
    private lateinit var emptyIcon: ImageView
    lateinit var list: RecyclerView
    private lateinit var listItems: ArrayList<PropertyListData>
    private lateinit var error: LinearLayout
    private lateinit var empty: LinearLayout
    private lateinit var shimmerViewContainer: ShimmerFrameLayout
    private lateinit var parent: RelativeLayout
    private lateinit var shimmerImg: ShapeableImageView
    private lateinit var nextShimmerImg: ShapeableImageView
    private lateinit var shimmerText: TextView
    private lateinit var shimmerText2: TextView
    private lateinit var shimmerText3: TextView
    private lateinit var shimmerText4: TextView
    private lateinit var shimmerText5: TextView
    var isLoadingMore = false

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))
        greeting.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        greeting.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, requireActivity().resources.getDimension(R.dimen.big_text)
        )
        if ((activity as HomeActivity).fullName != "-") {
            if ((activity as HomeActivity).fullName.split(" ").isNotEmpty()) {
                showGreeting((activity as HomeActivity).fullName.split(" ")[0])
            }
        } else {
            greeting.text = getString(R.string.hi)
        }
        notificationsIcon.contentDescription = getString(R.string.notifications)
        notificationsIcon.setImageResource(R.drawable.bell)
        notificationsDot.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.white_black_stroke_notification_dot
        )

        errorIcon.setImageResource(R.drawable.arrow_clockwise)
        errorTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        errorTitle.text = getString(R.string.something_went_wrong)
        errorTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, requireActivity().resources.getDimension(R.dimen.big_text)
        )
        errorText.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGrey_lightGrey))
        errorText.text =
            getString(R.string.we_are_having_issues_loading_this_page_please_tap_to_retry)
        errorText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            requireActivity().resources.getDimension(R.dimen.normal_text)
        )

        emptyIcon.setImageResource(R.drawable.trash_circle)
        emptyTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        emptyTitle.text = getString(R.string.no_property_listed)
        emptyTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, requireActivity().resources.getDimension(R.dimen.big_text)
        )
        emptyText.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGrey_lightGrey))
        emptyText.text =
            getString(R.string.we_do_not_have_any_properties_listed_yet_when_we_do_they_will_appear_here)
        emptyText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            requireActivity().resources.getDimension(R.dimen.normal_text)
        )

        shimmerImg.setImageResource(R.color.darkGrey_lightGrey)
        nextShimmerImg.setImageResource(R.color.darkGrey_lightGrey)
        shimmerText.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.dark_grey_light_grey_solid_curved_corners
        )
        shimmerText2.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.dark_grey_light_grey_solid_curved_corners
        )
        shimmerText3.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.dark_grey_light_grey_solid_curved_corners
        )
        shimmerText4.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.dark_grey_light_grey_solid_curved_corners
        )
        shimmerText5.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.dark_grey_light_grey_solid_curved_corners
        )
        (activity as HomeActivity).propertiesAdapter.notifyItemRangeChanged(
            0, (activity as HomeActivity).propertiesAdapter.itemCount
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        parent = v.findViewById(R.id.parent)
        greeting = v.findViewById(R.id.greeting)
        notificationsDot = v.findViewById(R.id.notifications_dot)
        notificationsIcon = v.findViewById(R.id.notifications_icon)

        v.findViewById<FrameLayout>(R.id.notifications_container).setOnClickListener {
            val intent = Intent(requireContext(), VerticalListActivity::class.java)
            intent.putExtra("heading", R.string.notifications)
            startActivity(intent)
        }
        listItems = ArrayList()
        shimmerViewContainer = v.findViewById(R.id.shimmer_view_container)
        shimmerImg = v.findViewById(R.id.shimmer_img)
        nextShimmerImg = v.findViewById(R.id.next_shimmer_img)
        shimmerText = v.findViewById(R.id.shimmer_text)
        shimmerText2 = v.findViewById(R.id.shimmer_text_2)
        shimmerText3 = v.findViewById(R.id.shimmer_text_3)
        shimmerText4 = v.findViewById(R.id.shimmer_text_4)
        shimmerText5 = v.findViewById(R.id.shimmer_text_5)

        empty = v.findViewById(R.id.empty_container)
        emptyIcon = v.findViewById(R.id.empty_icon)
        emptyTitle = v.findViewById(R.id.empty_title)
        emptyText = v.findViewById(R.id.empty_text)

        error = v.findViewById(R.id.error_container)
        errorIcon = v.findViewById(R.id.error_icon)
        errorTitle = v.findViewById(R.id.error_title)
        errorText = v.findViewById(R.id.error_text)
        error.setOnClickListener {
            showShimmer()
            (activity as HomeActivity).fetchUserData()
        }
        list = v.findViewById(R.id.list)
        PagerSnapHelper().attachToRecyclerView(list)
        (list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        list.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!isLoadingMore && !list.canScrollHorizontally(1)) {
                        isLoadingMore = true
                        (activity as HomeActivity).propertiesAdapter.addLoadMoreView()
                    }
                }
            })
        }
        showShimmer()
        return v
    }

    fun showGreeting(name: String) {
        greeting.text = getString(
            R.string.greeting, name
        )
    }

    fun showNotificationsDot() {
        if (notificationsDot.visibility != View.VISIBLE) {
            notificationsDot.visibility = View.VISIBLE
        }
    }

    fun hideNotificationsDot() {
        if (notificationsDot.visibility != View.GONE) {
            notificationsDot.visibility = View.GONE
        }
    }

    fun showList() {
        if (list.visibility != View.VISIBLE) {
            list.visibility = View.VISIBLE
        }
        if (shimmerViewContainer.isShimmerStarted) {
            shimmerViewContainer.stopShimmer()
        }
        if (shimmerViewContainer.visibility != View.GONE) {
            shimmerViewContainer.visibility = View.GONE
        }
        if (error.visibility != View.GONE) {
            error.visibility = View.GONE
        }
        if (empty.visibility != View.GONE) {
            empty.visibility = View.GONE
        }
    }

    private fun showShimmer() {
        if (list.visibility != View.GONE) {
            list.visibility = View.GONE
        }
        if (!shimmerViewContainer.isShimmerStarted) {
            shimmerViewContainer.startShimmer()
        }
        if (shimmerViewContainer.visibility != View.VISIBLE) {
            shimmerViewContainer.visibility = View.VISIBLE
        }
        if (error.visibility != View.GONE) {
            error.visibility = View.GONE
        }
        if (empty.visibility != View.GONE) {
            empty.visibility = View.GONE
        }
    }

    fun showError() {
        if (list.visibility != View.GONE) {
            list.visibility = View.GONE
        }
        if (shimmerViewContainer.isShimmerStarted) {
            shimmerViewContainer.stopShimmer()
        }
        if (shimmerViewContainer.visibility != View.GONE) {
            shimmerViewContainer.visibility = View.GONE
        }
        if (error.visibility != View.VISIBLE) {
            error.visibility = View.VISIBLE
        }
        if (empty.visibility != View.GONE) {
            empty.visibility = View.GONE
        }
    }

    fun showEmpty() {
        if (list.visibility != View.GONE) {
            list.visibility = View.GONE
        }
        if (shimmerViewContainer.isShimmerStarted) {
            shimmerViewContainer.stopShimmer()
        }
        if (shimmerViewContainer.visibility != View.GONE) {
            shimmerViewContainer.visibility = View.GONE
        }
        if (error.visibility != View.GONE) {
            error.visibility = View.GONE
        }
        if (empty.visibility != View.VISIBLE) {
            empty.visibility = View.VISIBLE
        }
    }

    fun addListItem(
        propertyId: String,
        address: String,
        imageUrls: String,
        description: String,
        valueUsd: Double,
        percentageAvailable: Double,
        monthlyEarningUsd: Double,
        sizeSf: Int,
        valueAverageAnnualChangePercentage: Double,
        index: Int = -1
    ) {
        if (index != -1) {
            listItems.add(
                index, PropertyListData(
                    propertyId,
                    address,
                    imageUrls,
                    description,
                    valueUsd,
                    percentageAvailable,
                    monthlyEarningUsd,
                    sizeSf,
                    valueAverageAnnualChangePercentage,
                )
            )
        } else {
            listItems.add(
                PropertyListData(
                    propertyId,
                    address,
                    imageUrls,
                    description,
                    valueUsd,
                    percentageAvailable,
                    monthlyEarningUsd,
                    sizeSf,
                    valueAverageAnnualChangePercentage,
                )
            )
        }
    }

    fun setListItem(
        propertyId: String,
        address: String,
        imageUrls: String,
        description: String,
        valueUsd: Double,
        percentageAvailable: Double,
        monthlyEarningUsd: Double,
        sizeSf: Int,
        valueAverageAnnualChangePercentage: Double,
        index: Int
    ) {
        listItems[index] = PropertyListData(
            propertyId,
            address,
            imageUrls,
            description,
            valueUsd,
            percentageAvailable,
            monthlyEarningUsd,
            sizeSf,
            valueAverageAnnualChangePercentage,
        )
    }

    fun removeListItem(index: Int) {
        listItems.removeAt(index)
    }

    fun changeListItem(index: Int, jsonObject: JSONObject) {
        val newData = listItems[index]
        if (jsonObject.has("address")) {
            newData.address = jsonObject.getString("address")
        }
        if (jsonObject.has("image_urls")) {
            newData.imageUrls = jsonObject.getString("image_urls")
        }
        if (jsonObject.has("description")) {
            newData.description = jsonObject.getString("description")
        }
        if (jsonObject.has("value_usd")) {
            newData.valueUsd = jsonObject.getDouble("value_usd")
        }
        if (jsonObject.has("percentage_available")) {
            newData.percentageAvailable = jsonObject.getDouble("percentage_available")
        }
        if (jsonObject.has("monthly_earning_usd")) {
            newData.monthlyEarningUsd = jsonObject.getDouble("monthly_earning_usd")
        }
        if (jsonObject.has("size_sf")) {
            newData.sizeSf = jsonObject.getInt("size_sf")
        }
        if (jsonObject.has("value_average_annual_change_percentage")) {
            newData.valueAverageAnnualChangePercentage =
                jsonObject.getDouble("value_average_annual_change_percentage")
        }
    }

    fun setListAdapter() {
        (activity as HomeActivity).propertiesAdapter =
            PropertyListAdapter(requireContext(), listItems)
        list.adapter = (activity as HomeActivity).propertiesAdapter
    }

    fun checkIfExists(ignoredPropertyId: String): Boolean {
        return listItems.stream().map(PropertyListData::propertyId)
            .anyMatch(ignoredPropertyId::equals)
    }

    fun getIndexById(propertyId: String): Int {
        return listItems.map { x -> x.propertyId }.indexOf(propertyId)
    }

    fun notifyListAdapter(type: String, index: Int, itemCount: Int = 0) {
        when (type) {
            "insert" -> {
                if (itemCount == 0) {
                    (activity as HomeActivity).propertiesAdapter.notifyItemInserted(index)
                } else {
                    (activity as HomeActivity).propertiesAdapter.notifyItemRangeInserted(
                        index, itemCount
                    )
                }
                showList()
            }

            "remove" -> {
                (activity as HomeActivity).propertiesAdapter.notifyItemRemoved(index)
                if (listItems.size == 0) {
                    showShimmer()
                    (activity as HomeActivity).fetchProperties(
                        (activity as HomeActivity).propertiesLimit, 1
                    )
                }
            }

            "change" -> {
                (activity as HomeActivity).propertiesAdapter.notifyItemChanged(index)
            }
        }
    }
}