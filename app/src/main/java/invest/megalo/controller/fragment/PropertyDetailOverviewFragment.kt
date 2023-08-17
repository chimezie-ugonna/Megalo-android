package invest.megalo.controller.fragment

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import invest.megalo.R
import invest.megalo.adapter.PropertyDetailListAdapter
import invest.megalo.controller.activity.GalleryActivity
import invest.megalo.controller.activity.PropertyDetailActivity
import invest.megalo.model.ListItemDecoration

class PropertyDetailOverviewFragment : Fragment() {
    private lateinit var img: ShapeableImageView
    private lateinit var imageCount: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var list: RecyclerView
    private lateinit var titleResources: ArrayList<Int>
    private lateinit var valueResources: ArrayList<Int>
    private lateinit var valuesString: ArrayList<String?>
    private lateinit var valuesDouble: ArrayList<Double?>
    private lateinit var valuesInt: ArrayList<Int?>
    private lateinit var subParent: LinearLayout
    private lateinit var propertyDetailListAdapter: PropertyDetailListAdapter
    private lateinit var itemDecoration: ListItemDecoration

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

        list.removeItemDecoration(itemDecoration)
        itemDecoration = ListItemDecoration(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.divider
            )
        )
        list.addItemDecoration(itemDecoration)
        propertyDetailListAdapter.notifyItemRangeChanged(0, propertyDetailListAdapter.itemCount)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_property_detail_overview, container, false)

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

        title = v.findViewById(R.id.title)
        description = v.findViewById(R.id.description)
        description.text = (activity as PropertyDetailActivity).descriptionText

        titleResources = ArrayList()
        titleResources.add(R.string.property_value)
        titleResources.add(R.string.percentage_available)
        titleResources.add(R.string.monthly_earning)
        titleResources.add(R.string.size)
        titleResources.add(R.string.average_annual_change)
        titleResources.add(R.string.address)

        valueResources = ArrayList()
        valueResources.add(R.string.dollar_value)
        valueResources.add(R.string.percentage_value)
        valueResources.add(R.string.dollar_value)
        valueResources.add(R.string.square_foot_value)
        valueResources.add(R.string.percentage_value)
        valueResources.add(0)

        valuesString = ArrayList()
        valuesString.add(null)
        valuesString.add(null)
        valuesString.add(null)
        valuesString.add(null)
        valuesString.add(null)
        valuesString.add((activity as PropertyDetailActivity).address)

        valuesDouble = ArrayList()
        valuesDouble.add((activity as PropertyDetailActivity).valueUsd)
        valuesDouble.add((activity as PropertyDetailActivity).percentageAvailable)
        valuesDouble.add((activity as PropertyDetailActivity).monthlyEarningUsd)
        valuesDouble.add(null)
        valuesDouble.add((activity as PropertyDetailActivity).valueAverageAnnualChangePercentage)
        valuesDouble.add(null)

        valuesInt = ArrayList()
        valuesInt.add(null)
        valuesInt.add(null)
        valuesInt.add(null)
        valuesInt.add((activity as PropertyDetailActivity).sizeSf)
        valuesInt.add(null)
        valuesInt.add(null)

        propertyDetailListAdapter = PropertyDetailListAdapter(
            requireContext(), titleResources, valueResources, valuesString, valuesDouble, valuesInt
        )
        list = v.findViewById(R.id.list)
        itemDecoration = ListItemDecoration(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.divider
            )
        )
        list.apply {
            addItemDecoration(
                itemDecoration
            )
            adapter = propertyDetailListAdapter
            isNestedScrollingEnabled = false
        }

        return v
    }
}