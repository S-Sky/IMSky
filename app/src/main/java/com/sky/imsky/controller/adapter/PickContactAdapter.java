package com.sky.imsky.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sky.imsky.R;
import com.sky.imsky.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class PickContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();
    private List<String> mMembers = new ArrayList<>();//保存群中已经存在的成员

    public PickContactAdapter(Context context, List<PickContactInfo> picks, List<String> members) {
        this.mContext = context;

        if (picks != null && picks.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(picks);
        }

        //加载已经存在的成员集合
        mMembers.clear();
        mMembers.addAll(members);

    }

    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
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

            convertView = View.inflate(mContext, R.layout.item_pick, null);
            holder.cbPick = convertView.findViewById(R.id.cb_pick);
            holder.tvPickName = convertView.findViewById(R.id.tv_pick_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PickContactInfo pickContactInfo = mPicks.get(position);
        holder.tvPickName.setText(pickContactInfo.getUserInfo().getName());
        holder.cbPick.setChecked(pickContactInfo.isChecked());

        //判断
        if (mMembers.contains(pickContactInfo.getUserInfo().getHxid())) {
            holder.cbPick.setChecked(true);
            pickContactInfo.setChecked(true);
        }

        return convertView;
    }

    /**
     * 获取选择的联系人
     *
     * @return
     */
    public List<String> getPickContacts() {
        List<String> picks = new ArrayList<>();

        for (PickContactInfo pick : mPicks) {
            if (pick.isChecked()) {
                picks.add(pick.getUserInfo().getName());
            }
        }
        return picks;
    }

    private class ViewHolder {
        private CheckBox cbPick;
        private TextView tvPickName;
    }
}
