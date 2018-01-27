package com.sky.imsky.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.sky.imsky.R;
import com.sky.imsky.controller.adapter.PickContactAdapter;
import com.sky.imsky.model.Model;
import com.sky.imsky.model.bean.PickContactInfo;
import com.sky.imsky.model.bean.UserInfo;
import com.sky.imsky.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 选择联系人页面
 */
public class PickContactActivity extends Activity {

    private Unbinder unbinder;
    @BindView(R.id.tv_pick_save)
    TextView tvPickSave;
    @BindView(R.id.lv_pick)
    ListView lvPick;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter adapter;
    private List<String> members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        unbinder = ButterKnife.bind(this);
        //获取传递过来的数据
        getData();

        initData();

        initListener();
    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId != null) {
            //获取群信息
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            //获取群中已经存在的所有群成员
            members = group.getMembers();
        }
        if (members == null) {
            members = new ArrayList<>();
        }
    }

    private void initListener() {
        //listView条目的点击事件
        lvPick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //CheckBox的切换
                CheckBox checkBox = view.findViewById(R.id.cb_pick);
                checkBox.setChecked(!checkBox.isChecked());
                //修改数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setChecked(checkBox.isChecked());

                //刷新页面
                adapter.notifyDataSetChanged();
            }
        });
        //保存
        tvPickSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取已经选择的联系人
                List<String> names = adapter.getPickContacts();
                //给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members", names.toArray(new String[0]));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initData() {
        //从本地数据库中获取所有的联系人信息
        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        mPicks = new ArrayList<>();
        if (contacts != null && contacts.size() >= 0) {
            //进行转换
            for (UserInfo userInfo : contacts) {
                PickContactInfo pickContactInfo = new PickContactInfo(userInfo, false);
                mPicks.add(pickContactInfo);
            }
        }
        adapter = new PickContactAdapter(this, mPicks, members);
        lvPick.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
