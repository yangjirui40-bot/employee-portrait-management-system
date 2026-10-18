<template>
  <div class="login-page">
    <div class="login-wrapper">
      <el-card class="login-box" shadow="hover">
        <div class="brand">
          <div class="logo">懒羊羊</div>
          <div class="brand-title">欢迎使用员工管理系统</div>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="rules" label-width="0" class="login-form">
          <el-form-item prop="uname">
            <el-input v-model="loginForm.uname" placeholder="用户名">
              <template #prefix>
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="upwd">
            <el-input v-model="loginForm.upwd" placeholder="密码" show-password type="password">
              <template #prefix>
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-row class="actions" type="flex" justify="space-between" align="middle">
            <el-col :span="14">
              <el-checkbox v-model="remember">记住我</el-checkbox>
            </el-col>
            <el-col :span="10" class="forgot">
              <a @click.prevent class="forgot-link">忘记密码?</a>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary" @click="login(loginForm)" :loading="loadingbut" class="login-btn" round>{{loadingbuttext}}</el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">由懒羊羊股份有限公司提供支持</div>
      </el-card>
    </div>
  </div>
</template>
<script>
export default {
  name: 'Login',
  data () {
    return {
      loginForm: { uname: '', upwd: '' },
      //验证规则
      rules: {
        uname: [{required: true, message: '请输入用户名', trigger: 'blur'}],
        upwd: [{required: true, message: '请输入密码', trigger: 'blur'}]
      },
      loadingbut: false,
      loadingbuttext: '登录'
    }
  },
  methods: {
    login (loginForm) {
      this.$refs['loginForm'].validate((valid) => {
        if (valid) {
          this.loadingbut = true
          this.loadingbuttext = '登录中...'
          this.$axios
            .post('/login',{
              uname: loginForm.uname,
              upwd: loginForm.upwd
            })
            .then(successResponse => {
              if (successResponse.data === "ok") {
                this.$alert('登录成功', {confirmButtonText: '确定' })
                this.$store.commit('changeLogin',this.loginForm.uname)
                let path = this.$route.query.redirect
                this.$router.replace({path: path === '/' || path === undefined ? '/home': path})
              } else {
                this.$alert('用户名或密码错误！', {confirmButtonText: '确定' })
                this.loadingbut = false;
                this.loadingbuttext = '登录';
              }
            })
            .catch(failResponse => {
              this.$alert(failResponse && failResponse.response ? failResponse.response.status : '请求失败', {confirmButtonText: '确定' })
              this.loadingbut = false
              this.loadingbuttext = '登录'
            })
        } else {
          this.$alert('表单验证失败', {confirmButtonText: '确定' })
          return false;
        }
      })
    },
    cancel(){
      this.$refs['loginForm'].resetFields()
    }
  }
}
</script>
<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap');

.login-page{
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(34,40,49,0.85) 0%, rgba(58,74,89,0.85) 100%), url(~@/assets/lanyangyang.jpg) center/cover no-repeat;
  -webkit-font-smoothing: antialiased;
}

.login-wrapper{
  width: 100%;
  max-width: 420px;
  margin: 24px;
}

.login-box{
  padding: 28px 28px 18px 28px;
  border-radius: 12px;
  background: rgba(255,255,255,0.96);
  box-shadow: 0 20px 40px rgba(2,6,23,0.45);
}

.brand{
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 14px;
}

.logo{
  width: 78px;
  height: 78px;
  border-radius: 50%;
  background: linear-gradient(135deg,#6dd3ff,#6b8cff);
  display:flex;
  align-items:center;
  justify-content:center;
  color:#fff;
  font-weight:700;
  font-size:20px;
  margin-bottom:8px;
}

.brand-title{
  font-size:16px;
  color:#1f2d3d;
  font-weight:600;
}

.login-form :deep(.el-input__inner){
  border-radius: 8px;
  padding-left: 36px;
}

.login-form i{
  color: #9aa8b8;
  font-size: 16px;
}

.actions{margin-top:6px;margin-bottom:8px}
.forgot{ text-align: right }
.forgot-link{ color:#409EFF; cursor: pointer; font-size:13px }

.login-btn{
  width: 100%;
  height: 44px;
  font-weight: 600;
}

.login-footer{
  margin-top: 12px;
  font-size: 12px;
  color: #8b98a7;
  text-align: center;
}

@media (max-width: 480px){
  .login-wrapper{ padding: 12px }
  .logo{ width:64px;height:64px;font-size:18px }
}

</style>