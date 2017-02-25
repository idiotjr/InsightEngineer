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

import static java.lang.String.valueOf;


public class SubOnePageFrag extends Fragment{

    static TextToSpeech textToSpeech;
    public static VerticalViewPager verticalViewPager;
    public static boolean isPlaying = false;
    public static MediaPlayer mp1,mp2,mp3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_sub, container, false);
        Typeface custFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AmaticSC-Bold.ttf");
        ((TextView)rootView.findViewById(R.id.sub_name)).setText("English");
        ((TextView)rootView.findViewById(R.id.sub_name)).setTypeface(custFont);
        verticalViewPager = (VerticalViewPager) rootView.findViewById(R.id.sub_one_pager);
        verticalViewPager.setAdapter(new DummyAdapter(getFragmentManager()));
        final String path = Environment.getExternalStorageDirectory()+"/InsightEngineer/AudioLectures/";
        final String chap11 = "sub01_chapter_1.mp3";
        final String chap12 = "sub01_chapter_2.mp3";
        final String chap13 = "sub01_chapter_3.mp3";
        mp1 = new MediaPlayer();
        mp2 = new MediaPlayer();
        mp3 = new MediaPlayer();
        try {
            mp1.setDataSource(path + File.separator + chap11);
            mp1.prepare();
            mp2.setDataSource(path + File.separator + chap12);
            mp2.prepare();
            mp3.setDataSource(path + File.separator + chap13);
            mp3.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        textToSpeech.stop();
                        textToSpeech.speak("Chapter 1", TextToSpeech.QUEUE_FLUSH, null);
                        stopPlayer(mp2);
                        stopPlayer(mp3);
                        break;
                    case 1:
                        textToSpeech.stop();
                        textToSpeech.speak("Chapter 2", TextToSpeech.QUEUE_FLUSH, null);
                        stopPlayer(mp3);
                        stopPlayer(mp1);
                        break;
                    case 2:
                        textToSpeech.stop();
                        textToSpeech.speak("Chapter 3", TextToSpeech.QUEUE_FLUSH, null);
                        stopPlayer(mp1);
                        stopPlayer(mp2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verticalViewPager.setVisibility(View.VISIBLE);
                textToSpeech.speak("English chapters. Swipe up and down to change chapters. tap once to play current chapter." +
                        " tap again to pause. long press on screen to stop playing. Tap on bottom left to go back to subjects", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        return rootView;
    }
    public class DummyAdapter extends FragmentPagerAdapter {

        public DummyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragmentThree (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "PAGE 1";
                case 1:
                    return "PAGE 2";
                case 2:
                    return "PAGE 3";
            }
            return null;
        }

    }
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.chapter_layout, container, false);
            Typeface custFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AmaticSC-Bold.ttf");
            TextView textView = (TextView) rootView.findViewById(R.id.textview);
            textView.setTypeface(custFont);
            textView.setText("Chapter "+Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            ImageView goBack = (ImageView)rootView.findViewById(R.id.chap_left_bot);
            final String path = Environment.getExternalStorageDirectory()+"/InsightEngineer/AudioLectures/";
            final String fileName = "sub01_chapter_"+getArguments().getInt(ARG_SECTION_NUMBER)+".mp3";
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    audioPlayer(getArguments().getInt(ARG_SECTION_NUMBER));
                }
            });
            goBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verticalViewPager.setVisibility(View.GONE);
                    textToSpeech.speak("Back to subject"+getArguments().getInt(ARG_SECTION_NUMBER), TextToSpeech.QUEUE_FLUSH, null);
                    stopPlayer(mp1);
                    stopPlayer(mp2);
                    stopPlayer(mp2);
                }
            });
            rootView.findViewById(R.id.chap_right_bot).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopPlayer(mp1);
                    stopPlayer(mp2);
                    stopPlayer(mp2);
                    getActivity().recreate();
                }
            });
            return rootView;
        }
    }

    public static void audioPlayer(int chapCode){
        textToSpeech.stop();
        switch (chapCode) {
            case 1:
                if(mp1.isPlaying())
                    mp1.pause();
                else if(mp2.isPlaying() || mp3.isPlaying()) {
                    stopPlayer(mp2);
                    stopPlayer(mp3);
                    mp1.start();
                } else
                    mp1.start();
                break;
            case 2:
                if(mp2.isPlaying())
                    mp2.pause();
                else if(mp1.isPlaying() || mp3.isPlaying()) {
                    stopPlayer(mp1);
                    stopPlayer(mp3);
                    mp2.start();
                } else
                    mp2.start();
                break;
            case 3:
                if(mp3.isPlaying())
                    mp3.pause();
                else if(mp2.isPlaying() || mp1.isPlaying()) {
                    stopPlayer(mp2);
                    stopPlayer(mp1);
                    mp1.start();
                } else
                    mp3.start();
                break;
        }
    }
    private static void stopPlayer(MediaPlayer mp) {
        if(mp.isPlaying())
            mp.stop();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("MyFragment", "Not visible anymore.  Stopping audio.");
                stopPlayer(mp1);
                stopPlayer(mp2);
                stopPlayer(mp3);
                verticalViewPager.setVisibility(View.GONE);
                verticalViewPager.setAdapter(new DummyAdapter(getFragmentManager()));
                textToSpeech.stop();
            }
        }
    }
}
