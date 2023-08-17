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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.controller.fragment.BottomSheetDialogFragment
import invest.megalo.model.*
import org.json.JSONObject


class RegistrationActivity : AppCompatActivity() {
    private lateinit var back: ImageView
    private lateinit var nameTitle: TextView
    private lateinit var name: EditText
    private lateinit var nameError: TextView
    private lateinit var emailTitle: TextView
    private lateinit var email: EditText
    private lateinit var emailError: TextView
    private lateinit var dobTitle: TextView
    lateinit var dob: TextView
    private lateinit var dobError: TextView
    private lateinit var referralTitle: TextView
    private lateinit var referral: EditText
    private lateinit var referralError: TextView
    private lateinit var create: AppCompatButton
    private var phoneNumber = ""
    private lateinit var loader: CustomLoader
    private lateinit var parent: LinearLayout
    private lateinit var title: TextView
    private lateinit var header: TextView
    private lateinit var subHeader: TextView

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

        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        back.contentDescription = getString(R.string.go_back)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.arrow_back)

        header.text = resources.getString(R.string.get_started)
        header.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        header.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        subHeader.text = resources.getString(R.string.begin_your_journey_to_financial_freedom)
        subHeader.setTextColor(ContextCompat.getColor(this, R.color.darkGrey_lightGrey))
        subHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        nameTitle.text = resources.getString(R.string.full_name)
        if (!nameError.isVisible) {
            if (name.hasFocus()) {
                nameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                name.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                nameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                name.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            nameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
            name.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            if (name.text.toString().isEmpty()) {
                nameError.text = getString(R.string.please_enter_your_full_name)
            } else {
                nameError.text = getString(R.string.please_enter_your_first_and_last_name)
            }
        }
        nameTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        name.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        name.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        name.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        nameError.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        nameError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        emailTitle.text = resources.getString(R.string.email_address)
        if (!emailError.isVisible) {
            if (email.hasFocus()) {
                emailTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                email.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                emailTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                email.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            emailTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
            email.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            if (email.text.toString().isEmpty()) {
                emailError.text = getString(R.string.please_enter_your_email)
            } else {
                emailError.text = getString(R.string.please_enter_a_valid_email)
            }
        }
        emailTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        email.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        email.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        email.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        emailError.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        emailError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        dobTitle.text = resources.getString(R.string.birth_date)
        if (!dobError.isVisible) {
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
        } else {
            dobTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
            dob.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            dobError.text = getString(R.string.please_select_your_birth_date)
        }
        dobTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        if (dob.text.isEmpty() || dob.text.split("/").size != 3) {
            dob.text = getString(R.string.select_your_birth_date)
            dob.setTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        } else {
            dob.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        }
        dob.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        dobError.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        dobError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        referralTitle.text = resources.getString(R.string.referral_code)
        if (!referralError.isVisible) {
            if (referral.hasFocus()) {
                referralTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                referral.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                referralTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                referral.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            referralTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
            referral.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            referralError.text = getString(R.string.please_enter_a_valid_referral_code)
        }
        referralTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        referral.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        referral.hint = getString(R.string.optional)
        referral.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
        referral.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        referralError.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        referralError.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        create.text = resources.getString(R.string.create_account)
        create.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        loader.onConfigurationChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val bundle = intent.extras
        if (bundle != null) {
            phoneNumber = bundle.getString("phone_number").toString()
        }

        parent = findViewById(R.id.parent)
        title = findViewById(R.id.title)
        header = findViewById(R.id.header)
        subHeader = findViewById(R.id.sub_header)
        loader = CustomLoader(this)
        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setOnClickListener { finish() }
        create = findViewById(R.id.button)
        create.text = resources.getString(R.string.create_account)
        create.setOnClickListener {
            if (name.text.toString().isEmpty()) {
                nameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                name.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                nameError.text = getString(R.string.please_enter_your_full_name)
                nameError.visibility = View.VISIBLE
                name.requestFocus()
            } else {
                if (name.text.toString().split(" ").size == 1 || name.text.toString()
                        .split(" ")[0] == "" || name.text.toString().split(
                        " "
                    )[1] == ""
                ) {
                    nameTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                    name.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                    )
                    nameError.text = getString(R.string.please_enter_your_first_and_last_name)
                    nameError.visibility = View.VISIBLE
                    name.requestFocus()
                }
            }

            if (email.text.toString().isEmpty()) {
                emailTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                email.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                emailError.text = getString(R.string.please_enter_your_email)
                emailError.visibility = View.VISIBLE
                if (!nameError.isVisible) {
                    email.requestFocus()
                }
            } else {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                    emailTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                    )
                    emailError.text = getString(R.string.please_enter_a_valid_email)
                    emailError.visibility = View.VISIBLE
                    if (!nameError.isVisible) {
                        email.requestFocus()
                    }
                }
            }

            if (dob.text.toString() == resources.getString(R.string.select_your_birth_date)) {
                dobTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                dob.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                dobError.text = getString(R.string.please_select_your_birth_date)
                dobError.visibility = View.VISIBLE
                if (!nameError.isVisible && !emailError.isVisible) {
                    dob.requestFocus()
                }
            }

            if (referral.text.toString().isNotEmpty() && referral.text.toString().length < 6) {
                referralTitle.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.darkRed_lightRed
                    )
                )
                referral.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                referralError.text = getString(R.string.please_enter_a_valid_referral_code)
                referralError.visibility = View.VISIBLE
                if (!nameError.isVisible && !emailError.isVisible && !dobError.isVisible) {
                    referral.requestFocus()
                }
            }

            if (!nameError.isVisible && !emailError.isVisible && !dobError.isVisible && !referralError.isVisible) {
                if (InternetCheck(this, parent).status()) {
                    loader.show(R.string.creating_your_account)
                    var jsonObject = JSONObject()
                    jsonObject = if (referral.text.toString().isNotEmpty()) jsonObject.put(
                        "phone_number", phoneNumber
                    ).put("full_name", name.text.toString().trim())
                        .put("dob", dob.text.toString().trim())
                        .put("email", email.text.toString().trim()).put(
                            "referral_code", referral.text.toString().trim()
                        ) else jsonObject.put("phone_number", phoneNumber)
                        .put("full_name", name.text.toString().trim())
                        .put("dob", dob.text.toString().trim())
                        .put("email", email.text.toString().trim())
                    ServerConnection(
                        this, "register", Request.Method.POST, "user/create", jsonObject
                    )
                }
            }
        }
        nameTitle = findViewById(R.id.name_title)
        name = findViewById(R.id.name)
        nameError = findViewById(R.id.name_error)
        name.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (name.hasFocus()) {
                    nameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    name.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    nameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    name.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (nameError.isVisible) {
                    nameError.visibility = View.GONE
                }
            }
        }
        name.setOnFocusChangeListener { _, hasFocus ->
            if (!nameError.isVisible) {
                if (hasFocus) {
                    nameTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    name.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    nameTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    name.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        emailTitle = findViewById(R.id.email_title)
        email = findViewById(R.id.email)
        emailError = findViewById(R.id.email_error)
        email.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (email.hasFocus()) {
                    emailTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    emailTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    email.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (emailError.isVisible) {
                    emailError.visibility = View.GONE
                }
            }
        }
        email.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (!emailError.isVisible) {
                    emailTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                }
            } else {
                if (!emailError.isVisible) {
                    emailTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    email.background = ContextCompat.getDrawable(
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
        dobError = findViewById(R.id.dob_error)
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
                if (dobError.isVisible) {
                    dobError.visibility = View.GONE
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
            if (!dobError.isVisible) {
                if (hasFocus) {
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
        referralTitle = findViewById(R.id.referral_title)
        referral = findViewById(R.id.referral)
        referralError = findViewById(R.id.referral_error)
        referral.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (referral.hasFocus()) {
                    referralTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    referral.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    referralTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    referral.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (referralError.isVisible) {
                    referralError.visibility = View.GONE
                }
            }
        }
        referral.setOnFocusChangeListener { _, hasFocus ->
            if (!referralError.isVisible) {
                if (hasFocus) {
                    referralTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    referral.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    referralTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    referral.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
    }

    fun registered(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(true)
            Session(this).devicePhoneNumber(phoneNumber)
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@RegistrationActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@RegistrationActivity,
                            parent,
                            getString(R.string.unusual_error_message),
                            "error"
                        )
                    }

                    in 400..499 -> {
                        if (statusCode == 404) {
                            referralTitle.setTextColor(
                                ContextCompat.getColor(
                                    this, R.color.darkRed_lightRed
                                )
                            )
                            referral.background = ContextCompat.getDrawable(
                                this,
                                R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                            )
                            referralError.text =
                                getString(R.string.please_enter_a_valid_referral_code)
                            referralError.visibility = View.VISIBLE
                            referral.requestFocus()
                        } else {
                            CustomSnackBar(
                                this@RegistrationActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@RegistrationActivity,
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