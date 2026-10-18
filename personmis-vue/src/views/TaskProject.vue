<template>
    <NavMain></NavMain>
<div class="box1">
<br>
<el-form ref="selectForm" :model="selectForm" style="width:50%;"  label-width="50%" > 
  <el-form-item label="项目ID"  prop="projectId">
    <el-input v-model.number="selectForm.projectId"  placeholder="请输入项目ID"></el-input>
  </el-form-item>
  <el-form-item label="项目名称"  prop="projectName">
    <el-input v-model="selectForm.projectName"  placeholder="请输入项目名称"></el-input>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="selectTaskProjectsByCon">查询</el-button>
    <el-button type="success" @click="goToAdd">新增项目</el-button>
  </el-form-item>
</el-form>
</div>
<div class="box2">
  <el-table
    :data="tableData"  :header-cell-style="headClass" 
     :cell-style="{ textAlign: 'center' }"
    :default-sort = "{prop: 'id', order: 'descending'}">
    <el-table-column prop="id" label="ID" sortable></el-table-column>
    <el-table-column prop="projectName" label="项目名称"></el-table-column>
    <el-table-column prop="startDate" label="开始日期" :formatter="formatDate"></el-table-column>
    <el-table-column prop="endDate" label="结束日期" :formatter="formatDate"></el-table-column>
    <el-table-column prop="budgetedHours" label="预算工时" sortable></el-table-column>
    <el-table-column label="操作" width="340">
      <template #default="scope">
        <div class="action-group">
          <el-button size="mini" type="primary" plain @click="goToWorkloadDetail(scope.row.id)">填报</el-button>
          <el-button size="mini" type="success" plain @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="mini" class="delete-btn" @click="handleDelete(scope.$index, scope.row)">删除</el-button>

          <el-dropdown class="action-more" trigger="click">
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="() => showGantt(scope.row)">查看进度</el-dropdown-item>
                <el-dropdown-item @click="() => optimizeAllocation(scope.row)">人员分配</el-dropdown-item>
                <el-dropdown-item @click="() => showPerformance(scope.row)">绩效</el-dropdown-item>
              </el-dropdown-menu>
            </template>
            <el-button size="mini" type="info" plain>
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
          </el-dropdown>
        </div>
      </template>
    </el-table-column>
  </el-table>
  </div>

 <div class="block">
    <el-pagination
      @current-change="handleCurrentChange"
      v-model:currentPage="currentPage"
      :page-size="pageSize"
      layout="total, prev, pager, next"
      :total="total">
    </el-pagination>
  </div>
  <el-dialog title="项目修改" v-model="taskProjectDialogVisible"  width="40%">
   <el-form ref="updateForm" :model="updateForm"  style="width:80%;"  label-width="40%"  >
  <el-form-item label="项目名称"  prop="projectName">
    <el-input v-model="updateForm.projectName"></el-input>
  </el-form-item>
  <el-form-item label="开始日期"  prop="startDate">
    <el-date-picker v-model="updateForm.startDate" type="date" placeholder="选择开始日期" style="width:100%"></el-date-picker>
  </el-form-item>
  <el-form-item label="结束日期"  prop="endDate">
    <el-date-picker v-model="updateForm.endDate" type="date" placeholder="选择结束日期" style="width:100%"></el-date-picker>
  </el-form-item>
  <el-form-item label="预算工时"  prop="budgetedHours">
    <el-input-number v-model="updateForm.budgetedHours" :precision="2" :min="0" style="width:100%"></el-input-number>
  </el-form-item>
  <el-form-item label="所需技能" prop="requiredSkills">
    <el-input v-model="updateForm.requiredSkills" placeholder="请输入技能标签，用逗号分隔" style="width:100%"></el-input>
  </el-form-item>
  <el-form-item label="最大人员数" prop="maxStaffCount">
    <el-input-number v-model="updateForm.maxStaffCount" :min="1" :max="50" style="width:100%"></el-input-number>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="update(updateForm)" :loading="loadingbut">{{loadingbuttext}}</el-button>
    <el-button type="danger" @click="cancel">重置</el-button>
  </el-form-item>
</el-form>
</el-dialog>

<el-dialog title="人员分配优化方案" v-model="allocationDialogVisible" width="70%">
  <div v-if="allocationResult">
    <el-descriptions :column="2" border style="margin-bottom: 20px;">
      <el-descriptions-item label="项目名称">{{ allocationResult.projectName }}</el-descriptions-item>
      <el-descriptions-item label="项目ID">{{ allocationResult.project_id }}</el-descriptions-item>
      <el-descriptions-item label="预算工时">{{ allocationResult.budgeted_hours }} 小时</el-descriptions-item>
      <el-descriptions-item label="已分配工时">{{ allocationResult.total_allocated_hours }} 小时</el-descriptions-item>
      <el-descriptions-item label="工时利用率">{{ allocationResult.utilization_rate }}%</el-descriptions-item>
      <el-descriptions-item label="分配人数">{{ allocationResult.total_staff }} 人</el-descriptions-item>
    </el-descriptions>

    <h3>分配方案</h3>
    <el-table :data="allocationResult.allocations" border style="width: 100%">
      <el-table-column prop="staff_id" label="员工ID" width="80"></el-table-column>
      <el-table-column label="员工姓名" width="120">
        <template #default="scope">
          {{ getEmployeeName(scope.row.staff_id) }}
        </template>
      </el-table-column>
      <el-table-column prop="allocated_hours" label="分配工时(小时)" width="120"></el-table-column>
      <el-table-column prop="skill_match" label="技能匹配度" width="110">
        <template #default="scope">
          <el-tag :type="scope.row.skill_match >= 0.8 ? 'success' : scope.row.skill_match >= 0.5 ? 'warning' : 'danger'">
            {{ (scope.row.skill_match * 100).toFixed(0) }}%
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quality_score" label="质量评分" width="100"></el-table-column>
      <el-table-column prop="ability_score" label="综合能力" width="100"></el-table-column>
      <el-table-column prop="allocation_score" label="分配评分" width="100">
        <template #default="scope">
          <el-tag type="success">{{ (scope.row.allocation_score * 100).toFixed(0) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <template #footer>
    <el-button @click="allocationDialogVisible = false">关闭</el-button>
  </template>
</el-dialog>

<!-- 项目进度甘特图对话框 -->
<el-dialog title="项目进度甘特图" v-model="ganttDialogVisible" width="85%">
  <div v-if="currentProject">
    <el-descriptions :column="3" border size="small" style="margin-bottom: 20px;">
      <el-descriptions-item label="项目名称">{{ currentProject.projectName }}</el-descriptions-item>
      <el-descriptions-item label="开始日期">{{ formatDate(null, null, currentProject.startDate) }}</el-descriptions-item>
      <el-descriptions-item label="结束日期">{{ formatDate(null, null, currentProject.endDate) }}</el-descriptions-item>
    </el-descriptions>
  </div>
  <div id="gantt-chart" style="width: 100%; height: 500px;" v-loading="ganttLoading"></div>
  <template #footer>
    <el-button @click="ganttDialogVisible = false">关闭</el-button>
  </template>
</el-dialog>

<!-- 项目绩效对话框 -->
<el-dialog title="项目绩效" v-model="performanceDialogVisible" width="80%">
  <div v-if="currentProject">
    <el-descriptions :column="2" border style="margin-bottom: 20px;">
      <el-descriptions-item label="项目名称">{{ currentProject.projectName }}</el-descriptions-item>
      <el-descriptions-item label="项目ID">{{ currentProject.id }}</el-descriptions-item>
    </el-descriptions>
  </div>
  <el-table :data="performanceData" border style="width: 100%" :default-sort="{prop: 'eci', order: 'descending'}">
      <el-table-column prop="staffId" label="员工ID" width="80"></el-table-column>
      <el-table-column prop="staffName" label="员工名称" width="120"></el-table-column>
      <el-table-column prop="position" label="员工岗位" width="150"></el-table-column>
      <el-table-column prop="hours" label="工时" width="100"></el-table-column>
      <el-table-column prop="eci" label="ECI综合效率" width="150" sortable>
        <template #default="scope">
          <el-tag type="success">{{ scope.row.eci !== undefined && !isNaN(scope.row.eci) ? scope.row.eci.toFixed(2) : '0.00' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  <template #footer>
    <el-button @click="performanceDialogVisible = false">关闭</el-button>
  </template>
</el-dialog>
</template>
<script>
/**
 * 项目管理页面
 * 
 * 功能说明：
 * 1. 项目信息查询（支持项目ID/项目名称筛选）
 * 2. 项目信息CRUD（新增/编辑/删除）
 * 3. 工作量填报跳转（跳转到工作量填报页面）
 * 4. 项目进度查看（甘特图展示）
 * 5. 人员分配优化（调用Python服务优化人员分配）
 * 6. 项目绩效分析（展示项目绩效数据）
 * 
 * 关键数据来源：
 * - /getTaskProjectByPage - 分页查询项目列表
 * - /selectTaskProjectByCon - 条件查询项目列表
 * - /api/project/gantt - 获取甘特图数据
 * - /api/project/optimize-allocation - 人员分配优化
 * - /api/project/performance - 获取项目绩效数据
 */
import NavMain from '@/components/NavMain.vue'
import * as echarts from 'echarts'

export default {
  components:{
    NavMain
  },
  created: function () {
    this.loadTaskProjects()
  },
  data () {
     return {
        // 查询条件表单
        selectForm: {
          projectId: null,        // 项目ID筛选
          projectName: '',        // 项目名称筛选
          currentPage: 1,
          pageSize: 10,
          act: ''                 // 操作类型：byCon=条件查询
        },
        currentPage: 1,           // 当前页码
        tableData: [],            // 项目列表数据
        pageSize: 10,             // 每页显示数量
        total: 0,                 // 总记录数
        // 编辑表单数据
        updateForm: {},
        loadingbut: false, 
        loadingbuttext: '修改',
        taskProjectDialogVisible: false,  // 编辑对话框显示状态
        // 人员分配优化相关
        allocationDialogVisible: false,    // 分配优化对话框显示状态
        allocationResult: null,           // 分配优化结果
        optimizing: false,                // 优化中状态
        // 甘特图相关
        ganttDialogVisible: false,        // 甘特图对话框显示状态
        ganttChart: null,                 // 甘特图ECharts实例
        ganttData: [],                    // 甘特图数据
        ganttLoading: false,              // 甘特图加载状态
        currentProject: null,             // 当前查看的项目
        // 绩效相关
        performanceDialogVisible: false,  // 绩效对话框显示状态
        performanceData: []               // 绩效数据
      }
  },
  methods: {
    /**
     * 加载项目列表（分页查询）
     * 页面初始化时调用，获取分页数据
     */
    loadTaskProjects (){
      this.$axios
          .get('/getTaskProjectByPage?currentPage=' + this.currentPage + '&pageSize=' + this.pageSize)
          .then(successResponse => {
              console.log('API Response:', successResponse.data)
              console.log('Task Projects:', successResponse.data.taskProjects)
              this.tableData = successResponse.data.taskProjects
              this.total = successResponse.data.total
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status)
          })
    },
    /**
     * 条件查询项目列表
     * 根据查询条件（项目ID/项目名称）获取项目列表
     */
    selectTaskProjectsByCon(){
        this.selectForm.act = "byCon"
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        this.$axios
          .post('/selectTaskProjectByCon', this.selectForm)//直接提交表单
          .then(successResponse => {
             this.tableData = successResponse.data.taskProjects
             this.total = successResponse.data.total
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status, {confirmButtonText: '确定' })
          })
    },
     // 表头样式设置
    headClass () {
        return 'text-align: center;background:rgb(242,242,242);color:rgb(140,138,140)'
    },
    //页码变化
    handleCurrentChange(val) {
      this.currentPage = val
      if(this.selectForm.act === 'byCon'){
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        this.selectTaskProjectsByCon()
      }else{
         this.loadTaskProjects()
      }
    },
    handleEdit(index, row) {
      this.$axios
          .get('/getTaskProjectDetail?id=' + row.id )
          .then(successResponse => {
              this.updateForm = successResponse.data
              this.taskProjectDialogVisible = true
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status)
          })
    },
    handleDelete(index, row) {
      console.log(index, row);
      this.$confirm('删除项目, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$axios
          .post('/deleteTaskProject?id=' + row.id)
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
              //删除成功后重新加载
              this.loadTaskProjects()
            }else {
              this.$alert('不能删除有关联数据！', {confirmButtonText: '确定' })
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
    update(){
        this.loadingbut = true;
        this.loadingbuttext = '修改中...';
        this.$axios
          .post('/updateTaskProject', this.updateForm)//直接提交表单
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$alert('修改成功', {confirmButtonText: '确定' })
              this.taskProjectDialogVisible = false
              this.loadTaskProjects()
            }else {
              this.$alert('修改失败', {confirmButtonText: '确定' })
            }
            this.loadingbut = false;
            this.loadingbuttext = '修改';
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status, {confirmButtonText: '确定' })
          })
    },
    cancel(){
           this.$refs['updateForm'].resetFields()
    },
    goToAdd(){
      this.$router.push('/addTaskProject')
    },
    goToWorkloadDetail(projectId){
      this.$router.push({path: '/workloadDetail', query: {projectId: projectId}})
    },
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
    showPerformance(project) {
      this.currentProject = project
      
      // 先获取绩效数据，成功后再显示对话框
      const url = '/getProjectPerformance?projectId=' + project.id
      console.log('API请求URL:', url)
      this.$axios
        .get(url)
        .then(successResponse => {
          console.log('API响应:', successResponse)
          this.performanceData = successResponse.data
          // 确保按ECI降序排序
          this.performanceData.sort((a, b) => b.eci - a.eci)
          // 数据加载成功后显示对话框
          this.performanceDialogVisible = true
        })
        .catch(error => {
          console.log('API错误:', error)
          this.$alert('获取绩效数据失败', {confirmButtonText: '确定'})
        })
    },
    
    async optimizeAllocation(project) {
      alert('开始人员分配！如果看到这个提示说明代码正在执行')
      console.log('=== 步骤1：开始人员分配 ===')
      console.log('项目信息:', project)
      this.optimizing = true
      
      const axios = require('axios')
      
      console.log('=== 步骤2：准备调用后端接口 ===')
      console.log('接口地址: /getEmpImageByPage?currentPage=1&pageSize=1000&image_type=能力画像')
      console.log('注意：只查询"能力画像"类型，因为只有能力画像才有workload_ability字段')
      
      // 只查询"能力画像"，因为workload_ability字段只有能力画像才有值
      this.$axios.get('/getEmpImageByPage?currentPage=1&pageSize=1000&image_type=能力画像')
        .then(async response => {
          console.log('=== 步骤3：后端接口调用成功 ===')
          console.log('完整响应:', response)
          console.log('响应数据:', response.data)
          
          const d = response.data
          let empImages = d.empImages || d.images || (Array.isArray(d) ? d : [])
          
          // 调试：先打印原始数据
          console.log('=== 调试：原始画像数据 ===')
          console.log('原始empImages数量:', empImages.length)
          console.log('前3条数据:', empImages.slice(0, 3))
          console.log('staff_id字段检查:', empImages.map(e => ({
            id: e.id,
            staff_id: e.staff_id,
            staffId: e.staffId,
            sname: e.sname
          })).slice(0, 5))
          
          // 简单去重：因为已经只查询"能力画像"，理论上每个员工只有一条记录
          const uniqueEmpImages = []
          const staffIdSet = new Set()
          
          empImages.forEach((emp, idx) => {
            const staffId = emp.staff_id || emp.staffId
            console.log(`处理第${idx+1}/${empImages.length}条:`, {
              id: emp.id,
              staff_id: staffId,
              image_type: emp.image_type,
              ability_score: emp.ability_score,
              workload_ability: emp.workload_ability
            })
            
            if (staffId == null || staffId === 0) {
              console.log(`  ❌ 跳过: staff_id为空`)
              return
            }
            
            // 确保使用统一的staff_id字段
            if (!emp.staff_id && emp.staffId) {
              emp.staff_id = emp.staffId
            }
            
            // 去重：每个员工只保留第一条记录
            if (!staffIdSet.has(staffId)) {
              console.log(`  ✅ 添加画像: staff_id=${staffId}, type=${emp.image_type}`)
              staffIdSet.add(staffId)
              uniqueEmpImages.push(emp)
            } else {
              console.log(`  ⏭️  跳过: 已有员工${staffId}的画像`)
            }
          })
          
          empImages = uniqueEmpImages
          
          console.log(`=== 去重完成 ===`)
          console.log(`原始${d.empImages?.length || 0}条 -> 去重后${empImages.length}条`)
          console.log('去重后的员工ID列表:', Array.from(staffIdSet))
          
          if (!empImages || empImages.length === 0) {
            this.$alert('没有可用的员工画像数据，请先进行员工画像分析', {confirmButtonText: '确定'})
            this.optimizing = false
            return
          }
          
          // 获取员工基础信息（含手动技能标签）
          let staffSkillMap = {}
          try {
            const staffRes = await this.$axios.get('/getStaffByPage?currentPage=1&pageSize=1000')
            const staffs = staffRes.data.staffs || []
            staffs.forEach(s => {
              staffSkillMap[s.id] = s.skill_tags || s.skillTags || ''
            })
          } catch (e) {
            staffSkillMap = {}
          }

          // 调试：显示原始emp对象的能力字段
          console.log('\n=== 检查emp对象的能力字段 ===')
          empImages.forEach(emp => {
            console.log(`员工${emp.staff_id}:`, {
              ability_score: emp.ability_score,
              workload_ability: emp.workload_ability,
              quality_ability: emp.quality_ability,
              '是否为null/undefined': {
                ability_score: emp.ability_score == null,
                workload_ability: emp.workload_ability == null,
                quality_ability: emp.quality_ability == null
              }
            })
          })
          
          console.log('\n=== 检查后端返回的原始JSON数据 ===')
          empImages.forEach(emp => {
            console.log(`员工${emp.staff_id} 原始数据:`)
            console.log('  - 所有字段:', Object.keys(emp))
            console.log('  - ability_score:', emp.ability_score, typeof emp.ability_score)
            console.log('  - workload_ability:', emp.workload_ability, typeof emp.workload_ability)
            console.log('  - quality_ability:', emp.quality_ability, typeof emp.quality_ability)
            console.log('  - image_type:', emp.image_type)
          })

          const workloadPromises = empImages.map(emp => {
            // 1) 优先使用画像中的能力值，仅在null/undefined时置0
            const qualityAbilityRaw = (emp.quality_ability !== null && emp.quality_ability !== undefined) ? emp.quality_ability : 0
            const workloadAbilityRaw = (emp.workload_ability !== null && emp.workload_ability !== undefined) ? emp.workload_ability : 0
            const abilityScoreRaw = (emp.ability_score !== null && emp.ability_score !== undefined) ? emp.ability_score : 0

            return this.$axios.get('/getWorkloadStatsByStaffId?staffId=' + emp.staff_id)
              .then(res => {
                const stats = res.data || {}
                const totalHours = stats.total_hours || 0
                const totalRecords = stats.total_records || 0
                const avgTaskWeight = stats.avg_task_weight || 0
                const avgQualityScore = stats.avg_quality_score || 0.8

                // 2) 计算工作量能力的兜底值（与后端算法一致的简化版）
                const fallbackWorkload = Math.min(
                  100,
                  Math.min(50, (totalHours / 160) * 50) + Math.min(50, (totalRecords / 30) * 50)
                )

                // 3) 质量能力兜底：如果画像为0，用平均质量得分*100
                const fallbackQuality = qualityAbilityRaw > 0 ? qualityAbilityRaw : Math.round(avgQualityScore * 10000) / 100

                // 4) 使用画像值；若画像值为0则用兜底
                const workload_ability = workloadAbilityRaw > 0 ? workloadAbilityRaw : Math.round(fallbackWorkload * 100) / 100
                const quality_ability = fallbackQuality

                // 5) 综合能力兜底：如果画像综合能力为0，用 workload*0.6 + quality*0.4
                const ability_score = abilityScoreRaw > 0
                  ? abilityScoreRaw
                  : Math.round((workload_ability * 0.6 + quality_ability * 0.4) * 100) / 100

                console.log(`员工${emp.staff_id} 最终能力:`, {
                  ability_score,
                  workload_ability,
                  quality_ability,
                  来源: abilityScoreRaw > 0 ? '画像' : '兜底计算'
                })

                const skillTags = staffSkillMap[emp.staff_id] || emp.skill_tags || emp.skillTags || ''

                return {
                  staff_id: emp.staff_id,
                  sname: emp.sname,
                  skill_tags: skillTags,
                  quality_ability,
                  workload_ability,
                  ability_score,
                  avg_quality_score: avgQualityScore,
                  total_hours: totalHours,
                  total_records: totalRecords,
                  avg_task_weight: avgTaskWeight
                }
              })
              .catch(() => ({
                staff_id: emp.staff_id,
                sname: emp.sname,
                skill_tags: staffSkillMap[emp.staff_id] || emp.skill_tags || emp.skillTags || '',
                quality_ability: qualityAbilityRaw,
                workload_ability: workloadAbilityRaw,
                ability_score: abilityScoreRaw,
                avg_quality_score: 0.8
              }))
          })
          
          const employeesData = await Promise.all(workloadPromises)
          
          // 调试：打印员工数据
          console.log('\n=== 传递给Python的员工数据 ===')
          console.log('员工数量:', employeesData.length)
          employeesData.forEach(emp => {
            console.log(`员工${emp.staff_id}:`, {
              ability_score: emp.ability_score,
              workload_ability: emp.workload_ability,
              quality_ability: emp.quality_ability,
              skill_tags: emp.skill_tags
            })
          })
          
          const projectData = {
            project_id: project.id,
            required_skills: project.requiredSkills || '',
            budgeted_hours: project.budgetedHours || 0,
            max_staff_count: project.maxStaffCount || 10
          }
          
          axios.post('http://localhost:5000/api/allocation/optimize', {
            project_data: projectData,
            employees_data: employeesData
          })
          .then(response => {
            this.optimizing = false
            console.log('Python返回的完整数据:', response.data)
            console.log('分配结果:', response.data.data?.allocations)
            
            if (response.data.code === 200) {
              this.allocationResult = {
                ...response.data.data,
                projectName: project.projectName,
                employeesData: employeesData
              }
              this.allocationDialogVisible = true
            } else {
              this.$alert(response.data.message || '优化失败', {confirmButtonText: '确定'})
            }
          })
          .catch(error => {
            this.optimizing = false
            this.$alert('优化服务调用失败: ' + (error.message || '请确保Python服务已启动'), {confirmButtonText: '确定'})
          })
        })
        .catch(error => {
          console.error('=== 错误：接口调用失败 ===')
          console.error('错误详情:', error)
          console.error('错误响应:', error.response)
          this.optimizing = false
          this.$alert('获取员工画像数据失败: ' + error.message, {confirmButtonText: '确定'})
        })
    },
    getEmployeeName(staffId) {
      if (!this.allocationResult || !this.allocationResult.employeesData) return ''
      const emp = this.allocationResult.employeesData.find(e => e.staff_id === staffId)
      return emp ? emp.sname : ''
    },
    showGantt(project) {
      this.currentProject = project
      this.ganttDialogVisible = true
      // 等待对话框和DOM元素渲染完成后再初始化图表
      this.$nextTick(() => {
        this.initGanttChart()
        this.loadGanttData(project.id)
      })
    },
    initGanttChart() {
      const chartDom = document.getElementById('gantt-chart')
      if (chartDom) {
        if (this.ganttChart) {
          this.ganttChart.dispose()
        }
        this.ganttChart = echarts.init(chartDom)
      }
    },
    loadGanttData(projectId) {
      this.ganttLoading = true
      this.$axios
        .get('/api/project/gantt-data?projectId=' + projectId)
        .then(response => {
          this.ganttData = response.data || []
          this.renderGantt()
          this.ganttLoading = false
        })
        .catch(error => {
          console.error('加载甘特图数据失败:', error)
          this.ganttLoading = false
          this.$message.warning('暂无项目进度数据')
        })
    },
    renderGantt() {
      if (!this.ganttChart) return
      
      if (!this.ganttData || this.ganttData.length === 0) {
        this.ganttChart.setOption({
          title: {
            text: '暂无工作量数据',
            left: 'center',
            top: 'middle',
            textStyle: {
              color: '#999',
              fontSize: 16
            }
          }
        })
        return
      }

      // 后端已经按任务分组，直接使用返回的数据
      if (this.ganttData.length === 0) {
        this.ganttChart.setOption({
          title: {
            text: '暂无任务数据',
            left: 'center',
            top: 'middle',
            textStyle: {
              color: '#999',
              fontSize: 16
            }
          }
        })
        return
      }

      // 构建ECharts数据 - 显示每个员工用时最久的主要工作任务
      const tasks = []
      const series = []
      let yIndex = 0

      this.ganttData.forEach(item => {
        if (item.task_name && item.start_date && item.end_date) {
          // Y轴显示：员工姓名 - 主要任务名称
          const displayName = item.staff_name && item.task_desc 
            ? `${item.staff_name} - ${item.task_desc}` 
            : item.task_name
          tasks.push(displayName)
          series.push({
            name: displayName,
            value: [
              yIndex,
              new Date(item.start_date).getTime(),
              new Date(item.end_date).getTime(),
              item.hours || 0,
              item.staff_name || '',
              item.task_desc || '',
              item.task_count || 0
            ],
            itemStyle: {
              color: '#5470c6',
              borderRadius: 4
            }
          })
          yIndex++
        }
      })

      const option = {
        tooltip: {
          formatter: function (params) {
            const start = new Date(params.value[1]).toLocaleDateString()
            const end = new Date(params.value[2]).toLocaleDateString()
            const hours = params.value[3] ? params.value[3].toFixed(1) + '小时' : '0小时'
            const staff = params.value[4] ? params.value[4] : '未知'
            const taskDesc = params.value[5] ? params.value[5] : '未知任务'
            const taskCount = params.value[6] || 0
            return `<div style="font-weight: bold; margin-bottom: 8px; font-size: 14px;">${staff} - 主要工作任务</div>
                    <div style="margin-top: 8px; line-height: 1.8;">
                      <div>📋 任务: ${taskDesc}</div>
                      <div>📊 任务记录数: ${taskCount}条</div>
                      <div>📅 时间范围: ${start} 至 ${end}</div>
                      <div>⏱️ 总工时: ${hours}</div>
                    </div>`
          },
          backgroundColor: 'rgba(50, 50, 50, 0.95)',
          borderColor: '#5470c6',
          borderWidth: 2,
          textStyle: {
            color: '#fff',
            fontSize: 13
          },
          padding: [12, 16]
        },
        title: {
          text: this.currentProject ? this.currentProject.projectName + ' - 员工主要工作任务' : '员工主要工作任务',
          left: 'center',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        dataZoom: [
          {
            type: 'slider',
            yAxisIndex: 0,
            filterMode: 'empty',
            width: 25,
            right: 10,
            start: 0,
            end: 100,
            handleSize: '100%'
          },
          {
            type: 'slider',
            xAxisIndex: 0,
            filterMode: 'weakFilter',
            height: 20,
            bottom: 10,
            start: 0,
            end: 100
          }
        ],
        xAxis: {
          type: 'time',
          scale: true,
          boundaryGap: false,
          axisLabel: {
            formatter: function (val) {
              return new Date(val).toLocaleDateString()
            },
            rotate: 30
          }
        },
        yAxis: {
          type: 'category',
          data: tasks,
          axisLabel: {
            fontSize: 13,
            width: 200,
            overflow: 'truncate',
            ellipsis: '...',
            interval: 0,  // 显示所有标签
            fontWeight: 'bold'
          },
          axisLine: {
            lineStyle: {
              color: '#5470c6',
              width: 2
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: '#e0e0e0',
              type: 'solid',
              width: 1
            }
          }
        },
        grid: {
          left: '15%',  // 增加左侧空间以显示完整的员工-任务名称
          right: 60,
          top: 80,
          bottom: 50,
          containLabel: true
        },
        series: [{
          type: 'custom',
          renderItem: function (params, api) {
            const categoryIndex = api.value(0)
            const start = api.coord([api.value(1), categoryIndex])
            const end = api.coord([api.value(2), categoryIndex])
            const height = api.size([0, 1])[1] * 0.7

            return {
              type: 'rect',
              shape: {
                x: start[0],
                y: start[1] - height / 2,
                width: Math.max(end[0] - start[0], 2), // 确保至少2px宽度
                height: height
              },
              style: api.style()
            }
          },
          encode: {
            x: [1, 2],
            y: 0
          },
          data: series
        }]
      }

      this.ganttChart.setOption(option)
    }
  }
} 
</script>
<style scoped>
  .box1{
      width: 80%;
      height: 200px;
      margin-left: 20%;
    }
  .box2{
    margin-left: 5px;
    margin-right: 5px;
   }
  .el-select{
    width:100%
  }
  .action-group{
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }
  .action-group .el-button{
    padding: 4px 8px;
    border-radius: 16px;
  }
  .action-group .delete-btn{
    background: #f56c6c;
    border-color: #f56c6c;
    color: #fff;
  }
  .action-more .el-button{
    padding-right: 6px;
  }
</style>

