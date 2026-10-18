"""
高级画像分析模块
整合积分数据、技能成长轨迹、团队协作等高级分析功能
"""

from collections import defaultdict
from datetime import datetime, timedelta
import re

def calculate_incentive_response(workload_history, points_data):
    """
    计算激励响应度：分析积分奖励对工作表现的影响
    
    算法逻辑：
    1. 识别积分奖励时间点
    2. 对比奖励前后30天的工作质量
    3. 计算响应度 = 质量提升幅度 × 50 + 50（基准分）
    
    返回：0-100分数，50为中等响应度
    """
    if not points_data or len(points_data) < 2:
        return 50.0
    
    response_scores = []
    
    for point_event in points_data:
        if point_event.get('transaction_type') != 'EARN':
            continue
        
        event_time = point_event.get('transaction_time')
        if not event_time:
            continue
        
        # 获取奖励前后的工作质量
        quality_before = get_quality_around_date(workload_history, event_time, days_before=30, days_after=0)
        quality_after = get_quality_around_date(workload_history, event_time, days_before=0, days_after=30)
        
        if quality_before > 0 and quality_after > 0:
            improvement = (quality_after - quality_before) / (quality_before + 0.01)
            # 将改进率转换为0-100分数
            response_score = 50 + improvement * 50
            response_scores.append(max(0, min(100, response_score)))
    
    if not response_scores:
        return 50.0
    
    return round(sum(response_scores) / len(response_scores), 2)


def calculate_cost_effectiveness(contribution_ability, total_points):
    """
    计算成本效益：单位积分产生的价值
    
    公式：成本效益 = 贡献能力 / (总积分/10 + 1) × 10
    范围：0-100，越高表示单位积分产出越高
    """
    if total_points <= 0:
        return 100.0  # 未消耗积分，效益最高
    
    effectiveness = (contribution_ability / (total_points / 10 + 1)) * 10
    return round(min(100, max(0, effectiveness)), 2)


def calculate_team_collaboration(project_count, project_list, task_descriptions=''):
    """
    计算团队协作能力：基于项目多样性和跨部门协作
    
    评分维度：
    1. 项目数量（60分）：参与越多项目，协作能力越强
    2. 项目多样性（30分）：项目类型越丰富，适应性越强
    3. 协作关键词（10分）：任务描述中的协作相关词汇
    """
    if project_count == 0:
        return 0.0
    
    # 基础分：项目数量（最多10个项目=60分）
    base_score = min(project_count / 10 * 60, 60)
    
    # 多样性分：提取项目关键词
    diversity_score = 0
    if project_list:
        unique_keywords = set()
        projects = project_list.split(',') if isinstance(project_list, str) else project_list
        for project in projects:
            keywords = extract_keywords(project)
            unique_keywords.update(keywords)
        # 最多5个不同关键词=30分
        diversity_score = min(len(unique_keywords) / 5 * 30, 30)
    
    # 协作分：识别协作相关词汇
    collaboration_keywords = ['跨部门', '联合', '协作', '整合', '平台', '团队', '配合', '沟通']
    collaboration_score = 0
    
    search_text = (project_list or '') + ' ' + (task_descriptions or '')
    for keyword in collaboration_keywords:
        if keyword in search_text:
            collaboration_score += 1.25  # 每个关键词1.25分
    
    collaboration_score = min(collaboration_score, 10)
    
    total = base_score + diversity_score + collaboration_score
    return round(min(100, total), 2)


def build_skill_growth_map(workload_history):
    """
    构建技能成长轨迹
    
    返回格式：
    {
        'Java': {
            'first_use': '2024-01-01',
            'last_use': '2024-06-30',
            'difficulty_progression': [1.0, 1.2, 1.5, 1.8],
            'usage_frequency': 15,
            'mastery_level': 75
        },
        ...
    }
    """
    if not workload_history:
        return {}
    
    skill_map = defaultdict(lambda: {
        'first_use': None,
        'last_use': None,
        'difficulties': [],
        'usage_count': 0,
        'dates': []
    })
    
    for record in workload_history:
        task_desc = record.get('task_desc', '')
        work_date = record.get('work_date')
        task_weight = record.get('task_weight', 1.0)
        
        if not task_desc:
            continue
        
        # 从任务描述中提取技能
        skills = extract_skills(task_desc)
        
        for skill in skills:
            if not skill_map[skill]['first_use'] or work_date < skill_map[skill]['first_use']:
                skill_map[skill]['first_use'] = work_date
            
            if not skill_map[skill]['last_use'] or work_date > skill_map[skill]['last_use']:
                skill_map[skill]['last_use'] = work_date
            
            skill_map[skill]['difficulties'].append(task_weight)
            skill_map[skill]['dates'].append(work_date)
            skill_map[skill]['usage_count'] += 1
    
    # 计算每个技能的掌握程度
    result = {}
    for skill, data in skill_map.items():
        if data['usage_count'] == 0:
            continue
        
        difficulties = data['difficulties']
        avg_difficulty = sum(difficulties) / len(difficulties)
        
        # 计算难度成长
        if len(difficulties) > 1:
            difficulty_growth = difficulties[-1] - difficulties[0]
        else:
            difficulty_growth = 0
        
        # 掌握度计算公式：
        # - 平均难度贡献60分（最高难度2.0）
        # - 难度成长贡献20分（最大成长1.0）
        # - 使用频率贡献20分（最多10次）
        mastery_level = (
            min(avg_difficulty / 2.0 * 60, 60) +
            min(max(difficulty_growth, 0) / 1.0 * 20, 20) +
            min(data['usage_count'] / 10 * 20, 20)
        )
        
        result[skill] = {
            'first_use': str(data['first_use']) if data['first_use'] else None,
            'last_use': str(data['last_use']) if data['last_use'] else None,
            'difficulty_progression': [round(d, 2) for d in difficulties[-10:]],  # 最近10次
            'usage_frequency': data['usage_count'],
            'mastery_level': round(mastery_level, 2),
            'avg_difficulty': round(avg_difficulty, 2)
        }
    
    return result


def extract_skills(task_desc):
    """
    从任务描述中提取技能关键词
    """
    skill_keywords = [
        # 编程语言
        'Java', 'Python', 'JavaScript', 'TypeScript', 'C++', 'C#', 'Go', 'PHP', 'Ruby',
        # 框架
        'Spring', 'Django', 'Flask', 'React', 'Vue', 'Angular', 'Node.js', 'Express',
        # 数据库
        'MySQL', 'Oracle', 'PostgreSQL', 'MongoDB', 'Redis', 'Elasticsearch',
        # 工具
        'Docker', 'Kubernetes', 'Jenkins', 'Git', 'Maven', 'Gradle',
        # 技术领域
        'Machine Learning', 'Deep Learning', 'NLP', 'Computer Vision',
        'Microservices', 'Cloud', 'AWS', 'Azure',
        # 能力
        'UI设计', 'UX设计', '需求分析', '架构设计', '系统设计', '测试',
        '项目管理', '文档编写', '代码审查', '性能优化', '安全'
    ]
    
    found_skills = []
    task_desc_lower = task_desc.lower()
    
    for skill in skill_keywords:
        # 大小写不敏感匹配
        if skill.lower() in task_desc_lower or skill in task_desc:
            found_skills.append(skill)
    
    return list(set(found_skills))  # 去重


def extract_keywords(text):
    """
    从文本中提取关键词
    """
    keywords = set()
    
    # 常见项目类型关键词
    common_words = [
        '系统', '平台', '应用', '服务', '工具', '框架',
        '管理', '分析', '监控', '优化', '开发', '设计',
        '前端', '后端', '移动端', '大数据', '云计算', 'AI'
    ]
    
    for word in common_words:
        if word in text:
            keywords.add(word)
    
    return keywords


def get_quality_around_date(workload_history, target_date, days_before=30, days_after=0):
    """
    获取指定日期前后的平均工作质量
    
    参数：
    - target_date: 目标日期（字符串或datetime）
    - days_before: 向前查找的天数
    - days_after: 向后查找的天数
    """
    if isinstance(target_date, str):
        try:
            target_date = datetime.strptime(target_date, '%Y-%m-%d %H:%M:%S')
        except:
            try:
                target_date = datetime.strptime(target_date, '%Y-%m-%d')
            except:
                return 0.8  # 解析失败返回默认值
    
    start_date = target_date - timedelta(days=days_before)
    end_date = target_date + timedelta(days=days_after)
    
    quality_scores = []
    
    for record in workload_history:
        work_date = record.get('work_date')
        if not work_date:
            continue
        
        if isinstance(work_date, str):
            try:
                work_date = datetime.strptime(work_date, '%Y-%m-%d')
            except:
                continue
        
        if start_date <= work_date <= end_date:
            quality_score = record.get('quality_score', 0)
            if quality_score > 0:
                quality_scores.append(quality_score)
    
    if not quality_scores:
        return 0.8  # 默认质量分数
    
    return sum(quality_scores) / len(quality_scores)


def analyze_project_experience(workload_history, project_list=''):
    """
    分析项目经验
    
    返回：
    {
        'total_projects': 5,
        'project_types': ['管理系统', '数据平台', ...],
        'cross_department': 3,
        'complexity_level': 'high'
    }
    """
    if not workload_history:
        return {
            'total_projects': 0,
            'project_types': [],
            'cross_department': 0,
            'complexity_level': 'low'
        }
    
    # 统计项目
    project_ids = set()
    for record in workload_history:
        project_id = record.get('project_id')
        if project_id:
            project_ids.add(project_id)
    
    total_projects = len(project_ids)
    
    # 提取项目类型
    project_types = list(extract_keywords(project_list))
    
    # 识别跨部门协作
    cross_keywords = ['跨部门', '联合', '协作项目', '整合']
    cross_department = sum(1 for keyword in cross_keywords if keyword in project_list)
    
    # 评估复杂度
    avg_difficulty = sum(r.get('task_weight', 1.0) for r in workload_history) / len(workload_history)
    if avg_difficulty >= 1.5:
        complexity_level = 'high'
    elif avg_difficulty >= 1.0:
        complexity_level = 'medium'
    else:
        complexity_level = 'low'
    
    return {
        'total_projects': total_projects,
        'project_types': project_types,
        'cross_department': cross_department,
        'complexity_level': complexity_level,
        'avg_task_difficulty': round(avg_difficulty, 2)
    }


