package com.eye.cool.banner

import android.view.animation.Interpolator

/**
 *Created by ycb on 2019/2/20 0020
 */
class CarouselParams private constructor() {
  internal var interval: Long = 5000L //切换间隔
  internal var reversible = false  //轮播到最后一张后反向滑动
  internal var recyclable = true   //循环
  internal var pauseWhenTouch = true  //按下时不滑动
  internal var canFlashback = true  //在第一个位置时是否也可反向滑动(保留)
  internal var scrollDuration = 1000 //viewpager 切换速度
  internal var direction = RIGHT //轮播方向
  internal var carouselAble = true // 是否支持轮播
  internal var autoCarousel = true // 自动轮播
  internal var interpolator: Interpolator? = null //插入器

  private val observers = mutableListOf<ParamsObserver>()

  class Builder {
    private val params = CarouselParams()

    /**
     * 切换间隔时间，默认5s
     */
    fun setInterval(interval: Long): Builder {
      params.interval = interval
      return this
    }

    /**
     * 反转轮播，默认false
     */
    fun setReversible(reversible: Boolean): Builder {
      params.reversible = reversible
      return this
    }

    /**
     * 循环轮播，默认true
     */
    fun setRecyclable(recyclable: Boolean): Builder {
      params.recyclable = recyclable
      return this
    }

    /**
     * 当手指按下时停止轮播，默认true
     */
    fun setPauseWhenTouch(pauseWhenTouch: Boolean): Builder {
      params.pauseWhenTouch = pauseWhenTouch
      return this
    }

    /**
     * 单张图片轮播的时长,默认1s
     */
    fun setScrollDuration(scrollDuration: Int): Builder {
      params.scrollDuration = scrollDuration
      return this
    }

    /**
     * 轮播的方向 只能是{LEFT,RIGHT}，默认RIGHT
     */
    fun setDirection(direction: Int): Builder {
      params.direction = direction
      return this
    }

    /**
     * 是否支持轮播,默认true
     */
    fun enableCarousel(enable: Boolean): Builder {
      params.carouselAble = enable
      return this
    }

    /**
     * 自动开始/结束轮播，默认true
     * 注：ViewPager.context必须为AppCompatActivity实例时方可生效
     */
    fun setAutoCarousel(autoCarousel: Boolean): Builder {
      params.autoCarousel = autoCarousel
      return this
    }

    /**
     * ViewPager的scroller插值器
     */
    fun setInterpolator(interpolator: Interpolator): Builder {
      params.interpolator = interpolator
      return this
    }

    /**
     * 暂保留
     * 参数变动回调，需手动调用onParamsChanged后才会执行
     */
    private fun registerParamsObserver(observer: ParamsObserver): Builder {
      if (!params.observers.contains(observer)) {
        params.observers.add(observer)
      }
      return this
    }

    fun build(): CarouselParams {
      return params
    }
  }

  /**
   * 参数变动回调，需手动调用onParamsChanged后才会执行
   */
  internal fun registerParamsObserver(observer: ParamsObserver) {
    if (!observers.contains(observer)) {
      observers.add(observer)
    }
  }

  fun onParamsChanged() {
    observers.forEach {
      it.onParamsChanged()
    }
  }

  companion object {
    const val LEFT = 0  //向左轮播
    const val RIGHT = 1  //向右轮播
  }

  interface ParamsObserver {
    fun onParamsChanged()
  }
}