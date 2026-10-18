<template>
  <NavMain />
  <div class="box">
    <el-form :inline="true" style="margin-bottom: 16px;">
      <el-form-item label="员工ID">
        <el-input v-model="staffId" placeholder="请输入员工ID"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData" :loading="loading">加载整体画像</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12">
      <el-col :span="6">
        <el-card shadow="hover" class="card">
          <div class="card-title">基本信息</div>
          <div class="card-item">姓名：{{ staff?.sname || '-' }}</div>
          <div class="card-item">部门：{{ staff?.dname || '-' }}</div>
          <div class="card-item">岗位：{{ staff?.pname || '-' }}</div>
          <div class="card-item">年龄：{{ basicStats.age ?? '-' }}</div>
          <div class="card-item">入职天数：{{ basicStats.tenure_days ?? '-' }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="card">
          <div class="card-title">工作量</div>
          <div class="card-item">总工时：{{ workload.total_hours ?? 0 }}</div>
          <div class="card-item">记录数：{{ workload.total_records ?? 0 }}</div>
          <div class="card-item">平均难度：{{ (workload.avg_task_weight || 0).toFixed(2) }}</div>
          <div class="card-item">平均质量：{{ ((workload.avg_quality_score || 0) * 5).toFixed(1) }} 星</div>
          <div class="card-item">ECI：{{ (workload.eci_score || 0).toFixed(2) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="card">
          <div class="card-title">积分</div>
          <div class="card-item">累计获得：{{ points.total_earned ?? 0 }}</div>
          <div class="card-item">已使用：{{ points.total_used ?? 0 }}</div>
          <div class="card-item">当前可用：{{ points.current_available ?? 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="card">
          <div class="card-title">画像摘要</div>
          <div class="card-item" v-if="hasImageSummary">
            能力画像：{{ imageSummary.ability?.ability_score ?? '-' }}
          </div>
          <div class="card-item" v-if="hasImageSummary">
            绩效画像：{{ imageSummary.performance?.ability_score ?? '-' }}
          </div>
          <div class="card-item" v-if="hasImageSummary">
            潜力画像：{{ imageSummary.potential?.ability_score ?? '-' }}
          </div>
          <div class="card-item" v-if="!hasImageSummary">无画像记录，请先在画像分析页生成。</div>
          <div class="card-item">
            标签：
            <span v-if="staff?.skill_tags">
              <el-tag v-for="tag in staff.skill_tags.split(',').filter(t=>t)" :key="tag" size="small" style="margin:2px;">{{ tag }}</el-tag>
            </span>
            <span v-else>-</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import NavMain from '@/components/NavMain.vue'
export default {
  components: { NavMain },
  data() {
    return {
      staffId: '',
      loading: false,
      staff: null,
      basicStats: {},
      workload: {},
      points: {},
      imageSummary: {}
    }
  },
  created() {
    if (this.$route.query.staff_id) {
      this.staffId = this.$route.query.staff_id
      this.loadData()
    }
  },
  methods: {
    async loadData() {
      if (!this.staffId) {
        this.$message.warning('请输入员工ID')
        return
      }
      this.loading = true
      try {
        const res = await this.$axios.get('/api/profile/overall', { params: { staffId: this.staffId } })
        this.staff = res.data.staff || null
        this.basicStats = res.data.basic_stats || {}
        this.workload = res.data.workload_stats || {}
        this.points = res.data.points_summary || {}
        this.imageSummary = res.data.image_summary || {}
        this.$message.success('加载完成')
      } catch (e) {
        this.$message.error('加载失败: ' + (e.message || ''))
      } finally {
        this.loading = false
      }
    }
  },
  computed: {
    hasImageSummary() {
      return !!(this.imageSummary && (this.imageSummary.ability || this.imageSummary.performance || this.imageSummary.potential))
    }
  }
}
</script>

<style scoped>
.box { padding: 20px; }
.card { min-height: 220px; }
.card-title { font-weight: 600; margin-bottom: 8px; }
.card-item { margin: 4px 0; }
</style>

