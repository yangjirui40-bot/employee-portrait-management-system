package com.ch.personmis.service;

import com.ch.personmis.entity.EmpImage;
import com.ch.personmis.entity.Staff;
import com.ch.personmis.repository.EmpImageRepository;
import com.ch.personmis.repository.StaffRepository;
import com.ch.personmis.repository.WelfarePointsRepository;
import com.ch.personmis.repository.WorkloadDetailRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工综合画像/概览服务
 *
 * 功能说明：
 * 聚合“员工基础信息 + 工作量统计 + 积分汇总 + 最新画像摘要”，为个人首页/概览页提供一次性数据。
 *
 * 数据来源：
 * - StaffRepository：员工基本信息
 * - WorkloadDetailRepository：工作量统计（含 ECI 等）
 * - WelfarePointsRepository：积分汇总（earned/used/balance）
 * - EmpImageRepository：最新能力/绩效/潜力画像（各取 1 条）
 */
@Service
public class ProfileServiceImpl implements ProfileService {

    @Resource
    private StaffRepository staffRepository;
    @Resource
    private WorkloadDetailRepository workloadDetailRepository;
    @Resource
    private WelfarePointsRepository welfarePointsRepository;
    @Resource
    private EmpImageRepository empImageRepository;

    @Override
    public Map<String, Object> getOverallProfile(Integer staffId) {
        Map<String, Object> result = new HashMap<>();

        // 基本信息
        Staff staff = staffRepository.selectAStaff(staffId);
        result.put("staff", staff);

        // 年龄与入职天数
        Map<String, Object> basicStats = new HashMap<>();
        LocalDate today = LocalDate.now();
        if (staff != null) {
            if (staff.getBirthday() != null) {
                LocalDate birth = staff.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                basicStats.put("age", Period.between(birth, today).getYears());
            }
            if (staff.getEntrydate() != null) {
                LocalDate entry = staff.getEntrydate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                // tenure_days：近似换算（年按 365 天、月按 30 天）用于页面展示
                basicStats.put("tenure_days", Period.between(entry, today).getDays() + Period.between(entry, today).getMonths() * 30 + Period.between(entry, today).getYears() * 365);
            }
        }
        result.put("basic_stats", basicStats);

        // 工作量统计
        Map<String, Object> workloadStats = workloadDetailRepository.selectWorkloadStatsByStaffId(staffId);
        result.put("workload_stats", workloadStats);

        // 积分汇总
        Map<String, Object> pointsSummary = welfarePointsRepository.selectPointsSummary(staffId);
        result.put("points_summary", pointsSummary);

        // 画像摘要：按创建时间降序取最新的能力/绩效/潜力各一条（用于概览页快速展示）
        Map<String, Object> imageSummary = new HashMap<>();
        List<EmpImage> abilityImages = empImageRepository.selectEmpImageByStaffIdAndTypeLimit(staffId, "能力画像", 1);
        List<EmpImage> performanceImages = empImageRepository.selectEmpImageByStaffIdAndTypeLimit(staffId, "绩效画像", 1);
        List<EmpImage> potentialImages = empImageRepository.selectEmpImageByStaffIdAndTypeLimit(staffId, "潜力画像", 1);
        if (!abilityImages.isEmpty()) imageSummary.put("ability", abilityImages.get(0));
        if (!performanceImages.isEmpty()) imageSummary.put("performance", performanceImages.get(0));
        if (!potentialImages.isEmpty()) imageSummary.put("potential", potentialImages.get(0));
        result.put("image_summary", imageSummary);

        return result;
    }
}

