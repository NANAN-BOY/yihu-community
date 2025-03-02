import axios from "axios";
import {ElMessage} from "element-plus";

const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 30000
})

// request 拦截器
// 可以在发送请求前做一些统一处理
request.interceptors.request.use(config =>{
    config.headers['Content-Type'] = 'application/json;chaeset=utf-8';
    return config
}, error => {
    return Promise.reject(error)
})

// response 拦截器
// 可以在返回后做一些统一处理
request.interceptors.response.use(response =>{
    let res = response.data;
    // 兼容服务端返回的字符串数据
    if(typeof res === 'string'){
        res = res ? JSON.parse(res) : res
    }
    return res;
}, error => {
    if(error.response.status === 404){
        ElMessage.error("未找到请求接口")
    }else if(error.response.status === 500){
        ElMessage.error("系统异常，请查看后端报错")
    }else{
        console.error(error.message)
    }
    return Promise.reject(error)
})

export default request