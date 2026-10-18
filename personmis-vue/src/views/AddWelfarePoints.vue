<template>
 <NavMain></NavMain>
  <div class="box">
    <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 20px;">
      <el-button icon="el-icon-back" circle @click="$router.go(-1)" title="返回"></el-button>
      <h2 style="margin: 0; color: #ffd86b;">新增积分</h2>
    </div>
   <el-form ref="addForm" :model="addForm" :rules="rules" style="width:100%;"  label-width="30%" >
   <el-row>
        <el-col :span="8">
            <el-form-item label="员工ID"  prop="staff_id">
                <el-input v-model="addForm.staff_id"  placeholder="请输入员工ID"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="积分类型"  prop="points_type">
                <el-select v-model="addForm.points_type" placeholder="请选择积分类型">
                    <el-option v-for="(item,index) in pointsTypes" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="积分值" prop="points_value">
                <el-input-number v-model="addForm.points_value" :min="0" placeholder="请输入积分值" style="width:100%"></el-input-number>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="8">
            <el-form-item label="交易类型" prop="transaction_type">
                <el-select v-model="addForm.transaction_type" placeholder="请选择交易类型">
                    <el-option v-for="(item,index) in transactionTypes" :key="index" :label="item.label" :value="item.value"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <!--<el-col :span="8">
            <el-form-item label="积分来源" prop="points_source">
                <el-input v-model="addForm.points_source"  placeholder="请输入积分来源"></el-input>
            </el-form-item>
        </el-col>-->
      <!-- 修改查询表单中的积分来源字段 -->
      <el-col :span="8">
      <el-form-item label="积分来源" prop="points_source">
        <el-select v-model="addForm.points_source" placeholder="请选择积分来源" clearable style="width: 100%">
          <el-option v-for="(item,index) in pointsSources" :key="index" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      </el-col>
        <el-col :span="8">
            <el-form-item label="可用积分" prop="available_points">
                <el-input-number v-model="addForm.available_points" :min="0" placeholder="请输入可用积分" style="width:100%"></el-input-number>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="8">
            <el-form-item label="已使用积分" prop="used_points">
                <el-input-number v-model="addForm.used_points" :min="0" placeholder="请输入已使用积分" style="width:100%"></el-input-number>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="操作人" prop="operator">
                <el-input v-model="addForm.operator"  placeholder="请输入操作人"></el-input>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row>
        <el-col :span="8">
            <el-form-item label="备注" prop="remark">
                <el-input v-model="addForm.remark" type="textarea" :rows="3" placeholder="请输入备注"></el-input>
            </el-form-item>
        </el-col>
       <el-col :span="8">
            <el-form-item>
                <el-button type="primary" @click="add(addForm)" :loading="loadingbut">{{loadingbuttext}}</el-button>
                <el-button type="danger" @click="cancel">重置</el-button>
            </el-form-item>
      </el-col>
    </el-row>
    </el-form>
 </div>
</template>

<script>
import NavMain from '@/components/NavMain.vue'
import {ElOption} from "element-plus";
export default {
  components:{
    ElOption,
    NavMain
  },
  data () {
    return {
        pointsTypes: ['年度积分', '月度积分', '奖励积分', '消费积分', '其他积分'],
        transactionTypes: [
          {label: '获得', value: 'EARN'},
          {label: '使用', value: 'USE'},
          {label: '调整', value: 'ADJUST'}
        ],
        pointsSources: [
        { value: 'PERFORMANCE', label: '绩效奖励' },
        { value: 'PROJECT_BONUS', label: '项目奖金' },
        { value: 'TRAINING', label: '培训补贴' },
        { value: 'ATTENDANCE', label: '考勤奖励' },
        { value: 'FESTIVAL', label: '节日福利' },
        { value: 'SENIORITY', label: '工龄积分' },
        { value: 'CERTIFICATION', label: '技能认证' },
        { value: 'TEAM_CONTRIBUTION', label: '团队贡献' },
        { value: 'INNOVATION', label: '创新奖励' },
        { value: 'SPECIAL_REWARD', label: '特殊奖励' },
        { value: 'TASK_COMPLETION', label: '任务完成' },
        { value: 'OVERTIME', label: '加班补贴' },
        { value: 'OTHER', label: '其他' }
        ],
        addForm: {},
        rules: {
          staff_id: [{required: true, message: '请输入员工ID', trigger: 'blur'}],
          points_type: [{ required: true, message: '请选择积分类型', trigger: 'change' }],
          points_value: [{ required: true, message: '请输入积分值', trigger: 'blur' }],
          transaction_type: [{ required: true, message: '请选择交易类型', trigger: 'change' }]
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
                .post('/addWelfarePoints', this.addForm)
                .then(successResponse => {
                    if (successResponse.data === "ok") {
                    this.$alert('添加成功', {confirmButtonText: '确定' })
                    this.$router.replace({path: '/welfarePoints'})
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
            }else {
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
      width: 90%;
      margin-left: 5%;
    }
.el-select{
    width:100%
  }
</style>

