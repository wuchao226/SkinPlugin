package com.wuc.skinplugin;

import android.app.Application;
import com.wuc.skinlib.SkinManager;

/**
 * @author : wuchao5
 * @date : 2021/9/10 15:41
 * @desciption :
 */
public class MyApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    SkinManager.init(this);
  }
}