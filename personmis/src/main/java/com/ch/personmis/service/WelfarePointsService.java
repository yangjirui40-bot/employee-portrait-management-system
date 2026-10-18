package com.ch.personmis.service;

import com.ch.personmis.entity.WelfarePoints;

import java.util.Map;

/**
 * 功能说明：
 * 定义积分记录管理与积分汇总查询能力。
 */
public interface WelfarePointsService {
    String addWelfarePoints(WelfarePoints welfarePoints);
    Map<String, Object> selectWelfarePointsByPage(WelfarePoints welfarePoints);
    Map<String, Object> selectWelfarePointsByCon(WelfarePoints welfarePoints);
    WelfarePoints getWelfarePointsDetail(int id);
    Map<String, Object> getWelfarePointsByStaffId(int staff_id);
    Map<String, Object> getPointsSummary(int staff_id);
    String updateWelfarePoints(WelfarePoints welfarePoints);
    String deleteWelfarePoints(int id);
}

