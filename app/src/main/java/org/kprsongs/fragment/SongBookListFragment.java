package org.kprsongs.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.kprsongs.dao.SongBookDao;
import org.kprsongs.domain.SongBook;
import org.kprsongs.service.SongBookListAdapterService;
import org.kprsongs.utils.CommonUtils;
import org.kprsongs.glorytogod.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by K Purushotham Reddy on 5/17/2015.
 */
public class SongBookListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SongBookDao songBookDao;
    private List<SongBook> songBooks;
    private List<String> songBookNames = new ArrayList<String>();
    private SongBookListAdapterService adapterService = new SongBookListAdapterService();
    private ArrayAdapter<String> adapter;
    // private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetUp();
    }

    private void initSetUp() {
        songBookDao = new SongBookDao(getActivity());
        songBookDao.open();
        loadSongBooks();
    }

    private void loadSongBooks() {
        songBooks = songBookDao.findAll();
        for (SongBook songBook : songBooks) {
            if (!songBook.getName().equals(null)) {
                songBookNames.add(songBook.getName());
            }
        }
//        adapter = adapterService.getSongBookListAdapter(songBookNames, getFragmentManager());
//        setListAdapter(adapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        ImageView image = (ImageView) searchView.findViewById(R.id.search_close_btn);
        Drawable drawable = image.getDrawable();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter = adapterService.getSongBookListAdapter(getFilteredSongbook(query), getFragmentManager());
                setListAdapter(adapterService.getSongBookListAdapter(getFilteredSongbook(query), getFragmentManager()));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter = adapterService.getSongBookListAdapter(getFilteredSongbook(query), getFragmentManager());
                setListAdapter(adapterService.getSongBookListAdapter(getFilteredSongbook(query), getFragmentManager()));
                return true;

            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<String> getFilteredSongbook(String text) {
        List<String> filteredSongs = new ArrayList<String>();
        for (String songBookName : songBookNames) {
            if (songBookName.toLowerCase().contains(text.toLowerCase())) {
                filteredSongs.add(songBookName);
            }
        }
        return filteredSongs;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(this.getClass().getSimpleName(), "Is visible to user ?" + isVisibleToUser);
        if (isVisibleToUser) {
            setListAdapter(adapterService.getSongBookListAdapter(songBookNames, getFragmentManager()));
            CommonUtils.hideKeyboard(getActivity());
        }
    }

    @Override
    public void onRefresh() {
    }
}
