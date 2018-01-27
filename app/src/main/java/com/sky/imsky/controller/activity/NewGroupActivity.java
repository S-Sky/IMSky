package com.sky.imsky.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.sky.imsky.R;
import com.sky.imsky.model.Model;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewGroupActivity extends Activity {

    private Unbinder unbinder;
    @BindView(R.id.et_new_group_name)
    EditText et_new_group_name;
    @BindView(R.id.et_new_group_desc)
    EditText et_new_group_desc;
    @BindView(R.id.cb_new_group_public)
    CheckBox cb_new_group_public;
    @BindView(R.id.cb_new_group_invite)
    CheckBox cb_new_group_invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        unbinder = ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_new_group_create)
    public void onClick(View view) {
        Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //成功获取到联系人
        if (resultCode == RESULT_OK) {
            //创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    /**
     * 创建群
     *
     * @param members
     */
    private void createGroup(final String[] members) {
        final String groupName = et_new_group_name.getText().toString();
        final String groupDesc = et_new_group_desc.getText().toString();

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器创建群
                /**
                 * String var1: 群名称
                 * String var2: 群描述
                 * String[] var3: 群成员
                 * String var4: 创建群的原因
                 * EMGroupOptions var5: 参数设置 
                 */
                EMGroupOptions options = new EMGroupOptions();

                options.maxUsers = 200; //群最多容纳多少人
                EMGroupManager.EMGroupStyle groupStyle = null;
                if (cb_new_group_public.isChecked()) { //是否公开群
                    if (cb_new_group_invite.isChecked()) { //开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                } else {
                    if (cb_new_group_invite.isChecked()) { //开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    } else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }
                options.style = groupStyle; //群的样式(是否公开,群成员是否可以邀请他人)

                try {
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, members, "申请加入群", options);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show();
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
