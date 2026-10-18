package com.ch.personmis.repository;

import com.ch.personmis.entity.WelfarePoints;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WelfarePointsRepository {
    // 添加积分记录
    int addWelfarePoints(WelfarePoints welfarePoints);

    // 更新积分记录
    int updateWelfarePoints(WelfarePoints welfarePoints);

    // 根据ID查询积分记录
    WelfarePoints selectWelfarePointsById(int id);

    // 根据员工ID查询积分列表
    List<WelfarePoints> selectWelfarePointsByStaffId(@Param("staff_id") int staff_id);

    // 分页查询积分记录
    List<WelfarePoints> selectWelfarePointsByPage(WelfarePoints welfarePoints);

    // 条件查询积分记录
    List<WelfarePoints> selectWelfarePointsByCon(WelfarePoints welfarePoints);

    // 查询员工积分汇总
    Map<String, Object> selectPointsSummary(@Param("staff_id") int staff_id);

    // 删除积分记录（逻辑删除）
    int deleteWelfarePoints(int id);

    // 统计积分记录数量
    int countWelfarePoints(WelfarePoints welfarePoints);

    // 积分排行榜
    List<Map<String, Object>> selectPointsRanking(@Param("startDate") String startDate, 
                                                    @Param("departId") Integer departId, 
                                                    @Param("limit") Integer limit);

    // 积分历史序列（用于画像分析）
    List<Map<String, Object>> selectPointsHistory(@Param("staff_id") Integer staffId);
}

