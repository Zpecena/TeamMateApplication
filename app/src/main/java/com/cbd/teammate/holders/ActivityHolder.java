package com.cbd.teammate.holders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cbd.database.entities.Activity;
import com.cbd.teammate.R;

public class ActivityHolder extends RecyclerView.ViewHolder {
    private TextView activitySport, activityDate, activityNumPlayers;

    public ActivityHolder(View view) {
        super(view);

        activitySport = view.findViewById(R.id.venue_recycler_sport);
        activityDate = view.findViewById(R.id.venue_recycler_date);
        activityNumPlayers = view.findViewById(R.id.venue_recycler_players);
    }

    public void setDetails(Activity activity) {
        activitySport.setText(activity.getSport());
        activityDate.setText(activity.getDate());
        activityNumPlayers.setText(activity.getPlayers().size()
                + " out of " + activity.getPlayersNeeded());
    }

}