package info.MyParker.Apps.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.MyParker.Apps.R;
import info.MyParker.Apps.app.AppConfig;
import info.MyParker.Apps.app.AppController;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private TextView txtName;
	private TextView chksummon;
	private TextView txtBalance;
	private TextView currentpayment;

    private Button btnpay;

	private SQLiteHandler db;
	private ProgressDialog pDialog;
	private SessionManager session;
	private String bal;
	private String vehicle;
	private String location;
	private String endtime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().hide();

		txtName = (TextView) findViewById(R.id.name);
		chksummon = (TextView) findViewById(R.id.chksummon);
		//btnLogout = (Button) findViewById(R.id.btnLogout);
		txtBalance=(TextView)findViewById(R.id.balance);

		btnpay=(Button)findViewById(R.id.pay);
		currentpayment=(TextView)findViewById(R.id.current);


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
		String email = user.get("email");





		// Displaying the user details on the screen
		txtName.setText(name);

		if(isNetworkAvailable()) {// check whether is connected to intenet, if not, stop the json request
			checkBalance(email);//retrieve current balance of the user
			showpaymentstatus(email);// retrieve current active payment info
			checksummonstatus(email);//check summon status
		}
		else{
			Toast.makeText(getApplicationContext(),
					"No Internet, Please try again", Toast.LENGTH_LONG).show();
		}

		 BottomNavigationView bottomNavigationView = (BottomNavigationView)
				findViewById(R.id.navigationView);

		bottomNavigationView.setOnNavigationItemSelectedListener(
				new BottomNavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						switch (item.getItemId()) {
							case R.id.home:
								Intent intent = new Intent(MainActivity.this, MainActivity.class);
								startActivity(intent);
								break;
							case R.id.topup:
								Intent intent2 = new Intent(MainActivity.this, TopupActivity.class);
								startActivity(intent2);
								break;
							case R.id.history:
								Intent intent3 = new Intent(MainActivity.this, HistoryActivity.class);
								startActivity(intent3);
								break;
							case R.id.profile:
								Intent intent4 = new Intent(MainActivity.this, ProfileActivity.class);
								startActivity(intent4);
								break;
						}
						return true;
					}
				});

		btnpay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MakePaymentActivity.class);
				startActivity(intent);

			}
		});


	}



	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void checkBalance(final String email ){
		String tag_string_req = "req_balance";


		StringRequest strReq = new StringRequest(Request.Method.POST,
				AppConfig.URL_BALANCE, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, "Balance Response: " + response.toString());
				//hideDialog();

				try {
					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");

					// Check for error node in json
					if (!error) {

						JSONObject balance = jObj.getJSONObject("balance");
						 bal= balance.getString("balance");
						//Toast.makeText(getApplicationContext(),
								//bal, Toast.LENGTH_LONG).show();
						txtBalance.setText("RM "+bal);


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
				//params.put("password", password);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


	}

	private void showpaymentstatus(final String email ){
		String tag_string_req = "req_parkingstatus";


		StringRequest strReq = new StringRequest(Request.Method.POST,
				AppConfig.URL_SHOWPARKINGSTATUS, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, "parking payment Response: " + response.toString());
				//hideDialog();

				try {

						JSONArray list = new JSONArray(response);
						//boolean error = list.getBoolean("error");
					if(list.length()==0){
						currentpayment.setText("No active service");
					}
					else{
						for(int i=0; i<list.length();i++) {
							// User successfully stored in MySQL
							JSONObject data = list.getJSONObject(i);

							vehicle = data.getString("vehicle");
							location = data.getString("location");
							endtime = data.getString("endtime");


							currentpayment.setText(vehicle + ", location: " + location + ", Service ends at " + endtime);



						}}
				} catch (JSONException e) {
					// JSON error
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "parking payment Error: " + error.getMessage());
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
				//params.put("password", password);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


	}

	private void checksummonstatus(final String email ){
		String tag_string_req = "req_summonstatus";


		StringRequest strReq = new StringRequest(Request.Method.POST,
				AppConfig.URL_CHECKSUMMONSTATUS, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Log.d(TAG, "summon status Response: " + response.toString());
				//hideDialog();

				try {

					JSONObject jObj = new JSONObject(response);
					boolean error = jObj.getBoolean("error");

					// Check for error node in json
					if (!error) {

						String res=jObj.getString("response");
						if(res.equals("yes")){
							chksummon.setText("You have outstanding summon. Please pay now ");
							chksummon.setTextColor(Color.RED);

					}

						else if(res.equals("no")){
							chksummon.setText("You have no outstanding summon. ");
							chksummon.setTextColor(Color.GREEN);}


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
				Log.e(TAG, "parking summon Error: " + error.getMessage());
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
}
