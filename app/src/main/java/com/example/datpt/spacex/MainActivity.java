package com.example.datpt.spacex;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.datpt.spacex.Fragment.HomeFragment;
import com.example.datpt.spacex.Fragment.LikeFragment;
import com.example.datpt.spacex.Fragment.PersonFragment;
import com.example.datpt.spacex.Fragment.PlayMusicFragment;
import com.example.datpt.spacex.adapter.SearchAdapter;
import com.example.datpt.spacex.inter.InterfacePlaylistCustom;
import com.example.datpt.spacex.inter.InterfaceSearchCustom;
import com.example.datpt.spacex.inter.InterfaceSongClickCustom;
import com.example.datpt.spacex.item.LikeSong;
import com.example.datpt.spacex.item.Song;
import com.example.datpt.spacex.item.SongSearch;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements InterfaceSearchCustom, InterfaceSongClickCustom, MediaPlayer.OnCompletionListener, InterfacePlaylistCustom {

    public static final String NOTIFY_PREVIOUS = "com.example.datpt.spacex.previous";
    public static final String NOTIFY_PAUSE_PlAY = "com.example.datpt.spacex.pause.play";
    public static final String NOTIFY_NEXT = "com.example.datpt.spacex.next";
    private static final int NOTIFICATION_ID_CUSTOM_BIG = 9;
    private RecyclerView recyclerView;
    private TextView tv_noData;
    private ArrayList<SongSearch> arrayList;
    private SearchAdapter adapter;
    private SearchView searchView;
    //    private ActionBar toolbar;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mData;
    private FirebaseRecyclerAdapter<SongSearch, SearchAdapter.MyViewHolder> firebaseRecyclerAdapter;
    private String nameBH, singer, urlSong;
    private int pos;
    private ArrayList<Song> songList;
    private Toolbar toolbar;
    private TextView tv_nameBH_show, tv_singer_show;
    private ImageButton btn_previous_show, btn_play_pause_show, btn_next_show;
    private LinearLayout li_mp3_show;
    private RelativeLayout re_showBottomDialog;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler;
    private Runnable runnable;
    private ProgressBar progressBar;
    private BroadcastReceiver receiver;
    //Dialog

    private TextView tv_casi, tv_baihat;
    private ImageButton btn_play_pause, btn_shuffle, btn_previous, btn_next, btn_favorite;
    private SeekBar seekBar;
    private boolean isShuffle = false;
    private boolean isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view();

        arrayList = new ArrayList<>();
        adapter = new SearchAdapter(getApplicationContext(), arrayList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);

        loadFragment(new HomeFragment());
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.home_nav);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mData = mFirebaseDatabase.getReference("Album");
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SongSearch songSearch = dataSnapshot.getValue(SongSearch.class);
                arrayList.add(songSearch);
                adapter.notifyDataSetChanged();
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return true;
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.NOTIFY_PREVIOUS);
        filter.addAction(MainActivity.NOTIFY_PAUSE_PlAY);
        filter.addAction(MainActivity.NOTIFY_NEXT);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(MainActivity.NOTIFY_PREVIOUS)) {
                    pos++;
                    if (pos > arrayList.size() - 1) {
                        pos = 0;
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    onSongClick(songList, pos);

                } else if (intent.getAction().equals(MainActivity.NOTIFY_PAUSE_PlAY)) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                    }

                } else if (intent.getAction().equals(MainActivity.NOTIFY_NEXT)) {
                    pos--;
                    if (pos < 0) {
                        pos = arrayList.size() - 1;
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    btn_play_pause_show.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    onSongClick(songList, pos);
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    private void view() {
        re_showBottomDialog = findViewById(R.id.re_showBottomDialog);
        progressBar = findViewById(R.id.processBar);
        li_mp3_show = findViewById(R.id.li_mp3_show);
        recyclerView = findViewById(R.id.recycler_view_search);
        tv_noData = findViewById(R.id.tv_nodata);
        searchView = findViewById(R.id.searchView);
        //play_mp3_show
        tv_nameBH_show = findViewById(R.id.tv_nameSong_show);
        tv_singer_show = findViewById(R.id.tv_singer_show);
        btn_previous_show = findViewById(R.id.img_previous_show);
        btn_play_pause_show = findViewById(R.id.img_play_pause_show);
        btn_next_show = findViewById(R.id.img_next_show);
    }

    private void firebaseSearch(String searchText) {
        mData = mFirebaseDatabase.getReference();
        Query query = mData.child("Album").orderByChild("name").equalTo(searchText);
        FirebaseRecyclerOptions<SongSearch> options =
                new FirebaseRecyclerOptions.Builder<SongSearch>()
                        .setQuery(query, SongSearch.class)
                        .setLifecycleOwner(this)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SongSearch, SearchAdapter.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position, @NonNull SongSearch model) {
                holder.name.setText(model.getName());
                holder.tv_tenAlbum.setText(model.getNameAlbum());
                holder.tv_countSong.setText(model.getNumOfSong() + " songs ");
                Glide.with(getApplicationContext()).load(model.getThumbnail()).into(holder.thumbnail);
            }

            @NonNull
            @Override
            public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search, parent, false);
                final SearchAdapter.MyViewHolder viewHolder = new SearchAdapter.MyViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSearch(viewHolder.getAdapterPosition());
                    }
                });
                return viewHolder;
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void playSearch(int position) {
        String name = String.valueOf(arrayList.get(position).getNameAlbum());
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        PlayMusicFragment playMusicFragment = new PlayMusicFragment();
        playMusicFragment.setArguments(bundle);
        recyclerView.setVisibility(View.GONE);
        loadFragment(playMusicFragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            searchView.clearFocus();
            switch (item.getItemId()) {
                case R.id.home_nav:
                    fragment = new HomeFragment();
                    recyclerView.setVisibility(View.GONE);
                    loadFragment(fragment);
                    return true;
                case R.id.person_nav:
                    fragment = new PersonFragment();
                    recyclerView.setVisibility(View.GONE);
                    loadFragment(fragment);
                    return true;
                case R.id.like_nav:
                    fragment = new LikeFragment();
                    recyclerView.setVisibility(View.GONE);
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void playList(ArrayList<LikeSong> songArrayList, int position) {
        li_mp3_show.setVisibility(View.VISIBLE);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        nameBH = String.valueOf(songArrayList.get(position).getName());
        urlSong = String.valueOf(songArrayList.get(position).getUrlSong());
        singer = String.valueOf(songArrayList.get(position).getSinger());

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
        tv_nameBH_show.setText(nameBH);
        btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);

        mediaPlayer.start();
        controlMusic();
        showDialog();
        notification();
    }

    @Override
    public void onSongClick(ArrayList<Song> arrayList, int position) {
        li_mp3_show.setVisibility(View.VISIBLE);
        songList = arrayList;
        pos = position;
        nameBH = String.valueOf(songList.get(pos).getName());
        urlSong = String.valueOf(songList.get(pos).getUrlSong());
        singer = String.valueOf(songList.get(pos).getSinger());
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
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
        tv_nameBH_show.setText(nameBH);
        btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);

        mediaPlayer.start();
        controlMusic();
        showDialog();
        notification();
    }

    public void showDialog() {
        re_showBottomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.play_mp3, null);
                final BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
                dialog.setContentView(view);
                dialog.show();
                tv_baihat = view.findViewById(R.id.tv_baihat);
                tv_casi = view.findViewById(R.id.tv_casi);
                seekBar = view.findViewById(R.id.seekBar_view);
                btn_play_pause = view.findViewById(R.id.btn_play_pause);
                btn_favorite = view.findViewById(R.id.btn_favorite);
                btn_next = view.findViewById(R.id.btn_next);
                btn_shuffle = view.findViewById(R.id.btn_shuffle);
                btn_previous = view.findViewById(R.id.btn_previous);

                tv_baihat.setText(nameBH);
                tv_casi.setText(singer);

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
                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_play_pause.setImageResource(R.drawable.ic_play_circle_filled_black_60dp);
                        pos++;
                        if (pos > songList.size() - 1) {
                            pos = 0;
                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        btn_play_pause.setImageResource(R.drawable.ic_pause_circle_filled_black_60dp);
                        onSongClick(songList, pos);
                        tv_baihat.setText(nameBH);
                        tv_casi.setText(singer);
                    }
                });
                btn_previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_play_pause.setImageResource(R.drawable.ic_play_circle_filled_black_60dp);
                        pos--;
                        if (pos < 0) {
                            pos = songList.size() - 1;
                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        btn_play_pause.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                        onSongClick(songList, pos);
                        tv_baihat.setText(nameBH);
                        tv_casi.setText(singer);
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
                        if (isShuffle) {
                            isShuffle = false;
                            btn_shuffle.setImageResource(R.drawable.ic_shuffle_black_28dp);
                        } else {
                            isShuffle = true;
                            btn_shuffle.setImageResource(R.drawable.ic_shuffle_black_focus_28dp);
                            Random random = new Random();
                            pos = random.nextInt((songList.size() - 1) + 1);
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.start();
                                onSongClick(songList, pos);
                            }
                        }

                    }
                });
                handler = new Handler();
                seekBar.setMax(mediaPlayer.getDuration());
                playCycle();
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
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
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                playCycle();

            }
        });
    }

    public void playCycle() {
        progressBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying())
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
        handler.postDelayed(runnable, 1000);
    }

    public void controlMusic() {
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
        btn_next_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_play_pause_show.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                pos++;
                if (pos > arrayList.size() - 1) {
                    pos = 0;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                btn_play_pause_show.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                onSongClick(songList, pos);
            }
        });
        btn_previous_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_play_pause_show.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                pos--;
                if (pos < 0) {
                    pos = arrayList.size() - 1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                btn_play_pause_show.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_48dp);
                onSongClick(songList, pos);
            }
        });
        handler = new Handler();
        progressBar.setMax(mediaPlayer.getDuration());
        playCycle();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pos++;
                if (pos > songList.size() - 1) {
                    pos = 0;
                }
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                onSongClick(songList,pos);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void notification() {

        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.nofication);
        NotificationCompat.Builder nc = new NotificationCompat.Builder(getApplicationContext());
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
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
        Intent nofifyIntent = new Intent(getBaseContext(), MainActivity.class);
        li_mp3_show.setVisibility(View.VISIBLE);
        tv_nameBH_show.setText(nameBH);
        tv_singer_show.setText(singer);
        nofifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, nofifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nc.setContentIntent(pendingIntent);
        nc.setSmallIcon(R.drawable.ic_album_black_24dp);
        nc.setAutoCancel(true);
        nc.setCustomBigContentView(expandedView);
        nc.setContentTitle(nameBH);
        nc.setContentText(singer);
        nc.setChannelId(id);
        nc.getBigContentView().setTextViewText(R.id.tv_nameBH_noti, nameBH);
        nc.getBigContentView().setTextViewText(R.id.tv_singer_noti, singer);

        setListeners(expandedView, getApplicationContext());
        nm.notify(NOTIFICATION_ID_CUSTOM_BIG, nc.build());
    }

    private static void setListeners(RemoteViews view, Context context) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent pause_play = new Intent(NOTIFY_PAUSE_PlAY);
        Intent next = new Intent(NOTIFY_NEXT);

        PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_previous_noti, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, pause_play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_play_pause_noti, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btn_next_noti, pPause);
    }

    @Override
    protected void onStart() {
        tv_singer_show.setText("Nghe nhạc mọi lúc");
        tv_nameBH_show.setText("SpaceX");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        super.onBackPressed();
        searchView.clearFocus();
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

}
