package com.wuc.skinlib;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.wuc.skinlib.utils.SkinThemeUtils;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * @author : wuchao5
 * @date : 2021/9/9 15:00
 * @desciption : 用来接管系统的 view 的生产过程
 */
public class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {

  private static final String[] mClassPrefixList = {
      "android.widget.",
      "android.webkit.",
      "android.app.",
      "android.view."
  };

  /**
   * 记录对应 View 的构造函数
   */
  private static final Class<?>[] mConstructorSignature = new Class[] {
      Context.class, AttributeSet.class };

  private static final HashMap<String, Constructor<? extends View>> mConstructorMap =
      new HashMap<String, Constructor<? extends View>>();

  /**
   * 当选择新皮肤后需要替换 View 与之对应的属性
   * 页面属性管理器
   */
  private SkinAttribute mSkinAttribute;
  /**
   * 用于获取窗口的状态框的信息
   */
  private Activity mActivity;

  public SkinLayoutInflaterFactory(Activity activity) {
    mActivity = activity;
    mSkinAttribute = new SkinAttribute();
  }

  @Nullable
  @Override
  public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
    // 换肤就是在需要时候替换 View 的属性(src、background等)
    // 所以这里创建 View,从而修改 View 属性
    View view = createSDKView(name, context, attrs);
    if (null == view) {
      view = createView(name, context, attrs);
    }
    //这就是我们加入的逻辑
    if (null != view) {
      //加载属性
      mSkinAttribute.look(view, attrs);
    }
    return view;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
    return null;
  }

  private View createSDKView(String name, Context context, AttributeSet attrs) {
    //如果包含 . 则不是SDK中的view 可能是自定义view包括support库中的View
    if (-1 != name.indexOf('.')) {
      return null;
    }
    //不包含就要在解析的 节点 name前，拼上： android.widget. 等尝试去反射
    for (int i = 0; i < mClassPrefixList.length; i++) {
      View view = createView(mClassPrefixList[i] + name, context, attrs);
      if (view != null) {
        return view;
      }
    }
    return null;
  }

  private View createView(String name, Context context, AttributeSet attrs) {
    Constructor<? extends View> constructor = findConstructor(context, name);
    try {
      return constructor.newInstance(context, attrs);
    } catch (Exception e) {
    }
    return null;
  }

  private Constructor<? extends View> findConstructor(Context context, String name) {
    Constructor<? extends View> constructor = mConstructorMap.get(name);
    if (constructor == null) {
      try {
        Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
        constructor = clazz.getConstructor(mConstructorSignature);
        mConstructorMap.put(name, constructor);
      } catch (Exception e) {
      }
    }
    return constructor;
  }

  /**
   * 如果有人发送通知，这里就会执行
   */
  @Override
  public void update(Observable o, Object arg) {
    Log.i("ewr", "-----dfdfdgggggd");
    SkinThemeUtils.updateStatusBarColor(mActivity);
    mSkinAttribute.applySkin();
  }
}