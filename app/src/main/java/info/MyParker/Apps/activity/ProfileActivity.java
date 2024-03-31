package info.MyParker.Apps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import info.MyParker.Apps.R;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout;
    private Button addVehicle;
    private TextView Name;
    private Button qrcode;
    private SQLiteHandler db;
    private SessionManager session;

    private Button chgname;
    private Button chgpassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        getSupportActionBar().hide();

        Name=(TextView)findViewById(R.id.name);
        chgname=(Button)findViewById(R.id.chgname);
        chgpassword=(Button)findViewById(R.id.chgpassword);
        addVehicle=(Button)findViewById(R.id.addvehicle);
        qrcode=(Button)findViewById(R.id.qr);


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

        Name.setText(name);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationView);
        Menu menu= bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.topup:
                                Intent intent2 = new Intent(ProfileActivity.this, TopupActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.history:
                                Intent intent3 = new Intent(ProfileActivity.this, HistoryActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.profile:
                                Intent intent4 = new Intent(ProfileActivity.this, ProfileActivity.class);
                                startActivity(intent4);
                                break;
                        }
                        return true;
                    }
                });
        chgname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangeNameActivity.class);
                startActivity(intent);

            }
        });
        chgpassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);

            }
        });

        addVehicle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CheckVehicleActivity.class);
                startActivity(intent);

            }
        });

        qrcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, QRCodeActivity.class);
                startActivity(intent);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();



        // Launching the login activity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    }
