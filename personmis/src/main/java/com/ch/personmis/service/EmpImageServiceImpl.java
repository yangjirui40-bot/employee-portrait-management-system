package com.ch.personmis.service;
import com.ch.personmis.entity.EmpImage;
import com.ch.personmis.repository.EmpImageRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工画像服务实现
 *
 * 功能说明：
 * 提供画像数据的增删改查与分页/条件查询能力，供前端页面展示与管理。
 */
@Service
public class EmpImageServiceImpl implements EmpImageService {
    @Resource
    private EmpImageRepository empImageRepository; // 画像数据访问层（MyBatis Mapper）
    @Override
    public String addEmpImage(EmpImage empImage) {
        if (empImage.getStatus() == null) {
            empImage.setStatus(1);
        }
        if (empImageRepository.addEmpImage(empImage) > 0) {
            return "ok";
        }
        return "no";
    }
    @Override
    public Map<String, Object> selectEmpImageByPage(EmpImage empImage) {
        Map<String, Object> map = new HashMap<>();
        // act='byPage'：SQL 会追加 limit startIndex,pageSize，实现分页
        empImage.setAct("byPage");
        List<EmpImage> images = empImageRepository.selectEmpImageByPage(empImage);
        
        System.out.println("=== 后端：查询画像数据（增强版）===");
        System.out.println("查询条件 - image_type: " + empImage.getImage_type());
        System.out.println("查询到的记录数: " + images.size());
        
        // 详细打印每条记录的关键字段
        for (int i = 0; i < Math.min(images.size(), 3); i++) {
            EmpImage img = images.get(i);
            System.out.println(String.format(
                "  记录%d: staff_id=%d, image_type=%s, ability_score=%.2f, workload_ability=%.2f, quality_ability=%.2f",
                i+1,
                img.getStaff_id(),
                img.getImage_type(),
                img.getAbility_score() == null ? 0.0 : img.getAbility_score(),
                img.getWorkload_ability() == null ? 0.0 : img.getWorkload_ability(),
                img.getQuality_ability() == null ? 0.0 : img.getQuality_ability()
            ));
        }
        
        map.put("images", images);      // 推荐：前端统一使用 images
        map.put("empImages", images);   // 兼容：部分页面仍使用 empImages
        empImage.setAct("byNoPage");
        map.put("total", empImageRepository.countEmpImage(empImage));
        return map;
    }
    @Override
    public Map<String, Object> selectEmpImageByCon(EmpImage empImage) {
        Map<String, Object> map = new HashMap<>();
        // act='byPage'：按条件分页查询
        empImage.setAct("byPage");
        List<EmpImage> images = empImageRepository.selectEmpImageByCon(empImage);
        map.put("images", images);
        map.put("empImages", images);
        // act='byNoPage'：仅用于 total 统计（不加 limit）
        empImage.setAct("byNoPage");
        map.put("total", empImageRepository.countEmpImage(empImage));
        return map;
    }
    @Override
    public EmpImage getEmpImageDetail(int id) {
        return empImageRepository.selectEmpImageById(id);
    }
    @Override
    public Map<String, Object> getEmpImageByStaffId(int staff_id) {
        Map<String, Object> map = new HashMap<>();
        List<EmpImage> images = empImageRepository.selectEmpImageByStaffId(staff_id);
        map.put("images", images);
        map.put("total", images.size());
        return map;
    }
    @Override
    public String updateEmpImage(EmpImage empImage) {
        if (empImageRepository.updateEmpImage(empImage) > 0) {
            return "ok";
        }
        return "no";
    }
    @Override
    public String deleteEmpImage(int id) {
        if (empImageRepository.deleteEmpImage(id) > 0) {
            return "ok";
        }
        return "no";
    }
}

