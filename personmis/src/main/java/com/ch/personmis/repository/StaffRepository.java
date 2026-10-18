package com.ch.personmis.repository;

import com.ch.personmis.entity.PeriodByCon;
import com.ch.personmis.entity.Staff;
import com.ch.personmis.entity.StaffByCon;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import com.ch.personmis.entity.Transfer;

public interface StaffRepository {
     int addStaff(Staff staff);
     List<Staff> selectStaff(StaffByCon staffByCon);
     List<Staff>  selectStaffByCon(StaffByCon staffByCon);
     Staff selectAStaff(int id);
     int updateStaff(Staff staff);
     List<Staff> selectPeriodStaff(PeriodByCon periodByCon);
     List<Staff> selectPeriodStaffCon(PeriodByCon periodByCon);
     int periodOp(@Param("id") int id, @Param("status") String status);
     List<Map<String, Object>>  selectAssociateStaff(int id);
     // Transfer related
     Transfer getBeforePost(@Param("id") int id);
     int updateStaffPost(Transfer transfer);
     int addTransferStaff(Transfer transfer);
     List<Transfer> selectTransfer(Transfer transfer);
     List<Transfer> selectTransfersByCon(Transfer transfer);
     int deleteStaff(int id);
     
     int setEndDate(@Param("id") int id, @Param("enddate") String enddate);
}
