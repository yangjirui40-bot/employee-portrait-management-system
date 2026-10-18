package com.ch.personmis.service;

import com.ch.personmis.entity.WelfarePoints;
import com.ch.personmis.repository.WelfarePointsRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 福利积分服务实现
 *
 * 功能说明：
 * 负责积分记录的增删改查，以及按员工维度的积分汇总查询
 */
@Service
public class WelfarePointsServiceImpl implements WelfarePointsService {
    @Resource
    private WelfarePointsRepository welfarePointsRepository; // 积分数据访问层

    @Override
    public String addWelfarePoints(WelfarePoints welfarePoints) {
        // 默认：有效记录（逻辑删除用 status=0）
        if (welfarePoints.getStatus() == null) {
            welfarePoints.setStatus(1);
        }
        // 默认：可用积分=本次积分值（用于后续“余额”统计）
        if (welfarePoints.getAvailable_points() == null) {
            welfarePoints.setAvailable_points(welfarePoints.getPoints_value());
        }
        // 默认：新增时未使用
        if (welfarePoints.getUsed_points() == null) {
            welfarePoints.setUsed_points(0);
        }
        if (welfarePointsRepository.addWelfarePoints(welfarePoints) > 0) {
            return "ok";
        }
        return "no";
    }

    @Override
    public Map<String, Object> selectWelfarePointsByPage(WelfarePoints welfarePoints) {
        Map<String, Object> map = new HashMap<>();
        // act='byPage'：SQL 追加 limit，实现分页
        welfarePoints.setAct("byPage");
        List<WelfarePoints> pointsList = welfarePointsRepository.selectWelfarePointsByPage(welfarePoints);
        map.put("welfarePoints", pointsList);  // 修复：改为welfarePoints与前端匹配
        welfarePoints.setAct("byNoPage");
        map.put("total", welfarePointsRepository.countWelfarePoints(welfarePoints));
        return map;
    }

    @Override
    public Map<String, Object> selectWelfarePointsByCon(WelfarePoints welfarePoints) {
        Map<String, Object> map = new HashMap<>();
        // act='byPage'：按条件分页查询（姓名/类型/交易类型等）
        welfarePoints.setAct("byPage");
        List<WelfarePoints> pointsList = welfarePointsRepository.selectWelfarePointsByCon(welfarePoints);
        map.put("welfarePoints", pointsList);  // 修复：改为welfarePoints与前端匹配
        welfarePoints.setAct("byNoPage");
        map.put("total", welfarePointsRepository.countWelfarePoints(welfarePoints));
        return map;
    }

    @Override
    public WelfarePoints getWelfarePointsDetail(int id) {
        return welfarePointsRepository.selectWelfarePointsById(id);
    }

    @Override
    public Map<String, Object> getWelfarePointsByStaffId(int staff_id) {
        Map<String, Object> map = new HashMap<>();
        List<WelfarePoints> pointsList = welfarePointsRepository.selectWelfarePointsByStaffId(staff_id);
        map.put("pointsList", pointsList);
        map.put("total", pointsList.size());
        return map;
    }

    @Override
    public Map<String, Object> getPointsSummary(int staff_id) {
        // 汇总：total_earned/total_used/current_available（见 WelfarePointsMapper.xml）
        return welfarePointsRepository.selectPointsSummary(staff_id);
    }

    @Override
    public String updateWelfarePoints(WelfarePoints welfarePoints) {
        if (welfarePointsRepository.updateWelfarePoints(welfarePoints) > 0) {
            return "ok";
        }
        return "no";
    }

    @Override
    public String deleteWelfarePoints(int id) {
        if (welfarePointsRepository.deleteWelfarePoints(id) > 0) {
            return "ok";
        }
        return "no";
    }
}

