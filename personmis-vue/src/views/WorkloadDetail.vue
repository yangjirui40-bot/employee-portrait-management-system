<template>
  <NavMain></NavMain>
  <div class="workload-container">
    <!-- 查询条件区 -->
    <el-card class="query-card">
      <el-form ref="selectForm" :model="selectForm" :inline="true" label-width="80px">
        <el-form-item label="员工ID" prop="staffId">
          <el-input-number v-model="selectForm.staffId" :min="1" placeholder="请输入员工ID" style="width:150px"></el-input-number>
        </el-form-item>
        <el-form-item label="项目ID" prop="projectId">
          <el-input-number v-model="selectForm.projectId" :min="1" placeholder="请输入项目ID" style="width:150px"></el-input-number>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="selectForm.startDate" type="date" placeholder="选择开始日期" style="width:150px"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="selectForm.endDate" type="date" placeholder="选择结束日期" style="width:150px"></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="selectWorkloadDetailsByCon">查询</el-button>
          <el-button type="success" @click="goToAdd">新增填报</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 关键指标卡片区 -->
    <div class="stats-container">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-card-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalHours || 0 }}</div>
            <div class="stat-label">总工时（小时）</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-card-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.taskCount || 0 }}</div>
            <div class="stat-label">任务数量</div>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-card-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.avgQuality ? (statistics.avgQuality * 5).toFixed(1) : '0.0' }} 星</div>
            <div class="stat-label">平均质量评分</div>
            <el-rate :model-value="(statistics.avgQuality || 0) * 5" disabled show-score text-color="#ff9900" style="margin-top: 5px;"></el-rate>
          </div>
        </div>
      </el-card>
      
      <el-card class="stat-card" shadow="hover">
        <div class="stat-card-content">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <i class="el-icon-trophy"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.eciScore ? statistics.eciScore.toFixed(2) : '0.00' }}</div>
            <div class="stat-label">ECI综合效率</div>
            <el-progress 
              :percentage="eciPercentage" 
              :color="eciColor"
              :stroke-width="8"
              style="margin-top: 5px;"
            ></el-progress>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 月度工作量对比图 -->
    <el-card v-if="monthlyData && monthlyData.length > 0" class="chart-card">
      <template #header>
        <div class="card-header">
          <i class="el-icon-s-data"></i>
          <span>月度工作量对比</span>
          <el-tooltip content="直观对比各月工作量差异" placement="top">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </div>
      </template>
      <div id="monthly-chart" style="width: 100%; height: 320px;"></div>
    </el-card>

    <!-- 工作密集度热力图 -->
    <el-card v-if="heatmapData && heatmapData.length > 0" class="chart-card">
      <template #header>
        <div class="card-header">
          <i class="el-icon-calendar"></i>
          <span>工作密集度热力图</span>
          <el-tooltip content="颜色越深表示当天工作量越大，自动显示数据实际日期范围" placement="top">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </div>
      </template>
      <div id="heatmap-chart" style="width: 100%; height: 300px;"></div>
    </el-card>
    
    <!-- 每日工作量趋势图 -->
    <el-card v-if="trendData && trendData.length > 0" class="chart-card">
      <template #header>
        <div class="card-header">
          <i class="el-icon-data-line"></i>
          <span>每日工作量趋势</span>
        </div>
      </template>
      <div id="trend-chart" style="width: 100%; height: 300px;"></div>
    </el-card>
    
    <!-- 项目信息 -->
    <el-card v-if="projectInfo && projectInfo.projectName" style="margin-bottom: 10px;">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="项目名称">{{ projectInfo.projectName }}</el-descriptions-item>
        <el-descriptions-item label="所需技能">
          <el-tag v-if="projectInfo.requiredSkills" type="success">{{ projectInfo.requiredSkills }}</el-tag>
          <span v-else style="color: #999;">未设置</span>
        </el-descriptions-item>
        <el-descriptions-item label="最大人员数">
          <el-tag v-if="projectInfo.maxStaffCount" type="primary">{{ projectInfo.maxStaffCount }} 人</el-tag>
          <span v-else style="color: #999;">未设置</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 工作明细表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <i class="el-icon-tickets"></i>
          <span>工作明细列表</span>
        </div>
      </template>
      <el-table
        :data="tableData"
        :header-cell-style="headClass"
        :cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'workDate', order: 'descending'}">
        <el-table-column prop="id" label="ID" sortable width="80"></el-table-column>
        <el-table-column prop="staffId" label="员工ID" sortable width="90"></el-table-column>
        <el-table-column prop="staffName" label="员工姓名" width="100"></el-table-column>
        <el-table-column prop="projectName" label="项目名称" width="150"></el-table-column>
        <el-table-column prop="workDate" label="工作日期" :formatter="formatDate" width="110"></el-table-column>
        <el-table-column prop="taskDesc" label="任务描述" show-overflow-tooltip></el-table-column>
        <el-table-column prop="timeSpentHrs" label="工时" sortable width="80"></el-table-column>
        <el-table-column prop="taskWeight" label="难度权重" sortable width="100"></el-table-column>
        <el-table-column prop="qualityScore" label="质量评分" :formatter="formatQualityScore" width="100"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-block">
        <el-pagination
          @current-change="handleCurrentChange"
          v-model:current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>
<script>
/**
 * 工作量明细页面
 * 
 * 功能说明：
 * 1. 工作量记录查询（支持员工/项目/日期筛选）
 * 2. 工作量统计展示（总工时/任务数/平均质量/ECI）
 * 3. 图表可视化（月度对比/热力图/趋势图）
 * 4. 工作明细列表（分页/排序）
 * 
 * 关键数据来源：
 * - /selectWorkloadDetailByCon - 条件查询工作量明细
 * - /api/workload/statistics - 获取统计数据
 * - /api/workload/monthly - 获取月度数据
 * - /api/workload/heatmap - 获取热力图数据
 */
import NavMain from '@/components/NavMain.vue'
import * as echarts from 'echarts'

export default {
  components: {
    NavMain
  },
  created() {
    if (this.$route.query.projectId) {
      this.selectForm.projectId = parseInt(this.$route.query.projectId)
    }
    if (this.$route.query.staffId) {
      this.selectForm.staffId = parseInt(this.$route.query.staffId)
    }
    this.loadWorkloadDetails()
    this.loadStatistics()
  },
  mounted() {
    this.initCharts()
  },
  data() {
    return {
      // 查询条件表单
      selectForm: {
        projectId: null,      // 项目ID筛选
        staffId: null,        // 员工ID筛选
        startDate: null,      // 开始日期
        endDate: null,        // 结束日期
        currentPage: 1,
        pageSize: 10,
        act: ''               // 操作类型：byPage=分页查询，byCon=条件查询
      },
      currentPage: 1,         // 当前页码
      tableData: [],          // 工作明细列表数据
      pageSize: 10,           // 每页显示数量
      total: 0,               // 总记录数
      projectInfo: {},        // 项目信息（项目名称/所需技能/最大人员数）
      // 统计数据（总工时/任务数/平均质量/ECI）
      statistics: {
        totalHours: 0,        // 总工时
        taskCount: 0,         // 任务数量
        avgQuality: 0,        // 平均质量评分（0-1）
        eciScore: 0,          // ECI综合效率得分
        avgTaskWeight: 0      // 平均任务权重
      },
      heatmapChart: null,     // 热力图ECharts实例
      trendChart: null,       // 趋势图ECharts实例
      monthlyChart: null,     // 月度对比图ECharts实例
      heatmapData: [],        // 热力图数据（日期-工时）
      trendData: [],          // 趋势图数据（日期-工时）
      monthlyData: []         // 月度数据（月份-工时/任务数）
    }
  },
  computed: {
    eciPercentage() {
      if (!this.statistics.eciScore) return 0
      return Math.min(Math.round((this.statistics.eciScore / 100) * 100), 100)
    },
    eciColor() {
      const percentage = this.eciPercentage
      if (percentage >= 80) return '#67C23A'
      if (percentage >= 60) return '#E6A23C'
      if (percentage >= 40) return '#F56C6C'
      return '#909399'
    }
  },
  methods: {
    /**
     * 加载工作量明细（分页查询）
     * 页面初始化时调用，获取分页数据
     */
    loadWorkloadDetails() {
      this.selectForm.act = "byPage"
      this.selectForm.currentPage = this.currentPage
      this.selectForm.pageSize = this.pageSize
      this.$axios
        .post('/selectWorkloadDetailByCon', this.selectForm)
        .then(successResponse => {
          this.tableData = successResponse.data.workloadDetails || []
          this.total = successResponse.data.total
        })
        .catch(failResponse => {
          this.$alert(failResponse.response?.status || '加载失败')
        })
    },
    /**
     * 条件查询工作量明细
     * 根据查询条件（员工/项目/日期）获取工作量记录列表
     * 查询成功后自动加载统计数据、月度数据、热力图和趋势图
     */
    selectWorkloadDetailsByCon() {
      this.selectForm.act = "byCon"
      this.selectForm.currentPage = this.currentPage
      this.selectForm.pageSize = this.pageSize
      this.$axios
        .post('/selectWorkloadDetailByCon', this.selectForm)
        .then(successResponse => {
          this.tableData = successResponse.data.workloadDetails || []
          this.total = successResponse.data.total
          if (this.tableData && this.tableData.length > 0) {
            this.projectInfo = {
              projectName: this.tableData[0].projectName,
              requiredSkills: this.tableData[0].requiredSkills,
              maxStaffCount: this.tableData[0].maxStaffCount
            }
          }
          this.loadStatistics()
          this.loadMonthlyData()
          this.loadHeatmapData()
          this.loadTrendData()
        })
        .catch(failResponse => {
          this.$alert(failResponse.response?.status || '查询失败', {confirmButtonText: '确定'})
        })
    },
    /**
     * 加载工作量统计数据
     * 获取总工时、任务数量、平均质量、ECI得分等关键指标
     * ECI = 工时 × 任务权重 × 质量评分，反映综合贡献水平
     */
    loadStatistics() {
      const params = {
        staffId: this.selectForm.staffId,
        projectId: this.selectForm.projectId,
        startDate: this.selectForm.startDate ? this.formatDateForQuery(this.selectForm.startDate) : null,
        endDate: this.selectForm.endDate ? this.formatDateForQuery(this.selectForm.endDate) : null
      }
      this.$axios
        .get('/api/workload/statistics', {params})
        .then(response => {
          this.statistics = response.data

          // #region agent log
          fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
              sessionId: 'debug-session',
              runId: 'run5-pre',
              hypothesisId: 'H8',
              location: 'WorkloadDetail.vue:loadStatistics',
              message: 'Workload statistics fetched',
              data: {
                params,
                eciScore: this.statistics?.eciScore,
                totalHours: this.statistics?.totalHours,
                avgTaskWeight: this.statistics?.avgTaskWeight,
                avgQuality: this.statistics?.avgQuality
              },
              timestamp: Date.now()
            })
          }).catch(() => {})
          // #endregion
        })
        .catch(() => {
          this.statistics = {totalHours: 0, taskCount: 0, avgQuality: 0, eciScore: 0}
        })
    },
    /**
     * 加载热力图数据
     * 获取每日工时数据，用于日历热力图展示工作量密度分布
     */
    loadHeatmapData() {
      const params = {
        staffId: this.selectForm.staffId,
        projectId: this.selectForm.projectId,
        startDate: this.selectForm.startDate ? this.formatDateForQuery(this.selectForm.startDate) : null,
        endDate: this.selectForm.endDate ? this.formatDateForQuery(this.selectForm.endDate) : null
      }
      this.$axios
        .get('/api/workload/heatmap', {params})
        .then(response => {
          this.heatmapData = response.data.map(item => [item.date, item.hours])
          // 如果数据存在但图表未初始化，则初始化图表
          this.$nextTick(() => {
            if (this.heatmapData && this.heatmapData.length > 0 && !this.heatmapChart) {
              const heatmapChartEl = document.getElementById('heatmap-chart')
              if (heatmapChartEl) {
                this.heatmapChart = echarts.init(heatmapChartEl)
              }
            }
            this.renderHeatmap()
          })
        })
        .catch(() => {
          this.heatmapData = []
          this.renderHeatmap()
        })
    },
    /**
     * 加载趋势数据
     * 获取每日工时数据，用于折线图展示工作量变化趋势
     */
    loadTrendData() {
      const params = {
        staffId: this.selectForm.staffId,
        projectId: this.selectForm.projectId,
        startDate: this.selectForm.startDate ? this.formatDateForQuery(this.selectForm.startDate) : null,
        endDate: this.selectForm.endDate ? this.formatDateForQuery(this.selectForm.endDate) : null
      }
      this.$axios
        .get('/api/workload/heatmap', {params})
        .then(response => {
          this.trendData = response.data
          // 如果数据存在但图表未初始化，则初始化图表
          this.$nextTick(() => {
            if (this.trendData && this.trendData.length > 0 && !this.trendChart) {
              const trendChartEl = document.getElementById('trend-chart')
              if (trendChartEl) {
                this.trendChart = echarts.init(trendChartEl)
              }
            }
            this.renderTrend()
          })
        })
        .catch(() => {
          this.trendData = []
          this.renderTrend()
        })
    },
    /**
     * 初始化图表
     * 创建三个ECharts实例：月度对比图、热力图、趋势图
     * 监听窗口大小变化，自动调整图表尺寸
     */
    initCharts() {
      // 检查DOM元素是否存在，避免在v-if隐藏时初始化
      const monthlyChartEl = document.getElementById('monthly-chart')
      const heatmapChartEl = document.getElementById('heatmap-chart')
      const trendChartEl = document.getElementById('trend-chart')
      
      if (monthlyChartEl) {
        this.monthlyChart = echarts.init(monthlyChartEl)
        this.loadMonthlyData()
      }
      
      if (heatmapChartEl) {
        this.heatmapChart = echarts.init(heatmapChartEl)
        this.loadHeatmapData()
      }
      
      if (trendChartEl) {
        this.trendChart = echarts.init(trendChartEl)
        this.loadTrendData()
      }
      
      // 监听窗口大小变化，自动调整图表尺寸
      window.addEventListener('resize', () => {
        this.monthlyChart?.resize()
        this.heatmapChart?.resize()
        this.trendChart?.resize()
      })
    },
    /**
     * 渲染工作量密度热力图
     * 使用ECharts日历坐标系，按日期展示每日工时，颜色深浅表示工作量大小
     */
    renderHeatmap() {
      if (!this.heatmapChart) return
      
      const dateRange = this.getDateRange()
      const option = {
        tooltip: {
          formatter: function(params) {
            return `${params.value[0]}<br/>工时：${params.value[1]}小时`
          }
        },
        visualMap: {
          min: 0,
          max: 12,
          type: 'piecewise',
          orient: 'horizontal',
          left: 'center',
          top: 0,
          pieces: [
            {min: 10, label: '10小时以上', color: '#d53e4f'},
            {min: 8, max: 10, label: '8-10小时', color: '#fc8d59'},
            {min: 6, max: 8, label: '6-8小时', color: '#fee08b'},
            {min: 4, max: 6, label: '4-6小时', color: '#e6f598'},
            {min: 2, max: 4, label: '2-4小时', color: '#99d594'},
            {min: 0, max: 2, label: '0-2小时', color: '#3288bd'}
          ]
        },
        calendar: {
          top: 80,
          left: 50,
          right: 50,
          cellSize: ['auto', 20],
          range: dateRange,
          itemStyle: {
            borderWidth: 0.5
          },
          yearLabel: {show: false},
          dayLabel: {
            nameMap: ['日', '一', '二', '三', '四', '五', '六']
          },
          monthLabel: {
            nameMap: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
          }
        },
        series: [{
          type: 'heatmap',
          coordinateSystem: 'calendar',
          data: this.heatmapData
        }]
      }
      this.heatmapChart.setOption(option)
    },
    /**
     * 渲染ECI效率趋势图
     * 使用折线图展示每日工时变化趋势，帮助分析工作量波动情况
     */
    renderTrend() {
      if (!this.trendChart || !this.trendData.length) return
      
      const dates = this.trendData.map(item => {
        const d = new Date(item.date)
        return `${d.getMonth() + 1}-${d.getDate()}`
      })
      const hours = this.trendData.map(item => item.hours || 0)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['工时']
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '工时'
        },
        series: [
          {
            name: '工时',
            type: 'line',
            data: hours,
            smooth: true,
            areaStyle: {},
            itemStyle: {color: '#5470c6'}
          }
        ]
      }
      this.trendChart.setOption(option)
    },
    /**
     * 加载月度工作量数据
     * 获取按月份汇总的工时和任务数，用于月度对比柱状图
     */
    loadMonthlyData() {
      const params = {
        staffId: this.selectForm.staffId,
        projectId: this.selectForm.projectId,
        startDate: this.selectForm.startDate ? this.formatDateForQuery(this.selectForm.startDate) : null,
        endDate: this.selectForm.endDate ? this.formatDateForQuery(this.selectForm.endDate) : null
      }
      this.$axios
        .get('/api/workload/monthly', {params})
        .then(response => {
          this.monthlyData = response.data
          // 如果数据存在但图表未初始化，则初始化图表
          this.$nextTick(() => {
            if (this.monthlyData && this.monthlyData.length > 0 && !this.monthlyChart) {
              const monthlyChartEl = document.getElementById('monthly-chart')
              if (monthlyChartEl) {
                this.monthlyChart = echarts.init(monthlyChartEl)
              }
            }
            this.renderMonthly()
          })
        })
        .catch(() => {
          this.monthlyData = []
          this.renderMonthly()
        })
    },
    /**
     * 渲染月度工作量对比图
     * 使用柱状图+折线图展示每月工时和任务数，便于对比不同月份的工作量
     */
    renderMonthly() {
      if (!this.monthlyChart) return
      
      if (!this.monthlyData || this.monthlyData.length === 0) {
        return
      }
      
      const months = this.monthlyData.map(item => item.month)
      const hours = this.monthlyData.map(item => item.hours || 0)
      const taskCounts = this.monthlyData.map(item => item.taskCount || 0)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            let result = params[0].axisValue + '<br/>'
            params.forEach(item => {
              result += item.marker + item.seriesName + ': ' + item.value + (item.seriesName === '工时' ? '小时' : '个任务') + '<br/>'
            })
            return result
          }
        },
        legend: {
          data: ['工时', '任务数'],
          top: 10
        },
        grid: {
          left: '8%',
          right: '8%',
          top: '20%',
          bottom: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: months,
          axisLabel: {
            fontSize: 12,
            rotate: 0,
            margin: 10
          },
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '工时(小时)',
            position: 'left',
            axisLabel: {
              formatter: '{value}',
              margin: 8
            },
            nameTextStyle: {
              padding: [0, 0, 0, 10]
            }
          },
          {
            type: 'value',
            name: '任务数(个)',
            position: 'right',
            axisLabel: {
              formatter: '{value}',
              margin: 8
            },
            nameTextStyle: {
              padding: [0, 10, 0, 0]
            }
          }
        ],
        series: [
          {
            name: '工时',
            type: 'bar',
            data: hours,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {offset: 0, color: '#83bff6'},
                {offset: 1, color: '#188df0'}
              ])
            },
            barWidth: '40%',
            barGap: '20%',
            label: {
              show: true,
              position: 'inside',
              formatter: '{c}h',
              fontSize: 11,
              color: '#333333',
              fontWeight: 'bold',
              textStyle: {
                color: '#333333'
              }
            }
          },
          {
            name: '任务数',
            type: 'line',
            yAxisIndex: 1,
            data: taskCounts,
            itemStyle: {
              color: '#67C23A'
            },
            lineStyle: {
              width: 3
            },
            symbol: 'circle',
            symbolSize: 10,
            label: {
              show: true,
              position: 'bottom',
              formatter: '{c}个',
              fontSize: 11,
              color: '#67C23A',
              offset: [0, 8],
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              borderColor: '#67C23A',
              borderWidth: 1,
              borderRadius: 4,
              padding: [2, 4]
            }
          }
        ]
      }
      this.monthlyChart.setOption(option)
    },
    /**
     * 获取日期范围
     * 优先使用筛选条件的日期范围，如果没有则根据数据自动计算
     */
    getDateRange() {
      // 优先使用筛选条件的日期范围
      if (this.selectForm.startDate && this.selectForm.endDate) {
        return [this.formatDateForQuery(this.selectForm.startDate), this.formatDateForQuery(this.selectForm.endDate)]
      }
      
      // 如果有实际数据，根据数据的日期范围自动计算
      if (this.heatmapData && this.heatmapData.length > 0) {
        const dates = this.heatmapData.map(item => new Date(item[0]))
        const minDate = new Date(Math.min(...dates))
        const maxDate = new Date(Math.max(...dates))
        
        // 向前后各扩展3天，让热力图显示更完整
        minDate.setDate(minDate.getDate() - 3)
        maxDate.setDate(maxDate.getDate() + 3)
        
        return [this.formatDateForQuery(minDate), this.formatDateForQuery(maxDate)]
      }
      
      // 默认显示最近2个月
      const end = new Date()
      const start = new Date()
      start.setMonth(start.getMonth() - 1)
      return [this.formatDateForQuery(start), this.formatDateForQuery(end)]
    },
    /**
     * 格式化日期为查询格式
     * 将Date对象转换为YYYY-MM-DD格式字符串
     */
    formatDateForQuery(date) {
      if (!date) return null
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    /**
     * 表格表头样式
     */
    headClass() {
      return 'text-align: center;background:rgb(242,242,242);color:rgb(140,138,140)'
    },
    /**
     * 分页页码变化处理
     * 根据当前查询状态（条件查询/分页查询）重新加载数据
     */
    handleCurrentChange(val) {
      this.currentPage = val
      if (this.selectForm.act === 'byCon' || this.selectForm.projectId) {
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        this.selectWorkloadDetailsByCon()
      } else {
        this.loadWorkloadDetails()
      }
    },
    /**
     * 删除工作量明细
     * 确认后调用后端API删除记录，成功后刷新列表
     */
    handleDelete(index, row) {
      this.$confirm('删除工作量明细, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$axios
          .post('/deleteWorkloadDetail?id=' + row.id)
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
              this.loadWorkloadDetails()
            }else {
              this.$alert('删除失败', {confirmButtonText: '确定' })
            }
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status, {confirmButtonText: '确定' })
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
    },
    /**
     * 跳转到工作量填报页面
     * 如果当前有项目筛选条件，则传递projectId参数
     */
    goToAdd(){
      const query = {}
      if (this.selectForm.projectId) {
        query.projectId = this.selectForm.projectId
      }
      this.$router.push({path: '/addWorkload', query: query})
    },
    /**
     * 格式化日期显示
     * 将日期字符串格式化为YYYY-MM-DD格式
     */
    formatDate(row, column, cellValue) {
      if (cellValue) {
        const date = new Date(cellValue)
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      return ''
    },
    /**
     * 格式化质量评分显示
     * 将0-1的质量评分转换为星级显示（如0.8显示为4星）
     */
    formatQualityScore(row, column, cellValue) {
      if (cellValue != null) {
        const stars = Math.round(cellValue / 0.2)
        return `${stars}星`
      }
      return ''
    }
  }
} 
</script>
<style scoped>
.workload-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}

.query-card {
  margin-bottom: 20px;
}

.stats-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-card-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin-right: 20px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  gap: 8px;
}

.card-header i:first-child {
  color: #409EFF;
  font-size: 18px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-block {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 1200px) {
  .stats-container {
    flex-wrap: wrap;
  }
  .stat-card {
    flex: 0 0 calc(50% - 10px);
  }
}

@media (max-width: 768px) {
  .stat-card {
    flex: 0 0 100%;
  }
}
</style>

