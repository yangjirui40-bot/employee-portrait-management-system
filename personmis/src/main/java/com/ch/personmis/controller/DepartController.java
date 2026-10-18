package com.ch.personmis.controller;

import com.ch.personmis.entity.Depart;
import com.ch.personmis.entity.DepartByCon;
import com.ch.personmis.service.DepartService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class DepartController {
    @Resource
    private DepartService departService;
    @GetMapping("/getDepartment")
    public List<Depart> selectDepart() {
        return departService.selectDepart();
    }
    @GetMapping("/getDepartmentByPage")
    public Map<String, Object> selectDepartByPage(DepartByCon departByCon) {
        return departService.selectDepartByPage(departByCon);
    }
    @PostMapping("/addDepartment")
    public  String addDepartment(@RequestBody Depart depart){
        return  departService.addDepartment(depart);
    }
    @PostMapping("/selectDepartmentsByCon")
    public Map<String, Object> selectDepartmentsByCon(@RequestBody DepartByCon departByCon) {
        return departService.selectDepartmentsByCon(departByCon);
    }
    @GetMapping("/getDepartmentDetail")
    public Map<String, Object> getDepartmentDetail(int id){
        return departService.getDepartmentDetail(id);
    }
    @PostMapping("/updateDepartment")
    public  String updateDepartment(@RequestBody Depart depart){
        return  departService.updateDepartment(depart);
    }
    @PostMapping("/deleteDepartment")
    public  String deleteDepartment(int id){
        return departService.deleteDepartment(id);
    }
    // 在 DepartController 类中添加
    @GetMapping("/getDepartmentWorkloadStats")
    public Map<String, Object> getDepartmentWorkloadStats() {
        return departService.getDepartmentWorkloadStats();
    }

    @GetMapping("/getDepartmentWorkloadSummary")
    public Map<String, Object> getDepartmentWorkloadSummary() {
        return departService.getDepartmentWorkloadSummary();
    }

    @GetMapping("/getDepartmentWorkloadTrend")
    public Map<String, Object> getDepartmentWorkloadTrend(@RequestParam Integer departmentId) {
        return departService.getDepartmentWorkloadTrend(departmentId);
    }

    @GetMapping("/getDepartmentPosts")
    public Map<String, Object> getDepartmentPosts(@RequestParam Integer departmentId) {
        return departService.getDepartmentPosts(departmentId);
    }
}
