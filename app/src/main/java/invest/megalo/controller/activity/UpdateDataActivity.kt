package invest.megalo.controller.activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.hbb20.CountryCodePicker
import invest.megalo.R
import invest.megalo.model.CustomLoader
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.SetAppTheme
import org.json.JSONObject

class UpdateDataActivity : AppCompatActivity() {
    private var type = ""
    private lateinit var proceed: AppCompatButton
    private lateinit var loader: CustomLoader
    private lateinit var back: ImageView
    private lateinit var subTitle: TextView
    private lateinit var error: TextView
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var phoneContainer: RelativeLayout
    private lateinit var ccp: CountryCodePicker
    private lateinit var parent: LinearLayout
    private lateinit var title: TextView
    private lateinit var dialogParent: CardView
    private lateinit var dialogTitle: TextView
    private lateinit var dialogDismiss: ImageView
    private lateinit var dialogSearch: EditText
    private lateinit var dialogClearQuery: ImageView
    private lateinit var dialogList: RecyclerView
    private lateinit var dialogNoResult: TextView
    private var ccpDialogHasShown: Boolean = false
    private val updateDataResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 3) {
            val intent = Intent(this, EditProfileActivity::class.java)
            if (type == "email") {
                intent.putExtra("newEmail", email.text.toString().trim())
                setResult(1, intent)
            } else {
                intent.putExtra("newPhoneNumber", ccp.fullNumber)
                setResult(2, intent)
            }
            finish()
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

        title.text =
            if (type == "email") getString(R.string.update_email) else getString(R.string.update_phone_number)
        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        back.contentDescription = getString(R.string.go_back)
        back.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        back.setImageResource(R.drawable.arrow_back)

        if (type == "email") {
            subTitle.text = getString(R.string.new_email_address)
            if (!error.isVisible) {
                if (email.hasFocus()) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    email.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            } else {
                subTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                email.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                if (email.text.toString().isEmpty()) {
                    error.text = getString(R.string.please_enter_your_email)
                } else {
                    error.text = getString(R.string.please_enter_a_valid_email)
                }
            }
            email.setTextColor(ContextCompat.getColor(this, R.color.black_white))
            email.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
            email.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
            )
        } else {
            subTitle.text = getString(R.string.new_phone_number)
            if (!error.isVisible) {
                if (phone.hasFocus()) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    phoneContainer.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    phoneContainer.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            } else {
                subTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                phoneContainer.background = ContextCompat.getDrawable(
                    this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                if (phone.text.toString().isEmpty()) {
                    error.text = getString(R.string.please_enter_your_phone_number)
                } else {
                    error.text = getString(R.string.please_enter_a_valid_phone_number)
                }
            }
            phone.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_curved_right_corners
            )
            phone.setTextColor(ContextCompat.getColor(this, R.color.black_white))
            phone.setHintTextColor(ContextCompat.getColor(this, R.color.darkHint_lightHint))
            phone.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
            )

            ccp.background = ContextCompat.getDrawable(
                this, R.drawable.white_black_solid_curved_left_corners
            )
            ccp.contentColor = ContextCompat.getColor(
                this, R.color.black_white
            )
            ccp.setFlagBorderColor(
                ContextCompat.getColor(
                    this, R.color.darkGrey_lightGrey
                )
            )
            ccp.setTextSize(resources.getDimension(R.dimen.normal_text).toInt())

            ccp.setDialogBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.white_night
                )
            )
            ccp.setDialogTextColor(
                ContextCompat.getColor(
                    this, R.color.black_white
                )
            )
            if (ccpDialogHasShown) {
                dialogParent.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this, R.color.white_night
                    )
                )
                dialogClearQuery.setColorFilter(
                    ContextCompat.getColor(
                        this, R.color.black_white
                    )
                )
                dialogDismiss.setColorFilter(
                    ContextCompat.getColor(
                        this, R.color.black_white
                    )
                )
                dialogTitle.text = getString(R.string.select_a_country)
                dialogTitle.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
                )
                dialogTitle.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.black_white
                    )
                )
                dialogNoResult.text = getString(R.string.results_not_found)
                dialogNoResult.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
                )
                dialogNoResult.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.black_white
                    )
                )
                dialogSearch.hint = getString(R.string.search)
                dialogSearch.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
                )
                dialogSearch.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.black_white
                    )
                )
                dialogSearch.setHintTextColor(
                    ContextCompat.getColor(
                        this, R.color.darkHint_lightHint
                    )
                )
                if (dialogSearch.hasFocus()) {
                    dialogSearch.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    dialogSearch.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                dialogList.adapter?.notifyItemRangeChanged(0, dialogList.adapter!!.itemCount)

            }
        }
        subTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        error.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
        error.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        proceed.text = resources.getString(R.string.proceed)
        proceed.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
        loader.onConfigurationChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_data)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("type") != null) {
                type = bundle.getString("type").toString()
            }
        }

        parent = findViewById(R.id.parent)
        title = findViewById(R.id.title)
        back = findViewById(R.id.back)
        back.visibility = View.VISIBLE
        back.setOnClickListener { finish() }
        phoneContainer = findViewById(R.id.phone_container)

        loader = CustomLoader(this)

        findViewById<ImageView>(R.id.logo).visibility = View.GONE
        title.text =
            if (type == "email") getString(R.string.update_email) else getString(R.string.update_phone_number)

        proceed = findViewById(R.id.button)
        proceed.text = getString(R.string.proceed)
        proceed.setOnClickListener {
            if (type == "email") {
                if (email.text.toString().isEmpty()) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                    )
                    error.text = getString(R.string.please_enter_your_email)
                    error.visibility = View.VISIBLE
                } else {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                            .matches()
                    ) {
                        if (InternetCheck(this, parent).status()) {
                            loader.show(R.string.sending_verification_code)
                            ServerConnection(
                                this,
                                "sendOtp",
                                Request.Method.POST,
                                "user/send_otp",
                                JSONObject().put("type", type)
                                    .put("email", email.text.toString().trim())
                            )
                        }
                    } else {
                        subTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkRed_lightRed
                            )
                        )
                        email.background = ContextCompat.getDrawable(
                            this,
                            R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                        )
                        error.text = getString(R.string.please_enter_a_valid_email)
                        error.visibility = View.VISIBLE
                    }
                }
            } else {
                if (phone.text.toString().isEmpty()) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.darkRed_lightRed))
                    phoneContainer.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                    )
                    error.text = getString(R.string.please_enter_your_phone_number)
                    error.visibility = View.VISIBLE
                } else {
                    if (ccp.isValidFullNumber) {
                        if (InternetCheck(this, parent).status()) {
                            loader.show(R.string.sending_verification_code)
                            ServerConnection(
                                this,
                                "sendOtp",
                                Request.Method.POST,
                                "user/send_otp",
                                JSONObject().put("type", type)
                                    .put("phone_number", ccp.fullNumberWithPlus).put("update", true)
                            )
                        }
                    } else {
                        subTitle.setTextColor(
                            ContextCompat.getColor(
                                this, R.color.darkRed_lightRed
                            )
                        )
                        phoneContainer.background = ContextCompat.getDrawable(
                            this,
                            R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                        )
                        error.text = getString(R.string.please_enter_a_valid_phone_number)
                        error.visibility = View.VISIBLE
                    }
                }
            }
        }

        subTitle = findViewById(R.id.sub_title)
        error = findViewById(R.id.error)
        email = findViewById(R.id.email)
        email.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (email.hasFocus()) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    email.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (error.isVisible) {
                    error.visibility = View.GONE
                }
            }
        }
        email.setOnFocusChangeListener { _, hasFocus ->
            if (!error.isVisible) {
                if (hasFocus) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    email.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    email.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        phone = findViewById(R.id.phone)
        phone.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (phone.hasFocus()) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    phoneContainer.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    phoneContainer.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
                if (error.isVisible) {
                    error.visibility = View.GONE
                }
            }
        }
        phone.setOnFocusChangeListener { _, hasFocus ->
            if (!error.isVisible) {
                if (hasFocus) {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.appGreen))
                    phoneContainer.background = ContextCompat.getDrawable(
                        this, R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    subTitle.setTextColor(ContextCompat.getColor(this, R.color.black_white))
                    phoneContainer.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        ccp = findViewById(R.id.ccp)
        ccp.setDialogEventsListener(object : CountryCodePicker.DialogEventsListener {
            override fun onCcpDialogOpen(dialog: Dialog?) {
                dialogParent = dialog?.findViewById(com.hbb20.R.id.cardViewRoot) as CardView
                dialogTitle = dialog.findViewById(com.hbb20.R.id.textView_title) as TextView
                dialogDismiss = dialog.findViewById(com.hbb20.R.id.img_dismiss) as ImageView
                dialogSearch = dialog.findViewById(com.hbb20.R.id.editText_search) as EditText
                dialogClearQuery = dialog.findViewById(com.hbb20.R.id.img_clear_query) as ImageView
                dialogNoResult = dialog.findViewById(com.hbb20.R.id.textView_noresult) as TextView
                dialogList =
                    dialog.findViewById(com.hbb20.R.id.recycler_countryDialog) as RecyclerView

                dialogTitle.text = getString(R.string.select_a_country)
                dialogTitle.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
                )

                dialogSearch.hint = getString(R.string.search)
                dialogSearch.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
                )
                dialogSearch.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        this@UpdateDataActivity.resources.getDimension(R.dimen.normal_padding),
                        this@UpdateDataActivity.resources.displayMetrics
                    ).toInt()
                )
                dialogSearch.setHintTextColor(
                    ContextCompat.getColor(
                        this@UpdateDataActivity, R.color.darkHint_lightHint
                    )
                )
                dialogSearch.background = ContextCompat.getDrawable(
                    this@UpdateDataActivity,
                    R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )

                dialogSearch.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        dialogSearch.background = ContextCompat.getDrawable(
                            this@UpdateDataActivity,
                            R.drawable.white_black_solid_app_green_stroke_curved_corners
                        )
                    } else {
                        dialogSearch.background = ContextCompat.getDrawable(
                            this@UpdateDataActivity,
                            R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                        )
                    }
                }

                dialogNoResult.text = getString(R.string.results_not_found)
                dialogNoResult.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
                )
                ccpDialogHasShown = true
            }

            override fun onCcpDialogDismiss(dialogInterface: DialogInterface?) {

            }

            override fun onCcpDialogCancel(dialogInterface: DialogInterface?) {

            }
        })
        ccp.registerCarrierNumberEditText(phone)

        if (type == "email") {
            subTitle.text = getString(R.string.new_email_address)
            email.visibility = View.VISIBLE
            phoneContainer.visibility = View.GONE
        } else {
            subTitle.text = getString(R.string.new_phone_number)
            email.visibility = View.GONE
            phoneContainer.visibility = View.VISIBLE
        }
    }

    fun otpSent(l: Int, statusCode: Int? = 0, message: String = "") {
        loader.dismiss()
        if (l == 1) {
            val intent = Intent(this, OtpVerificationActivity::class.java)
            intent.putExtra("type", type)
            if (type == "email") {
                intent.putExtra("email", email.text.toString().trim())
            } else {
                intent.putExtra("formatted_phone_number", ccp.formattedFullNumber)
                intent.putExtra("full_phone_number", ccp.fullNumberWithPlus)
                intent.putExtra("update", true)
            }
            updateDataResultLauncher.launch(intent)
        } else {
            if (message != "") {
                CustomSnackBar(
                    this@UpdateDataActivity, parent, message, "error"
                )
            } else {
                when (statusCode) {
                    0 -> {
                        CustomSnackBar(
                            this@UpdateDataActivity,
                            parent,
                            getString(R.string.unusual_error_message),
                            "error"
                        )
                    }

                    in 400..499 -> {
                        if (statusCode == 409 && type == "sms") {
                            subTitle.setTextColor(
                                ContextCompat.getColor(
                                    this, R.color.darkRed_lightRed
                                )
                            )
                            phoneContainer.background = ContextCompat.getDrawable(
                                this,
                                R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                            )
                            error.text = getString(R.string.this_phone_number_is_already_taken)
                            error.visibility = View.VISIBLE
                        } else if (statusCode == 420) {
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            CustomSnackBar(
                                this@UpdateDataActivity,
                                parent,
                                getString(R.string.client_error_message),
                                "error"
                            )
                        }
                    }

                    else -> {
                        CustomSnackBar(
                            this@UpdateDataActivity,
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