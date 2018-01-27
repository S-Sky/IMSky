package com.sky.imsky.model;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.model.EaseNotifier;
import com.sky.imsky.controller.activity.ChatActivity;
import com.sky.imsky.easeclass.VideoCallActivity;
import com.sky.imsky.easeclass.VoiceCallActivity;
import com.sky.imsky.model.bean.GroupInfo;
import com.sky.imsky.model.bean.InvitationInfo;
import com.sky.imsky.model.bean.UserInfo;
import com.sky.imsky.utils.Constant;
import com.sky.imsky.utils.SpUtils;

import java.util.List;

/**
 * 全局事件监听类
 */

public class EventListener {

    private final Context mContext;
    private final LocalBroadcastManager mLBM;
    //private EaseUI easeUI;

    public static boolean isVoiceCalling;
    public static boolean isVideoCalling;


    public EventListener(Context context) {
        mContext = context;
        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);
        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        //注册一个群信息变换的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);

//        easeUI = EaseUI.getInstance();
//        setEaseUIProviders();
    }

//    private void setEaseUIProviders() {
//        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {
//
//            @Override
//            public String getTitle(EMMessage message) {
//                //you can update title here
//                return null;
//            }
//
//            @Override
//            public int getSmallIcon(EMMessage message) {
//                //you can update icon here
//                return 0;
//            }
//
//            @Override
//            public String getDisplayedText(EMMessage message) {
//                // be used on notification bar, different text according the message type.
////                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
////                if(message.getType() == EMMessage.Type.TXT){
////                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
////                }
////                EaseUser user = getUserInfo(message.getFrom());
////                if(user != null){
////                    if(EaseAtMessageHelper.get().isAtMeMsg(message)){
////                        return String.format(appContext.getString(R.string.at_your_in_group), user.getNick());
////                    }
////                    return user.getNick() + ": " + ticker;
////                }else{
////                    if(EaseAtMessageHelper.get().isAtMeMsg(message)){
////                        return String.format(appContext.getString(R.string.at_your_in_group), message.getFrom());
////                    }
////                    return message.getFrom() + ": " + ticker;
////                }
//                return null;
//            }
//
//            @Override
//            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
//                // here you can customize the text.
//                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
//                return null;
//            }
//
//            @Override
//            public Intent getLaunchIntent(EMMessage message) {
//                // you can set what activity you want display when user click the notification
//                Intent intent = new Intent(mContext, ChatActivity.class);
//                // open calling activity if there is call
//                if (isVideoCalling) {
//                    intent = new Intent(mContext, VideoCallActivity.class);
//                } else if (isVoiceCalling) {
//                    intent = new Intent(mContext, VoiceCallActivity.class);
//                } else {
//                    EMMessage.ChatType chatType = message.getChatType();
//                    if (chatType == EMMessage.ChatType.Chat) { // single chat message
//                        intent.putExtra("userId", message.getFrom());
//                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
//                    } else { // group chat message
//                        // message.getTo() is the group id
//                        intent.putExtra("userId", message.getTo());
//                        if (chatType == EMMessage.ChatType.GroupChat) {
//                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
//                        } else {
//                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
//                        }
//
//                    }
//                }
//                return intent;
//            }
//        });
//    }

    //群信息变化的监听
    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {
        @Override  //接收到群组加入邀请
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            Log.e("EventListener", "接收到群组加入邀请");
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

        }

        @Override  //用户申请加入群
        public void onRequestToJoinReceived(String groupId, String groupName, String applyer, String reason) {
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, applyer));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override //加群申请被同意
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, accepter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override  //加群申请被拒绝
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {

            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroupInfo(new GroupInfo(groupName, groupId, decliner));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override  //群组邀请被同意
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

        }

        @Override   //群组邀请被拒绝
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, invitee));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override //群成员删除
        public void onUserRemoved(String s, String s1) {

        }

        @Override //群被解散
        public void onGroupDestroyed(String s, String s1) {

        }

        @Override //接收邀请时自动加入到群组的通知
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            Log.e("EventListener", "接收邀请时自动加入到群组的通知");
            //数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(inviteMessage);
            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            //成员禁言的通知
        }

        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            //成员从禁言列表里移除通知
        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {
            //增加管理员的通知
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            //管理员移除的通知
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            //群所有者变动通知
        }

        @Override
        public void onMemberJoined(final String groupId, final String member) {
            //群组加入新成员通知
        }

        @Override
        public void onMemberExited(final String groupId, final String member) {
            //群成员退出通知
        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {
            //群公告变动通知
        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
            //增加共享文件的通知
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            //群共享文件删除通知
        }
    };

    //注册联系人变化的监听
    private final EMContactListener emContactListener = new EMContactListener() {
        //联系人增加后执行的方法
        @Override
        public void onContactAdded(String hxid) {
            //数据更新(需要在自己的服务器拿到添加的联系人的详细信息,这里是直接new UserInfo(hxid))
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid), true);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //联系人删除后执行的方法
        @Override
        public void onContactDeleted(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        //接收到添加好友邀请时执行
        @Override
        public void onContactInvited(String hxid, String reason) {
            //更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUserInfo(new UserInfo(hxid));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE); //新邀请

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            //提示红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //别人同意自己的好友邀请
        @Override
        public void onFriendRequestAccepted(String hxid) {
            //数据变化
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUserInfo(new UserInfo(hxid));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo); //别人同意了当前登录人的邀请

            //提示红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        //别人拒绝自己的好友邀请
        @Override
        public void onFriendRequestDeclined(String s) {
            //提示红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };
}
