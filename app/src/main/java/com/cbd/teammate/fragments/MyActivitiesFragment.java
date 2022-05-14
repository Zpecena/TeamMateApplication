package com.cbd.teammate.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cbd.teammate.R;
import com.cbd.teammate.fragments.viewpagerfragments.GoingToPageFragment;
import com.cbd.teammate.fragments.viewpagerfragments.MyActivitiesPageFragment;
import com.cbd.teammate.fragments.viewpagerfragments.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyActivitiesFragment extends Fragment {

    private View view;

    private TabLayout tabLayout;
    private ViewPager viewPager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_activities, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager_activities);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPageAdapter.addFragment(new MyActivitiesPageFragment(),
                "My Activities");

        viewPageAdapter.addFragment(new GoingToPageFragment(),
                "Going to");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
