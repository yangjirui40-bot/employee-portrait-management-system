package com.ch.personmis.controller;

import com.ch.personmis.entity.Report;
import com.ch.personmis.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 报表控制器
 * 提供人事管理报表数据接口
 * 支持前端报表可视化和数据分析展示
 */
@RestController
public class ReportController {
    @Resource
    private ReportService reportService;
    
    /**
     * 获取新员工入职报表
     * 用于新员工入职统计和趋势分析可视化
     * @param report 报表查询条件
     * @return 新员工报表数据
     */
    @GetMapping("/getNewStaffReport")
    public Map<String, Object> getNewStaffReport(Report report){
       return reportService.getNewStaffReport(report);
    }
    
    /**
     * 根据条件查询新员工入职报表
     * 支持多条件筛选新员工入职数据
     * @param report 报表查询条件对象
     * @return 筛选后的新员工报表数据
     */
    @PostMapping("/selectNewStaffReportByCon")
    public Map<String, Object> selectNewStaffReportByCon(@RequestBody Report report){
        return reportService.getNewStaffReport(report);
    }
    
    /**
     * 获取离职员工报表
     * 用于离职员工统计和离职原因分析可视化
     * @param report 报表查询条件
     * @return 离职员工报表数据
     */
    @GetMapping("/getQuitStaffReport")
    public Map<String, Object> getQuitStaffReport(Report report){
        return reportService.getQuitStaffReport(report);
    }
    
    /**
     * 根据条件查询离职员工报表
     * 支持多条件筛选离职员工数据
     * @param report 报表查询条件对象
     * @return 筛选后的离职员工报表数据
     */
    @PostMapping("/selectQuitStaffReportByCon")
    public Map<String, Object> selectQuitStaffReportByCon(@RequestBody Report report){
        return reportService.getQuitStaffReport(report);
    }
    
    /**
     * 获取员工调动报表
     * 用于员工调动统计和部门间流动分析可视化
     * @param report 报表查询条件
     * @return 员工调动报表数据
     */
    @GetMapping("/getTransferReport")
    public Map<String, Object> getTransferReport(Report report){
        return reportService.getTransferReport(report);
    }
    
    /**
     * 根据条件查询员工调动报表
     * 支持多条件筛选员工调动数据
     * @param report 报表查询条件对象
     * @return 筛选后的员工调动报表数据
     */
    @PostMapping("/selectTransferStaffReportByCon")
    public Map<String, Object> selectTransferStaffReportByCon(@RequestBody Report report){
        return reportService.getTransferReport(report);
    }
}
