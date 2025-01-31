package com.example.student_dynamic_resource_allocation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_dynamic_resource_allocation.R;
import com.example.student_dynamic_resource_allocation.models.ParkingAreaModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private List<ParkingAreaModel> parkingList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ParkingAreaModel parkingModel);
        void onLocateClick(ParkingAreaModel parkingModel);
    }

    public ParkingAdapter(List<ParkingAreaModel> parkingList, OnItemClickListener listener) {
        this.parkingList = parkingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_layout, parent, false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        ParkingAreaModel parking = parkingList.get(position);
        holder.parkingName.setText(parking.getName());
        holder.availableSlots.setText(String.valueOf(parking.getAvailableSlots()));

        holder.cardView.setOnClickListener(v -> listener.onItemClick(parking));

        holder.imageView.setOnClickListener(v -> listener.onLocateClick(parking));
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    static class ParkingViewHolder extends RecyclerView.ViewHolder {
        TextView parkingName, availableSlots;
        ImageView imageView;
        MaterialCardView cardView;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.available); // Add an ID to MaterialCardView in XML if needed
            parkingName = itemView.findViewById(R.id.parkingName);
            availableSlots = itemView.findViewById(R.id.availableSlots);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
