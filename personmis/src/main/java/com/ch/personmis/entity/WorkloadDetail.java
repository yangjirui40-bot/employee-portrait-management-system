package com.ch.personmis.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 工作量明细实体类
 * 用于记录员工在项目中的工作量数据
 * 是工作量统计、画像分析、积分发放的数据基础
 */
public class WorkloadDetail {
    private Integer id;
    private Integer projectId;
    private String taskDesc;
    private Double timeSpentHrs;
    private Double taskWeight;
    private Double qualityScore;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date workDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;
    private Integer staffId;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    // 查询时使用的字段
    private String projectName; // 项目名称，查询时使用
    private String staffName; // 员工姓名，查询时使用
    private String requiredSkills; // 项目所需技能，查询时使用
    private Integer maxStaffCount; // 项目最大人员数，查询时使用

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public Double getTimeSpentHrs() {
        return timeSpentHrs;
    }

    public void setTimeSpentHrs(Double timeSpentHrs) {
        this.timeSpentHrs = timeSpentHrs;
    }

    public Double getTaskWeight() {
        return taskWeight;
    }

    public void setTaskWeight(Double taskWeight) {
        this.taskWeight = taskWeight;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
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

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
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

