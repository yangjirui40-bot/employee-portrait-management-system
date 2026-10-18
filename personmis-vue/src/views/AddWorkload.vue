<template>
  <NavMain />
  <div class="page-content">
    <main class="content">
      <section class="form-card">
        <header class="card-header">
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-button icon="el-icon-back" circle @click="$router.go(-1)" title="返回"></el-button>
            <h2>工作量填报</h2>
          </div>
          <p class="hint">请填写本次任务的基本信息与工时，提交后将计入员工工作量统计。</p>
        </header>

        <el-form ref="addForm" :model="addForm" :rules="rules" label-width="120px" class="form-grid">
          <el-form-item label="任务开始日期" prop="startDate">
            <el-date-picker v-model="addForm.startDate" type="date" placeholder="选择开始日期" style="width:100%" />
          </el-form-item>

          <el-form-item label="任务结束日期" prop="endDate">
            <el-date-picker v-model="addForm.endDate" type="date" placeholder="选择结束日期" style="width:100%" />
          </el-form-item>

          <el-form-item label="项目" prop="projectId">
            <el-select v-model="addForm.projectId" placeholder="请选择项目" style="width:100%">
              <el-option v-for="item in projects" :key="item.id" :label="item.projectName" :value="item.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="任务描述" prop="taskDesc" class="full-width">
            <el-input v-model="addForm.taskDesc" type="textarea" :rows="4" placeholder="请输入任务描述" />
          </el-form-item>

          <el-form-item label="工时（小时）" prop="timeSpentHrs">
            <el-input-number v-model="addForm.timeSpentHrs" :precision="2" :min="0.01" :step="0.1" style="width:140px" />
          </el-form-item>

          <el-form-item label="难度权重" prop="taskWeight">
            <el-slider v-model="addForm.taskWeight" :min="0.5" :max="2.0" :step="0.1" show-input style="max-width:240px" />
          </el-form-item>

          <el-form-item label="自评质量" prop="qualityScore">
            <el-rate v-model="addForm.qualityStars" :max="5" @change="updateQualityScore" />
          </el-form-item>

          <el-form-item label="员工ID" prop="staffId">
            <el-input-number v-model="addForm.staffId" :min="1" style="width:140px" />
          </el-form-item>

          <el-form-item class="form-actions full-width">
            <div class="actions">
              <el-button type="primary" @click="add" :loading="loadingbut">{{loadingbuttext}}</el-button>
              <el-button @click="cancel">重置</el-button>
            </div>
          </el-form-item>
        </el-form>
      </section>
    </main>
  </div>
</template>

<script>
/**
 * 工作量填报页面
 * 
 * 功能说明：
 * 1. 工作量记录填报（项目/任务描述/工时/难度权重/质量评分）
 * 2. 表单验证（日期范围/必填项验证）
 * 3. 提交后自动触发积分引擎生成积分
 * 
 * 关键数据来源：
 * - /getTaskProject - 获取项目列表
 * - /addWorkloadDetail - 提交工作量记录（成功后触发积分引擎）
 */
import NavMain from '@/components/NavMain.vue'
export default {
  components:{
    NavMain
  },
  created: function () {
    this.loadProjects()
    // 如果从路由参数中获取了projectId，则自动填充
    if (this.$route.query.projectId) {
      this.addForm.projectId = parseInt(this.$route.query.projectId)
    }
  },
  data () {
    return {
       // 项目列表，用于下拉选择
       projects: [],
       // 工作量填报表单数据模型
       addForm: {
         projectId: null,        // 项目ID
         taskDesc: '',           // 任务描述
         timeSpentHrs: null,     // 工时（小时）
         taskWeight: 1.0,        // 难度权重（0.5-2.0，用于ECI计算）
         qualityScore: 1.0,      // 质量评分（0-1，由星级转换）
         qualityStars: 5,        // 质量星级（1-5星，用于UI显示）
         startDate: null,        // 任务开始日期
         endDate: null,          // 任务结束日期
         staffId: null           // 员工ID
       },
      // 表单验证规则
      rules: {
          startDate: [{required: true, message: '请选择任务开始日期', trigger: 'change'}],
          endDate: [
            {required: true, message: '请选择任务结束日期', trigger: 'change'},
            {
              validator: (rule, value, callback) => {
                if (value && this.addForm.startDate && value < this.addForm.startDate) {
                  callback(new Error('结束日期不能早于开始日期'))
                } else {
                  callback()
                }
              },
              trigger: 'change'
            }
          ],
          projectId: [{required: true, message: '请选择项目', trigger: 'change'}],
          taskDesc: [{required: true, message: '请输入任务描述', trigger: 'blur'}],
          timeSpentHrs: [{required: true, message: '请输入工时', trigger: 'blur'}],
          staffId: [{required: true, message: '请输入员工ID', trigger: 'blur'}]
      },
       loadingbut: false, 
       loadingbuttext: '提交'
    }
  },
  methods: {
    /**
     * 加载项目列表
     * 从后端获取所有项目，用于下拉选择框
     */
    loadProjects() {
      this.$axios
        .get('/getTaskProject')
        .then(successResponse => {
          this.projects = successResponse.data
        })
        .catch(() => {
          this.$alert('加载项目列表失败', {confirmButtonText: '确定' })
        })
    },
    /**
     * 更新质量评分
     * 将星级评分（1-5）转换为0-1的质量评分
     * 5星=1.0, 4星=0.8, 3星=0.6, 2星=0.4, 1星=0.2
     */
    updateQualityScore(val) {
      this.addForm.qualityScore = val * 0.2
    },
    /**
     * 提交工作量数据
     * 1. 验证表单
     * 2. 将质量星级转换为质量评分
     * 3. 调用后端API提交数据
     * 4. 提交成功后跳转到工作量明细页面或项目管理页面
     */
    add(){
        this.$refs['addForm'].validate((valid) => {
            if (valid) {
                this.loadingbut = true;
                this.loadingbuttext = '提交中...';
                this.addForm.qualityScore = this.addForm.qualityStars * 0.2
                this.$axios
                  .post('/addWorkloadDetail', this.addForm)
                  .then(successResponse => {
                    if (successResponse.data === "ok") {
                      this.$alert('提交成功', {confirmButtonText: '确定' })
                      if (this.addForm.projectId) {
                        this.$router.replace({path: '/workloadDetail', query: {projectId: this.addForm.projectId}})
                      } else {
                        this.$router.replace({path: '/taskProject'})
                      }
                    } else {
                      this.$alert('提交失败', {confirmButtonText: '确定' })
                      this.loadingbut = false;
                      this.loadingbuttext = '提交';
                    }
                  })
                  .catch(() => {
                    this.$alert('提交失败', {confirmButtonText: '确定' })
                    this.loadingbut = false;
                    this.loadingbuttext = '提交';
                  })
            } else {
                this.$alert('表单验证失败', {confirmButtonText: '确定' })
                return false;
            }
        })
    },
    /**
     * 重置表单
     * 清空所有表单字段，恢复默认值
     */
    cancel(){
           this.$refs['addForm'].resetFields()
           this.addForm.taskWeight = 1.0
           this.addForm.qualityScore = 1.0
           this.addForm.qualityStars = 5
      }
  } 
}
</script>
<style scoped>
.page-content{min-height:100vh}
.content{margin-left:0;padding:28px;min-height:100vh}
.form-card{max-width:960px;background:linear-gradient(180deg,rgba(255,255,255,0.02),rgba(255,255,255,0.01));padding:20px;border-radius:12px;box-shadow:0 8px 20px rgba(3,12,34,0.45)}
/* Center the card in viewport while accounting for left sidebar (220px) */
.form-card.centered{position:fixed;left:calc(50% + 110px);top:50%;transform:translate(-50%,-50%);z-index:1300;width:min(880px,90%)}
.card-header h2{margin:0 0 6px 0;color:#ffd86b}
.card-header .hint{margin:0;color:rgba(255,255,255,0.75);font-size:13px}
.form-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:12px;align-items:center}
.form-grid .full-width{grid-column:1 / -1}
.form-actions{display:flex;justify-content:flex-end}
.form-actions .actions{display:flex;gap:12px}
.el-form-item{margin-bottom:14px}
@media (max-width:900px){
  .content{margin-left:0;padding:16px}
  .form-grid{grid-template-columns:1fr}
  .form-card.centered{position:static;transform:none;width:100%;margin:0}
}
</style>

