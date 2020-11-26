package com.example.photoeditorrss.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditorrss.Interface.ItemClickListener
import com.example.photoeditorrss.Model.Root
import com.example.photoeditorrss.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.gpu.InvertFilterTransformation
import jp.wasabeef.picasso.transformations.gpu.SepiaFilterTransformation
import jp.wasabeef.picasso.transformations.gpu.SketchFilterTransformation


@Suppress("DEPRECATION")
class FeedViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener,View.OnLongClickListener{

    var txtTitle: TextView
    var image: ImageView

    private var itemClickListener: ItemClickListener?=null

    init{
        txtTitle = itemView.findViewById(R.id.txtTitle) as TextView
        image = itemView.findViewById(R.id.img) as ImageView

        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun setItemClickListener (itemClickListener: ItemClickListener)
    {
        this.itemClickListener = itemClickListener
    }
    override fun onClick(v: View?) {
        itemClickListener!!.onClick(v, adapterPosition,false)
    }

    override fun onLongClick(v: View?): Boolean {
        itemClickListener!!.onClick(v, adapterPosition,true)
        return true
    }

}

class FeedAdapter(private val root: Root, private val mContext: Context, private val type: String): RecyclerView.Adapter<FeedViewHolder>()
{
    private val inflater: LayoutInflater

    init{
        inflater = LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {

        //add info to item (row)
        holder.txtTitle.text = root.items[position].title
        when(type){
            "normal" -> normal(holder,position)
            "sepia" -> sepia(holder,position)
            "inversion" -> inversion(holder,position)
            "sketch" -> sketch(holder, position)
        }
        //add ability to go at item's link
        holder.setItemClickListener(ItemClickListener{ view, position, isLongClick ->
            if(!isLongClick){
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(root.items[position].link))
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContext.startActivity(browserIntent)
            }
        })
    }

    override fun getItemCount(): Int {
        return root.items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView = inflater.inflate(R.layout.row, parent, false)
        return FeedViewHolder(itemView)
    }

    //functions to add filters
    fun normal(holder: FeedViewHolder, position: Int){
        val link = search(position)
        if (link != null) Picasso.get()
            .load(link)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_add_photo)
            .fit().centerCrop()
            .into(holder.image)

        else Picasso.get()
            .load(R.drawable.ic_add_photo)
            .placeholder(R.drawable.ic_add_photo)
            .resize(50,50)
            .into(holder.image)

    }
    fun sepia(holder: FeedViewHolder, position: Int){
        val link = search(position)
        if (link != null) {
            Picasso.get()
                .load(link)
                .placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_add_photo)
                .transform(SepiaFilterTransformation(mContext))
                .fit().centerCrop()
                .into(holder.image)
        }
        else {Picasso.get()
            .load(R.drawable.ic_add_photo)
            .placeholder(R.drawable.ic_add_photo)
            .resize(50,50)
            .into(holder.image)
        }
    }
    fun inversion(holder: FeedViewHolder, position: Int) {
        val link = search(position)
        if (link != null) {
            Picasso.get()
                .load(link)
                .placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_add_photo)
                .transform(InvertFilterTransformation(mContext))
                .fit().centerCrop()
                .into(holder.image)
        } else {
            Picasso.get()
                .load(R.drawable.ic_add_photo)
                .placeholder(R.drawable.ic_add_photo)
                .resize(50,50)
                .into(holder.image)
        }
    }
    fun sketch(holder: FeedViewHolder, position: Int) {
        val link = search(position)
        if (link != null) {
            Picasso.get()
                .load(link)
                .placeholder(R.drawable.ic_downloading)
                .error(R.drawable.ic_add_photo)
                .transform(SketchFilterTransformation(mContext))
                .fit().centerCrop()
                .into(holder.image)
        } else {
            Picasso.get()
                .load(R.drawable.ic_add_photo)
                .placeholder(R.drawable.ic_add_photo)
                .resize(50,50)
                .into(holder.image)
        }
    }

    //additional functions to find path of images
    fun search(position: Int): String?{
        val pic_type: Array<String> = arrayOf(".jpg", ".png", ".jpeg")
        val link: String = findPicture(root.items[position].description, pic_type)
        if (link!="") return link
        else return root.items[position].enclosure.link
    }
    fun findPicture(string: String, under_string: Array<String>): String{
        var type = ""
        for(x in 0..under_string.size-1) if (string.contains(under_string[x])) {
            type = string.substring(0,string.lastIndexOf(under_string[x])+under_string[x].length)
            type = type.drop(type.lastIndexOf("http"))
            break
        }
        return type
    }
}