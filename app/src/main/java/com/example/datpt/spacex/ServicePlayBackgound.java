package com.example.datpt.spacex;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.datpt.spacex.item.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ServicePlayBackgound extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<Song> arrayList;

    private boolean shuffle = false;
    private Random rand;
    int songPos;
    String nameBH, singer, urlSong;

    private IBinder iBinder = new MusicBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        nameBH = (String) intent.getExtras().get("nameBH");
        singer = (String) intent.getExtras().get("singer");
        urlSong = (String) intent.getExtras().get("urlSong");

        playSong(songPos);

        return iBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rand = new Random();
        mediaPlayer = new MediaPlayer();

        initMusicPlayer();
    }

    public void initMusicPlayer() {
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    //pass song list
    public void setList(ArrayList<Song> theSongs) {
        arrayList = theSongs;

        Log.d("Setlisttttt---" ,String.valueOf(arrayList.size()));

    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    public class MusicBinder extends Binder {
        public ServicePlayBackgound getService() {
            return ServicePlayBackgound.this;
        }
    }

    public void getTrackName(String tenbaihat) {
        nameBH = tenbaihat;
    }

    public void getTrackSinger(String tencasi) {
        singer = tencasi;
    }

    public void playSong(int song) {

        songPos = song;

        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(urlSong);
            Log.d("Url---------", urlSong);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    public void getUrl(String url) {
        urlSong = url;
        setSong(songPos);
        playSong(songPos);
    }

    //set the song

    public void setSong(int songIndex) {
        songPos = songIndex;

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer.getCurrentPosition() > 0) {
            mp.reset();
            next();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public int getCurrentPosition() {
        playSong(songPos);
        return mediaPlayer.getCurrentPosition();
    }

    public int getDurration() {
        playSong(songPos);
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void seek(int posn) {
        mediaPlayer.seekTo(posn);
    }

    public void start() {
        mediaPlayer.start();
    }

    public void stop() {

        mediaPlayer.stop();
    }


    //skip to previous track
    public void previous() {
        songPos--;
        if (songPos < 0) {
            songPos = arrayList.size() - 1;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        playSong(songPos);

    }

    //skip to next
    public void next() {
        if (shuffle) {
            int newSong = songPos;
            while (newSong == songPos) {
                newSong = rand.nextInt(arrayList.size());
            }
            songPos = newSong;
        } else {
            songPos++;
            if (songPos >= arrayList.size()) songPos = 0;
        }
        playSong(songPos);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    //toggle shuffle
    public void setShuffle() {
        if (shuffle) shuffle = false;
        else shuffle = true;
    }

}
