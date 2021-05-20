package com.eye.cool.banner

import android.view.animation.Interpolator
import androidx.lifecycle.Lifecycle

/**
 *Created by ycb on 2019/2/20 0020
 */
class CarouselParams private constructor(
    internal val interval: Long,
    internal val reversible: Boolean,
    internal val recyclable: Boolean,
    internal val pauseWhenTouch: Boolean,
    internal val scrollDuration: Int?,
    internal var direction: Int,
    internal val carouselAble: Boolean,
    internal val autoCarousel: Boolean,
    internal val scrollWhenOne: Boolean,
    internal val interpolator: Interpolator?,
    internal val scrollAble: Boolean,
    internal val attachLifecycle: Lifecycle?,
    internal val cacheBoundary: Boolean,
    internal val indicator: IIndicator?
) {

  companion object {
    const val RIGHT_TO_LEFT = 0
    const val LEFT_TO_RIGHT = 1
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  data class Builder(
      var interval: Long = 5000L,
      var reversible: Boolean = false,
      var recyclable: Boolean = true,
      var pauseWhenTouch: Boolean = true,
      var scrollDuration: Int? = null,
      var direction: Int = RIGHT_TO_LEFT,
      var carouselAble: Boolean = true,
      var autoCarousel: Boolean = true,
      var scrollWhenOne: Boolean = false,
      var interpolator: Interpolator? = null,
      var scrollAble: Boolean = true,
      var attachLifecycle: Lifecycle? = null,
      var cacheBoundary: Boolean = false,
      var indicator: IIndicator? = null
  ) {
    /**
     * Switching interval time
     * @param [millisecond] default 5s. unit(ms)
     */
    fun interval(millisecond: Long) = apply { this.interval = millisecond }

    /**
     * Reverse rotation
     * @param [reversible] default false
     */
    fun reversible(reversible: Boolean) = apply { this.reversible = reversible }

    /**
     * Loop round robin
     * @param [recyclable] default true
     */
    fun recyclable(recyclable: Boolean) = apply { this.recyclable = recyclable }

    /**
     * Stops the round when pressed
     * @param [pauseWhenTouch] default true
     */
    fun pauseWhenTouch(pauseWhenTouch: Boolean) = apply { this.pauseWhenTouch = pauseWhenTouch }

    /**
     * The scrolling time of single picture.
     * @param [millisecond] default 1s unit(milliseconds)
     */
    fun scrollDuration(millisecond: Int) = apply { this.scrollDuration = millisecond }

    /**
     * Scroll direction can only be {@link LEFT_TO_RIGHT, @link RIGHT_TO_LEFT}
     * @param [direction] default RIGHT_TO_LEFT, It's equivalent to swiping to the left
     */
    fun direction(direction: Int) = apply { this.direction = direction }

    /**
     * Scroll with only one image
     * @param [scrollWhenOne] default false
     */
    fun scrollWhenOne(scrollWhenOne: Boolean) = apply { this.scrollWhenOne = scrollWhenOne }

    /**
     * Support to manual sliding
     * @param [scrollAble] default true
     */
    fun scrollAble(scrollAble: Boolean) = apply { this.scrollAble = scrollAble }

    /**
     * Support to round. If set false, it will never auto-rotation.
     * @param [enable] default true
     */
    fun enableCarousel(enable: Boolean) = apply { this.carouselAble = enable }

    /**
     * Auto start/end round
     * @param [autoCarousel] default true
     */
    fun autoCarousel(autoCarousel: Boolean) = apply { this.autoCarousel = autoCarousel }

    /**
     * Scroller's interpolator for ViewPager
     * @param [interpolator]
     */
    fun interpolator(interpolator: Interpolator) = apply { this.interpolator = interpolator }

    /**
     * Automatically rounds by Lifecycle with OnResume-onPause
     * @param [lifecycle]
     */
    fun attachLifecycle(lifecycle: Lifecycle) = apply { this.attachLifecycle = lifecycle }

    /**
     * If set to true, the boundary views (i.e. first and last) will never be destroyed
     * This may help to prevent "blinking" of some views
     *
     * @param [cacheBoundary] Cache the bounds View to prevent blinking, defaults false
     */
    fun cacheBoundary(cacheBoundary: Boolean) = apply { this.cacheBoundary = cacheBoundary }

    /**
     * Indicator which conjunction with Viewpager
     * @param [indicator]
     */
    fun indicator(indicator: IIndicator) = apply { this.indicator = indicator }

    fun build() = CarouselParams(
        interval = interval,
        reversible = reversible,
        recyclable = recyclable,
        pauseWhenTouch = pauseWhenTouch,
        scrollDuration = scrollDuration,
        direction = direction,
        carouselAble = carouselAble,
        autoCarousel = autoCarousel,
        scrollWhenOne = scrollWhenOne,
        interpolator = interpolator,
        scrollAble = scrollAble,
        attachLifecycle = attachLifecycle,
        cacheBoundary = cacheBoundary,
        indicator = indicator
    )
  }
}