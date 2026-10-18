package com.ch.personmis.service;

import com.ch.personmis.entity.Report;
import com.ch.personmis.repository.ReportRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现
 *
 * 功能说明：
 * 提供“新入职/离职/调动”三类报表的分页查询能力。
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Resource
    private ReportRepository reportRepository; // 报表 SQL 数据访问层
    @Override
    public Map<String, Object> getNewStaffReport(Report report) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：分页查询
        report.setAct("byPage");
        List<Map<String, Object>> newStaffReports = reportRepository.reportSelectNew(report);
        map.put("newStaffReports", newStaffReports);
        // act='byNoPage'：total 统计
        report.setAct("byNoPage");
        map.put("total", reportRepository.reportSelectNew(report).size());
        return map;
    }

    @Override
    public Map<String, Object> getQuitStaffReport(Report report) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：分页查询
        report.setAct("byPage");
        List<Map<String, Object>> newQuitReports = reportRepository.reportSelectQuit(report);
        map.put("newQuitReports", newQuitReports);
        // act='byNoPage'：total 统计
        report.setAct("byNoPage");
        map.put("total", reportRepository.reportSelectQuit(report).size());
        return map;
    }

    @Override
    public Map<String, Object> getTransferReport(Report report) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：分页查询
        report.setAct("byPage");
        List<Map<String, Object>> newTransferReports = reportRepository.reportSelectTransfer(report);
        map.put("newTransferReports", newTransferReports);
        // act='byNoPage'：total 统计
        report.setAct("byNoPage");
        map.put("total", reportRepository.reportSelectTransfer(report).size());
        return map;
    }
}
