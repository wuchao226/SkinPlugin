package com.wuc.skinplugin;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.wuc.skinplugin.fragment.MusicFragment;
import com.wuc.skinplugin.fragment.RadioFragment;
import com.wuc.skinplugin.fragment.VideoFragment;
import com.wuc.skinplugin.widget.MyTabLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    View view = findViewById(R.id.test);

    MyTabLayout tabLayout = findViewById(R.id.tabLayout);
    ViewPager viewPager = findViewById(R.id.viewPager);
    List<Fragment> list = new ArrayList<>();
    list.add(new MusicFragment());
    list.add(new VideoFragment());
    list.add(new RadioFragment());
    List<String> listTitle = new ArrayList<>();
    listTitle.add("音乐");
    listTitle.add("视频");
    listTitle.add("电台");
    MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter
        (getSupportFragmentManager(), list, listTitle);
    viewPager.setAdapter(myFragmentPagerAdapter);
    tabLayout.setupWithViewPager(viewPager);
  }

  /**
   * 进入换肤
   *
   * @param view
   */
  public void skinSelect(View view) {
    startActivity(new Intent(this, SkinActivity.class));
  }
}