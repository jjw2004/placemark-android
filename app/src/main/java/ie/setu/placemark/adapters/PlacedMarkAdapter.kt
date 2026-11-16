package ie.setu.placemark.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.setu.placemark.R
import ie.setu.placemark.models.PlacedMarkData
import timber.log.Timber

class PlacedMarkAdapter(
    private var marks: List<PlacedMarkData>,
    private val listener: OnMarkListener
) : RecyclerView.Adapter<PlacedMarkAdapter.MarkViewHolder>() {

    class MarkViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val markImage: ImageView =
            itemView.findViewById(R.id.markImage)

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val descriptionText: TextView =
            itemView.findViewById(R.id.descriptionText)

        val coordinatesText: TextView =
            itemView.findViewById(R.id.coordinatesText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarkViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_placed_mark,
                parent,
                false
            )

        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MarkViewHolder,
        position: Int
    ) {

        val mark = marks[position]

        holder.titleText.text =
            "${mark.id}: ${mark.title}"

        holder.descriptionText.text =
            mark.desc

        holder.coordinatesText.text =
            "Lat: ${mark.x}, Lng: ${mark.y}"

        val imageUri = mark.imageUri

        if (imageUri != null) {
            holder.markImage.setImageURI(Uri.parse(imageUri))
        } else {
            holder.markImage.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        holder.itemView.setOnClickListener {
            Timber.d("Item clicked id=${mark.id}")
            listener.onMarkClick(mark)
        }

        holder.itemView.setOnLongClickListener {
            Timber.d("Item long-clicked id=${mark.id}")
            listener.onMarkLongClick(mark)
        }
    }

    override fun getItemCount(): Int {
        return marks.size
    }

    fun updateMarks(newMarks: List<PlacedMarkData>) {
        marks = newMarks
        notifyDataSetChanged()
    }
}
