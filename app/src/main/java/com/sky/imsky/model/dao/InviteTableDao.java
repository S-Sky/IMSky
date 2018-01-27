package com.sky.imsky.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sky.imsky.model.bean.GroupInfo;
import com.sky.imsky.model.bean.InvitationInfo;
import com.sky.imsky.model.bean.UserInfo;
import com.sky.imsky.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请信息数据库的操作类
 */

public class InviteTableDao {
    private DBHelper mHelper;

    public InviteTableDao(DBHelper dbHelper) {
        this.mHelper = dbHelper;
    }

    // 添加邀请
    public void addInvitation(InvitationInfo invitationInfo) {
        //获取数据库的链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行添加语句语句
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_REASON, invitationInfo.getReason());
        values.put(InviteTable.COL_STATUS, invitationInfo.getStatus().ordinal());//ordinal返回枚举的序号

        UserInfo userInfo = invitationInfo.getUserInfo();
        if (userInfo != null) { //联系人
            values.put(InviteTable.COL_USER_HXID, invitationInfo.getUserInfo().getHxid());
            values.put(InviteTable.COL_USER_NAME, invitationInfo.getUserInfo().getName());
        } else { //群组
            values.put(InviteTable.COL_GROUP_HXID, invitationInfo.getGroupInfo().getGroupId());
            values.put(InviteTable.COL_GROUP_NAME, invitationInfo.getGroupInfo().getGroupName());
            //当为群组邀请时,COL_USER_HXID主键为空,所以这里存为群组邀请人
            values.put(InviteTable.COL_USER_HXID, invitationInfo.getGroupInfo().getInvitePerson());
        }

        db.replace(InviteTable.TAB_NAME, null, values);
    }

    // 获取所有邀请信息
    public List<InvitationInfo> getInvitations() {
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行查询语句
        String sql = "select * from " + InviteTable.TAB_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        List<InvitationInfo> invitationInfos = new ArrayList<>();

        while (cursor.moveToNext()) {
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));//原因
            invitationInfo.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS))));//状态

            String groupId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID));
            if (groupId == null) { //联系人的邀请信息

                UserInfo userInfo = new UserInfo();
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                invitationInfo.setUserInfo(userInfo);
            } else { //群组的邀请信息
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setInvitePerson(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                invitationInfo.setGroupInfo(groupInfo);
            }
            //将本次循环的邀请信息添加的集合中
            invitationInfos.add(invitationInfo);
        }
        //关闭资源
        cursor.close();
        return invitationInfos;
    }

    // 将int类型状态转换为邀请的状态
    private InvitationInfo.InvitationStatus int2InviteStatus(int intStatus) {

        if (intStatus == InvitationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }

    // 删除邀请
    public void removeInvitation(String hxId) {
        if (hxId == null) {
            return;
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.delete(InviteTable.TAB_NAME, InviteTable.COL_USER_HXID + "=?", new String[]{hxId});
    }

    // 更新邀请状态
    public void updateInvitationStatus(InvitationInfo.InvitationStatus invitationStatus, String hxId) {
        if (hxId == null) {
            return;
        }
        //获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //执行
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_STATUS, invitationStatus.ordinal());
        db.update(InviteTable.TAB_NAME, values, InviteTable.COL_USER_HXID + "=?", new String[]{hxId});
    }
}
