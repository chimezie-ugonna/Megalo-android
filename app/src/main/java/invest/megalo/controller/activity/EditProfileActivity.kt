package invest.megalo.controller.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.hbb20.CountryCodePicker
import invest.megalo.R
import invest.megalo.controller.fragment.BottomSheetDialogFragment
import invest.megalo.model.CustomLoader
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.SetAppTheme
import org.json.JSONArray
import org.json.JSONObject

class EditProfileActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var emailEdit: ImageView
    private lateinit var phoneEdit: ImageView
    private lateinit var firstNameTitle: TextView
    private lateinit var firstName: EditText
    private lateinit var firstNameError: TextView
    private lateinit var lastNameTitle: TextView
    private lateinit var lastName: EditText
    private lateinit var lastNameError: TextView
    private lateinit var dobTitle: TextView
    lateinit var dob: TextView
    private lateinit var email: EditText
    private lateinit var emailTitle: TextView
    private lateinit var phone: EditText
    private lateinit var phoneTitle: TextView
    private lateinit var phoneContainer: RelativeLayout
    private lateinit var ccp: CountryCodePicker
    private lateinit var update: AppCompatButton
    private lateinit var loader: CustomLoader
    private lateinit var scrollView: ScrollView
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorIcon: ImageView
    private lateinit var errorTitle: TextView
    private lateinit var errorDescription: TextView
    private lateinit var pageLoader: LottieAnimationView
    private lateinit var parent: LinearLayout
    private lateinit var title: TextView
    private val updateDataResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 1) {
            email.setText(it.data!!.getStringExtra("newEmail"))
            firstName.clearFocus()
            lastName.clearFocus()
            dob.clearFocus()
            CustomSnackBar(
                this@EditProfileActivity,
                parent,
                getString(R.string.email_verified_and_updated_successfully),
                "success"
            )
        } else if (it.resultCode == 2) {
            ccp.fullNumber = it.data!!.getStringExtra("newPhoneNumber")
            firstName.clearFocus()
            lastName.clearFocus()
            dob.clearFocus()
            CustomSnackBar(
                this@EditProfileActivity,
                parent,
                getString(R.string.phone_number_verified_and_updated_successfully),
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

        title.text = getString(R.string.edit_profile)
        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        back.contentDescription = getString(R.string.go_back)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.arrow_back)

        firstNameTitle.text = resources.getString(R.string.first_name)
        if (!firstNameError.isVisible) {
            if (firstName.hasFocus()) {
                firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                firstName.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                firstName.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
            firstName.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            firstNameError.text = getString(R.string.please_enter_your_first_name)
        }
        firstNameTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        firstName.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        firstName.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        firstName.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        firstNameError.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        firstNameError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        lastNameTitle.text = resources.getString(R.string.last_name)
        if (!lastNameError.isVisible) {
            if (lastName.hasFocus()) {
                lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                lastName.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                lastName.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
            lastName.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            lastNameError.text = getString(R.string.please_enter_your_last_name)
        }
        lastNameTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        lastName.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        lastName.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        lastName.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        lastNameError.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        lastNameError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        dobTitle.text = resources.getString(R.string.birth_date)
        if (dob.hasFocus()) {
            dobTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
            dob.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_app_green_stroke_curved_corners
            )
        } else {
            dobTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
            dob.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
            )
        }
        dobTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        dob.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        dob.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        emailTitle.text = resources.getString(R.string.email_address)
        emailTitle.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        emailTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        email.background = ContextCompat.getDrawable(
            this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
        )
        email.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        email.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        email.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        phoneTitle.text = resources.getString(R.string.phone_number)
        phoneTitle.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        phoneTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        phoneContainer.background = ContextCompat.getDrawable(
            this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
        )
        phone.background = ContextCompat.getDrawable(
            this, R.drawable.white_black_solid_curved_right_corners
        )
        phone.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        phone.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        phone.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        ccp.background = ContextCompat.getDrawable(
            this, R.drawable.white_black_solid_curved_left_corners
        )
        ccp.contentColor = ContextCompat.getColor(
            this, R.color.darkGrey_lightGrey
        )
        ccp.setFlagBorderColor(
            ContextCompat.getColor(
                this, R.color.darkGrey_lightGrey
            )
        )
        ccp.setTextSize(resources.getDimension(R.dimen.normal_text).toInt())

        update.text = resources.getString(R.string.update)
        update.setTextSize(
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        parent = findViewById(R.id.parent)
        title = findViewById(R.id.title)
        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setOnClickListener { finish() }

        loader = CustomLoader(this)

        findViewById<ImageView>(R.id.logo).visibility = View.GONE
        title.text = getString(R.string.edit_profile)

        update = findViewById(R.id.button)
        update.text = getString(R.string.update)
        update.setOnClickListener {
            if (firstName.text.toString().isEmpty()) {
                firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                firstName.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                firstNameError.text = getString(R.string.please_enter_your_first_name)
                firstNameError.visibility = View.VISIBLE
                firstName.requestFocus()
            }

            if (lastName.text.toString().isEmpty()) {
                lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                lastName.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                lastNameError.text = getString(R.string.please_enter_your_last_name)
                lastNameError.visibility = View.VISIBLE
                lastName.requestFocus()
            }

            if (!firstNameError.isVisible && !lastNameError.isVisible) {
                if (InternetCheck(this, parent).status()) {
                    loader.show(R.string.updating_your_account)
                    ServerConnection(
                        this, "updateAccount", Request.Method.PUT, "user/update", JSONObject().put(
                            "full_name",
                            "${firstName.text.toString().trim()} ${lastName.text.toString().trim()}"
                        ).put("dob", dob.text.toString().trim())
                    )
                }
            }
        }
        firstNameTitle = findViewById(R.id.first_name_title)
        firstName = findViewById(R.id.first_name)
        firstNameError = findViewById(R.id.first_name_error)
        firstName.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (firstName.hasFocus()) {
                    firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    firstName.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    firstName.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (firstNameError.isVisible) {
                    firstNameError.visibility = View.GONE
                }
            }
        }
        firstName.setOnFocusChangeListener { _, hasFocus ->
            if (!firstNameError.isVisible) {
                if (hasFocus) {
                    firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    firstName.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    firstNameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    firstName.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        lastNameTitle = findViewById(R.id.last_name_title)
        lastName = findViewById(R.id.last_name)
        lastNameError = findViewById(R.id.last_name_error)
        lastName.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (lastName.hasFocus()) {
                    lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    lastName.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    lastName.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (lastNameError.isVisible) {
                    lastNameError.visibility = View.GONE
                }
            }
        }
        lastName.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                lastName.setSelection(lastName.text.length)
                if (!lastNameError.isVisible) {
                    lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    lastName.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                }
            } else {
                if (!lastNameError.isVisible) {
                    lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    lastName.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        dobTitle = findViewById(R.id.dob_title)
        dob = findViewById(R.id.dob)
        dob.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (dob.hasFocus()) {
                    dobTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    dob.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    dobTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    dob.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        dob.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dob.requestFocus()
                BottomSheetDialogFragment(
                    v,
                    this,
                    "date_picker",
                    R.string.select_your_birth_date,
                    0,
                    R.string.select,
                    R.string.cancel
                ).show(supportFragmentManager, "date_picker")
            }
            false
        }
        dob.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                dobTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                dob.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                dobTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                dob.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        }
        email = findViewById(R.id.email)
        emailTitle = findViewById(R.id.email_title)
        emailEdit = findViewById(R.id.email_edit)
        emailEdit.setOnClickListener {
            val intent = Intent(this, UpdateDataActivity::class.java)
            intent.putExtra("type", "email")
            updateDataResultLauncher.launch(intent)
        }
        phone = findViewById(R.id.phone)
        phoneTitle = findViewById(R.id.phone_title)
        phoneContainer = findViewById(R.id.phone_container)
        phoneEdit = findViewById(R.id.phone_edit)
        phoneEdit.setOnClickListener {
            val intent = Intent(this, UpdateDataActivity::class.java)
            intent.putExtra("type", "sms")
            updateDataResultLauncher.launch(intent)
        }
        ccp = findViewById(R.id.ccp)
        ccp.registerCarrierNumberEditText(phone)
        scrollView = findViewById(R.id.scrollView)
        errorContainer = findViewById(R.id.error_container)
        errorIcon = findViewById(R.id.error_icon)
        errorTitle = findViewById(R.id.error_title)
        errorDescription = findViewById(R.id.error_description)
        errorContainer.setOnClickListener {
            load()
        }
        pageLoader = findViewById(R.id.page_loader)

        load()
    }

    private fun load() {
        if (InternetCheck(this, parent, false).status()) {
            scrollView.visibility = View.GONE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.VISIBLE
            ServerConnection(
                this, "fetchUserData", Request.Method.GET, "user/read", JSONObject()
            )
        } else {
            scrollView.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
            pageLoader.visibility = View.GONE
        }
    }

    fun userDataFetched(
        l: Int, jsonArray: JSONArray = JSONArray()
    ) {
        if (l == 1) {
            firstName.setText(jsonArray.getJSONObject(0).getString("first_name"))
            lastName.setText(jsonArray.getJSONObject(0).getString("last_name"))
            dob.text = jsonArray.getJSONObject(0).getString("dob")
            email.setText(jsonArray.getJSONObject(0).getString("email"))
            ccp.fullNumber = jsonArray.getJSONObject(0).getString("phone_number")
            if (jsonArray.getJSONObject(0)
                    .getString("identity_verification_status") == "verified"
            ) {
                firstNameTitle.isEnabled = false
                firstNameTitle.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.darkGrey_lightGrey
                    )
                )
                firstName.isEnabled = false
                firstName.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
                lastNameTitle.isEnabled = false
                lastNameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
                lastName.isEnabled = false
                lastName.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
                dobTitle.isEnabled = false
                dobTitle.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
                dob.isEnabled = false
                dob.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
                update.visibility = View.GONE
            }
            scrollView.visibility = View.VISIBLE
            errorContainer.visibility = View.GONE
            pageLoader.visibility = View.GONE
        } else {
            scrollView.visibility = View.GONE
            errorContainer.visibility = View.VISIBLE
            pageLoader.visibility = View.GONE
        }
    }

    fun updated(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            firstName.clearFocus()
            lastName.clearFocus()
            dob.clearFocus()
            CustomSnackBar(
                this@EditProfileActivity,
                parent,
                getString(R.string.account_updated_successfully),
                "success"
            )
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@EditProfileActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@EditProfileActivity,
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
                                this@EditProfileActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@EditProfileActivity,
                            parent,
                            getString(R.string.server_error_message),
                            "error"
                        )
                    }
                }
            }
        }
    }
}