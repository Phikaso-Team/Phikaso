package com.phikaso.app.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.phikaso.app.R;
import com.phikaso.app.adapter.FragmentAdapter;
import com.phikaso.app.fragment.RecentCallFragment;
import com.phikaso.app.fragment.RecentMsgFragment;
import com.phikaso.app.fragment.SearchFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ViewPager2 myViewPager2;
    private FragmentAdapter myAdapter;

    private SearchFragment searchFragment;
    private RecentCallFragment recentCallFragment;
    private RecentMsgFragment recentMsgFragment;
    private TabLayout tabLayout;

    static final List<String> tabTitles = new ArrayList<>(Arrays.asList(
            "피싱 번호 검색", "최근 통화 기록", "최근 문자 기록"));

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_search);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        myViewPager2 = findViewById(R.id.viewPager2);
        myAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());

        myAdapter.addFragment(new SearchFragment());
        myAdapter.addFragment(new RecentCallFragment());
        myAdapter.addFragment(new RecentMsgFragment());

        createFragment();
        createViewPager2();
        settingTabLayout();

        // TabLayout과 ViewPager2 연결
        new TabLayoutMediator(tabLayout, myViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int pos) {
                switch (pos) {
                    case 0:
                        tab.setText(tabTitles.get(pos));
                        tab.setIcon(R.drawable.ic_baseline_person_search_24);
                        break;

                    case 1:
                        tab.setText(tabTitles.get(pos));
                        tab.setIcon(R.drawable.ic_baseline_call_24);
                        break;

                    case 2:
                        tab.setText(tabTitles.get(pos));
                        tab.setIcon(R.drawable.ic_baseline_sms_24);
                        break;
                }
            }
        }).attach();

    }


    private void createFragment() {

        searchFragment = new SearchFragment();
        recentCallFragment = new RecentCallFragment();
        recentMsgFragment = new RecentMsgFragment();
    }


    private void createViewPager2() {

        myViewPager2 = (ViewPager2) findViewById(R.id.viewPager2);
        myAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        myAdapter.addFragment(searchFragment);
        myAdapter.addFragment(recentCallFragment);
        myAdapter.addFragment(recentMsgFragment);

        myViewPager2.setAdapter(myAdapter);
    }


    private void settingTabLayout() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        myViewPager2.setCurrentItem(0);
                        break;
                    case 1:
                        myViewPager2.setCurrentItem(1);
                        break;
                    case 2:
                        myViewPager2.setCurrentItem(2);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

}
