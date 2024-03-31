package info.MyParker.Apps.officer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import info.MyParker.Apps.R;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class OfficerMainActivity extends Activity {
    private static final String TAG = OfficerMainActivity.class.getSimpleName();

    private TextView txtName;
    private TextView txtBalance;
    private TextView txtAddress;
    private Geocoder geocoder;
    private List <Address> addresses;

    private Button btnHome;
    private Button btnProfile;
    private Button btnscan;
    private String area;

    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private SessionManager session;
    private String bal;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_main);

        txtName = (TextView) findViewById(R.id.name);
        txtBalance = (TextView) findViewById(R.id.balance);
        btnHome = (Button) findViewById(R.id.home);
        btnProfile = (Button) findViewById(R.id.pgprofile);
        btnscan = (Button) findViewById(R.id.scan);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        txtName.setText(name);
        getLocation();

        btnHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfficerMainActivity.this, OfficerMainActivity.class);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfficerMainActivity.this, OfficerProfileActivity.class);
                startActivity(intent);

            }
        });

        btnscan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfficerMainActivity.this, ScanActivity.class);
                intent.putExtra("loca", area);
                startActivity(intent);
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(OfficerMainActivity.this, OfficerLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    private void getLocation(){
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(OfficerMainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(OfficerMainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    txtAddress = (TextView) findViewById(R.id.tvAddress);
                    geocoder = new Geocoder(OfficerMainActivity.this,Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude,longitude,1);
                        String address = addresses.get(0).getAddressLine(0);
                        area = addresses.get(0).getLocality();
                        String city = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalcode = addresses.get(0).getPostalCode();
                        //address+", "+ area+", "+city+", " +country +", " +postalcode

                        txtAddress.setText(area+", "+ city );



                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });


    }
}
