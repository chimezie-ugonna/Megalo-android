package invest.megalo.controller.fragment

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import invest.megalo.R
import invest.megalo.controller.activity.PropertyDetailActivity
import invest.megalo.model.ServerConnection
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.Calendar


open class PropertyDetailCalculatorFragment : Fragment() {
    private lateinit var subParent: LinearLayout
    private lateinit var title: TextView
    private lateinit var amountTitle: TextView
    private lateinit var amount: EditText
    private lateinit var amountError: TextView
    private lateinit var yearsTitle: TextView
    private lateinit var years: EditText
    private lateinit var yearsError: TextView
    private lateinit var calculate: AppCompatButton
    private lateinit var resultContainer: LinearLayout
    private lateinit var resultTitle: TextView
    private lateinit var potentialValueImg: ImageView
    private lateinit var potentialValueTitle: TextView
    private lateinit var potentialValueValue: TextView
    private lateinit var potentialEarningsImg: ImageView
    private lateinit var potentialEarningsTitle: TextView
    private lateinit var potentialEarningsValue: TextView
    private lateinit var chart: LineChart
    private lateinit var v: View
    private lateinit var df: DecimalFormat
    private lateinit var chartEntries: ArrayList<Entry>
    private lateinit var chartDataSets: ArrayList<ILineDataSet>
    private lateinit var chartLineDataSet: LineDataSet
    private var potentialValue = 0.0
    private var potentialEarnings = 0.0
    var unformattedAmount = ""

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        subParent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        title.text = getString(R.string.investment_calculator)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        amountTitle.text = getString(R.string.amount)
        if (!amountError.isVisible) {
            if (amount.hasFocus()) {
                amountTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.appGreen))
                amount.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                amountTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.black_white
                    )
                )
                amount.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            amountTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.darkRed_lightRed
                )
            )
            amount.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            if (unformattedAmount.isEmpty()) {
                amountError.text = getString(R.string.please_enter_an_amount)
            } else if (unformattedAmount.toDouble() < 0.50) {
                amountError.text = getString(R.string.you_can_only_invest_a_minimum_of_0_50)
            } else if (unformattedAmount.toDouble() > 999999.99) {
                amountError.text = getString(R.string.you_can_only_invest_a_maximum_of_999_999_99)
            }
        }
        if (amount.text.isNotEmpty() && unformattedAmount != "") {
            amount.setText(
                getString(
                    R.string.dollar_value,
                    DecimalFormat("#,###.##").format(unformattedAmount.toLong())
                )
            )
        }
        amount.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        amount.hint = getString(R.string._0_00)
        amount.setHintTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkHint_lightHint
            )
        )
        amount.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        amountError.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkRed_lightRed))
        amountError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        amountTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        yearsTitle.text = resources.getString(R.string.year_s)
        if (!yearsError.isVisible) {
            if (years.hasFocus()) {
                yearsTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.appGreen))
                years.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                yearsTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.black_white
                    )
                )
                years.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            yearsTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.darkRed_lightRed
                )
            )
            years.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            if (years.text.isEmpty()) {
                yearsError.text = getString(R.string.please_enter_number_of_years)
            } else if (years.text.toString().toInt() < 1) {
                yearsError.text = getString(R.string.the_number_of_years_can_not_be_less_than_1)
            } else if (years.text.toString().toInt() > 50) {
                yearsError.text = getString(R.string.the_number_of_years_can_not_be_greater_than_50)
            }

        }
        yearsTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        years.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        years.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.darkHint_lightHint))
        years.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        yearsError.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkRed_lightRed))
        yearsError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        calculate.text = resources.getString(R.string.calculate)
        calculate.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        resultTitle.text = getString(R.string.potential_returns)
        resultTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        resultTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        potentialValueImg.contentDescription = getString(R.string.view_information)
        potentialValueImg.setImageResource(R.drawable.question_mark_circle)

        potentialEarningsImg.contentDescription = getString(R.string.view_information)
        potentialEarningsImg.setImageResource(R.drawable.question_mark_circle)

        if ((activity as PropertyDetailActivity).investmentCalculatorYears > 1) {
            potentialValueTitle.text = getString(
                R.string.potential_value_after_x_years,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
            potentialEarningsTitle.text = getString(
                R.string.potential_earnings_after_x_years,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
        } else {
            potentialValueTitle.text = getString(
                R.string.potential_value_after_x_year,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
            potentialEarningsTitle.text = getString(
                R.string.potential_earnings_after_x_year,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
        }

        potentialValueTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        potentialValueTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        potentialEarningsTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        potentialEarningsTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        potentialValueValue.text = getString(
            R.string.dollar_value, df.format(potentialValue)
        )
        potentialValueValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        potentialValueValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        potentialEarningsValue.text = getString(
            R.string.dollar_value, df.format(potentialEarnings)
        )
        potentialEarningsValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        potentialEarningsValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        v.findViewById<LinearLayout>(R.id.list_container).dividerDrawable =
            ContextCompat.getDrawable(
                requireContext(), R.drawable.divider
            )

        chartLineDataSet.label = getString(R.string.potential_Value)
        chart.notifyDataSetChanged()

        chart.xAxis.textSize = resources.getDimension(R.dimen.chart_text_size)
        chart.xAxis.textColor = ContextCompat.getColor(
            requireContext(), R.color.black_white
        )
        chart.xAxis.gridColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )
        chart.xAxis.axisLineColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )

        chart.axisLeft.valueFormatter = YAxisFormatter()
        chart.axisLeft.textSize = resources.getDimension(R.dimen.chart_text_size)
        chart.axisLeft.textColor = ContextCompat.getColor(
            requireContext(), R.color.black_white
        )
        chart.axisLeft.gridColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )
        chart.axisLeft.axisLineColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )

        chart.legend.textSize = resources.getDimension(R.dimen.chart_text_size)
        chart.legend.textColor = ContextCompat.getColor(
            requireContext(), R.color.black_white
        )
    }

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_property_detail_calculator, container, false)
        subParent = v.findViewById(R.id.sub_parent)

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        title = v.findViewById(R.id.title)
        amountTitle = v.findViewById(R.id.amount_title)
        amount = v.findViewById(R.id.amount)
        amount.addTextChangedListener(onTextChangedListener())
        amount.setOnFocusChangeListener { _, hasFocus ->
            if (!amountError.isVisible) {
                if (hasFocus) {
                    amountTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.appGreen
                        )
                    )
                    amount.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    amountTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black_white
                        )
                    )
                    amount.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        amountError = v.findViewById(R.id.amount_error)
        yearsTitle = v.findViewById(R.id.years_title)
        years = v.findViewById(R.id.years)
        years.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (years.hasFocus()) {
                    yearsTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.appGreen
                        )
                    )
                    years.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    yearsTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black_white
                        )
                    )
                    years.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (yearsError.isVisible) {
                    yearsError.visibility = View.GONE
                }
            }
        }
        years.setOnFocusChangeListener { _, hasFocus ->
            if (!yearsError.isVisible) {
                if (hasFocus) {
                    yearsTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.appGreen
                        )
                    )
                    years.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    yearsTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black_white
                        )
                    )
                    years.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        yearsError = v.findViewById(R.id.years_error)
        calculate = v.findViewById(R.id.button)
        calculate.text = getString(R.string.calculate)
        calculate.setOnClickListener {
            if (unformattedAmount.isEmpty()) {
                amountTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.darkRed_lightRed
                    )
                )
                amount.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text = getString(R.string.please_enter_an_amount)
                amountError.visibility = View.VISIBLE
                amount.requestFocus()
            } else if (unformattedAmount.toDouble() < 0.50) {
                amountTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.darkRed_lightRed
                    )
                )
                amount.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text = getString(R.string.you_can_only_invest_a_minimum_of_0_50)
                amountError.visibility = View.VISIBLE
                amount.requestFocus()
            } else if (unformattedAmount.toDouble() > 999999.99) {
                amountTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.darkRed_lightRed
                    )
                )
                amount.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text = getString(R.string.you_can_only_invest_a_maximum_of_999_999_99)
                amountError.visibility = View.VISIBLE
                amount.requestFocus()
            }

            if (years.text.isEmpty()) {
                yearsTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.darkRed_lightRed
                    )
                )
                years.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                yearsError.text = getString(R.string.please_enter_number_of_years)
                yearsError.visibility = View.VISIBLE
                if (!amountError.isVisible) {
                    years.requestFocus()
                }
            } else if (years.text.toString().toInt() < 1) {
                yearsTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.darkRed_lightRed
                    )
                )
                years.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                yearsError.text = getString(R.string.the_number_of_years_can_not_be_less_than_1)
                yearsError.visibility = View.VISIBLE
                if (!amountError.isVisible) {
                    years.requestFocus()
                }
            } else if (years.text.toString().toInt() > 50) {
                yearsTitle.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.darkRed_lightRed
                    )
                )
                years.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                yearsError.text = getString(R.string.the_number_of_years_can_not_be_greater_than_50)
                yearsError.visibility = View.VISIBLE
                if (!amountError.isVisible) {
                    years.requestFocus()
                }
            }

            if (!amountError.isVisible && !yearsError.isVisible) {
                val inputMethodManager =
                    requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                (activity as PropertyDetailActivity).investmentCalculatorYears =
                    years.text.toString().toInt()
                resultContainer.visibility = View.GONE
                (activity as PropertyDetailActivity).loader.show(R.string.calculating_investment_potential)
                ServerConnection(
                    requireContext(),
                    "calculatePotential",
                    Request.Method.POST,
                    "property/calculate_potential",
                    JSONObject().put("property_id", (activity as PropertyDetailActivity).propertyId)
                        .put("amount_usd", unformattedAmount.trim().toDouble())
                        .put("time_period", years.text.toString().toInt())
                )
            }
        }
        resultContainer = v.findViewById(R.id.result_container)
        resultTitle = v.findViewById(R.id.result_title)

        v.findViewById<View>(R.id.potential_value_layout).apply {
            potentialValueImg = findViewById(R.id.title_value_img)
            potentialValueImg.contentDescription = getString(R.string.view_information)
            potentialValueImg.setImageResource(R.drawable.question_mark_circle)
            potentialValueImg.visibility = View.VISIBLE
            potentialValueImg.setOnClickListener {
                BottomSheetDialogFragment(
                    (activity as PropertyDetailActivity).parent,
                    context,
                    "potential_value_explanation",
                    R.string.potential_value,
                    R.string.potential_value_refers_to_the_estimated_financial_growth_that_a_property_may_achieve_over_a_specific_period_of_time_considering_the_amount_invested_ppv_cpv_1_aac_100_tp_piv_ip_100_ppv_terms_ppv_potential_property_value_estimated_value_of_a_property_after_a_given_period_of_time_cpv_current_property_value_current_value_of_a_property_aac_average_annual_change_average_annual_change_in_value_of_a_property_over_a_period_of_time_tp_time_period_duration_of_your_investment_ip_investment_percentage_percentage_of_property_owned_by_you_piv_potential_investment_value_estimated_return_value_of_your_investment_after_a_period_of_time,
                    R.string.got_it,
                    0
                ).show(requireActivity().supportFragmentManager, "potential_value_explanation")
            }
            potentialValueTitle = findViewById(R.id.title_value_title)
            potentialValueValue = findViewById(R.id.title_value_value)
        }

        v.findViewById<View>(R.id.potential_earnings_layout).apply {
            potentialEarningsImg = findViewById(R.id.title_value_img)
            potentialEarningsImg.contentDescription = getString(R.string.view_information)
            potentialEarningsImg.setImageResource(R.drawable.question_mark_circle)
            potentialEarningsImg.visibility = View.VISIBLE
            potentialEarningsImg.setOnClickListener {
                BottomSheetDialogFragment(
                    (activity as PropertyDetailActivity).parent,
                    context,
                    "potential_earnings_explanation",
                    R.string.potential_earnings,
                    R.string.potential_earnings_refers_to_the_estimated_financial_return_that_a_property_may_generate_over_a_specific_period_of_time_considering_the_amount_invested_ppe_cpme_tp_12_pe_ip_100_ppe_terms_ppe_potential_property_earning_estimated_amount_of_revenue_a_property_will_generate_after_a_period_of_time_cpme_current_property_monthly_earning_current_amount_of_revenue_a_property_currently_generates_every_month_tp_time_period_duration_of_your_investment_pe_potential_earning_estimated_return_accumulated_from_the_monthly_earnings_of_a_property_ip_investment_percentage_percentage_of_property_owned_by_you,
                    R.string.got_it,
                    0
                ).show(requireActivity().supportFragmentManager, "potential_earnings_explanation")
            }
            potentialEarningsTitle = findViewById(R.id.title_value_title)
            potentialEarningsValue = findViewById(R.id.title_value_value)
        }

        chart = v.findViewById(R.id.chart)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.valueFormatter = XAxisFormatter()
        chart.xAxis.textSize = resources.getDimension(R.dimen.chart_text_size)
        chart.xAxis.textColor = ContextCompat.getColor(
            requireContext(), R.color.black_white
        )
        chart.xAxis.gridColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )
        chart.xAxis.axisLineColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )

        chart.axisLeft.valueFormatter = YAxisFormatter()
        chart.axisLeft.textSize = resources.getDimension(R.dimen.chart_text_size)
        chart.axisLeft.textColor = ContextCompat.getColor(
            requireContext(), R.color.black_white
        )
        chart.axisLeft.gridColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )
        chart.axisLeft.axisLineColor = ContextCompat.getColor(
            requireContext(), R.color.darkHint_lightHint
        )

        chart.legend.textSize = resources.getDimension(R.dimen.chart_text_size)
        chart.legend.textColor = ContextCompat.getColor(
            requireContext(), R.color.black_white
        )

        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(0)

        chartEntries = ArrayList()
        chartDataSets = ArrayList()
        chartLineDataSet = LineDataSet(chartEntries, getString(R.string.potential_Value))
        return v
    }

    private fun onTextChangedListener(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (amount.hasFocus()) {
                    amountTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.appGreen
                        )
                    )
                    amount.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    amountTitle.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.black_white
                        )
                    )
                    amount.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (amountError.isVisible) {
                    amountError.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                amount.removeTextChangedListener(this)
                try {
                    unformattedAmount = s.toString()
                    if (unformattedAmount.contains("$") || unformattedAmount.contains(df.decimalFormatSymbols.groupingSeparator)) {
                        unformattedAmount = unformattedAmount.replace("$", "")
                        unformattedAmount = unformattedAmount.replace(
                            df.decimalFormatSymbols.groupingSeparator.toString(), ""
                        )
                    }
                    if (unformattedAmount != "") {
                        amount.setText(
                            getString(
                                R.string.dollar_value,
                                DecimalFormat("#,###.##").format(unformattedAmount.toLong())
                            )
                        )
                        amount.setSelection(amount.text.length - 1)
                    } else {
                        amount.setText(unformattedAmount)
                    }
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }
                amount.addTextChangedListener(this)
            }
        }
    }

    fun loadResult(jsonArray: JSONArray) {
        if ((activity as PropertyDetailActivity).investmentCalculatorYears > 1) {
            potentialValueTitle.text = getString(
                R.string.potential_value_after_x_years,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
            potentialEarningsTitle.text = getString(
                R.string.potential_earnings_after_x_years,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
        } else {
            potentialValueTitle.text = getString(
                R.string.potential_value_after_x_year,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
            potentialEarningsTitle.text = getString(
                R.string.potential_earnings_after_x_year,
                (context as PropertyDetailActivity).investmentCalculatorYears.toString()
            )
        }

        potentialValue =
            jsonArray.getJSONObject(jsonArray.length() - 1).getDouble("potential_investment_value")
        potentialEarnings =
            jsonArray.getJSONObject(jsonArray.length() - 1).getDouble("potential_earning")

        potentialValueValue.text = getString(
            R.string.dollar_value, df.format(
                potentialValue
            )
        )
        potentialEarningsValue.text = getString(
            R.string.dollar_value, df.format(
                potentialEarnings
            )
        )

        var firstYear = Calendar.getInstance().get(Calendar.YEAR) + 1

        chartEntries.clear()
        chartDataSets.clear()
        for (i in 0 until jsonArray.length()) {
            chartEntries.add(
                Entry(
                    firstYear.toFloat(),
                    jsonArray.getJSONObject(i).getDouble("potential_investment_value").toFloat()
                )
            )
            firstYear++
        }

        chartLineDataSet = LineDataSet(chartEntries, getString(R.string.potential_Value))
        chartLineDataSet.color = ContextCompat.getColor(
            requireContext(), R.color.appGreen
        )
        chartLineDataSet.setCircleColor(
            ContextCompat.getColor(
                requireContext(), R.color.appGreen
            )
        )
        chartLineDataSet.lineWidth = resources.getDimension(R.dimen.stroke_size)
        chartLineDataSet.circleRadius = resources.getDimension(R.dimen.chart_line_circle_radius)

        chartDataSets.add(chartLineDataSet)

        chart.data = LineData(chartDataSets)
        chart.animateXY(1000, 1000)
        resultContainer.visibility = View.VISIBLE
    }

    inner class YAxisFormatter : ValueFormatter() {
        private val df = DecimalFormat("#,##0.0#")

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            df.minimumFractionDigits = 2
            return getString(
                R.string.dollar_value, df.format(value)
            )
        }
    }

    inner class XAxisFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return value.toInt().toString()
        }
    }
}