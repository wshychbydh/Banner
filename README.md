# Banner
快速构建无限轮播banner

### 功能点：

1、支持自定义轮播时长

2、支持自定义切换插值器及滚动时长

3、支持反向轮播

4、支持自定义轮播方向

4、支持手指触摸后不进行轮播及禁用滑动开关

5、支持自动开始轮播和伴随Lifecycle的生命周期开始/结束轮播

6、支持定义指示器及自带默认指示器

7、支持单张轮播开关

### 使用方法：

#### 一、添加依赖
###### 1、在root目录的build.gradle中添加
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
###### 2、在项目的build.gradle中添加依赖
```
    dependencies {
        implementation 'com.github.wshychbydh:banner:1.1.2'
    }
```

#### 二、使用CarouselViewPager实现自动无限轮播同使用ViewPager使用方式一样

**注:** 可能无法正常响应该ViewPager的触摸事件(通常也不会有此需求), 但不会影响子View。

#### 三、或使用LoopViewPager实现无限轮播同使用ViewPager使用方式一样（不支持自动轮播）

1、调用setBoundaryCaching设置是否缓存边界，默认false
```
  viewPager.setBoundaryCaching(false)
```
2、调用setIndicator设置自定义指示器
```
  viewPager.setIndicator(null)
```
3、调用toRealPosition来获取真实的position
```
  LoopViewPager.toRealPosition(position, count)
```

#### 四、构建CarouselViewPager的CarouselParams参数

```
  val params = CarouselParams.Builder()
        .setDirection(CarouselParams.LEFT)         //滚动方向，只能是LEFT或RIGHT,默认LEFT
        .setInterpolator(null)                     //滚动插值器，默认DecelerateInterpolator
        .setInterval(5000L)                        //滚动间隔millisecond，默认5s
        .setScrollDuration(250)                    //单张滚动时长millisecond，默认250ms
        .setPauseWhenTouch(true)                   //手指按下时是否滚动，默认true
        .enableCarousel(true)                      //是否支持轮播，默认true，（注：若设置为false，不会自动轮播）
        .setRecyclable(true)                       //循环滚动，默认true
        .setReversible(false)                      //反转滚动，默认false
        .setAutoCarousel(true)                     //自动开始/结束轮播，默认true，(注：若为false，数据改变后不会继续轮播）
        .setScrollWhenOne(false)                   //只有一张图时仍然滚动,默认false，禁用后，只有一个child时无法响应触摸事件
        .setScrollAble(true)                       //是否支持手动滑动，默认true，禁用无法响应触摸事件
        .setAttachLifecycle(null)                  //开始/结束伴随生命周期
        .setCacheBoundary(false)                   //是否缓存边界View，默认false
        .setIndicator(null)                        //自定义指示器
        .build()
  viewPager.params = params                        //支持动态设置参数(设置后自动生效)
```

#### 五、设置CarouselViewPager的开启/关闭自动轮播（可选）

1、在activity或fragment或其他适当时机，调用viewpager.start()启动自动轮播

2、在activity或fragment或其他适当时机，调用viewpager.stop()关闭自动轮播

3、若设置enableCarousel(false)，无论如何都不会自动轮播

4、若设置setAutoCarousel(false)，当adapter数据变化后轮播会停止，需再次调用start()开启轮播.

**注:** 若params.autoCarousel为true时，当adapter有数据后会自动开始轮播；
 若设置了params.attachLifecycle，则会自动跟随lifecycle的onStart/onStop自动调用


#### 六、配置指示器CarouselIndicator(可选)
```
  <com.eye.cool.banner.CarouselIndicator
    android:id="@+id/indicator"
    android:layout_width="wrap_content"
    android:layout_height="48dp"
    app:animator="reference"
    app:animator_reverse="reference"
    app:width="dimension"
    app:height="dimension"
    app:margin="dimension"
    app:drawable="reference"
    app:alwaysShownWhenOne="boolean"
    app:drawable_unselected="reference"
    app:orientation="horizontal|vertical"
    app:ci_gravity="同LinearLayout"/>
```

[![](https://jitpack.io/v/wshychbydh/banner.svg)](https://jitpack.io/#wshychbydh/banner)