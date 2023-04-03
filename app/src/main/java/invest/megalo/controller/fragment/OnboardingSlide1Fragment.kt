package invest.megalo.controller.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import invest.megalo.R

class OnboardingSlide1Fragment : Fragment() {
    lateinit var lottie: LottieAnimationView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            requireActivity().window.navigationBarColor =
                ContextCompat.getColor(requireActivity(), R.color.black)
        }
        val v = inflater.inflate(R.layout.fragment_onboarding_slide1, container, false)
        lottie = v.findViewById(R.id.lottie)
        lottie.playAnimation()
        return v
    }

    fun playLottie() {
        lottie.playAnimation()
    }
}