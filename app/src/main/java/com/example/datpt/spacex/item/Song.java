package com.example.datpt.spacex.item;

public class Song {

    private String id_song;
    private String idAlbum;
    private String name;
    private String singer;
    private String urlSong;
    private String duration;


    public Song() {

    }

    public Song(String id_song, String idAlbum, String name, String singer, String urlSong, String duration) {
        this.id_song = id_song;
        this.idAlbum = idAlbum;
        this.name = name;
        this.singer = singer;
        this.urlSong = urlSong;
        this.duration = duration;
    }


    public String getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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
