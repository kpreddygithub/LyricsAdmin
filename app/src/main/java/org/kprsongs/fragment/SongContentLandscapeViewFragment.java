package org.kprsongs.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.kprsongs.CommonConstants;
import org.kprsongs.service.CustomTagColorService;
import org.kprsongs.service.UserPreferenceSettingService;
import org.kprsongs.glorytogod.R;

/**
 * author:K Purushotham Reddy
 * version:2.1.0
 */
public class SongContentLandscapeViewFragment extends Fragment
{
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = (View) inflater.inflate(R.layout.song_content_landscape_view_fragment, container, false);
        hideStatusBar();
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        Bundle bundle = getArguments();
        String title = bundle.getString(CommonConstants.TITLE_KEY);
        String content = bundle.getString("content");
        String authorName = bundle.getString("authorName");
        String position = bundle.getString("position");
        String size = bundle.getString("size");
        String chord = bundle.getString("chord");
        setContent(content, view);
        setSongTitle(view, title, chord);
        setAuthorName(view, authorName);
        setSongSlide(view, position, size);
        return view;
    }

    private void hideStatusBar()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getActivity().getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void setContent(String content, View view)
    {
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(content);
        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(this.getActivity(), text, textView);
        textView.setTypeface(preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getLandScapeFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }

    private void setSongTitle(View view, String title, String chord)
    {
        TextView songTitleTextView = (TextView) view.findViewById(R.id.song_title);
        songTitleTextView.setText(" " + title + getChord(chord));
    }

    private String getChord(String chord)
    {

        if (chord != null && chord.length() > 0) {
            return " [" + chord + "]";
        }
        return "";
    }

    private void setAuthorName(View view, String authorName)
    {
        TextView authorNameTextView = (TextView) view.findViewById(R.id.author_name);
        authorNameTextView.setText(" " + authorName);
    }

    private void setSongSlide(View view, String position, String size)
    {
        TextView songSlideTextView = (TextView) view.findViewById(R.id.song_slide);
        songSlideTextView.setText(" " + getSongSlideValue(position, size));
    }

    private String getSongSlideValue(String currentPosition, String size)
    {
        int slidePosition = Integer.parseInt(currentPosition) + 1;
        return slidePosition + " of " + size;
    }
}
