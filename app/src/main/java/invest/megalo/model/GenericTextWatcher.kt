package invest.megalo.model

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import invest.megalo.controller.activity.OtpVerification

class GenericTextWatcher(
    val context: Context,
    private val e1: EditText,
    private val e2: EditText,
    private val e3: EditText,
    private val e4: EditText,
    private val e5: EditText,
    private val e6: EditText,
    private val ePrev: EditText,
    private val eNext: EditText
) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val text = p0.toString().trim()
        if (text.length == 1) {
            eNext.requestFocus()
            eNext.setSelection(eNext.text.length)
        } else {
            ePrev.requestFocus()
            ePrev.setSelection(ePrev.text.length)
        }
        if (e6.text.isNotEmpty() && e5.text.isNotEmpty() && e4.text.isNotEmpty() && e3.text.isNotEmpty() && e2.text.isNotEmpty() && e1.text.isNotEmpty()) {
            if (context is OtpVerification) {
                context.proceed()
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }
}