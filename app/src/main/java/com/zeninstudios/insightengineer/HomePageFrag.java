package com.zeninstudios.insightengineer;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skyfishjy.library.RippleBackground;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

public class HomePageFrag extends Fragment{

    private TextToSpeech textToSpeech;
    private ImageView topLeft, topRight, botLeft, botRight, mRecordBtn;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private int num;
    private boolean isRecording = false;
    private RippleBackground mRippler;
    private final static String LOG_TAG = "RECORD_LOG";
    private StorageReference mStorage;
    private FirebaseUser currUser;
    private DatabaseReference mDatabaseRef, mUIDRef;
    private String currUID;
    private MediaPlayer mp;
    public File file[];
    private int i=0;
    public View rootView;
    private int j;
    private boolean longPress=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.frag_home_page, container, false);

        topLeft = (ImageView)rootView.findViewById(R.id.home_left_top);
        topRight = (ImageView)rootView.findViewById(R.id.home_right_top);
        botLeft = (ImageView)rootView.findViewById(R.id.home_left_bot);
        botRight = (ImageView)rootView.findViewById(R.id.home_right_bot);
        mRecordBtn = (ImageView)rootView.findViewById(R.id.record_btn);
        mRippler = (RippleBackground)rootView.findViewById(R.id.rippler);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mp = new MediaPlayer();

        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();
        Log.v("Recording", "recording_"+ts);
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        mFileName = Environment.getExternalStorageDirectory() + "/InsightEngineer/tmp_recordings/";
        File direct = new File(mFileName);

        if (!direct.exists()) {
            direct.mkdirs();
        }
        Random rand = new Random();
        num = rand.nextInt(20);

        mFileName+= "/recorded_audio"+num+".3gp";

        rootView.findViewById(R.id.record_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!longPress) {
                    if (currUser != null) {
                        currUID = currUser.getUid();
                        Log.v("UID", currUID);
                        if (!isRecording) {
                            startRecording();
                            mRippler.startRippleAnimation();
                            isRecording = true;
                        } else {
                            stopRecording();
                            mRippler.stopRippleAnimation();
                            isRecording = false;
                        }
                    } else {
                        textToSpeech.speak("You are not signed in. Please restart the app and sign in to use this feature", TextToSpeech.QUEUE_FLUSH, null);
                    }
                } else {
                    longPress = false;
                }
            }
        });
        rootView.findViewById(R.id.recording_left_bot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.record_audio).setVisibility(View.GONE);
                textToSpeech.speak("You are now at home", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        rootView.findViewById(R.id.recording_right_bot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });
        rootView.findViewById(R.id.recorded_left_bot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.recorded_files).setVisibility(View.GONE);
                textToSpeech.speak("You are now at home", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        rootView.findViewById(R.id.recorded_right_bot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });
        topLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak("Tap on screen to play a saved recording. Tap again to skip current recording and play the next recording. tap on bottom left to go back", TextToSpeech.QUEUE_FLUSH, null);
                rootView.findViewById(R.id.recorded_files).setVisibility(View.VISIBLE);
            }
        });
        rootView.findViewById(R.id.recorded_files).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().toString()+"/InsightEngineer/RecordedAudio/";
                File f = new File(path);
                file = f.listFiles();
                j = file.length;
                playAudio();
            }
        });
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak("Tap on screen to start recording. tap again to stop. the recording will automatically be saved in your cloud. tap on bottom left to go back", TextToSpeech.QUEUE_FLUSH, null);
                rootView.findViewById(R.id.record_audio).setVisibility(View.VISIBLE);
            }
        });
        botLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        botRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak("You are at home. Press and hold to hear instructions on how to use the app", TextToSpeech.QUEUE_FLUSH, null);
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
                textToSpeech.speak("You are at home. Press and hold to hear instructions on how to use the app", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                textToSpeech.speak("Swipe Left or right to change subjects. tap on screen to load subject chapters. press the bottom right of screen anytime to return to home", TextToSpeech.QUEUE_FLUSH, null);
                longPress = true;
                return false;
            }
        });

        return rootView;
    }

    private void playAudio() {
        if(i == j)
            i = 0;
        String fileName = file[i].getAbsolutePath();
        ((TextView)rootView.findViewById(R.id.file_name)).setText(Uri.parse(fileName).getLastPathSegment());
        if(mp.isPlaying()){
            mp.stop();
            mp.reset();
            playAudio();
        } else {
            try {
                mp.stop();
                mp.reset();
                mp.setDataSource(fileName);
                mp.prepare();
                mp.start();
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playAudio();
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        textToSpeech.speak("Recording finished", TextToSpeech.QUEUE_FLUSH, null);
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void uploadAudio() {
        textToSpeech.speak("Uploading Recorded Audio", TextToSpeech.QUEUE_FLUSH, null);
        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();
        StorageReference filepatth = mStorage.child("RecordedAudio/"+currUID).child("recording_"+ts+".3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        filepatth.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                textToSpeech.speak("Successfully uploaded Recording", TextToSpeech.QUEUE_FLUSH, null);
                mUIDRef = mDatabaseRef.child(currUID);
                mUIDRef.child("recording_"+ts).setValue(taskSnapshot.getDownloadUrl().toString());
                mUIDRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String uri = dataSnapshot.getValue(String.class);
                        DownloadFromUrl(uri, dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textToSpeech.speak("Upload failed. Please make sure u r connected to the internet", TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }
    public void DownloadFromUrl(final String url, final String name){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                File file = new File(Environment.getExternalStorageDirectory()
                        + "/InsightEngineer/RecordedAudio/", name + ".3gp" );
                if (!file.exists()) {
                    File direct = new File(Environment.getExternalStorageDirectory()
                            + "/InsightEngineer/RecordedAudio/");

                    if (!direct.exists()) {
                        direct.mkdirs();
                    }

                    DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri downloadUri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(
                            downloadUri);

                    request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI
                                    | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(true)
                            .setVisibleInDownloadsUi(false)
                            .setDestinationInExternalPublicDir("/InsightEngineer/RecordedAudio/", name + ".3gp");

                    mgr.enqueue(request);
                }
            }
        });
    }
}
