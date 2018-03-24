package org.kprsongs.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.kprsongs.SongsApplication;
import org.kprsongs.activity.SongListActivity;
import org.kprsongs.dao.SongBookDao;
import org.kprsongs.dao.SongDao;
import org.kprsongs.domain.Song;
import org.kprsongs.domain.SongBook;
import org.kprsongs.admin.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by K Purushotham Reddy on 5/17/2015.
 */
public class SongBookListAdapterService {

    List<String> songName;
    private SongsApplication application = new SongsApplication();
    private SongBookDao songBookDao = new SongBookDao(application.getContext());
    private List<Song> songs;
    private SongDao songDao = new SongDao(application.getContext());

    public ArrayAdapter<String> getSongBookListAdapter(final List<String> songBookNames, final FragmentManager fragmentManager) {
        return new ArrayAdapter<String>(application.getContext(), R.layout.songs_listview_content, songBookNames) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) application.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.listview_content, parent, false);
                final TextView textView = (TextView) rowView.findViewById(R.id.listTextView);
                textView.setText(songBookNames.get(position));

                (rowView.findViewById(R.id.listTextView)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        songs = new ArrayList<Song>();
                        songName = new ArrayList<String>();
                        String bookName = textView.getText().toString();
                        SongBook selectedBook = songBookDao.findBookByName(bookName);
                        songs = songDao.getSongTitlesByBookId(selectedBook.getId());
                        for (Song song : songs) {
                            songName.add(song.getTitle());
                        }
                        Collections.sort(songName);
                        Intent intent = new Intent(application.getContext(), SongListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("title", selectedBook.getName());
                        intent.putStringArrayListExtra("songNames", new ArrayList<String>(songName));
                        application.getContext().startActivity(intent);
                    }
                });
                return rowView;
            }
        };
    }
}
