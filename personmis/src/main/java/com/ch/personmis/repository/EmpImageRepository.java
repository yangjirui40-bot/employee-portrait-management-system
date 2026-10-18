package com.ch.personmis.repository;

import com.ch.personmis.entity.EmpImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmpImageRepository {
    // 添加画像数据
    int addEmpImage(EmpImage empImage);
    
    // 更新画像数据
    int updateEmpImage(EmpImage empImage);
    
    // 根据ID查询画像数据
    EmpImage selectEmpImageById(int id);
    
    // 根据员工ID查询画像列表
    List<EmpImage> selectEmpImageByStaffId(@Param("staff_id") int staff_id);

    // 查询员工指定类型的最新画像（limit条）
    List<EmpImage> selectEmpImageByStaffIdAndTypeLimit(@Param("staff_id") int staffId,
                                                       @Param("image_type") String imageType,
                                                       @Param("limit") int limit);
    
    // 分页查询画像数据
    List<EmpImage> selectEmpImageByPage(EmpImage empImage);
    
    // 条件查询画像数据
    List<EmpImage> selectEmpImageByCon(EmpImage empImage);
    
    // 删除画像数据（逻辑删除）
    int deleteEmpImage(int id);
    
    // 统计画像数量
    int countEmpImage(EmpImage empImage);
}

