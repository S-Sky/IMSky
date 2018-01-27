package com.sky.imsky.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sky.imsky.R;
import com.sky.imsky.model.bean.InvitationInfo;
import com.sky.imsky.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请信息列表适配器
 */

public class InviteAdapter extends BaseAdapter {

    private Context mContext;
    private List<InvitationInfo> mInvitationInfos = new ArrayList<>();
    private OnInviteListener mOnInviteListener;
    private InvitationInfo invitationInfo;


    public InviteAdapter(Context context, OnInviteListener onInviteListener) {
        this.mContext = context;
        this.mOnInviteListener = onInviteListener;
    }

    //刷新数据的方法
    public void refresh(List<InvitationInfo> invitationInfos) {
        if (invitationInfos != null && invitationInfos.size() >= 0) {
            mInvitationInfos.clear();
            mInvitationInfos.addAll(invitationInfos);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvitationInfos == null ? 0 : mInvitationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvitationInfos.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_invite, null);

            holder.tvName = convertView.findViewById(R.id.tv_invite_name);
            holder.tvReason = convertView.findViewById(R.id.tv_invite_reason);
            holder.btnAccept = convertView.findViewById(R.id.btn_invite_accept);
            holder.btnReject = convertView.findViewById(R.id.btn_invite_reject);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        invitationInfo = mInvitationInfos.get(position);
        UserInfo userInfo = invitationInfo.getUserInfo();
        if (userInfo != null) { //联系人
            holder.tvName.setText(invitationInfo.getUserInfo().getName());
            //只有接收到新的邀请时才显示接受和拒绝按钮
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            //原因
            if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.NEW_INVITE) { //新的邀请
                if (invitationInfo.getReason() == null) {
                    holder.tvReason.setText("添加好友");
                } else {
                    holder.tvReason.setText(invitationInfo.getReason());
                }
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);
            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT) { //接受邀请
                if (invitationInfo.getReason() == null) {
                    holder.tvReason.setText("接受邀请");
                } else {
                    holder.tvReason.setText(invitationInfo.getReason());
                }
            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) { //邀请被接受
                if (invitationInfo.getReason() == null) {
                    holder.tvReason.setText("邀请被接受");
                } else {
                    holder.tvReason.setText(invitationInfo.getReason());
                }
            }
            //接受
            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onAccept(invitationInfo);
                }
            });
            //拒绝
            holder.btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onReject(invitationInfo);
                }
            });

        } else { //群组
            //显示名称
            holder.tvName.setText(invitationInfo.getGroupInfo().getInvitePerson());
            //只有接收到新的邀请时才显示接受和拒绝按钮
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            //显示原因
            switch (invitationInfo.getStatus()) {
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.tvReason.setText("您的群申请请已经被接受");
                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    holder.tvReason.setText("您的群邀请已经被接收");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.tvReason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.tvReason.setText("您的群邀请已经被拒绝");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    holder.tvReason.setText("您收到了群邀请");
                    //只有接收到新的邀请时才显示接受和拒绝按钮
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.btnReject.setVisibility(View.VISIBLE);

                    holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invitationInfo);
                        }
                    });
                    holder.btnReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invitationInfo);
                        }
                    });

                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    holder.tvReason.setText("您收到了群申请");
                    //只有接收到新的邀请时才显示接受和拒绝按钮
                    holder.btnAccept.setVisibility(View.VISIBLE);
                    holder.btnReject.setVisibility(View.VISIBLE);
                    holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invitationInfo);
                        }
                    });
                    holder.btnReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invitationInfo);
                        }
                    });
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.tvReason.setText("你接受了群邀请");
                    break;

                // 您批准了群加入
                case GROUP_ACCEPT_APPLICATION:
                    holder.tvReason.setText("您批准了群加入");
                    break;
            }
        }


        return convertView;
    }

    private class ViewHolder {
        private TextView tvName;
        private TextView tvReason;
        private Button btnAccept;
        private Button btnReject;
    }

    public interface OnInviteListener {

        //联系人接受按钮的点击事件
        void onAccept(InvitationInfo invitationInfo);

        //联系人拒绝按钮的点击事件
        void onReject(InvitationInfo invitationInfo);

        //(群)接受邀请
        void onInviteAccept(InvitationInfo invitationInfo);

        //(群)拒绝邀请
        void onInviteReject(InvitationInfo invitationInfo);

        //接受申请处理
        void onApplicationAccept(InvitationInfo invitationInfo);

        //拒绝申请处理
        void onApplicationReject(InvitationInfo invitationInfo);
    }
}
