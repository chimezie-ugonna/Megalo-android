package invest.megalo.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import invest.megalo.R
import invest.megalo.controller.activity.EditProfileActivity
import invest.megalo.controller.activity.HomeActivity
import invest.megalo.controller.activity.ReferralActivity
import invest.megalo.controller.activity.WebActivity
import invest.megalo.controller.fragment.BottomSheetDialogFragment
import invest.megalo.data.ProfileParentListData
import invest.megalo.model.CustomSnackBar
import invest.megalo.model.NoScrollExpandableListView


class ProfileParentListAdapter(
    private val context: Context, private val data: ArrayList<ProfileParentListData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var headerView = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == headerView) {
            HeaderViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.content_profile_list_header, parent, false)
            )
        } else {
            ViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.list_profile_parent_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = data[position]
        if (context is HomeActivity) {
            if (getItemViewType(position) == headerView) {
                val headerViewHolder = (holder as HeaderViewHolder)

                if (dataItem.section == "1") {
                    headerViewHolder.name.text = context.fullName
                    headerViewHolder.name.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.big_text)
                    )
                    headerViewHolder.name.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.black_white
                        )
                    )
                    headerViewHolder.email.text = context.emailAddress
                    headerViewHolder.email.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_text)
                    )
                    headerViewHolder.email.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.darkGrey_lightGrey
                        )
                    )
                }

                headerViewHolder.forward.contentDescription = context.getString(R.string.go_forward)
                headerViewHolder.forward.setImageResource(R.drawable.arrow_forward)

                val rlp = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                rlp.addRule(RelativeLayout.CENTER_VERTICAL)
                rlp.addRule(RelativeLayout.START_OF, R.id.forward)
                rlp.addRule(RelativeLayout.END_OF, R.id.lottie)
                rlp.setMargins(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt(), 0, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt(), 0
                )
                headerViewHolder.container.layoutParams = rlp

                headerViewHolder.parent.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt()
                )
                headerViewHolder.parent.background = ContextCompat.getDrawable(
                    context, R.drawable.profile_list_item_background
                )
                headerViewHolder.parent.setOnClickListener {
                    context.startActivity(Intent(context, EditProfileActivity::class.java))
                }
            } else {
                val viewHolder = (holder as ViewHolder)
                if (dataItem.section != "1") {
                    viewHolder.title.visibility = View.VISIBLE
                    viewHolder.title.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_text)
                    )
                    viewHolder.title.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.darkGrey_lightGrey
                        )
                    )
                    when (dataItem.section) {
                        "2" -> {
                            viewHolder.title.text = context.getString(R.string.account)
                        }

                        "3" -> {
                            viewHolder.title.text = context.getString(R.string.in_app_name)
                        }

                        else -> {
                            viewHolder.title.visibility = View.GONE
                        }
                    }

                    viewHolder.list.divider =
                        ContextCompat.getDrawable(context, R.drawable.list_divider)
                    viewHolder.list.setChildDivider(
                        ContextCompat.getDrawable(
                            context, R.drawable.list_divider
                        )
                    )
                    viewHolder.list.dividerHeight = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.tiny_stroke_size),
                        context.resources.displayMetrics
                    ).toInt()

                    if (viewHolder.list.expandableListAdapter != null && !viewHolder.list.expandableListAdapter.isEmpty) {
                        dataItem.childListAdapters.notifyDataSetChanged()
                    } else {
                        viewHolder.list.setAdapter(dataItem.childListAdapters)
                    }
                    viewHolder.list.setOnGroupClickListener { _, _, groupPosition, _ ->
                        when (dataItem.childListAdapters.getGroup(groupPosition) as String) {
                            "payment_methods" -> {

                            }

                            "terms_&_conditions" -> {
                                val intent = Intent(context, WebActivity::class.java)
                                intent.putExtra("url", "google.com")
                                context.startActivity(intent)
                            }

                            "privacy_policy" -> {
                                val intent = Intent(context, WebActivity::class.java)
                                intent.putExtra("url", "google.com")
                                context.startActivity(intent)
                            }

                            "refer_&_earn" -> {
                                context.startActivity(Intent(context, ReferralActivity::class.java))
                            }

                            "write_a_review" -> {
                                try {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=${context.packageName}")
                                        )
                                    )
                                } catch (e: ActivityNotFoundException) {
                                    context.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                        )
                                    )
                                }
                            }

                            "email_us" -> {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(
                                        Intent.EXTRA_EMAIL,
                                        arrayOf(context.getString(R.string.support_email))
                                    )
                                    putExtra(
                                        Intent.EXTRA_SUBJECT,
                                        context.getString(R.string.help_required)
                                    )
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        context.getString(R.string.please_clear_this_auto_generated_text_and_then_tell_us_what_you_need_help_with)
                                    )
                                }
                                if (emailIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(emailIntent)
                                } else {
                                    CustomSnackBar(
                                        context,
                                        context.parent,
                                        context.getString(R.string.we_could_not_find_an_available_email_app),
                                        "error"
                                    )
                                }
                            }

                            "log_out" -> {
                                BottomSheetDialogFragment(
                                    context.findViewById(R.id.parent),
                                    context,
                                    "confirm_log_out",
                                    R.string.logout_confirmation,
                                    R.string.are_you_sure_you_want_to_log_out,
                                    R.string.yes,
                                    R.string.no
                                ).show(context.supportFragmentManager, "confirm_log_out")
                            }

                            "delete_account" -> {
                                BottomSheetDialogFragment(
                                    context.findViewById(R.id.parent),
                                    context,
                                    "confirm_account_deletion",
                                    R.string.account_deletion_confirmation,
                                    R.string.deleting_your_account_means_losing_access_to_potential_investment_opportunities_in_properties_are_you_sure_you_want_to_end_your_investment_journey_with_us_consider_the_potential_returns_and_benefits_of_staying_invested_and_the_impact_it_could_have_on_your_long_term_financial_goals_we_re_here_to_help_you_make_the_most_of_your_investment_experience_but_if_you_re_certain_please_go_ahead_and_confirm_your_account_deletion,
                                    R.string.yes,
                                    R.string.no
                                ).show(context.supportFragmentManager, "confirm_account_deletion")
                            }
                        }
                        return@setOnGroupClickListener false
                    }
                    viewHolder.list.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                        val id = dataItem.childListAdapters.getChild(
                            groupPosition, childPosition
                        ) as String
                        if (id == "email_verification") {
                            if (!context.emailVerified) {
                                context.sendOtp()
                            }
                        } else if (id == "identity_verification") {
                            if (context.identityVerificationStatus == "unverified") {
                                context.verify()
                            }
                        }
                        return@setOnChildClickListener false
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            headerView
        } else {
            1
        }
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var parent: RelativeLayout = v.findViewById(R.id.parent)
        var container: LinearLayout = v.findViewById(R.id.container)
        var name: TextView = v.findViewById(R.id.name)
        var email: TextView = v.findViewById(R.id.email)
        var forward: ImageView = v.findViewById(R.id.forward)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var list: NoScrollExpandableListView = v.findViewById(R.id.list)
        var title: TextView = v.findViewById(R.id.title)
    }
}
