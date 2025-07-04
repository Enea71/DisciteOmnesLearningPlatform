package com.example.disciteomneslearningplatform.data.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disciteomneslearningplatform.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import API.GroupAPI;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.VH> {

    // Internal list of GroupResponse
    private final List<GroupAPI.GroupResponse> items = new ArrayList<>();
    private final Consumer<GroupAPI.GroupResponse> onClick;
    private final Consumer<GroupAPI.GroupResponse> onDelete;
    private final Consumer<GroupAPI.GroupResponse> onManage;
    private final @LayoutRes int layoutRes;

    public GroupAdapter(@LayoutRes int layoutRes, Consumer<GroupAPI.GroupResponse> onClick, Consumer<GroupAPI.GroupResponse> onDelete,Consumer<GroupAPI.GroupResponse> onManage) {
        this.onClick = onClick;
        this.layoutRes = layoutRes;
        this.onDelete   = onDelete;
        this.onManage = onManage;

    }
    public GroupAdapter(@LayoutRes int layoutRes, Consumer<GroupAPI.GroupResponse> onClick) {
        this.onClick = onClick;
        this.layoutRes = layoutRes;
        this.onDelete=null;
        this.onManage = null;
    }

    /** Replace current list and refresh UI */
    public void setGroups(List<GroupAPI.GroupResponse> groups) {
        items.clear();
        items.addAll(groups);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        GroupAPI.GroupResponse g = items.get(pos);
        holder.tvName.setText(g.name);
        holder.tvCount.setText(g.members.size() + " members");

        // whole-card click
        holder.itemView.setOnClickListener(v -> onClick.accept(g));

        // manage-icon click → onManage
        if (layoutRes == R.layout.item_group_owner && onManage != null) {
            holder.btnManage.setOnClickListener(v -> onManage.accept(g));
        }

        // delete-icon click → onDelete
        if (layoutRes == R.layout.item_group_owner && onDelete != null) {
            holder.btnDelete.setOnClickListener(v -> onDelete.accept(g));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvCount;
        View btnDelete,btnManage;
        VH(View item) {
            super(item);
            tvName  = item.findViewById(R.id.tvName);
            tvCount = item.findViewById(R.id.tvCount);
            btnDelete = item.findViewById(R.id.btnDelete);
            btnManage = item.findViewById(R.id.btnManage);
        }
    }
    public void removeGroup(GroupAPI.GroupResponse group) {
        int index = items.indexOf(group);
        if (index >= 0) {
            items.remove(index);
            notifyItemRemoved(index);
        }
    }
}
