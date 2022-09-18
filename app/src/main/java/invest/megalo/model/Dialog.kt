package invest.megalo.model

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import invest.megalo.R
import invest.megalo.controller.activity.Registration
import java.util.*

class Dialog(
    view: View? = null,
    context: Context,
    type: String,
    title_: String,
    content_: String,
    positive_button: String,
    negative_button: String,
    cancelable: Boolean = true
) {

    init {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setCancelable(cancelable)

        if (type == context.resources.getString(R.string.date_picker)) {
            bottomSheetDialog.setContentView(R.layout.dialog_date_picker)

            val title = bottomSheetDialog.findViewById<TextView>(R.id.title)
            val datePicker = bottomSheetDialog.findViewById<DatePicker>(R.id.date_picker)
            val positiveButton = bottomSheetDialog.findViewById<Button>(R.id.positive_button)
            val negativeButton = bottomSheetDialog.findViewById<Button>(R.id.negative_button)

            title?.setTextColor(ColorResCompat(context, R.attr.black_white).get())
            title?.setTypeface(null, Typeface.BOLD)
            title?.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.sub_header_text)
            )
            title?.gravity = Gravity.CENTER
            title?.text = title_

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 18)
            datePicker?.maxDate = calendar.timeInMillis
            datePicker?.init(
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            ) { _, _, _, _ -> }

            positiveButton?.setTypeface(null, Typeface.BOLD)
            positiveButton?.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.big_text)
            )
            positiveButton?.setTextColor(ContextCompat.getColor(context, R.color.white))
            positiveButton?.background = ContextCompat.getDrawable(
                context,
                R.drawable.app_green_solid_curved_corners
            )
            positiveButton?.foreground =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.white_ripple_curved_corners
                )
            positiveButton?.text = positive_button
            positiveButton?.setOnClickListener {
                val day =
                    if (datePicker?.dayOfMonth!! < 10) "0${datePicker.dayOfMonth}" else "${datePicker.dayOfMonth}"
                val month =
                    if (datePicker.month + 1 < 10) "0${datePicker.month + 1}" else "${datePicker.month + 1}"
                if (context is Registration) {
                    context.dob.setText(
                        context.resources.getString(
                            R.string.date_format,
                            day,
                            month,
                            datePicker.year.toString()
                        )
                    )
                }
                bottomSheetDialog.dismiss()
            }

            if (negative_button == "") {
                val llp = LinearLayout.LayoutParams(0, 0, 0.0f)
                llp.setMargins(0, 0, 0, 0)
                negativeButton?.layoutParams = llp
            } else {
                negativeButton?.setTypeface(null, Typeface.BOLD)
                negativeButton?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen.big_text)
                )
                negativeButton?.setTextColor(ContextCompat.getColor(context, R.color.black))
                negativeButton?.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.light_gray_solid
                )
                negativeButton?.foreground =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.black_ripple_curved_corners
                    )
                negativeButton?.text = negative_button
            }
            negativeButton?.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

        }

        bottomSheetDialog.show()

        view?.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }
}