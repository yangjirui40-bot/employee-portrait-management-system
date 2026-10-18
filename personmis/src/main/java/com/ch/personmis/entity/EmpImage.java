package com.ch.personmis.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 员工画像实体类
 * 用于存储通过算法分析生成的员工多维度画像数据
 * 包含能力画像（5个维度）、潜力画像（6个维度）、分析结果
 * 由Python算法服务生成，用于人员匹配、绩效评估等场景
 */
public class EmpImage {
    // 核心字段
    private Integer id;
    private Integer staff_id;
    private String image_type;
    private Double ability_score;
    
    // 能力维度字段（5个）
    private Double tech_ability;
    private Double workload_ability;
    private Double quality_ability;
    private Double contribution_ability;
    private Double stability_ability;
    
    // 潜力维度字段（6个）
    private Double performance_trend;
    private Double learning_ability;
    private Double growth_speed;
    private Double incentive_response;
    private Double promotion_probability;
    private String retention_risk;
    
    // 分析结果字段（5个）
    private String performance_level;
    private String skill_tags;
    private String work_style;
    private String career_orientation;
    private String risk_level;
    
    // 系统字段
    private String model_version;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date created_time;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updated_time;
    private Integer status;
    private String sname;
    private String sid;
    private String created_time1;
    private String updated_time1;
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

    // 添加驼峰命名的getter/setter，确保前端能正确接收
    public Integer getStaffId() {
        return staff_id;
    }

    public void setStaffId(Integer staffId) {
        this.staff_id = staffId;
    }

    public String getImage_type() {
        return image_type;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public Double getAbility_score() {
        return ability_score;
    }

    public void setAbility_score(Double ability_score) {
        this.ability_score = ability_score;
    }

    public Double getTech_ability() {
        return tech_ability;
    }

    public void setTech_ability(Double tech_ability) {
        this.tech_ability = tech_ability;
    }

    public Double getWorkload_ability() {
        return workload_ability;
    }

    public void setWorkload_ability(Double workload_ability) {
        this.workload_ability = workload_ability;
    }

    public Double getQuality_ability() {
        return quality_ability;
    }

    public void setQuality_ability(Double quality_ability) {
        this.quality_ability = quality_ability;
    }

    public Double getContribution_ability() {
        return contribution_ability;
    }

    public void setContribution_ability(Double contribution_ability) {
        this.contribution_ability = contribution_ability;
    }

    public Double getStability_ability() {
        return stability_ability;
    }

    public void setStability_ability(Double stability_ability) {
        this.stability_ability = stability_ability;
    }

    public Double getPerformance_trend() {
        return performance_trend;
    }

    public void setPerformance_trend(Double performance_trend) {
        this.performance_trend = performance_trend;
    }

    public Double getLearning_ability() {
        return learning_ability;
    }

    public void setLearning_ability(Double learning_ability) {
        this.learning_ability = learning_ability;
    }

    public Double getGrowth_speed() {
        return growth_speed;
    }

    public void setGrowth_speed(Double growth_speed) {
        this.growth_speed = growth_speed;
    }

    public Double getIncentive_response() {
        return incentive_response;
    }

    public void setIncentive_response(Double incentive_response) {
        this.incentive_response = incentive_response;
    }

    public Double getPromotion_probability() {
        return promotion_probability;
    }

    public void setPromotion_probability(Double promotion_probability) {
        this.promotion_probability = promotion_probability;
    }

    public String getRetention_risk() {
        return retention_risk;
    }

    public void setRetention_risk(String retention_risk) {
        this.retention_risk = retention_risk;
    }

    public String getPerformance_level() {
        return performance_level;
    }

    public void setPerformance_level(String performance_level) {
        this.performance_level = performance_level;
    }

    public String getSkill_tags() {
        return skill_tags;
    }

    public void setSkill_tags(String skill_tags) {
        this.skill_tags = skill_tags;
    }

    public String getWork_style() {
        return work_style;
    }

    public void setWork_style(String work_style) {
        this.work_style = work_style;
    }

    public String getCareer_orientation() {
        return career_orientation;
    }

    public void setCareer_orientation(String career_orientation) {
        this.career_orientation = career_orientation;
    }

    public String getRisk_level() {
        return risk_level;
    }

    public void setRisk_level(String risk_level) {
        this.risk_level = risk_level;
    }

    public String getModel_version() {
        return model_version;
    }

    public void setModel_version(String model_version) {
        this.model_version = model_version;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Date getUpdated_time() {
        return updated_time;
    }

    public void setUpdated_time(Date updated_time) {
        this.updated_time = updated_time;
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

    public String getCreated_time1() {
        return created_time1;
    }

    public void setCreated_time1(String created_time1) {
        this.created_time1 = created_time1;
    }

    public String getUpdated_time1() {
        return updated_time1;
    }

    public void setUpdated_time1(String updated_time1) {
        this.updated_time1 = updated_time1;
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
}

