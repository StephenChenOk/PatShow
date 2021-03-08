package com.chen.fy.patshow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.interfaces.OnHomeItemClickListener;
import com.chen.fy.patshow.model.HomeItem;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context mContext;
    private int mResourceId;

    private List<HomeItem> mList;
    private OnHomeItemClickListener mClickListener;

    public HomeAdapter(Context context, int resourceId) {
        this.mContext = context;
        this.mResourceId = resourceId;
    }

    public void setHomeList(List<HomeItem> list) {
        this.mList = list;
    }

    public void setOnItemClickListener(OnHomeItemClickListener onItemClickListener) {
        this.mClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(mResourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        HomeItem item = mList.get(position);
        Glide.with(mContext).load(item.getImageID()).into(holder.ivImage);
        holder.tvContent.setText(item.getContent());

        if (mClickListener != null) {
            mClickListener.clickHomeItem(position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvContent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_img_home_item);
            tvContent = itemView.findViewById(R.id.tv_content_home_item);
        }
    }
}
