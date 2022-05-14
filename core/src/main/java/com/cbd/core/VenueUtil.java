package com.cbd.core;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.cbd.database.entities.Venue;


public interface VenueUtil {

    void onClickListener(View view, Venue model);

    void setAdapter();

    void getAllVenues();

}