package info.MyParker.Apps.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

public class PaySummonActivity extends Activity {
    private static final String TAG = PaySummonActivity.class.getSimpleName();
    private Button ok;
    private Button cancel;
    private TextView title;
    private TextView content;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private SessionManager session;
    private int amount;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vehicle_delete);

        DisplayMetrics dm= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.18));

        title=(TextView)findViewById(R.id.title);
        content=(TextView)findViewById(R.id.content);

        Bundle intent = getIntent().getExtras();
        amount = intent.getInt("price");
        id= intent.getInt("summonid");


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        final String email = user.get("email");

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        ok=(Button)findViewById(R.id.btn_ok);
        cancel=(Button)findViewById(R.id.btn_cancel);

        title.setText("Pay Summon");
        content.setText("Are you sure to pay for the summon? RM "+amount);


        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paysummon(email,amount,id);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

    }
    private void paysummon(final String email,final int amount, final int id){
        String tag_string_req = "req_paysummon";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PAYSUMMON, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Summon Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(),
                                "Summon has been paid successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PaySummonActivity.this, SummonHistoryActivity.class);
                        startActivity(intent);
                        finish();


                    } else {
                        // Error in paying summon. Get the error message
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
                Log.e(TAG, "summon pay Error: " + error.getMessage());
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
                params.put("amount", String.valueOf(amount));
                params.put("summonid", String.valueOf(id));


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
        Intent intent = new Intent(PaySummonActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
