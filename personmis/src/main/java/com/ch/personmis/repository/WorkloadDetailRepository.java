package com.ch.personmis.repository;

import com.ch.personmis.entity.WorkloadDetail;
import com.ch.personmis.entity.WorkloadDetailByCon;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WorkloadDetailRepository {
    int addWorkloadDetail(WorkloadDetail workloadDetail);
    List<WorkloadDetail> selectWorkloadDetailByCon(WorkloadDetailByCon workloadDetailByCon);
    int deleteWorkloadDetail(int id);
    Map<String, Object> selectWorkloadStatsByStaffId(@Param("staffId") Integer staffId);
    Map<String, Object> selectStatistics(@Param("staffId") Integer staffId, @Param("projectId") Integer projectId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Map<String, Object>> selectHeatmapData(@Param("staffId") Integer staffId, @Param("projectId") Integer projectId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Map<String, Object>> selectMonthlyData(@Param("staffId") Integer staffId, @Param("projectId") Integer projectId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    List<Map<String, Object>> selectGanttData(@Param("projectId") Integer projectId);
    List<Map<String, Object>> selectProjectPerformance(@Param("projectId") Integer projectId);
    
    // 批量处理相关方法
    List<WorkloadDetail> selectAllWorkloadDetails();
    List<Integer> selectStaffIdsWithoutImages();
}

