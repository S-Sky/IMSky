package com.sky.imsky.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.chat.EMClient;
import com.sky.imsky.R;
import com.sky.imsky.model.Model;
import com.sky.imsky.model.bean.UserInfo;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //延迟两秒后执行
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //如果当前activity已经退出,就不处理handle中的消息
            if (isFinishing()) {
                return;
            }
            // 判断进入主页面还是登陆页面
            toMainOrLogin();
        }
    };

    /**
     * 这里请求环信服务器来判断当前账户是否登录过
     */
    private void toMainOrLogin() {
        //使用线程池可以减少匿名线程优化的可能
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断当前账户是否已经登录过
                if (EMClient.getInstance().isLoggedInBefore()) {//登录过
                    //获取当前登录用户的信息
                    UserInfo userInfo = Model.getInstance().getUserAccountDao().getAccountByHxid(EMClient.getInstance().getCurrentUser());
                    //如果用户信息为空,需要重新登录
                    if (userInfo == null) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    } else { //登录成功
                        Model.getInstance().loginSuccess(userInfo);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁handler中的消息
        handler.removeCallbacksAndMessages(null);
    }
}
