package invest.megalo.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import invest.megalo.R
import invest.megalo.controller.activity.Home

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        v.findViewById<TextView>(R.id.verify)
            .setOnClickListener {
                (context as Home).verify()
            }
        return v
    }
}