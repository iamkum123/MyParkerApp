package info.MyParker.Apps.officer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
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

public class IssueSummonActivity extends AppCompatActivity {

    private RadioButton rb1, rb2, rb3, rb4;
    private Button btnConfirm;
    private SQLiteHandler db;
    private SessionManager session;
    private TextView tvVehicleNum;

    private static final String TAG = ScanActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_issue_summon);


        btnConfirm= (Button) findViewById(R.id.btnConfirm);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb4 = (RadioButton) findViewById(R.id.rb4);
        tvVehicleNum = (TextView) findViewById(R.id.tv_vehicleNum);
        Bundle intent = getIntent().getExtras();
        final String uname = intent.getString("name");
        final String vehicle = intent.getString("vehicle");
        final String location = intent.getString("location");

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        tvVehicleNum.setText(vehicle);
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (rb1.isChecked()) {
                    Intent intent = new Intent(IssueSummonActivity.this, ConfirmSummonActivity.class);
                    intent.putExtra("name", uname);
                    intent.putExtra("vehicle", vehicle);
                    intent.putExtra("offense","Obstruct traffic");
                    intent.putExtra("price", 50);
                    intent.putExtra("location", location);
                    startActivity(intent);

                }
                else if (rb2.isChecked()) {
                    Intent intent = new Intent(IssueSummonActivity.this, ConfirmSummonActivity.class);
                    intent.putExtra("name", uname);
                    intent.putExtra("vehicle", vehicle);
                    intent.putExtra("offense","Parking vehicle outside designated parking lot");
                    intent.putExtra("price",100);
                    intent.putExtra("location", location);
                    startActivity(intent);
                }
                else if (rb3.isChecked()) {
                    Intent intent = new Intent(IssueSummonActivity.this, ConfirmSummonActivity.class);
                    intent.putExtra("name", uname);
                    intent.putExtra("vehicle", vehicle);
                    intent.putExtra("offense","Obstruct parking space");
                    intent.putExtra("price",150);
                    intent.putExtra("location", location);
                    startActivity(intent);
                }
                else if (rb4.isChecked()) {
                    Intent intent = new Intent(IssueSummonActivity.this, ConfirmSummonActivity.class);
                    intent.putExtra("name", uname);
                    intent.putExtra("vehicle", vehicle);
                    intent.putExtra("offense","No parking payment");
                    intent.putExtra("price",80);
                    intent.putExtra("location", location);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "Please select an Offense", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(IssueSummonActivity.this, OfficerLoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void check(final String vehicle ){
        String tag_string_req = "req_check";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                //need change the URL
                AppConfig.URL_CHECKVEHICLE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "check Response: " + response.toString());
                //hideDialog();
                //receive data
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        String vehicle_num= jObj.getString("vehicleNum");
                        String officer_email= jObj.getString("offEmail");

                        Toast.makeText(getApplicationContext(),
                                vehicle_num+" ", Toast.LENGTH_LONG).show();

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
                params.put("vehicle_num", vehicle);

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
}
