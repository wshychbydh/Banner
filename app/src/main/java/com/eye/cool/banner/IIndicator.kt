package com.eye.cool.banner

/**
 *Created by ycb on 2019/4/12 0012
 *Indicator for ViewPager
 */
interface IIndicator {

  /**
   * Called on ViewPager's data changed
   * @param currentPosition current selected position
   * @param count page's count
   */
  fun onDataChanged(currentPosition: Int, count: Int)

  /**
   * Called on ViewPager's page changed
   * @param position current selected position
   */
  fun onPageSelected(position: Int)

  fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
}