package com.eye.cool.banner

import android.arch.lifecycle.Lifecycle
import android.view.animation.Interpolator

/**
 *Created by ycb on 2019/2/20 0020
 */
class CarouselParams private constructor() {
  internal var interval: Long = 5000L
  internal var reversible = false
  internal var recyclable = true
  internal var pauseWhenTouch = true
  internal var scrollDuration: Int? = null
  internal var direction = LEFT
  internal var carouselAble = true
  internal var autoCarousel = true
  internal var scrollWhenOne = false
  internal var interpolator: Interpolator? = null
  internal var scrollAble = true
  internal var attachLifecycle: Lifecycle? = null
  internal var cacheBoundary = false
  internal var indicator: IIndicator? = null

  class Builder {
    private val params = CarouselParams()

    /**
     * Switching interval time, default 5s. unit(ms)
     */
    fun setInterval(millisecond: Long): Builder {
      params.interval = millisecond
      return this
    }

    /**
     * Reverse rotation, default false
     */
    fun setReversible(reversible: Boolean): Builder {
      params.reversible = reversible
      return this
    }

    /**
     * Loop round robin, default true
     */
    fun setRecyclable(recyclable: Boolean): Builder {
      params.recyclable = recyclable
      return this
    }

    /**
     * Stops the round when pressed, default true
     */
    fun setPauseWhenTouch(pauseWhenTouch: Boolean): Builder {
      params.pauseWhenTouch = pauseWhenTouch
      return this
    }

    /**
     * The rotation time of single picture is 1s by default. unit(milliseconds)
     */
    fun setScrollDuration(millisecond: Int): Builder {
      params.scrollDuration = millisecond
      return this
    }

    /**
     * Rotation direction can only be {LEFT,RIGHT}, default RIGHT
     */
    fun setDirection(direction: Int): Builder {
      params.direction = direction
      return this
    }

    /**
     * Scroll with only one image, default false
     */
    fun setScrollWhenOne(scrollWhenOne: Boolean): Builder {
      params.scrollWhenOne = scrollWhenOne
      return this
    }

    /**
     * Support to manual sliding, default true
     */
    fun setScrollAble(scrollAble: Boolean): Builder {
      params.scrollAble = scrollAble
      return this
    }

    /**
     * Support to round, default true。If set false, it will never auto-rotation.
     */
    fun enableCarousel(enable: Boolean): Builder {
      params.carouselAble = enable
      return this
    }

    /**
     * Auto start/end round, default true
     */
    fun setAutoCarousel(autoCarousel: Boolean): Builder {
      params.autoCarousel = autoCarousel
      return this
    }

    /**
     * Scroller's interpolator for ViewPager
     */
    fun setInterpolator(interpolator: Interpolator): Builder {
      params.interpolator = interpolator
      return this
    }

    /**
     * Automatically rounds by Lifecycle with OnResume-onPause
     */
    fun setAttachLifecycle(lifecycle: Lifecycle): Builder {
      params.attachLifecycle = lifecycle
      return this
    }

    /**
     * If set to true, the boundary views (i.e. first and last) will never be destroyed
     * This may help to prevent "blinking" of some views
     *
     * Cache the bounds View to prevent blinking, defaults false
     */
    fun setCacheBoundary(cacheBoundary: Boolean): Builder {
      params.cacheBoundary = cacheBoundary
      return this
    }

    /**
     * Indicator which conjunction with Viewpager
     */
    fun setIndicator(indicator: IIndicator): Builder {
      params.indicator = indicator
      return this
    }

    fun build(): CarouselParams {
      return params
    }
  }

  companion object {
    const val LEFT = 0
    const val RIGHT = 1
  }
}