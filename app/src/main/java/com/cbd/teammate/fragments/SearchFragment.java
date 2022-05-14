package com.cbd.teammate.fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.cbd.core.VenueUtil;
import com.cbd.database.entities.Venue;
import com.cbd.maps.LocationProvider;
import com.cbd.teammate.R;
import com.cbd.teammate.holders.VenuesViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements VenueUtil{

    private List<String> list;
    private FirestoreRecyclerAdapter<Venue, VenuesViewHolder> firestoreRecyclerAdapter;
    private EditText toSearch;
    private RecyclerView recyclerList;
    private LocationProvider lp;
    private View view;
    private com.google.firebase.firestore.Query venueQuery;
    private FirestoreRecyclerOptions<Venue> venueOptions;

    public SearchFragment(LocationProvider lp) {
        this.lp = lp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        toSearch = view.findViewById(R.id.to_search);
        recyclerList = view.findViewById(R.id.result_list);
        recyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerList.setHasFixedSize(true);

        this.algoliaSearch();
        return view;
    }


    private void algoliaSearch() {
        Client client = new Client(view.getResources().getString(R.string.algolia_app_id), view.getResources().getString(R.string.algolia_api_key));
        Index index = client.getIndex(view.getResources().getString(R.string.algolia_index));

        this.toSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable ed) {
                Query query = new Query(ed.toString())
                        .setAttributesToRetrieve("name")
                        .setHitsPerPage(50);
                index.searchAsync(query, (jsonObject, e) -> {
                    assert jsonObject != null;
                    Log.d("JSOBJECT", jsonObject.toString());
                    try {
                        JSONArray hits = jsonObject.getJSONArray("hits");
                        list = new ArrayList<>();

                        Log.w("testik", "hodnota = " + hits.length());

                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject object1 = hits.getJSONObject(i);
                            String name = object1.getString("name");
                            list.add(name);
                        }
                        // currently displays only first 10 results due to Firebase limitation
                        if (hits.length() != 0 && hits.length() <= 10) {
                            getAllVenues();
                            setOptions(venueQuery);

                                setAdapter();

                            if (firestoreRecyclerAdapter != null) {
                                firestoreRecyclerAdapter.startListening();
                                recyclerList.setAdapter(firestoreRecyclerAdapter);
                            }

                        }


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                });

            }
        });

    }

    public void onClickListener(View view, Venue model) {
        view.setOnClickListener(view1 -> {
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragment_above_nav, new VenueViewFragment(model,lp));
            fragmentTransaction.commit();
        });
    }

    @Override
    public void getAllVenues() {
        venueQuery =  FirebaseFirestore.getInstance()
                .collection("venues")
                .whereIn("name", list)
                .orderBy("name", com.google.firebase.firestore.Query.Direction.ASCENDING);
    }


    @Override
    public void setAdapter() {
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Venue, VenuesViewHolder>(this.venueOptions) {
            @Override
            protected void onBindViewHolder(@NonNull VenuesViewHolder holder, int position, @NonNull Venue model) {
                try {
                    holder.setDetails(model.getName(), model.getLatitude(), model.getLongitude(), model.getPictureReference(), lp.getLatLng());
                    onClickListener(holder.itemView, model);
                } catch (Throwable oops) {
                    Toast.makeText(view.getContext().getApplicationContext(), "Oops! Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @NonNull
            @Override
            public VenuesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.card_layout, parent, false);
                return new VenuesViewHolder(view);
            }
        };
    }


    public void setOptions(com.google.firebase.firestore.Query query) {
        this.venueOptions =  new FirestoreRecyclerOptions.Builder<Venue>()
                .setQuery(query, Venue.class)
                .build();
    }
}