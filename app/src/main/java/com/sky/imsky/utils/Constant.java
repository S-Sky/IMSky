package com.sky.imsky.utils;

import com.hyphenate.easeui.EaseConstant;

/**
 * 全局的常量类
 */

public class Constant extends EaseConstant {

    public static final String CONTACT_CHANGED = "contact_changed"; //发送联系人变化的广播
    public static final String CONTACT_INVITE_CHANGED = "contact_invite_changed"; // 联系人邀请信息变化的广播

    public static final String GROUP_INVITE_CHANGED = "group_invite_changed"; //群邀请信息变化的广播

    public static final String GROUP_ID = "group_id"; //群id
    public static final String EXIT_GROUP = "exit_group"; //退群广播

    public static final int CHATTYPE_SINGLE = 1;
}
