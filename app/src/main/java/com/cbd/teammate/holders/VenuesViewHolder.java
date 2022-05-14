package com.cbd.teammate.holders;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbd.teammate.DistanceCalculator;
import com.cbd.teammate.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;


public class VenuesViewHolder extends RecyclerView.ViewHolder {

    private View view;
    private Double distance;

    public VenuesViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
    }

    public void setDetails(String nameVenue, Double latitude, Double longitude, String pictureReference, Pair<Double, Double> latLong) {
        Log.d("HolderDetails", nameVenue +" " +latitude+ " "+ longitude);
        String finalUrl = "http://i.imgur.com/DvpvklR.png";
        try {
            finalUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + URLEncoder.encode(pictureReference, "UTF-8") + "&key=" + URLEncoder.encode(view.getResources().getString(R.string.api_key), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        TextView venueName = view.findViewById(R.id.venue_card_name);
        DistanceCalculator calculator = new DistanceCalculator();
        this.distance = calculator.calculateDistance(latitude, latLong.first, longitude, latLong.second);

        TextView venueDistance = view.findViewById(R.id.venue_card_distance);
        ImageView imageView = view.findViewById(R.id.venue_card_image);
        Picasso.get().load(finalUrl).into(imageView);

        venueName.setText(nameVenue);

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        venueDistance.setText(decimalFormat.format(distance) + " km");
    }


    public Double getDistance()
    {
        return this.distance;
    }


}

