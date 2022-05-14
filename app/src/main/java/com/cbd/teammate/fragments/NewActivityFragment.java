package com.cbd.teammate.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cbd.database.entities.Activity;
import com.cbd.database.entities.Venue;
import com.cbd.maps.LocationProvider;
import com.cbd.teammate.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewActivityFragment extends Fragment {

    private FirebaseFirestore db;
    private View view;
    private Venue venue;
    private Button button;
    // form fields
    private EditText activitySport;
    private EditText activityDate;
    private Spinner activityPlayerType;
    private EditText activityNumberOfPlayers;
    private EditText activityPrice;
    private EditText activityDescription;
    // form errors (if there is any)
    private String errorMessages;
    // auxiliary variables
    private Integer auxPlayers;
    private Double auxPrice;
    private LocationProvider lp;

    NewActivityFragment(Venue venue, LocationProvider lp) {
        this.venue = venue;
        this.lp = lp;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_activity, container, false);

        activitySport = view.findViewById(R.id.new_activity_sport_value);
        activityDate = view.findViewById(R.id.new_activity_date_value);
        activityPlayerType = view.findViewById(R.id.new_activity_type_value);
        activityNumberOfPlayers = view.findViewById(R.id.new_activity_nun_players_value);
        activityPrice = view.findViewById(R.id.new_activity_price_value);
        activityDescription = view.findViewById(R.id.new_activity_desc_value);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = view.findViewById(R.id.add_new_activity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCorrectData()) {
                    button.setEnabled(false);
                    button.setBackgroundColor(Color.GRAY);

                    String playerUid = FirebaseAuth.getInstance().getUid();
                    String venueUid = venue.getUid();

                    // String sport, String date, String description, String playerType,
                    // Double price, Integer playersNeeded, String creatorid, List<String> players
                    Activity toDb = new Activity(activitySport.getText().toString(),
                            activityDate.getText().toString(), activityDescription.getText().toString(),
                            activityPlayerType.getSelectedItem().toString(), auxPrice,
                            auxPlayers, playerUid, new ArrayList<>());


                    db.collection("activities").add(toDb)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    venue.getActivities().add(documentReference.getId());
                                    db.collection("venues")
                                            .whereEqualTo("uid", venueUid).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    String venueDocId = queryDocumentSnapshots.iterator().next().getId();
                                                    db.collection("venues").document(venueDocId)
                                                            .update("activities", venue.getActivities())
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(view.getContext(),
                                                                            "Successfully created activity!", Toast.LENGTH_LONG).show();
                                                                    FragmentTransaction fragmentTransaction = getActivity()
                                                                            .getSupportFragmentManager()
                                                                            .beginTransaction();
                                                                    fragmentTransaction.replace(R.id.fragment_above_nav,
                                                                            new VenueViewFragment(venue, lp));
                                                                    fragmentTransaction.commit();
                                                                }
                                                            });

                                                }
                                            });
                                }
                            });
                } else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(view.getContext());

                    builder.setMessage(errorMessages).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                }
            }
        });
    }

    private boolean checkCorrectData() {
        boolean res = true;
        errorMessages = "";

        if (activitySport.getText().toString().isEmpty()) {
            errorMessages += "Sport cannot be empty!\n";
            res = false;
        }

        if (!activityDate.getText().toString().isEmpty()) {
            String date = activityDate.getText().toString();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.ENGLISH);

            try {
                Date parsedDate = dateFormat.parse(date);

                assert parsedDate != null;
                if (parsedDate.before(new Date())) {
                    errorMessages += "Date must be a moment in the future!\n";
                    res = false;
                }

            } catch (Exception e) {
                errorMessages += "There was a problem related with the date... Please follow the pattern 'dd/MM/yyyy hh:mm'.\n";
                res = false;
            }

        } else {
            errorMessages += "Date cannot be empty!\n";
            res = false;
        }

        if (!activityNumberOfPlayers.getText().toString().isEmpty()) {

            try {
                auxPlayers = Integer.
                        parseInt(activityNumberOfPlayers.getText().toString());

                if (auxPlayers <= 0) {
                    errorMessages += "The number of players should be greater than 0!\n";
                    res = false;
                }

            } catch (Exception e) {
                errorMessages += "There was an error in the number of players... Make sure to only input integers.\n";
                res = false;
            }

        } else {
            errorMessages += "Number of players cannot be empty!\n";
            res = false;
        }

        if (!activityPrice.getText().toString().isEmpty()) {

            try {
                auxPrice = Double.parseDouble(activityPrice.getText().toString());

                if (auxPrice < 0) {
                    errorMessages += "The should not be lower than 0!\n";
                    res = false;
                }

            } catch (Exception e) {
                errorMessages += "There was an error in the price... Make sure to only input numbers.\n";
                res = false;
            }

        } else {
            errorMessages += "Price cannot be empty!\n";
            res = false;
        }

        if (activityDescription.getText().toString().isEmpty()) {
            errorMessages += "Description cannot be empty!";
            res = false;
        }

        return res;
    }
}
