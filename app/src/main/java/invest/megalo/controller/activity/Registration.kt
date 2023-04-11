package invest.megalo.controller.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.model.*
import org.json.JSONObject


class Registration : AppCompatActivity() {
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val bundle = intent.extras
        if (bundle != null) {
            phoneNumber = bundle.getString("phone_number").toString()
        }

        loader = CustomLoader(this)
        findViewById<ImageView>(R.id.back).setOnClickListener { finish() }
        create = findViewById(R.id.button)
        create.text = resources.getString(R.string.create_account)
        create.setOnClickListener {
            if (name.text.toString().isEmpty()) {
                nameTitle.setTextColor(ColorResCompat(this, R.attr.darkRed_lightRed).get())
                name.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_red_stroke_curved_corners
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
                    nameTitle.setTextColor(ColorResCompat(this, R.attr.darkRed_lightRed).get())
                    name.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_red_stroke_curved_corners
                    )
                    nameError.text = getString(R.string.please_enter_your_first_and_last_name)
                    nameError.visibility = View.VISIBLE
                    name.requestFocus()
                }
            }

            if (email.text.toString().isEmpty()) {
                emailTitle.setTextColor(ColorResCompat(this, R.attr.darkRed_lightRed).get())
                email.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_red_stroke_curved_corners
                )
                emailError.text = getString(R.string.please_enter_your_email)
                emailError.visibility = View.VISIBLE
                if (!nameError.isVisible) {
                    email.requestFocus()
                }
            } else {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                    emailTitle.setTextColor(ColorResCompat(this, R.attr.darkRed_lightRed).get())
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_red_stroke_curved_corners
                    )
                    emailError.text = getString(R.string.please_enter_a_valid_email)
                    emailError.visibility = View.VISIBLE
                    if (!nameError.isVisible) {
                        email.requestFocus()
                    }
                }
            }

            if (dob.text.toString() == resources.getString(R.string.select_your_birth_date)) {
                dobTitle.setTextColor(ColorResCompat(this, R.attr.darkRed_lightRed).get())
                dob.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_red_stroke_curved_corners
                )
                dobError.text = getString(R.string.please_select_your_birth_date)
                dobError.visibility = View.VISIBLE
                if (!nameError.isVisible && !emailError.isVisible) {
                    dob.requestFocus()
                }
            }

            if (referral.text.toString().isNotEmpty()) {
                if (referral.text.toString().length < 6) {
                    referralTitle.setTextColor(ColorResCompat(this, R.attr.darkRed_lightRed).get())
                    referral.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_red_stroke_curved_corners
                    )
                    referralError.text = getString(R.string.please_enter_a_valid_referral_code)
                    referralError.visibility = View.VISIBLE
                    if (!nameError.isVisible && !emailError.isVisible && !dobError.isVisible) {
                        referral.requestFocus()
                    }
                }
            }

            if (!nameError.isVisible && !emailError.isVisible && !dobError.isVisible && !referralError.isVisible) {
                if (InternetCheck(this, findViewById(R.id.parent)).status()) {
                    loader.show(getString(R.string.creating_your_account))
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
                    nameTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    name.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    nameTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
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
                    nameTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    name.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    nameTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
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
                    emailTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    emailTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
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
                    emailTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                }
            } else {
                if (!emailError.isVisible) {
                    emailTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
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
                    dobTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    dob.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    dobTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
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
                Dialog(
                    v,
                    this,
                    resources.getString(R.string.date_picker),
                    resources.getString(R.string.select_your_birth_date),
                    "",
                    getString(R.string.select),
                    getString(R.string.cancel),
                    false
                ).show()
            }
            false
        }
        dob.setOnFocusChangeListener { _, hasFocus ->
            if (!dobError.isVisible) {
                if (hasFocus) {
                    dobTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    dob.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    dobTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
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
                    referralTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    referral.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    referralTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
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
                    referralTitle.setTextColor(ContextCompat.getColor(this, R.color.app_green))
                    referral.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    referralTitle.setTextColor(ColorResCompat(this, R.attr.black_white).get())
                    referral.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
    }

    fun registered(l: Int, statusCode: Int? = 0) {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(true)
            startActivity(Intent(this, Home::class.java))
        } else {
            when (statusCode) {
                0 -> {
                    CustomSnackBar(
                        this@Registration,
                        findViewById(R.id.parent),
                        getString(R.string.unusual_error_message),
                        "error"
                    )
                }
                in 400..499 -> {
                    if (statusCode == 404) {
                        referralTitle.setTextColor(
                            ColorResCompat(
                                this, R.attr.darkRed_lightRed
                            ).get()
                        )
                        referral.background = ContextCompat.getDrawable(
                            this, R.drawable.white_black_solid_red_stroke_curved_corners
                        )
                        referralError.text = getString(R.string.please_enter_a_valid_referral_code)
                        referralError.visibility = View.VISIBLE
                        referral.requestFocus()
                    } else {
                        CustomSnackBar(
                            this@Registration,
                            findViewById(R.id.parent),
                            getString(R.string.client_error_message),
                            "error"
                        )
                    }
                }
                else -> {
                    CustomSnackBar(
                        this@Registration,
                        findViewById(R.id.parent),
                        getString(R.string.server_error_message),
                        "error"
                    )
                }
            }
        }
    }
}