const { defineConfig } = require('@vue/cli-service')

let proxyObj = {}

proxyObj['/chat'] = {
  ws:true,
  target:'http://localhost:5555',
  pathRewrite:{'^/chat':''},
  changeOrigin: true,
} 

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy:proxyObj
  },
  lintOnSave:false
})
