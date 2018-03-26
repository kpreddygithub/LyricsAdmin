package org.kprsongs.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.kprsongs.CommonConstants;
import org.kprsongs.SongsApplication;
import org.kprsongs.activity.CustomYoutubeBoxActivity;
import org.kprsongs.adapter.SongCardViewAdapter;
import org.kprsongs.admin.R;
import org.kprsongs.dao.SongDao;
import org.kprsongs.domain.Setting;
import org.kprsongs.domain.Song;
import org.kprsongs.service.SongListAdapterService;
import org.kprsongs.service.UserPreferenceSettingService;
import org.kprsongs.service.UtilitiesService;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: K Purushotham Reddy
 * version: 1.0.0
 */
public class SongContentPortraitViewFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
    private UserPreferenceSettingService preferenceSettingService;
    private SongCardViewAdapter songCarViewAdapter;
    private SongsApplication application = new SongsApplication();
    private SongDao songDao = new SongDao(application.getContext());
    private UtilitiesService utilitiesService = new UtilitiesService();
    private String title;
    private ArrayList<String> tilteList;
    private YouTubePlayerSupportFragment mYoutubePlayerFragment;
    private YouTubePlayer mPlayer;
    private boolean isFullscreen;
    private boolean playVideoStatus;
    private FrameLayout frameLayout;
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";
    private int millis;
    private SongListAdapterService songListAdapterService;
    private String mVideoId;
    private FrameLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private View mCustomView;
    private Song song;
    private EditText commentsEt;
    private RecyclerView recyclerView;
    private EditText search_lyrics_et;
    private EditText search_title_et;
    private EditText title_et;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = (View) inflater.inflate(R.layout.song_content_portrait_view, container, false);
        showStatusBar();
        recyclerView = (RecyclerView) view.findViewById(R.id.content_recycle_view);
        recyclerView.setHasFixedSize(true);
        preferenceSettingService = new UserPreferenceSettingService();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        Bundle bundle = getArguments();
        title = bundle.getString(CommonConstants.TITLE_KEY);
        tilteList = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY);

        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
        }
        song = songDao.findContentsByTitle(title);
        songCarViewAdapter = new SongCardViewAdapter(song.getContents(), this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(songCarViewAdapter);
        setOptionsImageView(view, song.getContents());
        setTitleTextView(view);
        mVideoId = song.getUrlKey();
        setFloatingButton(view, song.getUrlKey());
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_webview_player);
        if (mVideoId != null && mVideoId.length() > 0) {
            setYouTubeWebView(view, song.getUrlKey());
            frameLayout.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.GONE);
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(this.getClass().getSimpleName(), "Position " + tilteList.indexOf(title));
                int position = tilteList.indexOf(title);
                Setting.getInstance().setPosition(position);
                return true;
            }
        });
        Log.i(this.getClass().getSimpleName(), "Video status:" + playVideoStatus);
        return view;
    }

    private void setTitleTextView(View view) {
        TextView textView = (TextView) view.findViewById(R.id.song_title);
        textView.setText(title);
    }

    private void setOptionsImageView(View view, final List<String> contents) {
        ImageView imageView = (ImageView) view.findViewById(R.id.back_navigation);
        ImageView optionMenu = (ImageView) view.findViewById(R.id.optionMenu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder = new StringBuilder();
                builder.append(title).append("\n").append("\n");
                for (String string : contents) {
                    builder.append(string);
                }
                builder.append("\n").append("\n").append(getActivity().getString(R.string.share_info));
                String formattedText = builder.toString().replaceAll("\\{\\w\\}", "").replaceAll("\\{/\\w\\}", "");
                songListAdapterService = new SongListAdapterService();
                songListAdapterService.ShowSharePopupmenu(v, title, getFragmentManager(), formattedText);
            }
        });
    }

    //TODO:To display youtube in same view
    private void setYouTubeView(View view, String urlKey) {
/*
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_youtube_player);
        if (urlKey != null && urlKey.length() > 0) {
            frameLayout.setVisibility(View.VISIBLE);
            mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
            mYoutubePlayerFragment.initialize("AIzaSyCOm7g0J5xtgzDn_oSSa4Si5S6-co4OGhc", this);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_youtube_player, mYoutubePlayerFragment);
            fragmentTransaction.commit();
        } else {
//            frameLayout.setVisibility(View.GONE);
        }
*/

    }


    private void setYouTubeWebView(View view, String urlKey) {
        WebView myWebView = (WebView) view.findViewById(R.id.webView);
        MyWebChromeClient client = new MyWebChromeClient();
        myWebView.setWebChromeClient(client);
//        myWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return false;
//            }
//        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        myWebView.setBackgroundColor(0x00000000);
        myWebView.setKeepScreenOn(true);
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.setVerticalScrollBarEnabled(false);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
//        String html = getHTML();
//        myWebView.loadDataWithBaseURL("", html, mimeType, encoding, "");
        myWebView.loadUrl("https://www.youtube.com/embed/" + mVideoId + "?controls=1&showinfo=0&showsearch=0&modestbranding=1&autoplay=0&fs=0&iv_load_policy=3&nologo=1&loop=1&&playlist=" + mVideoId);
    }


    public String getHTML() {

        String html = "<html>"

                + "<head>"
                + "</head>"
                + "<body style=\"border: 0; padding: 0\">"
                + "<iframe "
                + "type=\"text/html\" "
                + "class=\"youtube-player\" "
                + "width= 100%\""
                + "\" "
                + "height= 95%\""
                + "\" "
                + "src=\"https://www.youtube.com/v/"
                + mVideoId
                + "?controls=0&showinfo=0&showsearch=0&modestbranding=0" +
                "&autoplay=1&fs=1&vq=hd720\" " + "frameborder=\"0\"></iframe>"
                + "</body>"
                + "</html>";

        return html;
    }

    private void showStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getActivity().getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void setFloatingButton(View view, final String urrlKey) {
/*
        playVideoStatus = preferenceSettingService.getPlayVideoStatus();
        FloatingActionButton playSongFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.play_song_fab);
        if (urrlKey != null && urrlKey.length() > 0 && playVideoStatus == true) {
            playSongFloatingActionButton.setVisibility(View.VISIBLE);
            playSongFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showYouTube(urrlKey);
                }
            });
        }
*/
        commentsEt = (EditText) view.findViewById(R.id.comments);
        search_title_et = (EditText) view.findViewById(R.id.search_title_et);
        search_lyrics_et = (EditText) view.findViewById(R.id.search_lyrics_et);
        title_et = (EditText) view.findViewById(R.id.title_et);

        search_title_et.setText(song.getSearchTitle());
        search_lyrics_et.setText(song.getSearchLyrics());
        title_et.setText(song.getTitle());
        if (song.getComments() == null || song.getComments().isEmpty()) {
            commentsEt.setText("mediaUrl=");
        } else {
            commentsEt.setText(song.getComments());
        }
        FloatingActionButton updateFab = (FloatingActionButton) view.findViewById(R.id.update_song_fab);
        updateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLyrics();
            }
        });
    }

    private void updateLyrics() {
        String comments = commentsEt.getText().toString();
        String searchTitle = search_title_et.getText().toString();
        String searchLyrics = search_lyrics_et.getText().toString();
        String title = title_et.getText().toString();

        String entireSong = getEntireSong().toString();
        long insertedRowId = songDao.updateSong(entireSong, comments, searchTitle, searchLyrics, title, song.getId());
        if (insertedRowId != -1) {
            Toast.makeText(getActivity(), "Song updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

/*    private StringBuilder getEntireSong() {
        StringBuilder entireSong = new StringBuilder();
        entireSong.append("<?xml version='1.0' encoding='UTF-8'?>\n")
                .append("<song version=\"1.0\"><lyrics><verse type=\"v\" label=\"1\"><![CDATA[")
                .append(recyclerView.findViewHolderForAdapterPosition(0)).append("]]>");
        for (int i = 1; i < song.getContents().size(); i++) {
            int j = i + 1;
            entireSong.append("</verse><verse type=\"v\" label=\"" + j + "\"><![CDATA[").append(recyclerView.findViewHolderForAdapterPosition(i)).append("]]>");
        }
        entireSong.append("</verse></lyrics></song>");
        return entireSong;
    }*/

    private StringBuilder getEntireSong() {
        StringBuilder entireSong = new StringBuilder();
        RecyclerView.ViewHolder viewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(0);
        if (viewHolderForAdapterPosition == null) {
            entireSong.append("<?xml version='1.0' encoding='UTF-8'?>\n")
                    .append("<song version=\"1.0\"><lyrics><verse type=\"v\" label=\"1\"><![CDATA[")
                    .append(song.getContents().get(0)).append("]]>");
        } else {
            View itemView = viewHolderForAdapterPosition.itemView;
            EditText editText = (EditText) itemView.findViewById(R.id.text);
            entireSong.append("<?xml version='1.0' encoding='UTF-8'?>\n")
                    .append("<song version=\"1.0\"><lyrics><verse type=\"v\" label=\"1\"><![CDATA[")
                    .append(editText.getText()).append("]]>");
        }
        for (int i = 1; i < song.getContents().size(); i++) {
            int j = i + 1;
            RecyclerView.ViewHolder viewHolderForAdapterPosition1 = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolderForAdapterPosition1 == null) {
                entireSong.append("</verse><verse type=\"v\" label=\"" + j + "\"><![CDATA[").append(song.getContents().get(i)).append("]]>");
            } else {
                View itemView1 = viewHolderForAdapterPosition1.itemView;
                EditText editText1 = (EditText) itemView1.findViewById(R.id.text);
                entireSong.append("</verse><verse type=\"v\" label=\"" + j + "\"><![CDATA[").append(editText1.getText()).append("]]>");
            }
        }
        entireSong.append("</verse></lyrics></song>");
        return entireSong;
    }

    private void showYouTube(String urlKey) {
        Log.i(this.getClass().getSimpleName(), "Url key: " + urlKey);
        Intent youTubeIntent = new Intent(getActivity(), CustomYoutubeBoxActivity.class);
        youTubeIntent.putExtra(CustomYoutubeBoxActivity.KEY_VIDEO_ID, urlKey);
        getActivity().startActivity(youTubeIntent);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        mPlayer = youTubePlayer;
        if (mVideoId != null && !wasRestored) {
            youTubePlayer.loadVideo(mVideoId);
        }

        if (wasRestored) {
            youTubePlayer.seekToMillis(millis);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPlayer != null) {
            outState.putInt(KEY_VIDEO_TIME, mPlayer.getCurrentTimeMillis());
            Log.i(this.getClass().getSimpleName(), "Video duration: " + mPlayer.getCurrentTimeMillis());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result.isUserRecoverableError()) {
            result.getErrorDialog(this.getActivity(), 1).show();
        } else {
            Toast.makeText(this.getActivity(),
                    "YouTubePlayer.onInitializationFailure(): " + result.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        private CustomViewCallback mCustomViewCallback;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = (FrameLayout) view.findViewById(R.id.fragment_webview_player);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(getActivity());
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            getActivity().setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                getActivity().setContentView(mContentView);
            }
        }
    }


}