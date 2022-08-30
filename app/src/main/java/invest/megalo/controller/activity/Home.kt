package invest.megalo.controller.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import invest.megalo.R
import invest.megalo.model.*
import org.json.JSONObject

class Home : AppCompatActivity() {
    private lateinit var loader: CustomLoader
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        loader = CustomLoader(this)
        findViewById<TextView>(R.id.log_out).setOnClickListener {
            if (InternetCheck(this, findViewById(R.id.parent)).status()) {
                loader.show(getString(R.string.logging_you_out))
                ServerConnection(
                    this, "logOut", Request.Method.DELETE, "login/delete",
                    JSONObject()
                )
            }
        }
    }

    fun loggedOut(l: Int) {
        loader.dismiss()
        if (l == 1) {
            finish()
            Session(this).loggedIn(false)
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            CustomSnackBar(
                this@Home,
                findViewById(R.id.parent),
                resources.getString(R.string.server_error_message),
                "error"
            )
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}