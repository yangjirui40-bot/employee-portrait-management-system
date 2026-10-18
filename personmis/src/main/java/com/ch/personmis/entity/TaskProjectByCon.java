package com.ch.personmis.entity;

/**
 * 项目查询条件实体类
 * 用于封装项目管理的查询条件（项目ID、项目名称等）
 * 支持分页查询
 */
public class TaskProjectByCon {
    private Integer projectId;
    private String projectName;
    private int currentPage;
    private int pageSize;
    private int startIndex;
    private String act;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        this.startIndex = (this.currentPage - 1) * this.pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.startIndex = (this.currentPage - 1) * this.pageSize;
    }
}

