package invest.megalo.controller.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import invest.megalo.R
import invest.megalo.model.SetAppTheme
import java.util.concurrent.Executor

class SecondaryLock : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var unlock: AppCompatButton
    private lateinit var promptInfo: PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary_lock)

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(this@SecondaryLock, Home::class.java))
                }
            })

        promptInfo = PromptInfo.Builder().setTitle(getString(R.string.unlock_megalo))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        unlock = findViewById(R.id.button)
        unlock.text = getString(R.string.unlock)
        unlock.setOnClickListener {
            biometricPrompt.authenticate(
                promptInfo
            )
        }

        biometricPrompt.authenticate(promptInfo)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}