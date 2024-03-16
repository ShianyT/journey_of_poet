// 添加请求拦截器
axios.interceptors.request.use(function(config){
    // 在发送请求之前需要做的操作
    // 从本地中获取token的值
    let token=localStorage.getItem('token');
    // 对请求头添加一个token
    config.headers.Authorization=`${token}`;
    return config;
},function(error){
    // 请求错误时，将错误的结果打印在控制台
    console.log(error);
    return Promise.reject(error);
})