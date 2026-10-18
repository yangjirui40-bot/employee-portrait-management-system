package com.ch.personmis.controller;

import com.ch.personmis.service.PointsRankingService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/points")
public class PointsRankingController {
    @Resource
    private PointsRankingService pointsRankingService;

    @GetMapping("/ranking")
    public Map<String, Object> getPointsRanking(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer departId,
            @RequestParam(required = false) Integer limit) {
        return pointsRankingService.getPointsRanking(period, departId, limit);
    }

    @GetMapping("/my-rank")
    public Map<String, Object> getMyRank(@RequestParam Integer staffId) {
        return pointsRankingService.getMyRank(staffId);
    }
}