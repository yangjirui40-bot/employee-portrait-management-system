<template>
  <NavMain></NavMain>
  <!-- 查询表单区域 - 添加边框 -->
  <div class="box1">
    <br>
    <el-form ref="selectForm" :model="selectForm" style="width:50%;" label-width="50%">
      <el-form-item label="员工ID" prop="staff_id">
        <el-input v-model="selectForm.staff_id" placeholder="请输入员工ID"></el-input>
      </el-form-item>

      <el-form-item label="积分类型" prop="points_type">
        <el-select v-model="selectForm.points_type" placeholder="请选择积分类型">
          <el-option v-for="(item,index) in pointsTypes" :key="index" :label="item" :value="item"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="交易类型" prop="transaction_type">
        <el-select v-model="selectForm.transaction_type" placeholder="请选择交易类型">
          <el-option v-for="(item,index) in transactionTypes" :key="index" :label="item.label" :value="item.value"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="selectWelfarePointsByCon">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="success" @click="handleBatchSync" :loading="batchSyncing">
          <i class="el-icon-refresh"></i> 批量同步（积分+画像）
        </el-button>
      </el-form-item>
    </el-form>
  </div>

  <!-- 数据表格区域 -->
  <div class="box2">
    <el-table
        :data="tableData"
        :header-cell-style="headClass"
        :cell-style="{ textAlign: 'center' }"
        :default-sort="{prop: 'id', order: 'descending'}"
        stripe
        border
        v-loading="loading"
    >
      <el-table-column prop="id" label="ID" sortable width="80"></el-table-column>
      <el-table-column prop="staff_id" label="员工ID" sortable width="100"></el-table-column>
      <el-table-column prop="sname" label="员工姓名" width="120"></el-table-column>
      <el-table-column prop="points_type" label="积分类型" width="120"></el-table-column>
      <el-table-column prop="points_value" label="积分值" width="100">
        <template #default="scope">
          <span style="color: #333;">{{ scope.row.points_value ?? 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="current_available" label="可用积分" width="100">
        <template #default="scope">
          <span style="color: #333; font-weight: bold;">{{ scope.row.current_available ?? 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="transaction_type" label="交易类型" width="120"></el-table-column>
      <el-table-column prop="transaction_time1" label="交易时间" width="180">
        <template #default="scope">
          <span style="color: #333;">
            {{ scope.row.transaction_time1 || formatTransactionTime(null, scope.row.transaction_time) || '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="mini" type="info" @click="handleSummary(scope.$index, scope.row)">汇总</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <!-- 分页 -->
  <div class="block">
    <el-pagination
        @current-change="handleCurrentChange"
        v-model:currentPage="currentPage"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
    >
    </el-pagination>
  </div>
</template>

<script>
/**
 * 福利积分管理页面
 * 
 * 功能说明：
 * 1. 积分记录查询（支持员工ID/积分类型/交易类型筛选）
 * 2. 积分记录列表展示（分页/排序）
 * 3. 积分汇总查询（查看员工累计获得/已使用/当前可用积分）
 * 4. 批量同步（为所有工作量记录生成积分和画像）
 * 
 * 关键数据来源：
 * - /getWelfarePointsByPage - 分页查询积分记录
 * - /selectWelfarePointsByCon - 条件查询积分记录
 * - /getPointsSummary - 获取积分汇总
 * - /batchSyncAll - 批量同步积分和画像
 */
import NavMain from '@/components/NavMain.vue'

export default {
  components: {
    NavMain
  },
  created: function() {
    this.loadWelfarePoints();
  },
  data() {
    return {
      loading: false,          // 加载状态
      batchSyncing: false,     // 批量同步状态
      // 积分类型选项
      pointsTypes: ['年度积分', '月度积分', '奖励积分', '消费积分', '其他积分'],
      // 交易类型选项（获得/使用/调整）
      transactionTypes: [
        { label: '获得', value: 'EARN' },
        { label: '使用', value: 'USE' },
        { label: '调整', value: 'ADJUST' }
      ],
      // 查询条件表单
      selectForm: {
        staff_id: '',         // 员工ID筛选
        points_type: '',       // 积分类型筛选
        transaction_type: ''   // 交易类型筛选
      },
      currentPage: 1,         // 当前页码
      tableData: [],          // 积分记录列表数据
      pageSize: 10,           // 每页显示数量
      total: 0                // 总记录数
    }
  },
  methods: {
    /**
     * 加载积分记录列表
     * 页面初始化时调用，加载所有积分记录
     */
    loadWelfarePoints() {
      this.loading = true;
      console.log('加载积分记录，页码:', this.currentPage, '每页:', this.pageSize);
      this.$axios.get('/getWelfarePointsByPage?currentPage=' + this.currentPage + '&pageSize=' + this.pageSize)
          .then(successResponse => {
            console.log('积分记录响应:', successResponse.data);
            this.tableData = successResponse.data.welfarePoints || successResponse.data.points || [];
            this.total = successResponse.data.total || 0;
            console.log('加载到的数据条数:', this.tableData.length, '总数:', this.total);
            // 调试：打印第一条数据，检查字段
            if (this.tableData.length > 0) {
              console.log('第一条数据示例:', this.tableData[0]);
              console.log('points_value:', this.tableData[0].points_value, typeof this.tableData[0].points_value);
              console.log('available_points:', this.tableData[0].available_points, typeof this.tableData[0].available_points);
              console.log('transaction_time1:', this.tableData[0].transaction_time1, typeof this.tableData[0].transaction_time1);
              console.log('transaction_time:', this.tableData[0].transaction_time, typeof this.tableData[0].transaction_time);
            }
            if (this.tableData.length === 0 && this.total === 0) {
              this.$message.info('暂无积分记录');
            }
          })
          .catch(failResponse => {
            console.error('加载数据失败:', failResponse);
            this.$message.error('加载失败: ' + (failResponse.response?.data?.message || failResponse.message));
            this.tableData = [];
            this.total = 0;
          })
          .finally(() => {
            this.loading = false;
          });
    },
    /**
     * 条件查询积分记录
     * 根据查询条件（员工ID/积分类型/交易类型）获取积分记录列表
     */
    selectWelfarePointsByCon() {
      this.loading = true;
      // 查询时包含分页参数
      const queryParams = {
        ...this.selectForm,
        currentPage: this.currentPage,
        pageSize: this.pageSize
      };
      this.$axios.post('/selectWelfarePointsByCon', queryParams)
          .then(successResponse => {
            console.log('查询响应:', successResponse.data);
            this.tableData = successResponse.data.welfarePoints || successResponse.data.points || [];
            this.total = successResponse.data.total || 0;
            if (this.tableData.length === 0) {
              this.$message.info('未找到符合条件的积分记录');
            }
          })
          .catch(failResponse => {
            console.error('查询失败:', failResponse);
            this.$message.error('查询失败: ' + (failResponse.response?.data?.message || failResponse.message));
            this.tableData = [];
            this.total = 0;
          })
          .finally(() => {
            this.loading = false;
          });
    },
    /**
     * 重置查询条件
     * 清空所有筛选条件，恢复默认查询
     */
    resetSearch() {
      this.selectForm = {
        staff_id: '',
        points_type: '',
        transaction_type: ''
      };
      this.currentPage = 1;
      this.loadWelfarePoints();
    },

    headClass() {
      return 'text-align: center; background: rgb(242,242,242); color: rgb(140,138,140); font-weight: bold;';
    },
    /**
     * 分页页码变化处理
     * 根据当前查询状态（条件查询/分页查询）重新加载数据
     */
    handleCurrentChange(val) {
      this.currentPage = val;
      // 如果当前是条件查询状态，使用条件查询；否则使用普通加载
      if (this.selectForm.staff_id || this.selectForm.points_type || 
          this.selectForm.transaction_type) {
        this.selectWelfarePointsByCon();
      } else {
        this.loadWelfarePoints();
      }
    },
    /**
     * 分页每页数量变化处理
     * 重置到第一页，根据当前查询状态重新加载数据
     */
    handleSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      // 如果当前是条件查询状态，使用条件查询；否则使用普通加载
      if (this.selectForm.staff_id || this.selectForm.points_type || 
          this.selectForm.transaction_type) {
        this.selectWelfarePointsByCon();
      } else {
        this.loadWelfarePoints();
      }
    },

    /**
     * 格式化积分值
     * 确保显示为数字，去除前导零（如"012"显示为"12"），但保留0值
     */
    formatPointsValue(value) {
      // 处理 null、undefined、空字符串
      if (value == null || value === '' || value === undefined) {
        return 0;
      }
      // 转换为数字
      const numValue = Number(value);
      if (isNaN(numValue)) {
        // 如果不是数字，尝试去除前导零后转换
        const strValue = String(value).trim();
        if (strValue === '' || strValue === '0') {
          return 0;
        }
        // 去除前导零
        const cleanedStr = strValue.replace(/^0+/, '');
        const cleanedNum = Number(cleanedStr);
        return isNaN(cleanedNum) ? 0 : cleanedNum;
      }
      // 直接返回数字值（Vue会自动转换为字符串显示）
      return numValue;
    },

    /**
     * 格式化交易时间
     * 优先使用 transaction_time1（已格式化的），如果没有则使用 transaction_time 并格式化
     */
    formatTransactionTime(time1, time) {
      // 优先使用已格式化的时间字符串
      if (time1 && time1 !== 'null' && time1 !== 'undefined' && time1.trim() !== '') {
        return time1;
      }
      // 如果没有格式化时间，尝试使用原始时间
      if (time) {
        // 如果 time 是字符串，直接返回
        if (typeof time === 'string' && time.trim() !== '') {
          return time;
        }
        // 如果 time 是 Date 对象或时间戳，格式化
        if (time instanceof Date || typeof time === 'number') {
          const date = new Date(time);
          if (!isNaN(date.getTime())) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const seconds = String(date.getSeconds()).padStart(2, '0');
            return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
          }
        }
      }
      // 如果都没有，返回提示信息
      return '未设置';
    },

    /**
     * 查看员工积分汇总
     * 显示该员工的总获得、总使用、当前可用积分等信息
     */
    handleSummary(index, row) {
      if (!row.staff_id) {
        this.$message.warning('该记录没有员工ID，无法查看汇总');
        return;
      }
      this.loading = true;
      this.$axios.get('/getPointsSummary', { params: { staff_id: row.staff_id } })
          .then(successResponse => {
            console.log('积分汇总响应:', successResponse.data);
            const summary = successResponse.data || {};
            const message = `
              <div style="text-align: left; line-height: 1.8;">
                <p><strong>员工姓名：</strong>${row.sname || '未知'}</p>
                <p><strong>员工ID：</strong>${row.staff_id}</p>
                <p><strong>累计获得积分：</strong><span style="color: #67C23A; font-weight: bold;">${summary.total_earned || 0}</span></p>
                <p><strong>已使用积分：</strong><span style="color: #909399;">${summary.total_used || 0}</span></p>
                <p><strong>当前可用积分：</strong><span style="color: #F56C6C; font-weight: bold; font-size: 18px;">${summary.current_available || 0}</span></p>
              </div>
            `;
            this.$alert(message, '积分汇总', {
              dangerouslyUseHTMLString: true,
              confirmButtonText: '确定'
            });
          })
          .catch(failResponse => {
            console.error('查询汇总失败:', failResponse);
            this.$message.error('查询汇总失败: ' + (failResponse.response?.data?.message || failResponse.message));
          })
          .finally(() => {
            this.loading = false;
          });
    },

    /**
     * 删除积分记录（逻辑删除）
     * 确认后调用后端API进行逻辑删除
     */
    handleDelete(index, row) {
      this.$confirm(`确认删除该积分记录吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true;
        this.$axios.post('/deleteWelfarePoints', null, { params: { id: row.id } })
            .then(successResponse => {
              if (successResponse.data === 'ok') {
                this.$message.success('删除成功');
                // 重新加载数据
                if (this.selectForm.staff_id || this.selectForm.points_type || 
                    this.selectForm.transaction_type) {
                  this.selectWelfarePointsByCon();
                } else {
                  this.loadWelfarePoints();
                }
              } else {
                this.$message.error('删除失败');
              }
            })
            .catch(failResponse => {
              console.error('删除失败:', failResponse);
              this.$message.error('删除失败: ' + (failResponse.response?.data?.message || failResponse.message));
            })
            .finally(() => {
              this.loading = false;
            });
      }).catch(() => {
        // 用户取消删除
      });
    },
    
    /**
     * 批量同步（积分+画像）
     * 为所有已有工作量记录生成积分和画像
     */
    handleBatchSync() {
      this.$confirm('批量同步将为所有已有工作量记录生成积分和画像，这可能需要较长时间，是否继续？', '批量同步确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.batchSyncing = true;
        this.$message.info('开始批量同步，请稍候...');
        
        this.$axios.post('/batchSyncAll')
            .then(successResponse => {
              console.log('批量同步响应:', successResponse.data);
              const result = successResponse.data;
              
              if (result.success) {
                const pointsResult = result.pointsResult || {};
                const imagesResult = result.imagesResult || {};
                
                let message = '批量同步完成！\n';
                message += `积分生成: 处理 ${pointsResult.processedWorkloads || 0} 条工作量，生成 ${pointsResult.generatedPointsCount || 0} 条积分记录\n`;
                message += `画像生成: 处理 ${imagesResult.processedEmployees || 0} 个员工，成功 ${imagesResult.successCount || 0} 个`;
                
                this.$alert(message, '批量同步完成', {
                  confirmButtonText: '确定',
                  type: 'success'
                });
                
                // 重新加载数据
                this.loadWelfarePoints();
              } else {
                this.$message.error('批量同步失败: ' + (result.message || '未知错误'));
              }
            })
            .catch(failResponse => {
              console.error('批量同步失败:', failResponse);
              this.$message.error('批量同步失败: ' + (failResponse.response?.data?.message || failResponse.message));
            })
            .finally(() => {
              this.batchSyncing = false;
            });
      }).catch(() => {
        // 用户取消
      });
    }
  }
}
</script>

<style scoped>
/* 查询表单区域 - 添加边框 */
.box1 {
  width: 80%;
  height: auto;
  margin-left: 20%;
  padding: 20px;
  background-color: #fff;
  /* 添加边框 */
  border: 1px solid #dcdfe6; /* 灰色边框 */
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 可选：轻微阴影 */
  margin-bottom: 20px;
}

/* 数据表格区域 */
.box2 {
  margin: 0 5px 20px 5px;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #dcdfe6; /* 表格区域也加边框 */
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 分页区域 */
.block {
  text-align: center;
  margin-top: 20px;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #dcdfe6; /* 分页区域也加边框 */
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-select {
  width: 100%;
}
</style>
