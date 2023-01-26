package com.comsats.aistoryteller;

public class RecommendedStoryModel {
    String story_name;
    String story_image;

    public RecommendedStoryModel(String story_name, String story_image) {
        this.story_name = story_name;
        this.story_image = story_image;
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
}
