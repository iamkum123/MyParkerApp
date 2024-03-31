package info.MyParker.Apps.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;
import info.MyParker.Apps.helper.payment;
import info.MyParker.Apps.app.AppConfig;
import info.MyParker.Apps.app.AppController;
import info.MyParker.Apps.app.PaymentHistoryAdapter;

public class PaymentHistoryActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    RecyclerView recyclerView;
    //ProductAdapter adapter;

    List<payment> paymentList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymenthistory);

        paymentList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        paymentList(email);

        //adapter = new ProductAdapter(this, productList);
        //recycleView.setAdapter(adapter);

    }

    private void paymentList(final String email) {
        String tag_string_req = "req_paymentDetails";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_PAYMENTHISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray list = new JSONArray(response);
                            //boolean error = list.getBoolean("error");
                            for(int i=0; i<list.length();i++) {
                                // User successfully stored in MySQL
                                JSONObject data = list.getJSONObject(i);

                                paymentList.add(new payment(
                                data.getString("Location"),
                                 data.getString("Price"),
                                 data.getString("Start_Time"),
                                 data.getString("End_Time"),
                                        data.getString("Vehicle"),
                                        data.getString("Status")

                                        ));


                                //Toast.makeText(getApplicationContext(),
                                        //location+price+starttime+endtime, Toast.LENGTH_LONG).show();

                               // payment payment= new payment(location,price,starttime,endtime);

                               // paymentList.add(payment);

                                // Inserting row in users table
                               // db.addUser(name, email, uid, created_at);

                               // Toast.makeText(getApplicationContext(), data.getString("Price"),
                                     //   Toast.LENGTH_LONG).show();

                            }
                            PaymentHistoryAdapter adapter = new PaymentHistoryAdapter(PaymentHistoryActivity.this,paymentList);
                            linearLayoutManager.setReverseLayout(true);
                            linearLayoutManager.setStackFromEnd(true);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                            if(adapter.getItemCount()==0){

                                Toast.makeText(PaymentHistoryActivity.this, "No Data Found",Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
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