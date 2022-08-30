package invest.megalo.controller.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.volley.Request
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import invest.megalo.R
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.Session
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var ccp: CountryCodePicker
    private lateinit var phone: EditText
    private lateinit var proceed: ImageView
    private lateinit var proceedParent: FrameLayout
    private lateinit var loader: ProgressBar
    lateinit var errorMessage: TextView
    private var phoneFieldState: String = "normal"
    /*private val onActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            /*if (requestCode == 1) {
                val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                // credential.getId();  <-- will need to process phone number string
            }*/
        } else {

        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ccp = findViewById(R.id.ccp)
        phone = findViewById(R.id.phone)
        errorMessage = findViewById(R.id.error_message)
        phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (phone.hasFocus()) {
                    phone.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                } else {
                    phone.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                }
                errorMessage.text = ""
                errorMessage.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        phone.setOnFocusChangeListener { _, b ->
            if (b) {
                if (phoneFieldState == "normal") {
                    phone.setBackgroundResource(R.drawable.light_gray_solid_app_green_stroke_curved_corners)
                    phoneFieldState = "active"
                }
            } else {
                if (phoneFieldState == "active") {
                    phone.setBackgroundResource(R.drawable.light_gray_solid_curved_corners)
                    phoneFieldState = "normal"
                }
            }
        }
        ccp.registerCarrierNumberEditText(phone)
        loader = findViewById(R.id.loader)
        proceed = findViewById(R.id.proceed)
        proceedParent = findViewById(R.id.proceed_parent)
        proceedParent.setOnClickListener {
            if (phone.text.isEmpty()) {
                phone.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
                errorMessage.text = getString(R.string.empty_phone_field_error_message)
                errorMessage.visibility = View.VISIBLE
                phoneFieldState = "error"
            } else {
                if (ccp.isValidFullNumber) {
                    if (InternetCheck(this, findViewById(R.id.parent)).status()) {
                        loader.visibility = View.VISIBLE
                        proceed.visibility = View.INVISIBLE
                        ccp.setCcpClickable(false)
                        proceedParent.isEnabled = false
                        phone.isEnabled = false
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Session(this).deviceToken(task.result)
                                ServerConnection(
                                    this, "sendOtp", Request.Method.POST, "user/send_otp",
                                    JSONObject().put("phone_number", ccp.fullNumberWithPlus)
                                )
                            }
                        }.addOnFailureListener {
                            loader.visibility = View.INVISIBLE
                            proceed.visibility = View.VISIBLE
                            ccp.setCcpClickable(true)
                            proceedParent.isEnabled = true
                            phone.isEnabled = true
                            CustomSnackBar(
                                this@MainActivity,
                                findViewById(R.id.parent),
                                resources.getString(R.string.unknown_error_message),
                                "error"
                            )
                        }
                    }
                } else {
                    phone.setBackgroundResource(R.drawable.light_gray_solid_red_stroke_curved_corners)
                    errorMessage.text = getString(R.string.invalid_phone_number_error_message)
                    errorMessage.visibility = View.VISIBLE
                    phoneFieldState = "error"
                }
            }
        }

        if (Session(this).loggedIn()) {
            startActivity(Intent(this, Home::class.java))
        }
    }

    // Construct a request for phone numbers and show the picker
    /*private fun requestHint() {
        val hintRequest: HintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent: PendingIntent = Auth.CredentialsApi.getHintPickerIntent(
            apiClient, hintRequest
        )
        startIntentSenderForResult(
            intent.intentSender,
            RESOLVE_HINT, null, 0, 0, 0
        )
        SignInClient.getPhoneNumberHintIntent(GetPhoneNumberHintIntentRequest)
    }

    // Obtain the phone number from the result
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                // credential.getId();  <-- will need to process phone number string
            }
        }
    }*/

    fun otpSent(l: Int) {
        loader.visibility = View.INVISIBLE
        proceed.visibility = View.VISIBLE
        ccp.setCcpClickable(true)
        proceedParent.isEnabled = true
        phone.isEnabled = true
        if (l == 1) {
            val intent = Intent(this, OtpVerification::class.java)
            intent.putExtra("formatted_phone_number", ccp.formattedFullNumber)
            intent.putExtra("full_phone_number", ccp.fullNumberWithPlus)
            startActivity(intent)
        } else {
            CustomSnackBar(
                this@MainActivity,
                findViewById(R.id.parent),
                resources.getString(R.string.server_error_message),
                "error"
            )
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}