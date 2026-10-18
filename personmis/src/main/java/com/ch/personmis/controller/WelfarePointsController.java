package com.ch.personmis.controller;

import com.ch.personmis.entity.WelfarePoints;
import com.ch.personmis.service.WelfarePointsService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@RestController
//@RequestMapping("/api")  // 添加API前缀，与前端路径区分开
//@CrossOrigin(origins = "http://localhost:8080")  // 允许前端跨域
public class WelfarePointsController {
    @Autowired
    private WelfarePointsService welfarePointsService;

    @PostMapping("/addWelfarePoints")
    public String addWelfarePoints(@RequestBody WelfarePoints welfarePoints) {
        return welfarePointsService.addWelfarePoints(welfarePoints);
    }

    @GetMapping("/getWelfarePointsByPage")
    public Map<String, Object> getWelfarePointsByPage(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize) {
        WelfarePoints welfarePoints = new WelfarePoints();
        welfarePoints.setCurrentPage(currentPage);
        welfarePoints.setPageSize(pageSize);
        welfarePoints.setStartIndex((currentPage - 1) * pageSize);
        return welfarePointsService.selectWelfarePointsByPage(welfarePoints);
    }

    @PostMapping("/selectWelfarePointsByCon")
    public Map<String, Object> selectWelfarePointsByCon(@RequestBody WelfarePoints welfarePoints) {
        if (welfarePoints.getCurrentPage() > 0 && welfarePoints.getPageSize() > 0) {
            welfarePoints.setStartIndex((welfarePoints.getCurrentPage() - 1) * welfarePoints.getPageSize());
        }
        return welfarePointsService.selectWelfarePointsByCon(welfarePoints);
    }

    @GetMapping("/getWelfarePointsDetail")
    public WelfarePoints getWelfarePointsDetail(int id) {
        return welfarePointsService.getWelfarePointsDetail(id);
    }

    @GetMapping("/getWelfarePointsByStaffId")
    public Map<String, Object> getWelfarePointsByStaffId(int staff_id) {
        return welfarePointsService.getWelfarePointsByStaffId(staff_id);
    }

    @GetMapping("/getPointsSummary")
    public Map<String, Object> getPointsSummary(int staff_id) {
        return welfarePointsService.getPointsSummary(staff_id);
    }

    @PostMapping("/updateWelfarePoints")
    public String updateWelfarePoints(@RequestBody WelfarePoints welfarePoints) {
        return welfarePointsService.updateWelfarePoints(welfarePoints);
    }

    @PostMapping("/deleteWelfarePoints")
    public String deleteWelfarePoints(@RequestParam int id) {
        return welfarePointsService.deleteWelfarePoints(id);
    }
}