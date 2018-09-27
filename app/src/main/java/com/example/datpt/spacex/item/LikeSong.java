package com.example.datpt.spacex.item;

public class LikeSong  {

    private String name;
    private String singer;
    private String urlSong;

    public LikeSong() {

    }

    public LikeSong( String name, String singer, String urlSong) {
        this.name = name;
        this.singer = singer;
        this.urlSong = urlSong;
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

