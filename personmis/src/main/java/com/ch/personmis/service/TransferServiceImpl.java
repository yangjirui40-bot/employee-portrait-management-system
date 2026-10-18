package com.ch.personmis.service;

import com.ch.personmis.entity.Transfer;
import com.ch.personmis.repository.StaffRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 岗位调动服务实现
 *
 * 功能说明：
 * - 查询员工调动前岗位信息（用于新增调动记录时的“调动前岗位”自动回填）
 * - 新增调动记录，并同步更新员工当前岗位
 */
@Service
public class TransferServiceImpl implements  TransferService{
    @Resource
    private StaffRepository staffRepository; // 复用 StaffRepository 中的调动相关 SQL
    @Override
    public Transfer getBeforePost(int id) {
        System.out.println("=== 查询调动前岗位 ===");
        System.out.println("员工ID: " + id);
        Transfer transfer = staffRepository.getBeforePost(id);
        if (transfer != null) {
            System.out.println("查询成功:");
            System.out.println("  员工姓名: " + transfer.getSname());
            System.out.println("  调动前岗位ID: " + transfer.getBeforepost_id());
            System.out.println("  调动前岗位名称: " + transfer.getBeforepost_name());
        } else {
            System.out.println("查询结果为null，员工ID可能不存在");
        }
        return transfer;
    }

    @Override
    public String addTransfer(Transfer transfer) {
        // 关键：新增调动记录 + 更新员工岗位，需要同时成功才返回 ok
        if (staffRepository.addTransferStaff(transfer) > 0
            && staffRepository.updateStaffPost(transfer) > 0)
            return "ok";
        return "no";
    }

    @Override
    public Map<String, Object> getTransfer(Transfer transfer) {
        System.out.println("=== 查询调动员工列表 ===");
        System.out.println("接收到的参数 - currentPage: " + transfer.getCurrentPage());
        System.out.println("接收到的参数 - pageSize: " + transfer.getPageSize());
        
        Map<String, Object> map = new HashMap<String, Object>();
        
        // 分页参数兜底：避免前端传 0 导致 startIndex 计算错误
        if (transfer.getCurrentPage() <= 0) {
            transfer.setCurrentPage(1);
        }
        if (transfer.getPageSize() <= 0) {
            transfer.setPageSize(10);
        }
        transfer.setStartIndex((transfer.getCurrentPage() - 1) * transfer.getPageSize());
        
        System.out.println("设置后的参数 - currentPage: " + transfer.getCurrentPage());
        System.out.println("设置后的参数 - pageSize: " + transfer.getPageSize());
        System.out.println("计算后的 startIndex: " + transfer.getStartIndex());
        
        transfer.setAct("byPage");
        List<Transfer> transfers = staffRepository.selectTransfer(transfer);
        System.out.println("查询到的记录数: " + transfers.size());
        
        // 如果查询结果为空，检查是否是数据问题
        if (transfers.isEmpty()) {
            System.out.println("警告：查询结果为空，可能是数据库中没有调动记录");
        } else {
            // 打印第一条记录的详细信息，用于调试
            Transfer firstTransfer = transfers.get(0);
            System.out.println("第一条记录详情:");
            System.out.println("  ID: " + firstTransfer.getId());
            System.out.println("  员工ID: " + firstTransfer.getStaff_id());
            System.out.println("  员工姓名: " + firstTransfer.getSname());
            System.out.println("  调动前岗位: " + firstTransfer.getBeforepost_name());
            System.out.println("  调动后岗位: " + firstTransfer.getAfterpost_name());
        }
        
        map.put("transfers", transfers);
        
        // act='byNoPage'：用于 total 统计（不加 limit）
        transfer.setAct("byNoPage");
        int total = staffRepository.selectTransfer(transfer).size();
        System.out.println("总记录数: " + total);
        map.put("total", total);
        return map;
    }

    @Override
    public Map<String, Object> selectTransfersByCon(Transfer transfer) {
        System.out.println("=== 条件查询调动员工 ===");
        System.out.println("查询条件 - staff_id: " + transfer.getStaff_id());
        System.out.println("查询条件 - sname: " + transfer.getSname());
        System.out.println("查询条件 - currentPage: " + transfer.getCurrentPage());
        System.out.println("查询条件 - pageSize: " + transfer.getPageSize());
        
        Map<String, Object> map = new HashMap<String, Object>();
        
        // 分页参数兜底：避免前端传 0 导致 startIndex 计算错误
        if (transfer.getCurrentPage() <= 0) {
            transfer.setCurrentPage(1);
        }
        if (transfer.getPageSize() <= 0) {
            transfer.setPageSize(10);
        }
        transfer.setStartIndex((transfer.getCurrentPage() - 1) * transfer.getPageSize());
        
        System.out.println("设置后的参数 - currentPage: " + transfer.getCurrentPage());
        System.out.println("设置后的参数 - pageSize: " + transfer.getPageSize());
        System.out.println("计算后的 startIndex: " + transfer.getStartIndex());
        
        transfer.setAct("byPage");
        List<Transfer> transfers = staffRepository.selectTransfersByCon(transfer);
        System.out.println("查询到的记录数: " + transfers.size());
        
        map.put("transfers", transfers);
        
        // act='byNoPage'：用于 total 统计（不加 limit）
        transfer.setAct("byNoPage");
        int total = staffRepository.selectTransfersByCon(transfer).size();
        System.out.println("总记录数: " + total);
        map.put("total", total);
        return map;
    }
}
