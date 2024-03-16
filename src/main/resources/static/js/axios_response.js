// 添加响应拦截器
axios.interceptors.response.use(function(response){
    // 在发送请求之前需要做的操作
    // 获取响应头中的token
    const authorization=response.headers.authorization;
    // 如果响应头中的token不是一个空值，那就是token过期了，需要新的token
    if(authorization!=undefined){
        // 将拿到的token存储在本地中
        localStorage.setItem('token',`${authorization}`);
    }
    return response;
},function(error){
    // 请求错误时，将错误的结果打印在控制台
    console.log(error);
    return Promise.reject(error);
})