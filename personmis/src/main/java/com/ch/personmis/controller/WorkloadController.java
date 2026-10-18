package com.ch.personmis.controller;

import com.ch.personmis.entity.TaskProject;
import com.ch.personmis.entity.TaskProjectByCon;
import com.ch.personmis.entity.WorkloadDetail;
import com.ch.personmis.entity.WorkloadDetailByCon;
import com.ch.personmis.service.TaskProjectService;
import com.ch.personmis.service.WorkloadDetailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 工作量控制器
 * 提供项目管理、工作量明细和可视化数据接口
 * 支持前端图表展示和数据分析
 */
@RestController
public class WorkloadController {
    @Resource
    private TaskProjectService taskProjectService;
    @Resource
    private WorkloadDetailService workloadDetailService;
    
    // 项目管理接口
    /**
     * 分页获取项目列表
     * 用于项目管理和甘特图数据展示
     * @param taskProjectByCon 查询条件对象
     * @return 分页项目数据
     */
    @GetMapping("/getTaskProjectByPage")
    public Map<String, Object> selectTaskProjectByPage(TaskProjectByCon taskProjectByCon) {
        return taskProjectService.selectTaskProjectByPage(taskProjectByCon);
    }
    
    @PostMapping("/addTaskProject")
    public String addTaskProject(@RequestBody TaskProject taskProject){
        return taskProjectService.addTaskProject(taskProject);
    }
    
    @PostMapping("/selectTaskProjectByCon")
    public Map<String, Object> selectTaskProjectByCon(@RequestBody TaskProjectByCon taskProjectByCon) {
        return taskProjectService.selectTaskProjectByCon(taskProjectByCon);
    }
    
    @GetMapping("/getTaskProjectDetail")
    public TaskProject getTaskProjectDetail(int id) {
        return taskProjectService.getTaskProjectDetail(id);
    }
    
    @PostMapping("/updateTaskProject")
    public String updateTaskProject(@RequestBody TaskProject taskProject){
        return taskProjectService.updateTaskProject(taskProject);
    }
    
    @GetMapping("/getTaskProject")
    public List<TaskProject> getTaskProject() {
        return taskProjectService.getTaskProject();
    }
    
    @PostMapping("/deleteTaskProject")
    public String deleteTaskProject(int id){
        return taskProjectService.deleteTaskProject(id);
    }
    
    // 工作量明细接口
    /**
     * 添加工作量明细记录
     * 用于工作量数据采集和后续分析
     * @param workloadDetail 工作量明细对象
     * @return 操作结果
     */
    @PostMapping("/addWorkloadDetail")
    public String addWorkloadDetail(@RequestBody WorkloadDetail workloadDetail){
        return workloadDetailService.addWorkloadDetail(workloadDetail);
    }
    
    @PostMapping("/selectWorkloadDetailByCon")
    public Map<String, Object> selectWorkloadDetailByCon(@RequestBody WorkloadDetailByCon workloadDetailByCon) {
        return workloadDetailService.selectWorkloadDetailByCon(workloadDetailByCon);
    }
    
    @PostMapping("/deleteWorkloadDetail")
    public String deleteWorkloadDetail(int id){
        return workloadDetailService.deleteWorkloadDetail(id);
    }
    
    /**
     * 获取员工工作量统计
     * 用于人员分配优化和员工能力分析
     * @param staffId 员工ID
     * @return 工作量统计数据
     */
    @GetMapping("/getWorkloadStatsByStaffId")
    public Map<String, Object> getWorkloadStatsByStaffId(Integer staffId) {
        return workloadDetailService.getWorkloadStatsByStaffId(staffId);
    }
    
    /**
     * 获取工作量统计信息
     * 用于前端图表展示和数据分析
     * @param staffId 员工ID（可选）
     * @param projectId 项目ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 统计结果数据
     */
    @GetMapping("/api/workload/statistics")
    public Map<String, Object> getWorkloadStatistics(
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return workloadDetailService.getStatistics(staffId, projectId, startDate, endDate);
    }
    
    /**
     * 获取工作量热力图数据
     * 用于日历热力图可视化展示
     * @param staffId 员工ID（可选）
     * @param projectId 项目ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 热力图数据列表
     */
    @GetMapping("/api/workload/heatmap")
    public List<Map<String, Object>> getWorkloadHeatmap(
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return workloadDetailService.getHeatmapData(staffId, projectId, startDate, endDate);
    }
    
    @GetMapping("/api/workload/monthly")
    public List<Map<String, Object>> getWorkloadMonthly(
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) Integer projectId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return workloadDetailService.getMonthlyData(staffId, projectId, startDate, endDate);
    }
    
    @GetMapping("/api/project/gantt-data")
    public List<Map<String, Object>> getGanttData(@RequestParam(required = false) Integer projectId) {
        return workloadDetailService.getGanttData(projectId);
    }
    
    /**
     * 获取项目绩效数据
     * 用于项目绩效展示，包含员工ID、姓名、岗位、工时、任务描述和ECI综合效率
     * @param projectId 项目ID
     * @return 项目绩效数据列表
     */
    @GetMapping("/getProjectPerformance")
    public List<Map<String, Object>> getProjectPerformance(@RequestParam Integer projectId) {
        return workloadDetailService.getProjectPerformance(projectId);
    }
    
    /**
     * 批量为所有已有工作量记录生成积分
     * 用于处理通过SQL直接插入的历史数据
     * @return 处理结果统计
     */
    @PostMapping("/batchGeneratePoints")
    public Map<String, Object> batchGeneratePoints() {
        return workloadDetailService.batchGeneratePointsForAllWorkloads();
    }
    
    /**
     * 批量为所有有工作量的员工生成画像
     * 用于处理通过SQL直接插入的历史数据
     * @return 处理结果统计
     */
    @PostMapping("/batchGenerateImages")
    public Map<String, Object> batchGenerateImages() {
        return workloadDetailService.batchGenerateImagesForAllEmployees();
    }
    
    /**
     * 批量同步所有（积分+画像）
     * 先批量生成积分，再批量生成画像
     * @return 处理结果统计
     */
    @PostMapping("/batchSyncAll")
    public Map<String, Object> batchSyncAll() {
        return workloadDetailService.batchSyncAll();
    }
}

