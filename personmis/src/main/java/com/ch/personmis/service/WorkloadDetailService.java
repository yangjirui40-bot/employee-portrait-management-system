package com.ch.personmis.service;

import com.ch.personmis.entity.WorkloadDetail;
import com.ch.personmis.entity.WorkloadDetailByCon;
import com.ch.personmis.entity.WelfarePoints;
import com.ch.personmis.entity.EmpImage;
import com.ch.personmis.repository.WorkloadDetailRepository;
import com.ch.personmis.repository.EmpImageRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * 工作量明细服务类
 * 
 * 核心功能：
 * 1. 工作量记录的增删改查
 * 2. 工作量统计和数据分析
 * 3. 自动触发积分规则引擎（工作量提交后）
 * 4. 自动更新员工画像（调用Python服务）
 * 
 * 业务流程：
 * 添加工作量 -> 触发积分规则 -> 自动更新画像 -> 返回结果
 */
@Service
public class WorkloadDetailService {
    @Resource
    private WorkloadDetailRepository workloadDetailRepository;  // 工作量数据访问层
    @Resource
    private PointsRuleEngine pointsRuleEngine;  // 积分规则引擎（自动计算积分）
    @Resource
    private PythonServiceClient pythonServiceClient;  // Python服务客户端（调用画像分析）
    @Resource
    private EmpImageService empImageService;  // 员工画像服务
    @Resource
    private EmpImageRepository empImageRepository;  // 员工画像数据访问层
    
    /**
     * 添加工作量明细
     * 
     * 业务流程：
     * 1. 保存工作量记录到数据库
     * 2. 自动触发积分规则引擎，根据工作量计算并生成积分
     * 3. 自动调用Python服务更新员工画像（能力画像、绩效画像、潜力画像）
     * 
     * @param workloadDetail 工作量明细对象（包含员工ID、项目ID、工时、任务难度、质量评分等）
     * @return "ok"表示成功，"no"表示失败
     */
    public String addWorkloadDetail(WorkloadDetail workloadDetail) {
        int insertResult = workloadDetailRepository.addWorkloadDetail(workloadDetail);
        System.out.println("=== 工作量提交结果: " + insertResult + " ===");
        System.out.println("员工ID: " + workloadDetail.getStaffId());
        System.out.println("任务描述: " + workloadDetail.getTaskDesc());
        System.out.println("质量: " + workloadDetail.getQualityScore());
        System.out.println("难度: " + workloadDetail.getTaskWeight());
        System.out.println("工时: " + workloadDetail.getTimeSpentHrs());
        System.out.println("生成的工作量ID: " + workloadDetail.getId());
        
        if(insertResult > 0) {
            System.out.println("工作量提交成功，触发积分规则引擎");
            try {
                List<WelfarePoints> generatedPoints = pointsRuleEngine.evaluateWorkloadRules(workloadDetail);
                System.out.println("自动生成积分记录数: " + generatedPoints.size());
                if (generatedPoints.isEmpty()) {
                    System.err.println("警告：没有生成任何积分记录，请检查规则配置");
                }
            } catch (Exception e) {
                System.err.println("规则引擎执行异常: " + e.getMessage());
                e.printStackTrace();
            }
            
            // 自动更新画像
            try {
                updateEmployeeImages(workloadDetail.getStaffId());
            } catch (Exception e) {
                System.err.println("画像更新异常: " + e.getMessage());
                e.printStackTrace();
                // 画像更新失败不影响工作量添加的成功返回
            }
            
            return "ok";
        } else {
            System.err.println("❌ 工作量提交失败，插入结果: " + insertResult);
        return "no";
        }
    }

    /**
     * 条件查询工作量明细（支持分页）
     * 
     * @param workloadDetailByCon 查询条件对象（包含员工ID、项目ID、日期范围、分页参数等）
     * @return 包含工作量明细列表和总数的Map
     */
    public Map<String, Object> selectWorkloadDetailByCon(WorkloadDetailByCon workloadDetailByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        workloadDetailByCon.setAct("byPage");
        List<WorkloadDetail> workloadDetails = workloadDetailRepository.selectWorkloadDetailByCon(workloadDetailByCon);
        map.put("workloadDetails", workloadDetails);
        workloadDetailByCon.setAct("byNoPage");
        map.put("total", workloadDetailRepository.selectWorkloadDetailByCon(workloadDetailByCon).size());
        return map;
    }

    /**
     * 删除工作量明细
     * @param id 工作量明细ID
     * @return "ok"表示成功，"no"表示失败
     */
    public String deleteWorkloadDetail(int id) {
        if (workloadDetailRepository.deleteWorkloadDetail(id) > 0)
            return "ok";
        return "no";
    }

    /**
     * 获取员工工作量统计数据
     * 
     * 统计内容：
     * - 总记录数、总工时
     * - 平均任务难度权重、平均质量评分
     * - ECI（效率贡献指数）= 工时 × 难度 × 质量
     * - 历史质量评分序列（用于计算趋势）
     * - 历史任务难度序列（用于计算学习能力）
     * 
     * @param staffId 员工ID
     * @return 包含统计数据的Map，用于画像分析
     */
    public Map<String, Object> getWorkloadStatsByStaffId(Integer staffId) {
        Map<String, Object> result = workloadDetailRepository.selectWorkloadStatsByStaffId(staffId);
        // #region agent log
        // 内部调试日志：用于排查统计字段（quality/task_weight/推断技能标签）是否按预期从 SQL 返回。
        // 说明：这是“开发期诊断信息”，不参与业务计算、不影响接口返回。
        try {
            String payload = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"pre-fix\",\"hypothesisId\":\"H1\",\"location\":\"WorkloadDetailService:getWorkloadStatsByStaffId\",\"message\":\"DB stats fetched\",\"data\":{\"staffId\":%s,\"total_records\":%s,\"quality_scores_str\":%s,\"task_weight_history_str\":%s},\"timestamp\":%d}%n",
                    staffId,
                    result != null ? result.get("total_records") : null,
                    result != null ? "\"" + result.get("quality_scores_str") + "\"" : null,
                    result != null ? "\"" + result.get("task_weight_history_str") + "\"" : null,
                    System.currentTimeMillis());
            Files.write(Path.of("d:\\SVN\\homework\\.cursor\\debug.log"), payload.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
        // #endregion
        // #region agent log
        // 内部调试日志：验证“推断技能标签 inferred_skill_tags”是否正确拼接。
        try {
            String payload = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run3-postfix\",\"hypothesisId\":\"H6\",\"location\":\"WorkloadDetailService:getWorkloadStatsByStaffId\",\"message\":\"DB stats fetched (including inferred_skill_tags)\",\"data\":{\"staffId\":%s,\"inferred_skill_tags\":%s},\"timestamp\":%d}%n",
                    staffId,
                    result != null ? "\"" + result.get("inferred_skill_tags") + "\"" : null,
                    System.currentTimeMillis());
            Files.write(Path.of("d:\\SVN\\homework\\.cursor\\debug.log"), payload.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
        // #endregion
        return result;
    }
    
    /**
     * 获取工作量统计信息（用于图表展示）
     * @param staffId 员工ID
     * @param projectId 项目ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包含总工时、任务数、平均质量、ECI得分等统计信息
     */
    public Map<String, Object> getStatistics(Integer staffId, Integer projectId, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> stats = workloadDetailRepository.selectStatistics(staffId, projectId, startDate, endDate);
        
        double totalHours = stats.get("total_hours") != null ? ((Number) stats.get("total_hours")).doubleValue() : 0.0;
        double avgTaskWeight = stats.get("avg_task_weight") != null ? ((Number) stats.get("avg_task_weight")).doubleValue() : 0.0;
        double avgQuality = stats.get("avg_quality") != null ? ((Number) stats.get("avg_quality")).doubleValue() : 0.0;
        int taskCount = stats.get("task_count") != null ? ((Number) stats.get("task_count")).intValue() : 0;
        double eciScore = stats.get("eci_score") != null ? ((Number) stats.get("eci_score")).doubleValue() : 0.0;
        double eciScoreOldApprox = totalHours * avgTaskWeight * avgQuality;

        // #region agent log
        // 内部调试日志：对比“SQL 精确求和的 ECI”与“用均值近似计算的 ECI”，避免前端看到异常波动。
        try {
            String payload = String.format("{\"sessionId\":\"debug-session\",\"runId\":\"run4-postfix\",\"hypothesisId\":\"H7\",\"location\":\"WorkloadDetailService:getStatistics\",\"message\":\"ECI compare (exact vs approx)\",\"data\":{\"staffId\":%s,\"projectId\":%s,\"startDate\":%s,\"endDate\":%s,\"eci_exact\":%s,\"eci_approx\":%s,\"total_hours\":%s,\"avg_task_weight\":%s,\"avg_quality\":%s,\"task_count\":%s},\"timestamp\":%d}%n",
                    staffId,
                    projectId,
                    startDate != null ? "\"" + startDate + "\"" : null,
                    endDate != null ? "\"" + endDate + "\"" : null,
                    eciScore,
                    eciScoreOldApprox,
                    totalHours,
                    avgTaskWeight,
                    avgQuality,
                    taskCount,
                    System.currentTimeMillis());
            Files.write(Path.of("d:\\SVN\\homework\\.cursor\\debug.log"), payload.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
        // #endregion
        
        result.put("totalHours", Math.round(totalHours * 100) / 100.0);
        result.put("taskCount", taskCount);
        result.put("avgQuality", Math.round(avgQuality * 100) / 100.0);
        result.put("eciScore", Math.round(eciScore * 100) / 100.0);
        result.put("avgTaskWeight", Math.round(avgTaskWeight * 100) / 100.0);
        
        return result;
    }
    
    public List<Map<String, Object>> getHeatmapData(Integer staffId, Integer projectId, String startDate, String endDate) {
        return workloadDetailRepository.selectHeatmapData(staffId, projectId, startDate, endDate);
    }
    
    public List<Map<String, Object>> getMonthlyData(Integer staffId, Integer projectId, String startDate, String endDate) {
        return workloadDetailRepository.selectMonthlyData(staffId, projectId, startDate, endDate);
    }
    
    public List<Map<String, Object>> getGanttData(Integer projectId) {
        return workloadDetailRepository.selectGanttData(projectId);
    }
    
    public List<Map<String, Object>> getProjectPerformance(Integer projectId) {
        return workloadDetailRepository.selectProjectPerformance(projectId);
    }
    
    /**
     * 更新员工画像
     * 功能说明：
     * 1. 获取员工工作量统计数据
     * 2. 调用Python服务生成三种类型的画像：
     *    能力画像：技术能力、工作量能力、稳定性能力
     *    绩效画像：质量能力、贡献能力、绩效趋势
     *    潜力画像：学习能力、成长速度、激励响应、晋升概率
     * 3. 将画像数据保存到数据库（如果已存在则更新，否则插入）
     * 调用时机：每次添加工作量记录后自动触发
     * @param staffId 员工ID
     */
    private void updateEmployeeImages(Integer staffId) {
        System.out.println("=== 开始更新员工画像，员工ID: " + staffId + " ===");
        // 1. 获取工作量统计数据
        Map<String, Object> workloadStats = getWorkloadStatsByStaffId(staffId);
        if (workloadStats == null || workloadStats.isEmpty()) {
            System.out.println("未找到员工工作量统计数据，跳过画像更新");
            return;
        }
        // 2. 转换数据格式为Python服务所需格式
        Map<String, Object> workloadData = convertWorkloadStatsToPythonFormat(workloadStats);
        // 3. 生成三种类型的画像
        String[] imageTypes = {"ability_image", "performance_image", "potential_image"};
        String[] imageTypeNames = {"能力画像", "绩效画像", "潜力画像"};
        
        for (int i = 0; i < imageTypes.length; i++) {
            String imageType = imageTypes[i];
            String imageTypeName = imageTypeNames[i];
            
            try {
                System.out.println("--- 生成" + imageTypeName + " ---");
                
                // 调用Python服务生成画像
                Map<String, Object> imageData = pythonServiceClient.analyzeImage(staffId, imageType, workloadData);
                
                if (imageData != null && !imageData.isEmpty()) {
                    // 转换为EmpImage实体
                    EmpImage empImage = convertPythonResponseToEmpImage(staffId, imageTypeName, imageData);
                    
                    // 检查是否存在该类型的画像
                    List<EmpImage> existingImages = empImageRepository.selectEmpImageByStaffIdAndTypeLimit(staffId, imageTypeName, 1);
                    
                    if (existingImages == null || existingImages.isEmpty()) {
                        // 不存在，插入新记录
                        empImage.setStatus(1);
                        int result = empImageRepository.addEmpImage(empImage);
                        if (result > 0) {
                            System.out.println("✅ " + imageTypeName + "插入成功");
                        } else {
                            System.err.println("❌ " + imageTypeName + "插入失败");
                        }
                    } else {
                        // 存在，更新最新的一条记录
                        EmpImage existingImage = existingImages.get(0);
                        empImage.setId(existingImage.getId());
                        int result = empImageRepository.updateEmpImage(empImage);
                        if (result > 0) {
                            System.out.println("✅ " + imageTypeName + "更新成功，ID: " + existingImage.getId());
                        } else {
                            System.err.println("❌ " + imageTypeName + "更新失败");
                        }
                    }
                } else {
                    System.err.println("⚠️ " + imageTypeName + "生成失败，Python服务返回空数据");
                }
                
            } catch (Exception e) {
                System.err.println("❌ 生成" + imageTypeName + "异常: " + e.getMessage());
                e.printStackTrace();
                // 继续处理其他类型的画像
            }
        }
        
        System.out.println("=== 员工画像更新完成 ===");
    }
    
    /**
     * 将工作量统计数据转换为Python服务所需的格式
     */
    private Map<String, Object> convertWorkloadStatsToPythonFormat(Map<String, Object> workloadStats) {
        Map<String, Object> result = new HashMap<>();
        
        // 基本统计数据
        result.put("total_records", getNumberValue(workloadStats.get("total_records"), 0));
        result.put("total_hours", getDoubleValue(workloadStats.get("total_hours"), 0.0));
        result.put("avg_task_weight", getDoubleValue(workloadStats.get("avg_task_weight"), 0.0));
        result.put("avg_quality_score", getDoubleValue(workloadStats.get("avg_quality_score"), 0.0));
        result.put("eci_score", getDoubleValue(workloadStats.get("eci_score"), 0.0));
        
        // 转换quality_scores字符串为数组
        String qualityScoresStr = (String) workloadStats.get("quality_scores_str");
        List<Double> qualityScores = parseStringToList(qualityScoresStr);
        result.put("quality_scores", qualityScores);
        
        // 转换task_weight_history字符串为数组
        String taskWeightHistoryStr = (String) workloadStats.get("task_weight_history_str");
        List<Double> taskWeightHistory = parseStringToList(taskWeightHistoryStr);
        result.put("task_weight_history", taskWeightHistory);
        
        // 技能标签
        String skillTags = (String) workloadStats.get("inferred_skill_tags");
        result.put("skill_tags", skillTags != null ? skillTags : "");
        
        return result;
    }
    
    /**
     * 将Python服务返回的数据转换为EmpImage实体
     */
    private EmpImage convertPythonResponseToEmpImage(Integer staffId, String imageType, Map<String, Object> imageData) {
        EmpImage empImage = new EmpImage();
        empImage.setStaff_id(staffId);
        empImage.setImage_type(imageType);
        
        // 能力得分
        Object abilityScore = imageData.get("ability_score");
        if (abilityScore != null) {
            empImage.setAbility_score(getDoubleValue(abilityScore, 0.0));
        }
        
        // 能力维度字段
        empImage.setTech_ability(getDoubleValue(imageData.get("tech_ability"), null));
        empImage.setWorkload_ability(getDoubleValue(imageData.get("workload_ability"), null));
        empImage.setQuality_ability(getDoubleValue(imageData.get("quality_ability"), null));
        empImage.setContribution_ability(getDoubleValue(imageData.get("contribution_ability"), null));
        empImage.setStability_ability(getDoubleValue(imageData.get("stability_ability"), null));
        
        // 潜力维度字段
        empImage.setPerformance_trend(getDoubleValue(imageData.get("performance_trend"), null));
        empImage.setLearning_ability(getDoubleValue(imageData.get("learning_ability"), null));
        empImage.setGrowth_speed(getDoubleValue(imageData.get("growth_speed"), null));
        empImage.setIncentive_response(getDoubleValue(imageData.get("incentive_response"), null));
        empImage.setPromotion_probability(getDoubleValue(imageData.get("promotion_probability"), null));
        
        String retentionRisk = (String) imageData.get("retention_risk");
        if (retentionRisk != null) {
            empImage.setRetention_risk(retentionRisk);
        }
        
        // 分析结果字段
        String performanceLevel = (String) imageData.get("performance_level");
        if (performanceLevel != null) {
            empImage.setPerformance_level(performanceLevel);
        }
        
        String skillTags = (String) imageData.get("skill_tags");
        if (skillTags != null) {
            empImage.setSkill_tags(skillTags);
        }
        
        String workStyle = (String) imageData.get("work_style");
        if (workStyle != null) {
            empImage.setWork_style(workStyle);
        }
        
        String careerOrientation = (String) imageData.get("career_orientation");
        if (careerOrientation != null) {
            empImage.setCareer_orientation(careerOrientation);
        }
        
        String riskLevel = (String) imageData.get("risk_level");
        if (riskLevel != null) {
            empImage.setRisk_level(riskLevel);
        }
        
        // 系统字段
        String modelVersion = (String) imageData.get("model_version");
        if (modelVersion != null) {
            empImage.setModel_version(modelVersion);
        }
        
        empImage.setStatus(1);
        empImage.setCreated_time(new Date());
        
        return empImage;
    }
    
    /**
     * 工具方法：获取数字值
     */
    private Integer getNumberValue(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 工具方法：获取Double值
     */
    private Double getDoubleValue(Object value, Double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 工具方法：将逗号分隔的字符串转换为Double列表
     */
    private List<Double> parseStringToList(String str) {
        List<Double> result = new ArrayList<>();
        if (str == null || str.trim().isEmpty()) {
            return result;
        }
        
        String[] parts = str.split(",");
        for (String part : parts) {
            try {
                Double value = Double.parseDouble(part.trim());
                result.add(value);
            } catch (NumberFormatException e) {
                // 忽略无效的数字
            }
        }
        return result;
    }
    
    /**
     * 批量为所有已有工作量记录生成积分
     * 用于处理通过SQL直接插入的历史数据
     * @return 处理结果统计
     */
    public Map<String, Object> batchGeneratePointsForAllWorkloads() {
        System.out.println("=== 开始批量生成积分 ===");
        Map<String, Object> result = new HashMap<>();
        int totalWorkloads = 0;
        int processedWorkloads = 0;
        int generatedPointsCount = 0;
        int errorCount = 0;
        
        try {
            // 1. 查询所有工作量记录
            List<WorkloadDetail> allWorkloads = workloadDetailRepository.selectAllWorkloadDetails();
            totalWorkloads = allWorkloads.size();
            System.out.println("找到工作量记录总数: " + totalWorkloads);
            
            // 2. 遍历每个工作量记录，生成积分
            for (WorkloadDetail workload : allWorkloads) {
                try {
                    System.out.println("处理工作量ID: " + workload.getId() + ", 员工ID: " + workload.getStaffId());
                    
                    // 调用积分规则引擎生成积分
                    List<WelfarePoints> generatedPoints = pointsRuleEngine.evaluateWorkloadRules(workload);
                    
                    if (!generatedPoints.isEmpty()) {
                        generatedPointsCount += generatedPoints.size();
                        System.out.println("  ✅ 为工作量ID " + workload.getId() + " 生成 " + generatedPoints.size() + " 条积分记录");
                    } else {
                        System.out.println("  ⚠️ 工作量ID " + workload.getId() + " 未生成积分（可能不匹配任何规则）");
                    }
                    
                    processedWorkloads++;
                    
                    // 每处理100条记录输出一次进度
                    if (processedWorkloads % 100 == 0) {
                        System.out.println("进度: " + processedWorkloads + "/" + totalWorkloads);
                    }
                    
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("  ❌ 处理工作量ID " + workload.getId() + " 时发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("=== 批量生成积分完成 ===");
            System.out.println("总工作量数: " + totalWorkloads);
            System.out.println("已处理: " + processedWorkloads);
            System.out.println("生成积分记录数: " + generatedPointsCount);
            System.out.println("错误数: " + errorCount);
            
            result.put("success", true);
            result.put("totalWorkloads", totalWorkloads);
            result.put("processedWorkloads", processedWorkloads);
            result.put("generatedPointsCount", generatedPointsCount);
            result.put("errorCount", errorCount);
            result.put("message", "批量生成积分完成");
            
        } catch (Exception e) {
            System.err.println("❌ 批量生成积分异常: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "批量生成积分失败: " + e.getMessage());
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量为所有有工作量的员工生成画像
     * 用于处理通过SQL直接插入的历史数据
     * @return 处理结果统计
     */
    public Map<String, Object> batchGenerateImagesForAllEmployees() {
        System.out.println("=== 开始批量生成画像 ===");
        Map<String, Object> result = new HashMap<>();
        int totalEmployees = 0;
        int processedEmployees = 0;
        int successCount = 0;
        int errorCount = 0;
        
        try {
            // 1. 查询所有有工作量但无画像的员工
            List<Integer> staffIds = workloadDetailRepository.selectStaffIdsWithoutImages();
            totalEmployees = staffIds.size();
            System.out.println("找到需要生成画像的员工数: " + totalEmployees);
            
            // 2. 遍历每个员工，生成画像
            for (Integer staffId : staffIds) {
                try {
                    System.out.println("处理员工ID: " + staffId);
                    
                    // 调用画像更新方法
                    updateEmployeeImages(staffId);
                    
                    successCount++;
                    processedEmployees++;
                    
                    System.out.println("  ✅ 员工ID " + staffId + " 画像生成成功");
                    
                    // 每处理10个员工输出一次进度
                    if (processedEmployees % 10 == 0) {
                        System.out.println("进度: " + processedEmployees + "/" + totalEmployees);
                    }
                    
                } catch (Exception e) {
                    errorCount++;
                    processedEmployees++;
                    System.err.println("  ❌ 处理员工ID " + staffId + " 时发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("=== 批量生成画像完成 ===");
            System.out.println("总员工数: " + totalEmployees);
            System.out.println("已处理: " + processedEmployees);
            System.out.println("成功数: " + successCount);
            System.out.println("错误数: " + errorCount);
            
            result.put("success", true);
            result.put("totalEmployees", totalEmployees);
            result.put("processedEmployees", processedEmployees);
            result.put("successCount", successCount);
            result.put("errorCount", errorCount);
            result.put("message", "批量生成画像完成");
            
        } catch (Exception e) {
            System.err.println("❌ 批量生成画像异常: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "批量生成画像失败: " + e.getMessage());
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量同步所有（积分+画像）
     * 先批量生成积分，再批量生成画像
     * @return 处理结果统计
     */
    public Map<String, Object> batchSyncAll() {
        System.out.println("=== 开始批量同步所有数据（积分+画像） ===");
        Map<String, Object> result = new HashMap<>();
        
        // 1. 先批量生成积分
        System.out.println("步骤1: 批量生成积分...");
        Map<String, Object> pointsResult = batchGeneratePointsForAllWorkloads();
        
        // 2. 再批量生成画像
        System.out.println("步骤2: 批量生成画像...");
        Map<String, Object> imagesResult = batchGenerateImagesForAllEmployees();
        
        // 3. 汇总结果
        result.put("success", 
            (Boolean) pointsResult.getOrDefault("success", false) && 
            (Boolean) imagesResult.getOrDefault("success", false));
        result.put("pointsResult", pointsResult);
        result.put("imagesResult", imagesResult);
        result.put("message", "批量同步完成");
        
        System.out.println("=== 批量同步所有数据完成 ===");
        
        return result;
    }
}

