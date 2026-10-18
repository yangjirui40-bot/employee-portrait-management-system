package com.ch.personmis.service;

import java.util.Map;

/**
 * 员工概览（综合画像）服务接口
 *
 * 功能说明：
 * 聚合多个模块数据，返回“基础信息 + 工作量 + 积分 + 最新画像摘要”。 
 */
public interface ProfileService {
    Map<String, Object> getOverallProfile(Integer staffId);
}

