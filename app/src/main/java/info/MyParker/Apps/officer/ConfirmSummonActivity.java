package info.MyParker.Apps.officer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.MyParker.Apps.R;
import info.MyParker.Apps.app.AppConfig;
import info.MyParker.Apps.app.AppController;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;

public class ConfirmSummonActivity extends AppCompatActivity {
    private static final String TAG = ConfirmSummonActivity.class.getSimpleName();
    private TextView tv_user;
    private TextView tv_vehicleNum;
    private TextView tv_offense;
    private TextView tv_price;
    private TextView tv_location;
    private Button btn_summon;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_confirm_summon);

        Bundle intent = getIntent().getExtras();
        final String uname = intent.getString("name");
        final String vehicle = intent.getString("vehicle");
        final String offense = intent.getString("offense");
        final int price = intent.getInt("price");
        final String location = intent.getString("location");

        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_vehicleNum = (TextView) findViewById(R.id.tv_vehicleNum);
        tv_offense = (TextView) findViewById(R.id.tv_offense);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_location = (TextView) findViewById(R.id.tv_location);
        btn_summon = (Button) findViewById(R.id.btnIssue);
        tv_user.setText(uname);
        tv_vehicleNum.setText(vehicle);
        tv_offense.setText(offense);
        tv_location.setText(location);
        tv_price.setText(Integer.toString(price));

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        final String oname = user.get("name");

        btn_summon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //add details to table
                if (isNetworkAvailable()) {
                    issue(oname,uname,vehicle,location,offense,price);
                    Toast.makeText(getApplicationContext(),
                            "Summon Issued", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ConfirmSummonActivity.this, ScanActivity.class);
                    startActivity(intent);}else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect to the internet", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void issue(final String officername, final String name, final String vehicle, final String location, final String offense, final int price ){
        String tag_string_req = "req_balance";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADDSUMMON, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Balance Response: " + response.toString());
                //hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Toast.makeText(getApplicationContext(),
                                "summon issued", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ConfirmSummonActivity.this, OfficerMainActivity.class);
                        startActivity(intent);
                    } else {
                        // Error in login. Get the error message
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
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to balance url
                Map<String, String> params = new HashMap<String,String>();
                params.put("officername", officername);
                params.put("name", name);
                params.put("vehicle", vehicle);
                params.put("location", location);
                params.put("offense", offense);
                params.put("price", String.valueOf(price));
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(ConfirmSummonActivity.this, OfficerLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
