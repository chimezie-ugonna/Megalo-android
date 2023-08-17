package invest.megalo.controller.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import invest.megalo.R
import invest.megalo.adapter.PropertyViewPagerFragmentAdapter
import invest.megalo.controller.fragment.PropertyDetailCalculatorFragment
import invest.megalo.controller.fragment.PropertyDetailMetricFragment
import invest.megalo.controller.fragment.PropertyDetailOverviewFragment
import invest.megalo.model.SetAppTheme
import org.json.JSONObject


class PropertyDetailActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var invest: AppCompatButton
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: PropertyViewPagerFragmentAdapter
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var fragmentManager: FragmentManager
    private lateinit var parent: RelativeLayout
    private lateinit var title: TextView
    private lateinit var buttonContainer: LinearLayout
    var propertyId = ""
    var address = ""
    var imageUrls = ""
    var descriptionText = ""
    var valueUsd = 0.0
    var percentageAvailable = 0.0
    var monthlyEarningUsd = 0.0
    var sizeSf = 0
    var valueAverageAnnualChangePercentage = 0.0

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

        title.text = getString(R.string.property_detail)
        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        back.contentDescription = getString(R.string.close_page)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.close)

        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_black))
        tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.darkHint_lightHint),
            ContextCompat.getColor(this, R.color.black)
        )
        tabLayout.getTabAt(0)?.text = getString(R.string.overview)
        tabLayout.getTabAt(1)?.text = getString(R.string.metric)
        tabLayout.getTabAt(2)?.text = getString(R.string.calculator)

        buttonContainer.background = ContextCompat.getDrawable(
            this, R.drawable.bottom_navigation_background
        )
        buttonContainer.setPadding(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.normal_padding),
                resources.displayMetrics
            ).toInt()
        )

        invest.text = getString(R.string.invest_now)
        invest.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("property_id") != null) {
                propertyId = bundle.getString("property_id").toString()
            }
            if (bundle.getString("address") != null) {
                address = bundle.getString("address").toString()
            }
            if (bundle.getString("image_urls") != null) {
                imageUrls = bundle.getString("image_urls").toString()
            }
            if (bundle.getString("description") != null) {
                descriptionText = bundle.getString("description").toString()
            }
            valueUsd = bundle.getDouble("value_usd")
            percentageAvailable = bundle.getDouble("percentage_available")
            monthlyEarningUsd = bundle.getDouble("monthly_earning_usd")
            sizeSf = bundle.getInt("size_sf")
            valueAverageAnnualChangePercentage =
                bundle.getDouble("value_average_annual_change_percentage")
        }

        fragmentManager = supportFragmentManager
        parent = findViewById(R.id.parent)
        title = findViewById(R.id.title)
        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setImageResource(R.drawable.close)
        back.setOnClickListener { finish() }

        findViewById<ImageView>(R.id.logo).visibility = View.GONE
        title.text = getString(R.string.property_detail)

        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.overview)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.metric)))
        if (percentageAvailable != 0.0) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.calculator)))
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position
                    tabLayout.performHapticFeedback(
                        HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.CONTEXT_CLICK
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        fragments = ArrayList()
        fragments.add(PropertyDetailOverviewFragment())
        fragments.add(PropertyDetailMetricFragment())
        if (percentageAvailable != 0.0) {
            fragments.add(PropertyDetailCalculatorFragment())
        }

        adapter = PropertyViewPagerFragmentAdapter(fragmentManager, lifecycle, fragments)

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        buttonContainer = findViewById(R.id.button_container)
        invest = findViewById(R.id.button)
        invest.text = getString(R.string.invest_now)
        invest.setOnClickListener {

        }

        if (percentageAvailable != 0.0) {
            val rlp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            buttonContainer.layoutParams = rlp
        }
    }

    fun propertyMetricFetched(
        l: Int, jsonObject: JSONObject = JSONObject()
    ) {
        if (l == 1) {
            (fragmentManager.findFragmentByTag("f1") as PropertyDetailMetricFragment).showContent(
                jsonObject.getInt("investor_count"),
                jsonObject.getInt("paid_dividend_count"),
                jsonObject.getDouble("all_paid_dividend_amount"),
                jsonObject.getDouble("dividend_percentage_increase"),
                jsonObject.getDouble("value_percentage_increase")
            )
        } else {
            (fragmentManager.findFragmentByTag("f1") as PropertyDetailMetricFragment).showError()
        }
    }

}