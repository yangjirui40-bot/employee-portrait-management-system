<template>
  <div class="nav-left" v-if="injected">
      <div class="nav-logo">
        <div class="logo-avatar"><img :src="logo" alt="logo" class="logo-img" /></div>
        <div class="logo-text"><h2>人事管理</h2></div>
        <button class="collapse-btn" @click.stop="toggleCollapse" title="折叠侧栏" v-if="!isMobile">⟨⟩</button>
      </div>
    <div class="nav-user">
      <div class="user-name">{{ userName }}</div>
      <div class="user-actions"><a @click.prevent="logout">登出</a></div>
    </div>
    <button class="hamburger" @click.stop="toggleMobile" v-if="isMobile">☰</button>

    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="vertical"
      @select="handleSelect"
      background-color="transparent"
      text-color="#fff"
      active-text-color="#ffd04b">
      <el-menu-item index="0"><router-link to="/home" class="a"><i class="el-icon-house" style="margin-right: 8px;"></i>主页</router-link></el-menu-item>
      <el-submenu index="1">
        <template #title><i class="el-icon-office-building" style="margin-right: 8px;"></i><span>部门管理</span></template>
        <el-menu-item index="1-1"><router-link to="/department" class="a">管理部门</router-link></el-menu-item>
        <el-menu-item index="1-2"><router-link to="/adddepartment" class="a">新增部门</router-link></el-menu-item>
      </el-submenu>
      <el-submenu index="2">
        <template #title><i class="el-icon-briefcase" style="margin-right: 8px;"></i><span>岗位管理</span></template>
        <el-menu-item index="2-1"><router-link to="/post" class="a">管理岗位</router-link></el-menu-item>
        <el-menu-item index="2-2"><router-link to="/addpost" class="a">新增岗位</router-link></el-menu-item>
      </el-submenu>
      <el-submenu index="3">
        <template #title><i class="el-icon-user" style="margin-right: 8px;"></i><span>员工管理</span></template>
        <el-menu-item index="3-1"><router-link to="/staff" class="a">管理员工</router-link></el-menu-item>
        <el-menu-item index="3-2"><router-link to="/addStaff" class="a">新增员工</router-link></el-menu-item>
        <el-menu-item index="3-3"><router-link to="/addTransferStaff" class="a">录入岗位调动</router-link></el-menu-item>
        <el-menu-item index="3-4"><router-link to="/transferStaff" class="a">查询调动员工</router-link></el-menu-item>
        <el-menu-item index="3-5"><router-link to="/peroidOp" class="a">试用期管理</router-link></el-menu-item>
      </el-submenu>

      <el-submenu index="6">
        <template #title><i class="el-icon-switch-button" style="margin-right: 8px;"></i><span>员工离职管理</span></template>
        <el-menu-item index="6-1"><router-link to="/addQuit" class="a">录入离职员工</router-link></el-menu-item>
        <el-menu-item index="6-2"><router-link to="/quit" class="a">查询已离职员工</router-link></el-menu-item>
      </el-submenu>
      <el-submenu index="7">
        <template #title><i class="el-icon-data-analysis" style="margin-right: 8px;"></i><span>报表管理</span></template>
        <el-menu-item index="7-1"><router-link to="/newStaffReport" class="a">新聘员工报表</router-link></el-menu-item>
        <el-menu-item index="7-2"><router-link to="/quitStaffReport" class="a">离职员工报表</router-link></el-menu-item>
        <el-menu-item index="7-3"><router-link to="/transferStaffReport" class="a">岗位调动报表</router-link></el-menu-item>
      </el-submenu>
      <el-submenu index="8">
        <template #title><i class="el-icon-picture" style="margin-right: 8px;"></i><span>画像管理</span></template>
        <el-menu-item index="8-1"><router-link to="/empImage" class="a">管理画像</router-link></el-menu-item>
        <el-menu-item index="8-2"><router-link to="/imageAnalyze" class="a">画像分析</router-link></el-menu-item>
      </el-submenu>
      <el-submenu index="9">
        <template #title><i class="el-icon-present" style="margin-right: 8px;"></i><span>福利积分管理</span></template>
        <el-menu-item index="9-1"><router-link to="/pointsRanking" class="a">积分排行榜</router-link></el-menu-item>
        <el-menu-item index="9-2"><router-link to="/welfarePoints" class="a">积分记录</router-link></el-menu-item>
        <el-menu-item index="9-3"><router-link to="/pointsExchange" class="a">积分兑换</router-link></el-menu-item>
      </el-submenu>
      <el-submenu index="10">
        <template #title><i class="el-icon-folder" style="margin-right: 8px;"></i><span>工作量管理</span></template>
        <el-menu-item index="10-1"><router-link to="/taskProject" class="a">项目管理</router-link></el-menu-item>
        <el-menu-item index="10-2"><router-link to="/addTaskProject" class="a">新增项目</router-link></el-menu-item>
        <el-menu-item index="10-3"><router-link to="/addWorkload" class="a">工作量填报</router-link></el-menu-item>
        <el-menu-item index="10-4"><router-link to="/workloadDetail" class="a">工作量明细</router-link></el-menu-item>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
import logoSrc from '@/assets/logo.jpg'

export default {
  name: 'NavMain',
  data() {
    return {
      activeIndex: '9',
      injected: false,
      collapsed: false,
      mobileOpen: false
      ,logo: logoSrc
    }
  },
  created() {
    if (window.__nav_injected) {
      this.injected = false
    } else {
      window.__nav_injected = true
      this.injected = true
    }
  },
  computed: {
    userName() {
      try {
        const u = window.sessionStorage.getItem('user')
        if (!u) return '访客'
        const parsed = JSON.parse(u)
        return parsed && parsed.username ? parsed.username : parsed.name || '用户'
      } catch (e) {
        return '用户'
      }
    }
  },
  watch: {
    // close mobile menu on route change
    '$route'() {
      if (this.isMobile) this.mobileOpen = false
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath)
    },
    logout() {
      window.sessionStorage.removeItem('user')
      this.$store && this.$store.commit && this.$store.commit('changeLogin', '0')
      this.$router && this.$router.push && this.$router.push('/login')
    }
    ,toggleCollapse() {
      this.collapsed = !this.collapsed
      const el = document.documentElement
      if (this.collapsed) {
        el.style.setProperty('--sidebar-width', '64px')
      } else {
        el.style.setProperty('--sidebar-width', '220px')
      }
    },
    toggleMobile() {
      this.mobileOpen = !this.mobileOpen
      const nav = this.$el
      if (this.mobileOpen) nav && (nav.style.transform = 'translateX(0)')
      else nav && (nav.style.transform = '')
    }
  }
}
</script>

<style>
.nav-left{
  position: fixed;
  left: 0;
  top: 64px; /* leave space for top header */
  bottom: 0;
  width: var(--sidebar-width,220px);
  background: linear-gradient(180deg,#0f1724 0%, #10243a 60%);
  color: var(--sidebar-text, #fff);
  padding-top: 8px;
  overflow-y: auto;
  box-shadow: 2px 0 8px rgba(2,6,23,0.3);
  transition: width .22s ease;
}
.collapse-btn{position:absolute;right:8px;top:8px;background:transparent;border:none;color:#fff;cursor:pointer;padding:6px;border-radius:4px}
.hamburger{display:none;position:fixed;left:12px;top:74px;z-index:1200;background:#10243a;color:#fff;border:1px solid rgba(255,255,255,0.04);padding:6px 8px;border-radius:6px}
.mi{display:inline-block;width:20px;text-align:center;margin-right:8px;flex-shrink:0}
@media (max-width: 768px){
  .nav-left{transform:translateX(-110%);transition:transform .18s ease;z-index:1200}
  .hamburger{display:block}
  .nav-left[style*="transform: translateX(0)"]{transform:translateX(0)}
  .nav-left{position:fixed;top:64px}
}
.nav-left h2{
  color: #fff;
  text-align: center;
  margin: 8px 0 12px 0;
  font-size: 18px;
  font-weight: 600;
}
.nav-logo{display:flex;align-items:center;gap:10px;padding:12px 16px;border-bottom:1px solid rgba(255,255,255,0.04)}
.logo-avatar{width:44px;height:44px;border-radius:8px;display:flex;align-items:center;justify-content:center;overflow:hidden;box-shadow:0 2px 6px rgba(0,0,0,0.3);background:rgba(255,255,255,0.02)}
.logo-img{width:100%;height:100%;object-fit:cover;display:block}
.logo-text h2{font-size:15px;margin:0;color:#fff}
.el-menu-demo{
  background: transparent;
  border-right: none;
}
.el-menu-demo .el-submenu__title{
  color: rgba(255,255,255,0.92) !important;
  font-size: 15px;
  padding-left: 12px !important;
  display:flex !important;
  align-items:center !important;
  gap:8px;
}
.el-menu-demo .el-menu-item{
  color: rgba(255,255,255,0.9) !important;
  font-size: 14px;
  padding: 10px 16px;
  transition: background .16s ease,color .16s ease,transform .12s ease;
  display:flex;
  align-items:center;
  gap:8px;
}
.el-menu-demo .el-menu-item:hover{background:rgba(255,255,255,0.03);transform:none}
.el-menu-demo .el-menu-item a,
.a{
  text-decoration: none;
  color: inherit;
  font-size: 14px;
  display:flex;
  align-items:center;
  width:100%;
}
.el-menu-demo .el-submenu__title .el-icon{margin-right:8px}
.nav-left::-webkit-scrollbar{width:8px}
.nav-left::-webkit-scrollbar-thumb{background:rgba(255,255,255,0.06);border-radius:8px}
.nav-user{padding:12px 16px;border-top:1px solid rgba(255,255,255,0.03);display:flex;align-items:center;justify-content:space-between;color:rgba(255,255,255,0.85)}
.nav-user .user-name{font-size:13px}
.nav-user .user-actions a{color:#ffd04b;cursor:pointer}
/* Active / selected styles - lighter tone */
.el-menu-demo .el-menu-item.is-active,
.el-menu-demo .el-submenu.is-active > .el-submenu__title {
  background: #e6f7ff !important; /* 浅蓝 */
  color: #1f3a93 !important; /* 深蓝文字 */
}
.el-menu-demo .el-menu-item.is-active a,
.el-menu-demo .el-submenu.is-active > .el-submenu__title {
  color: #1f3a93 !important;
}
.el-menu-demo .el-submenu.is-opened .el-menu-item,
.el-menu-demo .el-submenu.is-active .el-menu-item {
  background: transparent;
  color: #aaa !important;
}
</style>
