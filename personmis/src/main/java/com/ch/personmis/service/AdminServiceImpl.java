package com.ch.personmis.service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import com.ch.personmis.entity.UserEntity;
import com.ch.personmis.repository.AdminRepository;
import org.springframework.stereotype.Service;

/**
 * 管理员登录服务实现
 */
@Service
public class AdminServiceImpl implements AdminService{
	@Resource
	private  AdminRepository adminRepository; // 管理员/用户认证数据访问层
	@Override
	public String login(UserEntity userEntity, HttpSession session) {
		// 通过仓储层查询匹配的用户（存在即登录成功）
		UserEntity user = adminRepository.login(userEntity);
		if(user != null) {
			// 将登录态写入 session，前端/后端可通过该 key 获取当前用户
			session.setAttribute("auser", user);
			return "ok";
		}else {
			return "no";
		}
	}
}
