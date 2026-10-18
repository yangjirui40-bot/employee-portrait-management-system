package com.ch.personmis.service;

import jakarta.servlet.http.HttpSession;

import com.ch.personmis.entity.UserEntity;

/**
 * 管理员/系统用户服务接口
 */
public interface AdminService {
	public String login(UserEntity userEntity, HttpSession session);

}
    