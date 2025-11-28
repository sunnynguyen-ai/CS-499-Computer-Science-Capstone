package com.example.projectthree_sunnynguyen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Adapter for displaying events in a RecyclerView (uses row_event.xml). */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    public interface OnRowActionListener {
        void onDeleteClicked(EventsGridActivity.Event event);
    }

    private final List<EventsGridActivity.Event> events;
    private final OnRowActionListener listener;

    public EventsAdapter(List<EventsGridActivity.Event> events, OnRowActionListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventsGridActivity.Event event = events.get(position);
        holder.tvName.setText(event.name);
        holder.tvDate.setText(event.date);
        holder.tvTime.setText(event.time);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClicked(event);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvTime;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tvName);
            tvDate    = itemView.findViewById(R.id.tvDate);
            tvTime    = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete); // <- matches XML
        }
    }
}