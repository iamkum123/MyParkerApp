package info.MyParker.Apps.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import info.MyParker.Apps.R;

public class GenerateReceiptActivity extends Activity {
    TextView name;
    TextView location;
    TextView price;
    TextView date;
    TextView vehicle;
    TextView offense;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        name= (TextView)findViewById(R.id.name);
        location= (TextView)findViewById(R.id.location);
        price= (TextView)findViewById(R.id.price);
        date= (TextView)findViewById(R.id.date);
        vehicle= (TextView)findViewById(R.id.vehicle);
        offense= (TextView)findViewById(R.id.offense);
        status= (TextView)findViewById(R.id.status);

        Bundle intent = getIntent().getExtras();
       String price2 = intent.getString("price");
        String name2 = intent.getString("name");
        String location2 = intent.getString("location");
        String vehicle2 = intent.getString("vehicle");
        String date2 = intent.getString("date");
        String offense2 = intent.getString("offense");

        name.setText("Dear "+name2);
        location.setText("Location: "+location2);
        price.setText("Price: "+price2);
        date.setText("Date issued: "+date2);
        vehicle.setText("Vehicle: "+vehicle2);
        offense.setText("Offense: "+offense2);
        status.setText("Status: "+"Paid");


    }
//    @Override
//    public void onBackPressed(){
//        Intent i = new Intent(getApplicationContext(),
//                SummonHistoryActivity.class);
//        startActivity(i);
//        finish();
//    }
@Override
public void onBackPressed() {
    super.onBackPressed();
}

}
