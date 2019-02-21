package com.eye.cool.banner

/**
 * Created by cool on 2018/4/18.
 */
interface ICarousel {

  fun getInitItem(): Int

  fun getDataSize(): Int

  fun getCount(): Int

  fun getNextPage(currentPage: Int): Int

  var params: CarouselParams
}