package invest.megalo.model

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import invest.megalo.R

class SetAppTheme(context: Context) {
    init {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            context.setTheme(R.style.Theme_Megalo_Night)
        } else {
            context.setTheme(R.style.Theme_Megalo)
        }
    }
}