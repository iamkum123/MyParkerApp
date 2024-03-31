package info.MyParker.Apps.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.MyParker.Apps.R;
import info.MyParker.Apps.app.AppConfig;
import info.MyParker.Apps.app.AppController;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;
import info.MyParker.Apps.helper.vehicle;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MakePaymentActivity extends Activity {
    private Button btnpay;
    private Button btncancel;
    private EditText locationE;
    private EditText vehicle;
    Spinner duration;
    private TextView unit;
    private TextView total;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private SessionManager session;
    private String loca;
    private String dura;
    private String veh;
    private static final String TAG = MakePaymentActivity.class.getSimpleName();
    private String price;
    List<vehicle> vehicleList;
    Spinner dropdown;
    Spinner locat;

    private Geocoder geocoder;
    private List <Address> addresses;
    private FusedLocationProviderClient client;
    private String area;
    private boolean initial=false;//to let unit price appear first to avoid crash. to stop the calculation for total first.
    List<String> locationList=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepayment);

        btnpay=(Button)findViewById(R.id.pay);
        btncancel=(Button)findViewById(R.id.btncancel);
        locat=findViewById(R.id.location);
        dropdown = findViewById(R.id.vehicle);
        duration=findViewById(R.id.duration);
        total=(TextView)findViewById(R.id.total);
        unit=(TextView)findViewById(R.id.unitprice);

        vehicleList = new ArrayList<>();

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
       final  String email = user.get("email");

        getVehicle(email);
        getLocation();
        getlocation();
        btnpay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(dropdown.getAdapter().getCount()==0){
                    Toast.makeText(getApplicationContext(), "Cannot make payment without vehicle. Please add vehicle", Toast.LENGTH_LONG).show();

                }else {
                    String text = dropdown.getSelectedItem().toString();
                    String duraS= duration.getSelectedItem().toString();
                    payparking(email, locat.getSelectedItem().toString(), text, duraS, total.getText().toString());
                }
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakePaymentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

//        locationE.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {
//
//                checkParkingPrice(locationE.getText().toString());
//
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//        });

//        duration.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {
//                dura=duration.getText().toString();
//                //Toast.makeText(getApplicationContext(),  "hihi",Toast.LENGTH_LONG).show();
//                total.setText(Double.toString(Double.parseDouble(dura)*Double.parseDouble(unit.getText().toString())));
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//        });

        locat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String temp = locat.getSelectedItem().toString();
                // Toast.makeText(getApplicationContext(), unit.getText().toString()
                // , Toast.LENGTH_LONG).show();

                checkParkingPrice(temp);

            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

            }

        });

        duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                //if(initial==true) {// only let the calculation run after getting the unit price, to avoid crash

                    String duraS = duration.getSelectedItem().toString();
                   // Toast.makeText(getApplicationContext(), unit.getText().toString()
                           // , Toast.LENGTH_LONG).show();

                   double ttl= Double.parseDouble(duraS) * Double.parseDouble(unit.getText().toString());
                total.setText(String.format("%.2f", ttl));
                }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

            }

        });


    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    private void getLocation(){
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MakePaymentActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(MakePaymentActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //location = (TextView) findViewById(R.id.tvAddress);
                    geocoder = new Geocoder(MakePaymentActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latitude,longitude,1);
                        String address = addresses.get(0).getAddressLine(0);
                        area = addresses.get(0).getLocality();
                        String city = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalcode = addresses.get(0).getPostalCode();
                        //address+", "+ area+", "+city+", " +country +", " +postalcode

                        //locationE.setText(area );

                        //Toast.makeText(getApplicationContext(),  area+", "+ city,Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });


    }

    private void checkParkingPrice(final String location){
        String tag_string_req = "req_checkprice";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CHECKPARKINGPRICE, new Response.Listener<String>() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Parking Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        price= jObj.getString("price");
                        unit.setText(String.format("%.2f", Double.parseDouble(price)));

                        //create spinner only after getting the unit price
                        List<Integer> dura = new ArrayList<Integer>();
                        for(int i=1;i<11;i++)
                            dura.add(i);

                        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(MakePaymentActivity.this, android.R.layout.simple_spinner_dropdown_item, dura);

                        duration.setAdapter(adapter);

                    } else {
                        // Error in checking for price. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "make payment Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to balance url
                Map<String, String> params = new HashMap<String,String>();
                params.put("location", location);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void getVehicle(final String email){
        String tag_string_req = "req_getvehicle";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SHOWVEHICLE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "get vehicle Response: " + response.toString());
                //hideDialog();

                try {
                    JSONArray list = new JSONArray(response);
                    //boolean error = list.getBoolean("error");
                    for(int i=0; i<list.length();i++) {
                        // User successfully stored in MySQL
                        JSONObject data = list.getJSONObject(i);

                        vehicleList.add (new vehicle(data.getString("vehicle")));

                    }

                   // String []items;
                    List<String> items = new ArrayList<String>();

                    vehicle v;
                    for(int j=0;j<vehicleList.size();j++) {
                        v=vehicleList.get(j);
                        //items[j] = (v.getVehicle());//retrieve  vehicle data
                        items.add((v.getVehicle()));
                    }
                    if(vehicleList.size()==0){//if user has no vehicle registered
                        Toast.makeText(getApplicationContext(), "You have no registered vehicle, Please add vehicle", Toast.LENGTH_LONG).show();

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MakePaymentActivity.this, android.R.layout.simple_spinner_dropdown_item, items);

                    dropdown.setAdapter(adapter);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "make payment Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to balance url
                Map<String, String> params = new HashMap<String,String>();
                params.put("email", email);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }
    private void payparking(final String email, final String location, final String vehicle, final String duration, final String total){
        String tag_string_req = "req_payparking";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PAYPARKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Parking Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Payment has been successfully made", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MakePaymentActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        // Error in checking for price. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "make payment Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to balance url
                Map<String, String> params = new HashMap<String,String>();
                params.put("email", email);
                params.put("location", location);
                params.put("vehicle", vehicle);
                params.put("duration", duration);
                params.put("total", total);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }

    private void getlocation(){
        String tag_string_req = "req_getlocation";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETLOCATION, new Response.Listener<String>() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "location Response: " + response.toString());
                //hideDialog();

                try {
                    JSONArray list = new JSONArray(response);
                    //boolean error = list.getBoolean("error");
                    for(int i=0; i<list.length();i++) {
                        // User successfully stored in MySQL
                        JSONObject data = list.getJSONObject(i);

                        locationList.add(data.getString("location"));

//
                    }
//
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MakePaymentActivity.this, android.R.layout.simple_spinner_dropdown_item, locationList);

                        locat.setAdapter(adapter);
                        for(int j=0;j<locationList.size();j++) {//find match location
                            if (locationList.get(j).equals(area))
                                locat.setSelection(j);

                        }


                }  catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "make payment Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to balance url
                Map<String, String> params = new HashMap<String,String>();
               // params.put("location", location);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MakePaymentActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
