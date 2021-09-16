package com.wuc.skinplugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.wuc.skinlib.SkinManager;

public class SkinActivity extends AppCompatActivity {

  private String skinPackageName = "/skin-package-debug.apk";
  // private String skinPackageName = "/3116007.skin500";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //        LayoutInflater.from(this).setFactory2();
    setContentView(R.layout.activity_skin);

    //        findViewById(R.id.tabLayout);
    //        Resources resources = getResources();
    //        new Resources()

  }

  public void change(View view) {
    // /data/data/com.wuc.skinplugin/cache/skin-package-debug.apk
    String skinPkg = getBaseContext().getCacheDir() + skinPackageName;
    //skinPkg = /data/user/0/com.wuc.skinplugin/cache/skin-package-debug.apk
    //换肤，收包裹，皮肤包是独立的apk包，可以来自网络下载
    SkinManager.getInstance().loadSkin(skinPkg);
  }

  public void restore(View view) {
    SkinManager.getInstance().loadSkin(null);
  }
}
