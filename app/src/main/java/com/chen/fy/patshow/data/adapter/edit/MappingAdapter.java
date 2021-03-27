package com.chen.fy.patshow.data.adapter.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.fy.patshow.R;

import java.util.List;

public class MappingAdapter extends RecyclerView.Adapter<MappingAdapter.ViewHolder>{

    private List<Integer> mappingDrawables;
    private IOnClickMappingListener mappingListener;

    public MappingAdapter(List<Integer> mappingDrawables, IOnClickMappingListener clickColorListener){
        this.mappingDrawables = mappingDrawables;
        this.mappingListener = clickColorListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.mapping_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int colorDrawableID = mappingDrawables.get(position);
        holder.ivMapping.setImageResource(colorDrawableID);
        holder.ivMapping.setOnClickListener(v -> {
            mappingListener.clickMapping(position);
        });
    }

    @Override
    public int getItemCount() {
        return mappingDrawables.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMapping;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMapping = itemView.findViewById(R.id.iv_mapping);
        }
    }

    public interface IOnClickMappingListener{
        void clickMapping(int position);
    }
}
