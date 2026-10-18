<template>
  <NavMain></NavMain>
  <div class="exchange-container">
    <el-card class="header-card">
      <template #header>
        <div class="card-header">
          <span style="font-size: 20px; font-weight: bold;">🎁 积分兑换中心</span>
          <div style="margin-left: auto;">
            <span style="color: #909399; margin-right: 15px;">我的积分余额：</span>
            <el-tag type="danger" size="large" effect="dark">
              <strong style="font-size: 18px;">{{ myBalance }}</strong>
            </el-tag>
          </div>
        </div>
      </template>
      
      <el-alert type="info" :closable="false" style="margin-bottom: 20px;">
        <p>💡 积分通过完成高质量、高难度工作自动获得，可用于兑换培训、福利等</p>
      </el-alert>
      
      <el-form :inline="true" style="margin-bottom: 20px;">
        <el-form-item label="选择员工">
          <el-select v-model="staffId" placeholder="请选择员工" @change="handleStaffChange" clearable filterable style="width: 200px;">
            <el-option
              v-for="staff in staffList"
              :key="staff.id"
              :label="`${staff.sname} (ID: ${staff.id})`"
              :value="staff.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadMyBalance">刷新余额</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="🎓 培训课程" name="training">
        <el-row :gutter="20">
          <el-col :span="8" v-for="item in trainingItems" :key="item.id">
            <el-card class="exchange-card" shadow="hover">
              <div class="item-icon">{{ item.icon }}</div>
              <div class="item-title">{{ item.name }}</div>
              <div class="item-desc">{{ item.description }}</div>
              <div class="item-points">
                <el-tag type="warning" size="large">{{ item.points }} 积分</el-tag>
              </div>
              <el-button 
                type="primary" 
                @click="exchangeItem(item)" 
                :disabled="myBalance < item.points"
                style="width: 100%; margin-top: 15px;">
                {{ myBalance >= item.points ? '立即兑换' : '积分不足' }}
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="🏖️ 假期福利" name="leave">
        <el-row :gutter="20">
          <el-col :span="8" v-for="item in leaveItems" :key="item.id">
            <el-card class="exchange-card" shadow="hover">
              <div class="item-icon">{{ item.icon }}</div>
              <div class="item-title">{{ item.name }}</div>
              <div class="item-desc">{{ item.description }}</div>
              <div class="item-points">
                <el-tag type="success" size="large">{{ item.points }} 积分</el-tag>
              </div>
              <el-button 
                type="success" 
                @click="exchangeItem(item)" 
                :disabled="myBalance < item.points"
                style="width: 100%; margin-top: 15px;">
                {{ myBalance >= item.points ? '立即兑换' : '积分不足' }}
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="🎁 实物奖励" name="gift">
        <el-row :gutter="20">
          <el-col :span="8" v-for="item in giftItems" :key="item.id">
            <el-card class="exchange-card" shadow="hover">
              <div class="item-icon">{{ item.icon }}</div>
              <div class="item-title">{{ item.name }}</div>
              <div class="item-desc">{{ item.description }}</div>
              <div class="item-points">
                <el-tag type="danger" size="large">{{ item.points }} 积分</el-tag>
              </div>
              <el-button 
                type="danger" 
                @click="exchangeItem(item)" 
                :disabled="myBalance < item.points"
                style="width: 100%; margin-top: 15px;">
                {{ myBalance >= item.points ? '立即兑换' : '积分不足' }}
              </el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="📜 兑换记录" name="history">
        <el-table :data="exchangeHistory" :header-cell-style="{textAlign: 'center', background: '#f5f7fa'}" 
                  :cell-style="{textAlign: 'center'}">
          <el-table-column prop="exchange_time" label="兑换时间" width="180"></el-table-column>
          <el-table-column prop="item_name" label="兑换项目" width="200"></el-table-column>
          <el-table-column prop="item_type" label="类型" width="120">
            <template #default="scope">
              <el-tag :type="getTypeTag(scope.row.item_type)">{{ getTypeName(scope.row.item_type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="points_used" label="消耗积分" width="120">
            <template #default="scope">
              <el-tag type="danger">-{{ scope.row.points_used }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'completed' ? 'success' : 'warning'">
                {{ scope.row.status === 'completed' ? '已完成' : '处理中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注"></el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import NavMain from '@/components/NavMain.vue'

export default {
  components: {
    NavMain
  },
  data() {
    return {
      activeTab: 'training',
      myBalance: 0,
      staffId: null,
      staffList: [],
      trainingItems: [
        {id: 1, icon: '💻', name: 'Java高级开发', description: '深入理解JVM、并发编程、性能调优', points: 100, type: 'training'},
        {id: 2, icon: '🎨', name: 'Vue3实战课程', description: '组件化、状态管理、性能优化', points: 80, type: 'training'},
        {id: 3, icon: '🗄️', name: 'MySQL优化', description: '索引优化、查询调优、高可用架构', points: 90, type: 'training'},
        {id: 4, icon: '🔧', name: 'Docker容器技术', description: '容器化部署、镜像构建、编排管理', points: 85, type: 'training'},
        {id: 5, icon: '☁️', name: '微服务架构', description: 'Spring Cloud、服务治理、分布式事务', points: 120, type: 'training'},
        {id: 6, icon: '📊', name: '数据分析与可视化', description: 'Python数据分析、ECharts可视化', points: 70, type: 'training'}
      ],
      leaveItems: [
        {id: 11, icon: '🏖️', name: '调休假1天', description: '可用于调休或事假', points: 50, type: 'leave'},
        {id: 12, icon: '🌴', name: '调休假3天', description: '可用于调休或事假', points: 130, type: 'leave'},
        {id: 13, icon: '🏝️', name: '带薪假1天', description: '额外带薪休假天数', points: 80, type: 'leave'},
        {id: 14, icon: '⛱️', name: '带薪假3天', description: '额外带薪休假天数', points: 220, type: 'leave'}
      ],
      giftItems: [
        {id: 21, icon: '📚', name: '技术书籍', description: '价值200元技术书籍任选', points: 60, type: 'gift'},
        {id: 22, icon: '⌨️', name: '机械键盘', description: '程序员必备装备', points: 150, type: 'gift'},
        {id: 23, icon: '🎧', name: '降噪耳机', description: '提升专注度', points: 180, type: 'gift'},
        {id: 24, icon: '🖥️', name: '显示器升级', description: '升级为27寸4K显示器', points: 300, type: 'gift'},
        {id: 25, icon: '💼', name: '电脑包', description: '高品质双肩电脑包', points: 80, type: 'gift'},
        {id: 26, icon: '🎮', name: '团建活动', description: '部门团建活动名额', points: 120, type: 'gift'}
      ],
      exchangeHistory: []
    }
  },
  created() {
    this.loadStaffList()
    // 从URL参数获取员工ID，如果没有则提示选择
    if (this.$route.query.staffId) {
      this.staffId = parseInt(this.$route.query.staffId)
      this.loadMyBalance()
      this.loadExchangeHistory()
    } else {
      this.$message.info('请先选择员工查看积分余额')
    }
  },
  methods: {
    /**
     * 加载员工列表
     * 用于员工选择下拉框
     */
    async loadStaffList() {
      try {
        const response = await this.$axios.get('/getStaffByPage?currentPage=1&pageSize=1000')
        if (response.data && response.data.staffs) {
          this.staffList = response.data.staffs
        } else if (Array.isArray(response.data)) {
          this.staffList = response.data
        }
      } catch (error) {
        console.error('加载员工列表失败:', error)
      }
    },
    
    /**
     * 员工选择变化时重新加载数据
     */
    handleStaffChange() {
      if (this.staffId) {
        this.loadMyBalance()
        this.loadExchangeHistory()
      } else {
        this.myBalance = 0
        this.exchangeHistory = []
      }
    },
    
    /**
     * 加载我的积分余额
     * 从后端获取当前员工的可用积分余额
     */
    async loadMyBalance() {
      if (!this.staffId) {
        this.$message.warning('请先选择员工')
        return
      }
      try {
        console.log('加载积分余额，员工ID:', this.staffId)
        const response = await this.$axios.get('/getPointsSummary', { params: { staff_id: this.staffId } })
        console.log('积分余额响应:', response.data)
        // 确保余额不为负数
        const balance = response.data?.current_available || 0
        this.myBalance = Math.max(0, balance)
        console.log('当前余额:', this.myBalance, '(原始值:', balance, ')')
        console.log('总获得:', response.data?.total_earned, '总使用:', response.data?.total_used)
      } catch (error) {
        console.error('加载积分余额失败:', error)
        this.$message.error('加载积分余额失败: ' + (error.response?.data?.message || error.message))
      }
    },
    /**
     * 加载兑换历史记录
     * 获取当前员工的所有积分使用记录（transaction_type='USE'）
     */
    async loadExchangeHistory() {
      if (!this.staffId) {
        this.exchangeHistory = []
        return
      }
      try {
        console.log('加载兑换记录，员工ID:', this.staffId)
        const response = await this.$axios.get('/getWelfarePointsByStaffId', { 
          params: { staff_id: this.staffId } 
        })
        console.log('兑换记录响应:', response.data)
        const pointsList = response.data?.pointsList || []
        this.exchangeHistory = pointsList
          .filter(p => p.transaction_type === 'USE')
          .map(p => ({
            exchange_time: p.transaction_time1 || p.transaction_time,
            item_name: p.source_desc || p.points_type,
            item_type: this.inferItemType(p.points_type),
            points_used: p.used_points || p.points_value,
            status: 'completed',
            remark: p.remark || '-'
          }))
        console.log('兑换记录条数:', this.exchangeHistory.length)
      } catch (error) {
        console.error('加载兑换记录失败:', error)
        this.$message.error('加载兑换记录失败: ' + (error.response?.data?.message || error.message))
      }
    },
    inferItemType(pointsType) {
      if (!pointsType) return 'gift'
      if (pointsType.includes('培训') || pointsType.includes('课程')) return 'training'
      if (pointsType.includes('假') || pointsType.includes('休')) return 'leave'
      return 'gift'
    },
    exchangeItem(item) {
      this.$confirm(`确认使用 ${item.points} 积分兑换"${item.name}"吗？`, '确认兑换', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitExchange(item)
      }).catch(() => {})
    },
    async submitExchange(item) {
      if (!this.staffId) {
        this.$message.warning('请先选择员工')
        return
      }
      if (this.myBalance < item.points) {
        this.$message.warning('积分不足，无法兑换')
        return
      }
      try {
        const exchangeRecord = {
          staff_id: this.staffId,
          points_type: item.name,
          points_value: 0,
          points_source: '积分兑换',
          source_desc: `兑换：${item.name}`,
          transaction_type: 'USE',
          used_points: item.points,
          available_points: 0,
          operator: 'self',
          remark: item.description,
          status: 1
        }
        
        const response = await this.$axios.post('/addWelfarePoints', exchangeRecord)
        if (response.data === 'ok') {
          this.$message.success('兑换成功！积分已扣除')
          this.myBalance -= item.points
          this.loadExchangeHistory()
        } else {
          this.$message.error('兑换失败，请稍后重试')
        }
      } catch (error) {
        console.error('兑换失败:', error)
        this.$message.error('兑换失败: ' + (error.message || '网络错误'))
      }
    },
    handleTabClick(tab) {
      console.log('切换标签:', tab.props.name)
    },
    getTypeTag(type) {
      const tagMap = {
        'training': 'warning',
        'leave': 'success',
        'gift': 'danger'
      }
      return tagMap[type] || 'info'
    },
    getTypeName(type) {
      const nameMap = {
        'training': '培训课程',
        'leave': '假期福利',
        'gift': '实物奖励'
      }
      return nameMap[type] || '其他'
    }
  }
}
</script>

<style scoped>
.exchange-container {
  padding: 20px;
  margin-left: 240px;
  min-height: 100vh;
  background: #f0f2f5;
}

.header-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
}

.exchange-card {
  margin-bottom: 20px;
  text-align: center;
  transition: transform 0.3s;
}

.exchange-card:hover {
  transform: translateY(-5px);
}

.item-icon {
  font-size: 48px;
  margin: 20px 0;
}

.item-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.item-desc {
  font-size: 14px;
  color: #909399;
  min-height: 40px;
  margin-bottom: 15px;
}

.item-points {
  margin: 15px 0;
}
</style>

