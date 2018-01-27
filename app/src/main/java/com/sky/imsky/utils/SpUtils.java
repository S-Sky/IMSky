package com.sky.imsky.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sky.imsky.IMApplication;

/**
 * 保存、获取
 */

public class SpUtils {

    public static final String IS_NEW_INVITE = "is_new_invite"; //新的邀请标记
    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;

    private SpUtils() {
    }

    //单例
    public static SpUtils getInstance() {
        //使用全局context,否则会出现内存泄漏的风险
        if (mSp == null) {
            mSp = IMApplication.getGlobalApplication().getSharedPreferences("IMSky", Context.MODE_PRIVATE);
        }
        return instance;
    }

    //保存
    public void save(String key, Object value) {
        if (value instanceof String) {
            //必须edit开启事物,put之后必须commit
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    //获取数据
    public String getString(String key, String defaultValue) {
        return mSp.getString(key, defaultValue);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return mSp.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defauleValue) {
        return mSp.getInt(key, defauleValue);
    }
}
