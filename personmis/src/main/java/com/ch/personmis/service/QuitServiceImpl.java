package com.ch.personmis.service;

import com.ch.personmis.entity.Quit;
import com.ch.personmis.repository.QuitRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 离职管理服务实现
 *
 * 功能说明：
 * - 录入离职信息
 */
@Service
public class QuitServiceImpl implements  QuitService {
    @Resource
    private QuitRepository quitRepository; // 离职数据访问层
    @Override
    public String addQuit(Quit quit) {
        if(quitRepository.addQuit(quit) > 0)
            return "ok";
        return "no";
    }

    @Override
    public Map<String, Object> getQuit(Quit quit) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：分页查询离职列表
        quit.setAct("byPage");
        List<Quit> quits = quitRepository.selectQuitStaff(quit);
        map.put("quits", quits);
        // act='byNoPage'：total 统计（不加 limit）
        quit.setAct("byNoPage");
        map.put("total", quitRepository.selectQuitStaff(quit).size());
        return map;
    }

    @Override
    public Map<String, Object> selectQuitsByCon(Quit quit) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：按条件分页查询
        quit.setAct("byPage");
        List<Quit> quits = quitRepository.selectQuitsByCon(quit);
        map.put("quits", quits);
        // act='byNoPage'：total 统计（不加 limit）
        quit.setAct("byNoPage");
        map.put("total", quitRepository.selectQuitsByCon(quit).size());
        return map;
    }
}
