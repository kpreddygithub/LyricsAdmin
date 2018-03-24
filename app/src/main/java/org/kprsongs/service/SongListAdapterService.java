package org.kprsongs.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.kprsongs.CommonConstants;
import org.kprsongs.SongsApplication;
import org.kprsongs.dao.SongDao;
import org.kprsongs.domain.FirebaseSong;
import org.kprsongs.domain.Setting;
import org.kprsongs.domain.Song;
import org.kprsongs.fragment.AddPlayListsDialogFragment;
import org.kprsongs.activity.CustomYoutubeBoxActivity;
import org.kprsongs.activity.SongContentViewActivity;
import org.kprsongs.domain.Verse;
import org.kprsongs.fragment.ListDialogFragment;
import org.kprsongs.admin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by K Purushotham Reddy on 5/16/2015.
 */
public class SongListAdapterService {

    public PopupWindow popupWindow;
    private SongsApplication application = new SongsApplication();
    private String selectedSong;
    private SongDao songDao = new SongDao(application.getContext());
    private UtilitiesService utilitiesService = new UtilitiesService();
    private List<Verse> verseList;
    private String[] serviceNames;
    private CommonService commonService = new CommonService();
    private ListDialogFragment dialogFragment;
    private DatabaseReference database;

    public ArrayAdapter<Song> getNewSongListAdapter(final List<Song> songs, final FragmentManager fragmentManager) {
        return new ArrayAdapter<Song>(application.getContext(), R.layout.songs_listview_content, songs) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) application.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.songs_listview_content, parent, false);
                final TextView textView = (TextView) rowView.findViewById(R.id.songsTextView);
                textView.setText(songs.get(position).getTitle());

                final ImageView imageView = (ImageView) rowView.findViewById(R.id.optionMenuIcon);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopupMenu(v, String.valueOf(textView.getText()), fragmentManager);
                    }
                });

                (rowView.findViewById(R.id.songsTextView)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        selectedSong = textView.getText().toString();
                        displaySelectedSong(songs, position);
                    }
                });
                return rowView;
            }
        };
    }

    private void showYouTube() {
        Intent lightboxIntent = new Intent(application.getContext(), CustomYoutubeBoxActivity.class);
        lightboxIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, "yKc-ey5pnNo");
        lightboxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.getContext().startActivity(lightboxIntent);
    }

    public void showPopupMenu(View view, final String songName, final FragmentManager fragmentManager) {
        final PopupMenu popupMenu = new PopupMenu(application.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.song_list_option_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addToList:
                        getAddfavouriteDialogFragment(songName, fragmentManager);
                        return true;
                    default:
                        return false;
                }
            }

        });
        popupMenu.show();
    }

    public void ShowSharePopupmenu(View view, final String songName, final FragmentManager fragmentManager, final String content) {
        //Context wrapper = new ContextThemeWrapper(application.getContext(), R.style.PopupMenu);
        final PopupMenu popupMenu = new PopupMenu(application.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.favourite_share_option_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addToList:
                        getAddfavouriteDialogFragment(songName, fragmentManager);
                        return true;
                    case R.id.share_whatsapp:
                        Intent textShareIntent = new Intent(Intent.ACTION_SEND);
                        textShareIntent.putExtra(Intent.EXTRA_TEXT, content);
                        textShareIntent.setType("text/plain");
                        Intent intent = Intent.createChooser(textShareIntent, "Share " + songName + " with...");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        application.getContext().startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void getAddfavouriteDialogFragment(final String songName, final FragmentManager fragmentManager) {
        dialogFragment = new ListDialogFragment() {
            @Override
            public String[] getProductListsArray() {
                List<String> services = new ArrayList<String>();
                services.addAll(commonService.readServiceName());
                services.add(0, "New favourite...");
                Log.d("service names list", commonService.readServiceName().toString());
                serviceNames = new String[services.size()];
                services.toArray(serviceNames);
                Log.d("service names are", services.toString());
                return serviceNames;
            }

            @Override
            protected void onClick(int which) {
                Log.d("Clicked position:", String.valueOf(which));
                if (which == 0) {
                    final AddPlayListsDialogFragment addPlayListsDialog = new AddPlayListsDialogFragment() {
                        @Override
                        public String getSelectedSong() {
                            return songName;
                        }
                    };
                    addPlayListsDialog.show(fragmentManager, "addToListFragment");
                } else {
                    List<String> services = new ArrayList<String>();
                    services.addAll(commonService.readServiceName());
                    serviceNames = new String[services.size()];
                    String[] selectedServiceNames = services.toArray(serviceNames);
                    commonService.saveIntoFile(selectedServiceNames[which - 1].toString(), songName);
                    Toast.makeText(getActivity(), "Song added to favourite...!", Toast.LENGTH_LONG).show();
                }
            }
        };
        dialogFragment.show(fragmentManager, "serviceListFragment");
    }

    public void displaySelectedSong(List<Song> songs, int position) {
        Intent intent = new Intent(application.getContext(), SongContentViewActivity.class);
        ArrayList<String> songList = new ArrayList<String>();
        for (Song song : songs) {
            songList.add(song.getTitle());
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(CommonConstants.TITLE_LIST_KEY, songList);
        Setting.getInstance().setPosition(position);
        //bundle.putInt(CommonConstants.POSITION_KEY, position);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.getContext().startActivity(intent);

    }

}
