package invest.megalo.controller.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.fragment.app.Fragment
import invest.megalo.R
import invest.megalo.controller.activity.Home
import invest.megalo.model.Dialog
import invest.megalo.model.Session

class ProfileFragment : Fragment() {
    private lateinit var secondaryLock: TextView
    private lateinit var session: Session
    private var settingUpLock: Boolean = false
    private var condition1: Boolean = false
    private var condition2: Boolean = false
    private val intentResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        v.findViewById<TextView>(R.id.verify).setOnClickListener {
            (context as Home).verify()
        }
        secondaryLock = v.findViewById(R.id.secondary_lock)
        return v
    }

    override fun onResume() {
        super.onResume()
        val biometricManager = BiometricManager.from(requireContext())
        condition1 =
            biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
        condition2 =
            biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
        if (condition1 || condition2) {
            session = Session(requireContext())
            if (condition1) {
                if (settingUpLock) {
                    session.useSecondaryLock(true)
                    secondaryLock.text = resources.getString(R.string.disable_secondary_lock)
                } else {
                    if (session.useSecondaryLock()) {
                        secondaryLock.text = resources.getString(R.string.disable_secondary_lock)
                    } else {
                        secondaryLock.text = resources.getString(R.string.enable_secondary_lock)
                    }
                }
            } else {
                session.useSecondaryLock(false)
                secondaryLock.text = resources.getString(R.string.enable_secondary_lock)
            }

            secondaryLock.setOnClickListener {
                if (session.useSecondaryLock()) {
                    session.useSecondaryLock(false)
                    secondaryLock.text = resources.getString(R.string.enable_secondary_lock)
                } else {
                    if (condition2) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                )
                            }
                            intentResultLauncher.launch(
                                enrollIntent
                            )
                            settingUpLock = true
                        } else {
                            Dialog(
                                (activity as Home).findViewById(R.id.parent),
                                requireContext(),
                                getString(R.string.secondary_lock_setup),
                                getString(R.string.lock_setup_required),
                                getString(R.string.lock_setup_required_text),
                                getString(R.string.got_it),
                                "",
                                false,
                                R.drawable.app_green_lock_settings
                            ).show()
                        }
                    } else {
                        session.useSecondaryLock(true)
                        secondaryLock.text = resources.getString(R.string.disable_secondary_lock)
                    }
                }
            }
        } else {
            secondaryLock.visibility = View.GONE
        }
        settingUpLock = false
    }
}