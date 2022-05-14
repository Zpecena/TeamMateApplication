package com.cbd.teammate;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cbd.database.entities.Venue;
import com.cbd.maps.LocationProvider;
import com.cbd.teammate.fragments.MyActivitiesFragment;
import com.cbd.teammate.fragments.NearbyFragment;
import com.cbd.teammate.fragments.ProfileFragment;
import com.cbd.teammate.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class SignedActivity extends AbstractActivity {

    private final static int ALL_PERMISSIONS_RESULT = 101;
    public LocationProvider lp;
    private TextView nameofuser;
    private HashMap<Venue, Double> hashMapVenues;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "This app requires location permissions to be granted",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed);

        lp = new LocationProvider();
        lp.setup(this, this);

        BottomNavigationView navbar = findViewById(R.id.bottom_navigation);
        createNavigationListener(navbar);

        navbar.setSelectedItemId(R.id.nav_near);
    }

    private void createNavigationListener(BottomNavigationView navbar) {
        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selected = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_near:
                        selected = new NearbyFragment(lp);
                        break;
                    case R.id.nav_profile:
                        selected = new ProfileFragment(lp);
                        break;
                    case R.id.nav_search:
                        selected = new SearchFragment(lp);
                        break;
                    case R.id.nav_mine:
                        selected = new MyActivitiesFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.fragment_above_nav, selected)
                        .addToBackStack("main_fragment").commit();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        lp.resumeLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();

        lp.pauseLocationUpdates();
    }

    public HashMap<Venue, Double> getHashMapVenues() {
        return this.hashMapVenues;
    }

    public void setHashMapVenues(HashMap<Venue, Double> hashMapVenues) {
        this.hashMapVenues = hashMapVenues;
    }
}
