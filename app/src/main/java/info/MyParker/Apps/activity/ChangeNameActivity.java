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

public class ChangeNameActivity extends Activity {
    private SQLiteHandler db;
    private SessionManager session;
    private EditText chgname;
    private Button btnchgname;
    private Button btncancel;
    private static final String TAG = ChangeNameActivity.class.getSimpleName();
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chgname);


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
        final String email = user.get("email");
        final String uid = user.get("uid");
        final String created_at = user.get("created_at");


        chgname=(EditText)findViewById(R.id.chgname);
        chgname.setText(name);
        btnchgname=(Button)findViewById(R.id.btnchgname);
        btncancel=(Button)findViewById(R.id.btncancel);

        btnchgname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                changename(email,chgname.getText().toString(), uid,created_at);
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent startActivity = new Intent(ChangeNameActivity.this, ProfileActivity.class);
                startActivity(startActivity);
                finish();
            }
        });
    }

    private void changename(final String email, final String name, final String uid, final String created_at){
        String tag_string_req = "req_chgname";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CHGNAME, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Change name Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Toast.makeText(getApplicationContext(),
                                "Name has been successfully changed!", Toast.LENGTH_LONG).show();

                        //change name in sqlite
                        db.deleteUsers();
                        db.addUser(name, email, uid, created_at);


                                Intent startActivity = new Intent(ChangeNameActivity.this, ProfileActivity.class);
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
                params.put("name", name);

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
        Intent intent = new Intent(ChangeNameActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
