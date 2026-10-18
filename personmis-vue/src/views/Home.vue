<template>
  <div class="home-page">
    <div class="overlay"></div>

    <header class="hero">
      <div class="hero-inner">
        <div class="title-wrap">
          <h1 class="company-title">懒羊羊股份有限公司</h1>
          <p class="subtitle">欢迎回到人事管理系统</p>
        </div>
        <div class="hero-stats">
          <div class="stat">
            <div class="num">{{ departmentCount }}</div>
            <div class="label">部门总数</div>
          </div>
          <div class="stat">
            <div class="num">{{ staffCount }}</div>
            <div class="label">员工总数</div>
          </div>
        </div>
      </div>
    </header>

    <section class="cards">
      <article class="card">
        <div class="card-head"><span class="card-icon">👑</span><h3>核心人员</h3></div>
          <div class="card-body">
            <p class="muted">公司核心管理团队</p>
            <div class="staff-grid">
              <div class="staff-item">
                <div class="staff-avatar">
                  <img src="~@/assets/zongcai.jpg" alt="总裁" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">
                </div>
                <div class="staff-info">
                  <div class="staff-name">懒羊羊大王</div>
                  <div class="staff-post">总裁</div>
                </div>
              </div>
              <div class="staff-item">
                <div class="staff-avatar">
                  <img src="~@/assets/COO.jpg" alt="COO" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">
                </div>
                <div class="staff-info">
                  <div class="staff-name">懒小羊</div>
                  <div class="staff-post">COO</div>
                </div>
              </div>
              <div class="staff-item">
                <div class="staff-avatar">
                  <img src="~@/assets/CTO.jpg" alt="CTO" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">
                </div>
                <div class="staff-info">
                  <div class="staff-name">懒大王</div>
                  <div class="staff-post">CTO</div>
                </div>
              </div>
              <div class="staff-item">
                <div class="staff-avatar">
                  <img src="~@/assets/CHRO.jpg" alt="CHRO" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">
                </div>
                <div class="staff-info">
                  <div class="staff-name">懒羊羊</div>
                  <div class="staff-post">CHRO</div>
                </div>
              </div>
            </div>
          </div>
      </article>

      <article class="card">
        <div class="card-head"><span class="card-icon">🏢</span><h3>部门</h3></div>
        <div class="card-body">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
            <p class="muted">显示部分部门</p>
            <span style="font-size: 12px; color: rgba(255,216,107,0.8);">{{ departments.length }} 个部门</span>
          </div>
          <ul class="dept-list">
            <li v-for="dept in departments.slice(0,6)" :key="dept.id">
              <span class="dept-number"></span>
              <span class="dept-name">{{ dept.dname }}</span>
            </li>
            <li v-if="departments.length===0" class="no-data">
              <span>📁</span>
              <span>暂无部门数据</span>
            </li>
          </ul>
          <div v-if="departments.length > 6" style="margin-top: 12px; text-align: center;">
            <span style="font-size: 12px; color: rgba(255,255,255,0.6);">仅显示前 6 个部门</span>
          </div>
        </div>
      </article>

      <article class="card contact">
        <div class="card-head"><span class="card-icon">📋</span><h3>使用说明</h3></div>
        <div class="card-body">
          <p>通过左侧导航进入员工、部门、项目与画像管理。</p>
          <p>填写工作量后，可查看画像分析、项目绩效与福利积分。</p>
        </div>
      </article>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      departmentCount: 0,
      staffCount: 0,
      departments: [],
      staffs: [],
      featuredStaffs: []
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
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        // 部门列表
        const deptResp = await this.$axios.get('/getDepartment')
        if (deptResp && deptResp.data) {
          const depts = Array.isArray(deptResp.data) ? deptResp.data : (deptResp.data.departs || [])
          this.departments = depts
          this.departmentCount = depts.length
        }

        // 员工列表
        let staffResp = await this.$axios.get('/getStaff').catch(() => null)
        if (!staffResp) {
          staffResp = await this.$axios.get('/getStaffByPage?currentPage=1&pageSize=1000').catch(() => null)
        }
        if (staffResp && staffResp.data) {
          let staffList = []
          if (Array.isArray(staffResp.data)) {
            staffList = staffResp.data
            this.staffCount = staffResp.data.length
          } else if (staffResp.data.staffs) {
            staffList = staffResp.data.staffs
            this.staffCount = staffResp.data.staffs.length
          } else if (Array.isArray(staffResp.data.list)) {
            staffList = staffResp.data.list
            this.staffCount = staffResp.data.list.length
          } else if (typeof staffResp.data.total === 'number') {
            this.staffCount = staffResp.data.total
          }
          this.staffs = staffList
          // 随机选取最多5名员工作为首页展示
          this.featuredStaffs = this.pickRandom(staffList, 5)
        }
      } catch (err) {
        console.error('loadData error', err)
      }
    }
    ,
    pickRandom(list, n) {
      if (!Array.isArray(list)) return []
      const copy = list.slice()
      for (let i = copy.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1))
        const tmp = copy[i]
        copy[i] = copy[j]
        copy[j] = tmp
      }
      return copy.slice(0, n)
    }
  }
}
</script>

<style scoped>
.home-page{
  position: relative;
  width: 100%;
  min-height: 100vh;
  overflow: auto;
  background: url(~@/assets/lanyangyang-bg.jpg) center/cover no-repeat;
  padding: 24px;
  box-sizing: border-box;
  color: #fff;
}

.overlay{
  position: absolute;inset:0;background:linear-gradient(180deg,rgba(6,21,34,0.55),rgba(6,21,34,0.7));backdrop-filter: blur(4px);pointer-events:none;
}

.hero{position:relative;padding:36px 24px 20px;display:flex;align-items:center;justify-content:center}
.hero-inner{width:100%;max-width:1100px;display:flex;align-items:center;justify-content:space-between;gap:20px;z-index:2}
.title-wrap{display:flex;flex-direction:column}
.company-title{font-size:32px;margin:0 0 6px 0;color:#ffd86b;text-shadow:0 4px 18px rgba(0,0,0,0.45)}
.subtitle{margin:0;color:rgba(255,255,255,0.85)}
.hero-stats{display:flex;gap:12px}
.stat{background:rgba(255,255,255,0.04);padding:10px 14px;border-radius:10px;text-align:center;min-width:110px}
.stat .num{font-size:20px;font-weight:700;color:#ffd86b}
.stat .label{font-size:12px;color:rgba(255,255,255,0.75)}

.cards{z-index:2;max-width:1100px;margin:20px auto 60px;display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:18px}
.card{background:linear-gradient(180deg,rgba(255,255,255,0.03),rgba(255,255,255,0.02));border-radius:12px;padding:16px;box-shadow:0 6px 18px rgba(4,12,28,0.45);backdrop-filter: blur(6px);}
.card-head{display:flex;align-items:center;gap:12px;margin-bottom:8px}
.card-icon{width:36px;height:36px;border-radius:8px;background:rgba(255,255,255,0.04);display:flex;align-items:center;justify-content:center}
.card h3{margin:0;font-size:16px}
.card-body .muted{color:rgba(255,255,255,0.7);font-size:13px;margin-bottom:8px}
.dept-list{list-style:none;padding:0;margin:0;font-size:14px}
.dept-list li{
  padding:12px 14px;
  margin-bottom:8px;
  border-radius:8px;
  background:linear-gradient(135deg,rgba(255,255,255,0.03),rgba(255,255,255,0.01));
  border:1px solid rgba(255,255,255,0.04);
  transition:all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display:flex;
  align-items:center;
  gap:12px;
  box-shadow:0 2px 4px rgba(0,0,0,0.1);
}
.dept-list li:hover{
  background:linear-gradient(135deg,rgba(255,216,107,0.15),rgba(255,122,24,0.08));
  border-color:rgba(255,216,107,0.4);
  transform:translateX(6px) translateY(-2px);
  box-shadow:0 4px 12px rgba(255,216,107,0.2);
}
.dept-number{
  width:20px;
  height:20px;
  border-radius:50%;
  background:rgba(255,216,107,0.3);
  border:2px solid rgba(255,216,107,0.4);
  flex-shrink:0;
}
.dept-name{
  color:#fff;
  font-weight:500;
  flex:1;
}
.dept-list li.no-data{
  justify-content:center;
  color:rgba(255,255,255,0.6);
  background:rgba(255,255,255,0.01);
  border-style:dashed;
}
.dept-list li.no-data:hover{
  transform:none;
  background:rgba(255,255,255,0.02);
  box-shadow:0 2px 4px rgba(0,0,0,0.1);
}
.avatars{display:flex;gap:8px}
.avatar{width:36px;height:36px;border-radius:50%;background:linear-gradient(135deg,#ffd86b,#ff7a18);display:flex;align-items:center;justify-content:center;color:#102;box-shadow:0 2px 8px rgba(0,0,0,0.3)}

.staff-grid{display:flex;flex-wrap:wrap;gap:12px}
.staff-item{display:flex;align-items:center;gap:10px;background:rgba(255,255,255,0.02);padding:8px 12px;border-radius:8px;min-width:180px}
.staff-avatar{width:40px;height:40px;border-radius:50%;background:linear-gradient(135deg,#ffd86b,#ff7a18);display:flex;align-items:center;justify-content:center;color:#102;font-weight:700}
.staff-info{display:flex;flex-direction:column}
.staff-name{font-size:14px;color:#fff}
.staff-post{font-size:12px;color:rgba(255,255,255,0.7)}

@media (max-width:800px){
  .hero-inner{flex-direction:column;align-items:flex-start}
  .hero-stats{width:100%;display:flex}
}
</style>
