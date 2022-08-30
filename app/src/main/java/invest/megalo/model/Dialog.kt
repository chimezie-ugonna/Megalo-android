package invest.megalo.model

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import invest.megalo.R

class Dialog(
    view: View? = null,
    private val context: Context,
    private val type: String,
    title_: String,
    content_: String,
    positive_button: String,
    negative_button: String,
    cancelable: Boolean = true
) {

    init {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setCancelable(cancelable)

        bottomSheetDialog.show()

        view?.performHapticFeedback(
            HapticFeedbackConstants.VIRTUAL_KEY,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }
}