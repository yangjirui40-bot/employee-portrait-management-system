<template>
    <NavMain></NavMain>
<div class="box1">
<br>
<el-form ref="selectForm" :model="selectForm" style="width:50%;"  label-width="50%" > 
  <el-row>
    <el-col :span="12">
        <el-form-item label="员工姓名"  prop="sname">
            <el-input v-model="selectForm.sname"  placeholder="请输入员工姓名"></el-input>
        </el-form-item>
    </el-col>
   <el-col :span="12">
        <el-form-item label="部门名称"  prop="depart_id">
            <el-select v-model="selectForm.depart_id" placeholder="请选择部门">
                <el-option v-for="(item,index) in departs" :key="index" :label="item.dname" :value="item.id"></el-option>
            </el-select>
        </el-form-item>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="12">
        <el-form-item label="岗位名称"  prop="post_id">
            <el-select v-model="selectForm.post_id" placeholder="请选择岗位">
            <el-option v-for="(item,index) in posts" :key="index" :label="item.pname" :value="item.id"></el-option>
            </el-select>
        </el-form-item>
    </el-col>
   <el-col :span="12">
        <el-form-item label="试用期状态" prop="status">
            <el-select v-model="selectForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option v-for="(item,index) in status" :key="index" :label="item" :value="item"></el-option>
            </el-select>
        </el-form-item>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="12">
        <el-form-item label="开始日期"  prop="startdate">
                <el-date-picker v-model="selectForm.startdate" type="date" placeholder="选择开始日期" style="width:100%"></el-date-picker>
        </el-form-item> 
    </el-col>
   <el-col :span="12">
        <el-form-item label="结束日期"  prop="enddate">
                <el-date-picker v-model="selectForm.enddate" type="date" placeholder="选择结束日期" style="width:100%"></el-date-picker>
        </el-form-item>  
    </el-col>
  </el-row>
  <el-row>
   <el-col :span="20">
        <el-form-item>
            <el-button type="primary" @click="selectPeriodByCon">查询</el-button>
        </el-form-item>
    </el-col>
  </el-row>
</el-form>
</div>
<div>
  <el-table
    :key="tableKey"
    :data="tableData"  
    :header-cell-style="headClass" 
    :cell-style="{ textAlign: 'center' }"
    :default-sort = "{prop: 'id', order: 'descending'}"
    v-loading="loading"
    element-loading-text="查询中...">
    <el-table-column prop="id" label="ID" min-width="1" sortable></el-table-column>
    <el-table-column prop="sname" label="员工名称" min-width="1" ></el-table-column>
    <el-table-column prop="dname"  label="所在部门" min-width="1"></el-table-column>
    <el-table-column prop="pname"  label="岗位名称" min-width="1"></el-table-column>
    <el-table-column prop="status"  label="状态" min-width="1"></el-table-column>
    <el-table-column prop="startdate1" label="试用期开始日期" min-width="1" sortable></el-table-column>
    <el-table-column prop="enddate1" label="试用期结束日期" min-width="1" sortable></el-table-column>
    <el-table-column label="操作" min-width="2">
      <template #default="scope">
        <el-button
          size="mini"  type="success" v-if="scope.row.status=='正常'"
          @click="handleEdit(scope.$index, scope.row, '转正')">转正</el-button>
        <el-button
          size="mini" type="primary" v-if="scope.row.status=='正常'"
          @click="handleEdit(scope.$index, scope.row, '延期')">延期</el-button>
        <el-button size="mini" type="danger" v-if="scope.row.status=='正常'"
          @click="handleEdit(scope.$index, scope.row, '不录用')">不录用</el-button>
        <el-button
          size="mini" type="success"
          @click="handleAdmission(scope.$index, scope.row)">录取</el-button>
        <el-button size="mini" type="danger"
          @click="handleRejection(scope.$index, scope.row)">不录取</el-button>
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
</template>
<script>
import NavMain from '@/components/NavMain.vue'
export default {
  components:{
    NavMain
  },
  created: function () {
    this.loadDepartment()
    this.loadPost()
    // 页面初始化时加载所有试用期数据（不设置status条件）
    this.loadPeriods()
  },
  data () {
     return {
       status: ["正常", "转正", "延期", "不录用"],
       departs: [{id: '', dname: ''}],//定义空数组接收后台数据
       posts: [{id: '', pname: ''}],
       selectForm: {
         sname: '',
         depart_id: 0,
         post_id: 0,
         status: '',
         startdate: null,
         enddate: null,
         currentPage: 1,
         pageSize: 10,
         act: ''
       },
      currentPage: 1,
      tableData: [],//定义空数组接收数据
      pageSize: 10,
      total: 0,
      tableKey: 0,  // 用于强制刷新表格
      loading: false  // 加载状态
     }
  },
  methods: {
    loadDepartment(){
        this.$axios
          .get('/getDepartment')
          .then(successResponse => {
              this.departs = successResponse.data
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status)
          })

    },
    loadPost(){
        this.$axios
          .get('/getPost')
          .then(successResponse => {
              this.posts = successResponse.data
          })
          .catch(failResponse => {
            this.$alert(failResponse.response.status)
          })
    },
    /**
     * 加载试用期数据列表
     * 页面初始化时调用，加载所有有试用期开始和结束日期的员工
     */
    loadPeriods (){
      console.log('加载试用期数据，页码:', this.currentPage, '每页:', this.pageSize)
      this.$axios
          .get('/getPeriods?currentPage=' + this.currentPage + '&pageSize=' + this.pageSize)
          .then(successResponse => {
              console.log('试用期数据响应:', successResponse.data)
              const periods = successResponse.data.periods || []
              const total = successResponse.data.total || 0
              
              // Vue 3 直接赋值即可触发响应式更新
              this.tableData = Array.isArray(periods) ? periods : []
              this.total = total || 0
              
              console.log('加载到的数据条数:', this.tableData.length, '总数:', this.total)
          })
          .catch(failResponse => {
            console.error('加载试用期数据失败:', failResponse)
            this.$message.error('加载失败: ' + (failResponse.response?.status || failResponse.message))
            this.tableData = []
            this.total = 0
          })
    },
    //条件查询
    selectPeriodByCon(){
        this.selectForm.act = "byCon"
        this.selectForm.currentPage = 1  // 查询时重置到第一页
        this.selectForm.pageSize = this.pageSize
        this.currentPage = 1
        
        // 构建查询参数
        const queryParams = {
          sname: this.selectForm.sname || '',
          status: this.selectForm.status || '',
          currentPage: 1,
          pageSize: this.pageSize,
          act: 'byCon'
        }
        
        // 处理部门ID：如果选择了部门，则传递ID，否则不传递（让后端不过滤）
        if (this.selectForm.depart_id && this.selectForm.depart_id !== 0) {
          queryParams.depart_id = this.selectForm.depart_id
        }
        
        // 处理岗位ID：如果选择了岗位，则传递ID，否则不传递（让后端不过滤）
        if (this.selectForm.post_id && this.selectForm.post_id !== 0) {
          queryParams.post_id = this.selectForm.post_id
        }
        
        // 处理日期格式：将Date对象转换为字符串格式 yyyy-MM-dd
        if (this.selectForm.startdate) {
          if (this.selectForm.startdate instanceof Date) {
            const year = this.selectForm.startdate.getFullYear()
            const month = String(this.selectForm.startdate.getMonth() + 1).padStart(2, '0')
            const day = String(this.selectForm.startdate.getDate()).padStart(2, '0')
            queryParams.startdate = `${year}-${month}-${day}`
          } else if (typeof this.selectForm.startdate === 'string') {
            queryParams.startdate = this.selectForm.startdate
          }
        }
        
        if (this.selectForm.enddate) {
          if (this.selectForm.enddate instanceof Date) {
            const year = this.selectForm.enddate.getFullYear()
            const month = String(this.selectForm.enddate.getMonth() + 1).padStart(2, '0')
            const day = String(this.selectForm.enddate.getDate()).padStart(2, '0')
            queryParams.enddate = `${year}-${month}-${day}`
          } else if (typeof this.selectForm.enddate === 'string') {
            queryParams.enddate = this.selectForm.enddate
          }
        }
        
        // 如果状态选择"全部"或空，则不传递status字段
        if (queryParams.status === '' || queryParams.status === null || queryParams.status === '全部') {
          delete queryParams.status
        }
        
        console.log('=== 条件查询开始 ===')
        console.log('原始表单数据:', JSON.stringify(this.selectForm, null, 2))
        console.log('构建的查询参数:', JSON.stringify(queryParams, null, 2))
        
        // 先清空数据，显示加载状态
        this.loading = true
        this.tableData = []
        this.total = 0
        this.tableKey = Date.now()  // 立即更新key，显示空表格
        
        this.$axios
          .post('/selectPeriodByCon', queryParams)
          .then(response => {
             console.log('=== 条件查询响应 ===')
             console.log('完整响应对象:', response)
             console.log('response.data:', response.data)
             
             // 处理响应数据
             const responseData = response.data || response
             console.log('响应数据类型:', typeof responseData)
             console.log('periods字段:', responseData.periods)
             console.log('total字段:', responseData.total)
             
             // 确保数据正确更新
             let periods = []
             let total = 0
             
             if (responseData && responseData.periods) {
               periods = Array.isArray(responseData.periods) ? responseData.periods : []
             } else if (Array.isArray(responseData)) {
               periods = responseData
             }
             
             total = (responseData && responseData.total) ? responseData.total : periods.length
             
             console.log('解析后的periods:', periods)
             console.log('解析后的total:', total)
             
             // Vue 3 直接赋值即可触发响应式更新
             this.tableData = periods
             this.total = total
             
             // 更新tableKey强制刷新表格
             this.tableKey = Date.now()
             
             console.log('=== 数据更新完成 ===')
             console.log('this.tableData长度:', this.tableData.length)
             console.log('this.total:', this.total)
             console.log('this.tableData前3条:', this.tableData.slice(0, 3))
             
             // 使用nextTick确保DOM更新
             this.$nextTick(() => {
               console.log('DOM更新后，tableData长度:', this.tableData.length)
               console.log('DOM更新后，tableData内容:', this.tableData)
               if (this.tableData.length === 0) {
                 this.$message.info('未找到符合条件的记录')
               } else {
                 this.$message.success(`查询成功，找到 ${this.total} 条记录`)
               }
             })
          })
          .catch(failResponse => {
            console.error('=== 条件查询失败 ===')
            console.error('错误对象:', failResponse)
            console.error('错误响应:', failResponse.response)
            console.error('错误数据:', failResponse.response?.data)
            const errorMsg = failResponse.response?.data?.message || 
                           failResponse.response?.data?.error || 
                           failResponse.message || 
                           '未知错误'
            this.$message.error('查询失败: ' + errorMsg)
            this.tableData = []
            this.total = 0
            this.tableKey = Date.now()
          })
          .finally(() => {
            this.loading = false
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
        this.selectPeriodByCon()
      }else{
         this.loadPeriods()
      }
    },
    //编辑与详情
    handleEdit(index, row, status) {
      console.log(index, row);
      this.$axios
          .post('/periodOp?id=' + row.id +"&status=" + status)
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$message.success('操作成功')
              // 根据当前查询状态刷新数据
              if (this.selectForm.act === 'byCon') {
                this.selectPeriodByCon()
              } else {
                this.loadPeriods()
              }
            }else {
              this.$message.error('操作失败！')
            }
          })
          .catch(failResponse => {
            this.$message.error('操作失败: ' + (failResponse.response?.status || failResponse.message))
          })
    },
    //录取功能
    handleAdmission(index, row) {
      this.$confirm('确定要录取该员工吗？此操作将设置试用期结束日期为当前时间', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 获取当前日期作为试用期结束日期
        const currentDate = new Date();
        const endDate = currentDate.toISOString().split('T')[0]; // 格式化为 YYYY-MM-DD
        
        // 调用专门的接口来设置试用期结束日期
        this.$axios
          .post('/setEndDate?id=' + row.id +"&enddate=" + endDate)
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$message.success('录取成功，试用期结束日期已设置为当前日期')
              // 根据当前查询状态刷新数据
              if (this.selectForm.act === 'byCon') {
                this.selectPeriodByCon()
              } else {
                this.loadPeriods()
              }
            }else {
              this.$message.error('录取失败！')
            }
          })
          .catch(failResponse => {
            this.$message.error('设置试用期结束日期失败：' + (failResponse.response?.status || failResponse.message))
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消录取'
        });
      });
    },
    //不录取功能
    handleRejection(index, row) {
      this.$confirm('确定要不录取该员工吗？此操作将删除该员工的所有数据，且不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }).then(() => {
        this.$axios
          .post('/deleteStaff?id=' + row.id)
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$message.success('不录取成功，员工数据已删除')
              // 根据当前查询状态刷新数据
              if (this.selectForm.act === 'byCon') {
                this.selectPeriodByCon()
              } else {
                this.loadPeriods()
              }
            }else {
              this.$message.error('不录取失败！该员工可能有关联数据，无法删除。')
            }
          })
          .catch(failResponse => {
            this.$message.error('删除失败: ' + (failResponse.response?.status || failResponse.message))
          })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消不录取操作'
        });
      });
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