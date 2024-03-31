package info.MyParker.Apps.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import info.MyParker.Apps.R;
import info.MyParker.Apps.helper.SQLiteHandler;
import info.MyParker.Apps.helper.SessionManager;

public class TopupSuccessActivity extends Activity {
    private TextView newBalance;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topupsuccess);

        newBalance=(TextView)findViewById(R.id.newBalance);

        Bundle intent = getIntent().getExtras();
        Double latestBalance = intent.getDouble("latestBalance");

        newBalance.setText(Double.toString(latestBalance));
        Toast.makeText(getApplicationContext(),
                "Redirecting to home page in 3 seconds...", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startActivity = new Intent(TopupSuccessActivity.this, MainActivity.class);
                startActivity(startActivity);
                finish();
            }
        }, 3000);
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(TopupSuccessActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    }
