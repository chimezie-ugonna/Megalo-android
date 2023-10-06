package invest.megalo.controller.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import invest.megalo.R

class PortfolioFragment : Fragment() {
    private lateinit var parent: LinearLayout
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_portfolio, container, false)
        parent = v.findViewById(R.id.parent)
        return v
    }
}