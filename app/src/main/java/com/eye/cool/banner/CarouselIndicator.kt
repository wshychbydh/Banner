package com.eye.cool.banner

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.support.annotation.AnimatorRes
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout

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
  private var animatorResId = R.animator.scale_with_alpha
  private var animatorReverseResId = 0
  private var indicatorBackgroundResId = R.drawable.white_radius
  private var indicatorUnselectedBackgroundResId = R.drawable.white_radius
  private var animatorOut: Animator? = null
  private var animatorIn: Animator? = null
  private var immediateAnimatorOut: Animator? = null
  private var immediateAnimatorIn: Animator? = null
  private var placeHolderView: View? = null
  private var alwaysShownOnOnlyOne = true

  private var lastPosition = -1

  init {
    init(context, attrs)
  }

  fun configureIndicator(
      indicatorWidth: Int = 0,
      indicatorHeight: Int = 0,
      indicatorMargin: Int = 0,
      @AnimatorRes animatorId: Int = R.animator.scale_with_alpha,
      @AnimatorRes animatorReverseId: Int = 0,
      @DrawableRes indicatorBackgroundId: Int = R.drawable.white_radius,
      @DrawableRes indicatorUnselectedBackgroundId: Int = R.drawable.white_radius
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

    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CarouselIndicator)
    indicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CarouselIndicator_width, 0)
    indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.CarouselIndicator_height, 0)
    indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.CarouselIndicator_margin, 0)

    animatorResId = typedArray.getResourceId(R.styleable.CarouselIndicator_animator, R.animator.scale_with_alpha)
    animatorReverseResId = typedArray.getResourceId(R.styleable.CarouselIndicator_animator_reverse, 0)
    indicatorBackgroundResId = typedArray.getResourceId(R.styleable.CarouselIndicator_drawable, R.drawable.white_radius)
    indicatorUnselectedBackgroundResId = typedArray.getResourceId(R.styleable.CarouselIndicator_drawable_unselected, indicatorBackgroundResId)

    val orientation = typedArray.getInt(R.styleable.CarouselIndicator_orientation, LinearLayout.HORIZONTAL)
    setOrientation(if (orientation == LinearLayout.VERTICAL) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL)

    val gravity = typedArray.getInt(R.styleable.CarouselIndicator_ci_gravity, Gravity.CENTER)
    setGravity(if (gravity >= 0) gravity else Gravity.CENTER)

    alwaysShownOnOnlyOne = typedArray.getBoolean(R.styleable.CarouselIndicator_alwaysShownOnOnlyOne, true)
    typedArray.recycle()
  }

  private fun checkIndicatorConfig(context: Context) {
    indicatorWidth = if (indicatorWidth <= 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else indicatorWidth
    indicatorHeight = if (indicatorHeight <= 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else indicatorHeight
    indicatorMargin = if (indicatorMargin <= 0) dip2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else indicatorMargin

    animatorResId = if (animatorResId == 0) R.animator.scale_with_alpha else animatorResId

    animatorOut = createAnimatorOut(context)
    immediateAnimatorOut = createAnimatorOut(context)
    immediateAnimatorOut!!.duration = 0

    animatorIn = createAnimatorIn(context)
    immediateAnimatorIn = createAnimatorIn(context)
    immediateAnimatorIn!!.duration = 0

    indicatorBackgroundResId = if (indicatorBackgroundResId == 0) R.drawable.white_radius else indicatorBackgroundResId
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
  fun setPlaceHolderView(placeholder: View) {
    this.placeHolderView = placeholder
  }

  /**
   * Show indicator even if only one item
   */
  fun setAlwaysShownOnOnlyOne(shown: Boolean) {
    this.alwaysShownOnOnlyOne = shown
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

    if (orientation == LinearLayout.HORIZONTAL) {
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
    if (count > 0 && (alwaysShownOnOnlyOne || count > 1)) {
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

    if (placeHolderView != null && indicatorPosition == childCount - 1) {
      placeHolderView!!.visibility = View.VISIBLE
      visibility = View.GONE
    } else {
      placeHolderView?.visibility = View.GONE
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