package invest.megalo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import invest.megalo.R

class OnBoardingSlidesAdapter(val context: Context, private val images: ArrayList<Int>) :
    RecyclerView.Adapter<OnBoardingSlidesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_on_boarding_slide, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var img: ImageView = v.findViewById(R.id.img)
    }
}