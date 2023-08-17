package invest.megalo.controller.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import invest.megalo.R
import invest.megalo.adapter.GalleryListAdapter
import invest.megalo.model.SetAppTheme

class GalleryActivity : AppCompatActivity() {
    private lateinit var count: TextView
    private lateinit var list: RecyclerView
    private lateinit var imageUrlList: ArrayList<String>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var close: ImageView

    override fun onConfigurationChanged(newConfig: Configuration) {
        SetAppTheme(this)
        super.onConfigurationChanged(newConfig)
        close.contentDescription = getString(R.string.close_page)
        count.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.normal_text)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SetAppTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        count = findViewById(R.id.count)
        imageUrlList = ArrayList()
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("image_urls") != null) {
                val imageUrls = bundle.getString("image_urls").toString().split(", ")
                count.text = getString(R.string._0_0, "1", "${imageUrls.size}")
                for (i in imageUrls.indices) {
                    imageUrlList.add(imageUrls[i].split("+ ")[0])
                }
            }
        }

        close = findViewById(R.id.close)
        close.setOnClickListener {
            finish()
        }

        list = findViewById(R.id.list)
        PagerSnapHelper().attachToRecyclerView(list)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() + 1 != 0) {
                        count.text = getString(
                            R.string._0_0,
                            "${linearLayoutManager.findFirstCompletelyVisibleItemPosition() + 1}",
                            "${imageUrlList.size}"
                        )
                    }
                }
            })
            layoutManager = linearLayoutManager
            adapter = GalleryListAdapter(this@GalleryActivity, imageUrlList)
        }
    }
}