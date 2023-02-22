package invest.megalo.controller.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import invest.megalo.R
import invest.megalo.adapter.OnBoardingSlidesAdapter
import invest.megalo.model.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var ccp: CountryCodePicker
    private lateinit var phone: EditText
    private lateinit var proceed: ImageView
    private lateinit var proceedParent: FrameLayout
    private lateinit var loader: ProgressBar
    lateinit var errorMessage: TextView
    private var phoneFieldState: String = "normal"
    private lateinit var recyclerView: RecyclerView
    lateinit var firstTextParent: LinearLayout
    lateinit var secondTextParent: LinearLayout
    lateinit var thirdTextParent: LinearLayout
    lateinit var firstIndicator: TextView
    lateinit var secondIndicator: TextView
    lateinit var thirdIndicator: TextView
    private val phoneNumberHintIntentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val phoneNumber = Identity.getSignInClient(this).getPhoneNumberFromIntent(result.data)
            ccp.fullNumber = phoneNumber
            Session(this).devicePhoneNumber(phoneNumber)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstTextParent = findViewById(R.id.first_text_parent)
        secondTextParent = findViewById(R.id.second_text_parent)
        thirdTextParent = findViewById(R.id.third_text_parent)
        firstIndicator = findViewById(R.id.first_indicator)
        secondIndicator = findViewById(R.id.second_indicator)
        thirdIndicator = findViewById(R.id.third_indicator)
        recyclerView = findViewById(R.id.recycler_view)
        val images = ArrayList<Int>()
        images.add(R.drawable.first_slide)
        images.add(R.drawable.second_slide)
        images.add(R.drawable.third_slide)
        recyclerView.apply {
            PagerSnapHelper().attachToRecyclerView(this)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val llm = recyclerView.layoutManager as LinearLayoutManager
                    when (llm.findFirstCompletelyVisibleItemPosition()) {
                        0 -> {
                            firstTextParent.visibility = View.VISIBLE
                            secondTextParent.visibility = View.GONE
                            thirdTextParent.visibility = View.GONE

                            firstIndicator.alpha = 1f
                            secondIndicator.alpha = 0.40f
                            thirdIndicator.alpha = 0.40f
                        }

                        1 -> {
                            firstTextParent.visibility = View.GONE
                            secondTextParent.visibility = View.VISIBLE
                            thirdTextParent.visibility = View.GONE

                            firstIndicator.alpha = 0.40f
                            secondIndicator.alpha = 1f
                            thirdIndicator.alpha = 0.40f
                        }

                        2 -> {
                            firstTextParent.visibility = View.GONE
                            secondTextParent.visibility = View.GONE
                            thirdTextParent.visibility = View.VISIBLE

                            firstIndicator.alpha = 0.40f
                            secondIndicator.alpha = 0.40f
                            thirdIndicator.alpha = 1f
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                }
            })
            adapter = OnBoardingSlidesAdapter(this@MainActivity, images)
        }

        ccp = findViewById(R.id.ccp)
        phone = findViewById(R.id.phone)
        errorMessage = findViewById(R.id.error_message)
        phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phoneFieldState = if (phone.hasFocus()) {
                    phone.setBackgroundResource(R.drawable.light_gray_night_solid_app_green_stroke_curved_corners)
                    "active"
                } else {
                    phone.setBackgroundResource(R.drawable.light_gray_night_solid_light_gray_stroke_curved_corners)
                    "normal"
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
                    phone.setBackgroundResource(R.drawable.light_gray_night_solid_app_green_stroke_curved_corners)
                    phoneFieldState = "active"
                }
            } else {
                if (phoneFieldState == "active") {
                    phone.setBackgroundResource(R.drawable.light_gray_night_solid_light_gray_stroke_curved_corners)
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
                phone.setBackgroundResource(R.drawable.light_gray_night_solid_red_stroke_curved_corners)
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
                                    this,
                                    "sendOtp",
                                    Request.Method.POST,
                                    "user/send_otp",
                                    JSONObject().put("type", "sms")
                                        .put("phone_number", ccp.fullNumberWithPlus)
                                )
                            }
                        }.addOnFailureListener {
                            loader.visibility = View.INVISIBLE
                            proceed.visibility = View.VISIBLE
                            ccp.setCcpClickable(true)
                            proceedParent.isEnabled = true
                            phone.isEnabled = true
                            it.printStackTrace()
                            CustomSnackBar(
                                this@MainActivity,
                                findViewById(R.id.parent),
                                resources.getString(R.string.unknown_error_message),
                                "error"
                            )
                        }
                    }
                } else {
                    phone.setBackgroundResource(R.drawable.light_gray_night_solid_red_stroke_curved_corners)
                    errorMessage.text = getString(R.string.invalid_phone_number_error_message)
                    errorMessage.visibility = View.VISIBLE
                    phoneFieldState = "error"
                }
            }
        }

        val scheduler: JobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        if (scheduler.getPendingJob(1) == null) {
            scheduler.schedule(
                JobInfo.Builder(1, ComponentName(this, DeviceTokenUpdatingService::class.java))
                    .setPeriodic(7 * 24 * 60 * 60 * 1000L).setPersisted(true).build()
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "General"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val attribute =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            val sound = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.notifications
            )
            val mChannel = NotificationChannel("megalo_general_channel_id", name, importance)
            mChannel.setSound(sound, attribute)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        if (Session(this).loggedIn()) {
            startActivity(Intent(this, Home::class.java))
        } else if (Session(this).devicePhoneNumber() != "") {
            ccp.fullNumber = Session(this).devicePhoneNumber()
        } else {
            requestPhoneNumberHint()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    Dialog(
                        findViewById(R.id.parent),
                        this,
                        getString(R.string.permission_rationale),
                        getString(R.string.notifications_permission_required),
                        getString(R.string.notifications_permission_rationale_text),
                        getString(R.string.proceed),
                        getString(R.string.cancel),
                        false
                    )
                } else {
                    requestPermission()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun requestPhoneNumberHint() {
        val request: GetPhoneNumberHintIntentRequest =
            GetPhoneNumberHintIntentRequest.builder().build()

        Identity.getSignInClient(this).getPhoneNumberHintIntent(request).addOnSuccessListener {
            phoneNumberHintIntentResultLauncher.launch(
                IntentSenderRequest.Builder(it.intentSender).build()
            )
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    fun otpSent(l: Int, statusCode: Int? = 0) {
        loader.visibility = View.INVISIBLE
        proceed.visibility = View.VISIBLE
        ccp.setCcpClickable(true)
        proceedParent.isEnabled = true
        phone.isEnabled = true
        if (l == 1) {
            Session(this).devicePhoneNumber(ccp.fullNumberWithPlus)
            val intent = Intent(this, OtpVerification::class.java)
            intent.putExtra("formatted_phone_number", ccp.formattedFullNumber)
            intent.putExtra("full_phone_number", ccp.fullNumberWithPlus)
            startActivity(intent)
        } else {
            when (statusCode) {
                0 -> {
                    CustomSnackBar(
                        this@MainActivity,
                        findViewById(R.id.parent),
                        getString(R.string.unusual_error_message),
                        "error"
                    )
                }
                in 400..499 -> {
                    CustomSnackBar(
                        this@MainActivity,
                        findViewById(R.id.parent),
                        getString(R.string.client_error_message),
                        "error"
                    )
                }
                else -> {
                    CustomSnackBar(
                        this@MainActivity,
                        findViewById(R.id.parent),
                        getString(R.string.server_error_message),
                        "error"
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}