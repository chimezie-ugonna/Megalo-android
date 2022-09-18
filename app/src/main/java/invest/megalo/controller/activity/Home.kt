package invest.megalo.controller.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.controller.fragment.HomeFragment
import invest.megalo.controller.fragment.InvestmentsFragment
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
    private lateinit var profile: FrameLayout
    private lateinit var profileIcon: ImageView
    private lateinit var profileDot: TextView
    private lateinit var profileFragment: ProfileFragment
    private lateinit var parent: RelativeLayout
    private lateinit var bottomNav: LinearLayout
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

        profileIcon = findViewById(R.id.profile_icon)
        profileDot = findViewById(R.id.profile_dot)
        profile = findViewById(R.id.profile)
        profile.setOnClickListener {
            replaceFragment(profileFragment, it)
        }

        homeFragment = HomeFragment()
        investmentsFragment = InvestmentsFragment()
        profileFragment = ProfileFragment()
        replaceFragment(homeFragment)
    }

    fun logOut() {
        if (InternetCheck(this, findViewById(R.id.parent)).status()) {
            loader.show(getString(R.string.logging_you_out))
            ServerConnection(
                this, "logOut", Request.Method.DELETE, "login/delete",
                JSONObject()
            )
        }
    }

    fun loggedOut(l: Int) {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(false)
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            CustomSnackBar(
                this@Home,
                findViewById(R.id.parent),
                resources.getString(R.string.server_error_message),
                "error"
            )
        }
    }

    private fun replaceFragment(fragment: Fragment, view: View? = null) {
        val fragmentManager = supportFragmentManager.beginTransaction()

        when (fragment) {
            is HomeFragment -> {
                if (homeFragment.isAdded) {
                    fragmentManager.show(homeFragment)
                } else {
                    fragmentManager.add(R.id.fragment_container, homeFragment)
                }
                if (investmentsFragment.isAdded) {
                    fragmentManager.hide(investmentsFragment)
                }
                if (profileFragment.isAdded) {
                    fragmentManager.hide(profileFragment)
                }
                home.alpha = 1f
                investments.alpha = 0.30f
                profile.alpha = 0.30f
            }
            is InvestmentsFragment -> {
                if (investmentsFragment.isAdded) {
                    fragmentManager.show(investmentsFragment)
                } else {
                    fragmentManager.add(R.id.fragment_container, investmentsFragment)
                }
                if (homeFragment.isAdded) {
                    fragmentManager.hide(homeFragment)
                }
                if (profileFragment.isAdded) {
                    fragmentManager.hide(profileFragment)
                }
                home.alpha = 0.30f
                investments.alpha = 1f
                profile.alpha = 0.30f
            }
            is ProfileFragment -> {
                if (profileFragment.isAdded) {
                    fragmentManager.show(profileFragment)
                } else {
                    fragmentManager.add(R.id.fragment_container, profileFragment)
                }
                if (homeFragment.isAdded) {
                    fragmentManager.hide(homeFragment)
                }
                if (investmentsFragment.isAdded) {
                    fragmentManager.hide(investmentsFragment)
                }
                home.alpha = 0.30f
                investments.alpha = 0.30f
                profile.alpha = 1f
            }
        }

        fragmentManager.commit()

        view?.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
        hideKeyboard()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}