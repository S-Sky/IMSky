package com.sky.imsky.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.sky.imsky.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/23 0023.
 */

public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EMGroup> emGroups = new ArrayList<>();

    public GroupListAdapter(Context context) {
        this.mContext = context;
    }

    public void refresh(List<EMGroup> groups) {
        if (emGroups != null && groups.size() >= 0) {
            emGroups.clear();
            emGroups.addAll(groups);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return emGroups == null ? 0 : emGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return emGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_group, null);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EMGroup emGroup = emGroups.get(position);
        holder.tvName.setText(emGroup.getGroupName());

        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
    }
}
