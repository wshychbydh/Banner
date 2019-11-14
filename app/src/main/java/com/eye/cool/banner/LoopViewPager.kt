package com.eye.cool.banner

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 * Created by ycb on 2019/4/4
 * Modified form https://github.com/imbryk/LoopingViewPager
 */
open class LoopViewPager : ViewPager {

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
    init()
  }

  private fun init() {
    super.addOnPageChangeListener(onPageChangeListener)
  }

  internal var mOuterPageChangeListeners: ArrayList<OnPageChangeListener>? = null
  private var mAdapter: LoopPagerAdapterWrapper? = null
  private var mBoundaryCaching = DEFAULT_BOUNDARY_CASHING
  private var indicator: IIndicator? = null

  private val onPageChangeListener = object : OnPageChangeListener {
    private var mPreviousOffset = -1f
    private var mPreviousPosition = -1f

    override fun onPageSelected(position: Int) {

      val realPosition = mAdapter!!.toRealPosition(position)
      if (mPreviousPosition != realPosition.toFloat()) {
        mPreviousPosition = realPosition.toFloat()
        mOuterPageChangeListeners?.forEach {
          it.onPageSelected(realPosition)
        }
      }
      indicator?.onPageSelected(realPosition)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
      var realPosition = position
      if (mAdapter != null) {
        realPosition = mAdapter!!.toRealPosition(position)

        if (positionOffset == 0f
            && mPreviousOffset == 0f
            && (position == 0 || position == mAdapter!!.count - 1)) {
          setCurrentItem(realPosition, false)
        }
      }

      mPreviousOffset = positionOffset
      mOuterPageChangeListeners?.forEach {
        if (realPosition != mAdapter!!.realCount - 1) {
          it.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
        } else {
          if (positionOffset > 0.5) {
            it.onPageScrolled(0, 0f, 0)
          } else {
            it.onPageScrolled(realPosition, 0f, 0)
          }
        }
      }

      indicator?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
    }

    override fun onPageScrollStateChanged(state: Int) {
      if (mAdapter != null) {
        val position = super@LoopViewPager.getCurrentItem()
        val realPosition = mAdapter!!.toRealPosition(position)
        if (state == SCROLL_STATE_IDLE && (position == 0 || position == mAdapter!!.count - 1)) {
          setCurrentItem(realPosition, false)
        }
      }
      mOuterPageChangeListeners?.forEach {
        it.onPageScrollStateChanged(state)
      }
    }
  }

  /**
   * If set to true, the boundary views (i.e. first and last) will never be destroyed
   * This may help to prevent "blinking" of some views
   *
   * @param cacheBoundary
   */
  fun setBoundaryCaching(cacheBoundary: Boolean) {
    mBoundaryCaching = cacheBoundary
    mAdapter?.setBoundaryCaching(cacheBoundary)
  }

  /**
   * Indicator which conjunction with Viewpager
   */
  fun setIndicator(indicator: IIndicator?) {
    this.indicator = indicator
    indicator?.onDataChanged(currentItem, mAdapter?.realCount ?: 0)
  }

  override fun setAdapter(adapter: PagerAdapter?) {
    if (adapter == null) {
      super.setAdapter(null)
      return
    }
    mAdapter = LoopPagerAdapterWrapper(adapter)
    mAdapter!!.setBoundaryCaching(mBoundaryCaching)
    super.setAdapter(mAdapter)
    adapter.registerDataSetObserver(dataObserver)
    setCurrentItem(0, false)
  }

  override fun getAdapter(): PagerAdapter? {
    return mAdapter?.realAdapter
  }

  override fun getCurrentItem(): Int {
    return mAdapter?.toRealPosition(super.getCurrentItem()) ?: 0
  }

  override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
    val realItem = mAdapter!!.toInnerPosition(item)
    super.setCurrentItem(realItem, smoothScroll)
  }

  override fun setCurrentItem(item: Int) {
    if (currentItem != item) {
      setCurrentItem(item, true)
    }
  }

  override fun addOnPageChangeListener(listener: OnPageChangeListener) {
    if (this.mOuterPageChangeListeners == null) {
      this.mOuterPageChangeListeners = ArrayList()
    }
    if (!mOuterPageChangeListeners!!.contains(listener)) {
      mOuterPageChangeListeners!!.add(listener)
    }
  }

  override fun setOnPageChangeListener(listener: OnPageChangeListener) {
    if (mOuterPageChangeListeners == null) {
      mOuterPageChangeListeners = ArrayList()
    } else {
      mOuterPageChangeListeners!!.clear()
    }
    mOuterPageChangeListeners!!.add(listener)
  }

  private val dataObserver = object : DataSetObserver() {
    override fun onChanged() {
      mAdapter?.notifyDataSetChanged()
      setCurrentItem(0, false)
      indicator?.onDataChanged(currentItem, mAdapter?.realCount ?: 0)
    }
  }

  companion object {

    private const val DEFAULT_BOUNDARY_CASHING = false


    /**
     * helper function which may be used when implementing FragmentPagerAdapter
     *
     * @param position
     * @param count
     * @return (position-1)%count
     */
    fun toRealPosition(position: Int, count: Int): Int {
      if (count <= 0) return position
      var pos = position
      pos -= 1
      if (pos < 0) {
        pos += count
      } else {
        pos %= count
      }
      return pos
    }
  }
}