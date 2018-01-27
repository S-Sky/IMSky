package com.sky.imsky.model;

import android.content.Context;
import android.content.IntentFilter;

import com.hyphenate.chat.EMClient;
import com.sky.imsky.easeclass.CallReceiver;
import com.sky.imsky.model.bean.UserInfo;
import com.sky.imsky.model.dao.UserAccountDao;
import com.sky.imsky.model.db.DBManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/1/17 0017.
 * 数据模型层全局类
 */

public class Model {

    private Context mContext;
    /**
     * 获取线程池
     */
    private ExecutorService executorService = Executors.newCachedThreadPool();
    /**
     * 创建对象
     */
    private static Model model = new Model();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    /**
     * 私有化构造方法
     */
    private Model() {
    }

    /**
     * 单例模式,创建唯一实例
     */
    public static Model getInstance() {
        return model;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.mContext = context;
        //创建用户账号数据库的操作对象
        userAccountDao = new UserAccountDao(context);
        //开启全局监听
        new EventListener(mContext);

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        context.registerReceiver(new CallReceiver(), callFilter);

    }

    /**
     * 获取全局线程池对象
     *
     * @return
     */
    public ExecutorService getGlobalThreadPool() {
        return executorService;
    }

    /**
     * 用户登录成功后的处理方法
     * 登录成功之后创建联系人和邀请信息表
     */
    public void loginSuccess(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        if (dbManager != null) {
            dbManager.close();
        }

        //以当前登录人的name作为联系人和邀请信息表的库名
        dbManager = new DBManager(mContext, userInfo.getName());
    }

    /**
     * 获取邀请信息和联系人表的操作类的管理类的操作对象
     *
     * @return
     */
    public DBManager getDbManager() {
        return dbManager;
    }

    /**
     * 获取用户账号数据库的操作类
     *
     * @return
     */
    public UserAccountDao getUserAccountDao() {
        return userAccountDao;
    }
}
