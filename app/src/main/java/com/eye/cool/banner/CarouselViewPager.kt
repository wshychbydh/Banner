package com.eye.cool.banner

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.database.DataSetObserver
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Scroller


/**
 * Created by cool on 2018/4/18.
 */
class CarouselViewPager(context: Context, attrs: AttributeSet?)
  : LoopViewPager(context, attrs), Runnable, LifecycleObserver, ICarousel {

  private var scroller: PageScroller? = null

  override fun setAdapter(adapter: PagerAdapter?) {
    super.setAdapter(adapter)
    tryCarousel()
    adapter?.registerDataSetObserver(dataObserver)
  }

  private fun tryCarousel() {
    onStop()
    //Prevent view from being unable to be brought back when parameters change during scrolling
    postDelayed({
      configViewPagerWithParams()
      onStart()
    }, (scroller?.duration ?: 0).toLong())
  }

  private fun configViewPagerWithParams() {
    setIndicator(params.indicator)
    setBoundaryCaching(params.cacheBoundary)
    if (params.scrollDuration != null) {
      scroller = PageScroller(context, params.scrollDuration!!, params.interpolator)
      setPageChangeDuration(scroller!!)
    }
    val lifecycle = params.attachLifecycle
    lifecycle?.removeObserver(this)
    if (params.carouselAble && params.autoCarousel) {
      lifecycle?.addObserver(this)
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    when (ev?.action) {
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        if (params.pauseWhenTouch) {
          onStart()
        }
      }
      MotionEvent.ACTION_DOWN -> {
        scroller?.isDrag = true
        if (params.pauseWhenTouch) {
          onStop()
        }
      }
    }
    return super.dispatchTouchEvent(ev)
  }

  override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
    return if (params.scrollAble) {
      super.onInterceptTouchEvent(ev)
    } else false
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(ev: MotionEvent?): Boolean {
    return if (params.scrollAble) {
      super.onTouchEvent(ev)
    } else true
  }

  override fun run() {
    currentItem = getNextPage(currentItem)
    postDelayed(this, params.interval)
  }

  /**
   * switching period
   */
  private fun setPageChangeDuration(scroller: Scroller) {
    try {
      val scrollerField = ViewPager::class.java.getDeclaredField("mScroller")
      scrollerField.isAccessible = true
      scrollerField.set(this, scroller)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * Follow attachLifecycle's onStart
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  private fun onStart() {
    if (params.autoCarousel) {
      val count = adapter?.count ?: 0
      if (count <= 0 || (count == 1 && !params.scrollWhenOne)) return
      removeCallbacks(this)
      postDelayed(this, params.interval)
    }
  }

  /**
   * Call
   */
  fun start() {
    if (params.carouselAble) {
      onStart()
    }
  }

  fun stop() {
    onStop()
  }

  private fun restart() {
    stop()
    start()
  }

  /**
   * Follow attachLifecycle's onStop
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onStop() {
    removeCallbacks(this)
  }

  override fun getCount(): Int {
    return adapter?.count ?: 0
  }

  override var params: CarouselParams = CarouselParams.Builder().build()
    set(value) {
      field = value
      tryCarousel()
    }

  private val dataObserver = object : DataSetObserver() {
    override fun onChanged() {
      restart()
    }
  }
}