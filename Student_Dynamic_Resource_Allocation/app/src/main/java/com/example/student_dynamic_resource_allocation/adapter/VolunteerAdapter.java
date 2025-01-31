package com.example.student_dynamic_resource_allocation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_dynamic_resource_allocation.R;
import com.example.student_dynamic_resource_allocation.models.VolunteerModel;

import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder> {

    private final List<VolunteerModel> volunteerList;

    public VolunteerAdapter(List<VolunteerModel> volunteerList) {
        this.volunteerList = volunteerList;
    }

    @NonNull
    @Override
    public VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.volunteer_layout, parent, false);
        return new VolunteerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerViewHolder holder, int position) {
        VolunteerModel volunteer = volunteerList.get(position);
        holder.nameTextView.setText(volunteer.getName());
        holder.emailTextView.setText(volunteer.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    static class VolunteerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;

        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
            emailTextView = itemView.findViewById(R.id.tvPhoneNumber);
        }
    }
}
