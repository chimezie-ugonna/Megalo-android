package invest.megalo.controller.fragment

import android.annotation.SuppressLint
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
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.controller.activity.PropertyDetailActivity
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import org.json.JSONObject
import java.text.DecimalFormat

class PropertyDetailMetricFragment : Fragment() {
    private lateinit var parent: FrameLayout
    private lateinit var title: TextView
    private lateinit var scroll: NestedScrollView
    private lateinit var numberOfInvestorsTitle: TextView
    private lateinit var numberOfInvestorsValue: TextView
    private lateinit var numberOfDividendsPaidTitle: TextView
    private lateinit var numberOfDividendsPaidValue: TextView
    private lateinit var totalDividendsPaidTitle: TextView
    private lateinit var totalDividendsPaidValue: TextView
    private lateinit var dividendPercentageIncreaseTitle: TextView
    private lateinit var dividendPercentageIncreaseValue: TextView
    private lateinit var propertyValuePercentageIncreaseTitle: TextView
    private lateinit var propertyValuePercentageIncreaseValue: TextView
    private lateinit var v: View
    private lateinit var df: DecimalFormat
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorTitle: TextView
    private lateinit var errorDescription: TextView
    private lateinit var pageLoader: LottieAnimationView
    private var investorCount = 0
    private var paidDividendCount = 0
    private var allPaidDividendAmount = 0.0
    private var dividendPercentageIncrease = 0.0
    private var valuePercentageIncrease = 0.0

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))

        title.text = getString(R.string.key_metrics)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        numberOfInvestorsTitle.text = getString(R.string.number_of_investors)
        numberOfInvestorsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        numberOfInvestorsTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        numberOfInvestorsValue.text = DecimalFormat("#,###.##").format(investorCount).toString()
        numberOfInvestorsValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        numberOfInvestorsValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        numberOfDividendsPaidTitle.text = getString(R.string.number_of_dividends_paid)
        numberOfDividendsPaidTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        numberOfDividendsPaidTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        numberOfDividendsPaidValue.text =
            DecimalFormat("#,###.##").format(paidDividendCount).toString()
        numberOfDividendsPaidValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        numberOfDividendsPaidValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        totalDividendsPaidTitle.text = getString(R.string.total_dividends_paid)
        totalDividendsPaidTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        totalDividendsPaidTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalDividendsPaidValue.text = getString(
            R.string.dollar_value, df.format(allPaidDividendAmount)
        )
        totalDividendsPaidValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        totalDividendsPaidValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        dividendPercentageIncreaseTitle.text = getString(R.string.dividend_percentage_increase)
        dividendPercentageIncreaseTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        dividendPercentageIncreaseTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        dividendPercentageIncreaseValue.text = getString(
            R.string.percentage_value, DecimalFormat("#,###.##").format(dividendPercentageIncrease)
        )
        dividendPercentageIncreaseValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        dividendPercentageIncreaseValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        propertyValuePercentageIncreaseTitle.text =
            getString(R.string.property_value_percentage_increase)
        propertyValuePercentageIncreaseTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        propertyValuePercentageIncreaseTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        propertyValuePercentageIncreaseValue.text = getString(
            R.string.percentage_value, DecimalFormat("#,###.##").format(valuePercentageIncrease)
        )
        propertyValuePercentageIncreaseValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        propertyValuePercentageIncreaseValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        v.findViewById<LinearLayout>(R.id.list_container).dividerDrawable =
            ContextCompat.getDrawable(
                requireContext(), R.drawable.divider
            )

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

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_property_detail_metric, container, false)
        parent = v.findViewById(R.id.parent)
        scroll = v.findViewById(R.id.scroll)
        title = v.findViewById(R.id.title)
        errorContainer = v.findViewById(R.id.error_container)
        errorIcon = v.findViewById(R.id.error_icon)
        errorTitle = v.findViewById(R.id.error_title)
        errorDescription = v.findViewById(R.id.error_description)
        errorContainer.setOnClickListener {
            load()
        }
        pageLoader = v.findViewById(R.id.page_loader)

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        v.findViewById<View>(R.id.number_of_investors_layout).apply {
            numberOfInvestorsTitle = findViewById(R.id.title_value_title)
            numberOfInvestorsValue = findViewById(R.id.title_value_value)
        }

        v.findViewById<View>(R.id.number_of_dividends_paid_layout).apply {
            numberOfDividendsPaidTitle = findViewById(R.id.title_value_title)
            numberOfDividendsPaidValue = findViewById(R.id.title_value_value)
        }

        v.findViewById<View>(R.id.total_dividends_paid_layout).apply {
            totalDividendsPaidTitle = findViewById(R.id.title_value_title)
            totalDividendsPaidValue = findViewById(R.id.title_value_value)
        }

        v.findViewById<View>(R.id.dividend_percentage_increase_layout).apply {
            dividendPercentageIncreaseTitle = findViewById(R.id.title_value_title)
            dividendPercentageIncreaseValue = findViewById(R.id.title_value_value)
        }

        v.findViewById<View>(R.id.property_value_percentage_increase_layout).apply {
            propertyValuePercentageIncreaseTitle = findViewById(R.id.title_value_title)
            propertyValuePercentageIncreaseValue = findViewById(R.id.title_value_value)
        }

        load()
        return v
    }

    private fun load() {
        if (InternetCheck(requireContext(), parent, false).status()) {
            scroll.visibility = View.GONE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.VISIBLE
            pageLoader.playAnimation()
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
        this.investorCount = investorCount
        this.paidDividendCount = paidDividendCount
        this.allPaidDividendAmount = allPaidDividendAmount
        this.dividendPercentageIncrease = dividendPercentageIncrease
        this.valuePercentageIncrease = valuePercentageIncrease

        numberOfInvestorsTitle.text = getString(R.string.number_of_investors)
        numberOfInvestorsValue.text = DecimalFormat("#,###.##").format(investorCount).toString()

        numberOfDividendsPaidTitle.text = getString(R.string.number_of_dividends_paid)
        numberOfDividendsPaidValue.text =
            DecimalFormat("#,###.##").format(paidDividendCount).toString()

        totalDividendsPaidTitle.text = getString(R.string.total_dividends_paid)
        totalDividendsPaidValue.text = getString(
            R.string.dollar_value, df.format(allPaidDividendAmount)
        )

        dividendPercentageIncreaseTitle.text = getString(R.string.dividend_percentage_increase)
        dividendPercentageIncreaseValue.text = getString(
            R.string.percentage_value, DecimalFormat("#,###.##").format(dividendPercentageIncrease)
        )

        propertyValuePercentageIncreaseTitle.text =
            getString(R.string.property_value_percentage_increase)
        propertyValuePercentageIncreaseValue.text = getString(
            R.string.percentage_value, DecimalFormat("#,###.##").format(valuePercentageIncrease)
        )

        scroll.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        pageLoader.visibility = View.GONE
        pageLoader.cancelAnimation()
    }

    fun showError() {
        scroll.visibility = View.GONE
        errorContainer.visibility = View.VISIBLE
        pageLoader.visibility = View.GONE
        pageLoader.cancelAnimation()
    }
}