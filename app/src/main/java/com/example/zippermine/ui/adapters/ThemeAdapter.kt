package com.example.zippermine.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zippermine.R
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.data.interfaces.ThemeSelected
import com.preference.PowerPreference

class ThemeAdapter(
    val context: Context,
    private val data: List<Int>,
    private val themeSelected: ThemeSelected
) :
    RecyclerView.Adapter<ThemeAdapter.MyViewholder>() {

    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doorImg = itemView.findViewById<ImageView>(R.id.door_img)
        val tick = itemView.findViewById<RelativeLayout>(R.id.tick)
        val bg = itemView.findViewById<FrameLayout>(R.id.bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.gold_theme_item, parent, false)
        return MyViewholder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        Glide.with(context).load(data[position]).into(holder.doorImg)

        if (position == PowerPreference.getDefaultFile().getInt(HeartPrefConst.SetTheme, 0)) {
            holder.bg.visibility = View.GONE
            holder.tick.visibility = View.VISIBLE
        } else {
            holder.tick.visibility = View.GONE
            holder.bg.visibility = View.VISIBLE
        }

        holder.doorImg.setOnClickListener {
            themeSelected.onThemeSelected(position)
        }
    }
}
