package com.sky.imsky.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.sky.imsky.R;
import com.sky.imsky.controller.adapter.GroupListAdapter;
import com.sky.imsky.model.Model;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GroupListActivity extends Activity {

    private Unbinder unbinder;
    @BindView(R.id.lv_group_list)
    ListView groupList;
    private GroupListAdapter adapter;
    private LinearLayout ll_group_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        unbinder = ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        //listView条目的点击事件
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("TAG", "position=" + position);

                if (position == 0) {
                    return;
                }
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                //传递会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                //传递群id
                //这里position要减一,因为加了头布局之后listView第0个item是加的头布局的那一条
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.getGroupId());
                startActivity(intent);
            }
        });
        //新建群组
        ll_group_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupListActivity.this, NewGroupActivity.class));
            }
        });
    }

    private void initData() {
        adapter = new GroupListAdapter(this);
        groupList.setAdapter(adapter);

        //从环信服务器获取所有群的信息
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信获取所有的群信息
                    final List<EMGroup> emGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();
                            // adapter.refresh(emGroups);
                            //从环信服务器中拿到数据后,会把数据保存到本地的SDK中
                            refresh();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //刷新的方法
    private void refresh() {
        adapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initView() {
        View view = View.inflate(this, R.layout.header_grouplist, null);
        ll_group_list = view.findViewById(R.id.ll_group_list);
        groupList.addHeaderView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新页面
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
