package com.ch.personmis.service;

import com.ch.personmis.entity.PeriodByCon;
import com.ch.personmis.entity.Staff;
import com.ch.personmis.entity.StaffByCon;

import java.util.Map;

/**
 * 员工业务服务接口
 *
 * 功能说明：
 * 定义员工管理相关的业务能力（增删改查、分页/条件查询、试用期管理）。
 */
public interface StaffService {
    String addStaff(Staff staff );
    Map<String, Object> selectStaffByPage(StaffByCon staffByCon);
    Map<String, Object> selectStaffByCon(StaffByCon staffByCon);
    Staff getStaffDetail(int id);
    String updateStaff(Staff staff );
    Map<String, Object> selectPeriodsByPage(PeriodByCon periodByCon);
    Map<String, Object> selectPeriodByCon(PeriodByCon periodByCon);
    String periodOp(int id, String status);
    String setEndDate(int id, String enddate);
    String deleteStaff(int id);
}
