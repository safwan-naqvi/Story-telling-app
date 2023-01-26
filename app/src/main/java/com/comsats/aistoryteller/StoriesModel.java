package com.comsats.aistoryteller;

public class StoriesModel {

    String story_name;
    String story_image;
    float story_rating;
    String story_type;
    String story_views;
    String story_audio;

    public StoriesModel(String story_name, String story_image, float story_rating, String story_type, String story_views, String story_audio) {
        this.story_name = story_name;
        this.story_image = story_image;
        this.story_rating = story_rating;
        this.story_type = story_type;
        this.story_views = story_views;
        this.story_audio = story_audio;
    }

    public String getStory_audio() {
        return story_audio;
    }

    public void setStory_audio(String story_audio) {
        this.story_audio = story_audio;
    }

    public StoriesModel() {
    }

    public String getStory_name() {
        return story_name;
    }

    public void setStory_name(String story_name) {
        this.story_name = story_name;
    }

    public String getStory_image() {
        return story_image;
    }

    public void setStory_image(String story_image) {
        this.story_image = story_image;
    }

    public float getStory_rating() {
        return story_rating;
    }

    public void setStory_rating(float story_rating) {
        this.story_rating = story_rating;
    }

    public String getStory_type() {
        return story_type;
    }

    public void setStory_type(String story_type) {
        this.story_type = story_type;
    }

    public String getStory_views() {
        return story_views;
    }

    public void setStory_views(String story_views) {
        this.story_views = story_views;
    }
}