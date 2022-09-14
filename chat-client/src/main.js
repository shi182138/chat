import Vue from 'vue'
import App from './App.vue'
import VueRouter from 'vue-router'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import store from './store';
import VueSocketIO from 'vue-socket.io';
import ClientSocketIO from 'socket.io-client'
import {postRequest} from './utils/Interceptor'
import {putRequest} from './utils/Interceptor'
import {getRequest} from './utils/Interceptor'
import {deleteRequest} from './utils/Interceptor'

Vue.prototype.postRequest = postRequest;
Vue.prototype.putRequest = putRequest;
Vue.prototype.getRequest = getRequest;
Vue.prototype.deleteRequest =deleteRequest;



Vue.use(ElementUI);
Vue.use(VueRouter);


router.beforeEach((to,from,next) => {
  if (window.sessionStorage.getItem('tokenStr')){
    // if (!window.sessionStorage.getItem('userInfo')){
    //   return getRequest('/chat/getUserInfo').then( resp => {
    //     if (resp) {
    //       window.sessionStorage.setItem('userInfo',JSON.stringify(resp.userInfo));
    //       next();
    //     }
    //   })
    // }
    next();
  }else{
    next();
  }

})
// socket连接
Vue.use(new VueSocketIO({
  debug: true,
  connection: ClientSocketIO.connect("http://127.0.0.1:9999/", {
    transports: ['websocket']
  }),
}))
Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App),
  beforeCreate(){
    Vue.prototype.$bus = this;
  },
}).$mount('#app')
