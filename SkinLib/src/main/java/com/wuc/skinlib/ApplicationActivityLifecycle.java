package com.wuc.skinlib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;
import com.wuc.skinlib.utils.SkinThemeUtils;
import java.lang.reflect.Field;
import java.util.Observable;

/**
 * @author : wuchao5
 * @date : 2021/9/10 14:37
 * @desciption : 注册Activity生命周期的回调
 */
public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {

  private Observable mObservable;
  /**
   * 记录下每个Activity所对应的工厂
   */
  private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new ArrayMap<>();

  public ApplicationActivityLifecycle(Observable observable) {
    mObservable = observable;
  }

  @Override
  public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    // 更新状态栏
    SkinThemeUtils.updateStatusBarColor(activity);
    //获得Activity的布局加载器
    LayoutInflater layoutInflater = activity.getLayoutInflater();
    try {
      //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
      // Android10版本以后，mFactorySet 这个字段就不再被支持了
      //如设置过抛出一次
      //设置 mFactorySet 标签为false

      // 依赖 FreeReflection 库：一个允许你在Android P上使用反射而没有任何限制的库
      @SuppressLint("SoonBlockedPrivateApi")
      Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
      field.setAccessible(true);
      field.setBoolean(layoutInflater, false);
      // 使用factory2 设置布局加载工程
      SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);
      LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
      mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);
      mObservable.addObserver(skinLayoutInflaterFactory);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override public void onActivityStarted(@NonNull Activity activity) {

  }

  @Override public void onActivityResumed(@NonNull Activity activity) {

  }

  @Override public void onActivityPaused(@NonNull Activity activity) {

  }

  @Override public void onActivityStopped(@NonNull Activity activity) {

  }

  @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

  }

  @Override
  public void onActivityDestroyed(@NonNull Activity activity) {
    SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
    SkinManager.getInstance().deleteObserver(observer);
  }

}