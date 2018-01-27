package com.sky.imsky.model.db;

import android.content.Context;

import com.sky.imsky.model.dao.ContactTableDao;
import com.sky.imsky.model.dao.InviteTableDao;

/**
 * 邀请信息和联系人表的操作类的管理类
 */

public class DBManager {

    private final DBHelper dbHelper;
    private final ContactTableDao contactTableDao;
    private final InviteTableDao inviteTableDao;

    public DBManager(Context context, String name) {
        //创建数据库
        dbHelper = new DBHelper(context, name);
        //创建该数据库中两种表的操作类
        contactTableDao = new ContactTableDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);
    }

    /**
     * 获取联系人表的操作对象
     *
     * @return
     */
    public ContactTableDao getContactTableDao() {
        return contactTableDao;
    }

    /**
     * 获取邀请信息表的操作对象
     *
     * @return
     */
    public InviteTableDao getInviteTableDao() {
        return inviteTableDao;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        dbHelper.close();
    }
}
