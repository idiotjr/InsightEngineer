package com.zeninstudios.insightengineer;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


import java.io.File;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.google.android.gms.internal.zzt.TAG;

import static android.util.Config.DEBUG;

public class Dashboard extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    public SharedPreferences mSharedPreferences;
    private String[] audioList = {"sub01_chapter_1", "sub01_chapter_2", "sub01_chapter_3", "sub02_chapter_1", "sub02_chapter_2"
            , "sub02_chapter_3", "sub03_chapter_1", "sub03_chapter_3", "sub03_chapter_3"};
    private String[] audioLinkList = {"https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub01_chapter_1.mp3?alt=media&token=0bb47acb-1ee0-4981-8ac8-2ea660b0a556",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub01_chapter_2.mp3?alt=media&token=32b40547-8f6d-4028-bd95-e0c75885f975",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub01_chapter_3.mp3?alt=media&token=11ac5c6a-ab7d-4ea7-bdb5-4d878a9055f6",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub02_chapter_1.mp3?alt=media&token=9b0f3e31-5e94-4bf0-8d49-a7ba0417404c",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub02_chapter_2.mp3?alt=media&token=00266149-f3fe-46ca-88d9-e3d82fb7b7ba",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub02_chapter_3.mp3?alt=media&token=faac9c37-fee0-4428-903f-cb9f16197df8",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub03_chapter_1.mp3?alt=media&token=b1518086-804d-4a2c-a7e4-255fcf5583a6",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub03_chapter_2.mp3?alt=media&token=fd76c1c5-0799-45cb-ae95-d5abca079420",
            "https://firebasestorage.googleapis.com/v0/b/insight-engineer.appspot.com/o/sub03_chapter_3.mp3?alt=media&token=14db4d32-bc1b-4d88-9486-85ebd6b4a91f"};

    public  void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {

                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {

                ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = (new TabsPagerAdapter(getSupportFragmentManager()));

        mSharedPreferences = getSharedPreferences("FUGENIZ_PREFERENCES", MODE_PRIVATE);

        isStoragePermissionGranted();
        mViewPager.setAdapter(mAdapter);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        textToSpeech.stop();
        textToSpeech.speak("Welcome to insight engineeer. Tap for more info", TextToSpeech.QUEUE_FLUSH, null);
        if(!mSharedPreferences.getBoolean("STORAGE_PERMISSION",false)) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/InsightEngineer/AudioLectures/");

            if (!direct.exists()) {
                direct.mkdirs();
            }
            textToSpeech.speak("Downloading Necessary files. please wait.", TextToSpeech.QUEUE_FLUSH, null);
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            for(int i = 0; i<audioList.length; i++) {
                DownloadFromUrl(audioLinkList[i], audioList[i]);
            }
        }
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        textToSpeech.speak("HOME", TextToSpeech.QUEUE_FLUSH, null);
                        break;
                    case 1:
                        textToSpeech.speak("Subject English", TextToSpeech.QUEUE_FLUSH, null);
                        break;
                    case 2:
                        textToSpeech.speak("Subject Civil", TextToSpeech.QUEUE_FLUSH, null);
                        break;
                    case 3:
                        textToSpeech.speak("Subject Computer", TextToSpeech.QUEUE_FLUSH, null);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    public void DownloadFromUrl(final String url, final String name){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory()
                        + "/InsightEngineer/AudioLectures/", name + ".mp3" );
                if (!file.exists()) {
                    File direct = new File(Environment.getExternalStorageDirectory()
                            + "/InsightEngineer/AudioLectures/");

                    if (!direct.exists()) {
                        direct.mkdirs();
                    }

                    DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri downloadUri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(
                            downloadUri);

                    request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI
                                    | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(true)
                            .setVisibleInDownloadsUi(false)
                            .setDestinationInExternalPublicDir("/InsightEngineer/AudioLectures/", name + ".mp3");

                    mgr.enqueue(request);
                }
            }
        });
    }
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            textToSpeech.speak("Downloaded all files. you may now proceed to use the app. press and hold to get instructions", TextToSpeech.QUEUE_FLUSH, null);
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
        textToSpeech.stop();

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSharedPreferences.edit().putBoolean("STORAGE_PERMISSION",true).apply();
                } else {
                    textToSpeech.speak("You have denied permission. Grant requied permissions in settings to use the app properly", TextToSpeech.QUEUE_FLUSH, null);
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = (new TabsPagerAdapter(getSupportFragmentManager()));

        mViewPager.setAdapter(mAdapter);
    }
}
