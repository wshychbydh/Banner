package com.eye.cool.banner

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by cool on 2018/4/18.
 */
abstract class CarouselPagerAdapter<T> : PagerAdapter(), ICarouselData<T> {

  override var data: ArrayList<T> = ArrayList()

  override var params: CarouselParams = CarouselParams.Builder().build()

  fun setData(data: T) {
    this.data.clear()
    if (data != null) {
      this.data.add(data)
    }
    notifyDataSetChanged()
  }

  fun setData(data: Array<T>) {
    this.data.clear()
    if (data.isNotEmpty()) {
      this.data.addAll(data)
    }
    notifyDataSetChanged()
  }

  fun appendData(data: T) {
    if (data != null) {
      this.data.add(data)
      notifyDataSetChanged()
    }
  }

  fun setData(data: List<T>) {
    this.data.clear()
    if (data.isNotEmpty()) {
      this.data.addAll(data)
    }
    notifyDataSetChanged()
  }

  fun appendData(data: Array<T>) {
    if (data.isNotEmpty()) {
      this.data.addAll(data)
      notifyDataSetChanged()
    }
  }

  override fun isViewFromObject(view: View, `object`: Any): Boolean {
    return view === `object`
  }

  override fun destroyItem(view: ViewGroup, position: Int, `object`: Any) {
    view.removeView(`object` as View)
  }

  override fun instantiateItem(view: ViewGroup, position: Int): Any {
    return loadItemView(view, position)
  }

  abstract fun loadItemView(view: ViewGroup, position: Int): View
}