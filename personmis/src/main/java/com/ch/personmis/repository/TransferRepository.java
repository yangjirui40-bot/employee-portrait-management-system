package com.ch.personmis.repository;

import com.ch.personmis.entity.Transfer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TransferRepository {
     Transfer getBeforePost(@Param("id") int id);
     int updateStaff(Transfer transfer);
     int addTransferStaff(Transfer transfer);
     List<Transfer>  selectTransfer(Transfer transfer);
     List<Transfer> selectTransfersByCon(Transfer transfer);
}
