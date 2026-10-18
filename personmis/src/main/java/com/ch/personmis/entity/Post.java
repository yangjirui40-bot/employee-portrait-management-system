package com.ch.personmis.entity;

/**
 * 岗位实体类
 * 用于存储岗位的基本信息
 * 与部门表关联，支持岗位管理和人员匹配
 */
public class Post {
    private Integer id;
    private String pname;
    private String ptype;
    private Integer organization;
    private String post_level;
    private String requirements;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getPname() {
        return pname;
    }
    public void setPname(String pname) {
        this.pname = pname;
    }
    public String getPtype() {
        return ptype;
    }
    public void setPtype(String ptype) {
        this.ptype = ptype;
    }
    public Integer getOrganization() {
        return organization;
    }
    public void setOrganization(Integer organization) {
        this.organization = organization;
    }

    public String getPost_level() {
        return post_level;
    }

    public void setPost_level(String post_level) {
        this.post_level = post_level;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}
