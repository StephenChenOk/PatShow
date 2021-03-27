package com.chen.fy.patshow.data.adapter.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.chen.fy.patshow.R;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{

    private List<Integer> colorDrawables;
    private IOnClickColorListener colorListener;

    public ColorAdapter(List<Integer> colorDrawables, IOnClickColorListener clickColorListener){
        this.colorDrawables = colorDrawables;
        this.colorListener = clickColorListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.color_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int colorDrawableID = colorDrawables.get(position);
        holder.ivColor.setImageResource(colorDrawableID);
        holder.ivColor.setOnClickListener(v -> {
            colorListener.clickColor(position);
        });
    }

    @Override
    public int getItemCount() {
        return colorDrawables.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivColor;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivColor = itemView.findViewById(R.id.iv_color);
        }
    }

    public interface IOnClickColorListener{
        void clickColor(int position);
    }
}
