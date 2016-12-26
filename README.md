<p align="center"><img src="http://i67.tinypic.com/2ij1d2r.jpg"></p>

FrameImageView
=================
[ ![Download](https://api.bintray.com/packages/jjhesk/maven/frame-imageview/images/download.svg) ](https://bintray.com/jjhesk/maven/frame-imageview/_latestVersion)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-11%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=11)


This is an Android project allowing to realize a frame ImageView in the simplest way possible.


USAGE
-----

To make a circular ImageView add FrameImageView in your layout XML and add FrameImageView library in your project or you can also grab it via Gradle:

```groovy
compile 'com.hkm.ui:frame-imageview:3.20.0'
```

XML
-----

```xml
<com.mikhaellopez.frameImageView.FrameImageView
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:src="@drawable/image"
        app:fic_border_color="#EEEEEE"
        app:fic_border_width="4dp"
        app:fic_shadow="true"
        app:fic_shadow_radius="10"
        app:fic_shadow_color="#8BC34A"/>

<com.mikhaellopez.frameImageView.EditFrameImageView
        android:layout_width="250dp"
        android:layout_height="250dp"
        android:src="@drawable/image"
  />
```

You must use the following properties in your XML to change your FrameImageView.


#####Properties:

* `app:fic_border`          (boolean)   -> default true
* `app:fic_border_color`    (color)     -> default WHITE
* `app:fic_border_width`    (dimension) -> default 4dp
* `app:fic_shadow`          (boolean)   -> default false
* `app:fic_shadow_color`    (color)     -> default BLACK
* `app:fic_shadow_radius`   (float)     -> default 8.0f

JAVA
-----

```java
FrameImageView mFrameImageView = (FrameImageView)findViewById(R.id.yourFrameImageView);
// Set Border
mFrameImageView.setBorderColor(getResources().getColor(R.color.GrayLight));
mFrameImageView.setBorderWidth(10);
// Add Shadow with default param
mFrameImageView.addShadow();
// or with custom param
mFrameImageView.setShadowRadius(15);
mFrameImageView.setShadowColor(Color.RED);
```

LINK
-----

**Stack OverFlow:**

I realized this project using this post:
* [Create circular image view in android](http://stackoverflow.com/a/16208548/1832221)
* [How to add a shadow and a border on circular imageView android?](http://stackoverflow.com/q/17655264/1832221)


LICENCE
-----

FrameImageView by [Lopez Mikhael](http://mikhaellopez.com/) is licensed under a [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
