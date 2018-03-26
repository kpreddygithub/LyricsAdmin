package org.kprsongs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kprsongs.admin.R;
import org.kprsongs.dao.SongDao;
import org.kprsongs.domain.FirebaseSong;
import org.kprsongs.domain.Song;
import org.kprsongs.domain.Verse;
import org.kprsongs.service.UtilitiesService;

import java.io.IOException;
import java.util.List;

/**
 * Created by Purushotam on 4/10/2017.
 */

public class AddSongFragment extends Fragment {

    private Button submit_song;
    private EditText pallavi_et;
    private EditText first_charanam_et;
    private EditText second_charanam_et;
    private EditText third_charanam_et;
    private EditText youtube_et, search_title_et, search_lyrics_et;
    private String entireSong, title, alternate_title, search_title, search_lyrics;
    private Button export_db;
    private SongDao songDao;
    private DatabaseReference database;
    private List<Song> songs;
    private UtilitiesService utilitiesService = new UtilitiesService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_song, container, false);
        pallavi_et = (EditText) rootView.findViewById(R.id.pallavi_et);
        first_charanam_et = (EditText) rootView.findViewById(R.id.first_charanam_et);
        second_charanam_et = (EditText) rootView.findViewById(R.id.second_charanam_et);
        third_charanam_et = (EditText) rootView.findViewById(R.id.third_charanam_et);
        youtube_et = (EditText) rootView.findViewById(R.id.youtube_et);
        search_title_et = (EditText) rootView.findViewById(R.id.search_title_et);
        search_lyrics_et = (EditText) rootView.findViewById(R.id.search_lyrics_et);
        submit_song = (Button) rootView.findViewById(R.id.submit_song);
        export_db = (Button) rootView.findViewById(R.id.export_db);

        database = FirebaseDatabase.getInstance().getReference();
        songDao = new SongDao(getActivity());

        songs = songDao.findAll();

//        exportToFireBase();

        submit_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String pallavi = pallavi_et.getText().toString();
                String first_charanam = first_charanam_et.getText().toString();
                String second_charanam = second_charanam_et.getText().toString();
                String third_charanam = third_charanam_et.getText().toString();
                String youtubeLink = youtube_et.getText().toString();
                String searchTitle = search_title_et.getText().toString();
                String searchLyrics = search_lyrics_et.getText().toString();

                if (third_charanam.isEmpty()) {
                    entireSong = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                            "<song version=\"1.0\"><lyrics><verse type=\"v\" label=\"1\"><![CDATA[ప: " + pallavi +
                            "]]></verse><verse type=\"v\" label=\"2\"><![CDATA[1.\n" +
                            "\n" + first_charanam +
                            "]]></verse><verse type=\"v\" label=\"3\"><![CDATA[2. \n" +
                            "\n" + second_charanam +
                            " ]]></verse></lyrics></song>";

                } else {

                    entireSong = "<?xml version='1.0' encoding='UTF-8'?><song version=\"1.0\"><lyrics><verse type=\"v\" label=\"1\"><![CDATA[ప:" + pallavi +
                            "]]></verse><verse type=\"v\" label=\"2\"><![CDATA[\n" +
                            "1 \n" + first_charanam +
                            "]]></verse><verse type=\"v\" label=\"3\"><![CDATA[\n" +
                            "2 \n" + second_charanam +
                            "]]></verse><verse type=\"v\" label=\"4\"><![CDATA[\n" +
                            "3 \n" + third_charanam + " ]]></verse></lyrics></song>\n";
                }


                int second_occurence_of_double_qoutes = pallavi.indexOf(" ", pallavi.indexOf(" ") + 1);
                if (second_occurence_of_double_qoutes == -1) {
                    title = "";
                } else {

                    title = pallavi.substring(0, second_occurence_of_double_qoutes);
                }
                alternate_title = title;
                search_title = searchTitle;
                search_lyrics = searchLyrics;

                Log.d("lyrics", "entireSong" + entireSong);
                Log.d("lyrics", "title" + title);
                Log.d("lyrics", "alternate_title" + alternate_title);
                Log.d("lyrics", "search_title" + search_title);
                Log.d("lyrics", "search_lyrics" + search_lyrics);
                Log.d("lyrics", "youtubeLink" + youtubeLink);

                String comments = null;

                if (youtubeLink.isEmpty()) {
                    comments = "mediaUrl=https://www.youtube.com/watch?v=f6_uYhXG138";
                } else {
                    comments = "mediaUrl=" + youtubeLink;
                }

                long insertedRowId = songDao.insertSong(title, entireSong, alternate_title, search_title, search_lyrics, comments);
                if (insertedRowId != -1) {
                    Toast.makeText(getActivity(), "Song added successfully", Toast.LENGTH_SHORT).show();
                    pallavi_et.setText("");
                    first_charanam_et.setText("");
                    second_charanam_et.setText("");
                    third_charanam_et.setText("");
                    youtube_et.setText("");
                }
                Log.d("lyrics", "insertedRowId" + insertedRowId);

                FirebaseSong song = new FirebaseSong();
                song.setSongNumber(database.child("songs").push().getKey());
                song.setId((int) insertedRowId);
                song.setTitle(title);
                song.setLyrics(entireSong);
                song.setAlternateTitle(alternate_title);
                song.setSearchTitle(search_title);
                song.setSearchLyrics(search_lyrics);
                song.setSongBookId(1412);
                song.setComments(comments);

                database.child("songs").child(song.getSongNumber()).setValue(song);

            }
        });

        export_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int bytesCopied = songDao.exportDatabase();
                    Toast.makeText(getActivity(), "database export success" + bytesCopied, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    private void exportToFireBase() {
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            FirebaseSong firebaseSong = new FirebaseSong();
            firebaseSong.setSongNumber(database.child("songs").push().getKey());
            firebaseSong.setId(song.getId());
            firebaseSong.setTitle(song.getTitle());
            firebaseSong.setAlternateTitle(song.getAlternateTitle());
            firebaseSong.setSearchTitle(song.getSearchTitle());
            firebaseSong.setSearchLyrics(song.getSearchLyrics());
            firebaseSong.setSongBookId(1412);
            firebaseSong.setComments(song.getComments());


            String lyrics = song.getLyrics();
            List<Verse> verseList = utilitiesService.getVerse(lyrics);
            JSONArray lyricsArray = new JSONArray();
            for (int k = 0; k < verseList.size(); k++) {
                Verse verse = verseList.get(k);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("content", verse.getContent());
                    jsonObject.put("label", verse.getLabel());
                    jsonObject.put("type", verse.getType());
                    jsonObject.put("order", verse.getVerseOrder());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lyricsArray.put(jsonObject);
            }

            firebaseSong.setLyrics(lyricsArray.toString());
            database.child("songs").child(firebaseSong.getSongNumber()).setValue(firebaseSong);

        }
    }

}
