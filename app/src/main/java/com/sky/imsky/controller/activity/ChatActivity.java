package com.sky.imsky.controller.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.sky.imsky.R;
import com.sky.imsky.controller.weight.ChatFragment;
import com.sky.imsky.runtimepermissions.PermissionsManager;
import com.sky.imsky.utils.Constant;

/**
 * 会话页面
 */
public class ChatActivity extends FragmentActivity {

    private String mHxid;
    private EaseChatFragment easeChatFragment;
    private LocalBroadcastManager mLBM;
    private int mChatType;
    private BroadcastReceiver exitGroupReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }

        initData();
        initListener();
    }

    private void initListener() {
        easeChatFragment.setChatFragmentHelper(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            @Override //进入到会话详情
            public void onEnterToChatDetails() {
                Intent intent = new Intent(ChatActivity.this, GroupDetailActivity.class);
                intent.putExtra(Constant.GROUP_ID, mHxid);
                startActivity(intent);
            }

            @Override //头像的点击事件
            public void onAvatarClick(String username) {

            }

            @Override //头像的长点击
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });
        //如果当前类型为群聊
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            //注册退群广播
            //判断传过来的群id与退出或者解散的群id是否一样
            //结束当前页面
            exitGroupReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //判断传过来的群id与退出或者解散的群id是否一样
                    if (mHxid.equals(intent.getStringExtra(Constant.GROUP_ID))) {
                        //结束当前页面
                        finish();
                    }
                }
            };
            mLBM.registerReceiver(exitGroupReceiver, new IntentFilter(Constant.EXIT_GROUP));
        }
    }

    private void initData() {
        //创建一个会话的fragment
        easeChatFragment = new ChatFragment();
        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        //获取聊天类型参数
        mChatType = getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE);
        //这里一点要设置argument
        easeChatFragment.setArguments(getIntent().getExtras());
        //替换fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_chat, easeChatFragment).commit();

        //获取发送广播的管理者
        mLBM = LocalBroadcastManager.getInstance(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(exitGroupReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
