package info.MyParker.Apps.officer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import info.MyParker.Apps.R;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;

public class OfficerProfileActivity extends Activity {
    private Button btnHome;
    private Button btnTopup;
    private Button btnHistory;
    private Button btnProfile;
    private Button btnLogout;
    private TextView Name;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officer_profile);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnHome= (Button) findViewById(R.id.home);
        Name=(TextView)findViewById(R.id.name);
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
        btnHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfficerProfileActivity.this,OfficerMainActivity.class);
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
        Intent intent = new Intent(OfficerProfileActivity.this, OfficerLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
