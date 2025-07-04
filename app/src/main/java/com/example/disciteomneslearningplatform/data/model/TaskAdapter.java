package com.example.disciteomneslearningplatform.data.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disciteomneslearningplatform.R;

import java.util.ArrayList;
import java.util.List;

import API.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {
    private final List<Task> items = new ArrayList<>();
    private final int layoutRes;

    public TaskAdapter(int layoutRes) {
        this.layoutRes = layoutRes;
    }

    public void setItems(List<Task> tasks) {
        items.clear();
        if (tasks != null) items.addAll(tasks);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Task t = items.get(position);
        holder.title.setText(t.title);
        holder.desc.setText(t.description);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView title, desc;
        VH(View v) {
            super(v);
            title = v.findViewById(R.id.tvTaskTitle);
            desc  = v.findViewById(R.id.tvTaskDesc);
        }
    }
}
