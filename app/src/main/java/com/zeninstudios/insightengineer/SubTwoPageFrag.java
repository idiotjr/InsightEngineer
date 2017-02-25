package com.zeninstudios.insightengineer;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by vaisa on 2/23/2017.
 */

public class SubTwoPageFrag extends Fragment{

    static TextToSpeech textToSpeech;
    public static VerticalViewPager verticalViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Typeface custFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AmaticSC-Bold.ttf");

        View rootView = inflater.inflate(R.layout.frag_sub, container, false);
        ((TextView)rootView.findViewById(R.id.sub_name)).setText("Civil");
        ((TextView)rootView.findViewById(R.id.sub_name)).setTypeface(custFont);
        verticalViewPager = (VerticalViewPager) rootView.findViewById(R.id.sub_one_pager);
        verticalViewPager.setVisibility(View.GONE);

        rootView.findViewById(R.id.sub_right_bot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });

        textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        rootView.findViewById(R.id.sub_right_bot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak("Civil chapters will be added soon. this is a sample prototype of the app", TextToSpeech.QUEUE_FLUSH, null);
        }
        });

        return rootView;
    }

}
