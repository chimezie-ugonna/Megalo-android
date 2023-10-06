package invest.megalo.controller.fragment

import android.annotation.SuppressLint
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import invest.megalo.R
import invest.megalo.controller.activity.GalleryActivity
import invest.megalo.controller.activity.PropertyDetailActivity
import java.text.DecimalFormat

class PropertyDetailOverviewFragment : Fragment() {
    private lateinit var img: ShapeableImageView
    private lateinit var imageCount: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var subParent: LinearLayout
    private lateinit var propertyValueTitle: TextView
    private lateinit var propertyValueValue: TextView
    private lateinit var percentageAvailableTitle: TextView
    private lateinit var percentageAvailableValue: TextView
    private lateinit var monthlyEarningTitle: TextView
    private lateinit var monthlyEarningValue: TextView
    private lateinit var sizeTitle: TextView
    private lateinit var sizeValue: TextView
    private lateinit var averageAnnualChangeTitle: TextView
    private lateinit var averageAnnualChangeValue: TextView
    private lateinit var addressTitle: TextView
    private lateinit var addressValue: TextView
    private lateinit var v: View
    private lateinit var df: DecimalFormat

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        subParent.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_black))

        img.strokeColor = ContextCompat.getColorStateList(
            requireContext(), R.color.darkGrey_lightGrey
        )

        imageCount.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        title.text = getString(R.string.details)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.big_text)
        )

        description.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_white))
        description.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        propertyValueTitle.text = getString(R.string.property_value)
        propertyValueTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        propertyValueTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        propertyValueValue.text = getString(
            R.string.dollar_value, df.format((activity as PropertyDetailActivity).valueUsd)
        )
        propertyValueValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        propertyValueValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        percentageAvailableTitle.text = getString(R.string.percentage_available)
        percentageAvailableTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        percentageAvailableTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        percentageAvailableValue.text = getString(
            R.string.percentage_value,
            DecimalFormat("#,###.##").format((activity as PropertyDetailActivity).percentageAvailable)
        )
        percentageAvailableValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        percentageAvailableValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        monthlyEarningTitle.text = getString(R.string.monthly_earning)
        monthlyEarningTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        monthlyEarningTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        monthlyEarningValue.text = getString(
            R.string.dollar_value, df.format((activity as PropertyDetailActivity).monthlyEarningUsd)
        )
        monthlyEarningValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        monthlyEarningValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        sizeTitle.text = getString(R.string.size)
        sizeTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        sizeTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        sizeValue.text = getString(
            R.string.square_foot_value,
            DecimalFormat("#,###.##").format((activity as PropertyDetailActivity).sizeSf)
        )
        sizeValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        sizeValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        averageAnnualChangeTitle.text = getString(R.string.average_annual_change)
        averageAnnualChangeTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        averageAnnualChangeTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        averageAnnualChangeValue.text = getString(
            R.string.percentage_value,
            DecimalFormat("#,###.##").format((activity as PropertyDetailActivity).valueAverageAnnualChangePercentage)
        )
        averageAnnualChangeValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        averageAnnualChangeValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        addressTitle.text = getString(R.string.address)
        addressTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.darkGrey_lightGrey
            )
        )
        addressTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        addressValue.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.black_white
            )
        )
        addressValue.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )

        v.findViewById<LinearLayout>(R.id.detail_list_container).dividerDrawable =
            ContextCompat.getDrawable(
                requireContext(), R.drawable.divider
            )
    }

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_property_detail_overview, container, false)

        subParent = v.findViewById(R.id.sub_parent)
        img = v.findViewById(R.id.img)
        img.setOnClickListener {
            val intent = Intent(activity, GalleryActivity::class.java)
            intent.putExtra("image_urls", (activity as PropertyDetailActivity).imageUrls)
            startActivity(intent)
        }

        val imageUrls = (activity as PropertyDetailActivity).imageUrls.split(", ")
        val firstImageUrl = if (imageUrls.isNotEmpty()) {
            imageUrls[0].split("+ ")[0]
        } else {
            ""
        }
        if (firstImageUrl != "") {
            Glide.with(this).load(firstImageUrl).centerCrop()
                .placeholder(R.drawable.img_placeholder).into(img)
        }

        imageCount = v.findViewById(R.id.image_count)
        if (imageUrls.size > 1) {
            imageCount.visibility = View.VISIBLE
            imageCount.text = getString(R.string.plus, "${imageUrls.size - 1}")
        } else {
            imageCount.visibility = View.GONE
        }

        df = DecimalFormat("#,##0.0#")
        df.minimumFractionDigits = 2

        title = v.findViewById(R.id.title)
        description = v.findViewById(R.id.description)
        description.text = (activity as PropertyDetailActivity).descriptionText

        v.findViewById<View>(R.id.property_value_layout).apply {
            propertyValueTitle = findViewById(R.id.title_value_title)
            propertyValueValue = findViewById(R.id.title_value_value)

            propertyValueTitle.text = getString(R.string.property_value)
            propertyValueValue.text = getString(
                R.string.dollar_value, df.format((activity as PropertyDetailActivity).valueUsd)
            )
        }

        v.findViewById<View>(R.id.percentage_available_layout).apply {
            percentageAvailableTitle = findViewById(R.id.title_value_title)
            percentageAvailableValue = findViewById(R.id.title_value_value)

            percentageAvailableTitle.text = getString(R.string.percentage_available)
            percentageAvailableValue.text = getString(
                R.string.percentage_value,
                DecimalFormat("#,###.##").format((activity as PropertyDetailActivity).percentageAvailable)
            )
        }

        v.findViewById<View>(R.id.monthly_earning_layout).apply {
            monthlyEarningTitle = findViewById(R.id.title_value_title)
            monthlyEarningValue = findViewById(R.id.title_value_value)

            monthlyEarningTitle.text = getString(R.string.monthly_earning)
            monthlyEarningValue.text = getString(
                R.string.dollar_value,
                df.format((activity as PropertyDetailActivity).monthlyEarningUsd)
            )
        }

        v.findViewById<View>(R.id.size_layout).apply {
            sizeTitle = findViewById(R.id.title_value_title)
            sizeValue = findViewById(R.id.title_value_value)

            sizeTitle.text = getString(R.string.size)
            sizeValue.text = getString(
                R.string.square_foot_value,
                DecimalFormat("#,###.##").format((activity as PropertyDetailActivity).sizeSf)
            )
        }

        v.findViewById<View>(R.id.average_annual_change_layout).apply {
            averageAnnualChangeTitle = findViewById(R.id.title_value_title)
            averageAnnualChangeValue = findViewById(R.id.title_value_value)

            averageAnnualChangeTitle.text = getString(R.string.average_annual_change)
            averageAnnualChangeValue.text = getString(
                R.string.percentage_value,
                DecimalFormat("#,###.##").format((activity as PropertyDetailActivity).valueAverageAnnualChangePercentage)
            )
        }

        v.findViewById<View>(R.id.address_layout).apply {
            addressTitle = findViewById(R.id.title_value_title)
            addressValue = findViewById(R.id.title_value_value)

            addressTitle.text = getString(R.string.address)
            addressValue.text = (activity as PropertyDetailActivity).address
        }
        return v
    }
}