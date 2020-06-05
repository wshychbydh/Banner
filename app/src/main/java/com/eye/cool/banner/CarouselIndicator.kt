package com.eye.cool.banner

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.AnimatorRes
import androidx.annotation.DrawableRes

/**
 * Created by cool on 2018/4/18.
 * More see https://github.com/JakeWharton/ViewPagerIndicator
 */
class CarouselIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), IIndicator {
  private var indicatorMargin = 0
  private var indicatorWidth = 0
  private var indicatorHeight = 0
  private var animatorResId = R.animator.banner_scale_with_alpha
  private var animatorReverseResId = 0
  private var indicatorBackgroundResId = R.drawable.banner_white_radius
  private var indicatorUnselectedBackgroundResId = R.drawable.banner_white_radius
  private var animatorOut: Animator? = null
  private var animatorIn: Animator? = null
  private var immediateAnimatorOut: Animator? = null
  private var immediateAnimatorIn: Animator? = null
  private var placeholder: View? = null
  private var alwaysShownWhenOne = true

  private var lastPosition = -1

  init {
    init(context, attrs)
  }

  fun configureIndicator(
      indicatorWidth: Int = 0,
      indicatorHeight: Int = 0,
      indicatorMargin: Int = 0,
      @AnimatorRes animatorId: Int = R.animator.banner_scale_with_alpha,
      @AnimatorRes animatorReverseId: Int = 0,
      @DrawableRes indicatorBackgroundId: Int = R.drawable.banner_white_radius,
      @DrawableRes indicatorUnselectedBackgroundId: Int = R.drawable.banner_white_radius
  ) {

    this.indicatorWidth = indicatorWidth
    this.indicatorHeight = indicatorHeight
    this.indicatorMargin = indicatorMargin

    animatorResId = animatorId
    animatorReverseResId = animatorReverseId
    indicatorBackgroundResId = indicatorBackgroundId
    indicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId

    checkIndicatorConfig(context)
  }

  private fun init(context: Context, attrs: AttributeSet?) {
    handleTypedArray(context, attrs)
    checkIndicatorConfig(context)
  }

  private fun handleTypedArray(context: Context, attrs: AttributeSet?) {
    if (attrs == null) {
      return
    }

    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.banner_carousel_indicator)
    indicatorWidth = typedArray.getDimensionPixelSize(R.styleable.banner_carousel_indicator_banner_indicator_width, 0)
    indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.banner_carousel_indicator_banner_indicator_height, 0)
    indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.banner_carousel_indicator_banner_indicator_margin, 0)

    animatorResId = typedArray.getResourceId(R.styleable.banner_carousel_indicator_banner_indicator_animator, R.animator.banner_scale_with_alpha)
    animatorReverseResId = typedArray.getResourceId(R.styleable.banner_carousel_indicator_banner_indicator_animator_reverse, 0)
    indicatorBackgroundResId = typedArray.getResourceId(R.styleable.banner_carousel_indicator_banner_indicator_drawable, R.drawable.banner_white_radius)
    indicatorUnselectedBackgroundResId = typedArray.getResourceId(R.styleable.banner_carousel_indicator_banner_indicator_drawable_unselected, indicatorBackgroundResId)

    val orientation = typedArray.getInt(R.styleable.banner_carousel_indicator_banner_indicator_orientation, HORIZONTAL)
    setOrientation(if (orientation == VERTICAL) VERTICAL else HORIZONTAL)

    val gravity = typedArray.getInt(R.styleable.banner_carousel_indicator_banner_indicator_gravity, Gravity.CENTER)
    setGravity(if (gravity >= 0) gravity else Gravity.CENTER)

    alwaysShownWhenOne = typedArray.getBoolean(R.styleable.banner_carousel_indicator_banner_indicator_alwaysShownWhenOne, true)
    typedArray.recycle()
  }

  private fun checkIndicatorConfig(context: Context) {
    indicatorWidth = if (indicatorWidth <= 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else indicatorWidth
    indicatorHeight = if (indicatorHeight <= 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else indicatorHeight
    indicatorMargin = if (indicatorMargin <= 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else indicatorMargin

    animatorResId = if (animatorResId == 0) R.animator.banner_scale_with_alpha else animatorResId

    animatorOut = createAnimatorOut(context)
    immediateAnimatorOut = createAnimatorOut(context)
    immediateAnimatorOut!!.duration = 0

    animatorIn = createAnimatorIn(context)
    immediateAnimatorIn = createAnimatorIn(context)
    immediateAnimatorIn!!.duration = 0

    indicatorBackgroundResId = if (indicatorBackgroundResId == 0) R.drawable.banner_white_radius else indicatorBackgroundResId
    indicatorUnselectedBackgroundResId = if (indicatorUnselectedBackgroundResId == 0) indicatorBackgroundResId else indicatorUnselectedBackgroundResId
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val minHeight = Math.max(indicatorHeight * 2, measuredHeight)
    setMeasuredDimension(measuredWidth, minHeight)
  }

  /**
   * When the last item is selected, the placeholder will displayed
   */
  fun placeholder(placeholder: View): CarouselIndicator {
    this.placeholder = placeholder
    return this
  }

  /**
   * Show indicator even if only one item
   */
  fun alwaysShownWhenOnlyOne(alwaysShownWhenOne: Boolean): CarouselIndicator {
    this.alwaysShownWhenOne = alwaysShownWhenOne
    return this
  }

  private fun createAnimatorOut(context: Context): Animator {
    return AnimatorInflater.loadAnimator(context, animatorResId)
  }

  private fun createAnimatorIn(context: Context): Animator {
    val animatorIn: Animator
    if (animatorReverseResId == 0) {
      animatorIn = AnimatorInflater.loadAnimator(context, animatorResId)
      animatorIn.interpolator = ReverseInterpolator()
    } else {
      animatorIn = AnimatorInflater.loadAnimator(context, animatorReverseResId)
    }
    return animatorIn
  }

  private fun createIndicators(currentItem: Int, indicatorCount: Int) {
    val orientation = orientation
    for (i in 0 until indicatorCount) {
      if (currentItem == i) {
        addIndicator(orientation, indicatorBackgroundResId, immediateAnimatorOut)
      } else {
        addIndicator(orientation, indicatorUnselectedBackgroundResId, immediateAnimatorIn)
      }
    }
  }

  private fun addIndicator(orientation: Int, @DrawableRes backgroundDrawableId: Int,
                           animator: Animator?) {
    if (animator!!.isRunning) {
      animator.end()
      animator.cancel()
    }

    val indicatorView = View(context)
    indicatorView.setBackgroundResource(backgroundDrawableId)
    addView(indicatorView, indicatorWidth, indicatorHeight)
    val lp = indicatorView.layoutParams as LinearLayout.LayoutParams

    if (orientation == HORIZONTAL) {
      lp.leftMargin = indicatorMargin
      lp.rightMargin = indicatorMargin
    } else {
      lp.topMargin = indicatorMargin
      lp.bottomMargin = indicatorMargin
    }

    indicatorView.layoutParams = lp

    animator.setTarget(indicatorView)
    animator.start()
  }

  private inner class ReverseInterpolator : Interpolator {
    override fun getInterpolation(value: Float): Float {
      return Math.abs(1.0f - value)
    }
  }

  private fun dip2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
  }

  override fun onDataChanged(currentPosition: Int, count: Int) {
    removeAllViews()
    visibility = View.GONE
    if (count > 0 && (alwaysShownWhenOne || count > 1)) {
      val currentCount = childCount
      val currentItem = currentPosition % count
      lastPosition = when {
        count == currentCount -> return
        lastPosition < count -> currentPosition
        else -> -1
      }
      createIndicators(currentItem, count)
      visibility = View.VISIBLE
    }
  }

  override fun onPageSelected(position: Int) {
    if (childCount == 0) return

    val indicatorPosition = position % childCount

    if (lastPosition == indicatorPosition) return

    if (placeholder != null && indicatorPosition == childCount - 1) {
      placeholder!!.visibility = View.VISIBLE
      visibility = View.GONE
    } else {
      placeholder?.visibility = View.GONE
      visibility = View.VISIBLE
    }

    if (animatorIn!!.isRunning) {
      animatorIn!!.end()
      animatorIn!!.cancel()
    }

    if (animatorOut!!.isRunning) {
      animatorOut!!.end()
      animatorOut!!.cancel()
    }

    if (lastPosition >= 0 && getChildAt(lastPosition) != null) {
      val currentIndicator = getChildAt(lastPosition)
      currentIndicator.setBackgroundResource(indicatorUnselectedBackgroundResId)
      animatorIn!!.setTarget(currentIndicator)
      animatorIn!!.start()
    }

    val selectedIndicator = getChildAt(indicatorPosition)
    if (selectedIndicator != null) {
      selectedIndicator.setBackgroundResource(indicatorBackgroundResId)
      animatorOut!!.setTarget(selectedIndicator)
      animatorOut!!.start()
    }
    lastPosition = indicatorPosition
  }

  companion object {
    private const val DEFAULT_INDICATOR_WIDTH = 8
  }
}