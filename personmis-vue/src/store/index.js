import { createStore } from 'vuex'

export default createStore({
  state: {
    //初始时候给一个 isLogin='0' 表示用户未登录
    isLogin:window.sessionStorage.getItem('user') == null ? '0' : window.sessionStorage.getItem('user')
  },
  mutations: {
    changeLogin(state, data) {
      state.isLogin = data;
      window.sessionStorage.setItem('user', data)
    }
  }
})
