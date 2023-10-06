package invest.megalo.controller.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.controller.fragment.BottomSheetDialogFragment
import invest.megalo.model.CustomLoader
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.SetAppTheme
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat


class InvestmentActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var title: TextView
    private lateinit var subTitle: TextView
    private lateinit var value: TextView
    private lateinit var percentageAvailable: TextView
    private lateinit var amountTitle: TextView
    private lateinit var amount: EditText
    private lateinit var amountError: TextView
    private lateinit var percentageBeingPurchased: TextView
    private lateinit var invest: AppCompatButton
    private lateinit var loader: CustomLoader
    private lateinit var scroll: NestedScrollView
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorTitle: TextView
    private lateinit var errorDescription: TextView
    private lateinit var pageLoader: LottieAnimationView
    private lateinit var parent: LinearLayout
    private var propertyId = ""
    private var percentageBeingPurchasedValue = 0.0
    private var propertyValue = 0.0
    var investmentPercentage = 0.0
    private var userBalance = 0.0
    private var percentageAvailableValue = 0.0
    private var df = DecimalFormat()
    private var amountErrorNumber = 0
    var unformattedAmount = ""

    override fun onConfigurationChanged(newConfig: Configuration) {
        SetAppTheme(this)
        super.onConfigurationChanged(newConfig)

        val config = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (config == Configuration.UI_MODE_NIGHT_YES) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                false
        } else if (config == Configuration.UI_MODE_NIGHT_NO) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                true
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.white_black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white_black)

        parent.setBackgroundColor(ContextCompat.getColor(this, R.color.white_black))

        back.contentDescription = getString(R.string.close_page)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.close)

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        title.text = Html.fromHtml(
            getString(
                R.string.available_balance_, df.format(
                    userBalance
                )
            ), Html.FROM_HTML_MODE_LEGACY
        )
        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        subTitle.text = getString(R.string.investment)
        subTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        subTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        value.text = Html.fromHtml(
            getString(
                R.string.property_value_, df.format(
                    propertyValue
                )
            ), Html.FROM_HTML_MODE_LEGACY
        )
        value.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        value.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        percentageAvailable.text = Html.fromHtml(
            getString(
                R.string.percentage_available_, DecimalFormat("#,###.##").format(
                    percentageAvailableValue
                )
            ), Html.FROM_HTML_MODE_LEGACY
        )
        percentageAvailable.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        percentageAvailable.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        amountTitle.text = getString(R.string.amount)
        if (!amountError.isVisible) {
            if (amount.hasFocus()) {
                amountTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                amount.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                amountTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                amount.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            amountTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
            amount.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            when (amountErrorNumber) {
                1 -> {
                    amountError.text = getString(R.string.please_enter_an_amount)
                }

                2 -> {
                    amountError.text = getString(R.string.you_can_only_invest_a_minimum_of_0_50)
                }

                3 -> {
                    amountError.text =
                        getString(R.string.you_can_only_invest_a_maximum_of_999_999_99)
                }

                4 -> {
                    amountError.text =
                        getString(R.string.you_do_not_have_sufficient_funds_in_your_balance_for_this_investment)
                }

                5 -> {
                    amountError.text =
                        getString(R.string.this_amount_exceeds_the_percentage_available_for_investment)

                }
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
        amount.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        amount.hint = getString(R.string._0_00)
        amount.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        amount.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        amountError.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        amountError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        amountTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        percentageBeingPurchased.text = Html.fromHtml(
            getString(
                R.string.percentage_of_value,
                DecimalFormat("#,###.######").format(percentageBeingPurchasedValue),
                df.format(
                    propertyValue
                )
            ), Html.FROM_HTML_MODE_LEGACY
        )
        percentageBeingPurchased.setTextColor(
            ContextCompat.getColor(
                this, R.color.darkGrey_lightGrey
            )
        )
        percentageBeingPurchased.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        invest.text = resources.getString(R.string.invest)
        invest.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        errorIcon.setImageResource(R.drawable.arrow_clockwise)
        errorTitle.text = resources.getString(R.string.something_went_wrong)
        errorTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        errorTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )
        errorDescription.text =
            resources.getString(R.string.we_are_having_issues_loading_this_page_please_tap_to_retry)
        errorDescription.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        errorDescription.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        loader.onConfigurationChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_investment)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("property_id") != null) {
                propertyId = bundle.getString("property_id").toString()
            }
        }

        parent = findViewById(R.id.parent)
        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setImageResource(R.drawable.close)
        back.setOnClickListener { finish() }
        findViewById<ImageView>(R.id.logo).visibility = View.GONE

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        title = findViewById(R.id.title)
        title.setTypeface(null, Typeface.NORMAL)
        title.text = Html.fromHtml(
            getString(
                R.string.available_balance_, df.format(
                    userBalance
                )
            ), Html.FROM_HTML_MODE_LEGACY
        )
        scroll = findViewById(R.id.scroll)
        subTitle = findViewById(R.id.sub_title)
        value = findViewById(R.id.property_value)
        percentageAvailable = findViewById(R.id.percentage_available)
        amountTitle = findViewById(R.id.amount_title)
        amount = findViewById(R.id.amount)
        amount.addTextChangedListener(onTextChangedListener())
        amount.setOnFocusChangeListener { _, hasFocus ->
            if (!amountError.isVisible) {
                if (hasFocus) {
                    amountTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    amount.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    amountTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    amount.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        amountError = findViewById(R.id.amount_error)
        percentageBeingPurchased = findViewById(R.id.percentage_being_purchased)
        invest = findViewById(R.id.button)
        invest.text = getString(R.string.invest)
        invest.setOnClickListener {
            if (unformattedAmount.isEmpty()) {
                amountTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                amount.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text = getString(R.string.please_enter_an_amount)
                amountError.visibility = View.VISIBLE
                amountErrorNumber = 1
            } else if (unformattedAmount.toDouble() < 0.50) {
                amountTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                amount.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text = getString(R.string.you_can_only_invest_a_minimum_of_0_50)
                amountError.visibility = View.VISIBLE
                amountErrorNumber = 2
            } else if (unformattedAmount.toDouble() > 999999.99) {
                amountTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                amount.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text = getString(R.string.you_can_only_invest_a_maximum_of_999_999_99)
                amountError.visibility = View.VISIBLE
                amountErrorNumber = 3
            } else if (unformattedAmount.toDouble() > userBalance) {
                amountTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                amount.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text =
                    getString(R.string.you_do_not_have_sufficient_funds_in_your_balance_for_this_investment)
                amountError.visibility = View.VISIBLE
                amountErrorNumber = 4
            } else if (percentageBeingPurchasedValue > percentageAvailableValue) {
                amountTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                amount.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                amountError.text =
                    getString(R.string.this_amount_exceeds_the_percentage_available_for_investment)
                amountError.visibility = View.VISIBLE
                amountErrorNumber = 5
            } else {
                if (InternetCheck(this, parent).status()) {
                    loader.show(R.string.processing_investment)
                    ServerConnection(
                        this,
                        "invest",
                        Request.Method.POST,
                        "investment/create",
                        JSONObject().put("property_id", propertyId)
                            .put("amount_invested_usd", unformattedAmount.trim())
                    )
                }
            }

        }
        loader = CustomLoader(this)
        errorContainer = findViewById(R.id.error_container)
        errorContainer.setOnClickListener {
            load()
        }
        errorIcon = findViewById(R.id.error_icon)
        errorTitle = findViewById(R.id.error_title)
        errorDescription = findViewById(R.id.error_description)
        pageLoader = findViewById(R.id.page_loader)
        load()
    }

    private fun load() {
        if (InternetCheck(this, parent, false).status()) {
            scroll.visibility = View.GONE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.VISIBLE
            pageLoader.playAnimation()
            ServerConnection(
                this, "fetchUserData", Request.Method.GET, "user/read", JSONObject()
            )
        } else {
            scroll.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
            pageLoader.visibility = View.GONE
            pageLoader.cancelAnimation()
        }
    }

    fun userDataFetched(
        l: Int, statusCode: Int? = 0, jsonArray: JSONArray = JSONArray()
    ) {
        if (l == 1) {
            userBalance = jsonArray.getJSONObject(0).getDouble("balance_usd")
            ServerConnection(
                this,
                "fetchPropertyData",
                Request.Method.GET,
                "property/read?property_id=$propertyId",
                JSONObject()
            )
        } else {
            if (statusCode == 420) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                scroll.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                pageLoader.visibility = View.GONE
                pageLoader.cancelAnimation()
            }
        }
    }

    fun propertyDataFetched(
        l: Int, statusCode: Int? = 0, jsonArray: JSONArray = JSONArray()
    ) {
        if (l == 1) {
            propertyValue = jsonArray.getJSONObject(0).getDouble("value_usd")
            percentageAvailableValue = jsonArray.getJSONObject(0).getDouble("percentage_available")

            title.text = Html.fromHtml(
                getString(
                    R.string.available_balance_, df.format(
                        userBalance
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            value.text = Html.fromHtml(
                getString(
                    R.string.property_value_, df.format(
                        propertyValue
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            percentageAvailable.text = Html.fromHtml(
                getString(
                    R.string.percentage_available_, DecimalFormat("#,###.##").format(
                        percentageAvailableValue
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            percentageBeingPurchased.text = Html.fromHtml(
                getString(
                    R.string.percentage_of_value,
                    DecimalFormat("#,###.######").format(percentageBeingPurchasedValue),
                    df.format(
                        propertyValue
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )
            scroll.visibility = View.VISIBLE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.GONE
            pageLoader.cancelAnimation()
            amount.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(amount, InputMethodManager.SHOW_IMPLICIT)
        } else {
            if (statusCode == 420) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                scroll.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                pageLoader.visibility = View.GONE
                pageLoader.cancelAnimation()
            }
        }
    }

    fun invested(
        l: Int, statusCode: Int? = 0, message: String = "", jsonObject: JSONObject = JSONObject()
    ) {
        loader.dismiss()
        if (l == 1) {
            userBalance = jsonObject.getDouble("balance_usd")
            propertyValue = jsonObject.getDouble("value_usd")
            percentageAvailableValue = jsonObject.getDouble("percentage_available")
            investmentPercentage = jsonObject.getDouble("investment_percentage")

            title.text = Html.fromHtml(
                getString(
                    R.string.available_balance_, df.format(
                        userBalance
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            value.text = Html.fromHtml(
                getString(
                    R.string.property_value_, df.format(
                        propertyValue
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            percentageAvailable.text = Html.fromHtml(
                getString(
                    R.string.percentage_available_, DecimalFormat("#,###.##").format(
                        percentageAvailableValue
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            percentageBeingPurchased.text = Html.fromHtml(
                getString(
                    R.string.percentage_of_value,
                    DecimalFormat("#,###.######").format(percentageBeingPurchasedValue),
                    df.format(
                        propertyValue
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            BottomSheetDialogFragment(
                parent,
                this,
                "successful_investment",
                R.string.investment_successful,
                R.string.congratulations_your_investment_was_successful_and_you_now_own_s_of_this_property_you_can_view_your_investments_any_time_at_the_portfolio_tab_on_the_home_page,
                R.string.got_it,
                0,
                imgResource = R.drawable.success_green_check_circle
            ).show(supportFragmentManager, "successful_investment")
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@InvestmentActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@InvestmentActivity,
                            parent,
                            getString(R.string.unusual_error_message),
                            "error"
                        )
                    }

                    in 400..499 -> {
                        if (statusCode == 420) {
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            CustomSnackBar(
                                this@InvestmentActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@InvestmentActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }

    private fun onTextChangedListener(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (amount.hasFocus()) {
                    amountTitle.setTextColor(
                        ContextCompat.getColor(
                            this@InvestmentActivity, R.color.appGreen
                        )
                    )
                    amount.background = ContextCompat.getDrawable(
                        this@InvestmentActivity,
                        R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    amountTitle.setTextColor(
                        ContextCompat.getColor(
                            this@InvestmentActivity, R.color.black_white
                        )
                    )
                    amount.background = ContextCompat.getDrawable(
                        this@InvestmentActivity,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (amountError.isVisible) {
                    amountError.visibility = View.GONE
                }

                if (amount.text.isNotEmpty() && unformattedAmount != "") {
                    percentageBeingPurchasedValue =
                        (unformattedAmount.toDouble() / propertyValue) * 100
                    percentageBeingPurchased.text = Html.fromHtml(
                        getString(
                            R.string.percentage_of_value,
                            DecimalFormat("#,###.######").format(percentageBeingPurchasedValue),
                            df.format(
                                propertyValue
                            )
                        ), Html.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    percentageBeingPurchasedValue = 0.0
                    percentageBeingPurchased.text = Html.fromHtml(
                        getString(
                            R.string.percentage_of_value,
                            DecimalFormat("#,###.######").format(percentageBeingPurchasedValue),
                            df.format(
                                propertyValue
                            )
                        ), Html.FROM_HTML_MODE_LEGACY
                    )
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
}