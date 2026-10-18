<template>
    <NavMain></NavMain>
<div class="box1">
<br>
<el-form ref="selectForm" :model="selectForm" style="width:50%;"  label-width="50%" > 
 <el-form-item label="画像类型" prop="image_type">
    <el-select v-model="selectForm.image_type" placeholder="请选择画像类型">
      <el-option v-for="(item,index) in imageTypes" :key="index" :label="item" :value="item"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="selectEmpImagesByCon">查询</el-button>
  </el-form-item>
</el-form>
</div>
<div class="box2">
  <div class="table-wrapper">
  <el-table
      height="520"
      :data="tableData"
      :header-cell-style="headClass"
     :cell-style="{ textAlign: 'center' }"
      :default-sort = "{prop: 'ability_score', order: 'descending'}"
      :row-class-name="tableRowClassName">
      <el-table-column prop="staff_id" label="员工ID" sortable width="90"></el-table-column>
      <el-table-column prop="sname" label="员工姓名" width="120"></el-table-column>
      <el-table-column prop="image_type" label="画像类型" width="140">
        <template #default="scope">
          <el-tag 
            :type="getImageTypeTag(scope.row.image_type)" 
            effect="dark"
            size="large">
            {{ scope.row.image_type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ability_score" label="画像得分" sortable width="100">
        <template #default="scope">
          <strong style="font-size: 16px; color: #409EFF;">{{ scope.row.ability_score ?? '-' }}</strong>
        </template>
      </el-table-column>
      <el-table-column prop="skill_tags" label="技能标签" width="260">
        <template #default="scope">
          <el-tag v-for="tag in (scope.row.skill_tags || '').split(',').filter(t=>t)" :key="tag" size="small" style="margin:2px;">
            {{ tag }}
          </el-tag>
          <span v-if="!(scope.row.skill_tags || '').split(',').filter(t=>t).length">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="work_style" label="工作风格" width="220">
        <template #default="scope">
          {{ scope.row.work_style || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="created_time1" label="创建时间" width="220" show-overflow-tooltip>
        <template #default="scope">
          {{ scope.row.created_time1 || scope.row.created_time || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="300">
      <template #default="scope">
        <el-button
            size="small" type="warning"
          @click="handleAnalyze(scope.$index, scope.row)">画像分析</el-button>
          <el-button
            size="small" type="info"
          @click="handleViewWorkload(scope.$index, scope.row)">工作量明细</el-button>
        <el-button
            size="small" type="danger"
          @click="handleDelete(scope.$index, scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
  </div>
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
      <template v-if="false">
  <el-dialog title="画像修改" v-model="empImageDialogVisible"  width="90%" v-loading="loadingData">
    <!-- 能力维度编辑卡片 - 根据画像类型动态显示 -->
    <el-card shadow="hover" style="margin-bottom: 20px; background: #f5f7fa;">
      <template #header>
        <span style="font-size: 16px; font-weight: bold; color: #409EFF;">
          <i class="el-icon-data-analysis"></i> {{ editForm.image_type || '画像' }}能力设置
        </span>
      </template>
      <el-row :gutter="15">
        <!-- 能力画像 - 显示技术、工作量、稳定性 -->
        <template v-if="editForm.image_type === '能力画像'">
          <el-col :span="8">
            <el-form-item label="技术能力" label-width="100px">
              <el-input-number v-model="editForm.tech_ability" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="工作量能力" label-width="100px">
              <el-input-number v-model="editForm.workload_ability" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="稳定性能力" label-width="100px">
              <el-input-number v-model="editForm.stability_ability" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </template>
        
        <!-- 绩效画像 - 显示质量、贡献、绩效趋势 -->
        <template v-else-if="editForm.image_type === '绩效画像'">
          <el-col :span="8">
            <el-form-item label="质量能力" label-width="100px">
              <el-input-number v-model="editForm.quality_ability" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="综合贡献能力" label-width="100px">
              <el-input-number v-model="editForm.contribution_ability" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="绩效趋势" label-width="100px">
              <el-input-number v-model="editForm.performance_trend" :min="-100" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </template>
        
        <!-- 潜力画像 - 显示学习能力、成长速度、激励响应、晋升概率、留存风险 -->
        <template v-else-if="editForm.image_type === '潜力画像'">
          <el-col :span="8">
            <el-form-item label="学习能力" label-width="100px">
              <el-input-number v-model="editForm.learning_ability" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="成长速度" label-width="100px">
              <el-input-number v-model="editForm.growth_speed" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="激励响应" label-width="100px">
              <el-input-number v-model="editForm.incentive_response" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="晋升概率" label-width="100px">
              <el-input-number v-model="editForm.promotion_probability" :min="0" :max="1" :precision="2" :step="0.01" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="留存风险" label-width="100px">
              <el-select v-model="editForm.retention_risk" placeholder="请选择留存风险" style="width: 100%;">
                <el-option label="低" value="低"></el-option>
                <el-option label="中" value="中"></el-option>
                <el-option label="高" value="高"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </template>
        
        <!-- 综合能力分数 - 所有类型都显示 -->
        <el-col :span="8">
          <el-form-item label="综合能力分数" label-width="100px">
            <el-input-number v-model="editForm.ability_score" :min="0" :max="100" :precision="2" style="width: 100%;"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
    </el-card>

   <el-form ref="updateForm" :model="editForm" :rules="rules" style="width:100%;"  label-width="25%" >
   <el-row :gutter="15">
        <el-col :span="8">
            <el-form-item label="员工ID"  prop="staff_id">
                <el-input v-model="editForm.staff_id" disabled placeholder="请输入员工ID"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="画像类型"  prop="image_type">
                <el-select v-model="editForm.image_type" placeholder="请选择画像类型">
                    <el-option v-for="(item,index) in imageTypes" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="绩效等级" prop="performance_level">
                <el-select v-model="editForm.performance_level" placeholder="请选择绩效等级">
                    <el-option v-for="(item,index) in performanceLevels" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="技能标签" prop="skill_tags">
                <el-input v-model="editForm.skill_tags"  placeholder="请输入技能标签，逗号分隔"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="工作风格" prop="work_style">
                <el-input v-model="editForm.work_style"  placeholder="请输入工作风格"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="职业方向" prop="career_orientation">
                <el-input v-model="editForm.career_orientation"  placeholder="请输入职业方向"></el-input>
            </el-form-item>  
        </el-col>
       <el-col :span="8">
            <el-form-item label="风险等级" prop="risk_level">
                <el-select v-model="editForm.risk_level" placeholder="请选择风险等级">
                    <el-option v-for="(item,index) in riskLevels" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>  
        </el-col>
        <!-- 模型版本字段不再展示（保留数据字段，但UI不需要） -->
        <el-col :span="16">
            <el-form-item label="性格特征" prop="personality_traits">
                <el-input v-model="editForm.personality_traits" type="textarea" :rows="2" placeholder="请输入性格特征"></el-input>
            </el-form-item>
        </el-col>
       <el-col :span="24" style="text-align: center; margin-top: 15px;">
            <el-button type="primary" @click="update" :loading="loadingbut" size="large">{{loadingbuttext}}</el-button>
            <el-button type="danger" @click="cancel" size="large">取消</el-button>
      </el-col>
  </el-row>
</el-form>
</el-dialog>
<!--画像详情-->
<el-dialog title="画像详情" v-model="empImageDetailDialogVisible"  width="90%">
  <!-- 能力可视化卡片 - 根据画像类型动态显示 -->
  <el-card shadow="always" style="margin-bottom: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <span style="font-size: 18px; font-weight: bold;">
          <el-tag :type="getImageTypeTag(detailForm.image_type)" effect="dark" size="large">
            {{ detailForm.image_type }}
          </el-tag>
          - {{ detailForm.sname || '员工' }}
        </span>
        <span style="font-size: 24px; font-weight: bold;">
          综合能力：{{ detailForm.ability_score || '-' }} 分
        </span>
      </div>
    </template>
    
    <!-- 能力画像 - 显示技术、工作量、稳定性 -->
    <template v-if="detailForm.image_type === '能力画像'">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">技术能力</div>
            <div class="ability-value">{{ detailForm.tech_ability || 0 }}</div>
            <el-progress :percentage="detailForm.tech_ability || 0" color="#67C23A"></el-progress>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">工作量能力</div>
            <div class="ability-value">{{ detailForm.workload_ability || 0 }}</div>
            <el-progress :percentage="detailForm.workload_ability || 0" color="#409EFF"></el-progress>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">稳定性能力</div>
            <div class="ability-value">{{ detailForm.stability_ability || 0 }}</div>
            <el-progress :percentage="detailForm.stability_ability || 0" color="#909399"></el-progress>
          </div>
        </el-col>
      </el-row>
    </template>
    
    <!-- 绩效画像 - 显示质量、贡献、绩效趋势 -->
    <template v-else-if="detailForm.image_type === '绩效画像'">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">质量能力</div>
            <div class="ability-value">{{ detailForm.quality_ability || 0 }}</div>
            <el-progress :percentage="detailForm.quality_ability || 0" color="#E6A23C"></el-progress>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">综合贡献能力</div>
            <div class="ability-value">{{ detailForm.contribution_ability || 0 }}</div>
            <el-progress :percentage="detailForm.contribution_ability || 0" color="#F56C6C"></el-progress>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">绩效趋势</div>
            <div class="ability-value">{{ detailForm.performance_trend || 0 }}</div>
            <el-progress 
              :percentage="Math.abs(detailForm.performance_trend || 0)" 
              :color="(detailForm.performance_trend || 0) > 0 ? '#67C23A' : '#F56C6C'">
            </el-progress>
          </div>
        </el-col>
      </el-row>
    </template>
    
    <!-- 潜力画像 - 显示学习能力、成长速度、激励响应 -->
    <template v-else-if="detailForm.image_type === '潜力画像'">
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">学习能力</div>
            <div class="ability-value">{{ detailForm.learning_ability || 0 }}</div>
            <el-progress :percentage="detailForm.learning_ability || 0" color="#67C23A"></el-progress>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">成长速度</div>
            <div class="ability-value">{{ detailForm.growth_speed || 0 }}</div>
            <el-progress :percentage="detailForm.growth_speed || 0" color="#409EFF"></el-progress>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="ability-card">
            <div class="ability-label">激励响应</div>
            <div class="ability-value">{{ detailForm.incentive_response || 0 }}</div>
            <el-progress :percentage="detailForm.incentive_response || 0" color="#E6A23C"></el-progress>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 15px;">
        <el-col :span="12">
          <div class="ability-card">
            <div class="ability-label">晋升概率</div>
            <div class="ability-value">{{ ((detailForm.promotion_probability || 0) * 100).toFixed(0) }}%</div>
            <el-progress :percentage="(detailForm.promotion_probability || 0) * 100" color="#F56C6C"></el-progress>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="ability-card">
            <div class="ability-label">留存风险</div>
            <div class="ability-value">{{ detailForm.retention_risk || '-' }}</div>
            <el-tag 
              :type="detailForm.retention_risk === '低' ? 'success' : detailForm.retention_risk === '中' ? 'warning' : 'danger'"
              size="large"
              style="margin-top: 10px;">
              {{ detailForm.retention_risk || '-' }}
            </el-tag>
          </div>
        </el-col>
      </el-row>
    </template>
  </el-card>

  <!-- 详细信息表单 -->
<el-form ref="detailFormRef" :model="detailForm" style="width:100%;" disabled label-width="25%" >
   <el-row>
        <el-col :span="8">
            <el-form-item label="员工ID"  prop="staff_id">
                <el-input v-model="detailForm.staff_id"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="绩效等级" prop="performance_level">
                <el-input v-model="detailForm.performance_level"></el-input>
            </el-form-item>  
        </el-col>
       <el-col :span="8">
            <el-form-item label="风险等级" prop="risk_level">
                <el-input v-model="detailForm.risk_level"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="12">
            <el-form-item label="技能标签" prop="skill_tags">
                <el-input v-model="detailForm.skill_tags"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="工作风格" prop="work_style">
                <el-input v-model="detailForm.work_style"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="12">
            <el-form-item label="职业方向" prop="career_orientation">
                <el-input v-model="detailForm.career_orientation"></el-input>
            </el-form-item>
        </el-col>
        <!-- 模型版本字段不再展示（保留数据字段，但UI不需要） -->
        <el-col :span="24">
            <el-form-item label="性格特征" prop="personality_traits">
                <el-input v-model="detailForm.personality_traits" type="textarea" :rows="2"></el-input>
            </el-form-item>
        </el-col>
  </el-row>
</el-form>
</el-dialog>
  </template>

</template>
<script>
/**
 * 画像管理页面
 * 
 * 功能说明：
 * 1. 画像记录查询（支持画像类型筛选）
 * 2. 画像列表展示（分页/排序）
 * 3. 画像分析跳转（跳转到画像分析页面）
 * 4. 工作量明细跳转（查看员工工作量）
 * 5. 画像删除（删除画像记录）
 * 
 * 关键数据来源：
 * - /getEmpImageByPage - 分页查询画像记录
 * - /selectEmpImageByCon - 条件查询画像记录
 * - /deleteEmpImage - 删除画像记录
 */
import NavMain from '@/components/NavMain.vue'
export default {
  components:{
    NavMain
  },
  created: function () {
    this.loadEmpImages(),
    void 0
  },
  data () {
     return {
        staffs: [],
        // 画像类型选项
        imageTypes: ['能力画像', '绩效画像', '潜力画像'],
        // 绩效等级选项
        performanceLevels: ['优秀', '良好', '一般', '待改进'],
        // 风险等级选项
        riskLevels: ['低', '中', '高'],
        // 查询条件表单
        selectForm: {
          currentPage: 1,
          pageSize: 10,
          act: '',              // 操作类型：byCon=条件查询
          image_type: ''        // 画像类型筛选
        },
        currentPage: 1,         // 当前页码
        tableData: [{ }],       // 画像记录列表数据
        pageSize: 10,           // 每页显示数量
        total: 0,               // 总记录数
        addForm: {},
        editForm: {},           // 编辑表单数据
        detailForm: {},         // 详情表单数据
        // 表单验证规则
        rules: {
          staff_id: [{required: true, message: '请输入员工ID', trigger: 'blur'}],
          image_type: [{ required: true, message: '请选择画像类型', trigger: 'change' }]
       },
        loadingbut: false, 
        loadingbuttext: '修改',
        loadingData: false,
        empImageDialogVisible: false,      // 编辑对话框显示状态
        empImageDetailDialogVisible: false  // 详情对话框显示状态
      }
  },
  methods: {
    getImageTypeTag(type) {
      const tagMap = {
        '能力画像': 'primary',
        '绩效画像': 'success',
        '潜力画像': 'warning'
      }
      return tagMap[type] || ''
    },
    getPerformanceTag(level) {
      const tagMap = {
        '优秀': 'danger',
        '良好': 'success',
        '一般': 'warning',
        '待改进': 'info'
      }
      return tagMap[level] || ''
    },
    /**
     * 跳转到画像分析页面
     */
    goToAnalyze(){
      this.$router.push('/imageAnalyze')
    },
    /**
     * 加载画像记录列表（分页查询）
     * 页面初始化时调用，获取分页数据
     */
    loadEmpImages (){
      console.log('=== 管理画像：开始加载数据 ===')
      console.log('当前页:', this.currentPage, '每页数量:', this.pageSize)
      
      this.$axios
          .get('/getEmpImageByPage?currentPage=' + this.currentPage + '&&pageSize=' + this.pageSize)
          .then(successResponse => {
              console.log('=== 管理画像：后端返回成功 ===')
              console.log('完整响应:', successResponse)
              console.log('响应数据:', successResponse.data)
              
              const d = successResponse.data
              this.tableData = d.empImages || d.images || (Array.isArray(d) ? d : [])
              this.total = d.total || (this.tableData ? this.tableData.length : 0)
              
              console.log('解析后的tableData数量:', this.tableData.length)
              console.log('前3条数据:', this.tableData.slice(0, 3))
              console.log('总记录数:', this.total)
              
              if (this.tableData.length === 0) {
                console.warn('⚠️ 警告：tableData为空数组')
                console.log('原始数据结构检查:', {
                  hasEmpImages: !!d.empImages,
                  hasImages: !!d.images,
                  isArray: Array.isArray(d),
                  empImagesLength: d.empImages?.length,
                  imagesLength: d.images?.length
                })
              }

              // #region agent log
              fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                  sessionId: 'debug-session',
                  runId: 'run6-pre',
                  hypothesisId: 'H10',
                  location: 'EmpImage.vue:loadEmpImages',
                  message: 'EmpImage list loaded',
                  data: {
                    currentPage: this.currentPage,
                    pageSize: this.pageSize,
                    total: this.total,
                    firstRow: this.tableData && this.tableData.length ? {
                      id: this.tableData[0].id,
                      staff_id: this.tableData[0].staff_id,
                      image_type: this.tableData[0].image_type,
                      skill_tags: this.tableData[0].skill_tags,
                      created_time1: this.tableData[0].created_time1
                    } : null
                  },
                  timestamp: Date.now()
                })
              }).catch(() => {})
              // #endregion
          })
          .catch(failResponse => {
            console.error('=== 管理画像：加载失败 ===')
            console.error('错误:', failResponse)
            this.$alert(failResponse.response?.status || failResponse.message)
            // #region agent log
            fetch('http://127.0.0.1:7243/ingest/2b5e5602-95d1-4519-8cbb-d9c317cb5264', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({
                sessionId: 'debug-session',
                runId: 'run6-pre',
                hypothesisId: 'H10',
                location: 'EmpImage.vue:loadEmpImages',
                message: 'EmpImage list load failed',
                data: { status: failResponse?.response?.status, message: failResponse?.message },
                timestamp: Date.now()
              })
            }).catch(() => {})
            // #endregion
          })
    },
    /**
     * 条件查询画像记录
     * 根据画像类型筛选条件获取画像记录列表
     */
    selectEmpImagesByCon(){
        console.log('=== 管理画像：条件查询 ===')
        console.log('查询条件:', this.selectForm)
        
        this.selectForm.act = "byCon"
        this.$axios
          .post('/selectEmpImageByCon', this.selectForm)
          .then(successResponse => {
             console.log('=== 条件查询返回 ===')
             console.log('响应数据:', successResponse.data)
             
             const d = successResponse.data
             this.tableData = d.empImages || d.images || (Array.isArray(d) ? d : [])
             this.total = d.total || (this.tableData ? this.tableData.length : 0)
             
             console.log('解析后的tableData数量:', this.tableData.length)
             console.log('前3条数据:', this.tableData.slice(0, 3))
          })
          .catch(failResponse => {
            console.error('=== 条件查询失败 ===')
            console.error('错误:', failResponse)
            this.$alert(failResponse.response?.status || failResponse.message, {confirmButtonText: '确定' })
          })
    },
    /**
     * 表格表头样式
     */
     headClass () {
        return 'text-align: center;background:rgb(242,242,242);color:rgb(140,138,140)'
    },
    /**
     * 分页页码变化处理
     * 根据当前查询状态（条件查询/分页查询）重新加载数据
     */
    handleCurrentChange(val) {
      this.currentPage = val
      if(this.selectForm.act === 'byCon'){
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        this.selectEmpImagesByCon()
      }else{
         this.loadEmpImages()
      }
    },
    /**
     * 跳转到画像分析页面
     * 传递员工ID参数，自动填充分析表单
     */
    handleAnalyze(index, row) {
      this.$router.push({path: '/imageAnalyze', query: {staff_id: row.staff_id}})
    },
    /**
     * 跳转到工作量明细页面
     * 传递员工ID参数，查看该员工的工作量记录
     */
    handleViewWorkload(index, row) {
      this.$router.push({path: '/workloadDetail', query: {staffId: row.staff_id}})
    },
    /**
     * 跳转到个人概览页面
     */
    goToOverall(staffId) {
      this.$router.push({ path: '/overallProfile', query: { staff_id: staffId } })
    },
    /**
     * 删除画像记录
     * 确认后调用后端API删除记录，成功后刷新列表
     */
    handleDelete(index, row) {
      console.log(index, row);
            this.$confirm('删除画像, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$axios
          .post('/deleteEmpImage?id=' + row.id)
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
              this.loadEmpImages()
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
    /**
     * 更新画像记录
     * 验证表单后调用后端API更新画像数据
     */
    update(){
        this.$refs['updateForm'].validate((valid) => {
          if (!valid) {
            this.$message.warning('请检查表单填写')
            return false
          }
          
        this.loadingbut = true;
        this.loadingbuttext = '修改中...';
        this.$axios
            .post('/updateEmpImage', this.editForm)
          .then(successResponse => {
            if (successResponse.data === "ok") {
                this.$message.success('修改成功')
              this.empImageDialogVisible = false
              this.loadEmpImages()
            }else {
              this.$alert('修改失败', {confirmButtonText: '确定' })
            }
            this.loadingbut = false;
            this.loadingbuttext = '修改';
          })
          .catch(failResponse => {
              this.loadingbut = false;
              this.loadingbuttext = '修改';
              this.$alert('修改失败: ' + failResponse.response.status, {confirmButtonText: '确定' })
            })
          })
    },
    /**
     * 取消编辑
     * 关闭编辑对话框，清空编辑表单数据
     */
    cancel(){
      this.empImageDialogVisible = false
      this.editForm = {}
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
</style>
<style>

/* 能力卡片样式 */
.ability-card {
  text-align: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  backdrop-filter: blur(10px);
}
.ability-label {
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: 500;
}
.ability-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
  }
</style>

