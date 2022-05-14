package com.cbd.teammate.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbd.database.entities.Activity;
import com.cbd.database.entities.Venue;
import com.cbd.maps.LocationProvider;
import com.cbd.teammate.R;
import com.cbd.teammate.holders.ActivityHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VenueViewFragment extends Fragment {

    private FirestoreRecyclerAdapter<Activity, ActivityHolder> firestoreActivityRecyclerAdapter;
    private RecyclerView activitiesList;
    private View view;
    private Venue venue;
    private FirebaseFirestore db;
    private String newPicRef;
    private LocationProvider lp;

    public VenueViewFragment(Venue venue, LocationProvider lp) {
        this.venue = venue;
        this.lp = lp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_venue, container, false);

        activitiesList = view.findViewById(R.id.venue_view_activities);
        activitiesList.setLayoutManager(new LinearLayoutManager(getContext()));

        initialiseDB();

        FloatingActionButton newActivity = view.findViewById(R.id.new_activity);
        newActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                fragmentTransaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right);

                fragmentTransaction.replace(R.id.fragment_above_nav,
                        new NewActivityFragment(venue, lp));

                fragmentTransaction.addToBackStack(null).commit();
            }
        });

        if (venue != null) {
            setViewAttributes();
        }

        return view;
    }

    private void initialiseDB() {
        db = FirebaseFirestore.getInstance();
    }

    private void picReferenceCreate() {

        try {
            newPicRef = venue.getPictureReference();
        } catch (Throwable ii) {
            newPicRef = "CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU";
        }

    }

    private String createPictureUrl() {
        this.picReferenceCreate();
        String finalUrl;
        try {
            finalUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + URLEncoder.encode(newPicRef, "UTF-8") + "&key=" + URLEncoder.encode(view.getResources().getString(R.string.api_key), "UTF-8");
        } catch (Throwable ee) {
            finalUrl = "http://i.imgur.com/DvpvklR.png";
        }
        return finalUrl;
    }

    private void setViewAttributes() {
        TextView name = view.findViewById(R.id.venue_view_name);
        TextView lat = view.findViewById(R.id.venue_view_latitude);
        TextView lon = view.findViewById(R.id.venue_view_longitude);
        ImageView image = view.findViewById(R.id.venue_view_image_image);
        String url = this.createPictureUrl();
        configureVenueRecyclerView(venue.getActivities());

        name.setText(venue.getName());
        lat.setText(venue.getLatitude().toString());
        lon.setText(venue.getLongitude().toString());
        Picasso.get()
                .load(url).into(image);
    }

    private void configureVenueRecyclerView(List<String> activities) {

        if (activities != null && !activities.isEmpty()) {

            Query query = db.collection("activities")
                    .whereIn(FieldPath.documentId(), activities);
            FirestoreRecyclerOptions<Activity> options = new FirestoreRecyclerOptions.Builder<Activity>()
                    .setQuery(query, Activity.class)
                    .build();

            firestoreActivityRecyclerAdapter =
                    new FirestoreRecyclerAdapter<Activity, ActivityHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull ActivityHolder holder, int position, @NonNull Activity model) {
                            if (hasExpired(model.getDate())) {
                                holder.setDetails(model);
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        FragmentTransaction fragmentTransaction = getActivity()
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                                        R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                                        fragmentTransaction.replace(R.id.fragment_above_nav,
                                                new ActivityViewFragment(model, venue.getName(), newPicRef, lp));
                                        fragmentTransaction.addToBackStack(null).commit();
                                    }
                                });
                            }
                        }

                        @NonNull
                        @Override
                        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.list_layout_venue, parent, false);

                            return new ActivityHolder(view);
                        }
                    };
            activitiesList.setAdapter(firestoreActivityRecyclerAdapter);
            firestoreActivityRecyclerAdapter.startListening();
        }
    }

    private boolean hasExpired(String dateString) {
        boolean res = false;

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date date = format.parse(dateString);

            if (date.after(new Date())) {
                res = true;
            }
        } catch (ParseException e) {
            Log.w("Parse exception", e.toString());
        } catch (Throwable e) {
            Log.w("Error", e.toString());
        }

        return res;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firestoreActivityRecyclerAdapter != null) {
            firestoreActivityRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreActivityRecyclerAdapter != null) {
            firestoreActivityRecyclerAdapter.stopListening();
        }
    }
}
