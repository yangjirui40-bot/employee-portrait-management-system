package com.ch.personmis.entity;

/**
 * 用户实体类
 * 用于存储系统登录用户的基本信息
 * 用于登录认证和权限管理
 */
public class UserEntity {
    private int id;//主键
    private String uname;
    private String upwd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpwd() {
        return upwd;
    }

    public void setUpwd(String upwd) {
        this.upwd = upwd;
    }
}
