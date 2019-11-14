package com.eye.cool.banner

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * Created by ycb on 2019/4/4
 * Modified form https://github.com/imbryk/LoopingViewPager
 *
 * A PagerAdapter wrapper responsible for providing a proper page to
 * LoopViewPager
 *
 * This class shouldn't be used directly
 */
internal class LoopPagerAdapterWrapper constructor(val realAdapter: PagerAdapter) : PagerAdapter() {

  private var mToDestroy = SparseArray<ToDestroy>()

  private var mBoundaryCaching: Boolean = false

  private val realFirstPosition: Int
    get() = 1

  private val realLastPosition: Int
    get() = realFirstPosition + realCount - 1

  val realCount: Int
    get() = realAdapter.count

  internal fun setBoundaryCaching(cacheBoundary: Boolean) {
    mBoundaryCaching = cacheBoundary
  }

  override fun notifyDataSetChanged() {
    mToDestroy.clear()
    super.notifyDataSetChanged()
  }

  internal fun toRealPosition(position: Int): Int {
    val count = realCount
    if (count == 0) return 0
    var realPosition = (position - 1) % count
    if (realPosition < 0) realPosition += count
    return realPosition
  }

  fun toInnerPosition(realPosition: Int): Int {
    return realPosition + 1
  }

  override fun getCount(): Int {
    if (realAdapter.count == 0) return 0
    return realAdapter.count + 2
  }

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val realPosition = if (realAdapter is FragmentPagerAdapter || realAdapter is FragmentStatePagerAdapter)
      position
    else toRealPosition(position)

    if (mBoundaryCaching) {
      val toDestroy = mToDestroy.get(position)
      if (toDestroy != null) {
        mToDestroy.remove(position)
        return toDestroy.target
      }
    }
    return realAdapter.instantiateItem(container, realPosition)
  }

  override fun destroyItem(container: ViewGroup, position: Int, target: Any) {
    val realFirst = realFirstPosition
    val realLast = realLastPosition
    val realPosition = if (realAdapter is FragmentPagerAdapter || realAdapter is FragmentStatePagerAdapter)
      position
    else toRealPosition(position)

    if (mBoundaryCaching && (position == realFirst || position == realLast)) {
      mToDestroy.put(position, ToDestroy(container, realPosition, target))
    } else {
      realAdapter.destroyItem(container, realPosition, target)
    }
  }

  /*
   * Delegate rest of methods directly to the inner adapter.
   */
  override fun finishUpdate(container: ViewGroup) {
    realAdapter.finishUpdate(container)
  }

  override fun isViewFromObject(view: View, `object`: Any): Boolean {
    return realAdapter.isViewFromObject(view, `object`)
  }

  override fun restoreState(bundle: Parcelable?, classLoader: ClassLoader?) {
    realAdapter.restoreState(bundle, classLoader)
  }

  override fun saveState(): Parcelable? {
    return realAdapter.saveState()
  }

  override fun startUpdate(container: ViewGroup) {
    realAdapter.startUpdate(container)
  }

  override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
    realAdapter.setPrimaryItem(container, position, `object`)
  }

  /*
   * End delegation
   */

  /**
   * Container class for caching the boundary views
   */
  internal class ToDestroy(var container: ViewGroup, var position: Int, var target: Any)

}