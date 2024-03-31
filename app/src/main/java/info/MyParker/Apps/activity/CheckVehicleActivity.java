package info.MyParker.Apps.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.MyParker.Apps.R;
import info.MyParker.Apps.app.AppConfig;
import info.MyParker.Apps.app.AppController;
import info.MyParker.Apps.app.VehicleAdapter;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;
import info.MyParker.Apps.helper.vehicle;

public class CheckVehicleActivity extends AppCompatActivity {
    private static final String TAG = CheckVehicleActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Button add;


    RecyclerView recyclerView;
    //ProductAdapter adapter;

    List<vehicle> vehicleList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkvehicle);
        getSupportActionBar().hide();

        vehicleList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add= (Button)findViewById(R.id.addvehicle);

        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        getVehicle(email);
        //adapter = new ProductAdapter(this, productList);
        //recycleView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckVehicleActivity.this, AddVehicleActivity.class);
                startActivity(intent);
            }
        });


    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),
                ProfileActivity.class);
        startActivity(i);
        finish();
    }


    private void getVehicle(final String email) {
        String tag_string_req = "req_vehicleDetails";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SHOWVEHICLE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray list = new JSONArray(response);
                            //boolean error = list.getBoolean("error");
                            for(int i=0; i<list.length();i++) {
                                // User successfully stored in MySQL
                                JSONObject data = list.getJSONObject(i);

                                         vehicleList.add (new vehicle(data.getString("vehicle")));



                            }
                            VehicleAdapter adapter = new VehicleAdapter(CheckVehicleActivity.this,vehicleList);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "get vehicle Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);

                return params;
            }

        };
        //send request to php
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);

    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
