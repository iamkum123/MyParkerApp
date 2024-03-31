package info.MyParker.Apps.app;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.MyParker.Apps.R;
import info.MyParker.Apps.helper.payment;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentViewHolder> {


    private Context mCtx;
    private List<payment> paymentList;

    public PaymentHistoryAdapter(Context mCtx, List<payment> paymentList) {
        this.mCtx = mCtx;
        this.paymentList = paymentList;
    }

    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_paymenthistory, null);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, int position) {
        payment payment = paymentList.get(position);

        //loading the image
       /* Glide.with(mCtx)
                .load(product.getImage())
                .into(holder.imageView);*/
        holder.textLocation.setText(payment.getLocation());
        holder.textPrice.setText("RM "+ payment.getPrice());
        holder.textStart.setText(payment.getStart());
        holder.textEnd.setText(payment.getEnd());
        holder.textVehicle.setText(payment.getVehicle());
        if(payment.getStatus().equals("Active")) {
            holder.textStatus.setText(payment.getStatus());
            holder.textStatus.setTextColor(Color.GREEN);

        }
        else{
            holder.textStatus.setText(payment.getStatus());
            holder.textStatus.setTextColor(Color.RED);

        }

    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {

        TextView textLocation, textPrice, textStart, textEnd, textVehicle,textStatus;
        ImageView imageView;

        public PaymentViewHolder(View itemView) {
            super(itemView);

            textLocation = itemView.findViewById(R.id.location);
            textPrice = itemView.findViewById(R.id.price);
            textStart = itemView.findViewById(R.id.starttime);
            textEnd = itemView.findViewById(R.id.endtime);
            textVehicle = itemView.findViewById(R.id.vehicle);
            textStatus = itemView.findViewById(R.id.status);

        }
    }
}