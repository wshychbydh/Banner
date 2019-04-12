package com.eye.cool.banner

/**
 * Created by cool on 2018/4/18.
 */
internal interface ICarousel {

  fun getCount(): Int

  fun getNextPage(currentPage: Int): Int {
    if (!params.recyclable && !params.reversible) return currentPage
    val size = getCount()
    if (params.reversible) {
      if (params.direction == CarouselParams.LEFT) {
        val next = currentPage + 1
        return if (next % size == 0) {
          params.direction = CarouselParams.RIGHT
          currentPage - 1
        } else {
          next
        }
      } else {
        val next = currentPage - 1
        return if (next % size <= 0) {
          params.direction = CarouselParams.LEFT
          currentPage + 1
        } else {
          next
        }
      }
    }
    if (params.recyclable) {
      return if (params.direction == CarouselParams.LEFT) currentPage + 1 else currentPage - 1
    }
    return currentPage
  }

  var params: CarouselParams
}