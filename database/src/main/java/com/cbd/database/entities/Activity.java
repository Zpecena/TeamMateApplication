package com.cbd.database.entities;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Activity {

    private String sport, date, description,
            playerType;
    private Double price;
    private Integer playersNeeded;
    private String creatorid;
    private List<String> players;

    public Activity() {

    }

    public Activity(String sport, String date, String description, String playerType,
                    Double price, Integer playersNeeded, String creatorid, List<String> players) {
        this.sport = sport;
        this.date = date;
        this.description = description;
        this.playerType = playerType;
        this.price = price;
        this.playersNeeded = playersNeeded;
        this.creatorid = creatorid;
        this.players = players;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getPlayersNeeded() {
        return playersNeeded;
    }

    public void setPlayersNeeded(Integer playersNeeded) {
        this.playersNeeded = playersNeeded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getCreatorId() {
        return creatorid;
    }

    public void setCreatorId(String creatorid) {
        this.creatorid = creatorid;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
