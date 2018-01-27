package com.sky.imsky.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sky.imsky.R;
import com.sky.imsky.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class GroupDetailAdapter extends BaseAdapter {

    private Context mContext;
    private boolean mIsCanModify; //是否允许添加和删除群成员
    private List<UserInfo> mUsers = new ArrayList<>();
    private boolean mIsDeleteModel = false; //删除模式
    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context context, boolean isCanModify, OnGroupDetailListener onGroupDetailListener) {
        this.mContext = context;
        this.mIsCanModify = isCanModify;
        this.mOnGroupDetailListener = onGroupDetailListener;
    }

    //获取当前的删除模式
    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }

    //设置当前的删除模式
    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }

    //刷新
    public void refresh(List<UserInfo> userInfos) {
        if (userInfos != null && userInfos.size() >= 0) {
            mUsers.clear();

            //添加加号和减号
            initUsers();
            //把所有数据添加到0号位置
            mUsers.addAll(0, userInfos);
        }
        notifyDataSetChanged();
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");
        //保持加号始终在减号前面
        mUsers.add(delete);
        mUsers.add(0, add);
    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_group_detail, null);
            holder.photo = convertView.findViewById(R.id.iv_group_detail_photo);
            holder.delete = convertView.findViewById(R.id.iv_group_detail_delete);
            holder.tvName = convertView.findViewById(R.id.tv_group_detail_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserInfo userInfo = mUsers.get(position);

        //显示数据
        if (mIsCanModify) { //群主或者开放了群权限
            //布局的处理
            if (position == getCount() - 1) { //减号的处理
                //删除模式判断
                if (mIsDeleteModel) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    holder.tvName.setVisibility(View.INVISIBLE);
                    holder.delete.setVisibility(View.GONE);
                }

            } else if (position == getCount() - 2) { //加号的处理
                //删除模式判断
                if (mIsDeleteModel) {
                    convertView.setVisibility(View.GONE);
                } else {
                    convertView.setVisibility(View.VISIBLE);
                    holder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    holder.tvName.setVisibility(View.INVISIBLE);
                    holder.delete.setVisibility(View.GONE);
                }

            } else { //普通群成员
                convertView.setVisibility(View.VISIBLE);
                holder.tvName.setVisibility(View.VISIBLE);
                holder.tvName.setText(userInfo.getName());
                holder.photo.setImageResource(R.drawable.em_default_avatar);
                if (mIsDeleteModel) {
                    holder.delete.setVisibility(View.VISIBLE);
                } else {
                    holder.delete.setVisibility(View.GONE);
                }
            }
            //点击事件的处理
            if (position == getCount() - 1) {
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteModel) {
                            mIsDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });
            } else if (position == getCount() - 2) {
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
            } else {
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onDeleteMembers(userInfo);
                    }
                });
            }
        } else { //普通群成员
            if (position == getCount() - 1 || position == getCount() - 2) { //减号和加号的位置
                convertView.setVisibility(View.GONE);
            } else {
                convertView.setVisibility(View.VISIBLE);
                //名称
                holder.tvName.setText(userInfo.getName());
                //头像
                holder.photo.setImageResource(R.drawable.em_default_avatar);
                //删除
                holder.delete.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView photo;
        private ImageView delete;
        private TextView tvName;
    }

    public interface OnGroupDetailListener {
        //添加群成员
        void onAddMembers();

        //删除群成员
        void onDeleteMembers(UserInfo userInfo);
    }
}
