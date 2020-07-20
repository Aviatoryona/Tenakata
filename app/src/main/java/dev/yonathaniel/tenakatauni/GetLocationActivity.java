package dev.yonathaniel.tenakatauni;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class GetLocationActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient mGoogleApiClient;
    Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        // TODO: 7/19/2020
        initViews();
        setSupportActionBar(toolbar);

        init();
    }

    private void init() {
        hideFab();
        fab.setOnClickListener(view -> {
//                if (TextUtils.isEmpty(countryNam)) {
//                    Snackbar.make(toolbar, "", Snackbar.LENGTH_INDEFINITE)
//                            .setAction("", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    startLocationUpdates();
//                                }
//                            }).show();
//                    return;
//                }

            if (spinnerCountry.getSelectedItemPosition() != 0) {
                Toast.makeText(GetLocationActivity.this, "Only Kenyan citizens are eligible", Toast.LENGTH_SHORT).show();
                return;
            }

            if (countryNam != null && !countryNam.equalsIgnoreCase(spinnerCountry.getItemAtPosition(0).toString())) {
                Snackbar.make(toolbar,
                        String.format("Your current location is %s, proceed?", countryNam),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Proceed?", view1 -> NextActivity()).show();
                return;
            }
            NextActivity();
        });

        getPermission();
    }

    private void NextActivity() {
        finish();
        Intent intent = new Intent(this, CaptureInfoActivity.class);
        intent.putExtra("lat", mLocation == null ? -1 : mLocation.getLatitude());
        intent.putExtra("lng", mLocation == null ? -1 : mLocation.getLongitude());
        intent.putExtra("ctry", countryNam == null ? "Unknown" : countryNam);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    // TODO: 7/19/2020
    String countryNam;
    void getCountry(double lat, double lng) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                countryNam = addresses.get(0).getCountryName();
                hideLoad();
                return;
            }
        } catch (IOException e) {
            Log.e("MITAG", Objects.requireNonNull(e.getLocalizedMessage()));
        }

        hideFab();
    }

    void hideLoad() {
        pG.setVisibility(View.GONE);
    }

    void hideFab() {
        pG.setVisibility(View.VISIBLE);
    }

    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, "CaptureInfo", 4500,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private Toolbar toolbar;
    private ProgressBar pG;
    private Spinner spinnerCountry;
    private FloatingActionButton fab;

    public void initViews() {
        toolbar = findViewById(R.id.toolbar);
        pG = findViewById(R.id.pG);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        fab = findViewById(R.id.fab);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /* 15 secs */
        long UPDATE_INTERVAL = 15000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 5 secs */
        long FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getPermission();
        onStart();
        startLocationUpdates();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Kindly grant all permissions", Toast.LENGTH_SHORT).show();
        getPermission();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mLocation != null) {
//            latLng.setText("Latitude : "+mLocation.getLatitude()+" , Longitude : "+mLocation.getLongitude());
            getCountry(mLocation.getLatitude(), mLocation.getLongitude());
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

}