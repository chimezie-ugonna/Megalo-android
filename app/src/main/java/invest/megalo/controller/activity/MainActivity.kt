package invest.megalo.controller.activity

import android.Manifest
import android.annotation.SuppressLint
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
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import invest.megalo.R
import invest.megalo.controller.fragment.*
import invest.megalo.model.*


class MainActivity : AppCompatActivity() {
    private lateinit var firstIndicator: LinearProgressIndicator
    private var firstIndicatorProgress = 0
    private lateinit var secondIndicator: LinearProgressIndicator
    private var secondIndicatorProgress = 0
    private lateinit var thirdIndicator: LinearProgressIndicator
    private var thirdIndicatorProgress = 0
    private lateinit var onboardingSlide1Fragment: OnboardingSlide1Fragment
    private lateinit var onboardingSlide2Fragment: OnboardingSlide2Fragment
    private lateinit var onboardingSlide3Fragment: OnboardingSlide3Fragment
    private lateinit var onboardingSlide4Fragment: OnboardingSlide4Fragment
    private lateinit var handler: Handler
    lateinit var indicatorContainer: LinearLayout
    private lateinit var prev: View
    private lateinit var next: View
    lateinit var adjustmentContainer: LinearLayout
    private var pressTime = 0L
    private var limit = 500L

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstIndicator = findViewById(R.id.first_indicator)
        secondIndicator = findViewById(R.id.second_indicator)
        thirdIndicator = findViewById(R.id.third_indicator)
        indicatorContainer = findViewById(R.id.indicator_container)
        prev = findViewById(R.id.prev)
        next = findViewById(R.id.next)
        adjustmentContainer = findViewById(R.id.adjustment_container)

        onboardingSlide1Fragment = OnboardingSlide1Fragment()
        onboardingSlide2Fragment = OnboardingSlide2Fragment()
        onboardingSlide3Fragment = OnboardingSlide3Fragment()
        onboardingSlide4Fragment = OnboardingSlide4Fragment()

        handler = Handler(Looper.getMainLooper())

        prev.setOnClickListener {
            if (onboardingSlide1Fragment.isAdded) {
                firstIndicatorProgress = 0
                secondIndicatorProgress = 0
                thirdIndicatorProgress = 0
            } else if (onboardingSlide2Fragment.isAdded) {
                firstIndicatorProgress = 0
                secondIndicatorProgress = 0
                thirdIndicatorProgress = 0
            } else if (onboardingSlide3Fragment.isAdded) {
                secondIndicatorProgress = 0
                thirdIndicatorProgress = 0
            }
            handler.removeCallbacksAndMessages(null)
            incrementProgress()
        }
        prev.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    handler.removeCallbacksAndMessages(null)
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    incrementProgress()
                    return@setOnTouchListener limit < now - pressTime
                }
            }
            return@setOnTouchListener false
        }
        next.setOnClickListener {
            if (onboardingSlide1Fragment.isAdded) {
                firstIndicatorProgress = 99
                secondIndicatorProgress = 0
                thirdIndicatorProgress = 0
            } else if (onboardingSlide2Fragment.isAdded) {
                firstIndicatorProgress = 100
                secondIndicatorProgress = 99
                thirdIndicatorProgress = 0
            } else if (onboardingSlide3Fragment.isAdded) {
                firstIndicatorProgress = 100
                secondIndicatorProgress = 100
                thirdIndicatorProgress = 99
            }
            handler.removeCallbacksAndMessages(null)
            incrementProgress()
        }
        next.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    handler.removeCallbacksAndMessages(null)
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    incrementProgress()
                    return@setOnTouchListener limit < now - pressTime
                }
            }
            return@setOnTouchListener false
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

        if (Session(this).onboarded()) {
            if (Session(this).loggedIn()) {
                startActivity(Intent(this, Home::class.java))
            } else {
                if (!onboardingSlide4Fragment.isAdded) {
                    replaceFragment(onboardingSlide4Fragment)
                    indicatorContainer.visibility = View.GONE
                    adjustmentContainer.visibility = View.GONE
                }
            }
        } else {
            incrementProgress()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Session(this).onboarded()) {
            handler.removeCallbacksAndMessages(null)
            incrementProgress()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onRestart() {
        super.onRestart()
        if (!Session(this).onboarded()) {
            handler.removeCallbacksAndMessages(null)
            incrementProgress()
        }
    }

    private fun incrementProgress() {
        if (firstIndicatorProgress < 100) {
            if (firstIndicatorProgress == 0) {
                replaceFragment(onboardingSlide1Fragment)
            }
            firstIndicatorProgress++
            firstIndicator.progress = firstIndicatorProgress
            if (secondIndicator.progress != 0) {
                secondIndicator.progress = 0
            }
            if (thirdIndicator.progress != 0) {
                thirdIndicator.progress = 0
            }
            handler.postDelayed({
                incrementProgress()
            }, 50)
        } else if (secondIndicatorProgress < 100) {
            if (!onboardingSlide2Fragment.isAdded) {
                replaceFragment(onboardingSlide2Fragment)
            }
            secondIndicatorProgress++
            secondIndicator.progress = secondIndicatorProgress
            if (thirdIndicator.progress != 0) {
                thirdIndicator.progress = 0
            }
            handler.postDelayed({
                incrementProgress()
            }, 50)
        } else if (thirdIndicatorProgress < 100) {
            if (!onboardingSlide3Fragment.isAdded) {
                replaceFragment(onboardingSlide3Fragment)
            }
            thirdIndicatorProgress++
            thirdIndicator.progress = thirdIndicatorProgress
            handler.postDelayed({
                incrementProgress()
            }, 50)
        } else {
            Session(this).onboarded(true)
            if (!onboardingSlide4Fragment.isAdded) {
                replaceFragment(onboardingSlide4Fragment)
            }
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        if (!isFinishing) {
            val fragmentManager = supportFragmentManager
            if (!fragmentManager.isDestroyed) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                when (fragment) {
                    is OnboardingSlide1Fragment -> {
                        fragmentTransaction.replace(
                            R.id.fragment_container, onboardingSlide1Fragment
                        )
                        if (onboardingSlide1Fragment.isAdded) {
                            (fragmentManager.findFragmentById(R.id.fragment_container) as OnboardingSlide1Fragment?)?.playLottie()
                        }
                    }
                    is OnboardingSlide2Fragment -> {
                        fragmentTransaction.replace(
                            R.id.fragment_container, onboardingSlide2Fragment
                        )
                    }
                    is OnboardingSlide3Fragment -> {
                        fragmentTransaction.replace(
                            R.id.fragment_container, onboardingSlide3Fragment
                        )
                    }
                    is OnboardingSlide4Fragment -> {
                        fragmentTransaction.replace(
                            R.id.fragment_container, onboardingSlide4Fragment
                        )
                    }
                }
                fragmentTransaction.commit()
            }
        }
    }

    fun checkPermission() {
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
        val fragmentManager = supportFragmentManager
        if (!fragmentManager.isDestroyed) {
            if (onboardingSlide4Fragment.isAdded) {
                (fragmentManager.findFragmentById(R.id.fragment_container) as OnboardingSlide4Fragment?)?.checkAvailablePhoneNumber()
            }
        }
    }

    fun otpSent(l: Int, statusCode: Int? = 0) {
        val fragmentManager = supportFragmentManager
        if (!fragmentManager.isDestroyed) {
            if (onboardingSlide4Fragment.isAdded) {
                (fragmentManager.findFragmentById(R.id.fragment_container) as OnboardingSlide4Fragment?)?.otpSent()
                if (l == 1) {
                    (fragmentManager.findFragmentById(R.id.fragment_container) as OnboardingSlide4Fragment?)?.moveToOtpVerificationPage()
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
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}