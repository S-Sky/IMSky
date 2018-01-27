package com.sky.imsky.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.RadioGroup;

import com.sky.imsky.R;
import com.sky.imsky.controller.fragment.ContactFragment;
import com.sky.imsky.controller.fragment.MessageFragment;
import com.sky.imsky.controller.fragment.SettingFragment;
import com.sky.imsky.utils.OpenPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends FragmentActivity {

    private Unbinder unbinder;

    @BindView(R.id.rg_main)
    RadioGroup rgMain;

    private MessageFragment messageFragment;
    private ContactFragment contactFragment;
    private SettingFragment settingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        OpenPermissions.getInstance().init(this);

        initFragment();
        initListener();
    }

    private void initListener() {
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                switch (checkedId) {
                    case R.id.rb_message:
                        fragment = messageFragment;
                        break;
                    case R.id.rb_contact:
                        fragment = contactFragment;
                        break;
                    case R.id.rb_setting:
                        fragment = settingFragment;
                        break;
                }
                //切换fragment
                switchFragment(fragment);
            }
        });
        rgMain.check(R.id.rb_message);
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    private void initFragment() {
        messageFragment = new MessageFragment();
        contactFragment = new ContactFragment();
        settingFragment = new SettingFragment();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
