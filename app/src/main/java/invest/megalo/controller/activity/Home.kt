package invest.megalo.controller.activity

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.idenfy.idenfySdk.CoreSdkInitialization.IdenfyController
import com.idenfy.idenfySdk.api.initialization.IdenfySettingsV2
import com.idenfy.idenfySdk.api.response.AutoIdentificationStatus
import com.idenfy.idenfySdk.api.response.IdenfyIdentificationResult
import com.idenfy.idenfySdk.api.ui.IdenfyUISettingsV2
import com.idenfy.idenfySdk.camerasession.commoncamerasession.presentation.model.IdenfyInstructionsType
import invest.megalo.R
import invest.megalo.controller.fragment.HomeFragment
import invest.megalo.controller.fragment.InvestmentsFragment
import invest.megalo.controller.fragment.NotificationsFragment
import invest.megalo.controller.fragment.ProfileFragment
import invest.megalo.model.*
import org.json.JSONObject


class Home : AppCompatActivity() {
    private lateinit var loader: CustomLoader
    private lateinit var home: FrameLayout
    private lateinit var homeIcon: ImageView
    private lateinit var homeDot: TextView
    private lateinit var homeFragment: HomeFragment
    private lateinit var investments: FrameLayout
    private lateinit var investmentsIcon: ImageView
    private lateinit var investmentsDot: TextView
    private lateinit var investmentsFragment: InvestmentsFragment
    private lateinit var notifications: FrameLayout
    private lateinit var notificationsIcon: ImageView
    private lateinit var notificationsDot: TextView
    private lateinit var notificationsFragment: NotificationsFragment
    private lateinit var profile: FrameLayout
    private lateinit var profileIcon: ImageView
    private lateinit var profileDot: TextView
    private lateinit var profileFragment: ProfileFragment
    private lateinit var parent: RelativeLayout
    private lateinit var bottomNav: LinearLayout
    private lateinit var authToken: String
    private lateinit var idenfySettingsV2: IdenfySettingsV2
    private var everywhere = false
    private val idenfyActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == IdenfyController.IDENFY_IDENTIFICATION_RESULT_CODE) {
            val idenfyIdentificationResult: IdenfyIdentificationResult =
                it.data!!.getParcelableExtra(IdenfyController.IDENFY_IDENTIFICATION_RESULT)!!
            if (idenfyIdentificationResult.autoIdentificationStatus == AutoIdentificationStatus.APPROVED || idenfyIdentificationResult.autoIdentificationStatus == AutoIdentificationStatus.FAILED) {
                Dialog(
                    findViewById(R.id.parent),
                    this,
                    getString(R.string.identification_document_review_ongoing),
                    getString(R.string.document_review_ongoing),
                    getString(R.string.your_identification_document_is_now_being_reviewed_you_will_be_notified_when_it_is_completed),
                    getString(R.string.got_it),
                    "",
                    false,
                    R.drawable.timer
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        loader = CustomLoader(this)

        parent = findViewById(R.id.parent)
        bottomNav = findViewById(R.id.bottom_nav)

        homeIcon = findViewById(R.id.home_icon)
        homeDot = findViewById(R.id.home_dot)
        home = findViewById(R.id.home)
        home.setOnClickListener {
            replaceFragment(homeFragment, it)
        }

        investmentsIcon = findViewById(R.id.investments_icon)
        investmentsDot = findViewById(R.id.investments_dot)
        investments = findViewById(R.id.investments)
        investments.setOnClickListener {
            replaceFragment(investmentsFragment, it)
        }

        notificationsIcon = findViewById(R.id.notifications_icon)
        notificationsDot = findViewById(R.id.notifications_dot)
        notifications = findViewById(R.id.notifications)
        notifications.setOnClickListener {
            replaceFragment(notificationsFragment, it)
        }

        profileIcon = findViewById(R.id.profile_icon)
        profileDot = findViewById(R.id.profile_dot)
        profile = findViewById(R.id.profile)
        profile.setOnClickListener {
            replaceFragment(profileFragment, it)
        }

        homeFragment = HomeFragment()
        investmentsFragment = InvestmentsFragment()
        notificationsFragment = NotificationsFragment()
        profileFragment = ProfileFragment()
        replaceFragment(homeFragment)
    }

    fun logOut() {
        if (InternetCheck(this, findViewById(R.id.parent)).status()) {
            loader.show(getString(R.string.logging_you_out))
            ServerConnection(
                this,
                "logOut",
                Request.Method.DELETE,
                "login/delete",
                JSONObject().put("everywhere", everywhere)
            )
        }
    }

    fun loggedOut(l: Int, statusCode: Int? = 0) {
        loader.dismiss()
        if (l == 1) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            when (statusCode) {
                0 -> {
                    CustomSnackBar(
                        this@Home,
                        findViewById(R.id.parent),
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
                            this@Home,
                            findViewById(R.id.parent),
                            getString(R.string.client_error_message),
                            "error"
                        )
                    }
                }
                else -> {
                    CustomSnackBar(
                        this@Home,
                        findViewById(R.id.parent),
                        getString(R.string.server_error_message),
                        "error"
                    )
                }
            }
        }
    }

    fun verify() {
        if (InternetCheck(
                this, findViewById(R.id.parent)
            ).status()
        ) {
            loader.show(getString(R.string.initiating_identity_verification))
            ServerConnection(
                this, "verifyIdentity", Request.Method.GET, "user/verify_identity", JSONObject()
            )
        }
    }

    fun initiated(i: Int, auth_token: String, statusCode: Int? = 0) {
        loader.dismiss()
        if (i == 1) {
            val idenfyUISettingsV2 = IdenfyUISettingsV2.IdenfyUIBuilderV2()
                .withInstructions(IdenfyInstructionsType.DIALOG).build()
            authToken = auth_token
            idenfySettingsV2 = IdenfySettingsV2.IdenfyBuilderV2().withAuthToken(authToken)
                .withIdenfyUISettingsV2(idenfyUISettingsV2).build()

            IdenfyController.getInstance().initializeIdenfySDKV2WithManual(
                this, idenfyActivityResultLauncher, idenfySettingsV2
            )
        } else {
            when (statusCode) {
                0 -> {
                    CustomSnackBar(
                        this@Home,
                        findViewById(R.id.parent),
                        getString(R.string.unusual_error_message),
                        "error"
                    )
                }
                in 400..499 -> {
                    when (statusCode) {
                        403 -> {
                            CustomSnackBar(
                                this@Home,
                                findViewById(R.id.parent),
                                getString(R.string.your_identity_has_already_been_verified),
                                "error"
                            )
                        }
                        420 -> {
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        else -> {
                            CustomSnackBar(
                                this@Home,
                                findViewById(R.id.parent),
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }
                }
                else -> {
                    CustomSnackBar(
                        this@Home,
                        findViewById(R.id.parent),
                        getString(R.string.server_error_message),
                        "error"
                    )
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, view: View? = null) {
        if (!isFinishing) {
            val fragmentManager = supportFragmentManager
            if (!fragmentManager.isDestroyed) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                when (fragment) {
                    is HomeFragment -> {
                        if (homeFragment.isAdded) {
                            fragmentTransaction.show(homeFragment)
                        } else {
                            fragmentTransaction.add(R.id.fragment_container, homeFragment)
                        }
                        if (investmentsFragment.isAdded) {
                            fragmentTransaction.hide(investmentsFragment)
                        }
                        if (notificationsFragment.isAdded) {
                            fragmentTransaction.hide(notificationsFragment)
                        }
                        if (profileFragment.isAdded) {
                            fragmentTransaction.hide(profileFragment)
                        }

                        homeIcon.setImageResource(R.drawable.home_filled)
                        investmentsIcon.setImageResource(R.drawable.investments_outline)
                        notificationsIcon.setImageResource(R.drawable.notifications_outline)
                        profileIcon.setImageResource(R.drawable.profile_outline)
                    }
                    is InvestmentsFragment -> {
                        if (investmentsFragment.isAdded) {
                            fragmentTransaction.show(investmentsFragment)
                        } else {
                            fragmentTransaction.add(R.id.fragment_container, investmentsFragment)
                        }
                        if (homeFragment.isAdded) {
                            fragmentTransaction.hide(homeFragment)
                        }
                        if (notificationsFragment.isAdded) {
                            fragmentTransaction.hide(notificationsFragment)
                        }
                        if (profileFragment.isAdded) {
                            fragmentTransaction.hide(profileFragment)
                        }

                        homeIcon.setImageResource(R.drawable.home_outline)
                        investmentsIcon.setImageResource(R.drawable.investments_filled)
                        notificationsIcon.setImageResource(R.drawable.notifications_outline)
                        profileIcon.setImageResource(R.drawable.profile_outline)
                    }
                    is NotificationsFragment -> {
                        if (notificationsFragment.isAdded) {
                            fragmentTransaction.show(notificationsFragment)
                        } else {
                            fragmentTransaction.add(R.id.fragment_container, notificationsFragment)
                        }
                        if (homeFragment.isAdded) {
                            fragmentTransaction.hide(homeFragment)
                        }
                        if (investmentsFragment.isAdded) {
                            fragmentTransaction.hide(investmentsFragment)
                        }
                        if (profileFragment.isAdded) {
                            fragmentTransaction.hide(profileFragment)
                        }

                        homeIcon.setImageResource(R.drawable.home_outline)
                        investmentsIcon.setImageResource(R.drawable.investments_outline)
                        notificationsIcon.setImageResource(R.drawable.notifications_filled)
                        profileIcon.setImageResource(R.drawable.profile_outline)
                    }
                    is ProfileFragment -> {
                        if (profileFragment.isAdded) {
                            fragmentTransaction.show(profileFragment)
                        } else {
                            fragmentTransaction.add(R.id.fragment_container, profileFragment)
                        }
                        if (homeFragment.isAdded) {
                            fragmentTransaction.hide(homeFragment)
                        }
                        if (investmentsFragment.isAdded) {
                            fragmentTransaction.hide(investmentsFragment)
                        }
                        if (notificationsFragment.isAdded) {
                            fragmentTransaction.hide(notificationsFragment)
                        }

                        homeIcon.setImageResource(R.drawable.home_outline)
                        investmentsIcon.setImageResource(R.drawable.investments_outline)
                        notificationsIcon.setImageResource(R.drawable.notifications_outline)
                        profileIcon.setImageResource(R.drawable.profile_filled)
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
}