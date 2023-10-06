package invest.megalo.model

import android.content.Context
import android.os.Build
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import invest.megalo.R
import invest.megalo.controller.activity.EditProfileActivity
import invest.megalo.controller.activity.HomeActivity
import invest.megalo.controller.activity.InvestmentActivity
import invest.megalo.controller.activity.MainActivity
import invest.megalo.controller.activity.OtpVerificationActivity
import invest.megalo.controller.activity.PropertyDetailActivity
import invest.megalo.controller.activity.ReferralActivity
import invest.megalo.controller.activity.RegistrationActivity
import invest.megalo.controller.activity.UpdateDataActivity
import invest.megalo.controller.activity.VerticalListActivity
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.Locale

class ServerConnection(
    var context: Context,
    var operation: String,
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
                            when (context) {
                                is MainActivity -> {
                                    (context as MainActivity).otpSent(1)
                                }

                                is OtpVerificationActivity -> {
                                    (context as OtpVerificationActivity).otpSent(1)
                                }

                                is HomeActivity -> {
                                    (context as HomeActivity).otpSent(1)
                                }

                                is UpdateDataActivity -> {
                                    (context as UpdateDataActivity).otpSent(1)
                                }
                            }
                        }
                    }

                    "verifyOtp" -> {
                        if (status) {
                            if (context is OtpVerificationActivity) {
                                (context as OtpVerificationActivity).otpVerified(
                                    1, jsonObject = it
                                )
                            }
                        }
                    }

                    "register" -> {
                        if (status) {
                            KeyStore(context).encryptData(
                                it.getJSONObject("data").getString("token")
                            )
                            if (context is RegistrationActivity) {
                                (context as RegistrationActivity).registered(1)
                            }
                        }
                    }

                    "logIn" -> {
                        if (status) {
                            KeyStore(context).encryptData(
                                it.getJSONObject("data").getString("token")
                            )
                            if (context is OtpVerificationActivity) {
                                (context as OtpVerificationActivity).loggedIn(1)
                            }
                        }
                    }

                    "logOut" -> {
                        if (status) {
                            logOutPrerequisites()
                            if (context is HomeActivity) {
                                (context as HomeActivity).loggedOut(1)
                            }
                        }
                    }

                    "updateAccount" -> {
                        if (status) {
                            if (context is EditProfileActivity) {
                                (context as EditProfileActivity).updated(1)
                            }
                        }
                    }

                    "deleteAccount" -> {
                        if (status) {
                            logOutPrerequisites()
                            if (context is HomeActivity) {
                                (context as HomeActivity).accountDeleted(1)
                            }
                        }
                    }

                    "verifyIdentity" -> {
                        if (status) {
                            if (context is HomeActivity) {
                                (context as HomeActivity).initiated(
                                    1, it.getJSONObject("data").getString("auth_token")
                                )
                            }
                        }
                    }

                    "updateDeviceToken" -> {
                        if (status) {
                            Session(context).deviceToken(
                                it.getJSONObject("data").getString("device_token")
                            )
                        }
                    }

                    "fetchUserData" -> {
                        if (status) {
                            when (context) {
                                is HomeActivity -> {
                                    (context as HomeActivity).userDataFetched(
                                        1, jsonArray = it.getJSONArray("data")
                                    )
                                }

                                is EditProfileActivity -> {
                                    (context as EditProfileActivity).userDataFetched(
                                        1, jsonArray = it.getJSONArray("data")
                                    )
                                }

                                is InvestmentActivity -> {
                                    (context as InvestmentActivity).userDataFetched(
                                        1, jsonArray = it.getJSONArray("data")
                                    )
                                }
                            }
                        }
                    }

                    "fetchProperties" -> {
                        if (status) {
                            if (context is HomeActivity) {
                                (context as HomeActivity).propertiesFetched(
                                    1, jsonObject = it.getJSONObject("data")
                                )
                            }
                        }
                    }

                    "fetchPropertyMetric" -> {
                        if (status) {
                            if (context is PropertyDetailActivity) {
                                (context as PropertyDetailActivity).propertyMetricFetched(
                                    1, jsonObject = it.getJSONObject("data")
                                )
                            }
                        }
                    }

                    "fetchPropertyData" -> {
                        if (status) {
                            if (context is InvestmentActivity) {
                                (context as InvestmentActivity).propertyDataFetched(
                                    1, jsonArray = it.getJSONArray("data")
                                )
                            }
                        }
                    }

                    "fetchReferralData" -> {
                        if (status) {
                            if (context is ReferralActivity) {
                                (context as ReferralActivity).referralDataFetched(
                                    1, jsonObject = it.getJSONObject("data")
                                )
                            }
                        }
                    }

                    "fetchReferrees" -> {
                        if (status) {
                            if (context is VerticalListActivity) {
                                (context as VerticalListActivity).referreesFetched(
                                    1, jsonObject = it.getJSONObject("data")
                                )
                            }
                        }
                    }

                    "invest" -> {
                        if (status) {
                            if (context is InvestmentActivity) {
                                (context as InvestmentActivity).invested(
                                    1, jsonObject = it.getJSONObject("data")
                                )
                            }
                        }
                    }

                    "calculatePotential" -> {
                        if (status) {
                            if (context is PropertyDetailActivity) {
                                (context as PropertyDetailActivity).potentialCalculated(
                                    1, jsonArray = it.getJSONArray("data")
                                )
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                respond(400)
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            if (error.networkResponse != null) {
                val body = String(error.networkResponse.data, Charset.defaultCharset())
                val clientMessage = try {
                    val obj = JSONObject(body)
                    obj.getString("client_message")
                } catch (e: JSONException) {
                    ""
                }
                respond(error.networkResponse.statusCode, clientMessage)
            } else {
                respond(0)
            }
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                if (Session(context).encryptedTokenIv() != "" && Session(context).encryptedToken() != "") {
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
                header["app-language-code"] = Locale.getDefault().language
                header["os-version"] = Build.VERSION.RELEASE
                return header
            }
        }
        request.retryPolicy = DefaultRetryPolicy(
            10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQue.add(request)
    }

    fun respond(statusCode: Int, message: String = "") {
        if (statusCode == 420) {
            logOutPrerequisites()
        }
        when (operation) {
            "sendOtp" -> {
                when (context) {
                    is MainActivity -> {
                        (context as MainActivity).otpSent(
                            -1, statusCode, message
                        )
                    }

                    is OtpVerificationActivity -> {
                        (context as OtpVerificationActivity).otpSent(
                            -1, statusCode, message
                        )
                    }

                    is HomeActivity -> {
                        (context as HomeActivity).otpSent(
                            -1, statusCode, message
                        )
                    }

                    is UpdateDataActivity -> {
                        (context as UpdateDataActivity).otpSent(
                            -1, statusCode, message
                        )
                    }
                }
            }

            "verifyOtp" -> {
                if (context is OtpVerificationActivity) {
                    (context as OtpVerificationActivity).otpVerified(-1, statusCode, message)
                }
            }

            "register" -> {
                if (context is RegistrationActivity) {
                    (context as RegistrationActivity).registered(-1, statusCode, message)
                }
            }

            "logIn" -> {
                if (context is OtpVerificationActivity) {
                    (context as OtpVerificationActivity).loggedIn(-1, statusCode, message)
                }
            }

            "logOut" -> {
                if (context is HomeActivity) {
                    (context as HomeActivity).loggedOut(-1, statusCode, message)
                }
            }

            "updateAccount" -> {
                if (context is EditProfileActivity) {
                    (context as EditProfileActivity).updated(-1, statusCode, message)
                }
            }

            "deleteAccount" -> {
                if (context is HomeActivity) {
                    (context as HomeActivity).accountDeleted(-1, statusCode, message)
                }
            }

            "verifyIdentity" -> {
                if (context is HomeActivity) {
                    (context as HomeActivity).initiated(-1, "", statusCode, message)
                }
            }

            "fetchUserData" -> {
                when (context) {
                    is HomeActivity -> {
                        (context as HomeActivity).userDataFetched(-1, statusCode, message)
                    }

                    is EditProfileActivity -> {
                        (context as EditProfileActivity).userDataFetched(-1, statusCode)
                    }

                    is InvestmentActivity -> {
                        (context as InvestmentActivity).userDataFetched(-1, statusCode)
                    }
                }
            }

            "fetchProperties" -> {
                if (context is HomeActivity) {
                    (context as HomeActivity).propertiesFetched(-1, statusCode, message)
                }
            }

            "fetchPropertyMetric" -> {
                if (context is PropertyDetailActivity) {
                    (context as PropertyDetailActivity).propertyMetricFetched(
                        -1, statusCode
                    )
                }
            }

            "fetchPropertyData" -> {
                if (context is InvestmentActivity) {
                    (context as InvestmentActivity).propertyDataFetched(
                        -1, statusCode
                    )
                }
            }

            "fetchReferralData" -> {
                if (context is ReferralActivity) {
                    (context as ReferralActivity).referralDataFetched(
                        -1, statusCode
                    )
                }
            }

            "fetchReferrees" -> {
                if (context is VerticalListActivity) {
                    (context as VerticalListActivity).referreesFetched(
                        -1, statusCode
                    )
                }
            }

            "invest" -> {
                if (context is InvestmentActivity) {
                    (context as InvestmentActivity).invested(
                        -1, statusCode, message
                    )
                }
            }

            "calculatePotential" -> {
                if (context is PropertyDetailActivity) {
                    (context as PropertyDetailActivity).potentialCalculated(
                        -1, statusCode, message
                    )
                }
            }
        }
    }

    fun logOutPrerequisites() {
        KeyStore(context).deleteKey()
        Session(context).loggedIn(false)
    }
}