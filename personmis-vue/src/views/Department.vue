<template>
  <NavMain></NavMain>
  <div class="box1">
    <br>
    <!-- 工作量统计卡片 -->
    <div class="workload-summary">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="summary-card" shadow="hover">
            <div class="card-content">
              <div class="card-icon" style="background-color: #409EFF;">
                <i class="el-icon-s-data"></i>
              </div>
              <div class="card-info">
                <div class="card-title">总工作量</div>
                <div class="card-value">{{ workloadSummary.total_workload_hours || 0 }} 小时</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card" shadow="hover">
            <div class="card-content">
              <div class="card-icon" style="background-color: #67C23A;">
                <i class="el-icon-s-flag"></i>
              </div>
              <div class="card-info">
                <div class="card-title">参与部门</div>
                <div class="card-value">{{ workloadSummary.total_departments || 0 }} 个</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card" shadow="hover">
            <div class="card-content">
              <div class="card-icon" style="background-color: #E6A23C;">
                <i class="el-icon-s-custom"></i>
              </div>
              <div class="card-info">
                <div class="card-title">参与员工</div>
                <div class="card-value">{{ workloadSummary.total_employees || 0 }} 人</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="summary-card" shadow="hover">
            <div class="card-content">
              <div class="card-icon" style="background-color: #F56C6C;">
                <i class="el-icon-trophy"></i>
              </div>
              <div class="card-info">
                <div class="card-title">最高部门</div>
                <div class="card-value">{{ workloadSummary.top_department || '暂无' }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 查询条件 -->
    <el-form ref="selectForm" :model="selectForm" style="width:50%;" label-width="50%">
      <el-form-item label="部门名称" prop="dname">
        <el-input v-model="selectForm.dname" placeholder="请输入部门名"></el-input>
      </el-form-item>
      <el-form-item label="部门类型" prop="dtype">
        <el-select v-model="selectForm.dtype" placeholder="请选择部门类型">
          <el-option v-for="(item,index) in dtypes" :key="index" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-row :gutter="20" style="width: 100%;">
          <el-col :span="8">
            <el-button type="primary" @click="selectDepartmentsByCon" style="width: 100%;">查询</el-button>
          </el-col>
          <el-col :span="8">
            <el-button @click="resetSearch" style="width: 100%;">重置</el-button>
          </el-col>
          <el-col :span="8">
            <el-button type="success" @click="showWorkloadAnalysis" style="width: 200%;">
              <i class="el-icon-data-analysis"></i> 工作量分析
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
  </div>

  <!-- 工作量柱状图 - 用于可视化展示各部门的工作量数据 -->
  <div class="chart-container" v-if="showChart">
    <el-card shadow="hover">
      <template #header>
        <div class="chart-header">
          <span>部门工作量统计</span>
          <div>
            <!-- 图表类型切换按钮组 - 支持总工作量和本月工作量两种视图 -->
            <el-radio-group v-model="chartType" size="small" @change="loadWorkloadData">
              <el-radio-button label="total">总工作量</el-radio-button>
              <el-radio-button label="monthly">本月工作量</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      <!-- ECharts图表容器 - 用于渲染柱状图 -->
      <div id="workloadChart" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>

  <!-- 部门树状结构 -->
  <div class="box2">
    <el-card shadow="hover">
      <template #header>
        <div class="tree-header">
          <span>部门组织结构</span>
          <div>
            <el-button type="primary" size="small" @click="expandAll">
              <i class="el-icon-s-fold"></i> 全部展开
            </el-button>
            <el-button type="info" size="small" @click="collapseAll">
              <i class="el-icon-s-unfold"></i> 全部折叠
            </el-button>
          </div>
        </div>
      </template>
      
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        :expand-on-click-node="true"
        :default-expand-all="false"
        :highlight-current="true"
        :filter-node-method="filterNode"
        ref="departmentTree">
        <template #default="{ node, data }">
          <div class="tree-node-content">
            <div class="tree-node-info">
              <span 
                class="expand-icon"
                @click="() => toggleNode(node)"
                v-if="node.childNodes && node.childNodes.length > 0"
              >
                {{ node.expanded ? '▼' : '▶' }}
              </span>
              <span class="department-name">{{ data.dname }}</span>
              <span class="department-type">{{ data.dtype }}</span>
              <span class="parent-department" v-if="data.supdepartment1 && data.supdepartment1 !== '顶级部门'" style="color: #909399; font-size: 12px; margin-left: 10px;">
                (上级: {{ data.supdepartment1 }})
              </span>
            </div>
            <div class="tree-node-stats">
              <span class="employee-count">{{ data.employee_count || 0 }}人</span>
              <el-tag :type="getWorkloadType(data.total_workload)" size="small">
                总: {{ data.total_workload || 0 }}h
              </el-tag>
              <el-tag :type="getMonthlyWorkloadType(data.monthly_workload)" size="small">
                本月: {{ data.monthly_workload || 0 }}h
              </el-tag>
            </div>
            <div class="tree-node-actions">
              <el-button size="mini" type="success" @click="handleEdit(null, data, 'update')">
                编辑
              </el-button>
              <el-button size="mini" type="primary" @click="handleEdit(null, data, 'detail')">
                详情
              </el-button>
              <el-button size="mini" type="info" @click="showDepartmentTrend(data)">
                趋势
              </el-button>
              <el-button size="mini" type="danger" @click="handleDelete(null, data)">
                删除
              </el-button>
            </div>
          </div>
        </template>
      </el-tree>
    </el-card>
  </div>

  <!-- 部门趋势分析对话框 -->
  <el-dialog title="部门工作量趋势" v-model="trendDialogVisible" width="60%">
    <div v-if="selectedDepartment">
      <h3>{{ selectedDepartment.dname }} - 近6个月工作量趋势</h3>
      <div id="trendChart" style="width: 100%; height: 300px;"></div>
    </div>
  </el-dialog>

  <!-- 编辑部门对话框 -->
  <el-dialog title="编辑部门" v-model="dialogVisible" width="30%">
    <el-form ref="detailData" :model="detailData" label-width="80px">
      <el-form-item label="部门名称">
        <el-input v-model="detailData.dname" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="部门类型">
        <el-select v-model="detailData.dtype" placeholder="请选择部门类型">
          <el-option v-for="(item,index) in dtypes" :key="index" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="上级部门">
        <el-select v-model="detailData.supdepart_id" placeholder="请选择上级部门">
          <el-option v-for="item in supdepartments" :key="item.id" :label="item.dname" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="update">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 部门详情对话框 -->
  <el-dialog title="部门详情" v-model="dialogVisibleDetail" width="60%">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="basic">
        <el-table :data="[detailData]" border style="width: 100%" size="small">
          <el-table-column prop="id" label="部门ID" width="120"></el-table-column>
          <el-table-column prop="dname" label="部门名称" width="150"></el-table-column>
          <el-table-column prop="dtype" label="部门类型" width="120"></el-table-column>
          <el-table-column label="上级部门" width="150">
            <template #default="scope">
              {{ scope.row.supdepartment1 || (scope.row.parent_id ? '未知部门' : '顶级部门') }}
            </template>
          </el-table-column>
          <el-table-column label="员工数量" width="100">
            <template #default="scope">
              {{ scope.row.employee_count || 0 }} 人
            </template>
          </el-table-column>
          <el-table-column label="总工作量" width="120">
            <template #default="scope">
              {{ scope.row.total_workload || 0 }} 小时
            </template>
          </el-table-column>
          <el-table-column label="本月工作量" width="120">
            <template #default="scope">
              {{ scope.row.monthly_workload || 0 }} 小时
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="岗位信息" name="posts">
        <div v-if="departmentPosts.length > 0">
          <el-table :data="departmentPosts" border style="width: 100%">
            <el-table-column prop="pname" label="岗位名称" width="150"></el-table-column>
            <el-table-column prop="ptype" label="岗位类型" width="120"></el-table-column>
            <el-table-column prop="post_level" label="岗位级别" width="100"></el-table-column>
            <el-table-column prop="employee_count" label="在岗人数" width="100">
              <template #default="scope">
                {{ scope.row.employee_count || 0 }} 人
              </template>
            </el-table-column>
            <el-table-column prop="organization" label="编制人数" width="100">
              <template #default="scope">
                {{ scope.row.organization || 0 }} 人
              </template>
            </el-table-column>
            <el-table-column prop="requirements" label="任职要求" min-width="200">
              <template #default="scope">
                <el-popover
                    placement="top-start"
                    title="任职要求"
                    width="300"
                    trigger="hover"
                    :content="formatRequirements(scope.row.requirements)">
                  <template #reference>
                    <el-button type="text">查看要求</el-button>
                  </template>
                </el-popover>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else style="text-align: center; padding: 20px;">
          <el-empty description="该部门暂无岗位信息"></el-empty>
        </div>
      </el-tab-pane>
    </el-tabs>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisibleDetail = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
/**
 * 部门管理页面
 * 
 * 功能说明：
 * 1. 部门信息查询（支持部门名称/部门类型筛选）
 * 2. 部门信息CRUD（新增/编辑/删除/详情查看）
 * 3. 部门工作量统计（总工作量/参与部门数/参与员工数）
 * 4. 部门工作量可视化（柱状图/趋势图）
 * 5. 部门树状结构展示
 * 
 * 关键数据来源：
 * - /getDepartmentByPage - 分页查询部门列表
 * - /selectDepartmentByCon - 条件查询部门列表
 * - /api/department/workload/summary - 获取工作量汇总
 * - /api/department/workload/stats - 获取工作量统计数据
 */
import NavMain from '@/components/NavMain.vue'
import * as echarts from 'echarts'

export default {
  components: {
    NavMain
  },
  created: function() {
    this.loadWorkloadData()
    this.loadWorkloadSummary()
    this.loadDepartments()
  },
  data() {
    return {
      // 部门类型选项
      dtypes: ['', '技术部门', '技术小组', '职能部门', '职能小组', '业务部门', '业务小组'],
      // 查询条件表单
      selectForm: {
        currentPage: 1,
        pageSize: 10,
        act: '',              // 操作类型：byCon=条件查询
        dname: '',            // 部门名称筛选
        dtype: ''            // 部门类型筛选
      },
      currentPage: 1,         // 当前页码
      tableData: [],          // 部门列表数据
      pageSize: 10,           // 每页显示数量
      total: 0,               // 总记录数
      // 树状结构相关数据
      treeData: [],           // 部门树状结构数据
      treeProps: {
        label: 'dname',
        children: 'children'
      },
      // 工作量相关数据
      workloadSummary: {},    // 工作量汇总（总工时/参与部门数/参与员工数）
      workloadStats: [],      // 工作量统计数据（用于图表）
      showChart: false,       // 是否显示图表
      chartType: 'total',     // 图表类型（total=总工作量，month=本月工作量）
      chart: null,            // 工作量柱状图ECharts实例
      trendChart: null,       // 趋势图ECharts实例
      trendDialogVisible: false,  // 趋势图对话框显示状态
      selectedDepartment: null,    // 选中的部门（用于趋势图）
      // 原有对话框数据保持不变
      dialogVisible: false,
      dialogVisibleDetail: false,
      detailData: {},
      supdepartments: [{ id: '', dname: '' }],
      // 岗位联动相关数据
      activeTab: 'basic',
      departmentPosts: [],
      // 下级部门相关数据
      subDepartments: [],
    }
  },
  mounted() {
    this.initChart()
  },
  methods: {
    /**
     * 初始化ECharts图表实例
     * 在组件挂载后调用，确保DOM元素已渲染完成
     * 使用$nextTick确保DOM更新完成后再初始化图表
     */
    initChart() {
      this.$nextTick(() => {
        const chartDom = document.getElementById('workloadChart')
        if (chartDom) {
          // 初始化ECharts实例
          this.chart = echarts.init(chartDom)
          // 加载数据并渲染图表
          this.loadWorkloadData()
        }
      })
    },

    /**
     * 加载工作量汇总数据
     * 从后端API获取部门工作量的总体统计信息
     * 包括总工时、参与部门数、参与员工数等关键指标
     */
    loadWorkloadSummary() {
      this.$axios
          .get('/getDepartmentWorkloadSummary')
          .then(successResponse => {
            if (successResponse.data) {
              this.workloadSummary = successResponse.data
            } else {
              console.warn('工作量汇总数据格式异常:', successResponse.data)
              this.workloadSummary = {}
            }
          })
          .catch(failResponse => {
            console.error('加载工作量汇总失败:', failResponse)
            this.workloadSummary = {}
          })
    },

    /**
     * 加载工作量数据并渲染图表
     * 获取各部门详细的工作量统计数据
     * 数据包括总工作量、本月工作量、员工数量等
     */
    loadWorkloadData() {
      this.$axios
          .get('/getDepartmentWorkloadStats')
          .then(successResponse => {
            if (successResponse.data && successResponse.data.workloadStats) {
              this.workloadStats = successResponse.data.workloadStats
              this.renderWorkloadChart()
            } else {
              console.warn('工作量统计数据格式异常:', successResponse.data)
              this.workloadStats = []
            }
          })
          .catch(failResponse => {
            console.error('加载工作量数据失败:', failResponse)
            this.workloadStats = []
          })
    },

    /**
     * 渲染工作量柱状图
     * 使用ECharts配置选项创建柱状图
     * 支持总工作量和本月工作量两种视图切换
     * 柱状图颜色根据工作量值动态变化
     */
    renderWorkloadChart() {
      if (!this.chart) return

      // 准备图表数据
      const data = this.workloadStats
      const xData = data.map(item => item.dname)  // X轴：部门名称
      const yData = data.map(item =>
          this.chartType === 'total' ? item.total_workload : item.monthly_workload  // Y轴：工作量值
      )

      // ECharts配置选项
      const option = {
        // 图表标题
        title: {
          text: this.chartType === 'total' ? '部门总工作量统计' : '部门本月工作量统计',
          left: 'center'
        },
        // 提示框配置
        tooltip: {
          trigger: 'axis',
          // 自定义提示框内容，显示详细的部门信息
          formatter: function(params) {
            const item = data[params[0].dataIndex]
            return `
                            ${item.dname}<br/>
                            部门类型: ${item.dtype}<br/>
                            员工数量: ${item.employee_count}人<br/>
                            总工作量: ${item.total_workload}小时<br/>
                            本月工作量: ${item.monthly_workload}小时<br/>
                            平均质量: ${(item.avg_quality_score * 100).toFixed(1)}%
                        `
          }
        },
        // X轴配置：部门名称
        xAxis: {
          type: 'category',
          data: xData,
          axisLabel: {
            rotate: 45  // 标签旋转45度，避免重叠
          }
        },
        // Y轴配置：工作量数值
        yAxis: {
          type: 'value',
          name: '工作量(小时)'
        },
        series: [{
          data: yData,
          type: 'bar',
          itemStyle: {
            color: function(params) {
              const value = params.value
              if (value > 100) return '#67C23A'
              if (value > 50) return '#409EFF'
              if (value > 20) return '#E6A23C'
              return '#F56C6C'
            }
          },
          label: {
            show: true,
            position: 'top'
          }
        }],
        dataZoom: [{
          type: 'inside',
          start: 0,
          end: 100
        }]
      }

      this.chart.setOption(option)
    },

    // 显示部门趋势分析
    showDepartmentTrend(department) {
      this.selectedDepartment = department
      this.trendDialogVisible = true

      this.$nextTick(() => {
        this.loadDepartmentTrend(department.id)
      })
    },

    // 加载部门趋势数据
    loadDepartmentTrend(departmentId) {
      this.$axios
          .get(`/getDepartmentWorkloadTrend?departmentId=${departmentId}`)
          .then(successResponse => {
            this.renderTrendChart(successResponse.data.trendData || [])
          })
          .catch(failResponse => {
            console.error('加载趋势数据失败:', failResponse)
          })
    },

    // 渲染趋势图表
    renderTrendChart(trendData) {
      const trendDom = document.getElementById('trendChart')
      if (!trendDom) return

      this.trendChart = echarts.init(trendDom)
      const months = trendData.map(item => item.month)
      const workloads = trendData.map(item => item.monthly_workload)

      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: months
        },
        yAxis: {
          type: 'value',
          name: '工作量(小时)'
        },
        series: [{
          data: workloads,
          type: 'line',
          smooth: true,
          lineStyle: {
            width: 3
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          }
        }]
      }

      this.trendChart.setOption(option)
    },

    // 显示/隐藏工作量分析
    showWorkloadAnalysis() {
      this.showChart = !this.showChart
      if (this.showChart) {
        this.$nextTick(() => {
          this.initChart()
        })
      }
    },

    // 根据工作量值获取标签类型
    getWorkloadType(workload) {
      if (workload > 100) return 'success'
      if (workload > 50) return 'primary'
      if (workload > 20) return 'warning'
      return 'danger'
    },

    // 根据月工作量值获取标签类型
    getMonthlyWorkloadType(workload) {
      if (workload > 30) return 'success'
      if (workload > 15) return 'primary'
      if (workload > 5) return 'warning'
      return 'danger'
    },

    // 重置查询条件
    resetSearch() {
      this.selectForm.dname = ''
      this.selectForm.dtype = ''
      this.selectForm.act = ''
      this.loadDepartments()
    },

    // 修改原有的加载部门方法，转换为树状结构
    loadDepartments() {
      this.$axios
          .get('/getDepartment')
          .then(successResponse => {
            // 统一转换数据类型，优先使用parent_id，如果没有则使用supdepartment
            // 注意：parent_id为0时表示顶级部门，应该转换为null
            const allDepartments = successResponse.data.map(dept => {
              const parentId = dept.parent_id !== undefined && dept.parent_id !== null ? parseInt(dept.parent_id) : (dept.supdepartment ? parseInt(dept.supdepartment) : null)
              return {
                ...dept,
                id: parseInt(dept.id),
                supdepart_id: parentId && parentId !== 0 ? parentId : null,
                parent_id: parentId && parentId !== 0 ? parentId : null,
                supdepartment: dept.supdepartment ? parseInt(dept.supdepartment) : null
              }
            })
            // 构建树状结构
            this.buildTreeData(allDepartments)
            // 合并工作量数据
            this.mergeWorkloadData()
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status)
          })
    },

    // 切换节点展开状态
    toggleNode(node) {
      // 使用Element Plus树组件的store来切换展开状态
      const store = this.$refs.departmentTree.store
      store.setExpanded(node, !node.expanded)
    },

    // 构建树状结构数据
    buildTreeData(departments) {
      console.log('构建树状结构，部门数据:', departments)
      
      // 数据类型验证
      console.log('数据类型检查:')
      departments.forEach(dept => {
        const parentId = dept.parent_id || dept.supdepart_id || dept.supdepartment
        console.log(`部门 ${dept.dname}: id=${dept.id} (${typeof dept.id}), parent_id=${parentId} (${typeof parentId})`)
      })
      
      // 创建部门映射表
      const departmentMap = {}
      departments.forEach(dept => {
        // 确保使用字符串键，因为JavaScript对象键始终是字符串
        departmentMap[dept.id.toString()] = { ...dept, children: [] }
      })
      
      // 构建树结构，优先使用parent_id，如果没有则使用supdepartment
      const treeData = []
      departments.forEach(dept => {
        // 优先使用parent_id，如果没有则使用supdepart_id或supdepartment
        // 注意：parent_id为0或null时，表示顶级部门
        let parentId = dept.parent_id || dept.supdepart_id || dept.supdepartment
        // 如果parent_id是0，应该被视为顶级部门
        if (parentId === 0) {
          parentId = null
        }
        const parentIdStr = parentId ? parentId.toString() : null
        if (parentIdStr && departmentMap[parentIdStr]) {
          // 如果有上级部门，添加到上级部门的children中
          console.log(`部门 ${dept.dname} (ID: ${dept.id}) 的上级部门ID: ${parentId}`)
          departmentMap[parentIdStr].children.push(departmentMap[dept.id.toString()])
        } else {
          // 如果没有上级部门（parent_id为null、0或不存在），作为根节点
          console.log(`部门 ${dept.dname} (ID: ${dept.id}) 是根节点，parent_id=${parentId}`)
          treeData.push(departmentMap[dept.id.toString()])
        }
      })
      
      console.log('构建完成的树结构:', treeData)
      this.treeData = treeData
    },

    // 合并工作量数据到树状数据
    mergeWorkloadData() {
      if (!this.workloadStats || this.workloadStats.length === 0) {
        console.warn('工作量统计数据为空，无法合并')
        return
      }
      
      const workloadMap = {}
      this.workloadStats.forEach(item => {
        if (item.id) {
          workloadMap[item.id] = item
        }
      })

      const mergeWorkloadToTree = (nodes) => {
        return nodes.map(node => {
          const workloadData = workloadMap[node.id] || {}
          const newNode = {
            ...node,
            total_workload: workloadData.total_workload || 0,
            monthly_workload: workloadData.monthly_workload || 0,
            employee_count: workloadData.employee_count || 0,
            avg_quality_score: workloadData.avg_quality_score || 0
          }
          
          if (node.children && node.children.length > 0) {
            newNode.children = mergeWorkloadToTree(node.children)
          }
          
          return newNode
        })
      }
      
      this.treeData = mergeWorkloadToTree(this.treeData)
    },

    // 树状结构操作方法
    expandAll() {
      const tree = this.$refs.departmentTree
      if (tree) {
        const nodes = tree.store._getAllNodes()
        nodes.forEach(node => {
          node.expanded = true
        })
      }
    },

    collapseAll() {
      const tree = this.$refs.departmentTree
      if (tree) {
        const nodes = tree.store._getAllNodes()
        nodes.forEach(node => {
          node.expanded = false
        })
      }
    },

    filterNode(value, data) {
      if (!value) return true
      return data.dname.toLowerCase().includes(value.toLowerCase()) || 
             data.dtype.toLowerCase().includes(value.toLowerCase())
    },

    // 原有方法保持不变，只添加工作量相关逻辑
    selectDepartmentsByCon() {
      this.selectForm.act = "byCon"
      this.$axios
          .post('/selectDepartmentsByCon', this.selectForm)
          .then(successResponse => {
            const allDepartments = successResponse.data.departs.map(dept => {
              const parentId = dept.parent_id !== undefined && dept.parent_id !== null ? parseInt(dept.parent_id) : (dept.supdepartment ? parseInt(dept.supdepartment) : null)
              return {
                ...dept,
                id: parseInt(dept.id),
                supdepart_id: parentId && parentId !== 0 ? parentId : null,
                parent_id: parentId && parentId !== 0 ? parentId : null,
                supdepartment: dept.supdepartment ? parseInt(dept.supdepartment) : null
              }
            })
            // 构建树状结构
            this.buildTreeData(allDepartments)
            // 合并工作量数据
            this.mergeWorkloadData()
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status, { confirmButtonText: '确定' })
          })
    },

    // 原有其他方法保持不变...
    handleCurrentChange(val) {
      this.currentPage = val
      if (this.selectForm.act === 'byCon') {
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        this.selectDepartmentsByCon()
      } else {
        this.loadDepartments()
      }
    },

    // 处理编辑和详情操作
    handleEdit(index, row, act) {
      this.detailData = { 
        ...row,
        // 确保parent_id和supdepart_id正确设置
        parent_id: row.parent_id || row.supdepart_id || row.supdepartment,
        supdepart_id: row.parent_id || row.supdepart_id || row.supdepartment
      }
      
      // 加载上级部门列表
      this.$axios.get('/getDepartment')
        .then(successResponse => {
          this.supdepartments = successResponse.data.filter(dept => dept.id !== row.id)
          
          // 如果是详情操作，同时加载下级部门列表
          if (act === 'detail') {
            this.loadSubDepartments(row.id, successResponse.data)
          }
        })
        .catch(failResponse => {
          console.error('加载部门列表失败:', failResponse)
          this.$message.error('加载部门列表失败')
        })
      
      if (act === 'update') {
        this.dialogVisible = true
      } else if (act === 'detail') {
        this.dialogVisibleDetail = true
        // 加载该部门的岗位信息
        this.loadDepartmentPosts(row.id)
      }
    },

    // 加载部门岗位信息
    loadDepartmentPosts(departmentId) {
      this.$axios.get(`/getDepartmentPosts?departmentId=${departmentId}`)
        .then(successResponse => {
          if (successResponse.data && successResponse.data.posts) {
            this.departmentPosts = successResponse.data.posts
          } else {
            this.departmentPosts = []
            console.warn('部门岗位数据格式异常:', successResponse.data)
          }
        })
        .catch(failResponse => {
          console.error('加载部门岗位失败:', failResponse)
          this.$message.error('加载部门岗位失败')
          this.departmentPosts = []
        })
    },

    // 加载下级部门列表
    loadSubDepartments(departmentId, allDepartments) {
      // 确保数据类型一致，统一转换为字符串进行比较
      const targetIdStr = departmentId.toString()
      
      // 从所有部门中筛选出当前部门的下级部门
      this.subDepartments = allDepartments.filter(dept => {
        // 统一使用字符串进行比较
        const supIdStr = dept.supdepart_id ? dept.supdepart_id.toString() : null
        return supIdStr === targetIdStr
      })
      
      console.log(`部门 ${departmentId} 的下级部门列表:`, this.subDepartments)
      console.log('所有部门数据:', allDepartments)
      console.log('目标部门ID字符串:', targetIdStr, '类型:', typeof targetIdStr)
    },

    // 查看下级部门详情
    viewSubDepartmentDetail(subDepartment) {
      // 关闭当前详情对话框
      this.dialogVisibleDetail = false
      
      // 延迟打开新的详情对话框，避免冲突
      this.$nextTick(() => {
        // 使用handleEdit方法打开下级部门的详情
        this.handleEdit(null, subDepartment, 'detail')
      })
    },

    // 格式化岗位要求
    formatRequirements(requirementsJson) {
      if (!requirementsJson) return '暂无要求'
      
      try {
        const requirements = JSON.parse(requirementsJson)
        let content = ''
        for (const [key, value] of Object.entries(requirements)) {
          content += `${key}: ${Array.isArray(value) ? value.join(', ') : value}\n`
        }
        return content
      } catch (e) {
        return requirementsJson
      }
    },

    // 处理删除操作
    handleDelete(index, row) {
      this.$confirm('确定要删除该部门吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.post('/deleteDepartment', { id: row.id })
          .then(successResponse => {
            if (successResponse.data === 'ok') {
              this.$message.success('删除成功')
              this.loadDepartments()
            } else {
              this.$message.error('删除失败，该部门可能有关联数据')
            }
          })
          .catch(failResponse => {
            console.error('删除部门失败:', failResponse)
            this.$message.error('删除失败')
          })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    // 取消编辑
    cancel() {
      this.dialogVisible = false
      this.$refs['detailData'].resetFields()
    },

    // 更新部门信息
    update() {
      // 确保数据类型正确，优先使用parent_id
      const parentId = this.detailData.parent_id || this.detailData.supdepart_id
      const updateData = {
        ...this.detailData,
        parent_id: parentId ? parseInt(parentId) : null,
        supdepartment: parentId ? parseInt(parentId) : null  // 同时更新supdepartment以保持兼容
      }
      
      this.$axios.post('/updateDepartment', updateData)
        .then(successResponse => {
          if (successResponse.data === 'ok') {
            this.$message.success('更新成功')
            this.dialogVisible = false
            this.loadDepartments()
          } else {
            this.$message.error('更新失败')
          }
        })
        .catch(failResponse => {
          console.error('更新部门失败:', failResponse)
          this.$message.error('更新失败')
        })
    },

    headClass() {
      return 'text-align: center;background:rgb(242,242,242);color:rgb(140,138,140)'
    }
  },
  beforeUnmount() {
    if (this.chart) {
      this.chart.dispose()
    }
    if (this.trendChart) {
      this.trendChart.dispose()
    }
  }
}
</script>

<style scoped>
.box1 {
  width: 80%;
  height: auto;
  margin-left: 20%;
  padding: 20px;
}

.box2 {
  margin-left: 5px;
  margin-right: 5px;
  margin-top: 20px;
}

.el-select {
  width: 100%
}

.workload-summary {
  margin-bottom: 20px;
}

.summary-card {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 10px;
  text-align: center;
  height: 100%;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.card-icon i {
  font-size: 28px;
  color: white;
}

.card-info {
  width: 100%;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  font-weight: normal;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.chart-container {
  width: 80%;
  margin-left: 20%;
  padding: 0 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.block {
  margin: 20px 0;
  text-align: center;
}

/* 树状结构样式 */
.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.tree-node-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 8px 0;
    margin-left: calc(var(--tree-node-level, 0) * 16px);
  }
  
  .expand-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    cursor: pointer;
    margin-right: 4px;
    font-size: 12px;
    color: #666;
    user-select: none;
  }
  
  .expand-icon:hover {
    color: #409eff;
  }

.tree-node-info {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 200px;
}

.department-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  margin-bottom: 2px;
}

.department-type {
  font-size: 12px;
  color: #909399;
  background-color: #f0f2f5;
  padding: 2px 6px;
  border-radius: 3px;
  align-self: flex-start;
}

.tree-node-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 15px;
}

.employee-count {
  font-size: 12px;
  color: #606266;
  background-color: #f0f2f5;
  padding: 4px 8px;
  border-radius: 4px;
}

.tree-node-actions {
  display: flex;
  gap: 5px;
}

/* 树节点悬停效果 */
.el-tree :deep(.el-tree-node__content) {
  height: auto;
  min-height: 50px;
  padding: 5px 0;
}

.el-tree :deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

.el-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .tree-node-content {
    flex-wrap: wrap;
  }
  
  .tree-node-stats {
    margin: 8px 0;
    order: 3;
    width: 100%;
    justify-content: flex-start;
  }
  
  .tree-node-actions {
    order: 2;
  }
}

@media (max-width: 768px) {
  .tree-node-info {
    min-width: 150px;
  }
}

/* 下级部门列表样式 */
.sub-departments-section {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
}

.sub-departments-section h4 {
  margin-bottom: 15px;
  color: #303133;
  font-weight: 600;
  font-size: 16px;
}

.sub-departments-section .el-table {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.sub-departments-section .el-table :deep(.el-table__header) {
  background-color: #f5f7fa;
}

.sub-departments-section .el-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 响应式设计 - 移动端适配 */
@media (max-width: 768px) {
  .tree-node-stats {
    flex-wrap: wrap;
    gap: 4px;
  }
  
  .tree-node-actions {
    flex-wrap: wrap;
    gap: 2px;
  }
  
  .tree-node-actions .el-button {
    margin-bottom: 2px;
  }
}
</style>