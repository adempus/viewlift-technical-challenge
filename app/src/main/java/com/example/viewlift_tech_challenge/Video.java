package com.example.viewlift_tech_challenge;

/** POJO for Video data extracted from XML resource. **/
public class Video {
    public String title, thumbnailUrl, videoUrl, duration;

    public Video() { }

    public Video(String title, String thumbnailUrl, String videoUrl, String duration) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.duration = duration;
    }

    public String toString() {
        return "{title: "+this.title+", "+
                "thumbnail_url: "+this.thumbnailUrl+", "+
                "video_url: "+this.videoUrl+", "+
                "duration: "+this.duration+
                "}";
    }
}
