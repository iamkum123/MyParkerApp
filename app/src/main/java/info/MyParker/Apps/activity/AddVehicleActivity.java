package info.MyParker.Apps.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AddVehicleActivity extends Activity {

    private Button btnvehicle;
    private Button btncancel;
    private EditText vehicle;
    private SQLiteHandler db;
    private SessionManager session;
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvehicle);

        vehicle=(EditText)findViewById(R.id.newvehicle);
        btnvehicle=(Button)findViewById(R.id.btnaddvehicle);
        btncancel=(Button)findViewById(R.id.btncancel);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        final String email = user.get("email");


        btnvehicle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addvehicle(email,vehicle.getText().toString());

            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent startActivity = new Intent(AddVehicleActivity.this, CheckVehicleActivity.class);
                startActivity(startActivity);
                finish();
            }
        });
    }

    public void addvehicle(final String email, final String vehicle){

        String tag_string_req = "req_addvehicle";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADDVEHICLE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add vehicle Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Toast.makeText(getApplicationContext(),
                                "Vehicle number has been updated ", Toast.LENGTH_LONG).show();



                        Intent startActivity = new Intent(AddVehicleActivity.this, ProfileActivity.class);
                        startActivity(startActivity);
                        finish();



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
                params.put("email", email);
                params.put("newvehicle",vehicle );


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(AddVehicleActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
