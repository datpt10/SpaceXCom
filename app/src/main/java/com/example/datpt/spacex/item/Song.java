package com.example.datpt.spacex.item;

public class Song {

    private String id_song;
    private String name;
    private String singer;
    private String urlSong;


    public Song() {

    }

    public Song(String id_song, String name, String singer, String urlSong) {
        this.id_song = id_song;
        this.name = name;
        this.singer = singer;
        this.urlSong = urlSong;
    }

    public String getId_song() {
        return id_song;
    }

    public void setId_song(String id_song) {
        this.id_song = id_song;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getUrlSong() {
        return urlSong;
    }

    public void setUrlSong(String urlSong) {
        this.urlSong = urlSong;
    }
}
