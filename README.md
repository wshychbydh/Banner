# Banner
快速构建轮播banner

### 功能点：

1、支持自定义轮播时长

2、支持自定义切换插值器及时长

3、支持反向轮播

4、支持自定义轮播方向

4、支持手指触摸后不进行轮播

5、支持自动伴随Activity的生命周期开始/结束轮播

### 使用方法：

##### 一、构建ViewPager的adapter（以下方式任选）

1、你的adapter实现ICarousel或ICarouselData接口

2、你的adapter继承CarouselPagerAdapter **（推荐）**

```
    adapter.setData(T)     //设置数据
    adapter.appendData(T)  //添加数据
    adapter.onParamsChanged() //当ViewPager的配置变化后手动调用
    adapter.getDataSize()  //获取内容的真实条数
```

##### 二、构建ViewPager参数

```
  val params = CarouselParams.Builder()
        .setDirection(CarouselParams.RIGHT)        //滚动方向，只能是LEFT或RIGHT,默认RIGHT
        .setInterpolator(DecelerateInterpolator()) //滚动插值器，默认DecelerateInterpolator
        .setInterval(5000L)                        //滚动间隔，默认5s
        .setPauseWhenTouch(true)                   //手指按下时是否滚动，默认true
        .setRecyclable(true)                       //循环滚动，默认true
        .setReversible(false)                      //反转滚动，默认false
        .setScrollDuration(2000)                   //单张滚动时长，默认1s
        .enableCarousel(true)                      //开启滚动，默认true
        .build()
  params.onParamsChanged()                         //支持动态设置参数，设置后调用改方法使其生效
```
##### 三、设置ViewPager

```
  viewPager.adapter = Your adapter
  val carouselViewPager = CarouselViewPager(viewPager, params) //绑定viewpager，params可选
  //TODO 为adapter设置数据
```

##### 四、设置生命周期

1、在activity或fragment的适当时机，如onStart的时候调用viewpager.onStart

2、在activity或fragment的适当时机，如onStop的时候调用viewpager.onStop

**注:** 如果viewPager.context为AppCompatActivity，会自动跟随activity的onStart/onStop调用
