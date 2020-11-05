package com.sanlin.parkeasy.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sanlin.parkeasy.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_slider_item.view.*

class ImageSliderAdapter(private val context: Context,private val imageList:List<String>):PagerAdapter() {

    private lateinit var layoutInflater: LayoutInflater


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.image_slider_item, null)
        Picasso.get().load(imageList.get(position)).into(view.sliderImage)
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

}