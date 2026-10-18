package com.ch.personmis.service;

import com.ch.personmis.entity.TaskProject;
import com.ch.personmis.entity.TaskProjectByCon;
import com.ch.personmis.repository.TaskProjectRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目/任务（TaskProject）服务
 *
 * 功能说明：
 * 提供项目的 CRUD、分页/条件查询能力，供工作量录入、项目管理页面使用。
 */
@Service
public class TaskProjectService {
    @Resource
    private TaskProjectRepository taskProjectRepository; // 项目数据访问层
    
    public String addTaskProject(TaskProject taskProject) {
        if(taskProjectRepository.addTaskProject(taskProject) > 0)
            return "ok";
        return "no";
    }

    public Map<String, Object> selectTaskProjectByPage(TaskProjectByCon taskProjectByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：分页查询
        taskProjectByCon.setAct("byPage");
        List<TaskProject> taskProjects = taskProjectRepository.selectTaskProject(taskProjectByCon);
        map.put("taskProjects", taskProjects);
        // act='byNoPage'：total 统计（不加 limit）
        taskProjectByCon.setAct("byNoPage");
        map.put("total", taskProjectRepository.selectTaskProject(taskProjectByCon).size());
        return map;
    }

    public Map<String, Object> selectTaskProjectByCon(TaskProjectByCon taskProjectByCon) {
        Map<String, Object> map = new HashMap<String, Object>();
        // act='byPage'：按条件分页查询
        taskProjectByCon.setAct("byPage");
        List<TaskProject> taskProjects = taskProjectRepository.selectTaskProjectByCon(taskProjectByCon);
        map.put("taskProjects", taskProjects);
        // act='byNoPage'：total 统计（不加 limit）
        taskProjectByCon.setAct("byNoPage");
        map.put("total", taskProjectRepository.selectTaskProjectByCon(taskProjectByCon).size());
        return map;
    }

    public TaskProject getTaskProjectDetail(int id) {
        return taskProjectRepository.selectATaskProject(id);
    }

    public String updateTaskProject(TaskProject taskProject) {
        if(taskProjectRepository.updateTaskProject(taskProject) > 0)
            return "ok";
        return "no";
    }

    public List<TaskProject> getTaskProject() {
        return taskProjectRepository.selectTaskProject(null);
    }

    public String deleteTaskProject(int id) {
        // 删除前检查是否有关联数据（有关联则不允许删除，避免影响工作量/统计）
        List<Map<String, Object>>  listMap = taskProjectRepository.selectAssociateTaskProject(id);
        if (listMap.size() <= 0) {
            if (taskProjectRepository.deleteTaskProject(id) > 0)
                return "ok";
        }
        return "no";
    }
}

