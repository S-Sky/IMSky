package com.sky.imsky;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;
import com.sky.imsky.model.Model;

/**
 * Created by Administrator on 2018/1/17 0017.
 */

public class IMApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化环信EaseUI
        EMOptions options = new EMOptions();
        //默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //设置需要同意后才能接受群邀请,默认自动接受
        options.setAutoAcceptGroupInvitation(false);
        EaseUI.getInstance().init(this, options);


        //初始化数据模式层类
        Model.getInstance().init(this);
        //初始化全局上下文
        mContext = this;

    }

    /**
     * 全局上下文对象
     *
     * @return
     */
    public static Context getGlobalApplication() {
        return mContext;
    }
}
