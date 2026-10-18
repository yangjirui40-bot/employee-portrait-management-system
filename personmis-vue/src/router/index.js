import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import Department from '../views/Department.vue'
import AddDepartment from '../views/AddDepartment.vue'
import Post from '../views/Post.vue'
import AddPost from '../views/AddPost.vue'
import AddStaff from '../views/AddStaff.vue'
import Staff from '../views/Staff.vue'
import PeroidOp from '../views/PeroidOp.vue'  
import AddTransferStaff from '../views/AddTransferStaff.vue'  
import TransferStaff from '../views/TransferStaff.vue' 
import AddQuit from '../views/AddQuit.vue' 
import Quit from '../views/Quit.vue' 
import NewStaffReport from '../views/NewStaffReport.vue' 
import QuitStaffReport from '../views/QuitStaffReport.vue' 
import TransferStaffReport from '../views/TransferStaffReport.vue'
import EmpImage from '../views/EmpImage.vue'
import WelfarePoints from '../views/WelfarePoints.vue'
import AddWelfarePoints from '../views/AddWelfarePoints.vue'
import ImageAnalyze from '../views/ImageAnalyze.vue'
import TaskProject from '../views/TaskProject.vue'
import AddTaskProject from '../views/AddTaskProject.vue'
import AddWorkload from '../views/AddWorkload.vue'
import WorkloadDetail from '../views/WorkloadDetail.vue'
import OverallProfile from '../views/OverallProfile.vue'
import PointsRanking from '../views/PointsRanking.vue'
import PointsExchange from '../views/PointsExchange.vue'
const routes = [
  //打开程序直接跳转到登录页面
  {
    path: '/',
    redirect:'/login',
    meta:{auth:true}//登录不需要验证权限
  },
  {
    path: '/login',
    component: Login,
    meta:{auth:true}
  },
  //部门管理
  {
    path: '/home',
    component: Home
  },
  {
    path: '/department',
    component: Department
  },
  {
    path: '/adddepartment',
    component: AddDepartment
  },
  //岗位管理
  {
    path: '/post',
    component: Post
  },
  {
    path: '/addpost',
    component: AddPost
  },
  //员工管理
  {
    path: '/addStaff',
    component: AddStaff
  },
  {
    path: '/staff',
    component: Staff
  },
  //试用期管理
  {
    path: '/peroidOp',
    component: PeroidOp
  },
  //岗位调动管理
  {
    path: '/addTransferStaff',
    component: AddTransferStaff
  },
  {
    path: '/transferStaff',
    component: TransferStaff
  },
   //离职管理
  {
    path: '/addQuit',
    component: AddQuit
  },
  {
    path: '/quit',
    component: Quit
  },
  //报表管理
  {
    path: '/newStaffReport',
    component: NewStaffReport
  },
  {
    path: '/quitStaffReport',
    component: QuitStaffReport
  },
  {
    path: '/transferStaffReport',
    component: TransferStaffReport
  },
  //画像管理
  {
    path: '/empImage',
    component: EmpImage
  },
  {
    path: '/imageAnalyze',
    component: ImageAnalyze
  },
  //福利积分管理
  {
    path: '/welfarePoints',
    component: WelfarePoints
  },
  {
    path: '/addWelfarePoints',
    component: AddWelfarePoints
  },
  //项目管理
  {
    path: '/taskProject',
    component: TaskProject
  },
  {
    path: '/addTaskProject',
    component: AddTaskProject
  },
  //工作量填报
  {
    path: '/addWorkload',
    component: AddWorkload
  },
  {
    path: '/workloadDetail',
    component: WorkloadDetail
  },
  //整体画像
  {
    path: '/overallProfile',
    component: OverallProfile
  },
  //积分排行榜
  {
    path: '/pointsRanking',
    component: PointsRanking
  },
  //积分兑换
  {
    path: '/pointsExchange',
    component: PointsExchange
  }
]
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
