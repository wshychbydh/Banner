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
      if (params.direction == CarouselParams.RIGHT_TO_LEFT) {
        val next = currentPage + 1
        return if (next % size == 0) {
          params.direction = CarouselParams.LEFT_TO_RIGHT
          currentPage - 1
        } else {
          next
        }
      } else {
        val next = currentPage - 1
        return if (next % size <= 0) {
          params.direction = CarouselParams.RIGHT_TO_LEFT
          0
        } else {
          next
        }
      }
    }
    if (params.recyclable) {
      return if (params.direction == CarouselParams.RIGHT_TO_LEFT) currentPage + 1 else currentPage - 1
    }
    return currentPage
  }

  var params: CarouselParams
}