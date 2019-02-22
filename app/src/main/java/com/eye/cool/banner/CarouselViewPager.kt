package com.eye.cool.banner

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.database.DataSetObserver
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller


/**
 * Created by cool on 2018/4/18.
 */
class CarouselViewPager constructor(private val viewPager: ViewPager,
                                    configParams: CarouselParams? = null)
  : View.OnTouchListener, Runnable, LifecycleObserver, DataSetObserver() {

  constructor(viewPager: ViewPager) : this(viewPager, null)

  @Volatile
  private var params: CarouselParams

  @Volatile
  private lateinit var scroller: PageScroller

  init {
    checkAdapter()
    val adapter = (viewPager.adapter as ICarousel)
    params = configParams ?: adapter.params
    adapter.params = params
    viewPager.adapter!!.registerDataSetObserver(this)
    viewPager.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
    viewPager.setOnTouchListener(this)
    configViewPagerWithParams()
    viewPager.currentItem = (viewPager.adapter as ICarousel).getInitItem()
  }

  private fun getLifecycle(context: Context): Lifecycle? {
    if (context is AppCompatActivity) {
      return context.lifecycle
    }
    return null
  }

  override fun onChanged() {
    checkAdapter()
    params = (viewPager.adapter as ICarousel).params
    viewPager.adapter!!.unregisterDataSetObserver(this)
    onStop()
    configViewPagerWithParams()
    onStart()
    viewPager.adapter!!.registerDataSetObserver(this)
  }

  private fun configViewPagerWithParams() {
    scroller = PageScroller(viewPager.context, params.scrollDuration, params.interpolator)
    setPageChangeDuration(scroller)
    val lifecycle = getLifecycle(viewPager.context)
    lifecycle?.removeObserver(this)
    if (params.carouselAble && params.autoCarousel) {
      lifecycle?.addObserver(this)
    }
  }

  override fun run() {
    viewPager.currentItem = (viewPager.adapter as ICarousel).getNextPage(viewPager.currentItem)
    viewPager.postDelayed(this, params.interval)
  }

  /**
   * switching period
   */
  private fun setPageChangeDuration(scroller: Scroller) {
    try {
      val scrollerField = ViewPager::class.java.getDeclaredField("mScroller")
      scrollerField.isAccessible = true
      scrollerField.set(viewPager, scroller)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private var touchWhen = 0L

  override fun onTouch(v: View?, event: MotionEvent): Boolean {
    when (event.action) {
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        if (params.pauseWhenTouch) {
          onStart()
        }
        if (event.action == MotionEvent.ACTION_UP) {
          if (v?.isClickable == true) {
            val duration = System.currentTimeMillis() - touchWhen
            if (duration < 400L) {
              v.performClick()
            }
          }
        }
      }
      else -> {
        if (event.action == MotionEvent.ACTION_DOWN) {
          touchWhen = System.currentTimeMillis()
        }
        scroller.isDrag = true
        if (params.pauseWhenTouch) {
          onStop()
        }
      }
    }
    return !params.scrollAble
  }

  /**
   * 如果viewPager.context为AppCompatActivity，会自动跟随activity的onStart调用
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onStart() {
    if (params.carouselAble) {
      checkAdapter()
      val count = (viewPager.adapter as ICarousel).getDataSize()
      if (count <= 0 || (count == 1 && !params.scrollWhenOne)) return
      viewPager.removeCallbacks(this)
      viewPager.postDelayed(this, params.interval)
    }
  }

  /**
   * 如果viewPager.context为AppCompatActivity，会自动跟随activity的onStop调用
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  fun onStop() {
    viewPager.removeCallbacks(this)
  }

  private fun checkAdapter() {
    if (viewPager.adapter == null) {
      throw IllegalArgumentException("ViewPager's adapter can not be null.")
    }
    if (viewPager.adapter !is ICarousel) {
      throw IllegalArgumentException("ViewPager's adapter must be implement ICarousel.")
    }
  }
}