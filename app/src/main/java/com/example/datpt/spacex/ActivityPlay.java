package com.example.datpt.spacex;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.datpt.spacex.adapter.ActivityPlayAdapter;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.Song;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ActivityPlay extends AppCompatActivity implements InterfaceSongClickCustom {

    int position = 0;
    private DatabaseReference mData;
    private FirebaseDatabase mFirebaseDatabase;
    private RecyclerView recyclerView;
    private ActivityPlayAdapter playAdapter;
    private ArrayList<Song> arrayList;
    private static String mSinger = "";

    private LinearLayout mPlayMp3;

    private SlidingUpPanelLayout mLayout;


    //playmp3
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean paused = true;
    private TextView tv_tenbai, tv_duration, tv_SingerMp3;
    private ImageButton btn_play_pause, btn_next, btn_previous, btn_favorite, btn_shuffle;
    private CircularImageView img_play_view;
    CircularSeekBar seekBar;


    //playmp3show
    LinearLayout mPlayMp3Show;
    TextView tv_nameBH_show, tv_singer_show;
    ImageButton btn_previous_show, btn_play_pause_show, btn_next_show;


    MediaPlayer mediaPlayer = new MediaPlayer(); ;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        viewControl();

        Intent intent = getIntent();
        mSinger = intent.getStringExtra("name");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        playAdapter = new ActivityPlayAdapter(this, arrayList, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(playAdapter);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mData = mFirebaseDatabase.getReference("Song").child(mSinger);

        showData();
    }

    public void viewControl() {

        //playmp3show

        mPlayMp3Show = (LinearLayout) findViewById(R.id.li_mp3_show);
        tv_nameBH_show = (TextView) findViewById(R.id.tv_nameSong_show);
        tv_singer_show = (TextView) findViewById(R.id.tv_singer_show);
        btn_previous_show = (ImageButton) findViewById(R.id.img_previous_show);
        btn_play_pause_show = (ImageButton) findViewById(R.id.img_play_pause_show);
        btn_next_show = (ImageButton) findViewById(R.id.img_next_show);


        //main and playmp3
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.activityPlay);

        seekBar = (CircularSeekBar) findViewById(R.id.seekbar);
        mPlayMp3 = (LinearLayout) findViewById(R.id.song_play_layout);
        img_play_view = (CircularImageView) findViewById(R.id.img_play_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tv_tenbai = (TextView) findViewById(R.id.tv_baihat);
        tv_SingerMp3 = (TextView) findViewById(R.id.tv_SingerMp3);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        btn_play_pause = (ImageButton) findViewById(R.id.btn_play_pause);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        btn_previous = (ImageButton) findViewById(R.id.btn_previous);
        btn_favorite = (ImageButton) findViewById(R.id.btn_favorite);
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

    @Override
    public void onSongClick(int position) {

        // get position cua item, co url va chay file.
        // sau khi click vao item  khac se giai phong url cu va update url moi vao
        // mediaPlayer.release();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        String nameBH = String.valueOf(arrayList.get(position).getName());
        String singer = String.valueOf(arrayList.get(position).getSinger());
        String urlSong = String.valueOf(arrayList.get(position).getUrlSong());

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
        tv_singer_show.setText(singer);
        tv_SingerMp3.setText(singer);
        tv_tenbai.setText(nameBH);
        tv_nameBH_show.setText(nameBH);
        String duration = milliSecondsToTimer(mediaPlayer.getDuration());
        tv_duration.setText(duration);
        btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        mediaPlayer.start();

        controlMusic();
    }

    // control musicplayer
    public void controlMusic() {

        final int seekForwardTime = 5 * 1000; // default 5 second
        final int seekBackwardTime = 5 * 1000; // default 5 second

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                for (int i = 0; i < arrayList.size(); i++) {
                    onSongClick(position);
                }
            }
        });
        //control music
        btn_play_pause_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_play_pause_show.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_play_circle_filled_black_60dp);
                } else {
                    mediaPlayer.start();
                    btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);
                    playCycle();
                }
            }
        });
        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_play_pause_show.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_play_circle_filled_black_60dp);
                } else {
                    mediaPlayer.start();
                    btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);
                    playCycle();
                }
            }
        });
//     /*   btn_forward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mediaPlayer != null) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
//                        mediaPlayer.seekTo(currentPosition + seekBackwardTime);
//                    } else {
//                        mediaPlayer.seekTo(mediaPlayer.getDuration());
//                    }
//                }
//
//            }
//        });
//        btn_rewind.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mediaPlayer != null) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    if (currentPosition - seekBackwardTime >= 0) {
//                        mediaPlayer.seekTo(currentPosition - seekBackwardTime);
//                    } else {
//                        mediaPlayer.seekTo(0);
//                    }
//                }
//            }
//        });*/
        btn_next_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position > arrayList.size() - 1) {
                    position = 0;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                onSongClick(position);

            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position > arrayList.size() - 1) {
                    position = 0;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                onSongClick(position);

            }
        });

        btn_previous_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                if (position < 0) {
                    position = arrayList.size() - 1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                btn_play_pause_show.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                btn_play_pause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_60dp);
                onSongClick(position);
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position--;
                if (position < 0) {
                    position = arrayList.size() - 1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                onSongClick(position);
                btn_play_pause_show.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                btn_play_pause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_60dp);

            }
        });

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                String duration = milliSecondsToTimer(mediaPlayer.getDuration());

                if (isShuffle) {
                    isShuffle = false;
                    btn_shuffle.setImageResource(R.drawable.ic_shuffle_black_28dp);
                } else {
                    isShuffle = true;
                    btn_shuffle.setImageResource(R.drawable.ic_shuffle_black_focus_28dp);
                    Random random = new Random();
                    position = random.nextInt((arrayList.size() - 1) + 1);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        onSongClick(position);
                    }
                }

            }
        });


        handler = new Handler();
        seekBar.setMax(mediaPlayer.getDuration());
        playCycle();

        seekBar.setOnSeekBarChangeListener(new CircleSeekBarListener());
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

    public class CircleSeekBarListener implements CircularSeekBar.OnCircularSeekBarChangeListener {
        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean input) {
            if (input) {
                mediaPlayer.seekTo(progress);
            }

        }

        @Override
        public void onStopTrackingTouch(CircularSeekBar seekBar) {

        }

        @Override
        public void onStartTrackingTouch(CircularSeekBar seekBar) {

        }
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}


