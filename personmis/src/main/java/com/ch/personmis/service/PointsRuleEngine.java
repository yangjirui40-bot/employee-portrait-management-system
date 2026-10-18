package com.ch.personmis.service;

import com.ch.personmis.entity.PointsRule;
import com.ch.personmis.entity.WelfarePoints;
import com.ch.personmis.entity.WorkloadDetail;
import com.ch.personmis.repository.PointsRuleRepository;
import com.ch.personmis.repository.WelfarePointsRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 积分规则引擎
 * 工作流程：
 * 1. 获取所有激活的积分规则
 * 2. 遍历规则，检查工作量记录是否匹配规则条件
 * 3. 如果匹配，根据规则计算积分并生成积分记录
 * 4. 如果没有任何规则匹配，发放基础工作量奖励（1-2分）
 * 调用时机：
 * 每次添加工作量记录后自动触发（由WorkloadDetailService调用）
 */
@Service
public class PointsRuleEngine {
    @Resource
    private PointsRuleRepository pointsRuleRepository;  // 积分规则数据访问层
    @Resource
    private WelfarePointsRepository welfarePointsRepository;  // 福利积分数据访问层

    /**
     * 评估工作量记录并生成积分

     * 1. 查询所有激活的积分规则
     * 2. 遍历规则，检查工作量是否匹配规则条件
     * 3. 匹配的规则生成对应的积分记录
     * 4. 如果没有任何规则匹配，发放基础奖励（1-2分）
     * 
     * @param workload 工作量明细对象
     * @return 生成的积分记录列表
     */
    public List<WelfarePoints> evaluateWorkloadRules(WorkloadDetail workload) {
        List<WelfarePoints> generatedPoints = new ArrayList<>();
        List<PointsRule> rules = pointsRuleRepository.selectActiveRules();

        System.out.println("=== 积分规则引擎评估 ===");
        System.out.println("员工ID: " + workload.getStaffId() + ", 任务: " + workload.getTaskDesc());
        System.out.println("可用规则数: " + (rules != null ? rules.size() : 0));

        // 如果没有规则，至少给一个基础工作量提交奖励
        if (rules == null || rules.isEmpty()) {
            System.out.println("警告：没有可用的积分规则，发放基础工作量奖励");
            WelfarePoints basePoints = createBaseWorkloadPoints(workload);
            if (basePoints != null) {
                try {
                    int result = welfarePointsRepository.addWelfarePoints(basePoints);
                    if (result > 0) {
                        System.out.println("  发放基础积分: " + basePoints.getPoints_value() + " (插入成功，影响行数: " + result + ")");
                        generatedPoints.add(basePoints);
                    } else {
                        System.err.println("  基础积分插入失败，返回结果: " + result);
                    }
                } catch (Exception e) {
                    System.err.println("  基础积分插入异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return generatedPoints;
        }

        for (PointsRule rule : rules) {
            boolean matched = checkRuleMatch(rule, workload);
            System.out.println("  规则[" + rule.getRuleName() + "] 匹配: " + matched);
            if (matched) {
                WelfarePoints points = createPointsFromRule(rule, workload);
                if (points != null) {
                    try {
                        int result = welfarePointsRepository.addWelfarePoints(points);
                        if (result > 0) {
                            System.out.println("  发放积分: " + points.getPoints_value() + " (插入成功，影响行数: " + result + ")");
                            generatedPoints.add(points);
                        } else {
                            System.err.println("  积分插入失败，返回结果: " + result);
                        }
                    } catch (Exception e) {
                        System.err.println("  积分插入异常: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        // 如果没有任何规则匹配，至少给一个基础工作量提交奖励（1-2分）
        if (generatedPoints.isEmpty() && workload.getTimeSpentHrs() > 0) {
            System.out.println("  没有规则匹配，发放基础工作量奖励");
            WelfarePoints basePoints = createBaseWorkloadPoints(workload);
            if (basePoints != null) {
                try {
                    int result = welfarePointsRepository.addWelfarePoints(basePoints);
                    if (result > 0) {
                        System.out.println("  发放基础积分: " + basePoints.getPoints_value() + " (插入成功，影响行数: " + result + ")");
                        generatedPoints.add(basePoints);
                    } else {
                        System.err.println("  基础积分插入失败，返回结果: " + result);
                    }
                } catch (Exception e) {
                    System.err.println("   基础积分插入异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("评估完成，生成 " + generatedPoints.size() + " 条积分记录");
        return generatedPoints;
    }
    
    /**
     * 检查规则是否匹配工作量记录
     * 
     * 规则类型说明：
     *  QUALITY_HIGH: 高质量奖励（质量评分>=0.8/0.95/1.0）
     * DIFFICULTY_HIGH: 高难度奖励（任务难度>=1.2/1.5/1.8）
     *  HOURS_LONG: 长工时奖励（工时>=10/20/30小时）
     *  QUALITY_DIFFICULTY_COMBO: 高质量+高难度组合奖励
     *  ECI_HIGH: 高ECI奖励（ECI = 工时 × 难度 × 质量）
     * 
     * @param rule 积分规则对象
     * @param workload 工作量明细对象
     * @return true表示匹配，false表示不匹配
     */
    private WelfarePoints createBaseWorkloadPoints(WorkloadDetail workload) {
        WelfarePoints points = new WelfarePoints();
        points.setStaff_id(workload.getStaffId());
        points.setPoints_type("自动奖励");
        // 根据工时给予基础积分：工时>=5小时给2分，否则给1分
        int basePoints = workload.getTimeSpentHrs() >= 5 ? 2 : 1;
        points.setPoints_value(basePoints);
        points.setPoints_source("工作量提交");
        // 即使workload.getId()为null也可以插入积分
        points.setSource_id(workload.getId());
        String taskDesc = workload.getTaskDesc() != null ? workload.getTaskDesc() : "未命名任务";
        points.setSource_desc("工作量提交奖励 - " + taskDesc);
        points.setRule_id(null);  // 基础奖励不关联规则
        points.setAuto_generated(1);  // 标记为自动生成
        points.setTransaction_type("EARN");  // 积分获得类型
        points.setUsed_points(0);  // 已使用积分为0
        points.setAvailable_points(basePoints);  // 可用积分等于总积分
        points.setOperator("system");  // 操作者为系统
        points.setStatus(1);  // 状态为有效
        points.setRemark("基础工作量提交奖励");
        
        System.out.println("  创建基础积分记录: 员工ID=" + points.getStaff_id() + ", 积分值=" + basePoints);
        return points;
    }

    private boolean checkRuleMatch(PointsRule rule, WorkloadDetail workload) {
        String ruleType = rule.getRuleType();
        double quality = workload.getQualityScore();  // 质量评分（0-1）
        double weight = workload.getTaskWeight();  // 任务难度权重（通常0.5-2.0）
        double hours = workload.getTimeSpentHrs();  // 工时数
        double eci = hours * weight * quality;  // ECI = 工时 × 难度 × 质量

        System.out.println("  规则类型: " + ruleType + ", 质量: " + quality + ", 难度: " + weight + ", 工时: " + hours + ", ECI: " + eci);

        switch (ruleType) {
            case "QUALITY_HIGH":
                // 质量>=1.0奖励20分，质量>=0.95奖励10分，质量>=0.8奖励5分
                if (quality >= 1.0) return true;
                if (quality >= 0.95) return true;
                if (quality >= 0.8) return true;  // 降低门槛，让更多工作能获得积分
                break;

            case "DIFFICULTY_HIGH":
                // 难度>=1.8奖励30分，难度>=1.5奖励15分，难度>=1.2奖励8分
                if (weight >= 1.8) return true;
                if (weight >= 1.5) return true;
                if (weight >= 1.2) return true;  // 降低门槛
                break;

            case "HOURS_LONG":
                // 工时>=30奖励15分，工时>=20奖励8分，工时>=10奖励5分
                if (hours >= 30) return true;
                if (hours >= 20) return true;
                if (hours >= 10) return true;  // 降低门槛
                break;

            case "QUALITY_DIFFICULTY_COMBO":
                // 高质量+高难度组合奖励
                if (quality >= 0.9 && weight >= 1.5) return true;
                if (quality >= 0.8 && weight >= 1.2) return true;  // 降低门槛
                break;

            case "EFFICIENCY_HIGH":
                // ECI>=25奖励20分，ECI>=15奖励10分（ECI = 工时 × 难度 × 质量）
                if (eci >= 25) return true;
                if (eci >= 15) return true;  // 降低门槛
                break;
        }

        return false;
    }

    /**
     * 根据匹配的积分规则创建积分记录
     * 
     * 功能说明：
     * 当工作量记录匹配某个积分规则时，根据规则配置的积分值创建积分记录。
     * 积分记录会关联到对应的规则ID，便于追溯和统计。
     * 
     * @param rule 匹配的积分规则对象（包含积分值和规则名称）
     * @param workload 工作量明细对象（用于关联来源）
     * @return 福利积分对象，包含规则触发的积分信息
     */
    private WelfarePoints createPointsFromRule(PointsRule rule, WorkloadDetail workload) {
        WelfarePoints points = new WelfarePoints();
        points.setStaff_id(workload.getStaffId());  // 关联员工ID
        points.setPoints_type("自动奖励");  // 积分类型
        points.setPoints_value(rule.getPointsValue());  // 使用规则配置的积分值
        points.setPoints_source("规则触发");  // 积分来源标识
        points.setSource_id(workload.getId());  // 关联工作量记录ID
        points.setSource_desc(rule.getRuleName() + " - " + workload.getTaskDesc());  // 描述信息
        points.setRule_id(rule.getId());  // 关联规则ID，便于追溯
        points.setAuto_generated(1);  // 标记为自动生成
        points.setTransaction_type("EARN");  // 积分获得类型
        points.setUsed_points(0);  // 初始已使用积分为0
        points.setAvailable_points(rule.getPointsValue());  // 可用积分等于总积分
        points.setOperator("system");  // 操作者为系统
        points.setStatus(1);  // 状态为有效
        points.setRemark("自动规则触发");
        
        return points;
    }
}

