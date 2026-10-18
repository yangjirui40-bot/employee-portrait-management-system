<template>
    <NavMain></NavMain>
<div class="box1">
<br>
<el-form ref="selectForm" :model="selectForm" style="width:50%;"  label-width="50%" > 
  <el-form-item label="员工名称"  prop="sname">
    <el-input v-model="selectForm.sname"  placeholder="请输入员工名称"></el-input>
  </el-form-item>
 <el-form-item label="部门名称" prop="depart_id">
    <el-select v-model="selectForm.depart_id" placeholder="请选择部门">
      <el-option v-for="(item,index) in dparts" :key="index" :label="item.dname" :value="item.id"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="selectStaffsByCon">查询</el-button>
  </el-form-item>
</el-form>
</div>
<div class="box2">
  <el-table
    :data="tableData"  :header-cell-style="headClass" 
     :cell-style="{ textAlign: 'center' }"
    :default-sort = "{prop: 'id', order: 'descending'}">
    <el-table-column prop="id" label="ID" width="60" sortable></el-table-column>
    <el-table-column prop="sname" label="员工姓名" width="140"></el-table-column>
    <el-table-column prop="dname"  label="所在部门" width="160"></el-table-column>
    <el-table-column prop="pname" label="岗位名称" width="160"></el-table-column>
        <!-- 技能标签列已移除 -->
        <el-table-column label="操作" width="520" align="center" header-class-name="col-operations-header" class-name="col-operations">
            <template #default="scope">
                        <div class="action-horizontal">
                            <el-tooltip content="编辑" placement="top">
                                <el-button size="mini" type="primary" plain @click="handleEdit(scope.$index, scope.row, 'update')">编辑</el-button>
                            </el-tooltip>
                            <el-tooltip content="详情" placement="top">
                                <el-button size="mini" type="info" plain @click="handleEdit(scope.$index, scope.row, 'detail')">详情</el-button>
                            </el-tooltip>
                            <el-tooltip content="删除" placement="top">
                                <el-button size="mini" type="danger" plain @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                            </el-tooltip>
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
  <el-dialog title="员工修改" v-model="staffDialogVisible"  width="100%">
   <el-form ref="addForm" :model="addForm" :rules="rules" style="width:100%;"  label-width="30%" >
   <el-row>
        <el-col :span="8">
            <el-form-item label="姓名"  prop="sname">
                <el-input v-model="addForm.sname"  placeholder="请输入姓名"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="性别"  prop="sex">
                <el-radio v-model="addForm.sex" label="男">男</el-radio>
                <el-radio v-model="addForm.sex" label="女">女</el-radio>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="出生日期"  prop="birthday">
                <el-date-picker v-model="addForm.birthday" type="date" placeholder="选择出生日期" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="身份证号" prop="sid">
                <el-input v-model="addForm.sid"  placeholder="请输入身份证号"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="部门" prop="depart_id">
                <el-select v-model="addForm.depart_id" placeholder="请选择部门">
                    <el-option v-for="(item,index) in dparts" :key="index" :label="item.dname" :value="item.id"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="岗位" prop="post_id">
                <el-select v-model="addForm.post_id" placeholder="请选择岗位">
                    <el-option v-for="(item,index) in posts" :key="index" :label="item.pname" :value="item.id"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="入职日期"  prop="entrydate">
                <el-date-picker v-model="addForm.entrydate" type="date" placeholder="选择入职日期" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="参加工作日期"  prop="joinworkdate">
                <el-date-picker v-model="addForm.joinworkdate" type="date" placeholder="选择参加工作日期" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
       <el-col :span="8">
            <el-form-item label="用工形式"  prop="workform">
                <el-radio v-model="addForm.workform" label="正式">正式</el-radio>
                <el-radio v-model="addForm.workform" label="临时">临时</el-radio>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="人员来源" prop="staffsource">
                <el-select v-model="addForm.staffsource" placeholder="请选择人员来源">
                    <el-option v-for="(item,index) in staffsources" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="政治面貌" prop="politicalstatus">
                <el-select v-model="addForm.politicalstatus" placeholder="请选择政治面貌">
                    <el-option v-for="(item,index) in politicalstatuss" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="民族" prop="nation">
                <el-select v-model="addForm.nation" placeholder="请选择民族">
                    <el-option v-for="(item,index) in nations" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="籍贯" prop="nativeplace">
                <el-input v-model="addForm.nativeplace"  placeholder="请输入籍贯"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="联系电话" prop="stel">
                <el-input v-model="addForm.stel"  placeholder="请输入联系电话"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="电子邮件" prop="semail">
                <el-input v-model="addForm.semail"  placeholder="请输入电子邮件"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="技能标签" prop="skill_tags">
                <el-input v-model="addForm.skill_tags"  placeholder="请输入技能标签，逗号分隔"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="身高" prop="sheight">
                <el-input v-model="addForm.sheight"  placeholder="请输入身高"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="血型" prop="bloodtype">
                <el-select v-model="addForm.bloodtype" placeholder="请选择血型">
                    <el-option v-for="(item,index) in bloodtypes" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="婚姻状况" prop="maritalstatus">
                <el-radio v-model="addForm.maritalstatus" label="已婚">已婚</el-radio>
                <el-radio v-model="addForm.maritalstatus" label="未婚">未婚</el-radio>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="户口所在地" prop="registeredresidence">
                <el-input v-model="addForm.registeredresidence"  placeholder="请输入户口所在地"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="最高学历" prop="education">
                <el-select v-model="addForm.education" placeholder="请选择最高学历">
                    <el-option v-for="(item,index) in educations" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="最高学位" prop="degree">
                <el-select v-model="addForm.degree" placeholder="请选择最高学位">
                    <el-option v-for="(item,index) in degrees" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="毕业院校" prop="university">
                <el-input v-model="addForm.university"  placeholder="请输入毕业院校"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="所学专业" prop="major">
                <el-input v-model="addForm.major"  placeholder="请输入所学专业"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="毕业日期"  prop="graduationdate">
                <el-date-picker v-model="addForm.graduationdate" type="date" placeholder="选择参加工作日期" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="试用期开始日期"  prop="startdate">
                <el-date-picker v-model="addForm.startdate" type="date" placeholder="选择参加工作日期" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="试用期结束日期"  prop="enddate">
                <el-date-picker v-model="addForm.enddate" type="date" placeholder="选择参加工作日期" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
       <el-col :span="8">
            <el-form-item>
                <el-button type="primary" @click="update(addForm)" :loading="loadingbut">{{loadingbuttext}}</el-button>
                <el-button type="danger" @click="cancel">重置</el-button>
            </el-form-item>
      </el-col>
  </el-row>
</el-form>
</el-dialog>
<!--员工详情-->
<el-dialog title="员工详情" v-model="staffDetailDialogVisible"  width="100%">
<el-form ref="addForm" :model="addForm" :rules="rules" style="width:100%;" disabled label-width="30%" >
   <el-row>
        <el-col :span="8">
            <el-form-item label="姓名"  prop="sname">
                <el-input v-model="addForm.sname"></el-input>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="性别"  prop="sex">
                <el-radio v-model="addForm.sex"  label="男">男</el-radio>
                <el-radio v-model="addForm.sex"  label="女">女</el-radio>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="出生日期"  prop="birthday">
                <el-date-picker v-model="addForm.birthday" type="date" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="身份证号" prop="sid">
                <el-input v-model="addForm.sid"  ></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="部门" prop="dname">
                <el-input v-model="addForm.dname"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="岗位" prop="pname">
                <el-input v-model="addForm.pname"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="入职日期"  prop="entrydate">
                <el-date-picker v-model="addForm.entrydate" type="date"  style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="参加工作日期"  prop="joinworkdate">
                <el-date-picker v-model="addForm.joinworkdate" type="date"  style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
       <el-col :span="8">
            <el-form-item label="用工形式"  prop="workform">
                <el-radio v-model="addForm.workform"  label="正式">正式</el-radio>
                <el-radio v-model="addForm.workform"  label="临时">临时</el-radio>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="人员来源" prop="staffsource">
                <el-select v-model="addForm.staffsource">
                    <el-option v-for="(item,index) in staffsources" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="政治面貌" prop="politicalstatus">
                <el-select v-model="addForm.politicalstatus">
                    <el-option v-for="(item,index) in politicalstatuss" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="民族" prop="nation">
                <el-select v-model="addForm.nation">
                    <el-option v-for="(item,index) in nations" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="籍贯" prop="nativeplace">
                <el-input v-model="addForm.nativeplace"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="联系电话" prop="stel">
                <el-input v-model="addForm.stel" ></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="电子邮件" prop="semail">
                <el-input v-model="addForm.semail"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="技能标签" prop="skill_tags">
                <el-input v-model="addForm.skill_tags"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="身高" prop="sheight">
                <el-input v-model="addForm.sheight"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="血型" prop="bloodtype">
                <el-select v-model="addForm.bloodtype">
                    <el-option v-for="(item,index) in bloodtypes" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="婚姻状况" prop="maritalstatus">
                <el-radio v-model="addForm.maritalstatus" label="已婚">已婚</el-radio>
                <el-radio v-model="addForm.maritalstatus" label="未婚">未婚</el-radio>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="户口所在地" prop="registeredresidence">
                <el-input v-model="addForm.registeredresidence"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="最高学历" prop="education">
                <el-select v-model="addForm.education">
                    <el-option v-for="(item,index) in educations" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="最高学位" prop="degree">
                <el-select v-model="addForm.degree">
                    <el-option v-for="(item,index) in degrees" :key="index" :label="item" :value="item"></el-option>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="毕业院校" prop="university">
                <el-input v-model="addForm.university"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="所学专业" prop="major">
                <el-input v-model="addForm.major"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="毕业日期"  prop="graduationdate">
                <el-date-picker v-model="addForm.graduationdate" type="date" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="试用期开始日期"  prop="startdate">
                <el-date-picker v-model="addForm.startdate" type="date" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
        <el-col :span="8">
            <el-form-item label="试用期结束日期"  prop="enddate">
                <el-date-picker v-model="addForm.enddate" type="date" style="width:100%"></el-date-picker>
            </el-form-item>  
        </el-col>
  </el-row>
</el-form>
</el-dialog>
</template>
<script>
/**
 * 员工管理页面
 * 
 * 功能说明：
 * 1. 员工信息查询（支持员工名称/部门筛选）
 * 2. 员工信息CRUD（新增/编辑/删除/详情查看）
 * 3. 试用期管理（试用期转正/延期）
 * 4. 员工列表展示（分页/排序）
 * 
 * 关键数据来源：
 * - /getStaffByPage - 分页查询员工列表
 * - /selectStaffByCon - 条件查询员工列表
 * - /getDepartment - 获取部门列表
 * - /getPost - 获取岗位列表
 */
import NavMain from '@/components/NavMain.vue'
export default {
  components:{
    NavMain
  },
  created: function () {
    this.loadStaffs(),
    this.loadDeparts()
  },
  data () {
     return {
        dparts:[{id: '', dname: ''}],      // 部门列表（用于下拉选择）
        // 查询条件表单
        selectForm: {
            currentPage: 1,
            pageSize: 10,
            act: '',                        // 操作类型：byCon=条件查询
            sname: '',                      // 员工名称筛选
            depart_id: null                 // 部门ID筛选
        },
        currentPage: 1,                    // 当前页码
        tableData: [],                      // 员工列表数据
        pageSize: 10,                       // 每页显示数量
        total: 0,                           // 总记录数
        posts: [ {id: '', pname: ''}] ,    // 岗位列表（用于下拉选择）
        // 下拉选项数据
        staffsources: [ '校园招聘','社会招聘','军转','其它'] ,  // 人员来源选项
        politicalstatuss: ['党员', '预备党员', '团员'],         // 政治面貌选项
        nations: ["汉族","蒙古族","回族","藏族","维吾尔族","苗族","彝族","壮族","布依族","朝鲜族","满族","侗族","瑶族","白族","土家族",
 "哈尼族","哈萨克族","傣族","黎族","傈僳族","佤族","畲族","高山族","拉祜族","水族","东乡族","纳西族","景颇族","柯尔克孜族",
 "土族","达斡尔族","仫佬族","羌族","布朗族","撒拉族","毛南族","仡佬族","锡伯族","阿昌族","普米族","塔吉克族","怒族", "乌孜别克族",
"俄罗斯族","鄂温克族","德昂族","保安族","裕固族","京族","塔塔尔族","独龙族","鄂伦春族","赫哲族","门巴族","珞巴族","基诺族","其它"],
        bloodtypes: ["A 型","B 型","AB 型","0 型","其他血型","未定血型"],
        educations: ["高中及以下","大专","本科","研究生"],
        degrees: ["无学位","学士","双学士","硕士","博士"],
        //编辑
        addForm: {},
        //验证规则
        rules: {
          sname: [{required: true, message: '请输入姓名', trigger: 'blur'}],
          sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
          birthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
          sid: [{required: true, message: '请输入身份证号', trigger: 'blur'}],
          depart_id: [{ required: true, message: '请选择部门', trigger: 'change' }],
          post_id: [{ required: true, message: '请选择岗位', trigger: 'change' }],
          entrydate: [{ required: true, message: '请选择入职日期', trigger: 'change' }],
          joinworkdate: [{ required: true, message: '请选择参加工作日期', trigger: 'change' }],
          workform: [{ required: true, message: '请选择用工形式', trigger: 'change' }],
          staffsource: [{ required: true, message: '请选择人员来源', trigger: 'change' }]
       },
        loadingbut: false, 
        loadingbuttext: '修改',
        staffDialogVisible: false,//编辑对话框是否显示
        staffDetailDialogVisible: false,
      }
  },
  methods: {
    /**
     * 加载部门列表
     * 获取所有部门，用于下拉选择框
     */
    loadDeparts(){
        this.$axios
          .get('/getDepartment')
          .then(successResponse => {
              this.dparts = successResponse.data
          })
          .catch(failResponse => {
            this.$alert(String(failResponse.response?.status || '未知错误'), {confirmButtonText: '确定'})
          })
    },
    /**
     * 加载岗位列表
     * 获取所有岗位，用于下拉选择框
     */
    loadPost(){
        this.$axios
          .get('/getPost')
          .then(successResponse => {
              this.posts = successResponse.data
          })
          .catch(failResponse => {
            this.$alert(String(failResponse.response?.status || '未知错误'), {confirmButtonText: '确定'})
          })
    },
    /**
     * 加载员工列表（分页查询）
     * 页面初始化时调用，获取分页数据
     */
    loadStaffs (){
      this.$axios
          .get('/getStaffByPage?currentPage=' + this.currentPage + '&&pageSize=' + this.pageSize)
          .then(successResponse => {
              this.tableData = successResponse.data.staffs
              this.total = successResponse.data.total
          })
          .catch(failResponse => {
            this.$alert(String(failResponse.response?.status || '未知错误'), {confirmButtonText: '确定'})
          })
    },
    /**
     * 条件查询员工列表
     * 根据查询条件（员工名称/部门）获取员工列表
     */
    selectStaffsByCon(){
                this.selectForm.act = "byCon"
                // ensure paging params are included
                this.selectForm.currentPage = this.currentPage
                this.selectForm.pageSize = this.pageSize
                this.$axios
                    .post('/selectStaffByCon', this.selectForm)//直接提交表单
                    .then(successResponse => {
                         this.tableData = successResponse.data.staffs
                         this.total = successResponse.data.total
                    })
          .catch(failResponse => {
            const errorMsg = failResponse.response?.data?.message || failResponse.message || `错误代码: ${failResponse.response?.status || '未知'}`
            this.$alert(errorMsg, {confirmButtonText: '确定' })
          })
    },
     // 表头样式设置
    headClass () {
        return 'text-align: center;background:rgb(242,242,242);color:rgb(140,138,140)'
    },
        normalizeTags(value) {
            if (!value && value !== 0) return []
            if (Array.isArray(value)) return value.filter(Boolean)
            try {
                return String(value).split(',').map(s => s.trim()).filter(Boolean)
            } catch (e) {
                return []
            }
        },
    handleEdit(index, row, act) {
      console.log('=== 开始编辑员工 ===');
      console.log('员工ID:', row.id);
      console.log('操作类型:', act);
      
      // 同时加载部门列表、职位列表和员工详情
      const loadDeptPromise = this.$axios.get('/getDepartment')
      const loadPostPromise = this.$axios.get('/getPost')
      const loadStaffPromise = this.$axios.get('/getStaffDetail?id=' + row.id)
      
      Promise.all([loadDeptPromise, loadPostPromise, loadStaffPromise])
        .then(([deptResponse, postResponse, staffResponse]) => {
          console.log('=== 数据加载完成 ===');
          console.log('部门列表:', deptResponse.data);
          console.log('职位列表:', postResponse.data);
          console.log('员工详情:', staffResponse.data);
          
          // 更新部门列表
          this.dparts = deptResponse.data
          // 更新职位列表
          this.posts = postResponse.data
          // 更新表单数据
          this.addForm = staffResponse.data
          
          console.log('表单数据已更新:', this.addForm);
          console.log('技能标签:', this.addForm.skill_tags);
          console.log('部门ID:', this.addForm.depart_id);
          console.log('部门名称:', this.addForm.dname);
          console.log('职位ID:', this.addForm.post_id);
          console.log('职位名称:', this.addForm.pname);
          
          // 验证关键字段是否存在
          if (!this.addForm.depart_id) {
            console.warn('警告：部门ID为空！');
          }
          if (!this.addForm.post_id) {
            console.warn('警告：职位ID为空！');
          }
          if (!this.addForm.id) {
            console.warn('警告：员工ID为空！');
          }
          
          // 数据加载完成后再打开对话框
          if(act === 'update')
            this.staffDialogVisible = true
          else
            this.staffDetailDialogVisible = true
        })
        .catch(failResponse => {
          console.error('=== 加载失败 ===');
          console.error('错误:', failResponse);
          this.$alert('加载失败: ' + (failResponse.response?.status || failResponse.message), {confirmButtonText: '确定'})
        })
    },
    handleDelete(index, row) {
      console.log(index, row);
            this.$confirm('删除员工, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$axios
          .post('/deleteStaff?id=' + row.id)
          .then(successResponse => {
            if (successResponse.data === "ok") {
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
              //删除成功后重新加载
              this.loadPosts()
            }else {
              this.$alert('不能删除有关联数据！', {confirmButtonText: '确定' })
            }
          })
          .catch(failResponse => {
            const errorMsg = failResponse.response?.data?.message || failResponse.message || `错误代码: ${failResponse.response?.status || '未知'}`
            this.$alert(errorMsg, {confirmButtonText: '确定' })
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
    },
        handleOpCommand(cmd, row) {
            if (cmd === 'update') this.handleEdit(null, row, 'update')
            else if (cmd === 'detail') this.handleEdit(null, row, 'detail')
            else if (cmd === 'delete') this.handleDelete(null, row)
        },
    update(){
        console.log('=== 开始更新员工信息 ===');
        console.log('表单数据:', this.addForm);
        
        // 验证必填字段
        if (!this.addForm.depart_id) {
            this.$alert('请选择部门', {confirmButtonText: '确定'})
            return
        }
        if (!this.addForm.post_id) {
            this.$alert('请选择岗位', {confirmButtonText: '确定'})
            return
        }
        if (!this.addForm.id) {
            this.$alert('员工ID缺失，无法更新', {confirmButtonText: '确定'})
            return
        }
        
        // 表单验证
        this.$refs['addForm'].validate((valid) => {
            if (!valid) {
                this.$alert('请填写完整的表单信息', {confirmButtonText: '确定'})
                return false
            }
            
            this.loadingbut = true;
            this.loadingbuttext = '修改中...';
            
            // 确保提交的数据包含必要的字段
            const submitData = {
                ...this.addForm,
                depart_id: this.addForm.depart_id,
                post_id: this.addForm.post_id,
                id: this.addForm.id
            }
            
            console.log('提交的数据:', submitData);
            console.log('部门ID:', submitData.depart_id);
            console.log('岗位ID:', submitData.post_id);
            
            this.$axios
          .post('/updateStaff', submitData)//直接提交表单
          .then(successResponse => {
            console.log('更新响应:', successResponse.data);
            if (successResponse.data === "ok") {
              this.$alert('修改成功', {confirmButtonText: '确定' })
              this.staffDialogVisible = false
              this.loadStaffs()
            }else {
              this.$alert('修改失败', {confirmButtonText: '确定' })
            }
            this.loadingbut = false;
            this.loadingbuttext = '修改';
          })
          .catch(failResponse => {
            console.error('=== 更新失败 ===');
            console.error('错误响应:', failResponse.response);
            console.error('错误数据:', failResponse.response?.data);
            const errorMsg = failResponse.response?.data?.message || 
                           failResponse.response?.data?.error || 
                           failResponse.message || 
                           `服务器错误: ${failResponse.response?.status || '未知'}`
            this.$alert(errorMsg, {confirmButtonText: '确定' })
            this.loadingbut = false;
            this.loadingbuttext = '修改';
          })
        })
    },
    cancel(){
           this.$refs['addForm'].resetFields()
    },
    //页码变化
    handleCurrentChange(val) {
      this.currentPage = val
      if(this.selectForm.act === 'byCon'){
        this.selectForm.currentPage = this.currentPage
        this.selectForm.pageSize = this.pageSize
        this.selectStaffsByCon()
      }else{
         this.loadStaffs()
      }
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
    width: 100%;
    margin-left: 0;
    margin-right: 0;
    padding-right: 12px; /* 微调右侧内边距 */
    overflow-x: auto; /* 如内容过宽允许横向滚动 */
    overflow-y: visible;
}
.el-select{
    width:100%
  }
/* Ensure table leaves extra right space and allows inner overflow so right-most buttons stay visible */
.box2 .el-table{
    width: 100% !important;
    min-width: unset !important;
}
.box2 .el-table__header-wrapper,
.box2 .el-table__body-wrapper{
    overflow: visible !important;
}
.box2 .cell{
    white-space: nowrap;
}
</style>
<style scoped>
.col-operations .cell{
    /* 不使用 sticky 定位，保持表头与单元格在同一流中对齐 */
    position: static !important;
    background: #fff;
    z-index:6;
    padding:6px 12px !important;
    display:flex;
    align-items:center;
    justify-content:center;
    box-sizing:border-box;
        min-width:520px;
 }
.col-operations-header{
    /* 统一表头居中对齐 */
    text-align:center;
    display:flex;
    align-items:center;
    justify-content:center;
    padding:16px 0;
    background:#f5f7fa;
}
/* 竖向排列操作按钮 */
/* 横向操作按钮 */
.col-operations .action-horizontal{
    display:flex;
    gap:8px;
    align-items:center;
    justify-content:center;
}
.col-operations .action-horizontal .el-button{
    min-width:140px;
}
</style>