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

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.VH> {
    private final List<String> items = new ArrayList<>();

    public void setItems(List<String> names) {
        items.clear();
        if (names != null) items.addAll(names);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.name.setText(items.get(position));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name;
        VH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvMemberName);
        }
    }
}

