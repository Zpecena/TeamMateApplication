package com.cbd.database.entities;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Request {

    private String uid, playerType, description;
    private Boolean isAccepted;
    private Activity activity;
    private Player player;

    public Request() {

    }

    public Request(String uid, String playerType, String description,
                   Boolean isAccepted, Activity activity, Player player) {
        this.uid = uid;
        this.playerType = playerType;
        this.description = description;
        this.isAccepted = isAccepted;
        this.activity = activity;
        this.player = player;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
