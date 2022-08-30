package invest.megalo.model

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import invest.megalo.R

class CustomLoader(val context: Context) {
    private lateinit var dialog: Dialog
    lateinit var text: TextView

    fun show(description: String) {
        val alertDialog = AlertDialog.Builder(context)
        val v = LayoutInflater.from(context).inflate(R.layout.dialog_loader, null, false)
        text = v.findViewById(R.id.description)
        setMessage(description)
        alertDialog.setCancelable(false)
        alertDialog.setView(v)
        dialog = alertDialog.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_background)
        dialog.show()
    }

    fun setMessage(description: String) {
        text.text = description
    }

    fun dismiss() {
        dialog.dismiss()
    }
}