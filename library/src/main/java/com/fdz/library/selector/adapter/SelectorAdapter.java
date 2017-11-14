package com.fdz.library.selector.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fdz.library.R;
import com.fdz.library.selector.SelectorItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : fangdazhu
 * @version : 1.0.0
 * @since : 2017/11/13
 */

public class SelectorAdapter extends BaseAdapter {
    private List<SelectorItem> dataList = new ArrayList<>();
    private SelectorItem selectedItem = null;

    public SelectorAdapter(List<SelectorItem> dataList) {
        setDataList(dataList);
    }

    public void setDataList(List<SelectorItem> dataList) {
        this.dataList.clear();
        if (dataList != null && !dataList.isEmpty()) {
            this.dataList.addAll(dataList);
        }
    }

    public void setSelectedItem(SelectorItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public SelectorItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.jd_ui_selector_item, parent, false);
            holder = new ViewHolder();
            holder.tvArea = convertView.findViewById(R.id.tv_area);
            holder.ivCheckMark = convertView.findViewById(R.id.iv_check_mark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SelectorItem item = getItem(position);
        holder.tvArea.setText(item.getName());
        boolean checked = (selectedItem != null && selectedItem.getId() == item.getId());
        int color = ContextCompat.getColor(holder.tvArea.getContext(), checked ? R.color.google_red : R.color.black);
        holder.tvArea.setTextColor(color);
        holder.ivCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private class ViewHolder {
        TextView tvArea;
        ImageView ivCheckMark;
    }
}
