package invest.megalo.controller.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.idenfy.idenfySdk.CoreSdkInitialization.IdenfyController
import com.idenfy.idenfySdk.api.initialization.IdenfySettingsV2
import com.idenfy.idenfySdk.api.response.AutoIdentificationStatus
import com.idenfy.idenfySdk.api.response.IdenfyIdentificationResult
import com.idenfy.idenfySdk.api.ui.IdenfyUISettingsV2
import com.idenfy.idenfySdk.camerasession.commoncamerasession.presentation.model.IdenfyInstructionsType
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import invest.megalo.R
import invest.megalo.adapter.ProfileParentListAdapter
import invest.megalo.adapter.PropertyListAdapter
import invest.megalo.controller.ConnectivityObserver
import invest.megalo.controller.fragment.BottomSheetDialogFragment
import invest.megalo.controller.fragment.ExploreFragment
import invest.megalo.controller.fragment.HomeFragment
import invest.megalo.controller.fragment.ProfileFragment
import invest.megalo.model.CustomLoader
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.SetAppTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONArray
import org.json.JSONObject


@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity(), ConnectionEventListener, SubscriptionEventListener {
    private lateinit var loader: CustomLoader
    private lateinit var home: FrameLayout
    private lateinit var homeIcon: ImageView
    private lateinit var homeTitle: TextView
    private lateinit var homeDot: TextView
    lateinit var homeFragment: HomeFragment
    private lateinit var explore: LinearLayout
    private lateinit var exploreIcon: ImageView
    private lateinit var exploreTitle: TextView
    private lateinit var exploreFragment: ExploreFragment
    private lateinit var profile: FrameLayout
    private lateinit var profileIcon: ImageView
    private lateinit var profileTitle: TextView
    private lateinit var profileDot: TextView
    private lateinit var profileFragment: ProfileFragment
    private lateinit var authToken: String
    private lateinit var pusher: Pusher
    private lateinit var pusherOptions: PusherOptions
    private lateinit var pusherChannelName: String
    private lateinit var pusherEventName: String
    private lateinit var pusherAppCluster: String
    private lateinit var pusherAppKey: String
    private lateinit var channel: Channel
    private lateinit var networkUnavailable: LinearLayout
    lateinit var parent: RelativeLayout
    private lateinit var connectivityObserver: ConnectivityObserver
    lateinit var fragmentManager: FragmentManager
    var profileAdapter = ProfileParentListAdapter(this, ArrayList())
    var propertiesAdapter = PropertyListAdapter(this, ArrayList())
    var fullName: String = "-"
    var emailAddress: String = "-"
    private var userId: String = ""
    var identityVerificationStatus: String = ""
    private var hasUnseenNotification: Boolean = false
    var emailVerified: Boolean = false
    var hasLoaded: Boolean = false
    private var networkUnavailableBarShowing: Boolean = false
    var everywhere = 0
    var propertiesCurrentPage = 1
    var propertiesLimit = 5
    private lateinit var dialog: BottomSheetDialogFragment
    private val idenfyActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == IdenfyController.IDENFY_IDENTIFICATION_RESULT_CODE) {
            val idenfyIdentificationResult: IdenfyIdentificationResult =
                it.data!!.getParcelableExtra(IdenfyController.IDENFY_IDENTIFICATION_RESULT)!!
            if (idenfyIdentificationResult.autoIdentificationStatus == AutoIdentificationStatus.APPROVED || idenfyIdentificationResult.autoIdentificationStatus == AutoIdentificationStatus.FAILED) {
                dialog.show(supportFragmentManager, "identification_document_review")
            }
        }
    }
    private val updateEmailResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 1) {
            CustomSnackBar(
                this@HomeActivity,
                findViewById(R.id.parent),
                getString(R.string.email_verified_successfully),
                "success"
            )
        }
    }

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
        findViewById<LinearLayout>(R.id.bottom_nav).background = ContextCompat.getDrawable(
            this, R.drawable.bottom_navigation_background
        )
        homeTitle.text = getString(R.string.home)
        homeTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.bottom_nav_text)
        )
        homeIcon.contentDescription = getString(R.string.home)
        exploreTitle.text = getString(R.string.explore)
        exploreTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.bottom_nav_text)
        )
        exploreIcon.contentDescription = getString(R.string.explore)
        profileTitle.text = getString(R.string.profile)
        profileTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.bottom_nav_text)
        )
        profileIcon.contentDescription = getString(R.string.profile)
        if (homeFragment.isVisible) {
            homeIcon.setImageResource(R.drawable.home_filled)
            homeTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
            exploreIcon.setImageResource(R.drawable.explore_outline)
            exploreTitle.setTextColor(
                ContextCompat.getColor(
                    this, R.color.darkHint_lightHint
                )
            )
            profileIcon.setImageResource(R.drawable.profile_outline)
            profileTitle.setTextColor(
                ContextCompat.getColor(
                    this, R.color.darkHint_lightHint
                )
            )
        } else if (exploreFragment.isVisible) {
            homeIcon.setImageResource(R.drawable.home_outline)
            homeTitle.setTextColor(
                ContextCompat.getColor(
                    this, R.color.darkHint_lightHint
                )
            )
            exploreIcon.setImageResource(R.drawable.explore_filled)
            exploreTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
            profileIcon.setImageResource(R.drawable.profile_outline)
            profileTitle.setTextColor(
                ContextCompat.getColor(
                    this, R.color.darkHint_lightHint
                )
            )
        } else if (profileFragment.isVisible) {
            homeIcon.setImageResource(R.drawable.home_outline)
            homeTitle.setTextColor(
                ContextCompat.getColor(
                    this, R.color.darkHint_lightHint
                )
            )
            exploreIcon.setImageResource(R.drawable.explore_outline)
            exploreTitle.setTextColor(
                ContextCompat.getColor(
                    this, R.color.darkHint_lightHint
                )
            )
            profileIcon.setImageResource(R.drawable.profile_filled)
            profileTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
        }
        homeDot.background = ContextCompat.getDrawable(
            this, R.drawable.white_black_stroke_notification_dot
        )
        profileDot.background = ContextCompat.getDrawable(
            this, R.drawable.white_black_stroke_notification_dot
        )
        findViewById<TextView>(R.id.network_unavailable_text).text =
            getString(R.string.no_internet_error_message)
        findViewById<TextView>(R.id.network_unavailable_text).setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        networkUnavailable.background = ContextCompat.getDrawable(
            this, R.drawable.dark_red_light_red_solid
        )
        loader.onConfigurationChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        loader = CustomLoader(this)
        networkUnavailable = findViewById(R.id.network_unavailable)
        parent = findViewById(R.id.parent)
        fragmentManager = supportFragmentManager

        dialog = BottomSheetDialogFragment(
            parent,
            this,
            "identification_document_review",
            R.string.document_review_ongoing,
            R.string.your_identification_document_is_now_being_reviewed_you_will_be_notified_when_it_is_completed,
            R.string.got_it,
            0,
            imgResource = R.drawable.hour_glass
        )

        homeIcon = findViewById(R.id.home_icon)
        homeTitle = findViewById(R.id.home_title)
        homeDot = findViewById(R.id.home_dot)
        home = findViewById(R.id.home)
        home.setOnClickListener {
            replaceFragment(homeFragment, it)
        }

        exploreIcon = findViewById(R.id.explore_icon)
        exploreTitle = findViewById(R.id.explore_title)
        explore = findViewById(R.id.explore)
        explore.setOnClickListener {
            replaceFragment(exploreFragment, it)
        }

        profileIcon = findViewById(R.id.profile_icon)
        profileTitle = findViewById(R.id.profile_title)
        profileDot = findViewById(R.id.profile_dot)
        profile = findViewById(R.id.profile)
        profile.setOnClickListener {
            replaceFragment(profileFragment, it)
        }

        homeFragment = HomeFragment()
        exploreFragment = ExploreFragment()
        profileFragment = ProfileFragment()
        replaceFragment(homeFragment)

        connectivityObserver = InternetCheck(this, parent)
        connectivityObserver.observe().onEach {
            when (it) {
                ConnectivityObserver.Status.Available -> {
                    if (networkUnavailableBarShowing) {
                        val rlp = RelativeLayout.LayoutParams(0, 0)
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                        networkUnavailable.layoutParams = rlp
                        fetchUserData()
                        networkUnavailableBarShowing = false
                    }
                }

                ConnectivityObserver.Status.Unavailable, ConnectivityObserver.Status.Lost -> {
                    if (!networkUnavailableBarShowing) {
                        val rlp = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                        networkUnavailable.layoutParams = rlp
                        networkUnavailableBarShowing = true
                    }
                }
            }
        }.launchIn(lifecycleScope)

        fetchUserData()
    }

    fun fetchUserData() {
        if (InternetCheck(this, parent, false).status()) {
            ServerConnection(
                this, "fetchUserData", Request.Method.GET, "user/read", JSONObject()
            )
        } else {
            if (!networkUnavailableBarShowing) {
                val rlp = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                networkUnavailable.layoutParams = rlp
                networkUnavailableBarShowing = true
            }
        }
    }

    fun userDataFetched(
        l: Int, statusCode: Int? = 0, message: String = "", jsonArray: JSONArray = JSONArray()
    ) {
        if (l == 1) {
            fullName = "${jsonArray.getJSONObject(0).getString("first_name")} ${
                jsonArray.getJSONObject(0).getString("last_name")
            }"
            userId = jsonArray.getJSONObject(0).getString("user_id")
            emailAddress = jsonArray.getJSONObject(0).getString("email")
            hasUnseenNotification = jsonArray.getJSONObject(0).getBoolean("has_unseen_notification")
            emailVerified = jsonArray.getJSONObject(0).getBoolean("email_verified")
            identityVerificationStatus =
                jsonArray.getJSONObject(0).getString("identity_verification_status")
            if (hasUnseenNotification && homeDot.visibility != View.VISIBLE) {
                homeDot.visibility = View.VISIBLE
            }
            if (!emailVerified && profileDot.visibility != View.VISIBLE || identityVerificationStatus == "unverified" && profileDot.visibility != View.VISIBLE) {
                profileDot.visibility = View.VISIBLE
            }
            if (!fragmentManager.isDestroyed) {
                if (homeFragment.isAdded) {
                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.showGreeting(
                        jsonArray.getJSONObject(0).getString("first_name")
                    )

                    if (hasUnseenNotification) {
                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.showNotificationsDot()
                    }
                }
            }
            pusherAppCluster = jsonArray.getJSONObject(0).getString("pusher_app_cluster")
            pusherAppKey = jsonArray.getJSONObject(0).getString("pusher_app_key")
            pusherChannelName = jsonArray.getJSONObject(0).getString("pusher_channel_name")
            pusherEventName = jsonArray.getJSONObject(0).getString("pusher_event_name")
            fetchProperties(propertiesLimit, propertiesCurrentPage)
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@HomeActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@HomeActivity,
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
                                this@HomeActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@HomeActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
            if (!fragmentManager.isDestroyed) {
                if (homeFragment.isAdded) {
                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.showError()
                }
            }
        }
    }

    fun fetchProperties(limit: Int, page: Int) {
        if (InternetCheck(this, parent, false).status()) {
            ServerConnection(
                this,
                "fetchProperties",
                Request.Method.GET,
                "property/read_all?limit=$limit&page=$page",
                JSONObject()
            )
        } else {
            if (!networkUnavailableBarShowing) {
                val rlp = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                networkUnavailable.layoutParams = rlp
                networkUnavailableBarShowing = true
            }
            if (!fragmentManager.isDestroyed) {
                if (homeFragment.isAdded) {
                    if ((fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore == true) {
                        propertiesAdapter.removeLoadMoreView()
                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore =
                            false
                    }
                }
            }
        }
    }

    fun propertiesFetched(
        l: Int, statusCode: Int? = 0, message: String = "", jsonObject: JSONObject = JSONObject()
    ) {
        if (l == 1) {
            if (!hasLoaded) {
                pusherOptions = PusherOptions().setCluster(
                    pusherAppCluster
                )
                pusher = Pusher(pusherAppKey, pusherOptions)
                pusher.connect(this)
                channel = pusher.subscribe(pusherChannelName)
                channel.bind(pusherEventName, this)
            }

            if (!hasLoaded && profileAdapter.itemCount > 0) {
                profileAdapter.notifyItemRangeChanged(0, profileAdapter.itemCount)
            }

            var addedListItemCount = 0
            propertiesCurrentPage = jsonObject.getInt("current_page")
            val nextPageUrl = if (!jsonObject.isNull("next_page_url")) {
                jsonObject.getString("next_page_url")
            } else {
                ""
            }
            if (!fragmentManager.isDestroyed) {
                if (homeFragment.isAdded) {
                    val jsonArray = jsonObject.getJSONArray("data")
                    if (jsonArray.length() > 0) {
                        for (i in 0 until jsonArray.length()) {
                            if ((fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.checkIfExists(
                                    jsonArray.getJSONObject(i).getString("property_id")
                                ) == false
                            ) {
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.addListItem(
                                    jsonArray.getJSONObject(i).getString("property_id"),
                                    jsonArray.getJSONObject(i).getString("address"),
                                    jsonArray.getJSONObject(i).getString("image_urls"),
                                    jsonArray.getJSONObject(i).getString("description"),
                                    jsonArray.getJSONObject(i).getDouble("value_usd"),
                                    jsonArray.getJSONObject(i).getDouble("percentage_available"),
                                    jsonArray.getJSONObject(i).getDouble("monthly_earning_usd"),
                                    jsonArray.getJSONObject(i).getInt("size_sf"),
                                    jsonArray.getJSONObject(i)
                                        .getDouble("value_average_annual_change_percentage")
                                )
                                addedListItemCount++
                            }
                        }
                        if (!hasLoaded) {
                            (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.setListAdapter()
                        } else if (propertiesCurrentPage == 1) {
                            (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.notifyListAdapter(
                                "insert", propertiesAdapter.itemCount, addedListItemCount
                            )
                        }
                    } else {
                        if (propertiesCurrentPage == 1) {
                            (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.showEmpty()
                        }
                    }
                    if ((fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore == true) {
                        when (addedListItemCount) {
                            0 -> {
                                if (nextPageUrl != "") {
                                    fetchProperties(
                                        propertiesLimit, propertiesCurrentPage++
                                    )
                                } else {
                                    propertiesAdapter.removeLoadMoreView()
                                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore =
                                        false
                                }
                            }

                            in 1 until propertiesLimit -> {
                                if (nextPageUrl != "") {
                                    fetchProperties(
                                        propertiesLimit, propertiesCurrentPage++
                                    )
                                } else {
                                    propertiesAdapter.removeLoadMoreView()
                                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.notifyListAdapter(
                                        "insert", propertiesAdapter.itemCount, addedListItemCount
                                    )
                                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore =
                                        false
                                }
                            }

                            else -> {
                                propertiesAdapter.removeLoadMoreView()
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.notifyListAdapter(
                                    "insert", propertiesAdapter.itemCount, addedListItemCount
                                )
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore =
                                    false
                            }
                        }
                    }
                }
            }

            hasLoaded = true
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@HomeActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@HomeActivity,
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
                                this@HomeActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@HomeActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
            if (!fragmentManager.isDestroyed) {
                if (homeFragment.isAdded) {
                    if (!hasLoaded) {
                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.showError()
                    }
                    if ((fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore == true) {
                        propertiesAdapter.removeLoadMoreView()
                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.isLoadingMore =
                            false
                    }
                }
            }
        }
    }

    fun logOut() {
        if (InternetCheck(this, parent).status()) {
            loader.show(R.string.logging_you_out)
            ServerConnection(
                this,
                "logOut",
                Request.Method.DELETE,
                "login/delete?everywhere=$everywhere",
                JSONObject()
            )
        }
    }

    fun loggedOut(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@HomeActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@HomeActivity,
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
                                this@HomeActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@HomeActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }

    fun deleteAccount() {
        if (InternetCheck(this, parent).status()) {
            loader.show(R.string.deleting_your_account)
            ServerConnection(
                this, "deleteAccount", Request.Method.DELETE, "user/delete", JSONObject()
            )
        }
    }

    fun accountDeleted(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@HomeActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@HomeActivity,
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
                                this@HomeActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@HomeActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }

    fun verify() {
        if (InternetCheck(
                this, parent
            ).status()
        ) {
            loader.show(R.string.initiating_identity_verification)
            ServerConnection(
                this, "verifyIdentity", Request.Method.GET, "user/verify_identity", JSONObject()
            )
        }
    }

    fun initiated(i: Int, authToken: String, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (i == 1) {
            val idenfyUISettingsV2 = IdenfyUISettingsV2.IdenfyUIBuilderV2()
                .withInstructions(IdenfyInstructionsType.DIALOG).build()
            this.authToken = authToken
            val idenfySettingsV2 = IdenfySettingsV2.IdenfyBuilderV2().withAuthToken(this.authToken)
                .withIdenfyUISettingsV2(idenfyUISettingsV2).build()

            IdenfyController.getInstance().initializeIdenfySDKV2WithManual(
                this, idenfyActivityResultLauncher, idenfySettingsV2
            )
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@HomeActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@HomeActivity,
                            parent,
                            getString(R.string.unusual_error_message),
                            "error"
                        )
                    }

                    in 400..499 -> {
                        when (statusCode) {
                            420 -> {
                                finish()
                                startActivity(Intent(this, MainActivity::class.java))
                            }

                            else -> {
                                CustomSnackBar(
                                    this@HomeActivity,
                                    parent,
                                    getString(R.string.client_error_message),
                                    "error"
                                )
                            }
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@HomeActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }

    fun sendOtp() {
        if (InternetCheck(
                this, parent
            ).status()
        ) {
            loader.show(R.string.sending_verification_code)
            ServerConnection(
                this,
                "sendOtp",
                Request.Method.POST,
                "user/send_otp",
                JSONObject().put("type", "email").put("email", emailAddress)
            )
        }
    }

    fun otpSent(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            val intent = Intent(this, OtpVerificationActivity::class.java)
            intent.putExtra("type", "email")
            intent.putExtra("email", emailAddress)
            intent.putExtra("origin", "home")
            updateEmailResultLauncher.launch(intent)
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@HomeActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@HomeActivity,
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
                                this@HomeActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@HomeActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, view: View? = null) {
        if (!isFinishing) {
            if (!fragmentManager.isDestroyed) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                when (fragment) {
                    is HomeFragment -> {
                        if (homeFragment.isAdded) {
                            if (homeFragment.isVisible) {
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.list?.smoothScrollToPosition(
                                    0
                                )
                            } else {
                                fragmentTransaction.show(homeFragment)
                            }
                        } else {
                            fragmentTransaction.add(
                                R.id.fragment_container, homeFragment, getString(R.string.home)
                            )
                        }
                        if (exploreFragment.isAdded) {
                            fragmentTransaction.hide(exploreFragment)
                        }
                        if (profileFragment.isAdded) {
                            fragmentTransaction.hide(profileFragment)
                        }

                        homeIcon.setImageResource(R.drawable.home_filled)
                        homeTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                        exploreIcon.setImageResource(R.drawable.explore_outline)
                        exploreTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkHint_lightHint
                            )
                        )
                        profileIcon.setImageResource(R.drawable.profile_outline)
                        profileTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkHint_lightHint
                            )
                        )
                    }

                    is ExploreFragment -> {
                        if (exploreFragment.isAdded) {
                            fragmentTransaction.show(exploreFragment)
                        } else {
                            fragmentTransaction.add(
                                R.id.fragment_container,
                                exploreFragment,
                                getString(R.string.explore)
                            )
                        }
                        if (homeFragment.isAdded) {
                            fragmentTransaction.hide(homeFragment)
                        }
                        if (profileFragment.isAdded) {
                            fragmentTransaction.hide(profileFragment)
                        }

                        homeIcon.setImageResource(R.drawable.home_outline)
                        homeTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkHint_lightHint
                            )
                        )
                        exploreIcon.setImageResource(R.drawable.explore_filled)
                        exploreTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                        profileIcon.setImageResource(R.drawable.profile_outline)
                        profileTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkHint_lightHint
                            )
                        )
                    }

                    is ProfileFragment -> {
                        if (profileFragment.isAdded) {
                            if (profileFragment.isVisible) {
                                (fragmentManager.findFragmentByTag(getString(R.string.profile)) as ProfileFragment?)?.list?.smoothScrollToPosition(
                                    0
                                )
                            } else {
                                fragmentTransaction.show(profileFragment)
                            }
                        } else {
                            fragmentTransaction.add(
                                R.id.fragment_container,
                                profileFragment,
                                getString(R.string.profile)
                            )
                        }
                        if (homeFragment.isAdded) {
                            fragmentTransaction.hide(homeFragment)
                        }
                        if (exploreFragment.isAdded) {
                            fragmentTransaction.hide(exploreFragment)
                        }

                        homeIcon.setImageResource(R.drawable.home_outline)
                        homeTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkHint_lightHint
                            )
                        )
                        exploreIcon.setImageResource(R.drawable.explore_outline)
                        exploreTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkHint_lightHint
                            )
                        )
                        profileIcon.setImageResource(R.drawable.profile_filled)
                        profileTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))

                        //loader.show(R.string.logging_you_out)
                    }
                }

                fragmentTransaction.commit()

                view?.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.CONTEXT_CLICK
                )

                val v = currentFocus ?: View(this)
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hasLoaded) {
            pusher.disconnect()
            pusher.unsubscribe(pusherChannelName)
            channel.unbind(pusherEventName, this)
        }
    }

    override fun onConnectionStateChange(change: ConnectionStateChange?) {

    }

    override fun onError(message: String?, code: String?, e: Exception?) {

    }

    override fun onEvent(event: PusherEvent?) {
        val obj = JSONObject(event?.data.toString())
        val type = obj.getString("type")
        if (obj.has("user_id") && obj.getString("user_id").equals(userId)) {
            if (type == "identity_verification") {
                identityVerificationStatus = obj.getString("status")
                if (profileAdapter.itemCount > 0) {
                    runOnUiThread {
                        profileAdapter.notifyItemRangeChanged(0, profileAdapter.itemCount)
                    }
                }
                if (identityVerificationStatus != "pending" && dialog.isVisible) {
                    runOnUiThread {
                        dialog.dismiss()
                    }
                }
                if (!emailVerified || identityVerificationStatus == "unverified") {
                    runOnUiThread {
                        profileDot.visibility = View.VISIBLE
                    }
                } else {
                    runOnUiThread {
                        profileDot.visibility = View.GONE
                    }
                }
            } else if (type == "email_verification") {
                emailVerified = obj.getBoolean("status")
                emailAddress = obj.getString("email")
                if (profileAdapter.itemCount > 0) {
                    runOnUiThread {
                        profileAdapter.notifyItemRangeChanged(0, profileAdapter.itemCount)
                    }
                }
                if (!emailVerified || identityVerificationStatus == "unverified") {
                    runOnUiThread {
                        profileDot.visibility = View.VISIBLE
                    }
                } else {
                    runOnUiThread {
                        profileDot.visibility = View.GONE
                    }
                }
            } else if (type == "user_data_update") {
                fullName = "${obj.getString("first_name")} ${obj.getString("last_name")}"
                if (profileAdapter.itemCount > 0) {
                    runOnUiThread {
                        profileAdapter.notifyItemChanged(0)
                    }
                }

                if (!fragmentManager.isDestroyed) {
                    if (homeFragment.isAdded) {
                        runOnUiThread {
                            (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.showGreeting(
                                obj.getString("first_name")
                            )
                        }
                    }
                }
            } else if (type == "has_unseen_notification") {
                hasUnseenNotification = obj.getBoolean("status")
                if (!hasUnseenNotification) {
                    runOnUiThread {
                        homeDot.visibility = View.GONE
                    }
                    if (!fragmentManager.isDestroyed) {
                        if (homeFragment.isAdded) {
                            runOnUiThread {
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.hideNotificationsDot()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        homeDot.visibility = View.VISIBLE
                    }
                    if (!fragmentManager.isDestroyed) {
                        if (homeFragment.isAdded) {
                            runOnUiThread {
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.showNotificationsDot()
                            }
                        }
                    }
                }
            }
        } else {
            if (type == "new_property") {
                if (!fragmentManager.isDestroyed) {
                    if (homeFragment.isAdded) {
                        if (!(fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.checkIfExists(
                                obj.getString("property_id")
                            )!!
                        ) {
                            (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.addListItem(
                                obj.getString("property_id"),
                                obj.getString("address"),
                                obj.getString("image_urls"),
                                obj.getString("description"),
                                obj.getDouble("value_usd"),
                                obj.getDouble("percentage_available"),
                                obj.getDouble("monthly_earning_usd"),
                                obj.getInt("size_sf"),
                                obj.getDouble("value_average_annual_change_percentage"),
                                0
                            )
                            runOnUiThread {
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.notifyListAdapter(
                                    "insert", 0
                                )
                            }
                        }
                    }
                }
            } else if (type == "delete_property") {
                if (!fragmentManager.isDestroyed) {
                    if (homeFragment.isAdded) {
                        if ((fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.checkIfExists(
                                obj.getString("property_id")
                            ) == true
                        ) {

                            runOnUiThread {
                                (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.getIndexById(
                                    obj.getString("property_id")
                                )?.let {
                                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.removeListItem(
                                        it
                                    )
                                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.notifyListAdapter(
                                        "remove", it
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (type == "update_property") {
                if (!fragmentManager.isDestroyed) {
                    if (homeFragment.isAdded) {
                        if ((fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.checkIfExists(
                                obj.getString("property_id")
                            ) == true
                        ) {
                            if (obj.has("sold") && obj.getBoolean("sold")) {
                                runOnUiThread {
                                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.getIndexById(
                                        obj.getString("property_id")
                                    )?.let {
                                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.removeListItem(
                                            it
                                        )
                                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.notifyListAdapter(
                                            "remove", it
                                        )
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.getIndexById(
                                        obj.getString("property_id")
                                    )?.let {
                                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.changeListItem(
                                            it, obj
                                        )
                                        (fragmentManager.findFragmentByTag(getString(R.string.home)) as HomeFragment?)?.notifyListAdapter(
                                            "change", it
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}