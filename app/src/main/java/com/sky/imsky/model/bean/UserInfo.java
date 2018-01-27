package com.sky.imsky.model.bean;

/**
 * Created by Administrator on 2018/1/18 0018.
 * 用户账号信息
 */

public class UserInfo {

    private String name; //用户名称
    private String hxid; //环信id,即在环信服务器中的唯一标识
    private String nick; //昵称
    private String photo; //头像

    public UserInfo() {
    }

    public UserInfo(String name) {
        this.name = name;
        this.hxid = name;
        this.nick = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", hxid='" + hxid + '\'' +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
