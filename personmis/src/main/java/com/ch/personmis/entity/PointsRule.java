package com.ch.personmis.entity;

import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

/**
 * 积分规则实体类
 * 用于定义积分自动发放的规则
 * 由积分规则引擎（PointsRuleEngine）使用，在满足条件时自动发放积分
 */
public class PointsRule {
    private Integer id;
    private String ruleName;
    private String ruleType;
    private String triggerCondition;
    private Integer pointsValue;
    private String description;
    private Integer status;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public Integer getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(Integer pointsValue) {
        this.pointsValue = pointsValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}

