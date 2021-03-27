package com.chen.fy.patshow.data.adapter.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chen.fy.patshow.R;

import java.util.List;

public class TextLogoAdapter extends RecyclerView.Adapter<TextLogoAdapter.ViewHolder>{

    private List<Integer> mTextLogoDrawables;
    private IOnClickTextLogoListener mListener;

    public TextLogoAdapter(List<Integer> textLogoDrawables, IOnClickTextLogoListener listener){
        this.mTextLogoDrawables = textLogoDrawables;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.text_logo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int id = mTextLogoDrawables.get(position);
        holder.ivTextLogo.setImageResource(id);
        holder.ivTextLogo.setOnClickListener(v -> {
            mListener.clickTextLogo(position);
        });
    }

    @Override
    public int getItemCount() {
        return mTextLogoDrawables.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTextLogo;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTextLogo = itemView.findViewById(R.id.iv_text_logo);
        }
    }

    public interface IOnClickTextLogoListener{
        void clickTextLogo(int position);
    }
}
