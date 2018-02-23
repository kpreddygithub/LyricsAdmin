package org.kprsongs.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.kprsongs.SongsApplication;
import org.kprsongs.activity.UserSettingActivity;
import org.kprsongs.picker.ColorPickerPreference;
import org.kprsongs.glorytogod.R;

/**
 * Author:K Purushotham Reddy
 * version:1.0.0
 */
public class SettingsPreferenceFragment extends PreferenceFragment
{
    private Preference resetDialogPreference;
    private Intent startIntent;
    private Context context = SongsApplication.getContext();
    UserSettingActivity userSettingActivity = new UserSettingActivity();


    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        //Initialize Color Picker preference
        colorPickerSettings("primaryColor");
        colorPickerSettings("secondaryColor");
        resetPreferenceSettings("resetDialog");
    }

    public void resetPreferenceSettings(String preferenceKey)
    {
        this.resetDialogPreference = findPreference(preferenceKey);
        this.startIntent = new Intent(context, UserSettingActivity.class);
        //Set the OnPreferenceChangeListener for the resetDialogPreference
        this.resetDialogPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //Close this Activity
                userSettingActivity.activityFinish();
                startActivity(startIntent);
                return false;
            }
        });
    }

    public void colorPickerSettings(String colorPickerKey)
    {
        ColorPickerPreference primaryColorPreference = (ColorPickerPreference) findPreference(colorPickerKey);
        setColorPickerPreferenceValue(colorPickerKey);
        ((ColorPickerPreference) findPreference(colorPickerKey)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                preference.setDefaultValue(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                //preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
                return true;
            }

        });
        ((ColorPickerPreference) findPreference(colorPickerKey)).setAlphaSliderEnabled(true);
    }

    public void setColorPickerPreferenceValue(String colorPickerKey)
    {
        ColorPickerPreference primaryColorPreference = (ColorPickerPreference) findPreference(colorPickerKey);
        int color = primaryColorPreference.getValue();
        //primaryColorPreference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(color))));
    }

}