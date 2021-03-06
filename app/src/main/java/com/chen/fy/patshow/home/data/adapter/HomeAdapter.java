package com.chen.fy.patshow.home.data.adapter;

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
import com.chen.fy.patshow.home.share.data.ShareInfo;
import com.chen.fy.patshow.network.MainServiceCreator;
import com.chen.fy.patshow.user.data.User;
import com.chen.fy.patshow.util.ShowUtils;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context mContext;
    private int mResourceId;

    private List<ShareInfo> mList;

    public HomeAdapter(Context context, int resourceId) {
        this.mContext = context;
        this.mResourceId = resourceId;
    }

    public void setData(List<ShareInfo> list) {
        this.mList = list;
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
        ShareInfo shareInfo = mList.get(position);
        String url = shareInfo.getImgURL();
        User user = shareInfo.getUser();
        // set
        Glide.with(mContext).load(url).into(holder.ivImage);
        holder.tvContent.setText(shareInfo.getContent());
        Glide.with(mContext)
                .load(MainServiceCreator.BASE_URL + user.getHeadUrl())
                .into(holder.civHeadIcon);
        holder.tvNickname.setText(user.getNickname());

        // image click
        holder.ivImage.setOnClickListener(v -> {
            ShowUtils.zoomPicture(mContext, v, url);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvContent;
        private CircleImageView civHeadIcon;
        private TextView tvNickname;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_img_home_item);
            tvContent = itemView.findViewById(R.id.tv_content_home_item);
            civHeadIcon = itemView.findViewById(R.id.crv_head_icon_item);
            tvNickname = itemView.findViewById(R.id.tv_nickname_item);
        }
    }

}
