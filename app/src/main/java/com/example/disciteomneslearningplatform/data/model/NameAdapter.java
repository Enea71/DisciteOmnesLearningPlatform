package com.example.disciteomneslearningplatform.data.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.disciteomneslearningplatform.R;


import java.util.ArrayList;
import java.util.List;

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.VH> {
    private final List<String> items = new ArrayList<>();
    private final int itemLayoutRes;
    public interface OnDeleteClickListener {
        void onDeleteClicked(String username);
    }

    private OnDeleteClickListener deleteListener;

    public void setOnDeleteClickListener(OnDeleteClickListener l) {
        this.deleteListener = l;
    }
    public void setItems(List<String> names) {
        items.clear();
        if (names != null) items.addAll(names);
        notifyDataSetChanged();
    }
    public NameAdapter(int itemLayoutRes) {
        this.itemLayoutRes = itemLayoutRes;
    }
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(itemLayoutRes, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String username = items.get(position);
        holder.name.setText(username);

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null && username != null) {
                deleteListener.onDeleteClicked(username);
            }
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageButton btnDelete;
        VH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvMemberName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

