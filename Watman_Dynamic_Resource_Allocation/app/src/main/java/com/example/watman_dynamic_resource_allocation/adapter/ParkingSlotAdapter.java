package com.example.watman_dynamic_resource_allocation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watman_dynamic_resource_allocation.R;
import com.example.watman_dynamic_resource_allocation.models.ParkingSlotModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ParkingSlotAdapter extends RecyclerView.Adapter<ParkingSlotAdapter.ParkingSlotViewHolder> {

    private final Context context;
    private final List<ParkingSlotModel> parkingSlotList;
    private final OnParkingSlotClickListener listener;

    public interface OnParkingSlotClickListener {
        void onParkingSlotClick(ParkingSlotModel parkingSlot);
    }

    public ParkingSlotAdapter(Context context, List<ParkingSlotModel> parkingSlotList, OnParkingSlotClickListener listener) {
        this.context = context;
        this.parkingSlotList = parkingSlotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParkingSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slot_layout, parent, false);
        return new ParkingSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingSlotViewHolder holder, int position) {
        ParkingSlotModel parkingSlot = parkingSlotList.get(position);
        holder.slotID.setText(parkingSlot.getId());

        // Set OnClickListener on the card
        holder.cardView.setOnClickListener(v -> listener.onParkingSlotClick(parkingSlot));
    }

    @Override
    public int getItemCount() {
        return parkingSlotList.size();
    }

    // Method to remove a parking slot and update the UI
    public void removeParkingSlot(ParkingSlotModel parkingSlot) {
        int position = parkingSlotList.indexOf(parkingSlot);
        if (position != -1) {
            parkingSlotList.remove(position);  // Remove the item from the list
            notifyItemRemoved(position);        // Notify the adapter about the removal
            notifyItemRangeChanged(position, parkingSlotList.size()); // Update the remaining items
        }
    }

    // Method to add a parking slot and update the UI
    public void addParkingSlot(ParkingSlotModel parkingSlot) {
        parkingSlotList.add(parkingSlot);  // Add the item to the list
        notifyItemInserted(parkingSlotList.size() - 1); // Notify the adapter about the new insertion
    }

    public static class ParkingSlotViewHolder extends RecyclerView.ViewHolder {
        TextView slotID;
        MaterialCardView cardView;

        public ParkingSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            slotID = itemView.findViewById(R.id.slotID);
            cardView = itemView.findViewById(R.id.available);
        }
    }
}
