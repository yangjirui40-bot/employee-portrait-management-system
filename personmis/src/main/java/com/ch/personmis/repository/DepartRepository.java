package com.ch.personmis.repository;

import com.ch.personmis.entity.Depart;
import com.ch.personmis.entity.DepartByCon;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DepartRepository {
    List<Depart> selectDepart(DepartByCon departByCon);
    int addDepart(Depart depart);
    List<Depart> selectDepartmentsByCon(DepartByCon departByCon);
    Depart getDepartmentDetail(int id);
    int updateDepartment(Depart depart);
    List<Map<String, Object>> selectAssociateDepart(int id);
    int deleteDepart(int id);
    // 在 DepartRepository 接口中添加
    List<Map<String, Object>> getDepartmentWorkloadStats();
    Map<String, Object> getDepartmentWorkloadSummary();
    List<Map<String, Object>> getDepartmentWorkloadTrend(Integer departmentId);
    
    // 获取部门岗位信息
    List<Map<String, Object>> getDepartmentPosts(Integer departmentId);
}
