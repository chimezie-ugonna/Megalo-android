package invest.megalo.controller.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.SetAppTheme
import org.json.JSONObject
import java.text.DecimalFormat

class ReferralActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var scroll: NestedScrollView
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorTitle: TextView
    private lateinit var errorDescription: TextView
    private lateinit var pageLoader: LottieAnimationView
    lateinit var parent: LinearLayout
    private lateinit var title: TextView
    private lateinit var header: TextView
    private lateinit var subHeader: TextView
    private lateinit var totalReferralsTitle: TextView
    private lateinit var totalReferralsValue: TextView
    private lateinit var totalReferralsImg2: ImageView
    private lateinit var totalCompletedTitle: TextView
    private lateinit var totalCompletedValue: TextView
    private lateinit var totalCompletedImg2: ImageView
    private lateinit var totalPendingTitle: TextView
    private lateinit var totalPendingValue: TextView
    private lateinit var totalPendingImg2: ImageView
    private lateinit var totalEarningsTitle: TextView
    private lateinit var totalEarningsValue: TextView
    private lateinit var referralCodeTitle: TextView
    private lateinit var referralCodeValue: TextView
    private lateinit var referralCodeImg2: ImageView
    private lateinit var refer: AppCompatButton
    private var rewardAmount = 0.0
    private var rewardMinimumInvestmentAmount = 0.0
    private var minimumInvestmentAmount = 0.0
    private var totalReferrals = 0
    private var totalCompleted = 0
    private var totalPending = 0
    private var totalEarnings = 0.0
    private var referralCode = ""
    private var referralText = ""
    private lateinit var df: DecimalFormat

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

        title.text = getString(R.string.refer_earn)
        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        back.contentDescription = getString(R.string.go_back)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.arrow_back)

        header.text = getString(R.string.refer_to_your_friends_and_earn_money)
        header.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        header.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        subHeader.text = Html.fromHtml(
            getString(
                R.string.you_and_the_person_you_invite_will_each_earn_s_when_they_create_an_account_using_your_referral_code_and_then_make_an_investment_of_at_least_s,
                df.format(
                    rewardAmount
                ),
                df.format(
                    rewardMinimumInvestmentAmount
                )
            ), Html.FROM_HTML_MODE_LEGACY
        )
        subHeader.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        subHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        totalReferralsTitle.text = getString(R.string.total_referrals)
        totalReferralsTitle.setTextColor(
            ContextCompat.getColor(
                this, R.color.darkGrey_lightGrey
            )
        )
        totalReferralsTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalReferralsValue.text = DecimalFormat("#,###.##").format(totalReferrals).toString()
        totalReferralsValue.setTextColor(
            ContextCompat.getColor(
                this, R.color.black_white
            )
        )
        totalReferralsValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalReferralsImg2.contentDescription = getString(R.string.go_forward)
        totalReferralsImg2.setImageResource(R.drawable.arrow_forward)

        totalCompletedTitle.text = getString(R.string.total_completed)
        totalCompletedTitle.setTextColor(
            ContextCompat.getColor(
                this, R.color.darkGrey_lightGrey
            )
        )
        totalCompletedTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalCompletedValue.text = DecimalFormat("#,###.##").format(totalCompleted).toString()
        totalCompletedValue.setTextColor(
            ContextCompat.getColor(
                this, R.color.black_white
            )
        )
        totalCompletedValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalCompletedImg2.contentDescription = getString(R.string.go_forward)
        totalCompletedImg2.setImageResource(R.drawable.arrow_forward)

        totalPendingTitle.text = getString(R.string.total_pending)
        totalPendingTitle.setTextColor(
            ContextCompat.getColor(
                this, R.color.darkGrey_lightGrey
            )
        )
        totalPendingTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalPendingValue.text = DecimalFormat("#,###.##").format(totalPending).toString()
        totalPendingValue.setTextColor(
            ContextCompat.getColor(
                this, R.color.black_white
            )
        )
        totalPendingValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalPendingImg2.contentDescription = getString(R.string.go_forward)
        totalPendingImg2.setImageResource(R.drawable.arrow_forward)

        totalEarningsTitle.text = getString(R.string.total_earnings)
        totalEarningsTitle.setTextColor(
            ContextCompat.getColor(
                this, R.color.darkGrey_lightGrey
            )
        )
        totalEarningsTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        totalEarningsValue.text = getString(
            R.string.dollar_value, df.format(
                totalEarnings
            )
        )
        totalEarningsValue.setTextColor(
            ContextCompat.getColor(
                this, R.color.black_white
            )
        )
        totalEarningsValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        referralCodeTitle.text = getString(R.string.Referral_Code)
        referralCodeTitle.setTextColor(
            ContextCompat.getColor(
                this, R.color.darkGrey_lightGrey
            )
        )
        referralCodeTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        referralCodeValue.setTextColor(
            ContextCompat.getColor(
                this, R.color.black_white
            )
        )
        referralCodeValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        referralCodeImg2.contentDescription = getString(R.string.copy_referral_code)

        findViewById<LinearLayout>(R.id.list_container).dividerDrawable = ContextCompat.getDrawable(
            this, R.drawable.divider
        )

        refer.text = getString(R.string.refer)
        refer.setTextSize(
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
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)

        parent = findViewById(R.id.parent)
        title = findViewById(R.id.title)
        findViewById<ImageView>(R.id.logo).visibility = View.GONE
        title.text = getString(R.string.refer_earn)
        header = findViewById(R.id.header)
        header.text = getString(R.string.refer_to_your_friends_and_earn_money)
        subHeader = findViewById(R.id.sub_header)
        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setOnClickListener { finish() }

        refer = findViewById(R.id.button)
        refer.text = getString(R.string.refer)
        refer.setOnClickListener {
            startActivity(Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, referralText
                )
                type = "text/plain"
            }, null))
        }

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        scroll = findViewById(R.id.scroll)
        errorContainer = findViewById(R.id.error_container)
        errorIcon = findViewById(R.id.error_icon)
        errorTitle = findViewById(R.id.error_title)
        errorDescription = findViewById(R.id.error_description)
        errorContainer.setOnClickListener {
            load()
        }
        pageLoader = findViewById(R.id.page_loader)

        findViewById<View>(R.id.total_referrals_layout).apply {
            totalReferralsTitle = findViewById(R.id.title_value_title)
            totalReferralsValue = findViewById(R.id.title_value_value)
            totalReferralsImg2 = findViewById(R.id.title_value_img2)
            val llp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llp.gravity = Gravity.CENTER_VERTICAL
            llp.setMargins(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.normal_padding),
                    resources.displayMetrics
                ).toInt(), 0, 0, 0
            )
            totalReferralsImg2.layoutParams = llp
            totalReferralsImg2.contentDescription = getString(R.string.go_forward)
            totalReferralsImg2.setImageResource(R.drawable.arrow_forward)
            totalReferralsImg2.visibility = View.VISIBLE
            setOnClickListener {
                val intent = Intent(context, VerticalListActivity::class.java)
                intent.putExtra("heading", R.string.total_referrals)
                startActivity(intent)
            }
        }

        findViewById<View>(R.id.total_completed_layout).apply {
            totalCompletedTitle = findViewById(R.id.title_value_title)
            totalCompletedValue = findViewById(R.id.title_value_value)
            totalCompletedImg2 = findViewById(R.id.title_value_img2)
            val llp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llp.gravity = Gravity.CENTER_VERTICAL
            llp.setMargins(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.normal_padding),
                    resources.displayMetrics
                ).toInt(), 0, 0, 0
            )
            totalCompletedImg2.layoutParams = llp
            totalCompletedImg2.contentDescription = getString(R.string.go_forward)
            totalCompletedImg2.setImageResource(R.drawable.arrow_forward)
            totalCompletedImg2.visibility = View.VISIBLE
            setOnClickListener {
                val intent = Intent(context, VerticalListActivity::class.java)
                intent.putExtra("heading", R.string.total_completed)
                startActivity(intent)
            }
        }

        findViewById<View>(R.id.total_pending_layout).apply {
            totalPendingTitle = findViewById(R.id.title_value_title)
            totalPendingValue = findViewById(R.id.title_value_value)
            totalPendingImg2 = findViewById(R.id.title_value_img2)
            val llp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llp.gravity = Gravity.CENTER_VERTICAL
            llp.setMargins(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.normal_padding),
                    resources.displayMetrics
                ).toInt(), 0, 0, 0
            )
            totalPendingImg2.layoutParams = llp
            totalPendingImg2.contentDescription = getString(R.string.go_forward)
            totalPendingImg2.setImageResource(R.drawable.arrow_forward)
            totalPendingImg2.visibility = View.VISIBLE
            setOnClickListener {
                val intent = Intent(context, VerticalListActivity::class.java)
                intent.putExtra("heading", R.string.total_pending)
                startActivity(intent)
            }
        }

        findViewById<View>(R.id.total_earnings_layout).apply {
            totalEarningsTitle = findViewById(R.id.title_value_title)
            totalEarningsValue = findViewById(R.id.title_value_value)
        }

        findViewById<View>(R.id.referral_code_layout).apply {
            referralCodeTitle = findViewById(R.id.title_value_title)
            referralCodeValue = findViewById(R.id.title_value_value)
            referralCodeImg2 = findViewById(R.id.title_value_img2)
            val llp = LinearLayout.LayoutParams(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.list_icon_size),
                    resources.displayMetrics
                ).toInt(), TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.list_icon_size),
                    resources.displayMetrics
                ).toInt()
            )
            llp.gravity = Gravity.CENTER_VERTICAL
            llp.setMargins(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.normal_padding),
                    resources.displayMetrics
                ).toInt(), 0, 0, 0
            )
            referralCodeImg2.layoutParams = llp
            referralCodeImg2.contentDescription = getString(R.string.copy_referral_code)
            referralCodeImg2.setImageResource(R.drawable.copy)
            referralCodeImg2.visibility = View.VISIBLE
            referralCodeImg2.setOnClickListener {
                (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                    ClipData.newPlainText("", referralCode)
                )
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    CustomSnackBar(
                        context,
                        this@ReferralActivity.parent,
                        getString(R.string.referral_code_copied_successfully),
                        "success"
                    )
                }
            }
        }

        load()
    }

    private fun load() {
        if (InternetCheck(this, parent, false).status()) {
            scroll.visibility = View.GONE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.VISIBLE
            pageLoader.playAnimation()
            ServerConnection(
                this,
                "fetchReferralData",
                Request.Method.GET,
                "user/read_referral_data",
                JSONObject()
            )
        } else {
            scroll.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
            pageLoader.visibility = View.GONE
            pageLoader.cancelAnimation()
        }
    }

    fun referralDataFetched(
        l: Int, statusCode: Int? = 0, jsonObject: JSONObject = JSONObject()
    ) {
        if (l == 1) {
            rewardAmount = jsonObject.getDouble("reward_amount")
            rewardMinimumInvestmentAmount = jsonObject.getDouble("reward_minimum_investment_amount")
            referralCode = jsonObject.getString("referral_code")
            minimumInvestmentAmount = jsonObject.getDouble("minimum_investment_amount")
            totalReferrals = jsonObject.getInt("total_referral")
            totalCompleted = jsonObject.getInt("total_rewarded")
            totalPending = jsonObject.getInt("total_pending")
            totalEarnings = jsonObject.getDouble("total_reward")

            subHeader.text = Html.fromHtml(
                getString(
                    R.string.you_and_the_person_you_invite_will_each_earn_s_when_they_create_an_account_using_your_referral_code_and_then_make_an_investment_of_at_least_s,
                    df.format(
                        rewardAmount
                    ),
                    df.format(
                        rewardMinimumInvestmentAmount
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            )

            referralText = Html.fromHtml(
                getString(
                    R.string.hello_do_you_want_to_begin_your_journey_to_financial_freedom_if_so_download_the_s_app_with_this_link_s_create_an_account_with_my_referral_code_s_and_get_the_opportunity_to_invest_in_properties_with_as_little_as_s,
                    getString(R.string.in_app_name),
                    getString(R.string.download_link),
                    referralCode,
                    df.format(
                        minimumInvestmentAmount
                    )
                ), Html.FROM_HTML_MODE_LEGACY
            ).toString()

            totalReferralsTitle.text = getString(R.string.total_referrals)
            totalReferralsValue.text = DecimalFormat("#,###.##").format(totalReferrals).toString()

            totalCompletedTitle.text = getString(R.string.total_completed)
            totalCompletedValue.text = DecimalFormat("#,###.##").format(totalCompleted).toString()

            totalPendingTitle.text = getString(R.string.total_pending)
            totalPendingValue.text = DecimalFormat("#,###.##").format(totalPending).toString()

            totalEarningsTitle.text = getString(R.string.total_earnings)
            totalEarningsValue.text = getString(
                R.string.dollar_value, df.format(
                    totalEarnings
                )
            )

            referralCodeTitle.text = getString(R.string.Referral_Code)
            referralCodeValue.text = referralCode

            scroll.visibility = View.VISIBLE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.GONE
            pageLoader.cancelAnimation()
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
}