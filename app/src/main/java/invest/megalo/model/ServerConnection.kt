package invest.megalo.model

import android.content.Context
import android.os.Build
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import invest.megalo.R
import invest.megalo.controller.activity.Home
import invest.megalo.controller.activity.MainActivity
import invest.megalo.controller.activity.OtpVerification
import invest.megalo.controller.activity.Registration
import org.json.JSONException
import org.json.JSONObject

class ServerConnection(
    var context: Context, var operation: String,
    var method: Int,
    var extension: String,
    var jsonObject: JSONObject,
    var url: String = context.resources.getString(R.string.server_url)
) {

    init {
        val requestQue = Volley.newRequestQueue(context)
        val request = object : JsonObjectRequest(method, "$url$extension", jsonObject, {
            try {
                val status = it.getBoolean("status")
                when (operation) {
                    "sendOtp" -> {
                        if (status) {
                            if (context is MainActivity) {
                                (context as MainActivity).otpSent(1)
                            } else if (context is OtpVerification) {
                                (context as OtpVerification).otpSent(1)
                            }
                        } else {
                            if (context is MainActivity) {
                                (context as MainActivity).otpSent(-1)
                            } else if (context is OtpVerification) {
                                (context as OtpVerification).otpSent(-1)
                            }
                        }
                    }
                    "verifyOtp" -> {
                        if (status) {
                            KeyStore(context).encryptData(
                                it.getJSONObject("data").getString("token")
                            )
                            if (context is OtpVerification) {
                                (context as OtpVerification).otpVerified(
                                    1,
                                    it.getJSONObject("data").getBoolean("user_exists")
                                )
                            }
                        } else {
                            if (context is OtpVerification) {
                                (context as OtpVerification).otpVerified(-1, false)
                            }
                        }
                    }
                    "register" -> {
                        if (status) {
                            KeyStore(context).encryptData(
                                it.getJSONObject("data").getString("token")
                            )
                            if (context is Registration) {
                                (context as Registration).registered(1)
                            }
                        } else {
                            if (context is Registration) {
                                (context as Registration).registered(-1)
                            }
                        }
                    }
                    "logIn" -> {
                        if (status) {
                            KeyStore(context).encryptData(
                                it.getJSONObject("data").getString("token")
                            )
                            if (context is OtpVerification) {
                                (context as OtpVerification).loggedIn(1)
                            }
                        } else {
                            if (context is OtpVerification) {
                                (context as OtpVerification).loggedIn(-1)
                            }
                        }
                    }
                    "logOut" -> {
                        if (status) {
                            KeyStore(context).deleteKey()
                            Session(context).appTheme("system")
                            if (context is Home) {
                                (context as Home).loggedOut(1)
                            }
                        } else {
                            if (context is Home) {
                                (context as Home).loggedOut(-1)
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                when (operation) {
                    "sendOtp" -> {
                        if (context is MainActivity) {
                            (context as MainActivity).otpSent(-1)
                        } else if (context is OtpVerification) {
                            (context as OtpVerification).otpSent(-1)
                        }
                    }
                    "verifyOtp" -> {
                        if (context is OtpVerification) {
                            (context as OtpVerification).otpVerified(-1, false)
                        }
                    }
                    "register" -> {
                        if (context is Registration) {
                            (context as Registration).registered(-1)
                        }
                    }
                    "logIn" -> {
                        if (context is OtpVerification) {
                            (context as OtpVerification).loggedIn(-1)
                        }
                    }
                    "logOut" -> {
                        if (context is Home) {
                            (context as Home).loggedOut(-1)
                        }
                    }
                }
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            when (operation) {
                "sendOtp" -> {
                    if (context is MainActivity) {
                        (context as MainActivity).otpSent(-1)
                    } else if (context is OtpVerification) {
                        (context as OtpVerification).otpSent(-1)
                    }
                }
                "verifyOtp" -> {
                    if (context is OtpVerification) {
                        (context as OtpVerification).otpVerified(-1, false)
                    }
                }
                "register" -> {
                    if (context is Registration) {
                        (context as Registration).registered(-1)
                    }
                }
                "logIn" -> {
                    if (context is OtpVerification) {
                        (context as OtpVerification).loggedIn(-1)
                    }
                }
                "logOut" -> {
                    if (context is Home) {
                        (context as Home).loggedOut(-1)
                    }
                }
            }
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                if (operation != "sendOtp" && operation != "verifyOtp" && Session(context).encryptedTokenIv() != "" && Session(
                        context
                    ).encryptedToken() != ""
                ) {
                    val decryptedData = KeyStore(context).decryptData()
                    header["Authorization"] = "Bearer $decryptedData"
                }
                header["Accept"] = "application/json"
                header["access-type"] = "mobile"
                header["device-os"] = "android"
                header["device-token"] = Session(context).deviceToken().toString()
                header["device-brand"] = Build.BRAND
                header["device-model"] = Build.MODEL
                header["app-version"] = context.resources.getString(R.string.app_version)
                header["os-version"] = Build.VERSION.RELEASE
                return header
            }
        }
        requestQue.add(request)
    }
}