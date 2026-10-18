package com.ch.personmis.repository;

import com.ch.personmis.entity.TaskProject;
import com.ch.personmis.entity.TaskProjectByCon;

import java.util.List;
import java.util.Map;

public interface TaskProjectRepository {
    List<TaskProject> selectTaskProject(TaskProjectByCon taskProjectByCon);
    int addTaskProject(TaskProject taskProject);
    List<TaskProject> selectTaskProjectByCon(TaskProjectByCon taskProjectByCon);
    TaskProject selectATaskProject(int id);
    int updateTaskProject(TaskProject taskProject);
    List<Map<String, Object>> selectAssociateTaskProject(int id);
    int deleteTaskProject(int id);
}

