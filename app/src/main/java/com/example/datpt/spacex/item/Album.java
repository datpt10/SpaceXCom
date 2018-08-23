package com.example.datpt.spacex.item;

public class Album {
    private String id;
    private String name;
    private String numOfSong;
    private String thumbnail;

    public Album() {


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumOfSong() {
        return numOfSong;
    }

    public void setNumOfSong(String numOfSong) {
        this.numOfSong = numOfSong;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}