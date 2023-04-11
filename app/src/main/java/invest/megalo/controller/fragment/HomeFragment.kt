package invest.megalo.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import invest.megalo.R
import invest.megalo.controller.activity.Home
import invest.megalo.model.Dialog

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        v.findViewById<TextView>(R.id.log_out).setOnClickListener {
            Dialog(
                (activity as Home).findViewById(R.id.parent),
                requireContext(),
                getString(R.string.confirm_logout),
                getString(R.string.logout_confirmation),
                getString(R.string.are_you_sure_you_want_to_log_out),
                getString(R.string.yes),
                getString(R.string.no),
                true
            ).show()
        }
        return v
    }
}