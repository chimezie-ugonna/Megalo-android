package invest.megalo.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import invest.megalo.R
import java.text.DecimalFormat

class PropertyDetailListAdapter(
    val context: Context,
    private val titleResources: ArrayList<Int>,
    private val valueResources: ArrayList<Int>,
    private val valuesString: ArrayList<String?>,
    private val valuesDouble: ArrayList<Double?>,
    private val valuesInt: ArrayList<Int?>
) : RecyclerView.Adapter<PropertyDetailListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_property_detail, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return titleResources.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = context.getString(titleResources[position])
        holder.title.setTextColor(ContextCompat.getColor(context, R.color.darkGrey_lightGrey))
        holder.title.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
        )

        if (valuesString[position] != null) {
            holder.value.text = valuesString[position]
        } else if (valuesDouble[position] != null) {
            holder.value.text = context.getString(
                valueResources[position], DecimalFormat("#,###.##").format(valuesDouble[position])
            )
        } else if (valuesInt[position] != null) {
            if (valueResources[position] != 0) {
                holder.value.text = context.getString(
                    valueResources[position], DecimalFormat("#,###.##").format(valuesInt[position])
                )
            } else {
                holder.value.text = DecimalFormat("#,###.##").format(valuesInt[position]).toString()
            }
        }
        holder.value.setTextColor(ContextCompat.getColor(context, R.color.black_white))
        holder.value.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_text)
        )
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.title)
        val value: TextView = v.findViewById(R.id.value)
    }
}