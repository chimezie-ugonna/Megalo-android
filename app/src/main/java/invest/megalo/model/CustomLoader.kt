package invest.megalo.model

import android.app.Dialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import invest.megalo.R

class CustomLoader(val context: Context) {
    private var dialog: Dialog
    lateinit var text: TextView
    private var resource: Int = 0

    init {
        dialog = Dialog(context)
    }

    fun show(resource: Int) {
        val alertDialog = AlertDialog.Builder(context)
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_loader, null, false)
        text = v.findViewById(R.id.description)
        this.resource = resource
        setMessage(resource)
        alertDialog.setCancelable(false)
        alertDialog.setView(v)
        dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.custom_loader_background)
        dialog.show()
    }

    fun setMessage(resource: Int) {
        this.resource = resource
        text.text = context.getString(resource)
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun onConfigurationChanged() {
        if (dialog.isShowing) {
            text.setTextColor(ContextCompat.getColor(context, R.color.black_white))
            text.text = context.getString(resource)
            text.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
            )
            dialog.window?.setBackgroundDrawableResource(R.drawable.custom_loader_background)
        }
    }
}