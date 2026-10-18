package com.ch.personmis.service;

import com.ch.personmis.entity.EmpImage;

import java.util.Map;

/**
 * 员工画像业务服务接口
 *
 * 功能说明：
 * 定义画像数据的增删改查与分页/条件查询能力。
 */
public interface EmpImageService {
    String addEmpImage(EmpImage empImage);
    Map<String, Object> selectEmpImageByPage(EmpImage empImage);
    Map<String, Object> selectEmpImageByCon(EmpImage empImage);
    EmpImage getEmpImageDetail(int id);
    Map<String, Object> getEmpImageByStaffId(int staff_id);
    String updateEmpImage(EmpImage empImage);
    String deleteEmpImage(int id);
}

