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

public class PaymentActivity extends AppCompatActivity {

    private TextView Tamount;
    private Button btnPay;
    private int latestBalance;
    private SQLiteHandler db;
    private RadioButton a,b,c,d;
    private ProgressDialog pDialog;
    private SessionManager session;
    private TextView newBalance;
    private String email;
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private Button btnHome;
    private Button btnTopup;
    private Button btnHistory;
    private Button btnProfile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().hide();

        Bundle intent = getIntent().getExtras();
        final int amount = intent.getInt("amount");
         //latestBalance = intent.getInt("latestBalance");

        Tamount=(TextView)findViewById(R.id.topup);
        Tamount.setText(Integer.toString(amount));

        a = (RadioButton) findViewById(R.id.a);
        b = (RadioButton) findViewById(R.id.b);
        c = (RadioButton) findViewById(R.id.c);
        d = (RadioButton) findViewById(R.id.d);

        btnPay=(Button)findViewById(R.id.btntopup2);

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

        btnPay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (a.isChecked())
                 updateNewBalance(email,amount);
                else if (b.isChecked())
                    updateNewBalance(email,amount);
                else if (c.isChecked())
                    updateNewBalance(email,amount);
                else if (d.isChecked())
                    updateNewBalance(email,amount);
                else
                    Toast.makeText(getApplicationContext(),
                            "No payment method is selected. Please select one", Toast.LENGTH_LONG).show();

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
                                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.topup:
                                Intent intent2 = new Intent(PaymentActivity.this, TopupActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.history:
                                Intent intent3 = new Intent(PaymentActivity.this, HistoryActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.profile:
                                Intent intent4 = new Intent(PaymentActivity.this, ProfileActivity.class);
                                startActivity(intent4);
                                break;
                        }
                        return true;
                    }
                });

    }

    private void updateNewBalance(final String email, final int amount ){
        String tag_string_req = "req_topupbalance";
        final String[] bal = new String[1];

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

                        JSONObject balance = jObj.getJSONObject("balance");

                        Intent intent = new Intent(PaymentActivity.this, TopupSuccessActivity.class);
                        intent.putExtra( "latestBalance",Double.parseDouble(balance.getString("balance")));
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
                params.put("email", email);
                params.put("topup", String.valueOf(amount));
                //params.put("password", password);

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
        Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
