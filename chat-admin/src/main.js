import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import store from './store'
import axios from './api'


Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.prototype.$http = axios

router.beforeEach((to,from,next) => {
  if (to.path === '/login') {
      next()
  }
  else {
      if (window.sessionStorage.getItem('tokenStr')) {
          next()
      } else {
          next({
              path: '/login'
          })
      }
  }
})

new Vue({
  router,
  store,
  render: h => h(App),
  beforeCreate(){
    Vue.prototype.$bus = this;
  },
}).$mount('#app')
