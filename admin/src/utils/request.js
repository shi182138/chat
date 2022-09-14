import axios from "axios";
import { Message } from 'element-ui';//单独引用message
import router from '../router'

// 请求拦截器
axios.interceptors.request.use(config=>{
    if (window.sessionStorage.getItem('tokenStr')) {
        config.headers['token'] = window.sessionStorage.getItem('tokenStr');
    }
    return config;
}, error => {
    console.log('errot');
    console.log(error);
})



// 响应拦截器
axios.interceptors.response.use(success => {
    console.log('fuck1');
    //成功调用后端接口，业务逻辑错误
    console.log(success);
    if (success.status || success.status==200) {
        if (success.data.code==500 || success.data.code==401 || success.data.code==403) {
            Message.error({message:success.data.message});
            return;
        }
        if (success.data.message && success.data.message != '成功') {
            Message.success({message:success.data.message});
        }
    }
    return success.data;
},error => {
    console.log('fuck');
    if (error.response.code==504 || error.response.code==404) {
        Message.error({message:'服务器被吃了！！！！'});
    }else if (error.response.code==403) {
        Message.error({message:'权限不足,请充VIp'});
    }else if (error.response.code==401) {
        Message.error({message:'请登录,在充Vip'});
        router.replace('/');
    }else {
        if (error.response.data.message) {
            Message.error({message:error.response.data.message});
        } else {
            Message.error({message:'未知错误'});
        }
    }
    return;
});

export default axios
