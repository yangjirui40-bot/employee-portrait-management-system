"""
增强人员分配算法
整合8维能力评价和智能激励建议
"""

import gurobipy as gp
from gurobipy import GRB

def calculate_skill_match_score(employee_skills, project_skills):
    """计算技能匹配度"""
    if not project_skills:
        return 1.0
    
    emp_set = set([s.strip().lower() for s in employee_skills.split(',') if s.strip()])
    proj_set = set([s.strip().lower() for s in project_skills.split(',') if s.strip()])
    
    if not proj_set:
        return 1.0
    
    intersection = emp_set & proj_set
    return len(intersection) / len(proj_set)


def calculate_comprehensive_ability(emp_data):
    """计算综合能力（8维）"""
    tech = emp_data.get('tech_ability', 70) / 100
    workload = emp_data.get('workload_ability', 70) / 100
    quality = emp_data.get('quality_ability', 70) / 100
    contribution = emp_data.get('contribution_ability', 70) / 100
    stability = emp_data.get('stability_ability', 70) / 100
    incentive = emp_data.get('incentive_response', 50) / 100
    cost_eff = emp_data.get('cost_effectiveness', 50) / 100
    team_collab = emp_data.get('team_collaboration', 50) / 100
    
    comprehensive = (
        tech * 0.20 +
        workload * 0.15 +
        quality * 0.15 +
        contribution * 0.15 +
        stability * 0.10 +
        team_collab * 0.15 +
        cost_eff * 0.10
    )
    
    return comprehensive, {
        'tech': tech,
        'workload': workload,
        'quality': quality,
        'contribution': contribution,
        'stability': stability,
        'incentive': incentive,
        'cost_effectiveness': cost_eff,
        'team_collaboration': team_collab
    }


def generate_incentive_plan(allocations, employees_data):
    """
    生成激励方案建议
    
    规则：
    - 基础积分：工时 × 3分/小时
    - 质量奖励：质量评分≥90%时，额外30%
    - 协作奖励：团队协作≥80%时，额外20%
    - 创新奖励：技术能力≥80%时，额外15%
    """
    suggestions = []
    
    # 构建员工索引
    emp_dict = {emp.get('staff_id'): emp for emp in employees_data}
    
    for alloc in allocations:
        staff_id = alloc['staff_id']
        emp = emp_dict.get(staff_id, {})
        
        allocated_hours = alloc['allocated_hours']
        quality = emp.get('quality_ability', 70) / 100
        team_collab = emp.get('team_collaboration', 50) / 100
        tech = emp.get('tech_ability', 70) / 100
        
        # 基础积分
        base_points = allocated_hours * 3
        
        # 质量奖励
        quality_bonus = base_points * 0.3 if quality >= 0.9 else 0
        
        # 协作奖励
        collaboration_bonus = base_points * 0.2 if team_collab >= 0.8 else 0
        
        # 创新奖励
        innovation_bonus = base_points * 0.15 if tech >= 0.8 else 0
        
        total_points = base_points + quality_bonus + collaboration_bonus + innovation_bonus
        
        suggestions.append({
            'staff_id': staff_id,
            'staff_name': emp.get('staff_name', f'员工{staff_id}'),
            'base_points': round(base_points, 0),
            'quality_bonus': round(quality_bonus, 0),
            'collaboration_bonus': round(collaboration_bonus, 0),
            'innovation_bonus': round(innovation_bonus, 0),
            'total_points': round(total_points, 0),
            'breakdown_text': f"基础{int(base_points)}分 + 质量{int(quality_bonus)}分 + 协作{int(collaboration_bonus)}分 + 创新{int(innovation_bonus)}分"
        })
    
    total_budget = sum(s['total_points'] for s in suggestions)
    
    return {
        'suggestions': suggestions,
        'total_budget': total_budget,
        'breakdown': {
            'base_points': sum(s['base_points'] for s in suggestions),
            'quality_bonus': sum(s['quality_bonus'] for s in suggestions),
            'collaboration_bonus': sum(s['collaboration_bonus'] for s in suggestions),
            'innovation_bonus': sum(s['innovation_bonus'] for s in suggestions)
        }
    }


def predict_project_success_rate(allocations, employees_data, project_data):
    """
    预测项目成功率
    
    考虑因素：
    - 技能覆盖率（30%）
    - 团队综合能力（40%）
    - 工时利用率（20%）
    - 团队协作指数（10%）
    """
    if not allocations:
        return 0.0, "无人员分配"
    
    # 1. 技能覆盖率
    required_skills = set([s.strip().lower() for s in project_data.get('required_skills', '').split(',') if s.strip()])
    if required_skills:
        covered_skills = set()
        for alloc in allocations:
            emp = next((e for e in employees_data if e.get('staff_id') == alloc['staff_id']), {})
            emp_skills = set([s.strip().lower() for s in emp.get('skill_tags', '').split(',') if s.strip()])
            covered_skills.update(emp_skills & required_skills)
        skill_coverage = len(covered_skills) / len(required_skills) if required_skills else 1.0
    else:
        skill_coverage = 1.0
    
    # 2. 团队综合能力
    ability_scores = []
    for alloc in allocations:
        emp = next((e for e in employees_data if e.get('staff_id') == alloc['staff_id']), {})
        comp_ability, _ = calculate_comprehensive_ability(emp)
        ability_scores.append(comp_ability)
    
    avg_ability = sum(ability_scores) / len(ability_scores) if ability_scores else 0.5
    
    # 3. 工时利用率
    total_allocated = sum(alloc['allocated_hours'] for alloc in allocations)
    budgeted = project_data.get('budgeted_hours', 160)
    utilization_rate = min(total_allocated / budgeted, 1.0) if budgeted > 0 else 0
    
    # 4. 团队协作指数
    team_collab_scores = []
    for alloc in allocations:
        emp = next((e for e in employees_data if e.get('staff_id') == alloc['staff_id']), {})
        team_collab = emp.get('team_collaboration', 50) / 100
        team_collab_scores.append(team_collab)
    
    avg_team_collab = sum(team_collab_scores) / len(team_collab_scores) if team_collab_scores else 0.5
    
    # 综合成功率
    success_rate = (
        skill_coverage * 0.30 +
        avg_ability * 0.40 +
        utilization_rate * 0.20 +
        avg_team_collab * 0.10
    )
    
    # 评估等级
    if success_rate >= 0.85:
        assessment = "优秀 - 项目成功率极高"
    elif success_rate >= 0.70:
        assessment = "良好 - 项目有较高成功把握"
    elif success_rate >= 0.55:
        assessment = "中等 - 存在一定风险，建议加强"
    else:
        assessment = "偏低 - 风险较大，建议调整方案"
    
    return round(success_rate * 100, 2), assessment


def optimize_allocation_enhanced(project_data, employees_data):
    """
    增强版人员分配优化
    """
    n_employees = len(employees_data)
    if n_employees == 0:
        return {
            'code': 400,
            'message': '没有可用的员工数据',
            'data': None
        }
    
    max_staff = project_data.get('max_staff_count', 10)
    budgeted_hours = project_data.get('budgeted_hours', 160)
    required_skills = project_data.get('required_skills', '')
    
    if budgeted_hours <= 0:
        return {
            'code': 400,
            'message': '项目预算工时必须大于0',
            'data': None
        }
    
    # 创建Gurobi模型
    model = gp.Model("EnhancedStaffAllocation")
    model.setParam('OutputFlag', 0)
    
    # 决策变量
    x = model.addVars(n_employees, vtype=GRB.BINARY, name="assigned")
    y = model.addVars(n_employees, vtype=GRB.CONTINUOUS, lb=0, name="hours")
    
    # 目标函数：最大化总分配得分
    obj_expr = gp.LinExpr()
    
    for i in range(n_employees):
        emp = employees_data[i]
        
        # 技能匹配
        skill_match = calculate_skill_match_score(emp.get('skill_tags', ''), required_skills)
        
        # 综合能力
        comp_ability, abilities = calculate_comprehensive_ability(emp)
        
        # 质量评分
        quality = emp.get('avg_quality_score', 0.8)
        
        # 激励响应度
        incentive_resp = emp.get('incentive_response', 50) / 100
        
        # 总分计算（权重分配）
        total_score = (
            skill_match * 40 +           # 技能匹配40%
            comp_ability * 30 +          # 综合能力30%
            quality * 20 +               # 工作质量20%
            incentive_resp * 10          # 激励响应10%
        )
        
        obj_expr += total_score * x[i]
    
    model.setObjective(obj_expr, GRB.MAXIMIZE)
    
    # 约束条件
    # 1. 最大人员数量
    model.addConstr(gp.quicksum(x[i] for i in range(n_employees)) <= max_staff, "max_staff")
    
    # 2. 总工时预算
    model.addConstr(gp.quicksum(y[i] for i in range(n_employees)) <= budgeted_hours, "budgeted_hours")
    
    # 3. 工时与分配关联
    for i in range(n_employees):
        # 分配了才有工时
        model.addConstr(y[i] <= budgeted_hours * x[i], f"link_{i}")
        
        # 最小工时（如果分配）
        model.addConstr(y[i] >= 8 * x[i], f"min_hours_{i}")
        
        # 个人工作负荷上限
        emp_max_hours = employees_data[i].get('max_workload_hours', 40)
        model.addConstr(y[i] <= emp_max_hours, f"emp_limit_{i}")
    
    # 求解
    model.optimize()
    
    if model.status != GRB.OPTIMAL:
        return {
            'code': 500,
            'message': '优化求解失败',
            'data': None
        }
    
    # 提取结果
    allocations = []
    for i in range(n_employees):
        if x[i].X > 0.5:
            emp = employees_data[i]
            skill_match = calculate_skill_match_score(emp.get('skill_tags', ''), required_skills)
            comp_ability, abilities = calculate_comprehensive_ability(emp)
            quality = emp.get('avg_quality_score', 0.8)
            incentive_resp = emp.get('incentive_response', 50) / 100
            
            total_score = (
                skill_match * 40 +
                comp_ability * 30 +
                quality * 20 +
                incentive_resp * 10
            )
            
            allocations.append({
                'staff_id': emp.get('staff_id'),
                'staff_name': emp.get('staff_name', f'员工{emp.get("staff_id")}'),
                'allocated_hours': round(y[i].X, 2),
                'allocation_score': round(total_score, 2),
                'skill_match': round(skill_match * 100, 2),
                'quality_score': round(quality * 100, 2),
                'comprehensive_ability': round(comp_ability * 100, 2),
                'incentive_response': round(incentive_resp * 100, 2),
                'abilities_detail': {k: round(v * 100, 2) for k, v in abilities.items()}
            })
    
    # 生成激励方案
    incentive_plan = generate_incentive_plan(allocations, employees_data)
    
    # 预测项目成功率
    success_rate, assessment = predict_project_success_rate(allocations, employees_data, project_data)
    
    return {
        'code': 200,
        'message': '人员分配优化成功',
        'data': {
            'project_id': project_data.get('project_id'),
            'allocations': allocations,
            'incentive_plan': incentive_plan,
            'project_prediction': {
                'success_rate': success_rate,
                'assessment': assessment
            },
            'summary': {
                'total_staff': len(allocations),
                'total_hours': sum(a['allocated_hours'] for a in allocations),
                'budgeted_hours': budgeted_hours,
                'utilization_rate': round(sum(a['allocated_hours'] for a in allocations) / budgeted_hours * 100, 2),
                'avg_skill_match': round(sum(a['skill_match'] for a in allocations) / len(allocations), 2) if allocations else 0
            }
        }
    }


