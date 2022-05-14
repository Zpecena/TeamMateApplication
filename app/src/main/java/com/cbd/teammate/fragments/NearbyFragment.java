package com.cbd.teammate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbd.core.VenueUtil;
import com.cbd.database.entities.Venue;
import com.cbd.maps.LocationProvider;
import com.cbd.teammate.DistanceCalculator;
import com.cbd.teammate.HashMapSort;
import com.cbd.teammate.R;
import com.cbd.teammate.holders.VenuesViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment implements NearbyRecyclerViewAdapter.ItemClickListener, VenueUtil {
    private View venuesView;
    private LocationProvider lp;
    private ArrayList<String> toJson;
    private RecyclerView.Adapter adapter;
    private HashMap<Venue, Double> hashMapVenues;

    public NearbyFragment(LocationProvider lp) {
        this.lp = lp;
        this.toJson = new ArrayList<>();
        this.hashMapVenues = new HashMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        venuesView = inflater.inflate(R.layout.fragment_nearby, container, false);

        this.getAllVenues();


        return venuesView;
    }


    public void onStart() {
        super.onStart();

    }


    public void onStop() {
        super.onStop();

    }


    public void onClickListener(View view, Venue model) {
        view.setOnClickListener(view1 -> {
            Fragment newFragment = new VenueViewFragment(model, lp);
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                            R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.fragment_above_nav, newFragment)
                    .addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @Override
    public void getAllVenues() {
        FirebaseFirestore.getInstance()
                .collection("venues")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("QuerySuccess", document.getId() + " => " + document.getData());

                                toJson.add(new Gson().toJson(document.getData()));

                            }
                        } else {
                            Log.d("QuerySuccess", "Error getting documents: ", task.getException());
                        }
                        createObjects();
                    }
                });
    }

    public void createObjects() {

        DistanceCalculator distanceCalculator = new DistanceCalculator();
        Double distance;
        Venue currentVenue;

        Gson gson = new Gson();
        for (int i = 0; i < toJson.size(); i++) {
            currentVenue = gson.fromJson(toJson.get(i), Venue.class);
            Log.d("MyAnd", currentVenue.getName() + " " + currentVenue.getLatitude() + " " + currentVenue.getLongitude());
            distance = distanceCalculator.calculateDistance(
                    lp.getLatLng().first,
                    currentVenue.getLatitude(),
                    lp.getLatLng().second,
                    currentVenue.getLongitude());
            hashMapVenues.put(currentVenue, distance);
        }
        Log.d("HashValues", "HM: " + new Gson().toJson(hashMapVenues));
        hashMapVenues = HashMapSort.sort(hashMapVenues);

        RecyclerView recyclerView = venuesView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setAdapter();

        recyclerView.setAdapter(adapter);

    }

    public void setAdapter() {
        adapter = new RecyclerView.Adapter<VenuesViewHolder>() {
            @NonNull
            @Override
            public VenuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_layout, parent, false);

                return new VenuesViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull VenuesViewHolder holder, int position) {
                Venue model;
                List<Venue> venues = new ArrayList<>(hashMapVenues.keySet());
                model = venues.get(position);
                try {
                    holder.setDetails(model.getName(), model.getLatitude(), model.getLongitude(), model.getPictureReference(), lp.getLatLng());
                    onClickListener(holder.itemView, model);
                } catch (Throwable oops) {
                    Toast.makeText(venuesView.getContext().getApplicationContext(),
                            "Oops! Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public int getItemCount() {

                return hashMapVenues.size() - 1;
            }
        };

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "You clicked", Toast.LENGTH_SHORT).show();
    }
}