package invest.megalo.controller.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import invest.megalo.R
import invest.megalo.adapter.ProfileChildListAdapter
import invest.megalo.adapter.ProfileParentListAdapter
import invest.megalo.controller.activity.HomeActivity
import invest.megalo.data.ProfileParentListData
import invest.megalo.model.NoDividerListItemDecoration

class ProfileFragment : Fragment() {
    lateinit var list: RecyclerView
    private lateinit var parentListItems: ArrayList<ProfileParentListData>
    private lateinit var parent: LinearLayout
    private lateinit var topParent: FrameLayout
    private lateinit var title: TextView
    private lateinit var groups: ArrayList<String>
    private lateinit var children: HashMap<String, ArrayList<String>>


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        parent.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), R.color.backgroundGrey_black
            )
        )
        topParent.background = ContextCompat.getDrawable(
            requireContext(), R.drawable.action_bar_background
        )
        val padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            requireActivity().resources.getDimension(R.dimen.normal_padding),
            requireActivity().resources.displayMetrics
        ).toInt()
        topParent.setPadding(padding, padding, padding, padding)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        title.text = getString(R.string.profile)
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            requireActivity().resources.getDimension(R.dimen.normal_text)
        )
        (activity as HomeActivity).profileAdapter.notifyItemRangeChanged(
            0, (activity as HomeActivity).profileAdapter.itemCount
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        parent = v.findViewById(R.id.parent)
        topParent = v.findViewById(R.id.top_parent)
        title = v.findViewById(R.id.title)
        title.text = getString(R.string.profile)
        list = v.findViewById(R.id.list)
        (list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        groups = ArrayList()
        children = HashMap()

        parentListItems = ArrayList()
        parentListItems.add(
            ProfileParentListData(
                parentListItems.size.inc().toString(),
                ProfileChildListAdapter(requireContext(), groups, children)
            )
        )

        groups = ArrayList()
        groups.add("verification_status")
        groups.add("payment_methods")
        children = HashMap()
        val newChildren = ArrayList<String>()
        newChildren.add("email_verification")
        newChildren.add("identity_verification")
        children[groups[0]] = newChildren

        parentListItems.add(
            ProfileParentListData(
                parentListItems.size.inc().toString(),
                ProfileChildListAdapter(requireContext(), groups, children)
            )
        )

        groups = ArrayList()
        groups.add("terms_&_conditions")
        groups.add("privacy_policy")
        groups.add("refer_&_earn")
        groups.add("write_a_review")
        children = HashMap()

        parentListItems.add(
            ProfileParentListData(
                parentListItems.size.inc().toString(),
                ProfileChildListAdapter(requireContext(), groups, children)
            )
        )

        groups = ArrayList()
        groups.add("log_out")
        children = HashMap()

        parentListItems.add(
            ProfileParentListData(
                parentListItems.size.inc().toString(),
                ProfileChildListAdapter(requireContext(), groups, children)
            )
        )

        groups = ArrayList()
        groups.add("delete_account")
        children = HashMap()

        parentListItems.add(
            ProfileParentListData(
                parentListItems.size.inc().toString(),
                ProfileChildListAdapter(requireContext(), groups, children)
            )
        )

        (activity as HomeActivity).profileAdapter =
            ProfileParentListAdapter(requireContext(), parentListItems)
        list.apply {
            addItemDecoration(
                NoDividerListItemDecoration(
                    resources.getDimension(R.dimen.normal_padding).toInt(),
                    resources.getDimension(R.dimen.large_padding).toInt(),
                    resources.getDimension(R.dimen.large_padding).toInt(),
                    resources.getDimension(R.dimen.large_padding).toInt()
                )
            )
        }
        list.adapter = (activity as HomeActivity).profileAdapter
        return v
    }
}