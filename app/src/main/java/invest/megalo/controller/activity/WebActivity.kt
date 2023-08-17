package invest.megalo.controller.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import invest.megalo.R
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.SetAppTheme


class WebActivity : AppCompatActivity() {
    private lateinit var title: TextView
    private lateinit var more: ImageView
    private lateinit var webView: WebView
    private lateinit var close: ImageView
    private lateinit var progress: LinearProgressIndicator
    private lateinit var popupMenu: PopupMenu
    private lateinit var subParent: FrameLayout
    private var popupMenuIsShowing: Boolean = false
    private var url: String = ""

    override fun onConfigurationChanged(newConfig: Configuration) {
        SetAppTheme(this)
        super.onConfigurationChanged(newConfig)
        val config = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (config == Configuration.UI_MODE_NIGHT_YES) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                false
        } else if (config == Configuration.UI_MODE_NIGHT_NO) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                true
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.white_black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white_black)

        subParent.background = ContextCompat.getDrawable(
            this, R.drawable.action_bar_background
        )
        subParent.setPadding(
            0, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.tiny_stroke_size),
                resources.displayMetrics
            ).toInt(), 0, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.tiny_stroke_size),
                resources.displayMetrics
            ).toInt()
        )

        close.contentDescription = getString(R.string.close_page)
        close.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        close.setImageResource(R.drawable.close)

        title.setTextColor(ContextCompat.getColor(this, R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        more.contentDescription = getString(R.string.more_options)
        more.foreground = ContextCompat.getDrawable(
            this, R.drawable.white_black_ripple_straight
        )
        more.setImageResource(R.drawable.more)

        progress.trackColor = ContextCompat.getColor(this, R.color.white_black)

        if (popupMenuIsShowing) {
            popupMenu.dismiss()
            definePopUpMenu()
            popupMenu.show()
            popupMenuIsShowing = true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("url") != null) {
                url = bundle.getString("url").toString()
            }
        }

        subParent = findViewById(R.id.sub_parent)
        progress = findViewById(R.id.progress)
        title = findViewById(R.id.title)
        close = findViewById(R.id.close)
        close.setOnClickListener {
            finish()
        }
        more = findViewById(R.id.more)
        more.setOnClickListener {
            definePopUpMenu()
            popupMenu.show()
            popupMenuIsShowing = true
        }

        webView = findViewById(R.id.web)
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webView.loadUrl(url)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress > 0 && progress.visibility != View.VISIBLE) {
                    progress.visibility = View.VISIBLE
                }
                progress.progress = newProgress
                if (newProgress == 100 && progress.visibility != View.GONE) {
                    progress.visibility = View.GONE
                }
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                title.text = view?.title
                this@WebActivity.url = url.toString()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

    private fun definePopUpMenu() {
        popupMenu = PopupMenu(this@WebActivity, more)
        popupMenu.inflate(R.menu.web_more_options)
        popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
            PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.refresh -> {
                        webView.loadUrl(url)
                    }

                    R.id.share -> {
                        startActivity(Intent.createChooser(Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, url)
                            type = "text/plain"
                        }, null))
                    }

                    R.id.browser -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            CustomSnackBar(
                                this@WebActivity,
                                findViewById(R.id.parent),
                                getString(R.string.we_could_not_find_any_available_browser),
                                "error"
                            )
                        }
                    }
                }
                return true
            }
        })
        popupMenu.setOnDismissListener { popupMenuIsShowing = false }
        popupMenu.setForceShowIcon(true)
    }
}