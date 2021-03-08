package com.chen.fy.patshow.interfaces;

import com.chen.fy.patshow.model.ShowItem;

import java.util.List;


/**
 * 删除图片
 */

public interface IOnDeleteItemClickListener {
    void deleteItem(List<ShowItem> data, int position);
}
