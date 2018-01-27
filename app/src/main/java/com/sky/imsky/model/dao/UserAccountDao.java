package com.sky.imsky.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sky.imsky.model.bean.UserInfo;
import com.sky.imsky.model.db.UserAccountDB;

/**
 * Created by Administrator on 2018/1/18 0018.
 * 用户信息数据库的操作类
 */

public class UserAccountDao {

    private final UserAccountDB mHelper;

    public UserAccountDao(Context context) {
        mHelper = new UserAccountDB(context);
    }

    /**
     * 添加数据
     */
    public void addAccount(UserInfo userInfo) {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行添加操作,有就替换,无则添加
        ContentValues values = new ContentValues();
        values.put(UserAccountTable.COL_NAME, userInfo.getName());
        values.put(UserAccountTable.COL_HXID, userInfo.getHxid());
        values.put(UserAccountTable.COL_NICK, userInfo.getNick());
        values.put(UserAccountTable.COL_PHOTO, userInfo.getPhoto());
        db.replace(UserAccountTable.TAB_NAME, null, values);
    }

    /**
     * 根据环信id查询数据
     */
    public UserInfo getAccountByHxid(String hxid) {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行查询语句
        String sql = "select * from " + UserAccountTable.TAB_NAME
                + " where " + UserAccountTable.COL_HXID
                + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxid});
        UserInfo userInfo = null;
        if (cursor.moveToNext()) {
            userInfo = new UserInfo();
            //封装对象
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(UserAccountTable.COL_PHOTO)));
        }
        //关闭资源
        cursor.close();
        return userInfo;
    }
}
