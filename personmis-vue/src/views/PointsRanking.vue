<template>
  <NavMain></NavMain>
  <div class="ranking-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span style="font-size: 20px; font-weight: bold;">🏆 福利积分排行榜</span>
          <span style="color: #909399; font-size: 14px; margin-left: 20px;">积分自动发放 | 激励员工竞争 | 可兑换培训和福利</span>
        </div>
      </template>
      
      <el-alert type="success" :closable="false" style="margin-bottom: 15px;">
        <template #title>
          <strong>💡 积分自动获得方式</strong>
        </template>
        <p style="margin: 5px 0;">✅ 填报工作量时，系统根据<strong>质量评分、任务难度、工时</strong>自动计算并发放积分</p>
        <p style="margin: 5px 0;">✅ 无需申请，完成工作即刻到账</p>
        <p style="margin: 5px 0;">✅ 积分可用于兑换培训课程、休假天数、实物奖励等</p>
      </el-alert>
      
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="统计周期">
          <el-select v-model="filterForm.period" placeholder="选择周期" @change="loadRanking">
            <el-option label="全部时间" value="all"></el-option>
            <el-option label="本月" value="month"></el-option>
            <el-option label="本季度" value="quarter"></el-option>
            <el-option label="本年度" value="year"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="filterForm.departId" placeholder="全部部门" clearable @change="loadRanking">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.dname" :value="dept.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="显示数量">
          <el-input-number v-model="filterForm.limit" :min="5" :max="50" @change="loadRanking"></el-input-number>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card class="ranking-card">
          <template #header>
            <span style="font-size: 18px; font-weight: bold;">📊 积分排行</span>
          </template>
          
          <el-table :data="rankingList" :header-cell-style="{textAlign: 'center', background: '#f5f7fa'}" 
                    :cell-style="{textAlign: 'center'}" :row-class-name="tableRowClassName">
            <el-table-column label="排名" width="80">
              <template #default="scope">
                <div class="rank-badge" :class="'rank-' + (scope.$index + 1)">
                  <span v-if="scope.$index < 3" class="medal">{{ ['🥇', '🥈', '🥉'][scope.$index] }}</span>
                  <span v-else>{{ scope.$index + 1 }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="staff_id" label="员工ID" width="100" sortable></el-table-column>
            <el-table-column prop="staff_name" label="员工姓名" width="120"></el-table-column>
            <el-table-column prop="depart_name" label="部门" width="120"></el-table-column>
            <el-table-column prop="total_earned" label="累计获得" width="100">
              <template #default="scope">
                <el-tag type="success">{{ scope.row.total_earned }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="total_used" label="已使用" width="100">
              <template #default="scope">
                <el-tag type="info">{{ scope.row.total_used }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="current_balance" label="当前余额" width="120">
              <template #default="scope">
                <el-tag type="danger" size="large" effect="dark">
                  <strong style="font-size: 16px;">{{ scope.row.current_balance }}</strong>
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="last_earn_time" label="最近获得时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.last_earn_time) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span style="font-size: 18px; font-weight: bold;">📈 积分分布</span>
          </template>
          <div ref="distributionChart" style="width: 100%; height: 350px;"></div>
        </el-card>

        <el-card class="info-card" style="margin-top: 20px;">
          <template #header>
            <span style="font-size: 18px; font-weight: bold;">💡 积分规则说明</span>
          </template>
          <el-timeline>
            <el-timeline-item timestamp="高质量工作" placement="top">
              <p>质量评分 ≥ 0.95 → <el-tag type="success" size="small">+10积分</el-tag></p>
              <p>质量评分 = 1.0 → <el-tag type="danger" size="small">+20积分</el-tag></p>
            </el-timeline-item>
            <el-timeline-item timestamp="高难度任务" placement="top">
              <p>任务权重 ≥ 1.5 → <el-tag type="success" size="small">+15积分</el-tag></p>
              <p>任务权重 ≥ 1.8 → <el-tag type="danger" size="small">+30积分</el-tag></p>
            </el-timeline-item>
            <el-timeline-item timestamp="长工时投入" placement="top">
              <p>工时 ≥ 20小时 → <el-tag type="success" size="small">+8积分</el-tag></p>
              <p>工时 ≥ 30小时 → <el-tag type="danger" size="small">+15积分</el-tag></p>
            </el-timeline-item>
            <el-timeline-item timestamp="高效完成" placement="top">
              <p>单次ECI ≥ 25 → <el-tag type="warning" size="small">+20积分</el-tag></p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
/**
 * 福利积分排行榜页面
 * 
 * 功能说明：
 * 1. 积分排行榜展示（按当前余额排序）
 * 2. 统计周期筛选（全部时间/本月/本季度/本年度）
 * 3. 部门筛选（按部门查看排名）
 * 4. 积分分布饼状图（可视化展示积分分布）
 * 5. 积分规则说明（展示积分获得规则）
 * 
 * 关键数据来源：
 * - /api/points/ranking - 获取积分排行榜
 * - /getDepartment - 获取部门列表
 */
import NavMain from '@/components/NavMain.vue'
import * as echarts from 'echarts'

export default {
  components: {
    NavMain
  },
  data() {
    return {
      // 筛选条件表单
      filterForm: {
        period: 'all',        // 统计周期（all/month/quarter/year）
        departId: null,        // 部门ID筛选
        limit: 10              // 显示数量限制
      },
      rankingList: [],         // 排行榜数据列表
      departments: [],          // 部门列表（用于下拉选择）
      distributionChart: null   // 积分分布饼状图ECharts实例
    }
  },
  created() {
    this.loadDepartments()
    this.loadRanking()
  },
  mounted() {
    this.initChart()
  },
  methods: {
    async loadDepartments() {
      try {
        const response = await this.$axios.get('/getDepartment')
        this.departments = response.data || []
      } catch (error) {
        console.error('加载部门失败:', error)
      }
    },
    /**
     * 加载积分排行榜
     * 根据统计周期、部门筛选条件获取积分排名数据
     * 成功后自动渲染积分分布饼状图
     */
    async loadRanking() {
      try {
        const params = {
          period: this.filterForm.period !== 'all' ? this.filterForm.period : null,
          departId: this.filterForm.departId,
          limit: this.filterForm.limit
        }
        console.log('加载排行榜，参数:', params)
        const response = await this.$axios.get('/api/points/ranking', { params })
        console.log('排行榜响应:', response.data)
        this.rankingList = response.data.ranking || []
        console.log('排行榜数据条数:', this.rankingList.length)
        this.$nextTick(() => {
          this.renderChart()
        })
        if (this.rankingList.length === 0) {
          this.$message.info('暂无排行榜数据')
        }
      } catch (error) {
        console.error('加载排行榜失败:', error)
        this.$message.error('加载排行榜失败: ' + (error.response?.data?.message || error.message))
        this.rankingList = []
      }
    },
    /**
     * 初始化图表
     * 创建积分分布饼状图ECharts实例
     */
    initChart() {
      if (this.$refs.distributionChart) {
        this.distributionChart = echarts.init(this.$refs.distributionChart)
      }
    },
    /**
     * 渲染积分分布饼状图
     * 显示所有员工数据，但只有前三个显示姓名标签
     */
    renderChart() {
      if (!this.distributionChart || this.rankingList.length === 0) return

      // 显示所有员工数据
      const data = this.rankingList.map((item, index) => ({
        name: item.staff_name,
        value: item.current_balance,
        itemStyle: {
          color: index < 3 ? ['#FFD700', '#C0C0C0', '#CD7F32'][index] : '#409EFF'
        }
      }))

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} 积分 ({d}%)'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: (params) => {
              // 只有前三个显示姓名，其他不显示标签
              if (params.dataIndex < 3) {
                return params.name + '\n' + params.value + '分'
              }
              return ''
            }
          },
          labelLine: {
            show: (params) => {
              // 只有前三个显示标签线
              return params.dataIndex < 3
            }
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold',
              formatter: '{b}\n{c}分'
            }
          },
          data: data
        }]
      }

      this.distributionChart.setOption(option)
    },
    /**
     * 表格行样式类名
     * 为前三名添加特殊样式（金色/银色/铜色背景）
     */
    tableRowClassName({rowIndex}) {
      if (rowIndex === 0) return 'rank-1-row'
      if (rowIndex === 1) return 'rank-2-row'
      if (rowIndex === 2) return 'rank-3-row'
      return ''
    },
    /**
     * 格式化日期时间显示
     * 将日期时间字符串格式化为本地化格式
     */
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.ranking-container {
  padding: 20px;
  margin-left: 240px;
  min-height: 100vh;
  background: #f0f2f5;
}

.header-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
}

.ranking-card, .chart-card, .info-card {
  height: auto;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-weight: bold;
  font-size: 18px;
}

.rank-badge.rank-1 {
  background: linear-gradient(135deg, #FFD700, #FFA500);
  color: #fff;
}

.rank-badge.rank-2 {
  background: linear-gradient(135deg, #C0C0C0, #A9A9A9);
  color: #fff;
}

.rank-badge.rank-3 {
  background: linear-gradient(135deg, #CD7F32, #8B4513);
  color: #fff;
}

.rank-badge .medal {
  font-size: 24px;
}

.rank-1-row {
  background: #fff7e6 !important;
}

.rank-2-row {
  background: #f5f5f5 !important;
}

.rank-3-row {
  background: #fff4e6 !important;
}
</style>

