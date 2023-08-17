package invest.megalo.controller.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.adapter.PropertyDetailListAdapter
import invest.megalo.controller.activity.PropertyDetailActivity
import invest.megalo.model.InternetCheck
import invest.megalo.model.ListItemDecoration
import invest.megalo.model.ServerConnection
import org.json.JSONObject

class PropertyDetailMetricFragment : Fragment() {
    private lateinit var parent: FrameLayout
    private lateinit var title: TextView
    private lateinit var list: RecyclerView
    private lateinit var titleResources: ArrayList<Int>
    private lateinit var valueResources: ArrayList<Int>
    private lateinit var valuesString: ArrayList<String?>
    private lateinit var valuesDouble: ArrayList<Double?>
    private lateinit var valuesInt: ArrayList<Int?>
    private lateinit var propertyDetailListAdapter: PropertyDetailListAdapter
    private lateinit var itemDecoration: ListItemDecoration
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorTitle: TextView
    private lateinit var errorDescription: TextView
    private lateinit var pageLoader: LottieAnimationView

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))

        title.text = getString(R.string.key_metrics)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        list.removeItemDecoration(itemDecoration)
        itemDecoration = ListItemDecoration(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.divider
            )
        )
        list.addItemDecoration(itemDecoration)
        propertyDetailListAdapter.notifyItemRangeChanged(0, propertyDetailListAdapter.itemCount)

        errorIcon.setImageResource(R.drawable.arrow_clockwise)
        errorTitle.text = resources.getString(R.string.something_went_wrong)
        errorTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        errorTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )
        errorDescription.text =
            resources.getString(R.string.we_are_having_issues_loading_this_page_please_tap_to_retry)
        errorDescription.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        errorDescription.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_property_detail_metric, container, false)
        parent = v.findViewById(R.id.parent)
        nestedScrollView = v.findViewById(R.id.nested_scroll_view)
        title = v.findViewById(R.id.title)
        errorContainer = v.findViewById(R.id.error_container)
        errorIcon = v.findViewById(R.id.error_icon)
        errorTitle = v.findViewById(R.id.error_title)
        errorDescription = v.findViewById(R.id.error_description)
        errorContainer.setOnClickListener {
            load()
        }
        pageLoader = v.findViewById(R.id.page_loader)

        titleResources = ArrayList()
        titleResources.add(R.string.number_of_investors)
        titleResources.add(R.string.number_of_dividends_paid)
        titleResources.add(R.string.total_dividends_paid)
        titleResources.add(R.string.dividend_percentage_increase)
        titleResources.add(R.string.property_value_percentage_increase)

        valueResources = ArrayList()
        valueResources.add(0)
        valueResources.add(0)
        valueResources.add(R.string.dollar_value)
        valueResources.add(R.string.percentage_value)
        valueResources.add(R.string.percentage_value)

        valuesString = ArrayList()
        valuesString.add(null)
        valuesString.add(null)
        valuesString.add(null)
        valuesString.add(null)
        valuesString.add(null)

        valuesDouble = ArrayList()
        valuesInt = ArrayList()

        list = v.findViewById(R.id.list)
        itemDecoration = ListItemDecoration(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.divider
            )
        )
        load()
        return v
    }

    private fun load() {
        if (InternetCheck(requireContext(), parent, false).status()) {
            nestedScrollView.visibility = View.GONE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.VISIBLE
            ServerConnection(
                requireContext(),
                "fetchPropertyMetric",
                Request.Method.GET,
                "property/read_metric?property_id=${(activity as PropertyDetailActivity).propertyId}",
                JSONObject()
            )
        } else {
            showError()
        }
    }

    fun showContent(
        investorCount: Int,
        paidDividendCount: Int,
        allPaidDividendAmount: Double,
        dividendPercentageIncrease: Double,
        valuePercentageIncrease: Double
    ) {
        valuesInt.add(investorCount)
        valuesInt.add(paidDividendCount)
        valuesInt.add(null)
        valuesInt.add(null)
        valuesInt.add(null)

        valuesDouble.add(null)
        valuesDouble.add(null)
        valuesDouble.add(allPaidDividendAmount)
        valuesDouble.add(dividendPercentageIncrease)
        valuesDouble.add(valuePercentageIncrease)

        propertyDetailListAdapter = PropertyDetailListAdapter(
            requireContext(), titleResources, valueResources, valuesString, valuesDouble, valuesInt
        )
        list.apply {
            addItemDecoration(
                itemDecoration
            )
            adapter = propertyDetailListAdapter
            isNestedScrollingEnabled = false
        }
        nestedScrollView.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        pageLoader.visibility = View.GONE
    }

    fun showError() {
        nestedScrollView.visibility = View.GONE
        errorContainer.visibility = View.VISIBLE
        pageLoader.visibility = View.GONE
    }
}