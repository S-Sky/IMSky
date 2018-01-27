package com.sky.imsky.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.sky.imsky.R;
import com.sky.imsky.controller.activity.AddContactActivity;
import com.sky.imsky.controller.activity.ChatActivity;
import com.sky.imsky.controller.activity.GroupListActivity;
import com.sky.imsky.controller.activity.InviteListActivity;
import com.sky.imsky.model.Model;
import com.sky.imsky.model.bean.UserInfo;
import com.sky.imsky.utils.Constant;
import com.sky.imsky.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/18 0018.
 * 联系人列表,这里继承的是easeui
 */

public class ContactFragment extends EaseContactListFragment {

    LinearLayout ll_contact_invite;
    LinearLayout ll_contact_group;
    ImageView iv_contact_red;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver contactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到广播之后,更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };
    private BroadcastReceiver contactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新页面
            refreshContact();
        }
    };
    private String delHxid;
    private BroadcastReceiver groupChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到广播之后,更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };

    @Override
    protected void initView() {
        super.initView();
        //布局显示加号
        titleBar.setRightImageResource(R.drawable.em_add);
        //添加布局
        View view = View.inflate(getActivity(), R.layout.header_fragment_contact, null);
        ll_contact_invite = view.findViewById(R.id.ll_contact_invite);
        ll_contact_group = view.findViewById(R.id.ll_contact_group);
        iv_contact_red = view.findViewById(R.id.iv_contact_red);
        listView.addHeaderView(view);

        //设置listView设置点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {

                if (user == null) {
                    return;
                }

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //需要传递参数  userName就是环信id
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
                startActivity(intent);
            }
        });
    }

    /**
     * 这个方法中处理业务逻辑
     */
    @Override
    protected void setUpView() {
        super.setUpView();
        //添加按钮的点击事件
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddContactActivity.class));
            }
        });
        //初始化小红点
        Boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        //注册广播
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        mLBM.registerReceiver(contactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(contactChangeReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        mLBM.registerReceiver(groupChangeReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));

        //好友邀请
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏红点
                iv_contact_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
                //跳转到邀请信息页面
                startActivity(new Intent(getActivity(), InviteListActivity.class));
            }
        });
        //群组
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(intent);
            }
        });

        //从环信服务器获取所有的联系人信息
        getContactFromHxServer();
        //删除好友
        //绑定listView和contextmenu
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //拿到需要删除的联系人在联系人列表中的下标
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        //获取到当前item的数据
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        delHxid = easeUser.getUsername();
        //添加删除布局
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contact_delete) {
            //执行删除选中的联系人操作
            deleteContact();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    //删除联系人
    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器中删除联系人
                    EMClient.getInstance().contactManager().deleteContact(delHxid);
                    //本地数据库的更新
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(delHxid);
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新页面
                            Toast.makeText(getActivity(), "删除" + delHxid + "成功", Toast.LENGTH_SHORT).show();
                            refreshContact();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除" + delHxid + "失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void getContactFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取到所有好友的环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //校验
                    if (hxids != null && hxids.size() >= 0) {
                        //转换
                        List<UserInfo> contacts = new ArrayList<>();
                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }
                        //保存好友信息到本地数据库
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts, true);
                        //刷新页面
                        //在子线程中调用getActivity方法存在报空的风险
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContact();
                            }
                        });
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshContact() {
        //获取数据
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        //校验
        if (contacts != null && contacts.size() >= 0) {
            //设置数据
            Map<String, EaseUser> contactsMap = new HashMap<>();
            //数据转换
            for (UserInfo userInfo : contacts) {
                EaseUser easeUser = new EaseUser(userInfo.getHxid());
                contactsMap.put(userInfo.getHxid(), easeUser);
            }
            setContactsMap(contactsMap);
            //刷新页面
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //一定要关闭广播
        mLBM.unregisterReceiver(contactInviteChangeReceiver);
        mLBM.unregisterReceiver(contactChangeReceiver);
        mLBM.unregisterReceiver(groupChangeReceiver);
    }
}
