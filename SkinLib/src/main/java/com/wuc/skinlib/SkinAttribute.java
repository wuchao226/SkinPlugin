package com.wuc.skinlib;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import com.wuc.skinlib.utils.SkinResources;
import com.wuc.skinlib.utils.SkinThemeUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : wuchao5
 * @date : 2021/9/8 20:27
 * @desciption : 这里面放了所有要换肤的view所对应的属性
 */
public class SkinAttribute {

  private static final List<String> mAttributes = new ArrayList<>();

  // 需要换肤的属性
  static {
    mAttributes.add("background");
    mAttributes.add("src");
    mAttributes.add("textColor");
    mAttributes.add("drawableLeft");
    mAttributes.add("drawableTop");
    mAttributes.add("drawableRight");
    mAttributes.add("drawableBottom");
  }

  /**
   * 记录换肤需要操作的 View 与属性信息
   */
  private List<SkinView> mSkinViews = new ArrayList<>();

  /**
   * 记录下一个 View 身上哪几个属性需要换肤textColor/src
   *
   * @param view
   * @param attrs
   */
  public void look(View view, AttributeSet attrs) {
    List<SkinPair> skinPairs = new ArrayList<>();
    for (int i = 0; i < attrs.getAttributeCount(); i++) {
      //获得属性名  textColor/background
      String attributeName = attrs.getAttributeName(i);
      if (mAttributes.contains(attributeName)) {
        // #
        // ?722727272
        // @722727272
        String attributeValue = attrs.getAttributeValue(i);
        // 比如color 以#开头表示写死的颜色 不可用于换肤
        // 固定的值如#FF00FF 色值
        if (attributeValue.startsWith("#")) {
          continue;
        }
        int resId;
        // 以 ？开头的表示使用 属性
        // 系统属性 android:layout_height="?actionBarSize"
        if (attributeValue.startsWith("?")) {
          int attrId = Integer.parseInt(attributeValue.substring(1));
          resId = SkinThemeUtils.getResId(view.getContext(), new int[] { attrId })[0];
        } else if (attributeValue.startsWith("@")) {
          // 正常以 @ 开头
          // 正常情况 android:background="@drawable/toolbar"
          resId = Integer.parseInt(attributeValue.substring(1));
        } else {
          //其他
          continue;
        }
        SkinPair skinPair = new SkinPair(attributeName, resId);
        skinPairs.add(skinPair);
      }
    }
    if (!skinPairs.isEmpty() || view instanceof SkinViewSupport) {
      SkinView skinView = new SkinView(view, skinPairs);
      // 如果选择过皮肤 ，调用 一次 applySkin 加载皮肤的资源
      skinView.applySkin();
      mSkinViews.add(skinView);
    }
  }

  /**
   * 对所有的 view 中的所有的属性进行皮肤修改
   */
  public void applySkin() {
    for (SkinView skinView : mSkinViews) {
      skinView.applySkin();
    }
  }

  static class SkinView {
    private View mView;
    //这个View的能被换肤的属性与它对应的id 的集合
    private List<SkinPair> mSkinPairs;

    public SkinView(View view, List<SkinPair> skinPairs) {
      mView = view;
      mSkinPairs = skinPairs;
    }

    /**
     * 对一个 View 中的所有的属性进行修改
     */
    public void applySkin() {
      applySkinSupport();
      for (SkinPair skinPair : mSkinPairs) {
        Drawable left = null, top = null, right = null, bottom = null;
        switch (skinPair.attributeName) {
          case "background":
            Object background = SkinResources.getInstance().getBackground(skinPair.resId);
            //背景可能是 @color 也可能是 @drawable
            // 比如@color/xxx
            if (background instanceof Integer) {
              mView.setBackgroundColor((int) background);
            } else {
              ViewCompat.setBackground(mView, (Drawable) background);
            }
            break;
          case "src":
            background = SkinResources.getInstance().getBackground(skinPair.resId);
            if (background instanceof Integer) {
              ((ImageView) mView).setImageDrawable(new ColorDrawable((Integer) background));
            } else {
              ((ImageView) mView).setImageDrawable((Drawable) background);
            }
            break;
          case "textColor":
            ColorStateList colorStateList = SkinResources.getInstance().getColorStateList(skinPair.resId);
            ((TextView) mView).setTextColor(colorStateList);
            break;
          case "drawableLeft":
            left = SkinResources.getInstance().getDrawable(skinPair.resId);
            break;
          case "drawableTop":
            top = SkinResources.getInstance().getDrawable(skinPair.resId);
            break;
          case "drawableRight":
            right = SkinResources.getInstance().getDrawable(skinPair.resId);
            break;
          case "drawableBottom":
            bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
            break;
          default:
            break;
        }
        if (null != left || null != top || null != right || null != bottom) {
          ((TextView) mView).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        }
      }
    }

    /**
     * 自定义view支持
     */
    private void applySkinSupport() {
      if (mView instanceof SkinViewSupport) {
        ((SkinViewSupport) mView).applySkin();
      }
    }
  }

  static class SkinPair {
    // 属性名
    String attributeName;
    // 对应的资源id
    int resId;

    public SkinPair(String attributeName, int resId) {
      this.attributeName = attributeName;
      this.resId = resId;
    }
  }
}