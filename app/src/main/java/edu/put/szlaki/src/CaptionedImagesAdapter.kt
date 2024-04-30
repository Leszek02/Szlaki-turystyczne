package edu.put.szlaki.src

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.put.szlaki.R

class CaptionedImagesAdapter(private val captions: Array<String>, private val imagePaths: Array<String>,):
                             RecyclerView.Adapter<CaptionedImagesAdapter.ViewHolder>() {

    private var listener: Listener? = null

    interface Listener {
        fun onClick(name: String)
    }

    fun setListener(listener: Listener){
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return captions.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_captioned_image, parent, false) as CardView
        return ViewHolder(cv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardView = holder.cardView
        val imageView: ImageView = cardView.findViewById(R.id.info_image)
        Glide.with(cardView.context)// load images from directory
            .load(imagePaths[position])
            .into(imageView)

        val textView: TextView = cardView.findViewById(R.id.info_text)
        textView.text = captions[position]
        cardView.setOnClickListener{
            listener?.onClick(textView.text.toString())
        }
    }

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

}