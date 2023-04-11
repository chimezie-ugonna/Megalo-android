package invest.megalo.controller.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import invest.megalo.R
import invest.megalo.controller.activity.MainActivity
import invest.megalo.controller.activity.OtpVerification
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.Session
import org.json.JSONObject

class OnboardingSlide4Fragment : Fragment() {
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var phoneContainer: RelativeLayout
    private lateinit var phone: EditText
    private lateinit var error: TextView
    private lateinit var proceed: AppCompatButton
    private lateinit var loader: ProgressBar
    private lateinit var ccp: CountryCodePicker
    private val phoneNumberHintIntentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        try {
            val phoneNumber =
                Identity.getSignInClient(requireContext()).getPhoneNumberFromIntent(it.data)
            ccp.fullNumber = phoneNumber
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            requireActivity().window.navigationBarColor =
                ContextCompat.getColor(requireActivity(), R.color.night)
        }
        val v = inflater.inflate(R.layout.fragment_onboarding_slide4, container, false)
        bottomSheetLayout = v.findViewById(R.id.bottom_sheet_layout)
        phoneContainer = v.findViewById(R.id.phone_container)
        error = v.findViewById(R.id.error)
        phone = v.findViewById(R.id.phone)
        phone.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (phone.hasFocus()) {
                    phoneContainer.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    phoneContainer.background = ContextCompat.getDrawable(
                        requireContext(),
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
                    phoneContainer.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_app_green_stroke_curved_corners
                    )
                } else {
                    phoneContainer.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                    )
                }
            }
        }
        ccp = v.findViewById(R.id.ccp)
        ccp.registerCarrierNumberEditText(phone)

        loader = v.findViewById(R.id.loader)
        proceed = v.findViewById(R.id.button)
        proceed.text = resources.getString(R.string.proceed)
        proceed.setOnClickListener {
            if (phone.text.toString().isEmpty()) {
                phoneContainer.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.white_black_solid_red_stroke_curved_corners
                )
                error.text = getString(R.string.empty_phone_field_error_message)
                error.visibility = View.VISIBLE
            } else {
                if (ccp.isValidFullNumber) {
                    if (InternetCheck(requireContext(), v).status()) {
                        proceed.text = ""
                        proceed.isEnabled = false
                        loader.visibility = View.VISIBLE
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Session(requireContext()).deviceToken(task.result)
                                ServerConnection(
                                    requireContext(),
                                    "sendOtp",
                                    Request.Method.POST,
                                    "user/send_otp",
                                    JSONObject().put("type", "sms")
                                        .put("phone_number", ccp.fullNumberWithPlus)
                                )
                            }
                        }.addOnFailureListener {
                            proceed.text = resources.getString(R.string.proceed)
                            proceed.isEnabled = true
                            loader.visibility = View.INVISIBLE
                            it.printStackTrace()
                            CustomSnackBar(
                                requireContext(),
                                v,
                                resources.getString(R.string.unknown_error_message),
                                "error"
                            )
                        }
                    }
                } else {
                    phoneContainer.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.white_black_solid_red_stroke_curved_corners
                    )
                    error.text = getString(R.string.invalid_phone_number_error_message)
                    error.visibility = View.VISIBLE
                }
            }
        }

        if ((activity as MainActivity).indicatorContainer.isVisible || (activity as MainActivity).adjustmentContainer.isVisible) {
            (activity as MainActivity).indicatorContainer.visibility = View.GONE
            (activity as MainActivity).adjustmentContainer.visibility = View.GONE
        }
        v.post {
            val animate = TranslateAnimation(
                0f, 0f, bottomSheetLayout.height.toFloat(), 0f
            )
            animate.duration = 500
            animate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    (activity as MainActivity).checkPermission()
                    bottomSheetLayout.performHapticFeedback(
                        HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.CONTEXT_CLICK
                    )
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
            })
            bottomSheetLayout.visibility = View.VISIBLE
            bottomSheetLayout.startAnimation(animate)
        }
        return v
    }

    fun otpSent() {
        proceed.text = resources.getString(R.string.proceed)
        proceed.isEnabled = true
        loader.visibility = View.INVISIBLE
    }

    fun moveToOtpVerificationPage() {
        Session(requireContext()).devicePhoneNumber(ccp.fullNumberWithPlus)
        val intent = Intent(requireContext(), OtpVerification::class.java)
        intent.putExtra("formatted_phone_number", ccp.formattedFullNumber)
        intent.putExtra("full_phone_number", ccp.fullNumberWithPlus)
        startActivity(intent)
    }

    fun checkAvailablePhoneNumber() {
        if (Session(requireContext()).devicePhoneNumber() != "") {
            ccp.fullNumber = Session(requireContext()).devicePhoneNumber()
        } else {
            val request: GetPhoneNumberHintIntentRequest =
                GetPhoneNumberHintIntentRequest.builder().build()

            Identity.getSignInClient(requireContext()).getPhoneNumberHintIntent(request)
                .addOnSuccessListener {
                    phoneNumberHintIntentResultLauncher.launch(
                        IntentSenderRequest.Builder(it.intentSender).build()
                    )
                }.addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }
}