<template>
  <NavMain></NavMain>
  <div class="box">
    <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 20px;">
      <el-button icon="el-icon-back" circle @click="$router.go(-1)" title="返回"></el-button>
      <h2 style="margin: 0; color: #ffd86b;">新增项目</h2>
    </div>
   <el-form ref="addForm" :model="addForm" :rules="rules" style="width:50%;"  label-width="50%" >
  <el-form-item label="项目名称"  prop="projectName">
    <el-input v-model="addForm.projectName"  placeholder="请输入项目名称"></el-input>
  </el-form-item>
  <el-form-item label="开始日期"  prop="startDate">
    <el-date-picker v-model="addForm.startDate" type="date" placeholder="选择开始日期" style="width:100%"></el-date-picker>
  </el-form-item>
  <el-form-item label="结束日期"  prop="endDate">
    <el-date-picker v-model="addForm.endDate" type="date" placeholder="选择结束日期" style="width:100%"></el-date-picker>
  </el-form-item>
  <el-form-item label="预算工时"  prop="budgetedHours">
    <el-input-number v-model="addForm.budgetedHours" :precision="2" :min="0" placeholder="请输入预算工时" style="width:100%"></el-input-number>
  </el-form-item>
  <el-form-item label="所需技能" prop="requiredSkills">
    <el-input v-model="addForm.requiredSkills" placeholder="请输入技能标签，用逗号分隔，如：Java,Spring,MySQL" style="width:100%"></el-input>
  </el-form-item>
  <el-form-item label="最大人员数" prop="maxStaffCount">
    <el-input-number v-model="addForm.maxStaffCount" :min="1" :max="50" placeholder="请输入最大人员数量" style="width:100%"></el-input-number>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="add(addForm)" :loading="loadingbut">{{loadingbuttext}}</el-button>
    <el-button type="danger" @click="cancel">重置</el-button>
  </el-form-item>
</el-form>
 </div>
</template>

<script>
import NavMain from '@/components/NavMain.vue'
export default {
  components:{
    NavMain
  },
  data () {
    return {
       addForm: {},
      //验证规则
      rules: {
          projectName: [{required: true, message: '请输入项目名称', trigger: 'blur'}],
          budgetedHours: [{ required: true, message: '请输入预算工时', trigger: 'blur' }, 
                         { type: 'number', min: 0.01, message: '预算工时必须大于0', trigger: 'blur' }]
      },
       loadingbut: false, 
       loadingbuttext: '新增'
    }
  },
  methods: {
    add(){
        this.$refs['addForm'].validate((valid) => {
            if (valid) {
                this.loadingbut = true;
                this.loadingbuttext = '添加中...';
                this.$axios
                .post('/addTaskProject', this.addForm)//直接提交表单
                    .then(successResponse => {
                        if (successResponse.data === "ok") {
                            this.$alert('添加成功', {confirmButtonText: '确定' })
                            this.$router.replace({path: '/taskProject'})
                        }else {
                            this.$alert('添加失败', {confirmButtonText: '确定' })
                            this.loadingbut = false;
                            this.loadingbuttext = '新增';
                        }
                    })
                    .catch(failResponse => {
                        this.$alert(failResponse.response.status, {confirmButtonText: '确定' })
                        this.loadingbut = false;
                        this.loadingbuttext = '新增';
                    })
            } else {
                this.$alert('表单验证失败', {confirmButtonText: '确定' })
                return false;
            }
        })
    },
    cancel(){
           this.$refs['addForm'].resetFields()
      }
  } 
}
</script>
<style scoped>
.box{
      width: 80%;
      height: 200px;
      margin-left: 20%;
    }
.el-select{
    width:100%
}
</style>

