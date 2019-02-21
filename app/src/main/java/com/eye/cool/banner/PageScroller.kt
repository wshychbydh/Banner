package com.eye.cool.banner

import android.content.Context
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller

/**
 * Created by cool on 2018/4/23.
 */
internal class PageScroller internal constructor(
    context: Context,
    private var sDuration: Int = 250,
    interpolator: Interpolator? = DecelerateInterpolator()
) : Scroller(context, interpolator) {

  @Volatile
  var isDrag = false

  override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
    super.startScroll(startX, startY, dx, dy, if (isDrag) duration else sDuration)
    isDrag = false
  }

  override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
    super.startScroll(startX, startY, dx, dy, if (isDrag) duration else sDuration)
    isDrag = false
  }
}