package org.kprsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kprsongs.component.HomeViewerPageAdapter;
import org.kprsongs.component.SlidingTabLayout;
import org.kprsongs.glorytogod.R;

import java.util.Arrays;
import java.util.List;

/**
 * author:K Purushotham Reddy
 * version:2.1.0
 */
public class HomeTabFragment extends Fragment
{
    private List<String> titles;
    private ViewPager pager;
    private HomeViewerPageAdapter adapter;
    private SlidingTabLayout tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.home_tab_layout, container, false);
        titles = Arrays.asList(getResources().getString(R.string.titles), getResources().getString(R.string.artists), getResources().getString(R.string.playlists));
        // Creating The HomeViewerPageAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.ome
        Log.i(this.getClass().getSimpleName(), "Preparing to load home view fragment");
        adapter = new HomeViewerPageAdapter(getChildFragmentManager(), titles);
        adapter.notifyDataSetChanged();

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.bright_foreground_material_dark);
            }
        });
               // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        Log.i(this.getClass().getSimpleName(), "Finished loading home fragment");
        return view;
    }
}