package invest.megalo.controller.fragment

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.contains
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDragHandleView
import invest.megalo.R
import invest.megalo.controller.activity.EditProfileActivity
import invest.megalo.controller.activity.HomeActivity
import invest.megalo.controller.activity.MainActivity
import invest.megalo.controller.activity.RegistrationActivity
import java.util.Calendar


class BottomSheetDialogFragment(
    private val view: View,
    private val context: Context,
    private val type: String,
    private val titleTextResource: Int,
    private val contentTextResource: Int,
    private val positiveButtonTextResource: Int,
    private val negativeButtonTextResource: Int,
    private val cancelable: Boolean = true,
    private val imgResource: Int = 0
) : BottomSheetDialogFragment() {
    private lateinit var handle: BottomSheetDragHandleView
    private lateinit var datePicker: DatePicker
    private lateinit var title: TextView
    private lateinit var content: TextView
    private lateinit var img: ImageView
    private lateinit var check: CheckBox
    private lateinit var positiveButton: AppCompatButton
    private lateinit var negativeButton: AppCompatButton
    private lateinit var parent: LinearLayout
    private lateinit var datePickerParent: RelativeLayout
    private lateinit var buttonContainer: LinearLayout
    private val calendar = Calendar.getInstance()
    private var iYear = 0
    private var iMonth = 0
    private var iDay = 0

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dialog?.window!!.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.white_night)
        setUiComponents()
    }

    private fun setUiComponents() {
        if (type == "date_picker") {
            datePickerParent.background = ContextCompat.getDrawable(
                context, R.drawable.bottom_sheet_background
            )
        } else {
            parent.background = ContextCompat.getDrawable(
                context, R.drawable.bottom_sheet_background
            )
        }
        handle.setColorFilter(ContextCompat.getColor(context, R.color.darkGrey_lightGrey))
        title.setTextColor(ContextCompat.getColor(context, R.color.black_white))
        title.setTypeface(null, Typeface.BOLD)
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
        )
        title.gravity = Gravity.CENTER_HORIZONTAL
        title.text = getString(titleTextResource)

        positiveButton.setTypeface(null, Typeface.NORMAL)
        positiveButton.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
        )
        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.black))
        positiveButton.background = ContextCompat.getDrawable(
            context, R.drawable.app_green_solid_curved_corners
        )
        positiveButton.foreground = ContextCompat.getDrawable(
            context, R.drawable.black_ripple_curved_corners
        )
        positiveButton.setPadding(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.normal_padding),
                context.resources.displayMetrics
            ).toInt()
        )
        positiveButton.text = getString(positiveButtonTextResource)

        if (negativeButtonTextResource == 0) {
            val llp = LinearLayout.LayoutParams(0, 0, 0.0f)
            llp.setMargins(0, 0, 0, 0)
            negativeButton.layoutParams = llp
        } else {
            negativeButton.setTypeface(null, Typeface.NORMAL)
            negativeButton.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
            negativeButton.setTextColor(ContextCompat.getColor(context, R.color.appGreen))
            negativeButton.background = ContextCompat.getDrawable(
                context, R.drawable.white_night_solid_app_green_stroke_curved_corners
            )
            negativeButton.foreground = ContextCompat.getDrawable(
                context, R.drawable.app_green_ripple_curved_corners
            )
            negativeButton.setPadding(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.normal_padding),
                    context.resources.displayMetrics
                ).toInt()
            )
            negativeButton.text = getString(negativeButtonTextResource)
        }

        if (type == "date_picker") {
            if (datePickerParent.contains(datePicker)) {
                datePickerParent.removeView(datePicker)
            }
            datePicker = DatePicker(ContextThemeWrapper(requireContext(), R.style.MyDatePicker))
            datePicker.maxDate = calendar.timeInMillis
            datePicker.init(
                iYear, iMonth, iDay
            ) { datePicker1, i, i1, i2 ->
                iYear = i
                iMonth = i1
                iDay = i2
                datePicker1.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.CONTEXT_CLICK
                )
            }
            var llp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            llp.setMargins(
                0, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.normal_padding),
                    context.resources.displayMetrics
                ).toInt(), 0, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.normal_padding),
                    context.resources.displayMetrics
                ).toInt()
            )
            llp.addRule(RelativeLayout.BELOW, title.id)
            datePicker.layoutParams = llp
            datePicker.id = ViewCompat.generateViewId()
            datePickerParent.addView(datePicker)

            llp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            llp.setMargins(0)
            llp.addRule(RelativeLayout.BELOW, datePicker.id)
            buttonContainer.layoutParams = llp
        } else {
            content.setTextColor(ContextCompat.getColor(context, R.color.black_white))
            content.setTypeface(null, Typeface.NORMAL)
            content.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
            content.gravity = Gravity.CENTER_HORIZONTAL
            content.text = getString(contentTextResource)

            if (type == "identification_document_review") {
                val llp = LinearLayout.LayoutParams(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.big_icon_size),
                        context.resources.displayMetrics
                    ).toInt(), TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.big_icon_size),
                        context.resources.displayMetrics
                    ).toInt()
                )
                llp.gravity = Gravity.CENTER_HORIZONTAL
                llp.setMargins(
                    0, 0, 0, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt()
                )
                img.layoutParams = llp
                img.setImageResource(imgResource)
            } else {
                if (type == "confirm_log_out") {
                    val llp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    llp.gravity = Gravity.CENTER_HORIZONTAL
                    llp.setMargins(
                        0, 0, 0, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX,
                            context.resources.getDimension(R.dimen.normal_padding),
                            context.resources.displayMetrics
                        ).toInt()
                    )
                    check.layoutParams = llp
                    check.buttonTintList = ContextCompat.getColorStateList(
                        context, R.color.check_box_selector
                    )
                    check.setTextColor(ContextCompat.getColor(context, R.color.black_white))
                    check.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_text)
                    )
                    check.text = context.getString(R.string.log_out_everywhere)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            view.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.CONTEXT_CLICK
            )
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v: View
        isCancelable = cancelable
        if (type == "date_picker") {
            v = inflater.inflate(R.layout.dialog_date_picker, container, false)
            datePickerParent = v.findViewById(R.id.parent)
            handle = v.findViewById(R.id.handle)
            title = v.findViewById(R.id.title)
            datePicker = DatePicker(ContextThemeWrapper(requireContext(), R.style.MyDatePicker))
            buttonContainer = v.findViewById(R.id.button_container)
            positiveButton = v.findViewById(R.id.positive_button)
            negativeButton = v.findViewById(R.id.negative_button)

            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 18)
            iYear = calendar.get(Calendar.YEAR)
            iMonth = calendar.get(Calendar.MONTH)
            iDay = calendar.get(Calendar.DAY_OF_MONTH)
            if (context is RegistrationActivity) {
                if (context.dob.text.isNotEmpty() && context.dob.text.toString() != resources.getString(
                        R.string.select_your_birth_date
                    ) && context.dob.text.split("/").size == 3
                ) {
                    iDay = Integer.parseInt(context.dob.text.split("/")[0])
                    iMonth = Integer.parseInt(context.dob.text.split("/")[1]) - 1
                    iYear = Integer.parseInt(context.dob.text.split("/")[2])
                }
            } else if (context is EditProfileActivity) {
                if (context.dob.text.isNotEmpty() && context.dob.text.toString() != resources.getString(
                        R.string.select_your_birth_date
                    ) && context.dob.text.split("/").size == 3
                ) {
                    iDay = Integer.parseInt(context.dob.text.split("/")[0])
                    iMonth = Integer.parseInt(context.dob.text.split("/")[1]) - 1
                    iYear = Integer.parseInt(context.dob.text.split("/")[2])
                }
            }

            positiveButton.setOnClickListener {
                val day =
                    if (datePicker.dayOfMonth < 10) "0${datePicker.dayOfMonth}" else "${datePicker.dayOfMonth}"
                val month =
                    if (datePicker.month + 1 < 10) "0${datePicker.month + 1}" else "${datePicker.month + 1}"
                if (context is RegistrationActivity) {
                    context.dob.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black_white
                        )
                    )
                    context.dob.text = context.resources.getString(
                        R.string.date_format, day, month, datePicker.year.toString()
                    )
                } else if (context is EditProfileActivity) {
                    context.dob.text = context.resources.getString(
                        R.string.date_format, day, month, datePicker.year.toString()
                    )
                }
                dismiss()
            }

            negativeButton.setOnClickListener {
                dismiss()
            }

        } else {
            v = inflater.inflate(R.layout.dialog_general, container, false)
            parent = v.findViewById(R.id.parent)
            handle = v.findViewById(R.id.handle)
            img = v.findViewById(R.id.img)
            title = v.findViewById(R.id.title)
            content = v.findViewById(R.id.content)
            check = v.findViewById(R.id.check)
            positiveButton = v.findViewById(R.id.positive_button)
            negativeButton = v.findViewById(R.id.negative_button)

            if (type == "identification_document_review") {
                positiveButton.setOnClickListener {
                    dismiss()
                }
            } else {
                positiveButton.setOnClickListener {
                    if (type == "confirm_log_out") {
                        if (context is HomeActivity) {
                            context.everywhere = if (check.isChecked) 1 else 0
                            context.logOut()
                        }
                    } else if (type == "confirm_account_deletion") {
                        if (context is HomeActivity) {
                            context.deleteAccount()
                        }
                    } else {
                        if (context is MainActivity) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                context.requestPermission()
                            }
                        }
                    }
                    dismiss()
                }
                negativeButton.setOnClickListener {
                    if (type == "permission_rationale") {
                        if (context is MainActivity) {
                            context.checkAvailablePhoneNumber()
                        }
                    }
                    dismiss()
                }
            }
        }
        setUiComponents()
        return v
    }
}