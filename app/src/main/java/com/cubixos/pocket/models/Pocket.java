package com.cubixos.pocket.models;

public class Pocket {

    private String status;
    private String category;
    private String title;
    private String description;
    private String channelUrl;
    private String imageUrl;

    public Pocket() {
        //empty Constructor
    }

    public Pocket(String status, String category, String title, String description, String channelUrl, String imageUrl) {
        this.status = status;
        this.category = category;
        this.title = title;
        this.description = description;
        this.channelUrl = channelUrl;
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}