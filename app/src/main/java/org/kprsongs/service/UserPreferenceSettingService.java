package org.kprsongs.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import org.kprsongs.CommonConstants;
import org.kprsongs.SongsApplication;

import java.util.Map;

/**
 * Author: K Purushotham Reddy
 * Version: 1.0.0
 */
public class UserPreferenceSettingService
{

    private Context context = SongsApplication.getContext();
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    private Typeface fontFaceStyle;
    private float fontSize;
    private Typeface fontStyle;
    private String color;

    public UserPreferenceSettingService()
    {
    }

    public float getPortraitFontSize()
    {
        return sharedPreferences.getInt(CommonConstants.PORTRAIT_FONT_SIZE_KEY, 18);
    }

    public float getLandScapeFontSize()
    {
        return sharedPreferences.getInt(CommonConstants.LANDSCAPE_FONT_SIZE_KEY, 25);
    }

    public Typeface getFontStyle()
    {
        String sharedTypeFace = sharedPreferences.getString("fontStyle", "DEFAULT");
        Typeface fontStyle = Typeface.DEFAULT;
        if (sharedTypeFace.equals("DEFAULT_BOLD"))
            fontStyle = Typeface.DEFAULT_BOLD;
        if (sharedTypeFace.equals("MONOSPACE"))
            fontStyle = Typeface.MONOSPACE;
        if (sharedTypeFace.equals("SANS_SERIF"))
            fontStyle = Typeface.SANS_SERIF;
        if (sharedTypeFace.equals("SERIF"))
            fontStyle = Typeface.SERIF;
        return fontStyle;
    }

    public int getColor()
    {
        Map<String, ?> all = sharedPreferences.getAll();
        int color;
        if (all.containsKey("primaryColor")) {
            color = Integer.parseInt(all.get("primaryColor").toString());
        } else {
            color = -12303292;
        }
        return color;
    }

    public Integer getTagColor()
    {
        Map<String, ?> all = sharedPreferences.getAll();
        int color;
        if (all.containsKey("secondaryColor")) {
            color = Integer.parseInt(all.get("secondaryColor").toString());
        } else {
            color = -65536;
        }
        return color;
    }

    public boolean getKeepAwakeStatus()
    {
        return sharedPreferences.getBoolean("prefKeepAwakeOn", false);
    }

    public boolean getPlayVideoStatus()
    {
        return sharedPreferences.getBoolean("prefVideoPlay", true);
    }
}
