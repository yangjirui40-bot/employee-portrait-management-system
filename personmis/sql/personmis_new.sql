-- 人事管理系统
SET NAMES utf8;
SET CHARACTER SET utf8;
SET character_set_connection=utf8;
SET FOREIGN_KEY_CHECKS=0;
-- 1. 用户登录表
CREATE TABLE `ausertable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `aname` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `apwd` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户登录表';
INSERT INTO `ausertable` VALUES ('1', 'admin', 'admin');


-- 2. 系统部门表

DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dname` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dtype` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dtel` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `dfax` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `supdepartment` int(11) DEFAULT NULL,
  `establishmentdate` date DEFAULT NULL,
  `parent_id` int(11) DEFAULT 0 COMMENT '父部门ID',
  `order_num` int(4) DEFAULT 0 COMMENT '显示排序',
  `leader_id` int(11) DEFAULT NULL COMMENT '部门负责人ID (FK to hr_employee.id)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='系统部门表';


-- 3. 系统岗位表
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pname` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `ptype` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `organization` int(11) NOT NULL DEFAULT '0',
  `post_level` varchar(20) DEFAULT NULL COMMENT '岗位职级 (如: P5, M3)',
  `requirements` text DEFAULT NULL COMMENT '岗位能力模型要求 (JSON格式存储标签)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='系统岗位表';

-- 4. 系统角色表

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称 (如: 管理员, HR, 普通员工)',
  `role_key` varchar(50) NOT NULL COMMENT '角色权限字符',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='角色信息表';
-- 插入默认角色
INSERT INTO `sys_role` VALUES (1, '管理员', 'admin');
INSERT INTO `sys_role` VALUES (2, '部门经理', 'manager');
INSERT INTO `sys_role` VALUES (3, '普通员工', 'employee');
-- 5. 系统用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名/登录名',
  `password` varchar(100) NOT NULL COMMENT '密码 (需加密存储)',
  `emp_id` int(11) DEFAULT NULL COMMENT '关联员工ID (FK)',
  `role_id` int(11) NOT NULL COMMENT '关联角色ID (FK)',
  `status` char(1) DEFAULT '0' COMMENT '用户状态 (0正常 1禁用)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户登录表';
-- 插入默认管理员用户（密码需要加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `role_id`, `status`) 
VALUES (1, 'admin', '$2a$10$Tqj...encrypted_pwd', 1, '0');
-- 6. 员工表
DROP TABLE IF EXISTS `hr_employee`;
CREATE TABLE `hr_employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sname` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `sex` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `birthday` date NOT NULL,
  `sid` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `depart_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `entrydate` date NOT NULL,
  `joinworkdate` date NOT NULL,
  `workform` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `staffsource` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `politicalstatus` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `nation` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `nativeplace` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `stel` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `semail` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `skill_tags` varchar(500) DEFAULT NULL COMMENT '技能标签，逗号分隔',
  `sheight` decimal(12,2) DEFAULT NULL,
  `bloodtype` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `maritalstatus` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `registeredresidence` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `education` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `degree` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `university` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `major` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `graduationdate` date DEFAULT NULL,
  `startdate` date DEFAULT NULL,
  `enddate` date DEFAULT NULL,
  `status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `peroidopdate` date DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL COMMENT '关联用户ID (FK to sys_user.id)',
  PRIMARY KEY (`id`),
  KEY `did` (`depart_id`),
  KEY `pid` (`post_id`),
  CONSTRAINT `fk_emp_dept` FOREIGN KEY (`depart_id`) REFERENCES `sys_department` (`id`),
  CONSTRAINT `fk_emp_post` FOREIGN KEY (`post_id`) REFERENCES `sys_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='员工表';

-- 7. 离职表
DROP TABLE IF EXISTS `quit`;
CREATE TABLE `quit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `staff_id` int(11) DEFAULT NULL,
  `sname` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `qtype` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `qdate` date DEFAULT NULL,
  `opdate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qsid` (`staff_id`),
  CONSTRAINT `qsid` FOREIGN KEY (`staff_id`) REFERENCES `hr_employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='离职表';

-- 8. 调动表
DROP TABLE IF EXISTS `transfer`;
CREATE TABLE `transfer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `staff_id` int(11) DEFAULT NULL,
  `sname` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `beforepost_id` int(11) DEFAULT NULL,
  `afterpost_id` int(11) DEFAULT NULL,
  `ttype` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `tdate` date DEFAULT NULL,
  `opdate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ssid` (`staff_id`),
  KEY `ppid` (`afterpost_id`),
  KEY `bpid` (`beforepost_id`),
  CONSTRAINT `bpid` FOREIGN KEY (`beforepost_id`) REFERENCES `sys_post` (`id`),
  CONSTRAINT `ppid` FOREIGN KEY (`afterpost_id`) REFERENCES `sys_post` (`id`),
  CONSTRAINT `ssid` FOREIGN KEY (`staff_id`) REFERENCES `hr_employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='调动表';

-- 9. 项目/任务清单表
DROP TABLE IF EXISTS `biz_task_project`;
CREATE TABLE `biz_task_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(255) NOT NULL COMMENT '项目名称',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `budgeted_hours` decimal(12,2) DEFAULT NULL COMMENT '预算总工时',
  `required_skills` varchar(500) DEFAULT NULL COMMENT '项目所需技能标签，逗号分隔',
  `max_staff_count` int(11) DEFAULT NULL COMMENT '项目最大人员数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目/任务清单表';


-- 10. 工作量填报明细表
DROP TABLE IF EXISTS `biz_workload_detail`;
CREATE TABLE `biz_workload_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `project_id` int(11) DEFAULT NULL COMMENT '关联项目ID (FK)',
  `task_desc` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `time_spent_hrs` decimal(5,2) NOT NULL COMMENT '实际花费工时',
  `task_weight` decimal(3,2) NOT NULL DEFAULT 1.0 COMMENT '任务难度权重 (0.5 - 2.0)',
  `quality_score` decimal(3,2) NOT NULL DEFAULT 1.0 COMMENT '完成质量自评分 (0.0 - 1.0)',
  `work_date` date NOT NULL COMMENT '工作日期',
  `start_date` date NOT NULL COMMENT '任务开始日期',
  `end_date` date NOT NULL COMMENT '任务结束日期',
  `staff_id` int(11) NOT NULL COMMENT '员工ID (FK)',
  `created_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_staff_id` (`staff_id`),
  KEY `idx_work_date` (`work_date`),
  KEY `idx_start_date` (`start_date`),
  KEY `idx_end_date` (`end_date`),
  FOREIGN KEY (`staff_id`) REFERENCES `hr_employee` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='工作量填报明细表';


-- 11. 项目人员分配结果表
DROP TABLE IF EXISTS `biz_project_staff_allocation`;
CREATE TABLE `biz_project_staff_allocation` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分配ID',
  `project_id` int(11) NOT NULL COMMENT '项目ID',
  `staff_id` int(11) NOT NULL COMMENT '员工ID',
  `allocated_hours` decimal(10,2) DEFAULT NULL COMMENT '分配工时',
  `allocation_score` decimal(5,2) DEFAULT NULL COMMENT '分配匹配度评分',
  `skill_match` decimal(5,2) DEFAULT NULL COMMENT '技能匹配度',
  `quality_score` decimal(5,2) DEFAULT NULL COMMENT '员工质量评分',
  `ability_score` decimal(5,2) DEFAULT NULL COMMENT '员工综合能力评分',
  `created_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_staff_id` (`staff_id`),
  FOREIGN KEY (`project_id`) REFERENCES `biz_task_project` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`staff_id`) REFERENCES `hr_employee` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目人员分配结果表';


-- 12. 员工画像数据表
DROP TABLE IF EXISTS `hr_emp_image`;
CREATE TABLE `hr_emp_image` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `staff_id` INT NOT NULL COMMENT '员工ID，关联hr_employee表',
  `image_type` VARCHAR(50) NOT NULL COMMENT '画像类型：能力画像/绩效画像/潜力画像',
  `ability_score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '综合能力评分(0-100)，基于多维度能力加权计算',
  `tech_ability` DECIMAL(5,2) DEFAULT 0.00 COMMENT '技术能力(0-100)，基于任务难度权重计算',
  `workload_ability` DECIMAL(5,2) DEFAULT 0.00 COMMENT '工作量能力(0-100)，基于工时和任务完成量计算',
  `quality_ability` DECIMAL(5,2) DEFAULT 0.00 COMMENT '质量能力(0-100)，基于平均质量评分计算',
  `contribution_ability` DECIMAL(5,2) DEFAULT 0.00 COMMENT '综合贡献能力(0-100)，基于ECI贡献度计算',
  `stability_ability` DECIMAL(5,2) DEFAULT 0.00 COMMENT '稳定性能力(0-100)，基于工作质量波动性计算',
  `performance_trend` DECIMAL(5,2) DEFAULT NULL COMMENT '绩效趋势(-100到100)',
  `learning_ability` DECIMAL(5,2) DEFAULT NULL COMMENT '学习能力(0-100)',
  `growth_speed` DECIMAL(5,2) DEFAULT NULL COMMENT '成长速度(0-100)',
  `incentive_response` DECIMAL(5,2) DEFAULT NULL COMMENT '激励响应(0-100)',
  `promotion_probability` DECIMAL(5,2) DEFAULT NULL COMMENT '晋升概率(0-1)',
  `retention_risk` VARCHAR(20) DEFAULT NULL COMMENT '留存风险(低/中/高)',
  `performance_level` VARCHAR(20) DEFAULT NULL COMMENT '绩效等级：优秀/良好/一般/待改进',
  `skill_tags` VARCHAR(500) DEFAULT NULL COMMENT '技能标签，逗号分隔',
  `work_style` VARCHAR(200) DEFAULT NULL COMMENT '工作风格描述',
  `career_orientation` VARCHAR(200) DEFAULT NULL COMMENT '职业倾向',
  `risk_level` VARCHAR(20) DEFAULT NULL COMMENT '风险等级：低/中/高',
  `model_version` VARCHAR(50) DEFAULT NULL COMMENT '画像生成模型版本号',
  `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` TIMESTAMP NULL COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  INDEX `idx_staff_id` (`staff_id`),
  INDEX `idx_image_type` (`image_type`),
  INDEX `idx_created_time` (`created_time`),
  UNIQUE KEY `uk_staff_image_type` (`staff_id`, `image_type`),
  FOREIGN KEY (`staff_id`) REFERENCES `hr_employee` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='员工画像数据表';


-- 13. 福利积分表
DROP TABLE IF EXISTS `hr_welfare_points`;
CREATE TABLE `hr_welfare_points` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `staff_id` INT NOT NULL COMMENT '员工ID，关联hr_employee表',
  `points_type` VARCHAR(50) NOT NULL COMMENT '积分类型：年度积分、月度积分、奖励积分、消费积分等',
  `points_value` INT NOT NULL DEFAULT 0 COMMENT '积分值',
  `points_source` VARCHAR(100) DEFAULT NULL COMMENT '积分来源：绩效奖励、项目奖励、考勤奖励、福利兑换等',
  `source_id` INT DEFAULT NULL COMMENT '来源记录ID（如奖励记录ID）',
  `source_desc` VARCHAR(500) DEFAULT NULL COMMENT '来源描述',
  `expire_date` DATE DEFAULT NULL COMMENT '过期日期，NULL表示永不过期',
  `used_points` INT DEFAULT 0 COMMENT '已使用积分',
  `available_points` INT DEFAULT 0 COMMENT '可用积分',
  `transaction_type` VARCHAR(20) NOT NULL COMMENT '交易类型：EARN-获得，USE-使用，EXPIRE-过期，ADJUST-调整',
  `transaction_time` TIMESTAMP NULL COMMENT '交易时间',
  `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-已删除',
  INDEX `idx_staff_id` (`staff_id`),
  INDEX `idx_points_type` (`points_type`),
  INDEX `idx_transaction_time` (`transaction_time`),
  INDEX `idx_expire_date` (`expire_date`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`staff_id`) REFERENCES `hr_employee` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='福利积分表';

-- 14. 员工积分汇总视图
CREATE OR REPLACE VIEW `v_staff_points_summary` AS
SELECT 
    `staff_id`,
    SUM(CASE WHEN `transaction_type` = 'EARN' AND `status` = 1 THEN `points_value` ELSE 0 END) AS `total_earned`,
    SUM(CASE WHEN `transaction_type` = 'USE' AND `status` = 1 THEN `used_points` ELSE 0 END) AS `total_used`,
    SUM(CASE WHEN `transaction_type` = 'EXPIRE' AND `status` = 1 THEN `points_value` ELSE 0 END) AS `total_expired`,
    SUM(CASE WHEN `transaction_type` IN ('EARN', 'ADJUST') AND `status` = 1 
             AND (`expire_date` IS NULL OR `expire_date` >= CURDATE()) 
        THEN `available_points` ELSE 0 END) AS `current_available`
FROM `hr_welfare_points`
GROUP BY `staff_id`;
-- 重新开启外键检查
SET FOREIGN_KEY_CHECKS=1;
SET NAMES utf8;

-- ===================================================================
-- 创建积分规则表
-- ===================================================================

CREATE TABLE `hr_points_rule` (
                                  `id` INT AUTO_INCREMENT PRIMARY KEY,
                                  `rule_name` VARCHAR(100) NOT NULL,
                                  `rule_type` VARCHAR(50) NOT NULL,
                                  `trigger_condition` TEXT,
                                  `points_value` INT NOT NULL,
                                  `description` VARCHAR(500),
                                  `status` TINYINT DEFAULT 1
);


ALTER TABLE `hr_welfare_points` ADD COLUMN `rule_id` INT DEFAULT NULL;
ALTER TABLE `hr_welfare_points` ADD COLUMN `auto_generated` TINYINT DEFAULT 0;

-- ===================================================================
-- 插入默认积分规则
-- ===================================================================

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('高质量工作奖励', 'QUALITY_HIGH', '{"quality_score": {"gte": 0.95}}', 10, '质量评分≥0.95时奖励10积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('优秀质量奖励', 'QUALITY_HIGH', '{"quality_score": {"eq": 1.0}}', 20, '质量评分=1.0时额外奖励20积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('高难度任务奖励', 'DIFFICULTY_HIGH', '{"task_weight": {"gte": 1.5}}', 15, '任务难度权重≥1.5时奖励15积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('超高难度任务奖励', 'DIFFICULTY_HIGH', '{"task_weight": {"gte": 1.8}}', 30, '任务难度权重≥1.8时额外奖励30积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('长工时投入奖励', 'HOURS_LONG', '{"time_spent_hrs": {"gte": 20}}', 8, '单次工时≥20小时奖励8积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('超长工时投入奖励', 'HOURS_LONG', '{"time_spent_hrs": {"gte": 30}}', 15, '单次工时≥30小时额外奖励15积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('连续工作奖励', 'CONTINUOUS_WORK', '{"continuous_days": {"gte": 7}}', 25, '连续工作7天奖励25积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('月度全勤奖励', 'CONTINUOUS_WORK', '{"continuous_days": {"gte": 30}}', 50, '月度全勤（连续30天）奖励50积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('高质量高难度组合', 'QUALITY_DIFFICULTY_COMBO', '{"quality_score": {"gte": 0.9}, "task_weight": {"gte": 1.5}}', 35, '高质量+高难度组合奖励35积分', 1);

INSERT INTO `hr_points_rule` (`rule_name`, `rule_type`, `trigger_condition`, `points_value`, `description`, `status`) VALUES
    ('高效完成奖励', 'EFFICIENCY_HIGH', '{"eci_single": {"gte": 25}}', 20, '单次ECI贡献≥25奖励20积分', 1);
