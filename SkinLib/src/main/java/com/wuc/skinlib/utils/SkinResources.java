package com.wuc.skinlib.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;

/**
 * @author : wuchao5
 * @date : 2021/9/8 14:43
 * @desciption :
 */
public class SkinResources {

  /**
   * 是否使用默认皮肤
   */
  private boolean isDefaultSkin = true;

  private String mSkinPkgName;
  /**
   * App 原始的Resources
   */
  private Resources mAppResources;
  /**
   * 皮肤包的Resources
   */
  private Resources mSkinResources;

  private volatile static SkinResources instance;

  public static void init(Context context) {
    if (instance == null) {
      synchronized (SkinResources.class) {
        if (instance == null) {
          instance = new SkinResources(context);
        }
      }
    }
  }

  public static SkinResources getInstance() {
    return instance;
  }

  private SkinResources(Context context) {
    mAppResources = context.getResources();
  }

  public void reset() {
    mSkinResources = null;
    mSkinPkgName = "";
    isDefaultSkin = true;
  }

  public void applySkin(Resources resource, String pkgName) {
    mSkinResources = resource;
    mSkinPkgName = pkgName;
    // 是否使用默认皮肤
    isDefaultSkin = TextUtils.isEmpty(pkgName) || resource == null;
  }

  /**
   * 1.通过原始app中的resId(R.color.XX)获取到自己的 名字
   * 2.根据名字和类型获取皮肤包中的ID
   */
  public int getIdentifier(int resId) {
    if (isDefaultSkin) {
      return resId;
    }
    // getResourceTypeName(resId) 获取 color, drawable 等类型
    String resName = mAppResources.getResourceEntryName(resId);
    String resType = mAppResources.getResourceTypeName(resId);
    // 通过APK资源的 mResources 获取APK 相关的资源.(类型，名字相关，值不一样的)
    // 比如APK资源里面是这样：<color name="tvui_bg_color">#FF0000</color>
    int skinId = mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
    return skinId;
  }

  /**
   * 输入主APP的ID，到皮肤APK文件中去找到对应ID的颜色值
   *
   * @param resId
   * @return
   */
  public int getColor(int resId) {
    if (isDefaultSkin) {
      return mAppResources.getColor(resId);
    }
    int skinId = getIdentifier(resId);
    if (skinId == 0) {
      return mAppResources.getColor(resId);
    }
    return mSkinResources.getColor(resId);
  }

  public ColorStateList getColorStateList(int resId) {
    if (isDefaultSkin) {
      return mAppResources.getColorStateList(resId);
    }
    int skinId = getIdentifier(resId);
    if (skinId == 0) {
      return mAppResources.getColorStateList(resId);
    }
    return mSkinResources.getColorStateList(skinId);
  }

  /**
   * 获取Drawble
   * @param resId
   * @return
   */
  public Drawable getDrawable(int resId) {
    if (isDefaultSkin) {
      return mAppResources.getDrawable(resId);
    }
    //通过 app的resource 获取id 对应的 资源名 与 资源类型
    //找到 皮肤包 匹配 的 资源名资源类型 的 皮肤包的 资源 ID
    int skinId = getIdentifier(resId);
    if (skinId == 0) {
      return mAppResources.getDrawable(resId);
    }
    return mSkinResources.getDrawable(skinId);
  }

  /**
   * 可能是Color 也可能是drawable
   *
   * @return
   */
  public Object getBackground(int resId) {
    String resourceTypeName = mAppResources.getResourceTypeName(resId);
    if ("color".equals(resourceTypeName)) {
      return getColor(resId);
    } else {
      // drawable
      return getDrawable(resId);
    }
  }
}