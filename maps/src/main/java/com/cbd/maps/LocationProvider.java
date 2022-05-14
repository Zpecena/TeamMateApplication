package com.cbd.maps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cbd.database.entities.Venue;
import com.cbd.maps.places.PlacesResponse;
import com.cbd.maps.places.Result;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationProvider {

    private final static int ALL_PERMISSIONS_RESULT = 101;

    private Context context;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mlocationRequest;
    private JsonObject jObj;
    private JsonElement jElem;
    private FirebaseFirestore db;

    private Pair<Double, Double> latLng;

    public LocationProvider() {

    }

    /*
     * Getters&Setters
     */

    public Pair<Double, Double> getLatLng() {
        return this.latLng;
    }

    public void setLatLng(Pair<Double, Double> latLng) {
        this.latLng = latLng;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public boolean isReady() {
        boolean res = false;

        if (latLng != null) {
            if (!(latLng.first == 0. && latLng.second == 0.)) {
                res = true;
            }
        }

        return res;
    }

    public void setup(Context context, Activity activity) {
        // Building and connecting
        // to Google's API client
        setContext(context);
        latLng = new Pair<>(0., 0.);

        db = FirebaseFirestore.getInstance();

        // Obtaining location services
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(context);

        if (hasPermissions()) {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        setLatLng(new Pair<>(location.getLatitude(), location.getLongitude()));
                        Log.w("LPDA", latLng.toString());
                    }
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{ACCESS_FINE_LOCATION}, ALL_PERMISSIONS_RESULT);
            }
        }

        createLocationRequest();

        locationCallback();

        requestToPlaces();
    }

    private void createLocationRequest() {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(15000);
        mlocationRequest.setFastestInterval(5000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void locationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for (Location location :
                        locationResult.getLocations()) {
                    if (location != null) {
                        setLatLng(new Pair<>(location.getLatitude(), location.getLongitude()));
                        Log.w("LPDA", latLng.toString());
                    }
                }
            }
        };
    }

    public void resumeLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(mlocationRequest,
                mLocationCallback,
                null);
    }

    public void pauseLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestToPlaces() {
        String uri = null;
        try {
            uri = "https://maps.googleapis.com/maps/api/place/textsearch/json?type=stadium&location=46.308029%2C16.3377904&rankby=distance&key=" + URLEncoder.encode(context.getResources().getString(R.string.api_key), "UTF-8");
        } catch (Throwable ii) {
            ii.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request =
                new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            jElem = gson.fromJson(response, JsonElement.class);
                            jObj = jElem.getAsJsonObject();

                            PlacesResponse placesResponse = gson.fromJson(jObj, PlacesResponse.class);

                            for (final Result r : placesResponse.getResults()) {
                                db.collection("venues")
                                        .whereEqualTo("uid", r.getPlaceId()).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                String newPicRef;

                                                try {
                                                    newPicRef = r.getPhotos().iterator().next().getPhotoReference();
                                                } catch (Throwable ii) {
                                                    newPicRef = "CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU";
                                                }

                                                Log.w("OLACI", r.getPlaceId());

                                                Venue newVenue = new Venue(r.getPlaceId(), r.getFormattedAddress(),
                                                        r.getGeometry().getLocation().getLat(),
                                                        r.getGeometry().getLocation().getLng(),
                                                        new ArrayList<String>(),
                                                        newPicRef);

                                                if (queryDocumentSnapshots.isEmpty()) {
                                                    db.collection("venues").add(newVenue);
                                                } else {
                                                    // always updating
                                                    Venue dbVenue = queryDocumentSnapshots.iterator().next().toObject(Venue.class);

                                                    if (!dbVenue.equals(newVenue)) {
                                                            DocumentReference docRef = db.collection("venues")
                                                                    .document(queryDocumentSnapshots.iterator().next().getId());

                                                            updateVenueData(r, docRef);

                                                            docRef.get();

                                                    }
                                                }
                                            }
                                        });
                            }
                        } catch (Throwable oops) {
                            Log.w("OLACI", oops.toString());

                            Toast.makeText(context, "Oops! Something went wrong...", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w("OLACI", error.toString());
                    }
                });

        queue.add(request);
    }

    private void updateVenueData(Result result, DocumentReference docRef) {
        Map<String, Object> res = new HashMap<>();

        docRef.update("name", result.getFormattedAddress());
        docRef.update("latitude", result.getGeometry().getLocation().getLat());
        docRef.update("longitude", result.getGeometry().getLocation().getLng());

        if (result.getPhotos() != null) {
            if (!result.getPhotos().isEmpty())
                docRef.update("pictureReference", result.getPhotos().iterator().next().getPhotoReference());
        }

    }
}