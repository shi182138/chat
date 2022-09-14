const { defineConfig } = require('@vue/cli-service')


let proxyObj = {}

proxyObj['/chat'] = {
  ws: true,
  target:'http://127.0.0.1:5555',
  pathRewrite:{'^/chat':''},
  changeOrigin: true,
} 



module.exports = defineConfig({
  // transpileDependencies: true,
  devServer: {
    // host: "localhost",
    // port: 8080,
    proxy:proxyObj
  },
  lintOnSave:false
})






