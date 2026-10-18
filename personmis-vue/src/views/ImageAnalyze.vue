<template>
  <NavMain></NavMain>
  <div class="box">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>💡 画像分析（智能生成）</span>
        </div>
      </template>
      <el-form :model="analyzeForm" style="width:100%;" label-width="25%">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="员工ID" prop="staff_id">
              <el-input v-model="analyzeForm.staff_id" placeholder="请输入员工ID"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="画像类型" prop="image_type">
              <el-select v-model="analyzeForm.image_type" placeholder="请选择画像类型" style="width:100%" @change="handleTypeChange">
                <el-option label="能力画像" value="ability_image">
                  <span style="float: left">能力画像</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">技能+工作量+稳定性</span>
                </el-option>
                <el-option label="绩效画像" value="performance_image">
                  <span style="float: left">绩效画像</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">质量+贡献+趋势</span>
                </el-option>
                <el-option label="潜力画像" value="potential_image">
                  <span style="float: left">潜力画像</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">学习+成长+晋升</span>
                </el-option>
                <el-option label="整体画像" value="overall_image">
                  <span style="float: left">整体画像</span>
                  <span style="float: right; color: #8492a6; font-size: 12px">综合+多维度分析</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item>
              <el-button type="primary" @click="analyze" :loading="analyzing">开始分析</el-button>
              <el-button type="danger" @click="reset">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 画像类型说明 -->
      <el-alert v-if="analyzeForm.image_type" :type="getAlertType()" :closable="false" style="margin-top: 15px;">
        <template #title>
          <strong>{{ getImageTypeName() }}</strong>
        </template>
        <p>{{ getImageTypeDescription() }}</p>
        <p><strong>核心维度：</strong>{{ getImageTypeDimensions() }}</p>
        <p><strong>数据来源：</strong>{{ getImageTypeDataSource() }}</p>
      </el-alert>
    </el-card>

    <!-- 工作量数据统计 -->
    <el-card v-if="workloadStats" style="margin-top: 20px;">
      <template #header>
        <span>📊 工作量数据统计</span>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="总工时">
          <el-tag type="primary">{{ workloadStats.total_hours }} 小时</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总记录数">
          <el-tag type="primary">{{ workloadStats.total_records }} 条</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="平均难度">
          <el-tag type="warning">{{ workloadStats.avg_task_weight ? workloadStats.avg_task_weight.toFixed(2) : '0.00' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="平均质量">
          <el-tag type="success">{{ workloadStats.avg_quality_score ? workloadStats.avg_quality_score.toFixed(2) : '0.00' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="ECI贡献">
          <el-tag type="danger">{{ workloadStats.eci_score ? workloadStats.eci_score.toFixed(2) : '0.00' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="数据状态">
          <el-tag v-if="workloadStats.total_records >= 10" type="success">✓ 充足</el-tag>
          <el-tag v-else type="warning">⚠ 偏少</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 能力画像结果展示 -->
    <div v-if="analyzeResult && analyzeResult.image_type === '能力画像'" style="margin-top: 20px;">
      <el-card>
      <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>🎯 能力画像分析结果</span>
            <el-button type="success" @click="showSaveDialog" size="small">💾 保存画像</el-button>
        </div>
      </template>

        <el-alert type="success" :closable="false" style="margin-bottom: 20px;">
          <strong>综合能力得分：{{ analyzeResult.ability_score }} 分</strong> | 工作风格：{{ analyzeResult.work_style }}
        </el-alert>

        <h3>三维能力分析</h3>
        <el-row :gutter="15" style="margin-top: 15px;">
          <el-col :span="8">
            <el-card shadow="hover" class="ability-card">
              <div class="ability-content">
                <div class="ability-icon">🔧</div>
                <div class="ability-title">技术能力</div>
                <div class="ability-score">{{ analyzeResult.tech_ability }}</div>
                <el-progress :percentage="analyzeResult.tech_ability" :stroke-width="10" color="#409EFF"></el-progress>
                <div class="ability-source">基于任务难度权重</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="ability-card">
              <div class="ability-content">
                <div class="ability-icon">⚡</div>
                <div class="ability-title">工作量能力</div>
                <div class="ability-score">{{ analyzeResult.workload_ability }}</div>
                <el-progress :percentage="analyzeResult.workload_ability" :stroke-width="10" color="#67C23A"></el-progress>
                <div class="ability-source">基于工时和记录数</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="ability-card">
              <div class="ability-content">
                <div class="ability-icon">📊</div>
                <div class="ability-title">稳定性</div>
                <div class="ability-score">{{ analyzeResult.stability_ability }}</div>
                <el-progress :percentage="analyzeResult.stability_ability" :stroke-width="10" color="#909399"></el-progress>
                <div class="ability-source">基于质量波动性</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <h4>技能标签云</h4>
            <div style="padding: 20px; background: #f5f7fa; border-radius: 8px;">
              <el-tag v-for="(tag, index) in getSkillTags()" :key="index" size="large" style="margin: 5px;">
                {{ tag }}
              </el-tag>
            </div>
          </el-col>
          <el-col :span="12">
            <h4>能力雷达图</h4>
            <div ref="abilityRadar" style="width: 100%; height: 300px;"></div>
          </el-col>
        </el-row>
      </el-card>
        </div>

    <!-- 绩效画像结果展示 -->
    <div v-if="analyzeResult && analyzeResult.image_type === '绩效画像'" style="margin-top: 20px;">
      <el-card>
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>📈 绩效画像分析结果</span>
            <el-button type="success" @click="showSaveDialog" size="small">💾 保存画像</el-button>
          </div>
        </template>

        <el-alert type="warning" :closable="false" style="margin-bottom: 20px;">
          <strong>绩效得分：{{ analyzeResult.ability_score }} 分</strong> | 绩效等级：
          <el-tag :type="getPerformanceTagType()">{{ analyzeResult.performance_level }}</el-tag>
        </el-alert>

        <h3>三维绩效分析</h3>
        <el-row :gutter="15" style="margin-top: 15px;">
          <el-col :span="8">
            <el-card shadow="hover" class="performance-card">
              <div class="ability-content">
                <div class="ability-icon">✨</div>
                <div class="ability-title">质量能力</div>
                <div class="ability-score">{{ analyzeResult.quality_ability }}</div>
                <el-progress :percentage="analyzeResult.quality_ability" :stroke-width="10" color="#E6A23C"></el-progress>
                <div class="ability-source">平均质量评分</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="performance-card">
              <div class="ability-content">
                <div class="ability-icon">🎯</div>
                <div class="ability-title">综合贡献(指数)</div>
                <div class="ability-score">{{ analyzeResult.contribution_ability }}</div>
                <el-progress :percentage="analyzeResult.contribution_ability" :stroke-width="10" color="#F56C6C"></el-progress>
                <div class="ability-source">
                  ECI原始值：{{ workloadStats && workloadStats.eci_score ? workloadStats.eci_score.toFixed(2) : '0.00' }}
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="performance-card">
              <div class="ability-content">
                <div class="ability-icon">📈</div>
                <div class="ability-title">绩效趋势</div>
                <div class="ability-score">{{ analyzeResult.performance_trend ? analyzeResult.performance_trend.toFixed(2) : '0.00' }}</div>
                <div style="height: 10px; margin: 10px 0;">
                  <el-progress 
                    :percentage="(analyzeResult.performance_trend + 100) / 2" 
                    :stroke-width="10" 
                    :color="analyzeResult.performance_trend >= 0 ? '#67C23A' : '#F56C6C'">
                  </el-progress>
                </div>
                <div class="ability-source">近期质量趋势(%)</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <h4>绩效等级分布</h4>
            <div ref="performancePie" style="width: 100%; height: 300px;"></div>
          </el-col>
          <el-col :span="12">
            <h4>质量趋势图</h4>
            <div ref="qualityTrendChart" class="quality-trend-chart"></div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 潜力画像结果展示 -->
    <div v-if="analyzeResult && analyzeResult.image_type === '潜力画像'" style="margin-top: 20px;">
      <el-card>
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>🚀 潜力画像分析结果</span>
            <el-button type="success" @click="showSaveDialog" size="small">💾 保存画像</el-button>
          </div>
        </template>

        <el-alert type="info" :closable="false" style="margin-bottom: 20px;">
          <strong>潜力得分：{{ analyzeResult.ability_score }} 分</strong> | 职业方向：{{ analyzeResult.career_orientation }}
        </el-alert>

        <h3>四维潜力分析</h3>
        <el-row :gutter="15" style="margin-top: 15px;">
          <el-col :span="6">
            <el-card shadow="hover" class="potential-card">
              <div class="ability-content">
                <div class="ability-icon">📚</div>
                <div class="ability-title">学习能力</div>
                <div class="ability-score">{{ analyzeResult.learning_ability }}</div>
                <el-progress :percentage="analyzeResult.learning_ability" :stroke-width="8" color="#409EFF"></el-progress>
                <div class="ability-source">任务难度增长率</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="potential-card">
              <div class="ability-content">
                <div class="ability-icon">🌱</div>
                <div class="ability-title">成长速度</div>
                <div class="ability-score">{{ analyzeResult.growth_speed }}</div>
                <el-progress :percentage="analyzeResult.growth_speed" :stroke-width="8" color="#67C23A"></el-progress>
                <div class="ability-source">质量分数增长率</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="potential-card">
              <div class="ability-content">
                <div class="ability-icon">💪</div>
                <div class="ability-title">激励响应</div>
                <div class="ability-score">{{ analyzeResult.incentive_response }}</div>
                <el-progress :percentage="analyzeResult.incentive_response" :stroke-width="8" color="#E6A23C"></el-progress>
                <div class="ability-source">积分与绩效关联</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="potential-card">
              <div class="ability-content">
                <div class="ability-icon">🎓</div>
                <div class="ability-title">晋升概率</div>
                <div class="ability-score">{{ Math.round(analyzeResult.promotion_probability * 100) }}%</div>
                <el-progress :percentage="Math.round(analyzeResult.promotion_probability * 100)" :stroke-width="8" color="#F56C6C"></el-progress>
                <div class="ability-source">综合预测</div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <h4>职业发展方向</h4>
            <div style="padding: 30px; text-align: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px; color: white;">
              <div style="font-size: 36px; font-weight: bold; margin-bottom: 10px;">
                {{ analyzeResult.career_orientation }}
              </div>
              <div style="font-size: 16px; opacity: 0.9;">
                基于学习能力和成长速度综合判断
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <h4>留存风险评估</h4>
            <div style="padding: 30px; text-align: center; background: #f5f7fa; border-radius: 8px;">
              <el-tag 
                :type="analyzeResult.retention_risk === '低' ? 'success' : analyzeResult.retention_risk === '中' ? 'warning' : 'danger'"
                size="large"
                style="font-size: 24px; padding: 15px 30px;">
                {{ analyzeResult.retention_risk }}风险
              </el-tag>
              <div style="font-size: 14px; color: #909399; margin-top: 15px;">
                潜力得分：{{ analyzeResult.ability_score }} | 晋升概率：{{ Math.round(analyzeResult.promotion_probability * 100) }}%
              </div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 整体画像结果展示 -->
    <div v-if="analyzeResult && analyzeResult.image_type === '整体画像'" style="margin-top: 20px;">
      <el-card>
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span>📊 整体画像分析结果</span>
            <el-button type="success" @click="showSaveDialog" size="small">💾 保存画像</el-button>
          </div>
        </template>

        <el-alert type="info" :closable="false" style="margin-bottom: 20px;">
          <strong>综合得分：{{ analyzeResult.ability_score }} 分</strong>
        </el-alert>

        <!-- 整体画像布局 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 基本信息卡片 -->
          <el-col :span="6">
            <el-card shadow="hover" class="overall-card">
              <template #header>
                <span style="font-weight: bold;">基本信息</span>
              </template>
              <div class="info-item">
                <span class="info-label">姓名：</span>
                <span class="info-value">{{ analyzeResult.sname || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">部门：</span>
                <span class="info-value">{{ analyzeResult.dept_name || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">岗位：</span>
                <span class="info-value">{{ analyzeResult.post_name || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">年龄：</span>
                <span class="info-value">{{ analyzeResult.age || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">入职天数：</span>
                <span class="info-value">{{ analyzeResult.tenure_days || '-' }}</span>
              </div>
            </el-card>
          </el-col>

          <!-- 工作量卡片 -->
          <el-col :span="6">
            <el-card shadow="hover" class="overall-card">
              <template #header>
                <span style="font-weight: bold;">工作量</span>
              </template>
              <div class="info-item">
                <span class="info-label">总工时：</span>
                <span class="info-value">{{ workloadStats?.total_hours || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">记录数：</span>
                <span class="info-value">{{ workloadStats?.total_records || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">平均难度：</span>
                <span class="info-value">{{ (workloadStats?.avg_task_weight || 0).toFixed(2) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">平均质量：</span>
                <span class="info-value">{{ (workloadStats?.avg_quality_score || 0).toFixed(1) }}星</span>
              </div>
              <div class="info-item">
                <span class="info-label">ECI：</span>
                <span class="info-value">{{ (workloadStats?.eci_score || 0).toFixed(2) }}</span>
              </div>
            </el-card>
          </el-col>

          <!-- 积分卡片 -->
          <el-col :span="6">
            <el-card shadow="hover" class="overall-card">
              <template #header>
                <span style="font-weight: bold;">积分</span>
              </template>
              <div class="info-item">
                <span class="info-label">累计获得：</span>
                <span class="info-value">{{ pointsData?.total_earned || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">已使用：</span>
                <span class="info-value">{{ pointsData?.total_used || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">当前可用：</span>
                <span class="info-value">{{ pointsData?.current_available || 0 }}</span>
              </div>
            </el-card>
          </el-col>

          <!-- 画像摘要卡片 -->
          <el-col :span="6">
            <el-card shadow="hover" class="overall-card">
              <template #header>
                <span style="font-weight: bold;">画像摘要</span>
              </template>
              <div class="info-item">
                <span class="info-label">能力画像：</span>
                <span class="info-value">{{ analyzeResult.ability_score || 0 }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">绩效画像：</span>
                <span class="info-value">{{ analyzeResult.performance_level || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">潜力画像：</span>
                <span class="info-value">{{ analyzeResult.career_orientation || '-' }}</span>
              </div>
              <div class="info-item" style="flex-wrap: wrap; align-items: flex-start; margin-bottom: 10px;">
                <span class="info-label" style="margin-bottom: 5px;">标签：</span>
                <div class="skill-tags-container">
                  <template v-if="analyzeResult.skill_tags">
                    <span class="skill-tag-item" v-for="tag in analyzeResult.skill_tags.split(',')" :key="tag">{{ tag }}</span>
                  </template>
                  <span v-else class="info-value">-</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 添加一些额外的图表展示 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span>能力雷达图</span>
              </template>
              <div ref="abilityRadar" style="width: 100%; height: 300px;"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span>技能标签云</span>
              </template>
              <div style="padding: 20px; background: #f5f7fa; border-radius: 8px;">
                <el-tag v-for="(tag, index) in getSkillTags()" :key="index" size="large" style="margin: 5px;">
                  {{ tag }}
                </el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 保存确认对话框 -->
    <el-dialog title="保存画像" v-model="saveDialogVisible" width="600px">
      <el-alert type="warning" :closable="false" style="margin-bottom: 20px;">
        确认要保存该画像吗？保存后可以在"画像管理"页面查看和编辑。
      </el-alert>
      <el-descriptions :column="2" border v-if="analyzeResult">
        <el-descriptions-item label="员工ID">{{ analyzeResult.staff_id }}</el-descriptions-item>
        <el-descriptions-item label="画像类型">{{ analyzeResult.image_type }}</el-descriptions-item>
        <el-descriptions-item label="综合得分">{{ analyzeResult.ability_score }}</el-descriptions-item>
        <el-descriptions-item label="模型版本">{{ analyzeResult.model_version }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="saveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveImage" :loading="saving">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.overall-card {
  height: 350px; /* Significantly increased card height */
  display: flex;
  flex-direction: column;
  overflow: visible;
}

.overall-card .el-card__header {
  padding: 12px 15px;
  font-size: 16px;
  font-weight: bold;
  background-color: #f5f7fa;
}

.overall-card .el-card__body {
  padding: 15px;
  overflow-y: auto;
  max-height: calc(100% - 48px);
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 14px;
  padding: 0 2px;
  font-size: 14px;
  line-height: 1.6;
  flex-wrap: wrap;
}

.info-label {
  font-weight: bold;
  color: #606266;
  white-space: nowrap;
  flex: 0 0 80px;
  text-align: left;
  margin-right: 10px;
}

.info-value {
  color: #303133;
  font-family: 'Courier New', monospace;
  text-align: right;
  flex: 1;
  min-width: 0;
  word-break: break-word;
}

/* Add some padding between rows */
.el-col {
  margin-bottom: 20px;
}

/* Ensure proper spacing between cards */
.el-row:last-child {
  margin-bottom: 0;
}

/* Special handling for skill tags to allow multi-line */
.skill-tags-container {
  max-height: 120px;
  min-height: 30px;
  overflow-y: auto;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  width: 100%;
  box-sizing: border-box;
}

.skill-tag-item {
  display: inline-block;
  margin: 3px 6px;
  padding: 4px 10px;
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 16px;
  font-size: 12px;
  color: #409eff;
  line-height: 1.5;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.skill-tag-item:hover {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
  cursor: default;
}

/* Improve scrolling for skill tags container */
.skill-tags-container::-webkit-scrollbar {
  width: 6px;
}

.skill-tags-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.skill-tags-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.skill-tags-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.ability-card, .performance-card, .potential-card {
  margin-bottom: 20px;
  height: 220px; /* Standardize card height */
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.performance-card .el-card__body {
  padding: 15px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(100% - 48px);
}

.performance-card .ability-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.performance-card .ability-score {
  font-size: 28px;
  font-weight: bold;
  margin: 10px 0;
  text-align: center;
}

/* Quality trend chart styling */
.quality-trend-chart {
  height: 300px;
  width: 100%;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* Adjust the four-card layout */
.el-row {
  margin-bottom: 20px;
}

/* Increase the height of radar chart and skill tag cloud containers */
.radar-chart-container {
  height: 350px;
}

.skill-tag-cloud-container {
  height: 350px;
}
</style>

<script>
/**
 * 画像分析页面
 * 
 * 功能说明：
 * 1. 员工画像智能生成（能力画像/绩效画像/潜力画像/整体画像）
 * 2. 工作量数据统计展示（总工时/记录数/平均难度/平均质量/ECI）
 * 3. 画像结果可视化（雷达图/饼图/趋势图）
 * 4. 画像保存（将分析结果保存到数据库）
 * 
 * 关键数据来源：
 * - /getStaffDetail - 获取员工基本信息（技能标签）
 * - /getWorkloadStatsByStaffId - 获取工作量统计数据
 * - /getPointsSummary - 获取积分汇总数据
 * - /getWelfarePointsByStaffId - 获取积分历史数据
 * - http://localhost:5000/api/image/analyze - Python服务画像分析
 * - /addEmpImage - 保存画像到数据库
 */
import NavMain from '@/components/NavMain.vue'
import * as echarts from 'echarts'

export default {
  components: {
    NavMain
  },
  data() {
    return {
        // 分析表单数据
        analyzeForm: {
          staff_id: '',                    // 员工ID
          image_type: 'ability_image'      // 画像类型（ability_image/performance_image/potential_image/overall_image）
        },
        workloadStats: null,               // 工作量统计数据（总工时/记录数/平均难度/平均质量/ECI等）
        analyzeResult: null,               // 画像分析结果（能力得分/各维度能力/工作风格/职业方向等）
        pointsData: null,                  // 积分汇总数据（累计获得/已使用/当前可用）
        analyzing: false,                  // 分析中状态
        saveDialogVisible: false,          // 保存对话框显示状态
        saving: false,                     // 保存中状态
        abilityRadarChart: null,           // 能力雷达图ECharts实例
        performancePieChart: null,         // 绩效饼图ECharts实例
        qualityTrendChart: null            // 质量趋势图ECharts实例
      }
  },
  mounted() {
    const staff_id = this.$route.query.staff_id
    if (staff_id) {
      this.analyzeForm.staff_id = staff_id
    }
  },
  methods: {
    handleTypeChange() {
      this.analyzeResult = null
      this.workloadStats = null
      this.pointsData = null
    },
    getAlertType() {
      const types = {
        'ability_image': 'primary',
        'performance_image': 'success',
        'potential_image': 'warning',
        'overall_image': 'info'
      }
      return types[this.analyzeForm.image_type] || 'info'
    },
    getImageTypeName() {
      const names = {
        'ability_image': '能力画像',
        'performance_image': '绩效画像',
        'potential_image': '潜力画像',
        'overall_image': '整体画像'
      }
      return names[this.analyzeForm.image_type]
    },
    getImageTypeDescription() {
      const desc = {
        'ability_image': '评估员工的技能水平和工作能力，帮助了解员工擅长的技术领域和工作效率。',
        'performance_image': '评估员工的工作质量和业绩贡献，跟踪员工的绩效表现趋势。',
        'potential_image': '预测员工的成长潜力和发展方向，帮助制定培养计划和晋升决策。',
        'overall_image': '综合评估员工的能力、绩效和潜力，提供全面的员工画像分析。'
      }
      return desc[this.analyzeForm.image_type]
    },
    getImageTypeDimensions() {
      const dims = {
        'ability_image': '技术能力、工作量能力、稳定性（3维）',
        'performance_image': '质量能力、综合贡献、绩效趋势（3维）',
        'potential_image': '学习能力、成长速度、激励响应、晋升概率（4维）',
        'overall_image': '技术能力、工作量能力、稳定性、质量能力、综合贡献、绩效趋势、学习能力、成长速度、激励响应、晋升概率（10维）'
      }
      return dims[this.analyzeForm.image_type]
    },
    getImageTypeDataSource() {
      const sources = {
        'ability_image': '任务难度权重、总工时、工作记录数、质量波动性',
        'performance_image': '平均质量评分、ECI贡献度、质量分数时间序列',
        'potential_image': '任务难度历史、质量分数历史、积分数据、综合表现',
        'overall_image': '所有维度数据的综合，包括能力、绩效和潜力画像的全部数据源'
      }
      return sources[this.analyzeForm.image_type]
    },
    getPerformanceTagType() {
      const types = {
        '优秀': 'danger',
        '良好': 'success',
        '一般': 'warning',
        '待改进': 'info'
      }
      return types[this.analyzeResult?.performance_level] || 'info'
    },
    getSkillTags() {
      if (!this.analyzeResult?.skill_tags) return []
      return this.analyzeResult.skill_tags.split(',').filter(t => t)
    },
    /**
     * 画像分析主流程
     * 1. 获取员工基本信息（技能标签）
     * 2. 获取员工工作量统计数据（工时、质量、ECI等）
     * 3. 获取员工积分历史（用于潜力画像的激励响应计算）
     * 4. 调用Python服务进行画像分析
     * 5. 根据画像类型渲染对应的图表
     */
    async analyze() {
      if (!this.analyzeForm.staff_id) {
        this.$message.warning('请输入员工ID')
        return
      }

      this.analyzing = true
      try {
        // 获取员工手动技能标签和基本信息（从员工信息表）
        let skillTags = ''
        let staffInfo = {}
        try {
          const staffResp = await this.$axios.get('/getStaffDetail', { params: { id: this.analyzeForm.staff_id }})
          if (staffResp.data) {
            staffInfo = staffResp.data
            skillTags = (staffResp.data && staffResp.data.skill_tags) || ''
            // 打印员工信息用于调试
            console.log('Staff Info:', staffResp.data)
          }
          // #region agent log
          fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              sessionId: 'debug-session',
              runId: 'run2-pre',
              hypothesisId: 'H4',
              location: 'ImageAnalyze.vue:staffResp',
              message: 'Staff detail response (skill_tags)',
              data: {
                staffId: this.analyzeForm.staff_id,
                hasData: !!staffResp?.data,
                keys: Object.keys(staffResp?.data || {}).slice(0, 40),
                skill_tags: staffResp?.data?.skill_tags || '',
                skill_tags_len: (staffResp?.data?.skill_tags || '').length
              },
              timestamp: Date.now()
            })
          }).catch(() => {})
          // #endregion
        } catch (e) {
          skillTags = ''
          // #region agent log
          fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              sessionId: 'debug-session',
              runId: 'run2-pre',
              hypothesisId: 'H4',
              location: 'ImageAnalyze.vue:staffResp',
              message: 'Staff detail request failed',
              data: { staffId: this.analyzeForm.staff_id },
              timestamp: Date.now()
            })
          }).catch(() => {})
          // #endregion
        }

        const statsResponse = await this.$axios.get('/getWorkloadStatsByStaffId', {
          params: { staffId: this.analyzeForm.staff_id }
        })
        this.workloadStats = statsResponse.data
        
        // 获取积分汇总数据
        try {
          const pointsSummaryResp = await this.$axios.get('/getPointsSummary', {
            params: { staff_id: this.analyzeForm.staff_id }
          })
          this.pointsData = pointsSummaryResp.data
          console.log('=== 积分数据调试 ===')
          console.log('pointsData:', this.pointsData)
        } catch (error) {
          console.error('获取积分数据失败:', error)
          this.pointsData = null
        }
        
        // 调试：查看workloadStats的实际内容
        console.log('=== 工作量数据调试 ===')
        console.log('workloadStats:', this.workloadStats)
        console.log('quality_scores_str:', this.workloadStats?.quality_scores_str)
        console.log('task_weight_history_str:', this.workloadStats?.task_weight_history_str)

        // 若员工未手动维护技能标签，则从其参与项目的 required_skills 推断
        if (!skillTags && this.workloadStats && this.workloadStats.inferred_skill_tags) {
          skillTags = this.workloadStats.inferred_skill_tags
        }

        // 获取积分历史（潜力画像需要）
        let pointsHistory = []
        try {
          const pointsResp = await this.$axios.get('/getWelfarePointsByStaffId', { params: { staff_id: this.analyzeForm.staff_id } })
          const pointsList = (pointsResp?.data && pointsResp.data.pointsList) || []
          // 取时间顺序的积分值序列（points_value），用于Python计算激励响应相关性
          pointsHistory = pointsList
            .slice()
            .sort((a, b) => {
              const timeA = a.transaction_time || a.transaction_time1 || ''
              const timeB = b.transaction_time || b.transaction_time1 || ''
              return String(timeA).localeCompare(String(timeB))
            })
            .map(p => {
              const val = p.points_value || 0
              return Number(val)
            })
            .filter(v => !Number.isNaN(v) && v > 0)

          // #region agent log
          fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              sessionId: 'debug-session',
              runId: 'run2-pre',
              hypothesisId: 'H5',
              location: 'ImageAnalyze.vue:pointsResp',
              message: 'Welfare points fetched',
              data: {
                staffId: this.analyzeForm.staff_id,
                pointsList_len: Array.isArray(pointsList) ? pointsList.length : -1,
                pointsHistory_len: pointsHistory.length,
                pointsHistory_sample: pointsHistory.slice(0, 5)
              },
              timestamp: Date.now()
            })
          }).catch(() => {})
          // #endregion
        } catch (e) {
          pointsHistory = []
          // #region agent log
          fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              sessionId: 'debug-session',
              runId: 'run2-pre',
              hypothesisId: 'H5',
              location: 'ImageAnalyze.vue:pointsResp',
              message: 'Welfare points request failed',
              data: { staffId: this.analyzeForm.staff_id },
              timestamp: Date.now()
            })
          }).catch(() => {})
          // #endregion
        }

        // #region agent log
        fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            sessionId: 'debug-session',
            runId: 'run2-pre',
            hypothesisId: 'H2',
            location: 'ImageAnalyze.vue:statsResponse',
            message: 'Workload stats response',
            data: {
              staffId: this.analyzeForm.staff_id,
              total_records: this.workloadStats?.total_records,
              quality_scores_str: this.workloadStats?.quality_scores_str,
              task_weight_history_str: this.workloadStats?.task_weight_history_str,
              inferred_skill_tags: this.workloadStats?.inferred_skill_tags,
              final_skill_tags: skillTags,
              has_points_history_field: Object.prototype.hasOwnProperty.call(this.workloadStats || {}, 'points_history'),
              points_history_type: typeof (this.workloadStats && this.workloadStats.points_history),
              keys: Object.keys(this.workloadStats || {}).slice(0, 30)
            },
            timestamp: Date.now()
          })
        }).catch(() => {})
        // #endregion

        // 解析历史数据字符串为数组
        const parseStringToArray = (str) => {
          if (!str) return []
          return str.split(',').map(v => parseFloat(v)).filter(v => !isNaN(v))
        }

        const workload_data = {
          total_hours: this.workloadStats.total_hours || 0,
          total_records: this.workloadStats.total_records || 0,
          avg_task_weight: this.workloadStats.avg_task_weight || 0,
          avg_quality_score: this.workloadStats.avg_quality_score || 0,
          eci_score: this.workloadStats.eci_score || 0,
          quality_scores: parseStringToArray(this.workloadStats.quality_scores_str),
          task_weight_history: parseStringToArray(this.workloadStats.task_weight_history_str),
          points_history: pointsHistory,
          skill_tags: skillTags
        }

        // #region agent log
        fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            sessionId: 'debug-session',
            runId: 'run2-pre',
            hypothesisId: 'H3',
            location: 'ImageAnalyze.vue:workload_data',
            message: 'Parsed workload data',
            data: {
              staffId: this.analyzeForm.staff_id,
              quality_scores_len: workload_data.quality_scores?.length,
              task_weight_history_len: workload_data.task_weight_history?.length,
              points_history_len: Array.isArray(workload_data.points_history) ? workload_data.points_history.length : -1,
              skill_tags_len: (workload_data.skill_tags || '').length,
              quality_scores_sample: workload_data.quality_scores?.slice(0, 3),
              task_weight_history_sample: workload_data.task_weight_history?.slice(0, 3)
            },
            timestamp: Date.now()
          })
        }).catch(() => {})
        // #endregion

        console.log('=== 工作量数据调试 ===')
        console.log('quality_scores数组:', workload_data.quality_scores)
        console.log('task_weight_history数组:', workload_data.task_weight_history)
        console.log('技能标签:', workload_data.skill_tags)

        // 处理整体画像类型 - 后端暂不支持，使用能力画像作为基础数据
        const apiImageType = this.analyzeForm.image_type === 'overall_image' ? 'ability_image' : this.analyzeForm.image_type

        const response = await this.$axios.post('http://localhost:5000/api/image/analyze', {
          staff_id: this.analyzeForm.staff_id,
          image_type: apiImageType,
          workload_data: workload_data
        })

        if (response.data.code === 200) {
          this.analyzeResult = response.data.data
          
          // 如果是整体画像，添加其他维度字段的默认值
          if (this.analyzeForm.image_type === 'overall_image') {
            this.analyzeResult.image_type = '整体画像'
            
            // 添加员工基本信息 - 处理后端返回的实际字段名
            this.analyzeResult.sname = staffInfo.sname || staffInfo.name || staffInfo.username || staffInfo.employee_name || ''
            
            // 从后端获取部门和岗位名称（后端使用dname和pname）
            this.analyzeResult.dept_name = staffInfo.dname || staffInfo.dept_name || staffInfo.department || staffInfo.dept || staffInfo.department_name || ''
            this.analyzeResult.post_name = staffInfo.pname || staffInfo.post_name || staffInfo.position || staffInfo.job || staffInfo.job_title || staffInfo.position_name || ''
            
            // 计算年龄（从生日字段）
            if (staffInfo.birthday) {
              const today = new Date()
              const birthDate = new Date(staffInfo.birthday)
              this.analyzeResult.age = today.getFullYear() - birthDate.getFullYear()
              const monthDiff = today.getMonth() - birthDate.getMonth()
              if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                this.analyzeResult.age--
              }
            } else {
              this.analyzeResult.age = 0
            }
            
            // 计算入职天数（从入职日期字段）
            if (staffInfo.entrydate) {
              const today = new Date()
              const entryDate = new Date(staffInfo.entrydate)
              const diffTime = Math.abs(today - entryDate)
              this.analyzeResult.tenure_days = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
            } else {
              this.analyzeResult.tenure_days = 0
            }
            
            // 尝试从其他可能的字段获取部门和岗位信息
            if (!this.analyzeResult.dept_name) {
              this.analyzeResult.dept_name = staffInfo.depart_id ? '部门' + staffInfo.depart_id : ''
            }
            if (!this.analyzeResult.post_name) {
              this.analyzeResult.post_name = staffInfo.post_id ? '岗位' + staffInfo.post_id : ''
            }
            
            // 添加更多调试信息
            console.log('Staff Info for Basic Details:', {
              staffInfoKeys: Object.keys(staffInfo),
              availableDeptFields: {
                dname: staffInfo.dname,
                dept_name: staffInfo.dept_name,
                department: staffInfo.department,
                dept: staffInfo.dept,
                department_name: staffInfo.department_name
              },
              availablePostFields: {
                pname: staffInfo.pname,
                post_name: staffInfo.post_name,
                position: staffInfo.position,
                job: staffInfo.job,
                job_title: staffInfo.job_title,
                position_name: staffInfo.position_name
              },
              birthDate: staffInfo.birthday,
              entryDate: staffInfo.entrydate,
              depart_id: staffInfo.depart_id,
              post_id: staffInfo.post_id
            })
            
            // 如果仍然没有数据，尝试通过额外API获取部门和岗位名称
            if (staffInfo.depart_id && !this.analyzeResult.dept_name) {
              try {
                const deptResp = await this.$axios.get('/getDepartDetail', { params: { id: staffInfo.depart_id }})
                console.log('Department API Response:', deptResp.data)
                if (deptResp.data) {
                  // 尝试多种可能的部门名称字段
                  this.analyzeResult.dept_name = deptResp.data.dname || deptResp.data.dept_name || deptResp.data.name || '部门' + staffInfo.depart_id
                }
              } catch (e) {
                console.log('Failed to get department name:', e)
              }
            }
            
            if (staffInfo.post_id && !this.analyzeResult.post_name) {
              try {
                const postResp = await this.$axios.get('/getPostDetail', { params: { id: staffInfo.post_id }})
                console.log('Position API Response:', postResp.data)
                if (postResp.data) {
                  // 尝试多种可能的岗位名称字段
                  this.analyzeResult.post_name = postResp.data.pname || postResp.data.post_name || postResp.data.position_name || postResp.data.name || '岗位' + staffInfo.post_id
                }
              } catch (e) {
                console.log('Failed to get post name:', e)
              }
            }
            
            // 打印最终的员工信息用于调试
            console.log('Final Analyze Result - Basic Info:', {
              name: this.analyzeResult.sname,
              dept: this.analyzeResult.dept_name,
              post: this.analyzeResult.post_name,
              age: this.analyzeResult.age,
              tenure: this.analyzeResult.tenure_days
            })
            
            // 添加绩效画像维度的默认值
            this.analyzeResult.quality_ability = this.analyzeResult.quality_ability || this.workloadStats.avg_quality_score * 2 || 0
            this.analyzeResult.contribution_ability = this.analyzeResult.contribution_ability || this.workloadStats.eci_score * 10 || 0
            this.analyzeResult.performance_trend = this.analyzeResult.performance_trend || 0
            
            // 添加潜力画像维度的默认值
            this.analyzeResult.learning_ability = this.analyzeResult.learning_ability || 0
            this.analyzeResult.growth_speed = this.analyzeResult.growth_speed || 0
            this.analyzeResult.incentive_response = this.analyzeResult.incentive_response || 0
            this.analyzeResult.promotion_probability = this.analyzeResult.promotion_probability || 0.5
            this.analyzeResult.retention_risk = this.analyzeResult.retention_risk || '中'
            this.analyzeResult.career_orientation = this.analyzeResult.career_orientation || '综合型人才'
            
            // 添加积分相关的默认值
            this.analyzeResult.total_points = this.analyzeResult.total_points || 0
            this.analyzeResult.used_points = this.analyzeResult.used_points || 0
            this.analyzeResult.available_points = this.analyzeResult.available_points || 0
          }
          
          this.$message.success('画像分析完成')
          
          this.$nextTick(() => {
            this.renderCharts()
          })
        } else {
          this.$message.error(response.data.message || '分析失败')
        }
      } catch (error) {
        console.error('Analysis error:', error)
        this.$message.error('分析失败: ' + (error.message || '网络错误'))
      } finally {
            this.analyzing = false
      }
    },
    /**
     * 渲染图表
     * 根据画像类型渲染对应的图表（能力画像→雷达图，绩效画像→饼图+趋势图）
     */
    renderCharts() {
      if (this.analyzeResult.image_type === '能力画像' || this.analyzeResult.image_type === '整体画像') {
        this.renderAbilityRadar()
      } else if (this.analyzeResult.image_type === '绩效画像') {
        this.renderPerformancePie()
        this.renderQualityTrend()
      }
    },
    /**
     * 渲染能力画像雷达图
     * 展示员工的技术能力、工作量能力和稳定性三个维度
     * 使用ECharts雷达图组件，直观展示能力分布
     */
    renderAbilityRadar() {
      if (!this.$refs.abilityRadar) return
      
      if (this.abilityRadarChart) {
        this.abilityRadarChart.dispose()
      }

      this.abilityRadarChart = echarts.init(this.$refs.abilityRadar)
      
      const option = {
        radar: {
          indicator: [
            { name: '技术能力', max: 100 },      // 基于任务难度权重计算
            { name: '工作量能力', max: 100 },    // 基于工时和任务数计算
            { name: '稳定性', max: 100 }          // 基于质量评分标准差计算
          ]
        },
        series: [{
          type: 'radar',
          data: [{
            value: [
              this.analyzeResult.tech_ability || 0,
              this.analyzeResult.workload_ability || 0,
              this.analyzeResult.stability_ability || 0
            ],
            name: '能力画像',
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.3)'
            }
          }]
        }]
      }
      
      this.abilityRadarChart.setOption(option)
    },
    /**
     * 渲染绩效画像饼图
     * 展示质量能力、综合贡献和绩效趋势三个维度的占比
     * 将原始得分归一化为百分比，确保饼图总和为100%
     */
    renderPerformancePie() {
      if (!this.$refs.performancePie) return
      
      if (this.performancePieChart) {
        this.performancePieChart.dispose()
      }
      
      this.performancePieChart = echarts.init(this.$refs.performancePie)
      
      // 获取原始得分
      const raw = {
        quality: Number(this.analyzeResult.quality_ability || 0),           // 质量能力（0-100）
        contribution: Number(this.analyzeResult.contribution_ability || 0), // 综合贡献（0-100）
        trendAbs: Math.abs(Number(this.analyzeResult.performance_trend || 0)) // 绩效趋势绝对值
      }
      // 归一化为百分比，确保饼图总和为100%
      const rawSum = raw.quality + raw.contribution + raw.trendAbs
      const pct = rawSum > 0 ? {
        quality: +(raw.quality / rawSum * 100).toFixed(2),
        contribution: +(raw.contribution / rawSum * 100).toFixed(2),
        trend: +(raw.trendAbs / rawSum * 100).toFixed(2)
      } : { quality: 0, contribution: 0, trend: 0 }

      // #region agent log
      fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          sessionId: 'debug-session',
          runId: 'run5-pre',
          hypothesisId: 'H9',
          location: 'ImageAnalyze.vue:renderPerformancePie',
          message: 'Performance pie raw vs normalized',
          data: { staffId: this.analyzeForm.staff_id, raw, rawSum, pct },
          timestamp: Date.now()
        })
      }).catch(() => {})
      // #endregion

      const option = {
          tooltip: {
          trigger: 'item'
          },
          series: [{
            type: 'pie',
          radius: '60%',
            data: [
            { value: pct.quality, name: '质量能力(%)' },
            { value: pct.contribution, name: '综合贡献(%)' },
            { value: pct.trend, name: '绩效趋势(%)' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
      
      this.performancePieChart.setOption(option)
    },
    /**
     * 渲染质量趋势图
     * 展示一段时间内的质量变化趋势
     * 使用ECharts折线图组件，直观展示质量评分的历史变化
     */
    renderQualityTrend() {
      if (!this.$refs.qualityTrendChart) return
      
      // 如果已有图表实例，先销毁
      if (this.qualityTrendChart) {
        this.qualityTrendChart.dispose()
      }

      // 初始化ECharts实例
      this.qualityTrendChart = echarts.init(this.$refs.qualityTrendChart)
      
      // 准备数据 - 使用质量评分历史数据字符串并解析为数组
      const parseStringToArray = (str) => {
        if (!str) return []
        return str.split(',').map(v => parseFloat(v)).filter(v => !isNaN(v))
      }
      let qualityScores = this.workloadStats?.quality_scores_str ? parseStringToArray(this.workloadStats.quality_scores_str) : []
      
      // 将质量评分从0.0-1.0的小数形式转换为0-100的百分比形式
      qualityScores = qualityScores.map(score => score * 100)
      
      // 如果没有质量评分历史数据，生成一些模拟数据作为示例
      if (qualityScores.length === 0) {
        // 生成最近7天的模拟质量评分数据
        qualityScores = [85, 88, 90, 87, 92, 91, 89]
        console.log('=== 质量趋势图调试 ===')
        console.log('没有实际质量评分数据，使用模拟数据:', qualityScores)
      }
      
      // 生成日期标签（从左到右显示更早到更晚的日期）
      const dateLabels = []
      const today = new Date()
      
      // 根据数据点数量调整日期显示策略
      const dataPoints = qualityScores.length
      
      // 计算日期：从左到右是时间的自然流逝方向（从早到晚）
      // 第i个数据点对应的日期（i=0是最早的日期，i=dataPoints-1是最近的日期）
      for (let i = 0; i < dataPoints; i++) {
        const date = new Date(today)
        // 计算与今天的天数差：最早的日期是今天减去(dataPoints-1)天，最近的日期是今天
        const daysDiff = dataPoints - 1 - i
        date.setDate(today.getDate() - daysDiff)
        
        // 根据数据点数量动态决定是否显示日期标签
        let showLabel = true
        let labelFormat = `${date.getMonth() + 1}/${date.getDate()}`
        
        if (dataPoints > 30) {
          // 长时间跨度（超过30个数据点）：只显示部分日期标签
          // 例如，每7天显示一个标签
          showLabel = i % 7 === 0
        } else if (dataPoints > 15) {
          // 中等时间跨度（15-30个数据点）：每3天显示一个标签
          showLabel = i % 3 === 0
        }
        
        // 如果需要显示标签，使用对应的日期格式，否则显示空字符串
        dateLabels.push(showLabel ? labelFormat : '')
      }
      
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: {c}分'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dateLabels,
          name: '日期',
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '质量评分',
          min: 0,
          max: 100,
          axisLabel: {
            formatter: '{value}分'
          }
        },
        series: [{
          name: '质量评分',
          type: 'line',
          smooth: true,
          data: qualityScores,
          symbol: 'circle',
          symbolSize: 6,
          itemStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          },
          markPoint: {
            data: [
              { type: 'max', name: '最高分' },
              { type: 'min', name: '最低分' }
            ]
          },
          markLine: {
            data: [
              { type: 'average', name: '平均分' }
            ]
          }
        }]
      }
      
      // 设置图表配置
      this.qualityTrendChart.setOption(option)
    },
    /**
     * 显示保存对话框
     * 打开保存画像的确认对话框
     */
    showSaveDialog() {
      this.saveDialogVisible = true
    },
    /**
     * 保存画像到数据库
     * 将分析结果保存到emp_image表，成功后跳转到画像管理页面
     */
    async saveImage() {
      this.saving = true
      try {
        const empImage = {
          staff_id: this.analyzeResult.staff_id,
          image_type: this.analyzeResult.image_type,
          ability_score: this.analyzeResult.ability_score,
          tech_ability: this.analyzeResult.tech_ability || 0,
          workload_ability: this.analyzeResult.workload_ability || 0,
          quality_ability: this.analyzeResult.quality_ability || 0,
          contribution_ability: this.analyzeResult.contribution_ability || 0,
          stability_ability: this.analyzeResult.stability_ability || 0,
          performance_trend: this.analyzeResult.performance_trend || 0,
          learning_ability: this.analyzeResult.learning_ability || 0,
          growth_speed: this.analyzeResult.growth_speed || 0,
          incentive_response: this.analyzeResult.incentive_response || 0,
          promotion_probability: this.analyzeResult.promotion_probability || 0,
          retention_risk: this.analyzeResult.retention_risk || '',
          skill_tags: this.analyzeResult.skill_tags || '',
          work_style: this.analyzeResult.work_style || '',
          career_orientation: this.analyzeResult.career_orientation || '',
          performance_level: this.analyzeResult.performance_level || '',
          personality_traits: this.analyzeResult.personality_traits || '',
          image_data: this.analyzeResult.image_data || '',
          model_version: this.analyzeResult.model_version || '',
          status: 1
        }

        const response = await this.$axios.post('/addEmpImage', empImage)
        
        if (response.data === 'ok') {
          this.$message.success('画像保存成功！')
          this.saveDialogVisible = false
          
          setTimeout(() => {
            this.$router.push('/empImage')
          }, 1000)
        } else {
          this.$message.error('保存失败')
        }
      } catch (error) {
        console.error('Save error:', error)
        this.$message.error('保存失败: ' + (error.message || '网络错误'))
      } finally {
        this.saving = false
      }

    },
    /**
     * 重置表单和数据
     * 清空分析结果、统计数据，销毁所有图表实例
     */
    reset() {
      this.analyzeForm = {
        staff_id: '',
        image_type: 'ability_image'
      }
      this.workloadStats = null
      this.analyzeResult = null
      if (this.abilityRadarChart) {
        this.abilityRadarChart.dispose()
        this.abilityRadarChart = null
      }
      if (this.performancePieChart) {
        this.performancePieChart.dispose()
        this.performancePieChart = null
      }
      if (this.qualityTrendChart) {
        this.qualityTrendChart.dispose()
        this.qualityTrendChart = null
      }
    }
  },
  beforeUnmount() {
    if (this.abilityRadarChart) {
      this.abilityRadarChart.dispose()
    }
    if (this.performancePieChart) {
      this.performancePieChart.dispose()
    }
    if (this.qualityTrendChart) {
      this.qualityTrendChart.dispose()
    }
  }
}
</script>

<style scoped>
.box {
  padding: 20px;
  overflow-y: auto;
  max-height: calc(100vh - 80px);
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.ability-card, .potential-card {
  height: 100%;
}

.performance-card {
  height: 220px; /* 统一卡片高度 */
  display: flex;
  flex-direction: column;
}

.ability-content {
  text-align: center;
}

.ability-icon {
  font-size: 36px;
  margin-bottom: 10px;
}

.ability-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.ability-score {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0;
}

.ability-source {
  font-size: 12px;
  color: #909399;
  margin-top: 10px;
}

.quality-trend-chart {
  width: 100%;
  height: 300px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 10px;
}
</style>
