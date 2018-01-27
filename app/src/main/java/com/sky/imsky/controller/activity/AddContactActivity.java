package com.sky.imsky.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.sky.imsky.R;
import com.sky.imsky.model.Model;
import com.sky.imsky.model.bean.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 添加联系人页面
 */
public class AddContactActivity extends Activity {

    private Unbinder unbinder;
    @BindView(R.id.tv_add_find)
    TextView tvFind;
    @BindView(R.id.edit_add_name)
    EditText editName;
    @BindView(R.id.rl_add)
    RelativeLayout rlAdd;
    @BindView(R.id.tv_add_name)
    TextView tvName;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        unbinder = ButterKnife.bind(this);

        setListener();
    }

    private void setListener() {
        tvFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户名
                final String addName = editName.getText().toString();
                if (TextUtils.isEmpty(addName)) {
                    Toast.makeText(AddContactActivity.this, "输入的用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //去服务器验证当前用户是否存在
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //判断当前查找的用户是否存在(正常是要在自己的服务器验证用户是否存在)
                        userInfo = new UserInfo(addName);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rlAdd.setVisibility(View.VISIBLE);
                                tvName.setText(addName);
                            }
                        });
                    }
                });
            }
        });
    }

    @OnClick(R.id.btn_add_add)
    public void onClick(View view) {
        //添加好友需要在环信服务器添加
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器添加好友
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(), "添加好友");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送添加好友邀请成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "发送添加好友邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
