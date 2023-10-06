package invest.megalo.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import invest.megalo.R
import invest.megalo.controller.activity.HomeActivity


class ProfileChildListAdapter(
    private val context: Context,
    private val groups: ArrayList<String>,
    private val children: HashMap<String, ArrayList<String>>
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return groups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if (children.size != 0) {
            if (groupPosition == 0 && (context as HomeActivity).hasLoaded) {
                children[groups[groupPosition]]!!.size
            } else {
                0
            }
        } else {
            0
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return groups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return children[groups[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?, viewGroup: ViewGroup?
    ): View {
        val id = getGroup(groupPosition) as String
        var v = convertView
        if (v == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = layoutInflater.inflate(R.layout.list_profile_child_item, viewGroup, false)
        }
        val parent: RelativeLayout = v!!.findViewById(R.id.parent)
        val img: ImageView = v.findViewById(R.id.img)
        val forward: ImageView = v.findViewById(R.id.forward)
        val text: TextView = v.findViewById(R.id.text)
        val notificationsDot: TextView = v.findViewById(R.id.notifications_dot)
        val value: TextView = v.findViewById(R.id.value)

        if (context is HomeActivity) {
            text.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
            text.setTextColor(
                ContextCompat.getColor(
                    context, R.color.black_white
                )
            )

            forward.contentDescription = context.getString(R.string.go_forward)
            forward.setImageResource(R.drawable.arrow_forward)
            forward.visibility = View.VISIBLE

            notificationsDot.background = ContextCompat.getDrawable(
                context, R.drawable.notification_dot
            )
            notificationsDot.visibility = View.GONE

            value.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
            value.setTextColor(
                ContextCompat.getColor(
                    context, R.color.darkGrey_lightGrey
                )
            )
            value.visibility = View.GONE

            if (id == "log_out" || id == "delete_account") {
                img.visibility = View.GONE
                val rlp = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                rlp.addRule(RelativeLayout.CENTER_VERTICAL)
                rlp.addRule(RelativeLayout.START_OF, R.id.container)
                rlp.addRule(RelativeLayout.ALIGN_PARENT_START)
                rlp.setMargins(
                    0, 0, TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.normal_padding),
                        context.resources.displayMetrics
                    ).toInt(), 0
                )
                text.layoutParams = rlp

                if (id == "log_out") {
                    parent.background = ContextCompat.getDrawable(
                        context, R.drawable.profile_list_item_background
                    )
                    text.text = context.getString(R.string.log_out)
                    forward.visibility = View.GONE
                } else {
                    parent.background = ContextCompat.getDrawable(
                        context, R.drawable.dark_red_light_red_solid_extra_curved_corners
                    )
                    text.text = context.getString(R.string.delete_account)
                    text.setTextColor(ContextCompat.getColor(context, R.color.white))
                    forward.visibility = View.GONE
                }
            } else {
                when (groupPosition) {
                    0 -> {
                        parent.background = ContextCompat.getDrawable(
                            context, R.drawable.profile_list_item_background_top
                        )
                    }

                    groups.size - 1 -> {
                        parent.background = ContextCompat.getDrawable(
                            context, R.drawable.profile_list_item_background_bottom
                        )
                    }

                    else -> {
                        parent.background = ContextCompat.getDrawable(
                            context, R.drawable.white_night_solid_straight
                        )
                    }
                }
                when (id) {
                    "verification_status" -> {
                        text.text = context.getString(R.string.verification_status)
                        img.setImageResource(R.drawable.identification)
                        forward.visibility = View.INVISIBLE
                        if (context.hasLoaded && context.identityVerificationStatus == "unverified" || context.hasLoaded && !context.emailVerified) {
                            notificationsDot.visibility = View.VISIBLE
                        } else {
                            notificationsDot.visibility = View.GONE
                        }
                    }

                    "payment_methods" -> {
                        text.text = context.getString(R.string.payment_methods)
                        img.setImageResource(R.drawable.credit_card)
                    }

                    "terms_&_conditions" -> {
                        text.text = context.getString(R.string.terms_conditions)
                        img.setImageResource(R.drawable.document)
                    }

                    "privacy_policy" -> {
                        text.text = context.getString(R.string.privacy_policy)
                        img.setImageResource(R.drawable.lock_document)
                    }

                    "refer_&_earn" -> {
                        text.text = context.getString(R.string.refer_earn)
                        img.setImageResource(R.drawable.gift)
                    }

                    "write_a_review" -> {
                        text.text = context.getString(R.string.write_a_review)
                        img.setImageResource(R.drawable.star)
                    }

                    "email_us" -> {
                        text.text = context.getString(R.string.email_us)
                        img.setImageResource(R.drawable.envelope)
                    }

                    "app_version" -> {
                        text.text = context.getString(R.string.app_version_text)
                        img.setImageResource(R.drawable.app)
                        forward.visibility = View.GONE
                        value.visibility = View.VISIBLE
                        value.text = context.getString(R.string.app_version)
                    }
                }
            }
        }
        return v
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        viewGroup: ViewGroup?
    ): View {
        val id = getChild(groupPosition, childPosition) as String
        var v = convertView
        if (v == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = layoutInflater.inflate(R.layout.list_profile_child_item, viewGroup, false)
        }
        val parent: RelativeLayout = v!!.findViewById(R.id.parent)
        val img: ImageView = v.findViewById(R.id.img)
        val forward: ImageView = v.findViewById(R.id.forward)
        val text: TextView = v.findViewById(R.id.text)
        val notificationsDot: TextView = v.findViewById(R.id.notifications_dot)

        if (context is HomeActivity) {
            parent.background = ContextCompat.getDrawable(
                context, R.drawable.white_night_solid_straight
            )

            text.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
            )
            text.setTextColor(
                ContextCompat.getColor(
                    context, R.color.black_white
                )
            )

            forward.contentDescription = context.getString(R.string.go_forward)
            forward.setImageResource(R.drawable.arrow_forward)

            notificationsDot.background = ContextCompat.getDrawable(
                context, R.drawable.notification_dot
            )
            if (id == "email_verification") {
                text.text = context.getString(R.string.email_verification)
                if (context.emailVerified) {
                    img.setImageResource(R.drawable.success_green_check_circle)
                    forward.visibility = View.INVISIBLE
                } else {
                    img.setImageResource(R.drawable.dark_red_light_red_body_white_center_close_circle)
                    forward.visibility = View.VISIBLE
                }
            } else if (id == "identity_verification") {
                text.text = context.getString(R.string.identity_verification)
                if (context.identityVerificationStatus != "unverified") {
                    if (context.identityVerificationStatus == "verified") {
                        img.setImageResource(R.drawable.success_green_check_circle)
                    } else if (context.identityVerificationStatus == "pending") {
                        img.setImageResource(R.drawable.hour_glass)
                    }
                    forward.visibility = View.INVISIBLE
                } else {
                    img.setImageResource(R.drawable.dark_red_light_red_body_white_center_close_circle)
                    forward.visibility = View.VISIBLE
                }
            }
        }
        return v
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}