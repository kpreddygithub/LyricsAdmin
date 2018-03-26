package org.kprsongs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import org.kprsongs.admin.R;
import org.kprsongs.fragment.AddSongFragment;
import org.kprsongs.fragment.DisplayNewSongsActivity;
import org.kprsongs.fragment.HomeTabFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * author:K Purushotham Reddy
 * version:2.1.0
 */
public class NavigationDrawerActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        this.addSubheader("");
        this.addSection(newSection(getString(R.string.home), R.drawable.ic_library_books_white, new HomeTabFragment()));
        this.addSection(newSection(getString(R.string.settings), R.drawable.ic_settings_white, getFragment(UserSettingActivity.class)));
        this.addSection(newSection(getString(R.string.add_song), android.R.drawable.ic_menu_add, new AddSongFragment()));
        this.addSection(newSection(getString(R.string.new_songs), android.R.drawable.ic_menu_add, getFragment(DisplayNewSongsActivity.class)));
//        this.addSection(newSection(getString(R.string.about), R.drawable.about, new AboutWebViewFragment()));
    }

    @NonNull
    private Fragment getFragment(final Class targetClass) {
        return new Fragment() {
            @Override
            public void onStart() {
                super.onStart();
                Intent intent = new Intent(NavigationDrawerActivity.this, targetClass);
                startActivity(intent);
            }
        };
    }
}
