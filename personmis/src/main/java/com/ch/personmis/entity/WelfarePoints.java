package com.ch.personmis.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 福利积分实体类
 * 用于记录员工的积分获得、使用、兑换等交易记录
 */
public class WelfarePoints {
    private Integer id;
    private Integer staff_id;
    private String points_type;
    private Integer points_value;
    private String points_source;
    private Integer source_id;
    private String source_desc;
    private Integer rule_id;
    private Integer auto_generated;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date expire_date;
    private Integer used_points;
    private Integer available_points;
    private String transaction_type;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date transaction_time;
    private String operator;
    private String remark;
    private Integer status;
    private String sname;
    private String sid;
    private String expire_date1;
    private String transaction_time1;
    private Integer current_available;
    private int currentPage;
    private int pageSize;
    private int startIndex;
    private String act;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(Integer staff_id) {
        this.staff_id = staff_id;
    }

    public String getPoints_type() {
        return points_type;
    }

    public void setPoints_type(String points_type) {
        this.points_type = points_type;
    }

    public Integer getPoints_value() {
        return points_value;
    }

    public void setPoints_value(Integer points_value) {
        this.points_value = points_value;
    }

    public String getPoints_source() {
        return points_source;
    }

    public void setPoints_source(String points_source) {
        this.points_source = points_source;
    }

    public Integer getSource_id() {
        return source_id;
    }

    public void setSource_id(Integer source_id) {
        this.source_id = source_id;
    }

    public String getSource_desc() {
        return source_desc;
    }

    public void setSource_desc(String source_desc) {
        this.source_desc = source_desc;
    }

    public Date getExpire_date() {
        return expire_date;
    }

    public void setExpire_date(Date expire_date) {
        this.expire_date = expire_date;
    }

    public Integer getUsed_points() {
        return used_points;
    }

    public void setUsed_points(Integer used_points) {
        this.used_points = used_points;
    }

    public Integer getAvailable_points() {
        return available_points;
    }

    public void setAvailable_points(Integer available_points) {
        this.available_points = available_points;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public Date getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(Date transaction_time) {
        this.transaction_time = transaction_time;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getExpire_date1() {
        return expire_date1;
    }

    public void setExpire_date1(String expire_date1) {
        this.expire_date1 = expire_date1;
    }

    public String getTransaction_time1() {
        return transaction_time1;
    }

    public void setTransaction_time1(String transaction_time1) {
        this.transaction_time1 = transaction_time1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public Integer getRule_id() {
        return rule_id;
    }

    public void setRule_id(Integer rule_id) {
        this.rule_id = rule_id;
    }

    public Integer getAuto_generated() {
        return auto_generated;
    }

    public void setAuto_generated(Integer auto_generated) {
        this.auto_generated = auto_generated;
    }

    public Integer getCurrent_available() {
        return current_available;
    }

    public void setCurrent_available(Integer current_available) {
        this.current_available = current_available;
    }
}

