package com.ch.personmis.service;

import com.ch.personmis.repository.WelfarePointsRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 积分排行榜服务
 * 
 * 功能说明：
 * 提供积分排行榜查询功能，支持按时间段、部门筛选，并支持查询个人排名。
 * 
 * 主要功能：
 * 1. 查询积分排行榜
 * 2. 查询个人排名和统计信息
 * 3. 计算排名百分比
 */
@Service
public class PointsRankingService {
    @Resource
    private WelfarePointsRepository welfarePointsRepository;  // 福利积分数据访问层

    /**
     * 获取积分排行榜
     * 
     * 功能说明：
     * 根据时间段、部门ID等条件查询积分排行榜，返回排名前N的员工列表。
     * 
     * 查询逻辑：
     * 1. 根据时间段计算起始日期
     * 2. 查询指定时间段内各员工的总积分
     * 3. 按积分降序排列，返回前N名
     */
    public Map<String, Object> getPointsRanking(String period, Integer departId, Integer limit) {
        Map<String, Object> result = new HashMap<>();
        
        String startDate = getStartDateByPeriod(period);
        int topN = (limit != null && limit > 0) ? limit : 10;
        
        System.out.println("=== 积分排行榜查询参数 ===");
        System.out.println("period: " + period);
        System.out.println("startDate: " + startDate);
        System.out.println("departId: " + departId);
        System.out.println("limit: " + topN);
        
        // 先测试一个简单的查询，看看数据库中是否有数据
        try {
            List<Map<String, Object>> testRanking = welfarePointsRepository.selectPointsRanking(null, null, 100);
            System.out.println("测试查询返回记录数: " + (testRanking != null ? testRanking.size() : 0));
        } catch (Exception e) {
            System.out.println("测试查询异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        List<Map<String, Object>> ranking = welfarePointsRepository.selectPointsRanking(startDate, departId, topN);
        
        System.out.println("积分排行榜查询结果 ");
        System.out.println("返回记录数: " + (ranking != null ? ranking.size() : 0));
        if (ranking != null && ranking.size() > 0) {
            System.out.println("第一条记录: " + ranking.get(0));
        } else {
            System.out.println("警告：查询返回空结果，可能原因：");
            System.out.println("  1. 数据库中没有积分记录（status=1且transaction_type='EARN'）");
            System.out.println("  2. 员工表中没有对应的员工ID");
            System.out.println("  3. SQL查询条件有问题");
        }
        
        result.put("ranking", ranking != null ? ranking : new ArrayList<>());
        result.put("period", period != null ? period : "all");
        result.put("total", ranking != null ? ranking.size() : 0);
        result.put("updateTime", new Date());
        
        return result;
    }

    public Map<String, Object> getMyRank(Integer staffId) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> myPoints = welfarePointsRepository.selectPointsSummary(staffId);
        Integer myBalance = myPoints != null ? 
            ((Number) myPoints.getOrDefault("current_available", 0)).intValue() : 0;
        
        List<Map<String, Object>> allRanking = welfarePointsRepository.selectPointsRanking(null, null, 1000);
        
        int myRank = 0;
        for (int i = 0; i < allRanking.size(); i++) {
            if (((Number) allRanking.get(i).get("staff_id")).intValue() == staffId) {
                myRank = i + 1;
                break;
            }
        }
        
        result.put("rank", myRank);
        result.put("totalStaff", allRanking.size());
        result.put("currentBalance", myBalance);
        result.put("beatPercentage", myRank > 0 ? Math.round((1 - (double)myRank / allRanking.size()) * 100) : 0);
        
        return result;
    }

    private String getStartDateByPeriod(String period) {
        if (period == null) return null;
        
        Calendar cal = Calendar.getInstance();
        switch (period) {
            case "month":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "quarter":
                int month = cal.get(Calendar.MONTH);
                int quarterStartMonth = (month / 3) * 3;
                cal.set(Calendar.MONTH, quarterStartMonth);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "year":
                cal.set(Calendar.DAY_OF_YEAR, 1);
                break;
            default:
                return null;
        }
        
        return String.format("%tF", cal);
    }
}

