package com.ch.personmis.entity;

import java.util.Date;

/**
 * 部门实体类
 * 用于存储部门的基本信息
 * 支持树形结构
 * 是组织架构管理的核心实体
 */
public class Depart {
    private Integer id;
    private String dname;
    private String dtype;
    private String dtel;
    private String dfax;
    private String description;
    private Integer supdepartment;
    private Integer parent_id;
    private Integer order_num;
    private Integer leader_id;
    private Date establishmentdate;
    private String establishmentdate1;//查询时使用
    private String supdepartment1;//查询时使用
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDname() {
        return dname;
    }
    public void setDname(String dname) {
        this.dname = dname;
    }
    public String getDtype() {
        return dtype;
    }
    public void setDtype(String dtype) {
        this.dtype = dtype;
    }
    public String getDtel() {
        return dtel;
    }
    public void setDtel(String dtel) {
        this.dtel = dtel;
    }
    public String getDfax() {
        return dfax;
    }
    public void setDfax(String dfax) {
        this.dfax = dfax;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getSupdepartment() {
        return supdepartment;
    }
    public void setSupdepartment(Integer supdepartment) {
        this.supdepartment = supdepartment;
    }
    public Integer getParentId() {
        return parent_id;
    }
    public void setParentId(Integer parent_id) {
        this.parent_id = parent_id;
    }
    public Integer getOrderNum() {
        return order_num;
    }
    public void setOrderNum(Integer order_num) {
        this.order_num = order_num;
    }
    public Integer getLeaderId() {
        return leader_id;
    }
    public void setLeaderId(Integer leader_id) {
        this.leader_id = leader_id;
    }
    public Date getEstablishmentdate() {
        return establishmentdate;
    }
    public void setEstablishmentdate(Date establishmentdate) {
        this.establishmentdate = establishmentdate;
    }
    public String getEstablishmentdate1() {
        return establishmentdate1;
    }
    public void setEstablishmentdate1(String establishmentdate1) {
        this.establishmentdate1 = establishmentdate1;
    }
    public String getSupdepartment1() {
        return supdepartment1;
    }
    public void setSupdepartment1(String supdepartment1) {
        this.supdepartment1 = supdepartment1;
    }

}
