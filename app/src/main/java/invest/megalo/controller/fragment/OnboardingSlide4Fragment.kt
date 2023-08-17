package invest.megalo.controller.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import invest.megalo.R
import invest.megalo.controller.activity.MainActivity
import invest.megalo.controller.activity.OtpVerificationActivity
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.InternetCheck
import invest.megalo.model.ServerConnection
import invest.megalo.model.Session
import org.json.JSONObject


class OnboardingSlide4Fragment : Fragment() {
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var phoneContainer: RelativeLayout
    private lateinit var phone: EditText
    private lateinit var ccp: CountryCodePicker
    private lateinit var error: TextView
    private lateinit var proceed: AppCompatButton
    private lateinit var loader: ProgressBar
    private lateinit var parent: RelativeLayout
    private lateinit var title: TextView
    private lateinit var header: TextView
    private lateinit var bottomSheetHeader: TextView
    private lateinit var bottomSheetSubHeader: TextView
    private lateinit var dialogParent: CardView
    private lateinit var dialogTitle: TextView
    private lateinit var dialogDismiss: ImageView
    private lateinit var dialogSearch: EditText
    private lateinit var dialogClearQuery: ImageView
    private lateinit var dialogList: RecyclerView
    private lateinit var dialogNoResult: TextView
    private var ccpDialogHasShown: Boolean = false
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))

        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        header.text = getString(R.string.your_access_to_real_estate_investment)
        header.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        header.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        bottomSheetLayout.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.bottom_sheet_background
        )

        bottomSheetHeader.text = getString(R.string.enter_your_phone_number)
        bottomSheetHeader.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        bottomSheetHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        bottomSheetSubHeader.text = getString(R.string.you_will_receive_a_6_digit_verification_code)
        bottomSheetSubHeader.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        bottomSheetSubHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        ccp.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.white_black_solid_curved_left_corners
        )
        ccp.contentColor = ContextCompat.getColor(
            requireContext(), R.color.black_white
        )
        ccp.setFlagBorderColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        ccp.setTextSize(resources.getDimension(R.dimen.normal_text).toInt())

        ccp.setDialogBackgroundColor(
            ContextCompat.getColor(
                requireContext(), R.color.white_night
            )
        )
        ccp.setDialogTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        if (ccpDialogHasShown) {
            dialogParent.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.white_night
                )
            )
            dialogClearQuery.setColorFilter(
                ContextCompat.getColor(
                    requireContext(), R.color.black_white
                )
            )
            dialogDismiss.setColorFilter(
                ContextCompat.getColor(
                    requireContext(), R.color.black_white
                )
            )
            dialogTitle.text = getString(R.string.select_a_country)
            dialogTitle.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
            )
            dialogTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.black_white
                )
            )
            dialogNoResult.text = getString(R.string.results_not_found)
            dialogNoResult.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
            )
            dialogNoResult.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.black_white
                )
            )
            dialogSearch.hint = getString(R.string.search)
            dialogSearch.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
            )
            dialogSearch.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.black_white
                )
            )
            dialogSearch.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.darkHint_lightHint
                )
            )
            if (dialogSearch.hasFocus()) {
                dialogSearch.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                dialogSearch.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
            dialogList.adapter?.notifyItemRangeChanged(0, dialogList.adapter!!.itemCount)

        }

        if (!error.isVisible) {
            if (phone.hasFocus()) {
                phoneContainer.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.white_black_solid_app_green_stroke_curved_corners
                )
            } else {
                phoneContainer.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )
            }
        } else {
            phoneContainer.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
            )
            if (phone.text.toString().isEmpty()) {
                error.text = getString(R.string.please_enter_your_phone_number)
            } else {
                error.text = getString(R.string.please_enter_a_valid_phone_number)
            }
        }
        phone.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.white_black_solid_curved_right_corners
        )
        phone.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        phone.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.darkHint_lightHint))
        phone.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        error.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkRed_lightRed))
        error.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        proceed.text = resources.getString(R.string.proceed)
        proceed.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireActivity(), R.color.white_night)
        val v = inflater.inflate(R.layout.fragment_onboarding_slide4, container, false)
        parent = v.findViewById(R.id.parent)
        title = v.findViewById(R.id.title)
        header = v.findViewById(R.id.header)
        bottomSheetLayout = v.findViewById(R.id.bottom_sheet_layout)
        bottomSheetHeader = v.findViewById(R.id.bottom_sheet_header)
        bottomSheetSubHeader = v.findViewById(R.id.bottom_sheet_sub_header)
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
                        requireContext().resources.getDimension(R.dimen.normal_padding),
                        requireContext().resources.displayMetrics
                    ).toInt()
                )
                dialogSearch.setHintTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.darkHint_lightHint
                    )
                )
                dialogSearch.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_grey_light_grey_stroke_curved_corners
                )

                dialogSearch.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        dialogSearch.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.white_black_solid_app_green_stroke_curved_corners
                        )
                    } else {
                        dialogSearch.background = ContextCompat.getDrawable(
                            requireContext(),
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

        loader = v.findViewById(R.id.loader)
        proceed = v.findViewById(R.id.button)
        proceed.text = resources.getString(R.string.proceed)
        proceed.setOnClickListener {
            if (phone.text.toString().isEmpty()) {
                phoneContainer.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                )
                error.text = getString(R.string.please_enter_your_phone_number)
                error.visibility = View.VISIBLE
            } else {
                if (ccp.isValidFullNumber) {
                    if (InternetCheck(requireContext(), v).status()) {
                        proceed.text = ""
                        proceed.isEnabled = false
                        ccp.setCcpClickable(false)
                        phone.isEnabled = false
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
                            ccp.setCcpClickable(true)
                            phone.isEnabled = true
                            loader.visibility = View.INVISIBLE
                            it.printStackTrace()
                            CustomSnackBar(
                                requireContext(),
                                v,
                                resources.getString(R.string.unusual_error_message),
                                "error"
                            )
                        }
                    }
                } else {
                    phoneContainer.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.white_black_solid_dark_red_light_red_stroke_curved_corners
                    )
                    error.text = getString(R.string.please_enter_a_valid_phone_number)
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
        ccp.setCcpClickable(true)
        phone.isEnabled = true
        loader.visibility = View.INVISIBLE
    }

    fun moveToOtpVerificationPage() {
        val intent = Intent(requireContext(), OtpVerificationActivity::class.java)
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