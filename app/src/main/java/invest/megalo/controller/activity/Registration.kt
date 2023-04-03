package invest.megalo.controller.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.android.volley.Request
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import invest.megalo.R
import invest.megalo.model.*
import org.json.JSONObject


class Registration : AppCompatActivity() {
    private lateinit var nameLayout: TextInputLayout
    private lateinit var name: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var email: TextInputEditText
    private lateinit var dobError: TextView
    lateinit var dob: TextView
    private lateinit var referralLayout: TextInputLayout
    private lateinit var referral: TextInputEditText
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
                nameLayout.error = getString(R.string.please_enter_your_full_name)
                nameLayout.requestFocus()
            } else {
                if (name.text.toString().split(" ").size == 1 || name.text.toString()
                        .split(" ")[0] == "" || name.text.toString().split(
                        " "
                    )[1] == ""
                ) {
                    nameLayout.error = getString(R.string.please_enter_your_first_and_last_name)
                    nameLayout.requestFocus()
                }
            }

            if (email.text.toString().isEmpty()) {
                emailLayout.error = getString(R.string.please_enter_your_email)
                if (!nameLayout.isErrorEnabled) {
                    emailLayout.requestFocus()
                }
            } else {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                    emailLayout.error = getString(R.string.please_enter_a_valid_email)
                    if (!nameLayout.isErrorEnabled) {
                        emailLayout.requestFocus()
                    }
                }
            }

            if (dob.text.toString() == resources.getString(R.string.select_your_birth_date)) {
                dob.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_red_stroke_curved_corners
                )
                dobError.text = getString(R.string.please_select_your_birth_date)
                dobError.visibility = View.VISIBLE
                if (!nameLayout.isErrorEnabled && !emailLayout.isErrorEnabled) {
                    dob.requestFocus()
                }
            }

            if (referral.text.toString().isNotEmpty()) {
                if (referral.text.toString().length < 6) {
                    referralLayout.error = getString(R.string.please_enter_a_valid_referral_code)
                    if (!nameLayout.isErrorEnabled && !emailLayout.isErrorEnabled && !dobError.isVisible) {
                        referralLayout.requestFocus()
                    }
                }
            }

            if (!nameLayout.isErrorEnabled && !emailLayout.isErrorEnabled && !dobError.isVisible && !referralLayout.isErrorEnabled) {
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
        nameLayout = findViewById(R.id.name_layout)
        name = findViewById(R.id.name)
        name.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (nameLayout.isErrorEnabled) {
                    nameLayout.isErrorEnabled = false
                }
            }
        }
        emailLayout = findViewById(R.id.email_layout)
        email = findViewById(R.id.email)
        email.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (emailLayout.isErrorEnabled) {
                    emailLayout.isErrorEnabled = false
                }
            }
        }
        email.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        dobError = findViewById(R.id.dob_error)
        dob = findViewById(R.id.dob)
        dob.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (dob.hasFocus()) {
                    dob.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    dob.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_black_disabled_white_disabled_stroke_curved_corners
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
                )
            }
            false
        }
        dob.setOnFocusChangeListener { _, hasFocus ->
            if (!dobError.isVisible) {
                if (hasFocus) {
                    dob.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    dob.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_black_disabled_white_disabled_stroke_curved_corners
                    )
                }
            }
        }
        referralLayout = findViewById(R.id.referral_layout)
        referral = findViewById(R.id.referral)
        referral.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (referralLayout.isErrorEnabled) {
                    referralLayout.isErrorEnabled = false
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
                        referralLayout.error =
                            getString(R.string.please_enter_a_valid_referral_code)
                        if (!nameLayout.isErrorEnabled && !emailLayout.isErrorEnabled && !dobError.isVisible) {
                            referralLayout.requestFocus()
                        }

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