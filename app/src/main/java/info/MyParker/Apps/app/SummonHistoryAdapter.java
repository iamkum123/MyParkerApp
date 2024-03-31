package info.MyParker.Apps.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.MyParker.Apps.R;
import info.MyParker.Apps.activity.GenerateReceiptActivity;
import info.MyParker.Apps.activity.PaySummonActivity;
import info.MyParker.Apps.helper.summon;

public class SummonHistoryAdapter extends RecyclerView.Adapter<SummonHistoryAdapter.SummonViewHolder> {
    private ProgressDialog pDialog;
    Runnable progressRunnable;
    Handler pdCanceller = new Handler();


    // private final View.OnClickListener listener = new MyOnClickListener();

    private Context mCtx;
    private List<summon> summonList;
    private String name;

    public SummonHistoryAdapter(Context mCtx, List<summon> summonList,String name) {
        this.mCtx = mCtx;
        this.summonList = summonList;
        this.name=name;
    }

    @Override
    public SummonHistoryAdapter.SummonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_summonhistory, null);
       // ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

       // view.setMinimumHeight((int) (parent.getMeasuredHeight() * .3));

        //view.setOnClickListener(listener);
        return new SummonHistoryAdapter.SummonViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SummonHistoryAdapter.SummonViewHolder holder, int position) {
        final summon summon = summonList.get(position);


        holder.textLocation.setText(summon.getLocation());
        holder.textDate.setText("Issued Date: "+summon.getDate());
        holder.textPrice.setText("RM "+ summon.getPrice());
        holder.textDesc.setText(summon.getDesc());

        if(summon.getStatus().equals("Paid")){
        holder.textStatus.setText(summon.getStatus());
        holder.textStatus.setTextColor(Color.GREEN);

        }
        else{
            holder.textStatus.setText(summon.getStatus());
            holder.textStatus.setTextColor(Color.RED);

        }

        holder.textVehicle.setText(summon.getVehicle());

        holder.Summonlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {



                if(summon.getStatus().equals("Unpaid")) {
                    Intent startActivity = new Intent(mCtx, PaySummonActivity.class);
                    startActivity.putExtra("price", Integer.parseInt(summon.getPrice()));
                    startActivity.putExtra("summonid", Integer.parseInt(summon.getID()));
                    mCtx.startActivity(startActivity);
                }
                else{

                    pDialog.setMessage("Generating Receipt");
                    showDialog();
                    //pdCanceller.postDelayed(progressRunnable, 1000);
                    // progressRunnable = new Runnable() {

                        //@Override
                        //public void run() {
                            pDialog.cancel();
                            pDialog.dismiss();

                            Intent startActivity = new Intent(mCtx, GenerateReceiptActivity.class);
                            startActivity.putExtra("price", summon.getPrice());
                            startActivity.putExtra("location", summon.getLocation());
                            startActivity.putExtra("name", name);
                            startActivity.putExtra("date", summon.getDate());
                            startActivity.putExtra("offense", summon.getDesc());
                            startActivity.putExtra("vehicle", summon.getVehicle());

                            mCtx.startActivity(startActivity);

                            Toast.makeText(mCtx.getApplicationContext(),
                                    "The summon selected has been paid, generating receipt", Toast.LENGTH_LONG)
                                    .show();
                        }
                   // };

                //}
            }

        });
    }

    @Override
    public int getItemCount() {
        return summonList.size();
    }

    class SummonViewHolder extends RecyclerView.ViewHolder {

        TextView textLocation, textPrice, textDate, textStatus, textDesc, textVehicle;
        ConstraintLayout Summonlayout;

        public SummonViewHolder(View itemView) {
            super(itemView);

            textLocation = itemView.findViewById(R.id.location);
            textPrice = itemView.findViewById(R.id.price);
            textDate = itemView.findViewById(R.id.date);
            textDesc = itemView.findViewById(R.id.desc);
            textStatus = itemView.findViewById(R.id.status);
            textVehicle = itemView.findViewById(R.id.vehicle);
            Summonlayout= itemView.findViewById(R.id.summonlayout);

            pDialog = new ProgressDialog(mCtx);
            pDialog.setCancelable(true);


        }
    }
 /*   public void bind(final ContentItem item, final OnItemClickListener listener) {
        name.setText(item.name);
        Picasso.with(itemView.getContext()).load(item.imageUrl).into(image);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });*/
 private void showDialog() {
     //if (!pDialog.isShowing()){
         pDialog.show();

 }

}