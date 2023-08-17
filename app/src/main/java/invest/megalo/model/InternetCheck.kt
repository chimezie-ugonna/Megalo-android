package invest.megalo.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.view.View
import invest.megalo.R
import invest.megalo.controller.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Suppress("BooleanMethodIsAlwaysInverted")
class InternetCheck(
    private val context: Context, val parent: View, private val showSnackBar: Boolean = true
) : ConnectivityObserver {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun status(): Boolean {
        val returnValue: Boolean
        val n = connectivityManager.activeNetwork
        returnValue = if (n != null) {
            val nc = connectivityManager.getNetworkCapabilities(n)
            nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        } else {
            false
        }
        if (!returnValue && showSnackBar) {
            CustomSnackBar(
                context,
                parent,
                context.resources.getString(R.string.no_internet_error_message),
                "error"
            )
        }
        return returnValue
    }

    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callBack = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.Available) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityObserver.Status.Unavailable) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.Lost) }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callBack)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callBack)
            }
        }.distinctUntilChanged()
    }
}