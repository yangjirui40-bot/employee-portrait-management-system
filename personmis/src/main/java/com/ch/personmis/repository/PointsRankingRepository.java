package com.ch.personmis.repository;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface PointsRankingRepository {
    List<Map<String, Object>> selectPointsRanking(@Param("startDate") String startDate, 
                                                  @Param("departId") Integer departId, 
                                                  @Param("limit") Integer limit);
    
    Map<String, Object> selectMyRank(@Param("staffId") Integer staffId);
}