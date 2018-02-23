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
import org.kprsongs.dao.SongDao;
import org.kprsongs.domain.Setting;
import org.kprsongs.domain.Song;
import org.kprsongs.service.SongListAdapterService;
import org.kprsongs.service.UtilitiesService;
import org.kprsongs.service.UserPreferenceSettingService;
import org.kprsongs.glorytogod.R;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = (View) inflater.inflate(R.layout.song_content_portrait_view, container, false);
        showStatusBar();
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.content_recycle_view);
        recList.setHasFixedSize(true);
        preferenceSettingService = new UserPreferenceSettingService();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(linearLayoutManager);
        Bundle bundle = getArguments();
        title = bundle.getString(CommonConstants.TITLE_KEY);
        tilteList = bundle.getStringArrayList(CommonConstants.TITLE_LIST_KEY);

        if (bundle != null) {
            millis = bundle.getInt(KEY_VIDEO_TIME);
            Log.i(this.getClass().getSimpleName(), "Video time " + millis);
        }
        Song song = songDao.findContentsByTitle(title);
        songCarViewAdapter = new SongCardViewAdapter(song.getContents(), this.getActivity());
        songCarViewAdapter.notifyDataSetChanged();
        recList.setAdapter(songCarViewAdapter);
//         setYouTubeView(view);
        setOptionsImageView(view, song.getContents());
        setTitleTextView(view);
        setFloatingButton(view, song.getUrlKey());
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
//    private void setYouTubeView(View view)
//    {
//        mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
//        mYoutubePlayerFragment.initialize("AIzaSyB7hLcRMs5KPZwElJnHBPK5DNmDqFxVy3s", this);
//        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_youtube_player);
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_youtube_player, mYoutubePlayerFragment);
//        fragmentTransaction.commit();
//    }

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
        if (!wasRestored) {
            youTubePlayer.loadVideo("yKc-ey5pnNo");
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
}