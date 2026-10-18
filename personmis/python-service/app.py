"""
Python算法服务 - Flask Web服务入口
功能说明：
1. 员工画像分析（能力画像、绩效画像、潜力画像）
2. 人员分配优化（使用Gurobi优化求解器）
3. 积分计算和优化
服务架构：
独立运行在5000端口
通过HTTP REST API与Java后端通信
"""
from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import json
import math
from datetime import datetime
from dotenv import load_dotenv
import gurobipy as gp
from gurobipy import GRB
# 尝试导入高级分析模块
try:
    from advanced_analysis import (
        calculate_incentive_response,
        calculate_cost_effectiveness,
        calculate_team_collaboration,
        build_skill_growth_map,
        analyze_project_experience
    )
    ADVANCED_ANALYSIS_AVAILABLE = True
except ImportError:
    ADVANCED_ANALYSIS_AVAILABLE = False
    print("Warning: advanced_analysis module not found. Advanced features disabled.")

# 尝试导入增强版分配算法（可选功能）
try:
    from enhanced_allocation import optimize_allocation_enhanced
    ENHANCED_ALLOCATION_AVAILABLE = True
except ImportError:
    ENHANCED_ALLOCATION_AVAILABLE = False
    print("Warning: enhanced_allocation module not found. Using basic allocation.")

# 加载环境变量
load_dotenv()

# 创建Flask应用
app = Flask(__name__)
CORS(app)  # 允许跨域请求（前端可以直接调用）
app.config['JSON_AS_ASCII'] = False  

# 能力计算函数 

def calculate_tech_ability(avg_task_weight):
    """
    计算技术能力得分
    算法说明：
    根据平均任务难度权重计算员工的技术能力，难度越高技术能力越强。
    计算公式：将任务权重从0.5-2.0映射到0-100分。 
    参数:
        avg_task_weight: 平均任务难度权重（范围通常0.5-2.0）    
    返回:
        技术能力得分
    """
    return round(max(0, min(100, (avg_task_weight - 0.5) / 1.5 * 100)), 2)

def calculate_workload_ability(total_hours, total_records, standard_hours=160, standard_records=30):
    """
    计算工作量能力得分
    参数:
        total_hours: 总工时数
        total_records: 总任务记录数
        standard_hours: 标准工时（默认160小时，约1个月）
        standard_records: 标准任务数（默认30条）
    
    返回:
        工作量能力得分（0-100分）
    """
    return round(min(100, min(50, (total_hours / standard_hours) * 50) + min(50, (total_records / standard_records) * 50)), 2)

def calculate_quality_ability(avg_quality_score):
    """
    计算质量能力得分
    参数:
        avg_quality_score: 平均质量评分（0-1，通常来自自评质量星级）
    
    返回:
        质量能力得分（0-100分）
    """
    return round(min(100, max(0, avg_quality_score * 100)), 2)

def calculate_contribution_ability(eci_score, max_eci_benchmark=1000):
    """
    计算贡献能力得分   
    根据ECI（效率贡献指数）计算员工的贡献能力。
    ECI = 工时 × 任务权重 × 质量评分，反映综合贡献水平。
    
    参数:
        eci_score: ECI得分（效率贡献指数）
        max_eci_benchmark: ECI最大值基准（默认1000）
    
    返回:
        贡献能力得分（0-100分）
    """
    return round(min(100, (eci_score / max_eci_benchmark) * 100), 2)

def calculate_stability_ability(quality_scores):
    """
    计算稳定性能力得分    
    通过质量评分的标准差评估工作稳定性，波动越小稳定性越高。
    标准差越大，稳定性得分越低。 
    参数:
        quality_scores: 历史质量评分列表（0-1之间的浮点数）
    
    返回:
        稳定性能力得分（0-100分），数据不足时返回75分
    """
    if not quality_scores or len(quality_scores) < 2:
        return 75.0
    mean = sum(quality_scores) / len(quality_scores)
    std_dev = math.sqrt(sum((x - mean) ** 2 for x in quality_scores) / len(quality_scores))
    return round(max(0, min(100, 100 - (std_dev * 20))), 2)

def calculate_comprehensive_ability(tech, workload, quality, contribution, stability):
    """
    计算综合能力得分
    将五项能力按权重加权平均，得到综合能力得分。
    权重分配：技术20%、工作量20%、质量25%、贡献25%、稳定性10%。   
    参数:
        tech: 技术能力得分
        workload: 工作量能力得分
        quality: 质量能力得分
        contribution: 贡献能力得分
        stability: 稳定性能力得分
    
    返回:
        综合能力得分（0-100分）
    """
    return round(tech * 0.20 + workload * 0.20 + quality * 0.25 + contribution * 0.25 + stability * 0.10, 2)

def calculate_trend(values):
    """
    计算数值序列的趋势
    使用线性回归计算趋势斜率，更准确地反映数据变化趋势。
    如果数据点少于2个，返回0（表示无趋势）。
    参数:
        values: 数值列表（如质量评分序列、任务权重序列）
    
    返回:
        趋势值（正数表示上升，负数表示下降，单位：百分比）
    """
    if not values or len(values) < 2:
        return 0
    
    # 使用线性回归计算趋势斜率
    n = len(values)
    x = list(range(n))  # 时间序列索引 [0, 1, 2, ..., n-1]
    y = values
    
    # 计算均值
    x_mean = sum(x) / n
    y_mean = sum(y) / n
    
    # 计算斜率和截距
    numerator = sum((x[i] - x_mean) * (y[i] - y_mean) for i in range(n))
    denominator = sum((x[i] - x_mean) ** 2 for i in range(n))
    
    if denominator == 0:
        return 0
    
    slope = numerator / denominator
    
    # 将斜率转换为百分比趋势（相对于平均值）
    if y_mean > 0:
        trend_percent = (slope / y_mean) * 100
    else:
        trend_percent = slope * 100
    
    return round(trend_percent, 2)

def calculate_learning_ability(task_weights):
    """
    计算学习能力得分
    通过任务难度权重的上升趋势评估学习能力，难度提升越快学习能力越强。
    基础分50分，根据趋势调整。
    
    参数:
        task_weights: 历史任务难度权重列表
    
    返回:
        学习能力得分（0-100分），数据不足时返回50分
    """
    if not task_weights or len(task_weights) < 2:
        return 50.0
    trend = calculate_trend(task_weights)
    base_score = 50
    return round(min(100, max(0, base_score + trend * 10)), 2)

def calculate_growth_speed(quality_scores):
    """
    计算成长速度得分
    
    通过质量评分的上升趋势评估成长速度，质量提升越快成长速度越快。
    基础分50分，根据趋势调整。
    
    参数:
        quality_scores: 历史质量评分列表
    
    返回:
        成长速度得分（0-100分），数据不足时返回50分
    """
    if not quality_scores or len(quality_scores) < 2:
        return 50.0
    trend = calculate_trend(quality_scores)
    base_score = 50
    return round(min(100, max(0, base_score + trend * 50)), 2)

def calculate_incentive_response_basic(points_data, quality_data):
    """
    计算激励响应得分   
    通过积分与质量评分的相关性评估员工对激励的响应程度。
    使用皮尔逊相关系数计算，相关性越高激励响应越强。    
    参数:
        points_data: 历史积分数据列表
        quality_data: 历史质量评分列表（需与积分数据时间对应）
    
    返回:
        激励响应得分（0-100分），数据不足时返回50分
    """
    if not points_data or not quality_data or len(points_data) < 2:
        return 50.0
    correlation = 0.0
    if len(points_data) == len(quality_data) and len(points_data) > 0:
        mean_points = sum(points_data) / len(points_data)
        mean_quality = sum(quality_data) / len(quality_data)
        numerator = sum((points_data[i] - mean_points) * (quality_data[i] - mean_quality) for i in range(len(points_data)))
        denom_p = math.sqrt(sum((p - mean_points) ** 2 for p in points_data))
        denom_q = math.sqrt(sum((q - mean_quality) ** 2 for q in quality_data))
        if denom_p > 0 and denom_q > 0:
            correlation = numerator / (denom_p * denom_q)
    return round(max(0, min(100, (correlation + 1) / 2 * 100)), 2)

def generate_skill_tags(workload_data):
    tags = workload_data.get('skill_tags') or ''
    if isinstance(tags, list):
        tags = ','.join([t for t in tags if t])
    return tags

def generate_work_style(workload_data, tech_ability, quality_ability, stability_ability):
    style_parts = []
    total_records = workload_data.get('total_records', 0)
    if tech_ability >= 80:
        style_parts.append('擅长高难度任务')
    elif tech_ability >= 60:
        style_parts.append('能够处理中等难度任务')
    if quality_ability >= 90:
        style_parts.append('工作质量极高')
    elif quality_ability >= 80:
        style_parts.append('工作质量高')
    if stability_ability >= 85:
        style_parts.append('工作稳定可靠')
    if total_records >= 50:
        style_parts.append('工作积极主动')
    elif total_records >= 30:
        style_parts.append('工作稳定')
    return '，'.join(style_parts) if style_parts else '工作态度认真'
def determine_performance_level(contribution, quality):
    avg = (contribution + quality) / 2
    return '优秀' if avg >= 85 else '良好' if avg >= 70 else '一般' if avg >= 60 else '待改进'
def determine_risk_level(contribution, stability):
    avg = (contribution + stability) / 2
    return '低' if avg >= 70 else '中' if avg >= 50 else '高'

def generate_ability_image(staff_id, workload_data):
    """
    生成能力画像
    基于工作量数据计算员工的技术能力、工作量能力和稳定性能力，
    生成能力画像数据，用于评估员工的基础工作能力。 
    参数:
        staff_id: 员工ID
        workload_data: 工作量数据字典，包含：
            avg_task_weight: 平均任务难度权重
            total_hours: 总工时
         total_records: 总任务记录数
            quality_scores: 历史质量评分列表
            skill_tags: 技能标签
    
    返回:
        能力画像数据字典，包含能力得分、工作风格、技能标签等
    """
    wd = workload_data
    if not wd:
        return {}
    tech = calculate_tech_ability(wd.get('avg_task_weight', 0) or 0)
    workload = calculate_workload_ability(wd.get('total_hours', 0) or 0, wd.get('total_records', 0) or 0)
    stability = calculate_stability_ability(wd.get('quality_scores', []) or [])
    
    # 能力得分权重：技术40%、工作量40%、稳定性20%
    ability_score = round(tech * 0.4 + workload * 0.4 + stability * 0.2, 2)
    
    total_records = wd.get('total_records', 0) or 0
    if tech >= 80 and workload >= 70:
        work_style = '技术专家型'
    elif workload >= 80:
        work_style = '高效执行型'
    elif tech >= 70:
        work_style = '技术型'
    elif total_records >= 50:
        work_style = '积极主动型'
    else:
        work_style = '稳定型'
    
    image_data = {
        'ability_dimensions': {'tech_ability': tech, 'workload_ability': workload, 'stability_ability': stability},
        'workload_stats': {'total_hours': wd.get('total_hours', 0), 'total_records': total_records,
                          'avg_task_weight': round(wd.get('avg_task_weight', 0), 2)},
        'analysis_details': {'comprehensive_score': ability_score, 'work_style': work_style}
    }
    
    return {
        'staff_id': staff_id, 'image_type': '能力画像', 'ability_score': ability_score,
        'tech_ability': tech, 'workload_ability': workload, 'stability_ability': stability,
        'quality_ability': 0, 'contribution_ability': 0,
        'skill_tags': generate_skill_tags(workload_data),
        'work_style': work_style,
        'image_data': json.dumps(image_data, ensure_ascii=False),
        'model_version': 'v4.0-ability', 'created_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }

def generate_performance_image(staff_id, workload_data):
    """
    生成绩效画像
    
    基于工作量数据计算员工的质量能力、贡献能力和绩效趋势，
    生成绩效画像数据，用于评估员工的工作绩效表现。
    
    参数:
        staff_id: 员工ID
        workload_data: 工作量数据字典，包含：
            - avg_quality_score: 平均质量评分
            - eci_score: ECI（效率贡献指数）
            - quality_scores: 历史质量评分列表（用于计算趋势）
            - skill_tags: 技能标签
    
    返回:
        绩效画像数据字典，包含绩效得分、绩效等级、质量趋势等
    """
    wd = workload_data
    if not wd:
        return {}
    quality = calculate_quality_ability(wd.get('avg_quality_score', 0) or 0)
    contribution = calculate_contribution_ability(wd.get('eci_score', 0) or 0)
    
    quality_scores = wd.get('quality_scores', []) or []
    performance_trend = calculate_trend(quality_scores) if quality_scores else 0
    performance_trend_normalized = round(max(-100, min(100, performance_trend)), 2)
    
    # 绩效得分权重：质量50%、贡献40%、趋势10%
    performance_score = round(quality * 0.5 + contribution * 0.4 + (performance_trend_normalized + 100) / 2 * 0.1, 2)
    
    if performance_score >= 85:
        performance_level = '优秀'
    elif performance_score >= 70:
        performance_level = '良好'
    elif performance_score >= 60:
        performance_level = '一般'
    else:
        performance_level = '待改进'
        
    image_data = {
        'performance_metrics': {'quality_ability': quality, 'contribution_ability': contribution, 'performance_trend': performance_trend_normalized},
        'eci_score': round(wd.get('eci_score', 0), 2),
        'avg_quality_score': round(wd.get('avg_quality_score', 0), 2),
        'performance_level': performance_level
    }
    
    return {
        'staff_id': staff_id, 'image_type': '绩效画像',
        'ability_score': performance_score,
        'quality_ability': quality, 'contribution_ability': contribution,
        'performance_trend': performance_trend_normalized,
        'tech_ability': 0, 'workload_ability': 0, 'stability_ability': 0,
        'performance_level': performance_level,
        'skill_tags': generate_skill_tags(workload_data),
        'work_style': '注重质量和贡献',
        'image_data': json.dumps(image_data, ensure_ascii=False),
        'model_version': 'v4.0-performance', 'created_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    }

def generate_potential_image(staff_id, workload_data):
    """
    生成潜力画像
    
    基于工作量历史数据计算员工的学习能力、成长速度和激励响应，
    生成潜力画像数据，用于评估员工的未来发展潜力和晋升可能性。
    
    参数:
        staff_id: 员工ID
        workload_data: 工作量数据字典，包含：
            task_weight_history: 历史任务难度权重列表
            quality_scores: 历史质量评分列表
            points_history: 历史积分数据列表
            avg_task_weight: 平均任务难度权重
            avg_quality_score: 平均质量评分
            skill_tags: 技能标签
    
    返回:
        潜力画像数据字典，包含潜力得分、晋升概率、职业方向、留任风险等
    """
    wd = workload_data
    if not wd:
        return {}
    
    task_weights = wd.get('task_weight_history', []) or []
    quality_scores = wd.get('quality_scores', []) or []
    points_history = wd.get('points_history', []) or []
    
    learning_ability = calculate_learning_ability(task_weights)
    growth_speed = calculate_growth_speed(quality_scores)
    incentive_response = calculate_incentive_response_basic(points_history, quality_scores[:len(points_history)] if points_history else [])
    
    # 基础能力取技术和质量能力的平均值
    base_ability = (calculate_tech_ability(wd.get('avg_task_weight', 0) or 0) + 
                    calculate_quality_ability(wd.get('avg_quality_score', 0) or 0)) / 2
    
    # 潜力得分权重：学习能力30%、成长速度30%、激励响应20%、基础能力20%
    potential_score = round(learning_ability * 0.3 + growth_speed * 0.3 + incentive_response * 0.2 + base_ability * 0.2, 2)
    
    promotion_probability = round(min(0.95, max(0.1, potential_score / 100)), 2)
    
    if learning_ability >= 70 and growth_speed >= 70:
        career_orientation = '技术专家/管理双通道'
    elif learning_ability >= 65:
        career_orientation = '技术专家方向'
    elif incentive_response >= 70:
        career_orientation = '管理方向'
    else:
        career_orientation = '技术骨干方向'
        
    if potential_score >= 75:
        retention_risk = '低'
    elif potential_score >= 55:
        retention_risk = '中'
    else:
        retention_risk = '高'
        
    image_data = {
        'potential_dimensions': {
            'learning_ability': learning_ability,
            'growth_speed': growth_speed,
            'incentive_response': incentive_response,
            'base_ability': base_ability
        },
        'predictions': {
            'promotion_probability': promotion_probability,
            'career_orientation': career_orientation,
            'retention_risk': retention_risk
        }
    }
    
    return {
        'staff_id': staff_id, 'image_type': '潜力画像',
        'ability_score': potential_score,
        'learning_ability': learning_ability,
        'growth_speed': growth_speed,
        'incentive_response': incentive_response,
        'promotion_probability': promotion_probability,
        'retention_risk': retention_risk,
        'tech_ability': 0, 'workload_ability': 0, 'quality_ability': 0,
        'contribution_ability': 0, 'stability_ability': 0,
        'skill_tags': generate_skill_tags(workload_data),
        'career_orientation': career_orientation,
        'personality_traits': f'学习能力{round(learning_ability, 1)}分，成长速度{round(growth_speed, 1)}分，晋升概率{round(promotion_probability*100, 1)}%',
        'image_data': json.dumps(image_data, ensure_ascii=False),
        'model_version': 'v4.0-potential', 'created_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }

# ==================== API接口定义 ====================

@app.route('/api/image/analyze', methods=['POST'])
def analyze_image():
    """
    画像分析API接口
    
    功能说明：
    接收Java后端或前端的请求，根据指定的画像类型生成对应的员工画像数据。
    支持三种画像类型：
    1. ability_image - 能力画像：技术能力、工作量能力、稳定性能力
    2. performance_image - 绩效画像：质量能力、贡献能力、绩效趋势
    3. potential_image - 潜力画像：学习能力、成长速度、激励响应、晋升概率
    
    调用流程：
    Java后端 -> HTTP POST -> Python服务 -> 算法计算 -> 返回JSON -> Java后端保存到数据库
    
    请求参数（JSON）:
        staff_id: 员工ID
        image_type: 画像类型（'ability_image'/'performance_image'/'potential_image'）
        workload_data: 工作量数据字典
    
    返回:
        JSON响应，包含画像分析结果
    """
    data = request.get_json()
    image_type = data.get('image_type', 'ability_image')
    staff_id = data.get('staff_id')
    workload_data = data.get('workload_data', {})
    
    if image_type == 'ability_image':
        result = generate_ability_image(staff_id, workload_data)
    elif image_type == 'performance_image':
        result = generate_performance_image(staff_id, workload_data)
    elif image_type == 'potential_image':
        result = generate_potential_image(staff_id, workload_data)
    else:
        return jsonify({'code': 400, 'message': '不支持的画像类型', 'data': None}), 400
    
    return jsonify({'code': 200, 'message': '画像分析成功', 'data': result}), 200

@app.route('/api/image/batch-analyze', methods=['POST'])
def batch_analyze_image():
    data = request.get_json()
    staff_ids = data.get('staff_ids', [])
    image_type = data.get('image_type', 'ability_image')
    workload_data_list = data.get('workload_data_list', [])
    results = []
    
    for i, staff_id in enumerate(staff_ids):
        workload_data = workload_data_list[i] if i < len(workload_data_list) else {}
        
        if image_type == 'ability_image':
            result = generate_ability_image(staff_id, workload_data)
        elif image_type == 'performance_image':
            result = generate_performance_image(staff_id, workload_data)
        elif image_type == 'potential_image':
            result = generate_potential_image(staff_id, workload_data)
        else:
            continue
        
        results.append({'staff_id': staff_id, 'status': 'success', **result})
    
    return jsonify({
        'code': 200, 'message': '批量画像分析完成',
        'data': {'total': len(results), 'results': results}
    }), 200

@app.route('/api/points/calculate', methods=['POST'])
def calculate_points():
    data = request.get_json()
    staff_id = data.get('staff_id')
    perf_points = int(data.get('performance_score', 0) * 10)
    att_points = data.get('attendance_days', 0) * 2
    project_bonus = data.get('project_bonus', 0)
    total_points = perf_points + att_points + project_bonus
    
    return jsonify({
        'code': 200, 'message': '积分计算成功',
        'data': {
            'staff_id': staff_id, 'total_points': total_points,
            'points_details': [
                {'type': '绩效积分', 'value': perf_points, 'source': f'绩效分数: {data.get("performance_score", 0)}'},
                {'type': '考勤积分', 'value': att_points, 'source': f'出勤天数: {data.get("attendance_days", 0)}'}
            ] + ([{'type': '项目奖励积分', 'value': project_bonus, 'source': '项目奖励'}] if project_bonus > 0 else []),
            'calculation_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }
    }), 200

@app.route('/api/points/optimize', methods=['POST'])
def optimize_points():
    data = request.get_json()
    staff_ids = data.get('staff_ids', [])
    total_points = data.get('total_points', 1000)
    points_per_staff = total_points // len(staff_ids)
    remainder = total_points % len(staff_ids)
    allocation = [{'staff_id': sid, 'allocated_points': points_per_staff + (1 if i < remainder else 0)}
                  for i, sid in enumerate(staff_ids)]
    
    return jsonify({'code': 200, 'message': '积分优化分配成功',
                   'data': {'total_points': total_points, 'allocation': allocation}}), 200

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({
        'code': 200, 'message': '服务运行正常',
        'data': {'service': 'Python算法服务', 'version': '1.0.0',
                'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
    }), 200

@app.route('/api/image/analyze-comprehensive', methods=['POST'])
def analyze_comprehensive():
    data = request.get_json()
    staff_id = data.get('staff_id')
    workload_data = data.get('workload_data', {})
    points_data = data.get('points_data', [])
    workload_history = data.get('workload_history', [])
    
    tech_ability = calculate_tech_ability(workload_data.get('avg_task_weight', 1.0))
    workload_ability_score = calculate_workload_ability(workload_data.get('total_hours', 0), 
                                                        workload_data.get('total_records', 0))
    quality_ability = calculate_quality_ability(workload_data.get('avg_quality_score', 0.8))
    contribution_ability = calculate_contribution_ability(workload_data.get('eci_score', 0))
    stability_ability = calculate_stability_ability(workload_data.get('quality_scores', []))
    
    if ADVANCED_ANALYSIS_AVAILABLE:
        incentive_response = calculate_incentive_response(workload_history, points_data)
        cost_effectiveness = calculate_cost_effectiveness(contribution_ability, workload_data.get('total_points', 0))
        team_collaboration = calculate_team_collaboration(
            workload_data.get('project_count', 0),
            workload_data.get('project_list', ''),
            workload_data.get('task_descriptions', '')
        )
        skill_growth_map = build_skill_growth_map(workload_history)
        project_exp = analyze_project_experience(workload_history, workload_data.get('project_list', ''))
    else:
        incentive_response = 50.0
        cost_effectiveness = 50.0
        team_collaboration = 50.0
        skill_growth_map = {}
        project_exp = {}
    
    comprehensive_score = (
        tech_ability * 0.15 +
        workload_ability_score * 0.15 +
        quality_ability * 0.15 +
        contribution_ability * 0.15 +
        stability_ability * 0.10 +
        incentive_response * 0.10 +
        cost_effectiveness * 0.10 +
        team_collaboration * 0.10
    )
    
    return jsonify({
        'code': 200,
        'message': '综合画像分析完成',
        'data': {
            'staff_id': staff_id,
            'comprehensive_score': round(comprehensive_score, 2),
            'abilities': {
                'tech_ability': tech_ability,
                'workload_ability': workload_ability_score,
                'quality_ability': quality_ability,
                'contribution_ability': contribution_ability,
                'stability_ability': stability_ability,
                'incentive_response': incentive_response,
                'cost_effectiveness': cost_effectiveness,
                'team_collaboration': team_collaboration
            },
            'skill_growth_map': skill_growth_map,
            'project_experience': project_exp,
            'analysis_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
        }
    }), 200

@app.route('/', methods=['GET'])
def index():
    endpoints = {
        '画像分析': '/api/image/analyze',
        '批量画像分析': '/api/image/batch-analyze',
        '综合画像分析': '/api/image/analyze-comprehensive',
        '积分计算': '/api/points/calculate',
        '积分优化': '/api/points/optimize',
        '人员分配优化': '/api/allocation/optimize',
        '健康检查': '/api/health'
    }
    return jsonify({
        'message': 'Python算法服务API',
        'version': '2.0.0',
        'advanced_analysis': ADVANCED_ANALYSIS_AVAILABLE,
        'endpoints': endpoints
    }), 200

def _normalize_tags(tags_str):
    """
    将技能/标签字符串统一转为小写，按常见分隔符拆分，去除空白。
    支持分隔符：逗号(, ，)、顿号(、)、分号(;)、空格。
    """
    if not tags_str:
        return set()
    # 统一分隔符为逗号
    for sep in ['，', '、', ';', '；', ' ']:
        tags_str = tags_str.replace(sep, ',')
    return set([s.strip().lower() for s in tags_str.split(',') if s.strip()])

def calculate_skill_match(employee_skills, project_skills):
    # 若项目未要求技能，则视为全匹配
    if not project_skills:
        return 1.0
    emp_set = _normalize_tags(employee_skills)
    proj_set = _normalize_tags(project_skills)
    if not proj_set:
        return 1.0
    intersection = emp_set & proj_set
    return len(intersection) / len(proj_set)

def prepare_employee_data(employees_data):
    employee_info = {}
    for emp in employees_data:
        staff_id = emp['staff_id']
        skills = emp.get('skill_tags', '') or ''
        # 只在缺失时使用0，不再强制最小值为1，确保真实值被使用
        quality_ability = emp.get('quality_ability')
        quality_ability = 0 if quality_ability is None else quality_ability
        workload_ability = emp.get('workload_ability')
        workload_ability = 0 if workload_ability is None else workload_ability
        ability_score = emp.get('ability_score')
        ability_score = 0 if ability_score is None else ability_score
        avg_quality = emp.get('avg_quality_score', 0.8) or 0.8
        
        employee_info[staff_id] = {
            'skills': skills,
            'quality_ability': quality_ability,
            'workload_ability': workload_ability,
            'ability_score': ability_score,
            'avg_quality': avg_quality
        }
    return employee_info

def optimize_staff_allocation(project_data, employees_data):
    """
    人员分配优化函数
    
    功能说明：
    使用Gurobi优化求解器（线性规划），根据项目需求和员工能力，计算最优的人员分配方案。
    
    优化目标：
    最大化：总质量 × 工时（确保能力高的人分配更多工时）
    
    约束条件：
    1. 员工数量不超过项目最大员工数限制
    2. 总分配工时等于项目预算工时（或尽可能接近）
    3. 每个被选中的员工至少分配一定比例的工时
    4. 每个员工的工时不超过其能力上限
    
    评分规则：
    技能匹配度权重70%（最重要）
    质量能力权重15%
    综合能力权重15%
    如果技能完全不匹配，降权处理（得分范围0.1-0.2）
    
    算法流程：
    1. 计算每个员工的技能匹配度
    2. 计算每个员工的综合得分（技能匹配70% + 质量15% + 能力15%）
    3. 构建Gurobi优化模型
    4. 求解最优分配方案
    5. 如果求解失败，使用贪心算法作为备选方案
    
    参数:
        project_data: 项目数据字典，包含：
            - project_id: 项目ID
            - required_skills: 所需技能（逗号分隔字符串）
            - budgeted_hours: 预算工时
            - max_staff_count: 最大员工数
        employees_data: 员工数据列表，每个员工包含：
            - staff_id: 员工ID
            - skill_tags: 技能标签
            - quality_ability: 质量能力
            - workload_ability: 工作量能力
            - ability_score: 综合能力得分
            - avg_quality_score: 平均质量评分
    
    返回:
        分配结果字典，包含选中员工、分配工时、得分等信息
    """
    project_id = project_data.get('project_id')
    required_skills = project_data.get('required_skills', '') or ''
    budgeted_hours = project_data.get('budgeted_hours', 0) or 0
    max_staff_count = project_data.get('max_staff_count', 10) or 10
    
    print('人员分配调试信息（Python）')
    print(f'接收到的员工数据数量: {len(employees_data)}')
    print(f'员工ID列表: {[e.get("staff_id") for e in employees_data]}')
    print('\n前端传递的原始员工数据:')
    for emp in employees_data[:2]:  # 只显示前2个
        print(f'  员工{emp.get("staff_id")}:')
        print(f'    - ability_score: {emp.get("ability_score")}')
        print(f'    - workload_ability: {emp.get("workload_ability")}')
        print(f'    - quality_ability: {emp.get("quality_ability")}')
        print(f'    - skill_tags: {emp.get("skill_tags")}')
        print(f'    - 所有字段: {list(emp.keys())}')
    
    print('\n=== Python接收到的原始数据（类型检查）===')
    for emp in employees_data[:3]:  # 显示前3个
        staff_id = emp.get("staff_id")
        print(f'\n员工{staff_id}:')
        for key in ['ability_score', 'workload_ability', 'quality_ability']:
            val = emp.get(key)
            print(f'  {key}: {val} (类型: {type(val).__name__}, 是否为None: {val is None})')
    
    if budgeted_hours <= 0:
        return {'code': 400, 'message': '项目预算工时必须大于0', 'data': None}
    
    employee_info = prepare_employee_data(employees_data)
    print(f'准备后的员工信息keys: {list(employee_info.keys())}')
    if not employee_info:
        return {'code': 400, 'message': '没有可用的员工数据', 'data': None}
    
    staff_ids = list(employee_info.keys())
    n_staff = len(staff_ids)
    
    model = gp.Model("StaffAllocation")
    model.setParam('OutputFlag', 0)
    
    x = {}
    y = {}
    match_scores = {}
    total_scores = {}
    max_hours_dict = {}  # 保存每个员工的工时上限
    
    print('\n=== Gurobi优化：计算员工综合得分 ===')
    normalized_proj = _normalize_tags(required_skills)
    for i, staff_id in enumerate(staff_ids):
        emp = employee_info[staff_id]
        normalized_emp = _normalize_tags(emp['skills'])
        if not normalized_proj:
            match_score = 1.0
        else:
            match_score = len(normalized_emp & normalized_proj) / len(normalized_proj)

        print(f"技能匹配 staff={staff_id}: emp_raw='{emp['skills']}', proj_raw='{required_skills}', "
              f"emp_set={normalized_emp}, proj_set={normalized_proj}, match={match_score:.2f}")

        match_scores[staff_id] = match_score
        
        quality_weight = emp['avg_quality']
        ability_weight = emp['ability_score'] / 100.0
        
        # 技能匹配绝对优先：技能70%，质量15%，能力15%
        if match_score == 0:
            # 技能完全不匹配，降低权重但仍按能力区分（质量10% + 能力10%）
            # 得分范围：0.1-0.2，远低于技能匹配者（0.7-1.0），但能区分能力差异
            total_score = quality_weight * 0.1 + ability_weight * 0.1
            print(f'员工{staff_id}: ⚠️ 技能匹配=0% (降权评分), 质量={quality_weight:.2f}, 能力={ability_weight:.2f}, 综合得分={total_score:.3f}')
        else:
            # 技能匹配权重70%，质量15%，能力15%
            total_score = match_score * 0.7 + quality_weight * 0.15 + ability_weight * 0.15
            print(f'员工{staff_id}: ✓ 技能匹配={match_score*100:.0f}%, 质量={quality_weight:.2f}, 能力={ability_weight:.2f}, 综合得分={total_score:.3f}')
        
        total_scores[staff_id] = total_score
        
        x[staff_id] = model.addVar(vtype=GRB.BINARY, name=f"assign_{staff_id}")
        
        # 更合理的工时上限：按人均工时的1.5倍封顶，避免全部压到一个人
        avg_hours = budgeted_hours / max_staff_count
        max_individual_hours = min(budgeted_hours, avg_hours * 1.5)
        # 至少允许1小时
        max_individual_hours = max(1, max_individual_hours)
        max_hours_dict[staff_id] = max_individual_hours
        
        y[staff_id] = model.addVar(lb=0, ub=max_individual_hours, vtype=GRB.CONTINUOUS, name=f"hours_{staff_id}")
    
    # 修正后的目标函数：最大化"总质量×工时"，确保能力高的人分配更多工时
    model.setObjective(
        sum(total_scores[staff_id] * y[staff_id] for staff_id in staff_ids), 
        GRB.MAXIMIZE
    )
    
    # 约束1：员工数量上限（灵活处理：如果现有员工不足，可以全部使用）
    max_assignable_staff = min(max_staff_count, len(staff_ids))
    model.addConstr(sum(x[staff_id] for staff_id in staff_ids) <= max_assignable_staff, "max_staff")
    
    # 约束2：总工时等于预算（容量充足）或尽力分配（容量不足）
    total_capacity = sum(max_hours_dict[sid] for sid in staff_ids)
    if total_capacity >= budgeted_hours * 0.8:
        # 容量充足，要求总工时精确等于预算
        model.addConstr(sum(y[staff_id] for staff_id in staff_ids) == budgeted_hours, "budget")
    else:
        # 容量不足，尽力分配
        model.addConstr(sum(y[staff_id] for staff_id in staff_ids) <= budgeted_hours, "budget")
    
    # 约束3：员工工时与选中状态关联，并设置最低分配比例以促进多员工分配
    min_hours_each = budgeted_hours / max_staff_count * 0.3  # 可调：选中后至少30%的人均工时
    for staff_id in staff_ids:
        # 如果员工被选中（x=1），工时在[min_hours_each, max_individual_hours]范围内
        # 如果员工未选中（x=0），工时必须为0
        model.addConstr(y[staff_id] <= max_hours_dict[staff_id] * x[staff_id], f"link_max_{staff_id}")
        model.addConstr(y[staff_id] >= min_hours_each * x[staff_id], f"link_min_{staff_id}")
    
    print(f'\n项目预算工时: {budgeted_hours}小时')
    print(f'最大员工数: {max_staff_count}人')
    print(f'员工总工时容量: {total_capacity:.1f}小时')
    
    print('开始Gurobi求解...\n')
    
    model.optimize()
    
    print(f'\n=== Gurobi求解完成 ===')
    print(f'求解状态: {model.status} (2=OPTIMAL最优解)')
    print(f'目标函数值: {model.objVal:.2f}')
    
    if model.status == GRB.OPTIMAL:
        allocation_result = []
        total_hours = 0
        
        print('\n=== 每个员工的分配详情 ===')
        for staff_id in staff_ids:
            is_selected = x[staff_id].x > 0.5
            hours = y[staff_id].x
            print(f'员工{staff_id}: 是否选中={is_selected}, 分配工时={hours:.2f}小时, 综合得分={total_scores[staff_id]:.3f}')
            
            if is_selected:
                score = total_scores[staff_id]
                allocation_result.append({
                    'staff_id': staff_id,
                    'allocated_hours': round(hours, 2),
                    'allocation_score': round(score, 2),
                    'skill_match': round(match_scores[staff_id], 2),
                    'quality_score': round(employee_info[staff_id]['avg_quality'], 2),
                    'ability_score': round(employee_info[staff_id]['ability_score'], 2)
                })
                total_hours += hours
        
        print(f'\n最终分配结果:')
        print(f'  选中员工数: {len(allocation_result)}人')
        print(f'  总分配工时: {total_hours:.2f}小时')
        print(f'  工时利用率: {total_hours/budgeted_hours*100:.1f}%')
        
        return {
            'code': 200,
            'message': '人员分配优化成功',
            'data': {
                'project_id': project_id,
                'total_allocated_hours': round(total_hours, 2),
                'budgeted_hours': budgeted_hours,
                'utilization_rate': round(total_hours / budgeted_hours * 100, 2) if budgeted_hours > 0 else 0,
                'allocations': allocation_result,
                'total_staff': len(allocation_result)
            }
        }
    else:
        error_reasons = {
            3: '无可行解（INFEASIBLE）：约束条件无法同时满足',
            4: '无界解（UNBOUNDED）：目标函数可以无限增大',
            5: '无界或无可行解（INF_OR_UNBD）',
            6: '数值困难（NUMERIC）',
            7: '内存不足（OUT_OF_MEMORY）',
            8: '超时（TIME_LIMIT）',
            9: '用户中断（USER_OBJ_LIMIT）'
        }
        error_msg = error_reasons.get(model.status, f'未知错误（状态码：{model.status}）')
        print(f'\n Gurobi标准求解未找到最优解: {error_msg}')
        print('尝试放宽约束，提供次优方案...')
        
        # 极简贪心算法：按得分比例分配工时
        if model.status == 3:  # INFEASIBLE（理论上已极少出现）
            allocation_result = []
            
            # 灵活的员工数量上限
            max_assignable = min(max_staff_count, len(staff_ids))
            sorted_staff = sorted(staff_ids, key=lambda sid: total_scores[sid], reverse=True)[:max_assignable]
            
            # 计算选中员工的总得分
            total_score_sum = sum(total_scores[sid] for sid in sorted_staff)
            
            if total_score_sum > 0:
                # 按得分比例分配工时
                for staff_id in sorted_staff:
                    allocated_hours = budgeted_hours * (total_scores[staff_id] / total_score_sum)
                    allocation_result.append({
                        'staff_id': staff_id,
                        'allocated_hours': round(allocated_hours, 2),
                        'allocation_score': round(total_scores[staff_id], 2),
                        'skill_match': round(match_scores[staff_id], 2),
                        'quality_score': round(employee_info[staff_id]['avg_quality'], 2),
                        'ability_score': round(employee_info[staff_id]['ability_score'], 2)
                    })
            
            print(f'\n使用极简贪心算法（按得分比例）:')
            print(f'  选中员工数: {len(allocation_result)}人')
            print(f'  总分配工时: {budgeted_hours}小时')
            
            return {
                'code': 200,
                'message': '已生成分配方案（按得分比例）',
                'data': {
                    'project_id': project_id,
                    'total_allocated_hours': budgeted_hours,
                    'budgeted_hours': budgeted_hours,
                    'utilization_rate': 100.0,
                    'allocations': allocation_result,
                    'total_staff': len(allocation_result)
                }
            }
        else:
            # 其他错误仍然返回500
            return {'code': 500, 'message': f'优化求解失败 - {error_msg}', 'data': None}

@app.route('/api/allocation/optimize', methods=['POST'])
def optimize_allocation():
    """
    人员分配优化API接口
    
    接收前端请求，调用优化算法计算最优的人员分配方案。
    使用Gurobi求解器进行线性规划优化。
    
    请求参数（JSON）:
        project_data: 项目数据字典
        employees_data: 员工数据列表
        use_enhanced: 是否使用增强版算法（当前默认False，使用基础版）
    
    返回:
        JSON响应，包含分配结果（选中员工、分配工时、得分等）
    """
    data = request.get_json()
    project_data = data.get('project_data', {})
    employees_data = data.get('employees_data', [])
    use_enhanced = data.get('use_enhanced', False)  # 改为默认False，使用基础版
    
    print('\n' + '='*60)
    print('收到人员分配请求')
    print(f'项目ID: {project_data.get("project_id")}')
    print(f'预算工时: {project_data.get("budgeted_hours")} 小时')
    print(f'所需技能: {project_data.get("required_skills")}')
    print(f'最大员工数: {project_data.get("max_staff_count")}')
    print(f'候选员工数: {len(employees_data)}')
    print(f'use_enhanced参数: {use_enhanced}')
    print(f'ENHANCED_ALLOCATION_AVAILABLE: {ENHANCED_ALLOCATION_AVAILABLE}')
    print('='*60 + '\n')
    
    if not project_data or not employees_data:
        return jsonify({'code': 400, 'message': '项目数据和员工数据不能为空', 'data': None}), 400
    
    # 强制使用基础版优化算法（包含详细日志和修复后的目标函数）
    print('>>> 使用基础版优化算法（已修复）\n')
    result = optimize_staff_allocation(project_data, employees_data)
    
    return jsonify(result), result.get('code', 200)

if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    debug = os.getenv('DEBUG', 'False').lower() == 'true'
    app.run(host='0.0.0.0', port=port, debug=debug)
