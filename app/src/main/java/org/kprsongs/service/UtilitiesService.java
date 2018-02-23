package org.kprsongs.service;

import android.util.Log;

import org.kprsongs.SongsApplication;
import org.kprsongs.domain.Verse;
import org.kprsongs.parser.VerseParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by K Purushotham Reddy on 5/9/2015.
 */
public class UtilitiesService implements IUtilitiesService {

    private SongsApplication application = new SongsApplication();
    private VerseParser verseparser = new VerseParser();

    @Override
    public List<Verse> getVerse(String lyrics) {
        return verseparser.parseVerseDom(application.getContext(), lyrics);
    }

    @Override
    public List<String> getVerseByVerseOrder(String verseOrder) {
        String split[] = verseOrder.split("\\s+");
        List<String> verses = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            verses.add(split[i].toLowerCase());
        }
        Log.d("Verses list: ", verses.toString());
        return verses;
    }
}
