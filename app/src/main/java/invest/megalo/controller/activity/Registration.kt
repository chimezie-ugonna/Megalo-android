package invest.megalo.controller.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.model.*
import org.json.JSONObject


class Registration : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var email: EditText
    lateinit var dob: EditText
    private lateinit var proceed: FrameLayout
    private lateinit var back: FrameLayout
    private var phoneNumber = ""
    lateinit var nameErrorMessage: TextView
    private var nameFieldState: String = "normal"
    lateinit var emailErrorMessage: TextView
    private var emailFieldState: String = "normal"
    private lateinit var dobErrorMessage: TextView
    private var dobFieldState: String = "normal"
    private lateinit var loader: CustomLoader

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val bundle = intent.extras
        phoneNumber = bundle?.get("phone_number").toString()

        loader = CustomLoader(this)
        name = findViewById(R.id.name)
        nameErrorMessage = findViewById(R.id.name_error_message)
        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (name.hasFocus()) {
                    name.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                    nameFieldState = "active"
                    if (dobFieldState == "active") {
                        dob.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                        dobFieldState = "normal"
                    }
                } else {
                    name.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                    nameFieldState = "normal"
                }
                nameErrorMessage.text = ""
                nameErrorMessage.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        name.setOnFocusChangeListener { _, b ->
            if (b) {
                if (nameFieldState == "normal") {
                    name.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                    nameFieldState = "active"
                    if (dobFieldState == "active") {
                        dob.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                        dobFieldState = "normal"
                    }
                }
            } else {
                if (nameFieldState == "active") {
                    name.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                    nameFieldState = "normal"
                }
            }
        }

        email = findViewById(R.id.email)
        emailErrorMessage = findViewById(R.id.email_error_message)
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (email.hasFocus()) {
                    email.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                    emailFieldState = "active"
                    if (dobFieldState == "active") {
                        dob.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                        dobFieldState = "normal"
                    }
                } else {
                    email.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                    emailFieldState = "normal"
                }
                emailErrorMessage.text = ""
                emailErrorMessage.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        email.setOnFocusChangeListener { _, b ->
            if (b) {
                if (emailFieldState == "normal") {
                    email.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                    emailFieldState = "active"
                    if (dobFieldState == "active") {
                        dob.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                        dobFieldState = "normal"
                    }
                }
            } else {
                if (emailFieldState == "active") {
                    email.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                    emailFieldState = "normal"
                }
            }
        }

        dob = findViewById(R.id.dob)
        dobErrorMessage = findViewById(R.id.dob_error_message)
        dob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                name.clearFocus()
                email.clearFocus()
                dob.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                dobFieldState = "active"
                dobErrorMessage.text = ""
                dobErrorMessage.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        dob.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                Dialog(
                    v,
                    this,
                    resources.getString(R.string.date_picker),
                    resources.getString(R.string.select_your_birthdate),
                    "",
                    getString(R.string.select),
                    getString(R.string.cancel),
                    false
                )
            }
            false
        }


        back = findViewById(R.id.back)
        back.setOnClickListener { finish() }

        proceed = findViewById(R.id.proceed)
        proceed.setOnClickListener {
            if (name.text.isEmpty()) {
                name.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
                nameErrorMessage.text = getString(R.string.please_enter_your_full_name)
                nameErrorMessage.visibility = View.VISIBLE
                nameFieldState = "error"
            } else {
                if (name.text.split(" ").size == 1 || name.text.split(" ")[0] == "" || name.text.split(
                        " "
                    )[1] == ""
                ) {
                    name.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
                    nameErrorMessage.text =
                        getString(R.string.please_enter_your_first_and_last_name)
                    nameErrorMessage.visibility = View.VISIBLE
                    nameFieldState = "error"
                } else {
                    if (name.hasFocus()) {
                        name.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                    } else {
                        name.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                    }
                    nameErrorMessage.text = ""
                    nameErrorMessage.visibility = View.GONE
                    nameFieldState = "normal"
                }
            }

            if (email.text.isEmpty()) {
                email.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
                emailErrorMessage.text = getString(R.string.please_enter_your_email)
                emailErrorMessage.visibility = View.VISIBLE
                emailFieldState = "error"
            } else {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
                    email.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
                    emailErrorMessage.text = getString(R.string.please_enter_a_valid_email)
                    emailErrorMessage.visibility = View.VISIBLE
                    emailFieldState = "error"
                } else {
                    if (email.hasFocus()) {
                        email.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                    } else {
                        email.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                    }
                    emailErrorMessage.text = ""
                    emailErrorMessage.visibility = View.GONE
                    emailFieldState = "normal"
                }
            }

            if (dob.text.isEmpty()) {
                dob.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
                dobErrorMessage.text = getString(R.string.please_select_your_birthdate)
                dobErrorMessage.visibility = View.VISIBLE
                dobFieldState = "error"
            }

            if (name.text.isNotEmpty() && nameFieldState != "error" && email.text.isNotEmpty() && emailFieldState != "error" && dob.text.isNotEmpty() && dobFieldState != "error") {
                if (InternetCheck(this, findViewById(R.id.parent)).status()) {
                    loader.show(getString(R.string.creating_your_account))
                    ServerConnection(
                        this, "register", Request.Method.POST, "user/create",
                        JSONObject().put("phone_number", phoneNumber)
                            .put("full_name", name.text.trim())
                            .put("dob", dob.text.trim())
                            .put("email", email.text.trim())
                            .put("type", "user")
                    )
                }
            }
        }
    }

    fun registered(l: Int) {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(true)
            startActivity(Intent(this, Home::class.java))
        } else {
            CustomSnackBar(
                this@Registration,
                findViewById(R.id.parent),
                resources.getString(R.string.server_error_message),
                "error"
            )
        }
    }
}