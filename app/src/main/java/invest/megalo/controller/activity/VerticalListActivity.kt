package invest.megalo.controller.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.android.volley.Request
import com.facebook.shimmer.ShimmerFrameLayout
import invest.megalo.R
import invest.megalo.adapter.TitleSubtitleListAdapter
import invest.megalo.data.TitleSubtitleListData
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ListItemDecoration
import invest.megalo.model.ServerConnection
import invest.megalo.model.SetAppTheme
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class VerticalListActivity : AppCompatActivity() {
    private lateinit var parent: LinearLayout
    private lateinit var title: TextView
    private lateinit var back: ImageView
    private lateinit var list: RecyclerView
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorTitle: TextView
    private lateinit var errorText: TextView
    private lateinit var emptyContainer: LinearLayout
    private lateinit var emptyIcon: ImageView
    private lateinit var emptyText: TextView
    private lateinit var shimmerScroll: NestedScrollView
    private lateinit var shimmerViewContainer: ShimmerFrameLayout
    private lateinit var shimmerItemContainer: LinearLayout
    private lateinit var itemDecoration: ListItemDecoration
    private var titleSubtitleListAdapter = TitleSubtitleListAdapter(this, ArrayList())
    private lateinit var referreeListItems: ArrayList<TitleSubtitleListData>
    var heading = 0
    var currentPage = 1
    var pageLimit = 20
    private var isLoadingMore = false
    private var hasLoaded = false

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

        title.text = getString(heading)
        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        back.contentDescription = getString(R.string.go_back)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.arrow_back)

        for (i in 1 until 16) {
            var id: Int
            when (i) {
                1 -> {
                    id = R.id.shimmer_layout_1
                }

                2 -> {
                    id = R.id.shimmer_layout_2
                }

                3 -> {
                    id = R.id.shimmer_layout_3
                }

                4 -> {
                    id = R.id.shimmer_layout_4
                }

                5 -> {
                    id = R.id.shimmer_layout_5
                }

                6 -> {
                    id = R.id.shimmer_layout_6
                }

                7 -> {
                    id = R.id.shimmer_layout_7
                }

                8 -> {
                    id = R.id.shimmer_layout_8
                }

                9 -> {
                    id = R.id.shimmer_layout_9
                }

                10 -> {
                    id = R.id.shimmer_layout_10
                }

                11 -> {
                    id = R.id.shimmer_layout_11
                }

                12 -> {
                    id = R.id.shimmer_layout_12
                }

                13 -> {
                    id = R.id.shimmer_layout_13
                }

                14 -> {
                    id = R.id.shimmer_layout_14
                }

                else -> {
                    id = R.id.shimmer_layout_15
                }
            }
            findViewById<View>(id).apply {
                findViewById<TextView>(R.id.title_shimmer).background = ContextCompat.getDrawable(
                    this@VerticalListActivity, R.drawable.dark_grey_light_grey_solid_curved_corners
                )
                findViewById<TextView>(R.id.title_shimmer).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
                )
                findViewById<TextView>(R.id.sub_title_shimmer).background =
                    ContextCompat.getDrawable(
                        this@VerticalListActivity,
                        R.drawable.dark_grey_light_grey_solid_curved_corners
                    )
                findViewById<TextView>(R.id.sub_title_shimmer).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
                )
                findViewById<TextView>(R.id.end_item).background = ContextCompat.getDrawable(
                    this@VerticalListActivity, R.drawable.dark_grey_light_grey_solid_curved_corners
                )
                findViewById<TextView>(R.id.end_item).setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
                )
            }
        }

        shimmerItemContainer.dividerDrawable = ContextCompat.getDrawable(
            this, R.drawable.shimmer_divider
        )

        itemDecoration = ListItemDecoration(
            ContextCompat.getDrawable(
                this, R.drawable.divider
            )
        )

        list.removeItemDecoration(itemDecoration)
        list.addItemDecoration(itemDecoration)
        titleSubtitleListAdapter.notifyItemRangeChanged(
            0, titleSubtitleListAdapter.itemCount
        )

        errorIcon.setImageResource(R.drawable.arrow_clockwise)
        errorTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        errorTitle.text = getString(R.string.something_went_wrong)
        errorTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )
        errorText.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        errorText.text =
            getString(R.string.we_are_having_issues_loading_this_page_please_tap_to_retry)
        errorText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        emptyIcon.setImageResource(R.drawable.trash_circle)
        emptyText.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        emptyText.text = getString(R.string.nothing_to_see_here)
        emptyText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vertical_list)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getInt("heading") != 0) {
                heading = bundle.getInt("heading")
            }
        }

        parent = findViewById(R.id.parent)
        title = findViewById(R.id.title)
        findViewById<ImageView>(R.id.logo).visibility = View.GONE
        title.text = getString(heading)
        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setOnClickListener { finish() }

        shimmerScroll = findViewById(R.id.shimmer_scroll)
        shimmerViewContainer = findViewById(R.id.shimmer_view_container)
        shimmerItemContainer = findViewById(R.id.shimmer_item_container)

        emptyContainer = findViewById(R.id.empty_container)
        emptyIcon = findViewById(R.id.empty_icon)
        emptyText = findViewById(R.id.empty_text)

        errorContainer = findViewById(R.id.error_container)
        errorIcon = findViewById(R.id.error_icon)
        errorTitle = findViewById(R.id.error_title)
        errorText = findViewById(R.id.error_text)
        errorContainer.setOnClickListener {
            load()
        }

        itemDecoration = ListItemDecoration(
            ContextCompat.getDrawable(
                this, R.drawable.divider
            )
        )

        referreeListItems = ArrayList()
        list = findViewById(R.id.list)
        list.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            addItemDecoration(itemDecoration)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!list.canScrollVertically(1)) {
                        if (heading != R.string.notifications) {
                            if (!isLoadingMore) {
                                isLoadingMore = true
                                titleSubtitleListAdapter.addLoadMoreView()
                            }
                        }
                    }
                }
            })
        }

        load()
    }

    private fun load() {
        if (InternetCheck(this, parent, false).status()) {
            list.visibility = View.GONE
            errorContainer.visibility = View.GONE
            emptyContainer.visibility = View.GONE
            shimmerViewContainer.startShimmer()
            shimmerScroll.visibility = View.VISIBLE
            if (heading != R.string.notifications) {
                fetchReferree(pageLimit, currentPage)
            }
        } else {
            list.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
            emptyContainer.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
            shimmerScroll.visibility = View.GONE
        }
    }

    fun fetchReferree(limit: Int, page: Int) {
        if (InternetCheck(this, parent, false).status()) {
            val extension = when (heading) {
                R.string.total_completed -> {
                    "user/read_referree?limit=$limit&page=$page&type=completed"
                }

                R.string.total_pending -> {
                    "user/read_referree?limit=$limit&page=$page&type=pending"
                }

                else -> {
                    "user/read_referree?limit=$limit&page=$page"
                }
            }
            ServerConnection(
                this, "fetchReferrees", Request.Method.GET, extension, JSONObject()
            )
        } else {
            if (isLoadingMore) {
                titleSubtitleListAdapter.removeLoadMoreView()
                isLoadingMore = false
            }
        }
    }

    fun referreesFetched(
        l: Int, statusCode: Int? = 0, message: String = "", jsonObject: JSONObject = JSONObject()
    ) {
        if (l == 1) {
            var addedListItemCount = 0
            currentPage = jsonObject.getInt("current_page")
            val nextPageUrl = if (!jsonObject.isNull("next_page_url")) {
                jsonObject.getString("next_page_url")
            } else {
                ""
            }

            val jsonArray = jsonObject.getJSONArray("data")
            if (jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    if (heading != R.string.notifications && !referreeListItems.stream()
                            .map(TitleSubtitleListData::id)
                            .anyMatch(jsonArray.getJSONObject(i).getInt("id").toString()::equals)
                    ) {
                        var addItem = true
                        if (i == 0) {
                            if (isLoadingMore) {
                                val index = referreeListItems.map { x -> x.id }.indexOf(null)
                                if (index != -1) {
                                    referreeListItems[index] = TitleSubtitleListData(
                                        jsonArray.getJSONObject(i).getInt("id").toString(),
                                        null,
                                        jsonArray.getJSONObject(i)
                                            .getString("first_name") + " " + jsonArray.getJSONObject(
                                            i
                                        ).getString("last_name"),
                                        null,
                                        jsonArray.getJSONObject(i).getString("created_at")
                                            .getPeriodFromServerTimeStamp(),
                                        R.string.dollar_value,
                                        jsonArray.getJSONObject(i).getInt("reward_usd").toString()
                                    )
                                    addItem = false
                                }
                            }
                        }
                        if (addItem) {
                            referreeListItems.add(
                                TitleSubtitleListData(
                                    jsonArray.getJSONObject(i).getInt("id").toString(),
                                    null,
                                    jsonArray.getJSONObject(i)
                                        .getString("first_name") + " " + jsonArray.getJSONObject(
                                        i
                                    ).getString("last_name"),
                                    null,
                                    jsonArray.getJSONObject(i).getString("created_at")
                                        .getPeriodFromServerTimeStamp(),
                                    R.string.dollar_value,
                                    jsonArray.getJSONObject(i).getInt("reward_usd").toString()
                                )
                            )
                        }
                        addedListItemCount++
                    }
                }
                if (!hasLoaded) {
                    titleSubtitleListAdapter = TitleSubtitleListAdapter(this, referreeListItems)
                    list.adapter = titleSubtitleListAdapter

                    list.visibility = View.VISIBLE
                    errorContainer.visibility = View.GONE
                    emptyContainer.visibility = View.GONE
                    shimmerViewContainer.stopShimmer()
                    shimmerScroll.visibility = View.GONE
                } else if (isLoadingMore) {
                    when (addedListItemCount) {
                        0 -> {
                            if (nextPageUrl != "") {
                                fetchReferree(
                                    pageLimit, currentPage++
                                )
                            } else {
                                titleSubtitleListAdapter.removeLoadMoreView()
                                isLoadingMore = false
                            }
                        }

                        in 1 until pageLimit -> {
                            if (nextPageUrl != "") {
                                fetchReferree(
                                    pageLimit, currentPage++
                                )
                            } else {
                                titleSubtitleListAdapter.removeLoadMoreView()
                                titleSubtitleListAdapter.notifyItemRangeInserted(
                                    titleSubtitleListAdapter.itemCount, addedListItemCount
                                )

                                isLoadingMore = false
                            }
                        }

                        else -> {
                            titleSubtitleListAdapter.removeLoadMoreView()
                            titleSubtitleListAdapter.notifyItemRangeInserted(
                                titleSubtitleListAdapter.itemCount, addedListItemCount
                            )

                            isLoadingMore = false
                        }
                    }
                }
            } else if (currentPage == 1) {
                list.visibility = View.GONE
                errorContainer.visibility = View.GONE
                emptyContainer.visibility = View.VISIBLE
                shimmerViewContainer.stopShimmer()
                shimmerScroll.visibility = View.GONE
            }
            hasLoaded = true
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@VerticalListActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@VerticalListActivity,
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
                                this@VerticalListActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@VerticalListActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
            if (!hasLoaded) {
                list.visibility = View.GONE
                errorContainer.visibility = View.VISIBLE
                emptyContainer.visibility = View.GONE
                shimmerViewContainer.stopShimmer()
                shimmerScroll.visibility = View.GONE
            }
            if (isLoadingMore) {
                titleSubtitleListAdapter.removeLoadMoreView()
                isLoadingMore = false
            }
        }
    }

    private fun String.getPeriodFromServerTimeStamp(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return SimpleDateFormat(
            "dd.MM.yy h:mm a", Locale.getDefault()
        ).format(simpleDateFormat.parse(this) as Date)
    }
}