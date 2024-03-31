package info.MyParker.Apps.officer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import info.MyParker.Apps.R;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;

public class ParkStatActivity extends AppCompatActivity {
    private TextView tv_vehicleNum;
    private TextView tv_user;
    private TextView tv_paymentStatus;
    private TextView tv_parkingStatus;
    private Button but_issueSummon;
    private Button but_scanNewVehicle;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_park_stat);
        Bundle intent = getIntent().getExtras();
        final String uname = intent.getString("name");
        String stat = intent.getString("status");
        final String vehicle = intent.getString("vehicle");
        final String location = intent.getString("location");
        //String location = intent.getString("location");

        tv_vehicleNum = (TextView) findViewById(R.id.tv_VehicleNum);
        tv_user = (TextView) findViewById(R.id.tv_User);
        tv_paymentStatus = (TextView) findViewById(R.id.tv_payStatus);

        but_issueSummon = (Button) findViewById(R.id.btnIssueSum);
        but_scanNewVehicle = (Button) findViewById(R.id.btnScan);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        //checkvehicle(vehicleNum);

        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        tv_vehicleNum.setText(vehicle);
        tv_user.setText(uname);
        tv_paymentStatus.setText(stat);
        //tv_vehicleNum.setText(vehicleNum);

        but_issueSummon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParkStatActivity.this, IssueSummonActivity.class);
                intent.putExtra("name", uname);
                intent.putExtra("vehicle", vehicle);
                intent.putExtra("location", location);
                startActivity(intent);
            }
        });

        but_scanNewVehicle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(ParkStatActivity.this, ScanActivity.class);
               // startActivity(intent);
                finish();
            }
        });
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(ParkStatActivity.this, OfficerLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
