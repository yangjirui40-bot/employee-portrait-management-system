package com.ch.personmis.repository;

import com.ch.personmis.entity.PointsRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointsRuleRepository {
    List<PointsRule> selectActiveRules();
    List<PointsRule> selectRulesByType(@Param("ruleType") String ruleType);
    PointsRule selectRuleById(@Param("id") Integer id);
}

