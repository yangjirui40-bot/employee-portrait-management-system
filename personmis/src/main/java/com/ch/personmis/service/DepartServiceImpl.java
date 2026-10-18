package com.ch.personmis.service;

import com.ch.personmis.entity.Depart;
import com.ch.personmis.entity.DepartByCon;
import com.ch.personmis.repository.DepartRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门业务服务实现
 *
 * 功能说明：
 * - 部门 CRUD 与分页/条件查询
 * - 部门工作量统计（
 */
@Service
public class DepartServiceImpl implements DepartService {
    @Resource
    private DepartRepository departRepository; // 部门数据访问层（MyBatis Mapper）
    @Override
    public List<Depart> selectDepart() {
        return departRepository.selectDepart(null);
    }
    @Override
    public String addDepartment(Depart depart) {
        if(departRepository.addDepart(depart) > 0)
            return "ok";
        return "no";
    }

    @Override
    public Map<String, Object> selectDepartByPage(DepartByCon departByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：分页查询部门列表
        departByCon.setAct("byPage");
        List<Depart> departs = departRepository.selectDepart(departByCon);
        map.put("departs", departs);
        // act='byNoPage'：用于 total 统计（不加 limit）
        departByCon.setAct("byNoPage");
        map.put("total", departRepository.selectDepart(departByCon).size());
        return map;
    }

    @Override
    public Map<String, Object> selectDepartmentsByCon(DepartByCon departByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：按条件分页查询
        departByCon.setAct("byPage");
        List<Depart> departs = departRepository.selectDepartmentsByCon(departByCon);
        map.put("departs", departs);
        // act='byNoPage'：用于 total 统计（不加 limit）
        departByCon.setAct("byNoPage");
        map.put("total", departRepository.selectDepartmentsByCon(departByCon).size());
        return map;
    }

    @Override
    public Map<String, Object> getDepartmentDetail(int id) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Depart> departs = departRepository.selectDepart(null);
        map.put("departs", departs);
        Depart aDepart = departRepository.getDepartmentDetail(id);
        map.put("aDepart", aDepart);
        return map;
    }

    @Override
    public String updateDepartment(Depart depart) {
        if(departRepository.updateDepartment(depart) > 0)
            return "ok";
        return "no";
    }

    @Override
    public String deleteDepartment(int id) {
        // 删除前检查关联数据：有关联则拒绝删除，避免产生“孤儿数据”
        List<Map<String, Object>>  listMap =departRepository.selectAssociateDepart(id);
        if (listMap.size() <= 0) {
            if (departRepository.deleteDepart(id) > 0)
                return "ok";
        }
        return "no";
    }

    /**
     * 部门工作量统计列表（部门维度聚合）
     * 用途：部门看板/排行，展示每个部门的员工数、工作量总和等。
     */
    @Override
    public Map<String, Object> getDepartmentWorkloadStats() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> stats = departRepository.getDepartmentWorkloadStats();
        
        // 添加日志输出，便于调试
        System.out.println("=== 部门工作量统计数据 ===");
        System.out.println("统计部门数: " + (stats != null ? stats.size() : 0));
        if (stats != null && !stats.isEmpty()) {
            System.out.println("前3个部门数据:");
            for (int i = 0; i < Math.min(3, stats.size()); i++) {
                Map<String, Object> dept = stats.get(i);
                System.out.println("  部门: " + dept.get("dname") + 
                    ", 员工数: " + dept.get("employee_count") + 
                    ", 总工作量: " + dept.get("total_workload"));
            }
        } else {
            System.err.println("⚠️ 部门工作量统计数据为空");
        }
        
        result.put("workloadStats", stats);
        return result;
    }

    /**
     * 部门工作量汇总（全局概览）
     * 用途：卡片指标/总览页（部门数、员工数、总工时、平均质量、最大部门工作量等）。
     */
    @Override
    public Map<String, Object> getDepartmentWorkloadSummary() {
        Map<String, Object> result = departRepository.getDepartmentWorkloadSummary();
        
        // 添加日志输出，便于调试
        System.out.println("=== 部门工作量汇总数据 ===");
        if (result != null) {
            System.out.println("total_departments: " + result.get("total_departments"));
            System.out.println("total_employees: " + result.get("total_employees"));
            System.out.println("total_workload_hours: " + result.get("total_workload_hours"));
            System.out.println("avg_quality_score: " + result.get("avg_quality_score"));
            System.out.println("max_department_workload: " + result.get("max_department_workload"));
            System.out.println("top_department: " + result.get("top_department"));
            
            // 确保所有字段都有值，如果没有则设置为0或默认值
            if (result.get("total_departments") == null) {
                result.put("total_departments", 0);
            }
            if (result.get("total_employees") == null) {
                result.put("total_employees", 0);
            }
            if (result.get("total_workload_hours") == null) {
                result.put("total_workload_hours", 0);
            }
            if (result.get("avg_quality_score") == null) {
                result.put("avg_quality_score", 0);
            }
            if (result.get("max_department_workload") == null) {
                result.put("max_department_workload", 0);
            }
            if (result.get("top_department") == null) {
                result.put("top_department", "暂无");
            }
        } else {
            System.err.println("⚠️ 部门工作量汇总数据为空，返回默认值");
            result = new HashMap<>();
            result.put("total_departments", 0);
            result.put("total_employees", 0);
            result.put("total_workload_hours", 0);
            result.put("avg_quality_score", 0);
            result.put("max_department_workload", 0);
            result.put("top_department", "暂无");
        }
        
        return result;
    }

    /**
     * 部门工作量趋势（时间序列）
     * 用途：折线图展示部门在一段时间内的工作量变化。
     */
    @Override
    public Map<String, Object> getDepartmentWorkloadTrend(Integer departmentId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> trend = departRepository.getDepartmentWorkloadTrend(departmentId);
        result.put("trendData", trend);
        return result;
    }

    /**
     * 查询部门下的岗位信息
     * 用途：部门详情页展示岗位分布、岗位列表等。
     */
    @Override
    public Map<String, Object> getDepartmentPosts(Integer departmentId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> posts = departRepository.getDepartmentPosts(departmentId);
        result.put("posts", posts);
        return result;
    }
}

