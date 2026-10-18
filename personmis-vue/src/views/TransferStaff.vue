<template>
    <NavMain></NavMain>
<div class="box1">
<br>
<el-form ref="selectForm" :model="selectForm" style="width:50%;"  label-width="50%" > 
  <el-row>
    <el-col :span="24">
        <el-form-item label="员工编号"  prop="staff_id">
            <el-input v-model="selectForm.staff_id"  placeholder="请输入员工编号"></el-input>
        </el-form-item>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="24">
        <el-form-item label="员工姓名"  prop="sname">
            <el-input v-model="selectForm.sname"  placeholder="请输入员工姓名"></el-input>
        </el-form-item>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="24" style="text-align:center">
        <el-form-item>
            <el-button type="primary" @click="selectTransfersByCon">查询</el-button>
        </el-form-item>
    </el-col>
  </el-row>
</el-form>
</div>
<div>
  <el-table
    :data="tableData"  :header-cell-style="headClass" 
     :cell-style="{ textAlign: 'center' }"
    :default-sort = "{prop: 'id', order: 'descending'}">
    <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
    <el-table-column prop="staff_id" label="员工编号" width="120" sortable></el-table-column>
    <el-table-column prop="sname" label="员工名称" width="120"></el-table-column>
    <el-table-column prop="ttype"  label="调动类型" width="120"></el-table-column>
    <el-table-column prop="beforepost_name"  label="调动前岗位" width="150"></el-table-column>
    <el-table-column prop="afterpost_name"  label="调动后岗位" width="150"></el-table-column>
    <el-table-column prop="tdate1" label="调动时间" width="120" sortable></el-table-column>
    <el-table-column prop="opdate1" label="操作时间" width="120" sortable></el-table-column>
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
</template>
<script>
import NavMain from '@/components/NavMain.vue'
export default {
  components:{
    NavMain
  },
  created: function () {
    this.loadTransfer()
  },
  data () {
     return {
        ttypes:[ "升职","降职","数据录入错误"] ,
        departs: [ {id: '', dname: ''}] ,//定义空数组接收后台数据
        posts: [ {id: '', pname: ''}] ,
        selectForm: {
          currentPage: 1,
          pageSize: 10,
          act: ''
        },
        currentPage: 1,
        tableData: [{}],//定义空数组接收数据
        pageSize: 10,
        total: 0
      }
  },
  methods: {
    loadTransfer (){
      console.log('=== 加载调动员工列表 ===');
      console.log('当前页:', this.currentPage);
      console.log('每页大小:', this.pageSize);
      this.$axios
          .get('/getTransfer?currentPage=' + this.currentPage + '&pageSize=' + this.pageSize)
          .then(successResponse => {
              console.log('查询结果:', successResponse.data);
              this.tableData = successResponse.data.transfers || []
              this.total = successResponse.data.total || 0
              console.log('调动员工数量:', this.tableData.length);
              console.log('总记录数:', this.total);
              
              // 如果没有数据，提示用户
              if (this.total === 0) {
                  this.$message({
                      type: 'info',
                      message: '当前没有调动记录，请先添加员工调动信息',
                      duration: 3000
                  });
              }
          })
          .catch(failResponse => {
            console.error('加载失败:', failResponse);
            const errorMsg = failResponse.response?.data?.message || 
                           failResponse.message || 
                           `错误代码: ${failResponse.response?.status || '未知'}`
            this.$alert(errorMsg, {confirmButtonText: '确定'})
          })
    },
    //条件查询
    selectTransfersByCon(){
        this.selectForm.act = "byCon"
        // 确保分页参数包含在查询条件中
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        console.log('=== 条件查询调动员工 ===');
        console.log('查询条件:', this.selectForm);
        this.$axios
          .post('/selectTransfersByCon', this.selectForm)//直接提交表单
          .then(successResponse => {
             console.log('查询结果:', successResponse.data);
             this.tableData = successResponse.data.transfers || []
             this.total = successResponse.data.total || 0
             console.log('调动员工数量:', this.tableData.length);
             console.log('总记录数:', this.total);
          })
          .catch(failResponse => {
            console.error('查询失败:', failResponse);
            const errorMsg = failResponse.response?.data?.message || 
                           failResponse.message || 
                           `错误代码: ${failResponse.response?.status || '未知'}`
            this.$alert(errorMsg, {confirmButtonText: '确定' })
          })
    },
     // 表头样式设置
    headClass () {
        return 'text-align: center;background:rgb(242,242,242);color:rgb(140,138,140)'
    },
    //页码变换
    handleCurrentChange(val) {
      this.currentPage = val
      if(this.selectForm.act === 'byCon'){
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        this.selectTransfersByCon()
      }else{
         this.loadTransfer()
      }
    }
  }
} 
</script>
<style scoped>
  .box1{
      width: 80%;
      height: 250px;
      margin-left: 20%;
    }
.el-select{
    width:100%
  }
</style>