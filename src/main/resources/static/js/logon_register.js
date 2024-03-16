// 获取登录表单左侧文字盒子元素
let logonText=document.querySelector('.introduceLogon');
// 获取登录表单所在的盒子的元素
let logonBox =document.querySelector('.logon');
// 获取登录表单左侧盒子中的按钮元素
let jump_rt=document.querySelector('.jumpright');
// 获取注册表单所在的盒子的元素
let registerBox=document.querySelector('.register');
// 获取注册表单右侧文字盒子元素
let registerText=document.querySelector('.introduceRegister');
// 获取注册表单右侧盒子中的按钮元素
let jump_lf=document.querySelector('.jumpleft');
// 获取注册表单
rg_form=document.querySelector('.register form'); 
// 获取登录表单
lg_form=document.querySelector('.logon form');
// 点击“注册账号”按钮时：(需要借助css动画属性，让透明度属性延后消失)
// 1.登录表单左侧文字盒子开始向右移动，同时登录表单所在的盒子的透明度开始逐步降低。
// 2.处在右侧的注册表单的透明度开始逐步提高，同时注册表单右侧的文字盒子透明度开始提高。
// 对按钮添加一个点击事件
jump_rt.addEventListener('click',function(e){
    // 先取消按钮的默认事件
    e.preventDefault();
    // 点击注册账号，那么登录表单中的值就会被清空
    lg_form.reset();
    // 先移除注册表单右侧文字盒子的移动类,以及注册表单的移动类
    registerText.classList.remove('registerMove');
    registerBox.classList.remove('rgformMove');
    // 对登录表单左侧盒子添加一个移动类,对登录表单也增加一个移动类
    logonText.classList.add('logonMove');
    logonBox.classList.add('lgformMove');

    // 监听登录表单左侧盒子的动画
    logonText.addEventListener('animationend',function(){
        // 动画结束对登录表单和自身减少透明度属性
        logonText.style.opacity='0';
        logonBox.style.opacity='0';
        logonBox.style.zIndex='1';
        logonText.style.zIndex='1';
        // 动画结束对注册表单和注册表单右侧的文字盒子提高透明度属性
        // 同时提高注册表单右侧文字盒子和注册表单的图层
        registerText.style.opacity='1';
        registerBox.style.opacity='1';
        registerText.style.zIndex='5';
        registerBox.style.zIndex='4';
    })
})

// 点击“开始登录”按钮时：
// 1.登录表单左侧文字盒子先移除移动类，回到原来的位置。
// 2.注册表单右侧文字盒子添加移动类，当动画结束时，自身和注册表单的透明度属性开始降低.
// 3.登录表单左侧文字盒子和登录表单的透明度属性开始提高
jump_lf.addEventListener('click',function(e){
    // 同样是先取消按钮的默认事件
    e.preventDefault();
    // 点击开始登录，那么注册表单中的值就会被清空
    rg_form.reset();
    // 先移除登录表单左侧文字盒子的移动类,以及登录表单的移动类
    logonText.classList.remove('logonMove');
    logonBox.classList.remove('lgformMove');
    // 对注册表单右侧文字盒子添加一个移动类
    registerText.classList.add('registerMove');
    registerBox.classList.add('rgformMove');
    // 监听注册表单右侧文字盒子的动画
    registerText.addEventListener('animationend',function(){
        // 动画结束注册表单和自身减少透明度属性
        registerText.style.opacity='0';
        registerBox.style.opacity='0';
        registerBox.style.zIndex='1';
        registerText.style.zIndex='1';
        // 动画结束登录表单和登录表单左侧的文字盒子提高透明度属性
        // 同时提高登录表单的图层
        logonText.style.opacity='1';
        logonBox.style.opacity='1';
        logonText.style.zIndex='5';
        logonBox.style.zIndex='4';
    })
})

// 页面登录响应板块
// 首先需要去获取登录表单中的值，然后将这些值通过Ajax传输到后端
// 获取登录表单的登录按钮并对该按钮添加一个点击事件
document.querySelector('.logon .buttonBox').addEventListener('click',function(e){
    // e.preventDefault();

    // 获取登录表单中用户输入的用户名和密码的值
    const E_mail=document.querySelector('.logon .E-mail').value;
    const password=document.querySelector('.logon .password').value;
   
    if(E_mail==''&&password==''){
        document.querySelector('.logon .first_tip').style.opacity='1';
        let timefour=setTimeout(function(){
            document.querySelector('.logon .first_tip').style.opacity='0';
        },1500)
    }
    else{
         // 使用axios将数据传递给后端
        axios({
        url:'http://localhost:8080/users/login',
        method:'POST',
        data:{
            mail:E_mail,
            password
        }
    }).then(result => {
        
        // 从后端那边获取了数据，对数据进行数组结构
        console.log(result)
        const {data}=result;
        console.log(data);
        const {resultData}=data;
        if(data.success==true){
            const lg=document.querySelector('.logon_pupop');
            lg.style.display='block';
            document.querySelector('.logon_pupop button').addEventListener('click',function(){
                lg.style.display='none';
                // 登录成功之后，清空登录表单中的值
                window.location.href='index.html';
                lg.reset();
            })
        }
        else{
            alert("登录失败");
        }
    }).catch(error => {
        alert(error.msg);
    })
    }

})

// 页面注册响应板块
// 首先需要去获取注册表单中的值，然后将这些值传递给后端
  
// 获取注册表单的验证码按钮并对该按钮添加一个点击事件
document.querySelector('.register .received_code').addEventListener('click',function(e){
    e.preventDefault();
    const Email=document.querySelector('.register .E-mail').value;
    const passWordFirst=document.querySelector('.register .password_first').value;
    const passWordSecond=document.querySelector('.register .password_second').value;
    // 点击获取验证码的按钮的DOM元素
    const register_bt=document.querySelector('.received_code');
    // 表单中的提示的DOM元素
    const first_tip=document.querySelector('.register .first_tip');
    const second_tip=document.querySelector('.register .second_tip');
    const third_tip=document.querySelector('.register .third_tip');
    code(Email,passWordFirst,passWordSecond,first_tip,second_tip,third_tip,register_bt);
})


// 获取注册表单的注册按钮并对添加一个事件
document.querySelector('.register .buttonBox').addEventListener('click',function(e){
    e.preventDefault();
    const code=document.querySelector('.register .codeBox .code').value;
    const Email=document.querySelector('.register .E-mail').value;
    const passWordFirst=document.querySelector('.register .password_first').value;
    const passWordSecond=document.querySelector('.register .password_second').value;
    // 判断一下用户名有没有输入，用户名不能为空
    if(Email==''){
        document.querySelector('.register .first_tip').style.opacity='1';
        let timeone=setTimeout(function(){
            document.querySelector('.register .first_tip').style.opacity='0';
        },1500)
    }
    // 判断一下用户是不是什么都没有填
    if(Email==''&&passWordFirst==''&&passWordSecond==''){
            document.querySelector('.register .first_tip').style.opacity='1';
            document.querySelector('.register .second_tip').style.opacity='1';
            document.querySelector('.register .third_tip').style.opacity='1';
        let timefour=setTimeout(function(){
            document.querySelector('.register .first_tip').style.opacity='0';
            document.querySelector('.register .second_tip').style.opacity='0';
            document.querySelector('.register .third_tip').style.opacity='0';
        },1500)
    }
    // 判断一下密码有没有输入，密码不能为空
    if(passWordFirst==''){
        document.querySelector('.register .second_tip').style.opacity='1';
        let timetwo=setTimeout(function(){
            document.querySelector('.register .second_tip').style.opacity='0';
        },1500)
    }
    if(code==''){
        document.querySelector('.register .fourth_tip').style.opacity='1';
        let time=setTimeout(function(){
            document.querySelector('.register .fourth_tip').style.opacity='0';
        },1500)
    }
    // 在提交数据前先比较一下第一次输入的密码和第二次输入的密码是不是一致的
    // 如果两次输入一致，那么就可以请求发送验证码
    if(passWordFirst==passWordSecond&&passWordFirst!=''){
    // 点击注册的时候，验证码已经发送给了用户，此时将表单中的数据全部发给后端处理
        axios({
            url:'http://localhost:8080/users/create',
            method:'POST',
            data: {
                mail:Email,
                password:passWordFirst,
                code,
            }
        }).then(result => {
            console.log(result);
            
            const {data}=result;
            console.log(data)
            if(data.success==true){
                const rg=document.querySelector('.register_pupop');
                rg.style.display='block';
                document.querySelector('.register_pupop button').addEventListener('click',function(){
                    rg.style.display='none';
                    jump_lf.click();
                    // 清空表单中的值
                    rg_form.reset();
                })
            }
            
        }).catch(error => {
            document.querySelector('.register .fourth_tip').style.opacity='1';
            let time=setTimeout(function(){
                document.querySelector('.register .fourth_tip').style.opacity='0';
            },1500)
        })
    }
})

// 点击“忘记密码”按钮时，忘记密码弹窗出现
// 获取忘记密码按钮的DOM元素
const forgot_entry=document.querySelector('.box .logon .forgot');
// 获取遮罩层DOM元素和忘记密码弹窗DOM元素
const mask=document.querySelector('.mask');
const forgot_pp=document.querySelector('.forgot_pp');
// 对该按钮绑定一个点击事件
forgot_entry.addEventListener('click',function(e){
    e.preventDefault();
    // 点击该按钮的时候，出现忘记密码弹窗
    mask.style.opacity='1';
    mask.style.zIndex=5;
    forgot_pp.style.opacity='1';
    forgot_pp.style.zIndex=6;
})

// 获取忘记密码弹窗的关闭按钮
const forgot_pp_close=document.querySelector('.forgot_pp .title .close');
// 获取忘记密码弹窗中的表单的DOM元素
const for_form=document.querySelector('.forgot_pp  form');
forgot_pp_close.addEventListener('click',function(){
    // 点击该按钮的时候，弹窗和遮罩层消失
    mask.style.opacity='0';
    mask.style.zIndex=-1;
    forgot_pp.style.opacity='0';
    forgot_pp.style.zIndex=-2;
    // 清空表单中的值
    for_form.reset();
})


// 点击忘记密码表单中的，获得验证码按钮
const forgot_bt=document.querySelector('.forgot_pp .received_code');
// 点击忘记密码表单中的验证码按钮
forgot_bt.addEventListener('click',function(e){
    e.preventDefault();
    // 获取忘记密码表单中的值
    const forgot_Email=document.querySelector('.forgot_pp .E-mail').value;
    const forgot_First=document.querySelector('.forgot_pp .password_first').value;
    const forgot_Second=document.querySelector('.forgot_pp .password_second').value;
    // 表单中的提示的DOM元素
    const first_tip=document.querySelector('.forgot_pp .first_tip');
    const second_tip=document.querySelector('.forgot_pp .second_tip');
    const third_tip=document.querySelector('.forgot_pp .third_tip');
    code(forgot_Email,forgot_First,forgot_Second,first_tip,second_tip,third_tip,forgot_bt);
})

// 点击“忘记密码”表单中的修改密码按钮
// 获取修改密码按钮的DOM元素
const forgot_change_bt=document.querySelector('.forgot_pp .forgot_bt');
forgot_change_bt.addEventListener('click',function(e){
    e.preventDefault();
    const forgot_Email=document.querySelector('.forgot_pp .E-mail').value;
    const forgot_First=document.querySelector('.forgot_pp .password_first').value;
    const forgot_Second=document.querySelector('.forgot_pp .password_second').value;
    const forgot_code=document.querySelector('.forgot_pp .code').value;
    // 表单中的提示的DOM元素
    const first_tip=document.querySelector('.forgot_pp .first_tip');
    const second_tip=document.querySelector('.forgot_pp .second_tip');
    const third_tip=document.querySelector('.forgot_pp .third_tip');
    const fourth_tip=document.querySelector('.forgot_pp .fourth_tip');
    // 判断一下用户名有没有输入，用户名不能为空
    if(forgot_Email==''){
        first_tip.style.opacity='1';
        let timeone=setTimeout(function(){
            first_tip.style.opacity='0';
        },1500)
    }
    // 判断一下用户是不是什么都没有填
    if(forgot_Email==''&&forgot_First==''&&forgot_Second==''){
        first_tip.style.opacity='1';
        second_tip.style.opacity='1';
        third_tip.style.opacity='1';
        let timefour=setTimeout(function(){
            first_tip.style.opacity='0';
            second_tip.style.opacity='0';
            third_tip.style.opacity='0';
        },1500)
    }
    // 判断一下密码有没有输入，密码不能为空
    if(forgot_First==''){
        second_tip.style.opacity='1';
        let timetwo=setTimeout(function(){
            second_tip.style.opacity='0';
        },1500)
    }
    // 判断一下验证码是否为空
    if(forgot_code==''){
        fourth_tip.style.opacity='1';
        let time=setTimeout(function(){
            fourth_tip.style.opacity='0';
        },1500)
    }
    // 在提交数据前先比较一下第一次输入的密码和第二次输入的密码是不是一致的
    // 如果两次输入一致，那么就可以请求发送验证码
    if(forgot_First==forgot_Second&&forgot_First!=''){
        console.log(1111111111111)
    // 点击注册的时候，验证码已经发送给了用户，此时将表单中的数据全部发给后端处理
        axios({
            url:'http://localhost:8080/users/modify',
            method:'POST',
            data: {
                mail:forgot_Email,
                password:forgot_First,
                code:forgot_code
            }
        }).then(result => {
            console.log(result)
            const {data}=result;
            if(data.success==true){
                // 密码修改成功后，修改成功弹窗弹出
                const fg=document.querySelector('.forgot_pupop');
                fg.style.display='block';
                document.querySelector('.forgot_pupop button').addEventListener('click',function(){
                    fg.style.display='none';
                    // 关掉弹窗的同时，关掉忘记密码弹窗，同时清空表单
                    forgot_pp_close.click();
                })
            }
            
        }).catch(error => {
            fourth_tip.style.opacity='1';
            let time=setTimeout(function(){
                fourth_tip.style.opacity='0';
            },1500)
        })
    }
})



// 点击发送验证码执行的函数
function code (Email,passWordFirst,passWordSecond,first_tip,second_tip,third_tip,bt){
    // 判断一下用户名有没有输入，用户名不能为空
    if(Email==''){
        first_tip.style.opacity='1';
        let timeone=setTimeout(function(){
            first_tip.style.opacity='0';
        },1500)
    }
    // 判断一下用户是不是什么都没有填
    if(Email==''&&passWordFirst==''&&passWordSecond==''){
        first_tip.style.opacity='1';
        second_tip.style.opacity='1';
        third_tip.style.opacity='1';
        let timefour=setTimeout(function(){
            first_tip.style.opacity='0';
            second_tip.style.opacity='0';
            third_tip.style.opacity='0';
        },1500)
    }
    // 判断一下密码有没有输入，密码不能为空
    if(passWordFirst==''){
        second_tip.style.opacity='1';
        let timetwo=setTimeout(function(){
            second_tip.style.opacity='0';
        },1500)
    }
    // 在提交数据前先比较一下第一次输入的密码和第二次输入的密码是不是一致的
    // 如果两次输入一致，那么就可以请求发送验证码
    if(passWordFirst==passWordSecond&&passWordFirst!=''){
        axios({
            url:'http://localhost:8080/users/code',
            method:'POST',
            params: {
                mail:Email
            }
        }).then(result => {
            console.log(result);
            const {data}=result;
            let str=data.resultData;
            let sr=str.slice(8,14);
        }).catch(error => {
            console.log(error.msg);
        })
        // 点击验证码之后，验证码的按钮编变成一分钟倒计时,并且禁止按钮点击
        bt.disabled=true;
        bt.innerHTML='59秒之后重试';
        // 设置一个倒计时函数
        let i=59;
        let countdown=setInterval(function(){
            if(i===0){
                console.log(bt)
                bt.disabled=false;
                bt.innerHTML='获取验证码';
                clearInterval(countdown);
            }
            else{
                bt.innerHTML=`${i}秒之后重试`;
                i--;
            }
        },1000)  
    }
    // 如果两次输入不一致，那么就跳出事件，同时提示用户“两次输入不一致”
    else{
        third_tip.style.opacity='1';
        let timethree=setTimeout(function(){
            third_tip.style.opacity='0';
        },1500)  
    } 
}