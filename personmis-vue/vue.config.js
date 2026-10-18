module.exports = {
    //在本地会创建一个虚拟服务端，虚拟服务器访问后端的服务器不存在跨域
    devServer: {
      proxy: {
        '/personmis': {//将所有以 /personmis开头的请求自动代理到http://localhost:8443下
          //后端的基准地址
          target: 'http://localhost:8443',
          //是否启用websockets
          ws: true,
          //开启代理：在本地会创建一个虚拟服务端，然后发送请求的数据，并同时接收请求的数据，这样服务端和服务端进行数据的交互就不会有跨域问题
          changeOrigin: true
        }
      }
    }
}