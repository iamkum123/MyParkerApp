package info.MyParker.Apps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import info.MyParker.Apps.R;
import info.MyParker.Apps.app.ImageAdapter;

public class HistoryActivity extends AppCompatActivity {

    private Button btnPayment;
    private Button btnSummon;

    private Button btnHome;
    private Button btnTopup;
    private Button btnHistory;
    private Button btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().hide();

        ViewPager viewPager;

        btnPayment= (Button) findViewById(R.id.pgpayhis);
        btnSummon= (Button) findViewById(R.id.pgsumhis);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ImageAdapter viewPagerAdapter = new ImageAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);

        btnPayment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, PaymentHistoryActivity.class);
                startActivity(intent);

            }
        });

        btnSummon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, SummonHistoryActivity.class);
                startActivity(intent);

            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigationView);
        Menu menu= bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.topup:
                                Intent intent2 = new Intent(HistoryActivity.this, TopupActivity.class);
                                startActivity(intent2);
                                break;
                            case R.id.history:
                                Intent intent3 = new Intent(HistoryActivity.this, HistoryActivity.class);
                                startActivity(intent3);
                                break;
                            case R.id.profile:
                                Intent intent4 = new Intent(HistoryActivity.this, ProfileActivity.class);
                                startActivity(intent4);
                                break;
                        }
                        return true;
                    }
                });


    }
    }
