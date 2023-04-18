package invest.megalo.model

import android.content.Context
import android.content.SharedPreferences
import invest.megalo.R

class Session(context: Context) {
    private val sp: SharedPreferences = context.getSharedPreferences(
        context.resources.getString(R.string.app_name), Context.MODE_PRIVATE
    )
    private val spe: SharedPreferences.Editor = sp.edit()

    fun deviceToken(data: String) {
        spe.putString("deviceToken", data)
        spe.commit()
    }

    fun deviceToken(): String? {
        return sp.getString("deviceToken", "")
    }

    fun encryptedTokenIv(data: String) {
        spe.putString("encryptedTokenIv", data)
        spe.commit()
    }

    fun encryptedTokenIv(): String? {
        return sp.getString("encryptedTokenIv", "")
    }

    fun encryptedToken(data: String) {
        spe.putString("encryptedToken", data)
        spe.commit()
    }

    fun encryptedToken(): String? {
        return sp.getString("encryptedToken", "")
    }

    fun loggedIn(data: Boolean) {
        spe.putBoolean("loggedIn", data)
        spe.commit()
    }

    @Suppress("BooleanMethodIsAlwaysInverted")
    fun loggedIn(): Boolean {
        return sp.getBoolean("loggedIn", false)
    }

    fun onboarded(data: Boolean) {
        spe.putBoolean("onboarded", data)
        spe.commit()
    }

    @Suppress("BooleanMethodIsAlwaysInverted")
    fun onboarded(): Boolean {
        return sp.getBoolean("onboarded", false)
    }

    fun devicePhoneNumber(data: String) {
        spe.putString("devicePhoneNumber", data)
        spe.commit()
    }

    fun devicePhoneNumber(): String? {
        return sp.getString("devicePhoneNumber", "")
    }
}