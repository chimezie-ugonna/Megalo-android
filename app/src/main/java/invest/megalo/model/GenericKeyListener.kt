package invest.megalo.model

import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class GenericKeyListener(
    private val ePrev: EditText,
    private val eCurr: EditText,
    private val eNext: EditText
) : View.OnKeyListener {
    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == KeyEvent.KEYCODE_DEL) {
            if (eCurr.text.isEmpty()) {
                ePrev.requestFocus()
                ePrev.setSelection(ePrev.text.length)
            }
        } else {
            if (eCurr.text.isNotEmpty()) {
                eNext.requestFocus()
                eNext.setSelection(eNext.text.length)
            }
        }
        return false
    }
}