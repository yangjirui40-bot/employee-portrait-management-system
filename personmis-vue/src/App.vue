<template>
  <div>
    <header class="app-header">
      <h1>人事管理系统</h1>
    </header>
    <NavMain />
    <div class="main-content">
      <router-view/>
    </div>
  </div>
</template>
<script>
import NavMain from './components/NavMain.vue'

export default {
  name: 'App',
  components: { NavMain }
}
</script>
<style>
/* header */
.app-header{
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  height: 64px; /* 增高 */
  background: #3b4650; /* 比左侧稍深的颜色 */
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  z-index: 1000;
}
.app-header h1{
  margin: 0;
  font-size: 26px; /* 更大字体 */
  color: #ffffff; /* 白字 */
  font-weight: 600;
}
/* push main content down to avoid header overlap */
.main-content{
  margin-left: 220px;
  margin-top: 64px; /* 同步 header 高度 */
  text-align: left;
  padding: 20px;
}
</style>
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
#nav {
  padding: 30px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
/* main content shifted right to make room for left navigation */
.main-content{
  margin-left: 220px;
  text-align: left;
  padding: 20px;
}
</style>
