# BottomMenu
iOS版Teambition首页菜单Android实现，效果如下：

![](http://ww1.sinaimg.cn/large/4f262e75ly1fdq57z8fnlg20bc0k0qv5.gif)

### Step1
加入构建脚本
```
compile 'com.ethan.menu.lib:Library:1.0.0'
```
或直接下载

[ ![Download](https://api.bintray.com/packages/zss9606/maven/BottomMenu/images/download.svg) ](https://bintray.com/zss9606/maven/BottomMenu/_latestVersion)

### Step2

在xml文件中声明
```
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.ethan.menu.lib.BottomMenu
        android:id="@+id/home_menu"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:menu_backgroundArcHeghit="180dp"
        app:menu_backgroundColor="#0000dd"
        app:menu_backgroundHeight="150dp"
        app:menu_item_marginEdge="20dp"
        app:menu_marginBottom="10dp">

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/ic_launcher" />


        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/ic_launcher" />

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/ic_launcher" />

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/ic_launcher" />

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/ic_launcher" />

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/ic_launcher" />

    </com.ethan.menu.lib.BottomMenu>

</FrameLayout>
```

### Step3

通过`findViewById`获取实例。

* 具体介绍欢迎[访问](http://blog.ethan.wiki/android/Teambition%E9%A6%96%E9%A1%B5%E8%8F%9C%E5%8D%95%E5%89%96%E6%9E%90)


[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](http://www.gnu.org/licenses/lgpl-3.0)
