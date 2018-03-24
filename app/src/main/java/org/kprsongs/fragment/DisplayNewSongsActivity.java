package org.kprsongs.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.kprsongs.dao.SongDao;
import org.kprsongs.domain.FirebaseSong;
import org.kprsongs.domain.Song;
import org.kprsongs.admin.R;
import org.kprsongs.service.SongListAdapterService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by K Purushotham Reddy on 4/11/2017.
 */

public class DisplayNewSongsActivity extends AppCompatActivity {
    private List<Song> songs;
    private ArrayAdapter<Song> adapter;
    private SongListAdapterService adapterService = new SongListAdapterService();
    DatabaseReference database;
    private ListView songListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list_activity);
        songListView = (ListView) findViewById(R.id.song_list_view);

        songs = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference();
        loadDataFromFirebase();

        adapter = adapterService.getNewSongListAdapter(songs, getSupportFragmentManager());
        songListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadDataFromFirebase() {
        database.child("songs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    FirebaseSong firebaseSong = noteDataSnapshot.getValue(FirebaseSong.class);
                    Log.e("lyrics", "firebaseSong:" + firebaseSong);
                    Song song = new Song();
                    song.setTitle(firebaseSong.getTitle());
                    song.setAlternateTitle(firebaseSong.getAlternateTitle());
                    song.setSongNumber(firebaseSong.getSongNumber());
                    song.setLyrics(firebaseSong.getLyrics());
                    song.setSearchTitle(firebaseSong.getSearchTitle());
                    song.setSearchLyrics(firebaseSong.getSearchLyrics());
                    song.setComments(firebaseSong.getComments());
                    songs.add(song);
                }
                adapter = adapterService.getNewSongListAdapter(songs, getSupportFragmentManager());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}