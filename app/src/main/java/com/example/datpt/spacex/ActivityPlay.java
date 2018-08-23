package com.example.datpt.spacex;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.datpt.spacex.adapter.PlayAdapter;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.Song;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;

public class ActivityPlay extends AppCompatActivity implements InterfaceSongClickCustom {

    private DatabaseReference mData;
    private FirebaseDatabase mFirebaseDatabase;
    private RecyclerView recyclerView;
    private PlayAdapter playAdapter;
    private ArrayList<Song> arrayList;
    private LinearLayout mPlaySongLayout;
    private static String mSinger = "BichPhuong";

    boolean isShuffle = false;
    boolean isRepeat = false;
    boolean paused = true;
    TextView tv_tenbai, tv_duration;
    ImageButton btn_play_pause, btn_forward, btn_rewind, btn_repeat, btn_shuffle;

    SeekBar seekBar;
    MediaPlayer mediaPlayer = new MediaPlayer();

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        seekBar = (SeekBar) findViewById(R.id.seekbar);

        viewControl();
        Intent intent = getIntent();
        mSinger = intent.getStringExtra("name");

        mPlaySongLayout = (LinearLayout) findViewById(R.id.song_play_layout);
        mPlaySongLayout.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mData = mFirebaseDatabase.getReference("Song").child(mSinger);
        showData();

        playAdapter = new PlayAdapter(this, arrayList, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(playAdapter);

    }

    public void viewControl() {
        tv_tenbai = (TextView) findViewById(R.id.tv_baihat);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        btn_play_pause = (ImageButton) findViewById(R.id.btn_play_pause);
        btn_play_pause.setImageResource(R.drawable.play);
        btn_forward = (ImageButton) findViewById(R.id.btn_forward);
        btn_rewind = (ImageButton) findViewById(R.id.btn_rewind);
        btn_repeat = (ImageButton) findViewById(R.id.btn_repeat);
        btn_shuffle = (ImageButton) findViewById(R.id.btn_shuffle);
    }

    public String milliSecondsToTimer(long milliseconds) {

        String finalTimerString = "";
        String secondsString;
        //Convert total duration into time
        int hour = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // add hours if here

        if (hour > 0) {
            finalTimerString = hour + ":";
        }
        //prepening 0 to second if it is one digit

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return time String
        return finalTimerString;

    }

    private void showData() {
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Song song = dataSnapshot.getValue(Song.class);
                Log.d("SONG", String.valueOf(song.getUrlSong()));
                arrayList.add(song);
                Log.d("SONG", String.valueOf(arrayList.size()));
                playAdapter.notifyDataSetChanged();

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSongClick(int position) {
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        mPlaySongLayout.setVisibility(View.VISIBLE);
        // get position cua item, co url va chay file.
        // sau khi click vao item  khac se giai phong url cu va update url moi vao
        // mediaPlayer.release();


        String tenbaihat = String.valueOf(arrayList.get(position).getName());
        String urlSong = String.valueOf(arrayList.get(position).getUrlSong());


        Intent intent = new Intent(this, ServicePlayBackgound.class);
        intent.putExtra("url", urlSong);
        this.startService(intent);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(urlSong);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_tenbai.setText(tenbaihat);
        final String duration = milliSecondsToTimer(mediaPlayer.getDuration());
        tv_duration.setText(duration);
        mediaPlayer.start();

        controlMusic();
    }

    // control musicplayer
    public void controlMusic() {
        final int seekForwardTime = 5 * 1000; // default 5 second
        final int seekBackwardTime = 5 * 1000; // default 5 second
        //control music
        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_play_pause.setImageResource(R.drawable.pause);
                } else {
                    mediaPlayer.start();
                    btn_play_pause.setImageResource(R.drawable.play);
                }
            }
        });
        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
                        mediaPlayer.seekTo(currentPosition + seekBackwardTime);
                    } else {
                        mediaPlayer.seekTo(mediaPlayer.getDuration());
                    }
                }

            }
        });
        btn_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    if (currentPosition - seekBackwardTime >= 0) {
                        mediaPlayer.seekTo(currentPosition - seekBackwardTime);
                    } else {
                        mediaPlayer.seekTo(0);
                    }
                }
            }
        });
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    btn_repeat.setImageResource(R.drawable.repeat);
                } else {
                    isRepeat = true;
                    isShuffle = false;
                    btn_repeat.setImageResource(R.drawable.repeat);
                    btn_shuffle.setImageResource(R.drawable.shuffle);
                    //reset mediaplayer
                    mediaPlayer.reset();
                }
            }
        });
        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffle) {
                    isShuffle = false;
                    btn_shuffle.setImageResource(R.drawable.shuffle);
                } else {
                    isShuffle = true;
                    isRepeat = false;
                    btn_shuffle.setImageResource(R.drawable.shuffle);
                    btn_repeat.setImageResource(R.drawable.repeat);
                }

            }
        });

        handler = new Handler();
        seekBar.setMax(mediaPlayer.getDuration());
        playCycle();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {

                if (input) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void playCycle() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            if (isFinishing()) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
            if (isFinishing()) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
            }
        }
        mPlaySongLayout.setVisibility(View.GONE);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                finish();
        }


        return super.onOptionsItemSelected(item);
    }*/
}


