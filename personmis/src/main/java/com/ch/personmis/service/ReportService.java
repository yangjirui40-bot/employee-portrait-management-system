package com.ch.personmis.service;

import com.ch.personmis.entity.Report;

import java.util.Map;

/**
 * 报表服务接口
 *
 * 功能说明：
 * 定义报表查询能力（新员工/离职/调动），返回 Map 便于直接给前端渲染。
 */
public interface ReportService {
    Map<String, Object> getNewStaffReport(Report report);
    Map<String, Object> getQuitStaffReport(Report report);
    Map<String, Object> getTransferReport(Report report);
}
