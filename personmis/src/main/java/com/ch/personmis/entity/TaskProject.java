package com.ch.personmis.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 项目实体类
 * 用于存储项目的基本信息
 * 用于项目管理和人员分配优化
 */
public class TaskProject {
    private Integer id;
    private String projectName;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    private Double budgetedHours;
    private String requiredSkills;
    private Integer maxStaffCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getBudgetedHours() {
        return budgetedHours;
    }

    public void setBudgetedHours(Double budgetedHours) {
        this.budgetedHours = budgetedHours;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Integer getMaxStaffCount() {
        return maxStaffCount;
    }

    public void setMaxStaffCount(Integer maxStaffCount) {
        this.maxStaffCount = maxStaffCount;
    }
}

