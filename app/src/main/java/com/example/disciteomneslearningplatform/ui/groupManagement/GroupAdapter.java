package com.example.disciteomneslearningplatform.ui.groupManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private final @LayoutRes int layoutRes;

    /**
     * @param onClick  handler that receives the clicked GroupResponse
     */
    public GroupAdapter(@LayoutRes int layoutRes, Consumer<GroupAPI.GroupResponse> onClick) {
        this.onClick = onClick;
        this.layoutRes = layoutRes;
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
        // Use the correct data type here:
        GroupAPI.GroupResponse g = items.get(pos);
        holder.tvName.setText(g.name);
        holder.tvCount.setText(g.members.size() + " members");
        holder.itemView.setOnClickListener(_v -> onClick.accept(g));
        holder.btnDelete.setOnClickListener(_v -> {
            // e.g. call back into your Fragment/Activity:
            //   onDeleteClicked.accept(g);
            // or handle deletion right here:

            Toast.makeText(
                    holder.itemView.getContext(),
                    "Deleted group “" + g.name + "”",
                    Toast.LENGTH_SHORT
            ).show();
        });
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
}
