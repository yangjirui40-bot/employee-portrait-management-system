package com.ch.personmis.service;

import com.ch.personmis.entity.Quit;

import java.util.Map;

/**
 * 离职管理服务接口
 *
 * 功能说明：
 * 定义离职信息录入与离职记录查询能力（分页/条件查询）。
 */
public interface QuitService {
    String addQuit(Quit quit);
    Map<String, Object> getQuit(Quit quit);
    Map<String, Object> selectQuitsByCon(Quit quit);
}
