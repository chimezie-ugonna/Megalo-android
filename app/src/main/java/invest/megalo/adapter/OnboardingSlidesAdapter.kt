package invest.megalo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import invest.megalo.R

class OnboardingSlidesAdapter(
    val context: Context,
    private val images: ArrayList<String>,
    private val texts: ArrayList<String>
) : RecyclerView.Adapter<OnboardingSlidesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_on_boarding_slide, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img.setAnimation(images[position])
        holder.text.text = texts[position]
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var img: LottieAnimationView = v.findViewById(R.id.img)
        var text: TextView = v.findViewById(R.id.text)
    }
}