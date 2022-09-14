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
    console.log(error);
})



// 响应拦截器
axios.interceptors.response.use(success => {
    //成功调用后端接口，业务逻辑错误
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


// let base = '';

//传送json格式的post请求
//url请求路径
//params请求参数
export const postRequest = (url,params) => {
    return axios({
        method:'post',
        url:'' + url,
        data: params
    })
}

//传送jason格式get请求
export const getRequest = (url,params) => {
    // console.log(params)
    return axios({
        method: 'get',
        url:'' + url,
        data: params
    })
}

//传送jason格式delete请求
export const deleteRequest = (url,params) => {
    return axios({
        method: 'delete',
        url: '' + url,
        data: params
    })
}

//传送jason格式put请求
export const putRequest = (url,params) => {
    return axios({
        method: 'put',
        url: '' + url,
        data: params
    })
}
