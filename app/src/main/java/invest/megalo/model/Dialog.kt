package invest.megalo.model

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.material.bottomsheet.BottomSheetDialog
import invest.megalo.R
import invest.megalo.controller.activity.Home
import invest.megalo.controller.activity.MainActivity
import invest.megalo.controller.activity.Registration
import java.util.*


class Dialog(
    view: View,
    context: Context,
    type: String,
    title_: String,
    content_: String,
    positive_button: String,
    negative_button: String,
    cancelable: Boolean = true,
    img_: Int = 0
) {
    private var bottomSheetDialog: BottomSheetDialog
    private var view: View

    init {
        this.view = view
        bottomSheetDialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setCancelable(cancelable)

        when (type) {
            context.resources.getString(R.string.date_picker) -> {
                bottomSheetDialog.setContentView(R.layout.dialog_date_picker)

                val title = bottomSheetDialog.findViewById<TextView>(R.id.title)
                val datePicker = bottomSheetDialog.findViewById<DatePicker>(R.id.date_picker)
                val positiveButton =
                    bottomSheetDialog.findViewById<AppCompatButton>(R.id.positive_button)
                val negativeButton =
                    bottomSheetDialog.findViewById<AppCompatButton>(R.id.negative_button)

                title?.setTextColor(ColorResCompat(context, R.attr.black_white).get())
                title?.setTypeface(null, Typeface.BOLD)
                title?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
                )
                title?.gravity = CENTER_HORIZONTAL
                title?.text = title_

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 18)
                datePicker?.maxDate = calendar.timeInMillis
                datePicker?.init(
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                ) { _, _, _, _ -> }

                positiveButton?.setTypeface(null, Typeface.NORMAL)
                positiveButton?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
                )
                positiveButton?.setTextColor(ContextCompat.getColor(context, R.color.black))
                positiveButton?.background = ContextCompat.getDrawable(
                    context, R.drawable.app_green_solid_curved_corners
                )
                positiveButton?.foreground = ContextCompat.getDrawable(
                    context, R.drawable.black_ripple_curved_corners
                )
                positiveButton?.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt()
                )
                positiveButton?.text = positive_button
                positiveButton?.setOnClickListener {
                    val day =
                        if (datePicker?.dayOfMonth!! < 10) "0${datePicker.dayOfMonth}" else "${datePicker.dayOfMonth}"
                    val month =
                        if (datePicker.month + 1 < 10) "0${datePicker.month + 1}" else "${datePicker.month + 1}"
                    if (context is Registration) {
                        context.dob.setTextColor(ColorResCompat(context, R.attr.black_white).get())
                        context.dob.text = context.resources.getString(
                            R.string.date_format, day, month, datePicker.year.toString()
                        )
                    }
                    bottomSheetDialog.dismiss()
                }

                if (negative_button == "") {
                    val llp = LinearLayout.LayoutParams(0, 0, 0.0f)
                    llp.setMargins(0, 0, 0, 0)
                    negativeButton?.layoutParams = llp
                } else {
                    negativeButton?.setTypeface(null, Typeface.NORMAL)
                    negativeButton?.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_text)
                    )
                    negativeButton?.setTextColor(ContextCompat.getColor(context, R.color.app_green))
                    negativeButton?.background = ContextCompat.getDrawable(
                        context, R.drawable.white_night_solid_app_green_stroke_curved_corners
                    )
                    negativeButton?.foreground = ContextCompat.getDrawable(
                        context, R.drawable.app_green_ripple_curved_corners
                    )
                    negativeButton?.setPadding(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX,
                            context.resources.getDimension(R.dimen.normal_padding),
                            context.resources.displayMetrics
                        ).toInt()
                    )
                    negativeButton?.text = negative_button
                }
                negativeButton?.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }

            }
            context.resources.getString(R.string.permission_rationale), context.getString(R.string.confirm_logout) -> {
                bottomSheetDialog.setContentView(R.layout.dialog_general)

                val title = bottomSheetDialog.findViewById<TextView>(R.id.title)
                val content = bottomSheetDialog.findViewById<TextView>(R.id.content)
                val positiveButton =
                    bottomSheetDialog.findViewById<AppCompatButton>(R.id.positive_button)
                val negativeButton =
                    bottomSheetDialog.findViewById<AppCompatButton>(R.id.negative_button)

                title?.setTextColor(ColorResCompat(context, R.attr.black_white).get())
                title?.setTypeface(null, Typeface.BOLD)
                title?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
                )
                title?.gravity = CENTER_HORIZONTAL
                title?.text = title_

                content?.setTextColor(ColorResCompat(context, R.attr.black_white).get())
                content?.setTypeface(null, Typeface.NORMAL)
                content?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
                )
                content?.gravity = CENTER_HORIZONTAL
                content?.text = content_

                positiveButton?.setTypeface(null, Typeface.NORMAL)
                positiveButton?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
                )
                positiveButton?.setTextColor(ContextCompat.getColor(context, R.color.black))
                positiveButton?.background = ContextCompat.getDrawable(
                    context, R.drawable.app_green_solid_curved_corners
                )
                positiveButton?.foreground = ContextCompat.getDrawable(
                    context, R.drawable.black_ripple_curved_corners
                )
                positiveButton?.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt()
                )
                positiveButton?.text = positive_button
                positiveButton?.setOnClickListener {
                    if (type == context.getString(R.string.confirm_logout)) {
                        if (context is Home) {
                            context.logOut()
                        }
                    } else {
                        if (context is MainActivity) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                context.requestPermission()
                            }
                        }
                    }
                    bottomSheetDialog.dismiss()
                }

                if (negative_button == "") {
                    val llp = LinearLayout.LayoutParams(0, 0, 0.0f)
                    llp.setMargins(0, 0, 0, 0)
                    negativeButton?.layoutParams = llp
                } else {
                    negativeButton?.setTypeface(null, Typeface.NORMAL)
                    negativeButton?.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_text)
                    )
                    negativeButton?.setTextColor(ContextCompat.getColor(context, R.color.app_green))
                    negativeButton?.background = ContextCompat.getDrawable(
                        context, R.drawable.white_night_solid_app_green_stroke_curved_corners
                    )
                    negativeButton?.foreground = ContextCompat.getDrawable(
                        context, R.drawable.app_green_ripple_curved_corners
                    )
                    negativeButton?.setPadding(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX,
                            context.resources.getDimension(R.dimen.normal_padding),
                            context.resources.displayMetrics
                        ).toInt()
                    )
                    negativeButton?.text = negative_button
                }
                negativeButton?.setOnClickListener {
                    if (type == context.getString(R.string.permission_rationale)) {
                        if (context is MainActivity) {
                            context.checkAvailablePhoneNumber()
                        }
                    }
                    bottomSheetDialog.dismiss()
                }
            }
            context.resources.getString(R.string.identification_document_review_ongoing), context.resources.getString(
                R.string.secondary_lock_setup
            ) -> {
                bottomSheetDialog.setContentView(R.layout.dialog_general)

                val img = bottomSheetDialog.findViewById<ImageView>(R.id.img)
                val title = bottomSheetDialog.findViewById<TextView>(R.id.title)
                val content = bottomSheetDialog.findViewById<TextView>(R.id.content)
                val positiveButton =
                    bottomSheetDialog.findViewById<AppCompatButton>(R.id.positive_button)
                val negativeButton =
                    bottomSheetDialog.findViewById<AppCompatButton>(R.id.negative_button)

                var llp = LinearLayout.LayoutParams(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.loader_animation_height),
                        context.resources.displayMetrics
                    ).toInt(), TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.loader_animation_height),
                        context.resources.displayMetrics
                    ).toInt()
                )
                llp.gravity = CENTER_HORIZONTAL
                llp.setMargins(
                    0, 0, 0, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt()
                )
                img?.layoutParams = llp
                img?.setImageResource(img_)

                title?.setTextColor(ColorResCompat(context, R.attr.black_white).get())
                title?.setTypeface(null, Typeface.BOLD)
                title?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
                )
                title?.gravity = CENTER_HORIZONTAL
                title?.text = title_

                content?.setTextColor(ColorResCompat(context, R.attr.black_white).get())
                content?.setTypeface(null, Typeface.NORMAL)
                content?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
                )
                content?.gravity = CENTER_HORIZONTAL
                content?.text = content_

                positiveButton?.setTypeface(null, Typeface.NORMAL)
                positiveButton?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
                )
                positiveButton?.setTextColor(ContextCompat.getColor(context, R.color.black))
                positiveButton?.background = ContextCompat.getDrawable(
                    context, R.drawable.app_green_solid_curved_corners
                )
                positiveButton?.foreground = ContextCompat.getDrawable(
                    context, R.drawable.black_ripple_curved_corners
                )
                positiveButton?.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt()
                )
                positiveButton?.text = positive_button
                positiveButton?.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }

                if (negative_button == "") {
                    llp = LinearLayout.LayoutParams(0, 0, 0.0f)
                    llp.setMargins(0, 0, 0, 0)
                    negativeButton?.layoutParams = llp
                } else {
                    negativeButton?.setTypeface(null, Typeface.NORMAL)
                    negativeButton?.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_text)
                    )
                    negativeButton?.setTextColor(ContextCompat.getColor(context, R.color.app_green))
                    negativeButton?.background = ContextCompat.getDrawable(
                        context, R.drawable.white_night_solid_app_green_stroke_curved_corners
                    )
                    negativeButton?.foreground = ContextCompat.getDrawable(
                        context, R.drawable.app_green_ripple_curved_corners
                    )
                    negativeButton?.setPadding(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_PX,
                            context.resources.getDimension(R.dimen.normal_padding),
                            context.resources.displayMetrics
                        ).toInt()
                    )
                    negativeButton?.text = negative_button
                }
                negativeButton?.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
            }
        }
    }

    fun show() {
        bottomSheetDialog.show()
        view.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.CONTEXT_CLICK
        )
    }

    fun isShowing(): Boolean {
        return bottomSheetDialog.isShowing
    }

    fun dismiss() {
        bottomSheetDialog.dismiss()
    }
}