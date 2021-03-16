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
import com.chen.fy.patshow.model.ShareResponse;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context mContext;
    private int mResourceId;

    private List<ShareResponse> mList;
    private IOnHomeItemClickListener mClickListener;

    public HomeAdapter(Context context, int resourceId) {
        this.mContext = context;
        this.mResourceId = resourceId;
    }

    public void setShareResponseList(List<ShareResponse> list) {
        this.mList = list;
    }

    public void setOnItemClickListener(IOnHomeItemClickListener onItemClickListener) {
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
        ShareResponse shareResponse = mList.get(position);
        Glide.with(mContext).load(shareResponse.getImgURL()).into(holder.ivImage);
        holder.tvContent.setText(shareResponse.getContent());

        holder.ivImage.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.clickHomeItem(holder.ivImage, shareResponse.getImgURL());
            }
        });
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

    public interface IOnHomeItemClickListener {
        void clickHomeItem(ImageView imageView, Object url);
    }
}
