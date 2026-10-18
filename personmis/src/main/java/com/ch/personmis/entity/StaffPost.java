package com.ch.personmis.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 员工岗位关联实体类
 * 用于记录员工与岗位的多对多关系，支持员工担任多个岗位
 */
public class StaffPost {
    private Integer id;
    private Integer staff_id;
    private Integer post_id;
    private Integer is_primary;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date start_time;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end_time;
    private Integer created_by;
    private Date created_at;
    private Date updated_at;
    // join query field
    private String post_name;

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

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public Integer getIs_primary() {
        return is_primary;
    }

    public void setIs_primary(Integer is_primary) {
        this.is_primary = is_primary;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getPost_name() {
        return post_name;
    }

    public void setPost_name(String post_name) {
        this.post_name = post_name;
    }
}
