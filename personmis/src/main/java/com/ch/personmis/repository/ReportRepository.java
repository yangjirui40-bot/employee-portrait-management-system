package com.ch.personmis.repository;

import com.ch.personmis.entity.Report;
import java.util.List;
import java.util.Map;

public interface ReportRepository {
   List<Map<String, Object>> reportSelectNew(Report report);
   List<Map<String, Object>>  reportSelectQuit(Report report);
   List<Map<String, Object>>  reportSelectTransfer(Report report);
}
