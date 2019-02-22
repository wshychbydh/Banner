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
  internal var scrollDuration = 1000 //viewpager 切换速度
  internal var direction = LEFT //轮播方向
  internal var carouselAble = true // 是否支持轮播
  internal var autoCarousel = true // 自动轮播
  internal var scrollWhenOne = false // 只有一张图时仍然滚动
  internal var interpolator: Interpolator? = null //插入器
  internal var scrollAble = true  //是否可以手动滑动

  class Builder {
    private val params = CarouselParams()

    /**
     * 切换间隔时间，默认5s,单位毫秒
     */
    fun setInterval(millisecond: Long): Builder {
      params.interval = millisecond
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
     * 单张图片轮播的时长,默认1s,单位毫秒
     */
    fun setScrollDuration(millisecond: Int): Builder {
      params.scrollDuration = millisecond
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
     * 只有一张图时仍然滚动,默认false
     */
    fun setScrollWhenOne(scrollWhenOne: Boolean): Builder {
      params.scrollWhenOne = scrollWhenOne
      return this
    }

    /**
     * 是否支持手动滑动，默认true(慎用)。设置为false后子view不会响应触摸事件
     * 但是viewPager仍然可以响应onClick(400ms)事情
     */
    fun setScrollAble(scrollAble: Boolean): Builder {
      params.scrollAble = scrollAble
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

    fun build(): CarouselParams {
      return params
    }
  }

  companion object {
    const val LEFT = 0  //向左轮播
    const val RIGHT = 1  //向右轮播
  }
}