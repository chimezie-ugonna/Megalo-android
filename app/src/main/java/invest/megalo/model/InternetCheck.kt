package invest.megalo.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import invest.megalo.R

class InternetCheck(private val context: Context, val parent: View) {
    fun status(): Boolean {
        val returnValue: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val n = connectivityManager.activeNetwork
        returnValue = if (n != null) {
            val nc = connectivityManager.getNetworkCapabilities(n)
            nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        } else {
            false
        }
        if (!returnValue) {
            CustomSnackBar(
                context,
                parent,
                context.resources.getString(R.string.no_internet_error_message),
                "error"
            )
        }
        return returnValue
    }
}