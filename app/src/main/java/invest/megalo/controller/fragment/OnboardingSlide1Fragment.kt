package invest.megalo.controller.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import invest.megalo.R

class OnboardingSlide1Fragment : Fragment() {
    private lateinit var lottie: LottieAnimationView
    private lateinit var parent: LinearLayout
    private lateinit var header: TextView
    private lateinit var subHeader: TextView

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))

        header.text = getString(R.string.welcome_to_megalo)
        header.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        header.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        subHeader.text =
            getString(R.string.invest_in_real_estate_from_anywhere_anytime_and_on_any_budget)
        subHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGrey_lightGrey))
        subHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_onboarding_slide1, container, false)
        parent = v.findViewById(R.id.parent)
        header = v.findViewById(R.id.header)
        subHeader = v.findViewById(R.id.sub_header)
        lottie = v.findViewById(R.id.lottie)
        lottie.playAnimation()
        return v
    }

    fun playLottie() {
        lottie.playAnimation()
    }
}