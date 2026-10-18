package com.ch.personmis.controller;

import com.ch.personmis.entity.EmpImage;
import com.ch.personmis.service.EmpImageService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 员工画像控制器
 * 提供员工画像数据的增删改查接口
 * 支持前端员工画像分析和可视化展示
 */
@RestController
public class EmpImageController {
    @Resource
    private EmpImageService empImageService;
    
    /**
     * 添加员工画像记录
     * 用于创建新的员工能力分析和画像数据
     * @param empImage 员工画像对象
     * @return 操作结果
     */
    @PostMapping("/addEmpImage")
    public String addEmpImage(@RequestBody EmpImage empImage) {
        return empImageService.addEmpImage(empImage);
    }

    /**
     * 分页获取员工画像列表
     * 用于员工画像管理和分析页面展示
     * @param empImage 查询条件对象
     * @return 分页员工画像数据
     */
    @GetMapping("/getEmpImageByPage")
    public Map<String, Object> getEmpImageByPage(EmpImage empImage) {
        if (empImage.getCurrentPage() > 0 && empImage.getPageSize() > 0) {
            empImage.setStartIndex((empImage.getCurrentPage() - 1) * empImage.getPageSize());
        }
        return empImageService.selectEmpImageByPage(empImage);
    }
    
    /**
     * 根据条件查询员工画像
     * 支持多条件筛选员工画像数据
     * @param empImage 查询条件对象
     * @return 查询结果数据
     */
    @PostMapping("/selectEmpImageByCon")
    public Map<String, Object> selectEmpImageByCon(@RequestBody EmpImage empImage) {
        if (empImage.getCurrentPage() > 0 && empImage.getPageSize() > 0) {
            empImage.setStartIndex((empImage.getCurrentPage() - 1) * empImage.getPageSize());
        }
        return empImageService.selectEmpImageByCon(empImage);
    }
    
    /**
     * 获取员工画像详情
     * 用于员工画像分析和详情页面展示
     * @param id 员工画像ID
     * @return 员工画像详情对象
     */
    @GetMapping("/getEmpImageDetail")
    public EmpImage getEmpImageDetail(int id) {
        return empImageService.getEmpImageDetail(id);
    }
    
    /**
     * 根据员工ID获取画像数据
     * 用于员工能力分析和可视化展示
     * @param staff_id 员工ID
     * @return 员工画像数据
     */
    @GetMapping("/getEmpImageByStaffId")
    public Map<String, Object> getEmpImageByStaffId(int staff_id) {
        return empImageService.getEmpImageByStaffId(staff_id);
    }
    
    /**
     * 更新员工画像记录
     * 用于修改员工能力分析和画像数据
     * @param empImage 员工画像对象
     * @return 操作结果
     */
    @PostMapping("/updateEmpImage")
    public String updateEmpImage(@RequestBody EmpImage empImage) {
        return empImageService.updateEmpImage(empImage);
    }
    
    /**
     * 删除员工画像记录
     * 用于移除不需要的员工画像数据
     * @param id 员工画像ID
     * @return 操作结果
     */
    @PostMapping("/deleteEmpImage")
    public String deleteEmpImage(int id) {
        return empImageService.deleteEmpImage(id);
    }
}

