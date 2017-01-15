package com.example.rishabh.lapit;



public class Notification {

    private String followerId;
    private String followerName;

    public Notification() {

    }

    public Notification(String followerId, String followerName) {

        this.followerId = followerId;
        this.followerName = followerName;

    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }
}
