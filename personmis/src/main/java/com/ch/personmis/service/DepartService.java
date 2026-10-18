package com.ch.personmis.service;

import com.ch.personmis.entity.Depart;
import com.ch.personmis.entity.DepartByCon;

import java.util.List;
import java.util.Map;

/**
 * 部门业务服务接口
 *
 * 功能说明：
 * 定义部门管理与部门工作量统计相关能力，供 Controller 调用。
 */
public interface DepartService {
    List<Depart> selectDepart();
    String addDepartment(Depart depart);
    Map<String, Object> selectDepartByPage(DepartByCon departByCon);
    Map<String, Object> selectDepartmentsByCon(DepartByCon departByCon);
    Map<String, Object> getDepartmentDetail(int id);
    String updateDepartment(Depart depart);
    String deleteDepartment(int id);
    // 部门工作量统计/看板相关接口
    Map<String, Object> getDepartmentWorkloadStats();
    Map<String, Object> getDepartmentWorkloadSummary();
    Map<String, Object> getDepartmentWorkloadTrend(Integer departmentId);
    
    // 获取部门岗位信息
    Map<String, Object> getDepartmentPosts(Integer departmentId);
}
