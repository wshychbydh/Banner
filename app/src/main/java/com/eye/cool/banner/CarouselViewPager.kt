package com.eye.cool.banner

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
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
  : View.OnTouchListener, Runnable, LifecycleObserver {

  constructor(viewPager: ViewPager) : this(viewPager, null)

  private var params: CarouselParams

  private lateinit var scroller: PageScroller

  init {
    if (viewPager.adapter !is ICarousel) {
      throw IllegalArgumentException("ViewPager's adapter must be implement ICarousel.")
    }
    val adapter = (viewPager.adapter as ICarousel)
    params = configParams ?: adapter.params
    adapter.params = params
    getLifecycle(viewPager.context)?.addObserver(this)
    registerParamsObserver()
    configViewPagerWithParams()
  }

  private fun getLifecycle(context: Context): Lifecycle? {
    if (context is AppCompatActivity) {
      return context.lifecycle
    }
    return null
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun configViewPagerWithParams() {
    viewPager.currentItem = (viewPager.adapter as ICarousel).getInitItem()
    viewPager.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
    viewPager.setOnTouchListener(this)
    scroller = PageScroller(viewPager.context, params.scrollDuration, params.interpolator)
    setPageChangeDuration(scroller)
  }

  private fun registerParamsObserver() {
    params.registerParamsObserver(object : CarouselParams.ParamsObserver {
      override fun onParamsChanged() {
        onStop()
        configViewPagerWithParams()
        onStart()
      }
    })
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
        if (System.nanoTime() - touchWhen < 1000) {
          v?.performClick()
        }
      }
      else -> {
        if (event.action == MotionEvent.ACTION_DOWN) {
          touchWhen = System.nanoTime()
        }
        scroller.isDrag = true
        if (params.pauseWhenTouch) {
          onStop()
        }
      }
    }
    return false
  }

  /**
   * 如果viewPager.context为AppCompatActivity，会自动跟随activity的onStart调用
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onStart() {
    if (params.carouselAble && viewPager.adapter?.count ?: 0 > 1) {
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
}