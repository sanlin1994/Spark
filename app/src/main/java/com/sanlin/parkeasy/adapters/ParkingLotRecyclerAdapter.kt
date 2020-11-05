package com.sanlin.parkeasy.adapters

import android.content.Context
import android.content.res.Resources
import android.media.Image
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.ImagesContract
import com.sanlin.parkeasy.R

class ParkingLotRecyclerAdapter(private val spaceList: List<String>, private val context: Context): RecyclerView.Adapter<ParkingLotRecyclerAdapter.ProductViewHolder>() {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var spaceNumber:TextView
        var spaceStatus:ImageView

        init {
            spaceNumber = itemView.findViewById(R.id.parkingItemText)
            spaceStatus = itemView.findViewById(R.id.parkingItemBackground)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val v =  LayoutInflater.from(context).inflate(R.layout.parking_space_item, parent, false)
        return ProductViewHolder(v)
    }

    override fun getItemCount(): Int {
        return spaceList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val spaceStatus = spaceList.get(position)
        holder.spaceNumber.setText((position+1).toString())
        when(spaceStatus){
            "01" ->{
                    holder.spaceStatus.setImageResource(R.drawable.ic_car_blue)
                    holder.spaceNumber.visibility = View.INVISIBLE
            }
            "00" -> {holder.spaceStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.green_background))
                    holder.spaceNumber.visibility = View.VISIBLE
            }
            "011" -> {holder.spaceStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.orange_background))
                    holder.spaceNumber.visibility = View.VISIBLE
            }
        }
    }

}