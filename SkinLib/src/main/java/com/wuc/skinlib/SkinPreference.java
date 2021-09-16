package com.wuc.skinlib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author : wuchao5
 * @date : 2021/9/8 11:43
 * @desciption :
 */
public class SkinPreference {
  private static final String SKIN_SHARED = "skins";
  private static final String KEY_SKIN_PATH = "skin-path";
  private final SharedPreferences mPref;
  private volatile static SkinPreference instance;

  public static void init(Context context) {
    if (instance == null) {
      synchronized (SkinPreference.class) {
        if (instance == null) {
          instance = new SkinPreference(context.getApplicationContext());
        }
      }
    }
  }

  public static SkinPreference getInstance() {
    return instance;
  }

  private SkinPreference(Context context) {
    mPref = context.getSharedPreferences(SKIN_SHARED, Context.MODE_PRIVATE);
  }

  public void setSkin(String skinPath) {
    mPref.edit().putString(KEY_SKIN_PATH, skinPath).apply();
  }

  public void reset() {
    mPref.edit().remove(KEY_SKIN_PATH).apply();
  }

  public String getSkin() {
    return mPref.getString(KEY_SKIN_PATH, null);
  }
}