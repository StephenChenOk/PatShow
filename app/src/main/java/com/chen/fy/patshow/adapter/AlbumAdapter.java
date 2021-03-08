package com.chen.fy.patshow.adapter;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chen.fy.patshow.interfaces.IOnDeleteItemClickListener;
import com.chen.fy.patshow.interfaces.PicturesItemClickListener;
import com.chen.fy.patshow.R;
import com.chen.fy.patshow.model.ShowItem;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<ShowItem> data;

    private PicturesItemClickListener mClickListener;

    private IOnDeleteItemClickListener deleteItemClickListener;

    private Context mContext;

    public AlbumAdapter(Context context, List<ShowItem> data) {
        mContext = context;
        this.data = data;
    }

    public void setClickListener(PicturesItemClickListener listener) {
        this.mClickListener = listener;
    }

    public void setDeleteItemClickListener(IOnDeleteItemClickListener deleteItemClickListener) {
        this.deleteItemClickListener = deleteItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_photo_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(holder.itemView.getContext())
                .load((data.get(position)).getPhoto().getUrl())
                .into(holder.image);
        holder.image.setOnClickListener(v -> {

            if (mClickListener != null) {
                mClickListener.onPicturesItemClick(holder.image, data.get(position).getPhoto().getUrl());
            }
        });

        holder.image.setOnLongClickListener(v -> {
            deleteItemClickListener.deleteItem(data, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_shop);
        }
    }
}
