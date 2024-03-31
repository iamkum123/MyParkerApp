package info.MyParker.Apps.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class TopupActivity extends AppCompatActivity {
    private Button btnProceed;
    private RadioButton rm5,rm10,rm30,rm50,rm100;
    private int amount;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private SessionManager session;
    private TextView newBalance;
    private String email;
    private String bal;
    private Button btnHome;
    private Button btnTopup;
    private Button btnHistory;
    private Button btnProfile;

    private static final String TAG = TopupActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        getSupportActionBar().hide();

        btnProceed= (Button) findViewById(R.id.btntopup2);
        rm5 = (RadioButton) findViewById(R.id.rm5);
        rm10 = (RadioButton) findViewById(R.id.rm10);
        rm30 = (RadioButton) findViewById(R.id.rm30);
        rm50 = (RadioButton) findViewById(R.id.rm50);
        rm100 = (RadioButton) findViewById(R.id.rm100);


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
         email = user.get("email");

        //newBalance= (TextView)findViewById(R.id.name);



        btnProceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (rm5.isChecked()) {
                    amount = 5;
                    Intent intent = new Intent(TopupActivity.this, PaymentActivity.class);
                    intent.putExtra("amount",5);
                    //String balance= updateNewBalance(email,amount);
                    //intent.putExtra("latestBalance",balance);
                    startActivity(intent);

                }
                else if (rm10.isChecked()) {
                    amount = 10;
                    Intent intent = new Intent(TopupActivity.this, PaymentActivity.class);
                    intent.putExtra("amount",10);
                    startActivity(intent);
                }
                else if (rm30.isChecked()) {
                    amount = 30;
                    Intent intent = new Intent(TopupActivity.this, PaymentActivity.class);
                    intent.putExtra("amount",30);
                    startActivity(intent);
                }
                else if (rm50.isChecked()) {
                    amount = 50;
                    Intent intent = new Intent(TopupActivity.this, PaymentActivity.class);
                    intent.putExtra("amount",50);
                    startActivity(intent);
                }
                else if (rm100.isChecked()) {
                    amount = 100;
                    Intent intent = new Intent(TopupActivity.this, PaymentActivity.class);
                    intent.putExtra("amount",100);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "No amount is selected. Please select amount", Toast.LENGTH_LONG).show();
                }


            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationView);
        Menu menu= bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                Intent intent = new Intent(TopupActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.topup:
                                Intent intent2 = new Intent(TopupActivity.this, TopupActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.history:
                                Intent intent3 = new Intent(TopupActivity.this, HistoryActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.profile:
                                Intent intent4 = new Intent(TopupActivity.this, ProfileActivity.class);
                                startActivity(intent4);
                                break;
                        }
                        return true;
                    }
                });

    }

    private String updateNewBalance(final String email, final int amount ){
        String tag_string_req = "req_topupbalance";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_TOPUPBALANCE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Topup Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(),
                                "balance is updated", Toast.LENGTH_LONG).show();
                        JSONObject balance = jObj.getJSONObject("balance");

                        bal= balance.getString("balance");


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
                params.put("topup", String.valueOf(amount));
                //params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

return bal;
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(TopupActivity.this, LoginActivity.class);
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

}
