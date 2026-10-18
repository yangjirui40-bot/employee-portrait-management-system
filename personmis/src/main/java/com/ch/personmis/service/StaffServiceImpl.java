package com.ch.personmis.service;
import com.ch.personmis.entity.PeriodByCon;
import com.ch.personmis.entity.Staff;
import com.ch.personmis.entity.StaffByCon;
import com.ch.personmis.repository.StaffRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工业务服务实现
 *
 * 功能说明：
 * - 员工基本信息 CRUD
 */
@Service
public class StaffServiceImpl implements StaffService {
    @Resource
    private StaffRepository staffRepository; // 员工数据访问层（MyBatis Mapper）
    @Override
    public String addStaff(Staff staff) {
        if(staffRepository.addStaff(staff) > 0)
            return "ok";
        return "no";
    }

    @Override
    public Map<String, Object> selectStaffByPage(StaffByCon staffByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：分页查询员工列表
        staffByCon.setAct("byPage");
        List<Staff> staffs = staffRepository.selectStaff(staffByCon);
        map.put("staffs", staffs);
        // act='byNoPage'：用于统计 total（不加 limit）
        staffByCon.setAct("byNoPage");
        map.put("total", staffRepository.selectStaff(staffByCon).size());
        return map;
    }

    @Override
    public Map<String, Object> selectStaffByCon(StaffByCon staffByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：按条件分页查询（姓名/部门等）
        staffByCon.setAct("byPage");
        List<Staff> staffs = staffRepository.selectStaffByCon(staffByCon);
        map.put("staffs", staffs);
        // act='byNoPage'：用于统计 total（不加 limit）
        staffByCon.setAct("byNoPage");
        map.put("total", staffRepository.selectStaffByCon(staffByCon).size());
        return map;
    }

    @Override
    public Staff getStaffDetail(int id) {
        System.out.println("=== 查询员工详情 ===");
        System.out.println("员工ID: " + id);
        Staff staff = staffRepository.selectAStaff(id);
        if (staff != null) {
            System.out.println("查询成功:");
            System.out.println("  姓名: " + staff.getSname());
            System.out.println("  部门ID: " + staff.getDepart_id());
            System.out.println("  部门名称: " + staff.getDname());
            System.out.println("  职位ID: " + staff.getPost_id());
            System.out.println("  职位名称: " + staff.getPname());
            System.out.println("  技能标签: " + staff.getSkill_tags());
        } else {
            System.out.println("查询结果为null");
        }
        return staff;
    }

    @Override
    public String updateStaff(Staff staff) {
        try {
            System.out.println("=== Service层：更新员工信息 ===");
            System.out.println("员工ID: " + staff.getId());
            System.out.println("员工姓名: " + staff.getSname());
            System.out.println("部门ID: " + staff.getDepart_id());
            System.out.println("职位ID: " + staff.getPost_id());
            
            // 验证必填字段（depart_id/post_id 在数据库层通常是非空约束）
            if (staff.getId() == null) {
                System.err.println("错误：员工ID为空，无法更新");
                throw new IllegalArgumentException("员工ID不能为空");
            }
            
            if (staff.getDepart_id() == null) {
                System.err.println("错误：部门ID为空，无法更新");
                throw new IllegalArgumentException("部门ID不能为空");
            }
            
            if (staff.getPost_id() == null) {
                System.err.println("错误：职位ID为空，无法更新");
                throw new IllegalArgumentException("职位ID不能为空");
            }
            
            int result = staffRepository.updateStaff(staff);
            System.out.println("更新结果: " + result);
            
            if(result > 0) {
                System.out.println("更新成功");
                return "ok";
            } else {
                System.out.println("更新失败：影响行数为0");
                return "no";
            }
        } catch (IllegalArgumentException e) {
            System.err.println("参数验证失败: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("更新员工信息时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("更新员工信息失败: " + e.getMessage(), e);
        }
    }
    //试用期管理
    @Override
    public Map<String, Object> selectPeriodsByPage(PeriodByCon periodByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 确保分页参数正确设置
        if (periodByCon.getCurrentPage() <= 0) {
            periodByCon.setCurrentPage(1);
        }
        if (periodByCon.getPageSize() <= 0) {
            periodByCon.setPageSize(10);
        }
        periodByCon.setAct("byPage");
        List<Staff> periods = staffRepository.selectPeriodStaff(periodByCon);
        map.put("periods", periods);
        periodByCon.setAct("byNoPage");
        map.put("total", staffRepository.selectPeriodStaff(periodByCon).size());
        return map;
    }

    @Override
    public Map<String, Object> selectPeriodByCon(PeriodByCon periodByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        System.out.println("=== 试用期条件查询 ===");
        System.out.println("员工姓名: " + periodByCon.getSname());
        System.out.println("部门ID: " + periodByCon.getDepart_id());
        System.out.println("岗位ID: " + periodByCon.getPost_id());
        System.out.println("状态: " + periodByCon.getStatus());
        System.out.println("开始日期: " + periodByCon.getStartdate());
        System.out.println("结束日期: " + periodByCon.getEnddate());
        
        // 确保分页参数正确设置
        if (periodByCon.getCurrentPage() <= 0) {
            periodByCon.setCurrentPage(1);
        }
        if (periodByCon.getPageSize() <= 0) {
            periodByCon.setPageSize(10);
        }
        periodByCon.setStartIndex((periodByCon.getCurrentPage() - 1) * periodByCon.getPageSize());
        
        periodByCon.setAct("byPage");
        List<Staff> periods = staffRepository.selectPeriodStaffCon(periodByCon);
        System.out.println("查询到的记录数: " + periods.size());
        
        map.put("periods", periods);
        periodByCon.setAct("byNoPage");
        int total = staffRepository.selectPeriodStaffCon(periodByCon).size();
        System.out.println("总记录数: " + total);
        map.put("total", total);
        return map;
    }

    @Override
    public String periodOp(int id, String status) {
        if (staffRepository.periodOp(id, status) > 0)
            return "ok";
        return "no";
    }

    @Override
    public String setEndDate(int id, String enddate) {
        if (staffRepository.setEndDate(id, enddate) > 0)
            return "ok";
        return "no";
    }

    @Override
    public String deleteStaff(int id) {
        // 删除前先检查关联数据（如调动/离职等），有关联则不允许删除
        List<Map<String, Object>>  listMap =staffRepository.selectAssociateStaff(id);
        if (listMap.size() <= 0) {
            if (staffRepository.deleteStaff(id) > 0)
                return "ok";
        }
        return "no";
    }
}
