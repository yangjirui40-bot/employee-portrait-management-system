package com.ch.personmis.controller;

import com.ch.personmis.entity.PeriodByCon;
import com.ch.personmis.entity.Staff;
import com.ch.personmis.entity.StaffByCon;
import com.ch.personmis.service.StaffService;
import com.ch.personmis.entity.Transfer;
import com.ch.personmis.service.TransferService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 员工管理控制器
 * 
 * 功能说明：
 * 1. 员工基本信息管理（增删改查）
 * 2. 员工分页查询和条件查询
 * 3. 试用期管理
 * 4. 岗位调动管理
 * 
 * @author 系统
 */
@RestController
public class StaffController {
    @Resource
    private StaffService staffService;  // 员工业务服务
    @Resource
    private TransferService transferService;  // 岗位调动业务服务
    /**
     * 添加新员工
     * @param staff 员工信息对象
     * @return "ok"表示成功，"no"表示失败
     */
    @PostMapping("/addStaff")
    public  String addStaff(@RequestBody Staff staff ){
        return staffService.addStaff(staff);
    }
    
    /**
     * 分页查询员工列表
     * @param staffByCon 查询条件对象（包含分页参数）
     * @return 包含员工列表和总数的Map
     */
    @GetMapping("/getStaffByPage")
    public Map<String, Object> selectStaffByPage(StaffByCon staffByCon) {
        return staffService.selectStaffByPage(staffByCon);
    }
    
    /**
     * 条件查询员工列表（支持姓名、部门等条件）
     * @param staffByCon 查询条件对象
     * @return 包含员工列表和总数的Map
     */
    @PostMapping("/selectStaffByCon")
    public Map<String, Object> selectStaffByCon(@RequestBody StaffByCon staffByCon){
        return staffService.selectStaffByCon(staffByCon);
    }
    
    /**
     * 根据ID查询员工详细信息
     * @param id 员工ID
     * @return 员工详细信息对象
     */
    @GetMapping("/getStaffDetail")
    public Staff getStaffDetail(int id){
        return staffService.getStaffDetail(id);
    }
    /**
     * 更新员工信息
     * 注意：depart_id和post_id不能为空，否则会触发数据库约束错误
     * @param staff 员工信息对象（必须包含id、depart_id、post_id）
     * @return "ok"表示成功，"no"表示失败
     */
    @PostMapping("/updateStaff")
    public  String updateStaff(@RequestBody Staff staff ){
        try {
            System.out.println("=== 更新员工信息 ===");
            System.out.println("员工ID: " + staff.getId());
            System.out.println("员工姓名: " + staff.getSname());
            System.out.println("部门ID: " + staff.getDepart_id());
            System.out.println("职位ID: " + staff.getPost_id());
            return staffService.updateStaff(staff);
        } catch (Exception e) {
            System.err.println("更新员工信息时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // ==================== 试用期管理 ====================
    
    /**
     * 分页查询试用期员工列表
     * @param periodByCon 查询条件对象（包含分页参数）
     * @return 包含试用期员工列表和总数的Map
     */
    @GetMapping("/getPeriods")
    public Map<String, Object> selectPeriodsByPage(PeriodByCon periodByCon) {
        return staffService.selectPeriodsByPage(periodByCon);
    }
    
    /**
     * 条件查询试用期员工
     * @param periodByCon 查询条件对象
     * @return 包含试用期员工列表和总数的Map
     */
    @PostMapping("/selectPeriodByCon")
    public Map<String, Object> selectPeriodByCon(@RequestBody PeriodByCon periodByCon){
        return staffService.selectPeriodByCon(periodByCon);
    }
    
    /**
     * 试用期操作（转正/延期等）
     * @param id 员工ID
     * @param status 操作状态（如"转正"、"延期"）
     * @return "ok"表示成功，"no"表示失败
     */
    @PostMapping("/periodOp")
    public String periodOp(int id, String status){
        return staffService.periodOp(id, status);
    }
    
    /**
     * 设置试用期结束日期
     * @param id 员工ID
     * @param enddate 结束日期
     * @return "ok"表示成功，"no"表示失败
     */
    @PostMapping("/setEndDate")
    public String setEndDate(int id, String enddate){
        return staffService.setEndDate(id, enddate);
    }
    
    // ==================== 岗位调动管理 ====================
    
    /**
     * 查询员工调动前的岗位信息
     * 用于添加调动记录时，自动填充调动前岗位
     * @param id 员工ID
     * @return 包含员工姓名、调动前岗位ID和名称的Transfer对象
     */
    @GetMapping("/getBeforePost")
    public Transfer getBeforePost(int id){
        return transferService.getBeforePost(id);
    }
    
    /**
     * 添加岗位调动记录
     * 同时会更新员工表中的岗位信息
     * @param transfer 调动信息对象
     * @return "ok"表示成功，"no"表示失败
     */
    @PostMapping("/addTransfer")
    public  String addTransfer(@RequestBody Transfer transfer){
        return transferService.addTransfer(transfer);
    }
    
    /**
     * 分页查询调动记录列表
     * @param transfer 查询条件对象（包含分页参数）
     * @return 包含调动记录列表和总数的Map
     */
    @GetMapping("/getTransfer")
    public Map<String, Object> getTransfer(Transfer transfer) {
        return transferService.getTransfer(transfer);
    }
    
    /**
     * 条件查询调动记录（支持员工姓名、员工ID、调动类型等条件）
     * @param transfer 查询条件对象
     * @return 包含调动记录列表和总数的Map
     */
    @PostMapping("/selectTransfersByCon")
    public  Map<String, Object> selectTransfersByCon(@RequestBody Transfer transfer){
        return transferService.selectTransfersByCon(transfer);
    }
    
    /**
     * 删除员工
     * 注意：会检查是否有关联数据（如调动记录、离职记录），有关联数据时不允许删除
     * @param id 员工ID
     * @return "ok"表示成功，"no"表示失败（可能因为有关联数据）
     */
    @PostMapping("/deleteStaff")
    public  String deleteStaff(int id){
        return staffService.deleteStaff(id);
    }
}
