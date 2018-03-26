package org.kprsongs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.kprsongs.admin.R;
import org.kprsongs.domain.ContactInfo;
import org.kprsongs.service.CustomTagColorService;
import org.kprsongs.service.UserPreferenceSettingService;

import java.util.List;

/**
 * author: K Purushotham Reddy:
 * version: 2.1.0
 */
public class SongCardViewAdapter extends RecyclerView.Adapter<SongCardViewAdapter.SongContentViewHolder> {
    private List<ContactInfo> contactList;
    private UserPreferenceSettingService preferenceSettingService;
    private CustomTagColorService customTagColorService;
    private List<String> songContent;
    private Context context;

    public SongCardViewAdapter(List<String> songContent, Context context) {
        this.context = context;
        this.songContent = songContent;
    }

    @Override
    public int getItemCount() {
        return songContent.size();
    }

    @Override
    public void onBindViewHolder(SongContentViewHolder songContentViewHolder, int position) {
        String text = songContent.get(position);
        songContentViewHolder.textView.setText(text);
        loadTextStyle(songContentViewHolder.textView);
    }

    private void loadTextStyle(TextView textView) {
        customTagColorService = new CustomTagColorService();
        preferenceSettingService = new UserPreferenceSettingService();
        String text = textView.getText().toString();
        textView.setText("");
        customTagColorService.setCustomTagTextView(context, text, textView);
        textView.setTypeface(preferenceSettingService.getFontStyle());
        textView.setTextSize(preferenceSettingService.getPortraitFontSize());
        textView.setTextColor(preferenceSettingService.getColor());
        textView.setVerticalScrollBarEnabled(true);
    }

    @Override
    public SongContentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        return new SongContentViewHolder(itemView);
    }

    public static class SongContentViewHolder extends RecyclerView.ViewHolder {
        protected EditText textView;

        public SongContentViewHolder(View v) {
            super(v);
            textView = (EditText) v.findViewById(R.id.text);
        }
    }
}
