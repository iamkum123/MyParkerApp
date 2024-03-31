package info.MyParker.Apps.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import info.MyParker.Apps.app.SummonHistoryAdapter;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;
import info.MyParker.Apps.helper.summon;

public class SummonHistoryActivity extends AppCompatActivity {
    private static final String TAG = SummonHistoryActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    Spinner filter;
    int options;

    RecyclerView recyclerView;
    //ProductAdapter adapter;

    List<summon> summonList;
    List<String>summonOption=new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summonhistory);

        filter=findViewById(R.id.filter);
        summonOption.add("Show All");
        summonOption.add("Show only Paid");
        summonOption.add("Show only Unpaid");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SummonHistoryActivity.this, android.R.layout.simple_spinner_dropdown_item, summonOption);

        filter.setAdapter(adapter);

        summonList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        final String name = user.get("name");
        final String email = user.get("email");

        //summonList(email,0);

        //adapter = new ProductAdapter(this, productList);
        //recycleView.setAdapter(adapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                //if(initial==true) {// only let the calculation run after getting the unit price, to avoid crash

                String option = filter.getSelectedItem().toString();
                // Toast.makeText(getApplicationContext(), unit.getText().toString()
                // , Toast.LENGTH_LONG).show();

                if(option.equals("Show All")){
                    options=0;
                    summonList.clear();
                    summonList(email,options,name);

                }
                else if(option.equals("Show only Paid")){
                    options=1;
                    summonList.clear();
                    summonList(email,options,name);

                }
                else{
                    options=2;
                    summonList.clear();
                    summonList(email,options,name);

                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

            }

        });
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),
                HistoryActivity.class);
        startActivity(i);
        finish();
    }




    private void summonList(final String email, final int option, final String name) {
        String tag_string_req = "req_summonDetails";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SUMMONHISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray list = new JSONArray(response);
                            //boolean error = list.getBoolean("error");
                            for(int i=0; i<list.length();i++) {
                                // User successfully stored in MySQL
                                JSONObject data = list.getJSONObject(i);

                                if(option==0) {
                                    summonList.add(new summon(
                                            data.getString("id"),
                                            data.getString("location"),
                                            data.getString("date"),
                                            data.getString("charges"),
                                            data.getString("description"),
                                            data.getString("status"),
                                            data.getString("vehicle")
                                    ));
                                }
                                else if (option==1){
                                    if(data.getString("status").equals("Paid")) {
                                        summonList.add(new summon(
                                                data.getString("id"),
                                                data.getString("location"),
                                                data.getString("date"),
                                                data.getString("charges"),
                                                data.getString("description"),
                                                data.getString("status"),
                                                data.getString("vehicle")
                                        ));
                                    }
                                }

                                else if (option==2){
                                    if(data.getString("status").equals("Unpaid")) {
                                        summonList.add(new summon(
                                                data.getString("id"),
                                                data.getString("location"),
                                                data.getString("date"),
                                                data.getString("charges"),
                                                data.getString("description"),
                                                data.getString("status"),
                                                data.getString("vehicle")
                                        ));
                                    }
                                }

                                //Toast.makeText(getApplicationContext(),
                                //location+price+starttime+endtime, Toast.LENGTH_LONG).show();

                                // payment payment= new payment(location,price,starttime,endtime);

                                // paymentList.add(payment);

                                // Inserting row in users table
                                // db.addUser(name, email, uid, created_at);

                                // Toast.makeText(getApplicationContext(), data.getString("Price"),
                                //   Toast.LENGTH_LONG).show();

                            }
                            SummonHistoryAdapter adapter = new SummonHistoryAdapter(SummonHistoryActivity.this,summonList,name);
                            linearLayoutManager.setReverseLayout(true);
                            linearLayoutManager.setStackFromEnd(true);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                            if(adapter.getItemCount()==0){

                                    Toast.makeText(SummonHistoryActivity.this, "No Data Found",Toast.LENGTH_SHORT).show();

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
