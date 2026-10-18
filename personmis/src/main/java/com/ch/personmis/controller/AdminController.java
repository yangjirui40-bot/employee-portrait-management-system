package com.ch.personmis.controller;

import com.ch.personmis.entity.UserEntity;
import com.ch.personmis.service.AdminService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@RestController
public class AdminController {
    @Resource
    private AdminService adminService;
    @PostMapping(value = "/login")
    public String login(@RequestBody UserEntity userEntity, HttpSession session) {
        return adminService.login(userEntity, session);
    }
}
