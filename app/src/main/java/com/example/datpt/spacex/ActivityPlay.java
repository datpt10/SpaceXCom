package com.example.datpt.spacex;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datpt.spacex.adapter.ActivityPlayAdapter;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.LikeSong;
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

    public static final String NOTIFY_PREVIOUS = "com.example.datpt.spacex.previous";
    public static final String NOTIFY_PAUSE = "com.example.datpt.spacex.pause";
    public static final String NOTIFY_NEXT = "com.example.datpt.spacex.next";


    private static final int NOTIFICATION_ID_CUSTOM_BIG = 9;

    private String nameBH, singer, urlSong;
    int pos = 0;

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
    private boolean isFavorite = false;
    private boolean paused = true;
    private boolean isService = false;
    private ServicePlayBackgound servicePlayBackgound;
    private Intent intent;

    private TextView tv_tenbai, tv_duration, tv_SingerMp3;

    private ImageButton btn_play_pause, btn_next, btn_previous, btn_favorite, btn_shuffle;
    private CircularImageView img_play_view;

    CircularSeekBar seekBar;

    //playmp3show
    LinearLayout mPlayMp3Show;
    TextView tv_nameBH_show, tv_singer_show;
    ImageButton btn_previous_show, btn_play_pause_show, btn_next_show;

    MediaPlayer mediaPlayer = new MediaPlayer();

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

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        showData();
    }

    public void viewControl() {

        //playmp3show

        mPlayMp3Show = findViewById(R.id.li_mp3_show);
        tv_nameBH_show = findViewById(R.id.tv_nameSong_show);
        tv_singer_show = findViewById(R.id.tv_singer_show);
        btn_previous_show = findViewById(R.id.img_previous_show);
        btn_play_pause_show = findViewById(R.id.img_play_pause_show);
        btn_next_show = findViewById(R.id.img_next_show);

        //main and playmp3
        mLayout = findViewById(R.id.activityPlay);

        seekBar = findViewById(R.id.seekbar);
        mPlayMp3 = findViewById(R.id.song_play_layout);
        img_play_view = findViewById(R.id.img_play_view);
        recyclerView = findViewById(R.id.recycler_view);
        tv_tenbai = findViewById(R.id.tv_baihat);
        tv_SingerMp3 = findViewById(R.id.tv_SingerMp3);
        tv_duration = findViewById(R.id.tv_duration);
        btn_play_pause = findViewById(R.id.btn_play_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_previous = findViewById(R.id.btn_previous);
        btn_favorite = findViewById(R.id.btn_favorite);
        btn_shuffle = findViewById(R.id.btn_shuffle);
    }

    private void showData() {
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Song song = dataSnapshot.getValue(Song.class);
                arrayList.add(song);
                playAdapter.notifyDataSetChanged();
                Log.d("SONG", String.valueOf(arrayList.size()));
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
    public void onSongClick(final int position) {

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        // get position cua item, co url va chay file.
        // sau khi click vao item  khac se giai phong url cu va update url moi vao
        // mediaPlayer.release();
        String name = String.valueOf(arrayList.get(position).getName());
        final String sing = String.valueOf(arrayList.get(position).getSinger());
        String url = String.valueOf(arrayList.get(position).getUrlSong());

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder iBinder) {
                ServicePlayBackgound.MusicBinder binder = (ServicePlayBackgound.MusicBinder) iBinder;
                servicePlayBackgound = binder.getService();
                servicePlayBackgound.setSong(pos);
                servicePlayBackgound.setList(arrayList);
                servicePlayBackgound.getUrl(urlSong);

                isService = true;
                Toast.makeText(getApplicationContext(), "onServiceConnected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

                isService = false;
            }
        };
        pos = position;
        nameBH = name;
        singer = sing;
        urlSong = url;

        intent = new Intent(this, ServicePlayBackgound.class);
        intent.putExtra("pos", pos);
        intent.putExtra("nameBH", nameBH);
        intent.putExtra("singer", singer);
        intent.putExtra("urlSong", urlSong);

        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        notification();

        tv_singer_show.setText(singer);
        tv_SingerMp3.setText(singer);
        tv_tenbai.setText(nameBH);
        tv_nameBH_show.setText(nameBH);
//        String duration = milliSecondsToTimer(mediaPlayer.getDuration());
//        tv_duration.setText(duration);
        btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        btn_play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);

        controlMusic();
    }

    @SuppressLint("RestrictedApi")
    private void notification() {

        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.nofication);
        NotificationCompat.Builder nc = new NotificationCompat.Builder(this);
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(mChannel);
        }
        Intent nofifyIntent = new Intent(this, ActivityPlay.class);

        nofifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nofifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        nc.setContentIntent(pendingIntent);
        nc.setSmallIcon(R.drawable.ic_featured_play_list_black_24dp);
        nc.setAutoCancel(true);
        nc.setCustomBigContentView(expandedView);
        nc.setContentTitle(nameBH);
        nc.setContentText(singer);
        nc.setChannelId(id);
        nc.getBigContentView().setTextViewText(R.id.tv_nameBH_noti, nameBH);
        nc.getBigContentView().setTextViewText(R.id.tv_singer_noti, singer);

        setListeners(expandedView, this);
        nm.notify(NOTIFICATION_ID_CUSTOM_BIG, nc.build());
    }

    private static void setListeners(RemoteViews view, Context context) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);

        PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_previous_noti, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_play_pause_noti, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_next_noti, pPause);
    }
    // control musicplayer

    public void controlMusic() {
     //   servicePlayBackgound.onCompletion(mediaPlayer);
        //control music
        btn_play_pause_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servicePlayBackgound.isPlaying()) {
                    servicePlayBackgound.pause();
                    btn_play_pause_show.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_play_circle_filled_black_60dp);
                } else {
                    servicePlayBackgound.start();
                    btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);
                    playCycle();
                }
            }
        });
        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servicePlayBackgound.isPlaying()) {
                    servicePlayBackgound.pause();
                    btn_play_pause_show.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_play_circle_filled_black_60dp);
                } else {
                    servicePlayBackgound.start();
                    btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    btn_play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);
                    playCycle();
                }
            }
        });
        btn_next_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos++;
                if (pos > arrayList.size() - 1) {
                    pos = 0;
                }
                if (servicePlayBackgound.isPlaying()) {
                    servicePlayBackgound.stop();
                }
                onSongClick(pos);

            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos++;
                if (pos > arrayList.size() - 1) {
                    pos = 0;
                }
                if (servicePlayBackgound.isPlaying()) {
                    servicePlayBackgound.stop();
                }
                onSongClick(pos);
            }
        });

        btn_previous_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos--;
                if (pos < 0) {
                    pos = arrayList.size() - 1;
                }
                if (servicePlayBackgound.isPlaying()) {
                    servicePlayBackgound.stop();
                }
                btn_play_pause_show.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                btn_play_pause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_60dp);
                onSongClick(pos);
            }
        });
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos--;
                if (pos < 0) {
                    pos = arrayList.size() - 1;
                }
                if (servicePlayBackgound.isPlaying()) {
                    servicePlayBackgound.stop();
                }

                onSongClick(pos);
                btn_play_pause_show.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                btn_play_pause.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_60dp);

            }
        });

        mData = mFirebaseDatabase.getReference();
        mData.child("LikeSong");
        final LikeSong likeSong = new LikeSong(nameBH, singer, urlSong);
        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    isFavorite = false;
                    btn_favorite.setImageResource(R.drawable.ic_favorite_border_black_28dp);
                    mData.child("LikeSong").child(nameBH).removeValue();
                } else {
                    isFavorite = true;
                    btn_favorite.setImageResource(R.drawable.ic_favorite_black_28dp);
                    mData.child("LikeSong").child(nameBH).setValue(likeSong);
                }
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
                    pos = random.nextInt((arrayList.size() - 1) + 1);
                    if (servicePlayBackgound.isPlaying()) {
                        servicePlayBackgound.start();
                        onSongClick(pos);
                    }
                }

            }
        });

        handler = new Handler();
//        seekBar.setMax(servicePlayBackgound.getDurration());
        playCycle();

        seekBar.setOnSeekBarChangeListener(new CircleSeekBarListener());
    }

    public void playCycle() {
//        seekBar.setProgress(servicePlayBackgound.getCurrentPosition());

        if (mediaPlayer.isPlaying())
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
        handler.postDelayed(runnable, 1000);
    }

    //
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


