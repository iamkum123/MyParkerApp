package info.MyParker.Apps.app;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.MyParker.Apps.R;
import info.MyParker.Apps.activity.DeleteVehicleActivity;
import info.MyParker.Apps.helper.vehicle;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    // private final View.OnClickListener listener = new MyOnClickListener();
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private Context mCtx;
    private List<vehicle> vehicleList;

    public VehicleAdapter(Context mCtx, List<vehicle> vehicle) {
        this.mCtx = mCtx;
        this.vehicleList = vehicle;
    }

    @Override
    public VehicleAdapter.VehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_vehicle, null);
        //view.setOnClickListener(listener);
        return new VehicleAdapter.VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VehicleAdapter.VehicleViewHolder holder, int position) {
        //final summon summon = summonList.get(position);
        final vehicle vehicle=vehicleList.get(position);

        holder.textVehicle.setText(vehicle.getVehicle());

        holder.Vehiclelayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                    Intent startActivity = new Intent(mCtx, DeleteVehicleActivity.class);
                    startActivity.putExtra("vehicleno", vehicle.getVehicle());
                    mCtx.startActivity(startActivity);

            }

        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {

        TextView textVehicle;
        ConstraintLayout Vehiclelayout;

        public VehicleViewHolder(View itemView) {
            super(itemView);

            textVehicle = itemView.findViewById(R.id.vehicleno);
           Vehiclelayout= itemView.findViewById(R.id.vehiclelayout);
        }
    }
}
