package com.sky.imsky.controller.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.sky.imsky.R;
import com.sky.imsky.controller.activity.LoginActivity;
import com.sky.imsky.model.Model;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class SettingFragment extends BaseFragment {

    private Unbinder unbinder;
    @BindView(R.id.tv_show)
    TextView tvShow;

    @Override
    protected int getResource() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    protected void initData() {
        tvShow.setText(EMClient.getInstance().getCurrentUser());
    }

    @OnClick(R.id.btn_exit)
    public void onClick(View view) {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //登录环信服务器退出登录
                EMClient.getInstance().logout(true, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        //退出成功之后,需要关闭DBHelper
                        Model.getInstance().getDbManager().close();
                        //需要在主线程中更新UI
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(mContext, LoginActivity.class));
                                Toast.makeText(mContext, "退出登录", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, final String s) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "退出失败" + s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    @Override
    protected void startDestroy() {
        unbinder.unbind();
    }
}
