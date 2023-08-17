package invest.megalo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import invest.megalo.R

class GalleryListAdapter(val context: Context, private val imageUrlList: ArrayList<String>) :
    RecyclerView.Adapter<GalleryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_gallery, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(imageUrlList[position]).centerCrop()
            .placeholder(R.drawable.img_placeholder).into(holder.img)
    }

    override fun getItemCount(): Int {
        return imageUrlList.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.img)
    }
}