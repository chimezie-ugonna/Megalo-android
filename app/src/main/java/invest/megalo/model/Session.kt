package invest.megalo.model

import android.content.Context
import android.content.SharedPreferences
import invest.megalo.R

class Session(context: Context) {
    private val sp: SharedPreferences = context.getSharedPreferences(
        context.resources.getString(R.string.app_name), Context.MODE_PRIVATE
    )
    private val spe: SharedPreferences.Editor = sp.edit()

    fun appTheme(state: String) {
        spe.putString("appTheme", state)
        spe.commit()
    }

    fun appTheme(): String? {
        return sp.getString("appTheme", "system")
    }

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

    fun loggedIn(): Boolean {
        return sp.getBoolean("loggedIn", false)
    }

    fun onboarded(data: Boolean) {
        spe.putBoolean("onboarded", data)
        spe.commit()
    }

    fun onboarded(): Boolean {
        return sp.getBoolean("onboarded", false)
    }

    fun useBiometric(data: Boolean) {
        spe.putBoolean("useBiometric", data)
        spe.commit()
    }

    fun useBiometric(): Boolean {
        return sp.getBoolean("useBiometric", false)
    }

    fun devicePhoneNumber(data: String) {
        spe.putString("devicePhoneNumber", data)
        spe.commit()
    }

    fun devicePhoneNumber(): String? {
        return sp.getString("devicePhoneNumber", "")
    }
}