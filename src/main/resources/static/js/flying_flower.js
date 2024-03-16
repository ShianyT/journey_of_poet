const mateBox=document.querySelector('.outBox .mate');
const leftBox=document.querySelector('.mate .leftBox');
const rightBox=document.querySelector('.mate .rightBox');
const leftBox_hand=document.querySelector('.mate .leftBox .hand');
const rightBox_hand=document.querySelector('.mate .rightBox .hand');
const mate_content=document.querySelector('.mate .content');
const topBox=document.querySelector('.mate .topBox');
const bodyBox=document.querySelector('.mate .bodyBox');
const bottomInputBox=document.querySelector('.mate .bottomInputBox');
const otheruserBox=document.querySelector('.mate .otheruserBox');
const userBox=document.querySelector('.mate .bodyBox .userBox');
const popup=document.querySelector('.outBox .popup');
const cancell=document.querySelector('.mate .content .playerbox .cancell');

// 在本地储存中写上一个标志，firstentry:表示是否是第一次进入学习页面
let firstentry=0;
localStorage.setItem('firstEntry',`${firstentry}`)

let token=localStorage.getItem('token');
// 获得本地储存中的头像，用户名等信息
let usericon=localStorage.getItem('usericon');
let username=localStorage.getItem('username');
let userid=localStorage.getItem('userid');
// 获取页面中的头像元素
const userIcon=document.querySelector('.mate .content .playerbox .right .userBox img');
userIcon.src=`${usericon}`;
// 获取页面中头像元素下的昵称
const userName=document.querySelector('.mate .content .playerbox .right .username');
userName.innerHTML=`${username}`

// 获取对方用户头像的DOM元素
const otheruserIcon=document.querySelector('.mate .content .playerbox .left .userBox img');
const otheruserName=document.querySelector('.mate .content .playerbox .left .username');
// 获取飞花令的关键词盒子
const flower_keyword=document.querySelector('.mate .content .playerbox .middle .bottom_text');
// 获取提示弹窗
const tip_popups=document.querySelectorAll('.outBox .tip_popup');
// 获取下面的文本输入框的DOM元素
const inputBottom=document.querySelector('.outBox .bottomInputBox input');
console.log(inputBottom)
// 获取顶部的倒计时盒子
const last_time=document.querySelector('.mate .topBox .timeBox');

// 对取消匹配按钮添加一个事件
cancell.addEventListener('click',function(){
    axios({
        url:'http://localhost:8080/battles',
        method:'DELETE'
    }).then(result =>{
        if(result.data.success==true){
            console.log("取消匹配成功");
            window.location.href="flying_flower_entry .html";
        }
    })
})

// 设置一个表示错误的标志
var myerror=0;
var yourerror=0;
// 创建一个websocket对象
var socket=new WebSocket(`ws://localhost:8080/battles/send?Authorization=${token}`);
socket.onopen=async function(){
    // 链接建立成功之后发送一个请求给后端，进行匹配
    await axios({
        url:'http://localhost:8080/battles/match',
        method:'GET',
    }).then(result =>{
        console.log(result)
        const {data}=result;
        if(data.success==true){
            console.log('开始匹配')
        }
    }).catch(error =>{
        console.log(error);
    })
    
    console.log('连接成功');
    
    // 当用户在输入框中敲回车或者点击发送的时候
    // 获取输入框中发送的DOM元素
    const bt=document.querySelector('.outBox .bottomInputBox .botton')
    bt.addEventListener('click',function(){
        let value=inputBottom.value;
        if(value.trim()!=''){
            value=escapeHtml(value);
            let uidStr=localStorage.getItem('otheruserid');
            let uid=Number(uidStr);
            // 定义一个对象
            let data={
                message:value,
                uid:uid
            }
            console.log(data)
            let sendData=JSON.stringify(data);
            // 将该内容发送给后端，websocket
            socket.send(sendData);
            inputBottom.value='';
            // 发送内容过去之后，让倒计时停止，重新开始
            clearInterval(timer);
        }
    })  
    inputBottom.addEventListener('keyup',function(e){
        if(e.key==='Enter'){
            let value=inputBottom.value;
            if(value.trim()!=''){
                value=escapeHtml(value);
                let uidStr=localStorage.getItem('otheruserid');
                let uid=Number(uidStr);
                // 定义一个对象
                let data={
                    message:value,
                    uid:uid
                }
                console.log(data)
                let sendData=JSON.stringify(data);
                // 将该内容发送给后端，websocket
                socket.send(sendData);
                inputBottom.value='';
                // 发送内容过去之后，让倒计时停止，重新开始
                clearInterval(timer);
            }
        } 
    })  
}
let flag=1;
    // 设置一个定时器
    var timer;
    socket.onmessage=async function(e){
        const message=JSON.parse(e.data);
        console.log(message);
        if(flag==1){
            bodyBox.style.display='flex';
            // 将拿到的对方用户的Uid和头像，昵称等信息储存在本地中
            let otherUicon='http://localhost:8080/imgs/thumbnail/icon/'+`${message.battleUser.icon}`;
            let otherUname=message.battleUser.nickname;
            let otherUid=message.battleUser.uid;
            let Uid=localStorage.getItem('userid');
            console.log(Uid)
            let keyword=message.keyword;
            localStorage.setItem('otherusericon',`${otherUicon}`)
            localStorage.setItem('otherusername',`${otherUname}`)
            localStorage.setItem('otheruserid',`${otherUid}`)
            localStorage.setItem('keyword',`${keyword}`)

            otheruserIcon.src=`${otherUicon}`;
            otheruserName.innerHTML=`${otherUname}`;
            flower_keyword.innerHTML=`${keyword}`;

            if(message._before==true){
                // 如果收到的数据为真，那么即先手
                rightBox_hand.classList.add('handDisplay');
                // 将先手的uid存到本地储存中
                localStorage.setItem('beforeUid',`${userid}`);
                localStorage.setItem('afterUid',`${otherUid}`);
            }
            else {
                leftBox_hand.classList.add('handDisplay');
                localStorage.setItem('beforeUid',`${otherUid}`);
                localStorage.setItem('afterUid',`${userid}`);
            }
            leftBox.style.left='0';
            rightBox.style.right='0';
            // // 获取顶部关键词元素
            const top_flower_keyword=document.querySelector('.mate .topBox .middleBox');
            top_flower_keyword.innerHTML=`本轮飞花令为：${keyword}`;
            // 动画效果
            rightBox_hand.addEventListener('animationend',function(){
                rightBox_hand.classList.remove('handDisplay');
                rightBox.classList.add('rightremove');
                leftBox.classList.add('leftremove');
            })

            leftBox_hand.addEventListener('animationend',function(){
                leftBox_hand.classList.remove('handDisplay');
                rightBox.classList.add('rightremove');
                leftBox.classList.add('leftremove');
            })
            
            rightBox.addEventListener('animationend',function(){
                mate_content.style.display='none';
                setTimeout(function(){
                    rightBox.style.display='none';
                    leftBox.style.display='none';
                    topBox.classList.add('display');
                    bodyBox.classList.add('display');
                    bottomInputBox.classList.add('display');
                },1500)
                if(message._before==false){
                    // 如果收到的数据为假，那么即对方先手
                    tip_popups[0].classList.add('display');
                    setTimeout(function(){
                        tip_popups[0].classList.remove('display');
                    },2500)
                    let i=30;
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        if(i==-1){
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:otherUid,
                                        afterUid:Uid,
                                        outcome:Uid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    胜出
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
                else {
                    inputBottom.disabled=true;
                    let i=30;
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        if(i==-1){
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:Uid,
                                        afterUid:otherUid,
                                        outcome:otherUid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    失败
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
            })
            flag++;
        }
        else{
            // 从本地获取自己用户id
            let myid=localStorage.getItem('userid');
            let yourid=localStorage.getItem('otheruserid');
            // 从本地拿到先后手id
            let beforeuid=localStorage.getItem('beforeUid');
            let afteruid=localStorage.getItem('afterUid');
            // 从本地拿到该局的飞花
            let keyword=localStorage.getItem('keyword');
            // 设置一个正则表达式
            let keyword_test=new RegExp(keyword);
            // 如果拿到的信息中的id不是自己的id，那么就是对方
            if(message.uid!=myid){
                // 重置myerror
                myerror=0;
                // 从本地中获取对方用户头像
                let battleicon=localStorage.getItem('otherusericon');
                // 创建一个新的元素在bodyBox中
                let div=document.createElement('div');
                div.classList.add('otheruserBox');
                if(message.poemTitle==undefined||message.poemTitle==null){
                    div.innerHTML=`
                        <div class="otheruser">
                            <div class="picture">
                                <img src="${battleicon}">
                            </div>
                            <div class="poemBox">
                                <div class="poem">${message.poem}</div>
                                <div class="poemname">
                                    <div class="iconfont icon-cha"></div>
                                </div>
                            </div>
                        </div>
                    `
                }
                else{
                    div.innerHTML=`
                        <div class="otheruser">
                            <div class="picture">
                                <img src="${battleicon}">
                            </div>
                            <div class="poemBox">
                                <div class="poem">${message.poem}</div>
                                <div class="poemname">《${message.poemTitle}》</div>
                            </div>
                        </div>
                    `
                }
                bodyBox.appendChild(div);
                div.classList.add('display');
                bodyBox.scrollTop=bodyBox.scrollHeight;
                // 如果对面发的诗句没有飞花
                if(!keyword_test.test(message.poem)){
                    yourerror++;
                    if(yourerror==2){
                        axios({
                            url:'http://localhost:8080/battles/outcome',
                                method:'POST',
                                data:{ 
                                    beforeUid:beforeuid,
                                    afterUid:afteruid,
                                    outcome:myid
                                },
                        }).then(result =>{
                            const {data}=result;
                            const {resultData}=data;
                            socket.close();
                            tip_popups[0].style.display='none';
                            tip_popups[1].style.display='none';
                            tip_popups[2].style.display='none';
                            // 获取弹窗中的状态
                            const popup_title=document.querySelector('.popup .title');
                            popup_title.innerHTML=`
                                胜出
                            `
                            money(resultData);
                            topBox.classList.remove('display');
                            bodyBox.classList.remove('display');
                            bottomInputBox.classList.remove('display');
                            popup.style.display='flex';
                        })
                    }
                    tip_popups[2].classList.add('display');
                    setTimeout(function(){
                        tip_popups[2].classList.remove('display');
                    },2500)
                    // 重新定义一个15秒的倒计时
                    let i=15;
                    clearInterval(timer);
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        if(i==-1){
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:beforeuid,
                                        afterUid:afteruid,
                                        outcome:myid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    胜出
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
                // 如果是对面发送错误
                else if(message.poemTitle==null){
                    yourerror++;
                    if(yourerror==2){
                        axios({
                            url:'http://localhost:8080/battles/outcome',
                                method:'POST',
                                data:{ 
                                    beforeUid:beforeuid,
                                    afterUid:afteruid,
                                    outcome:myid
                                },
                        }).then(result =>{
                            const {data}=result;
                            const {resultData}=data;
                            socket.close();
                            tip_popups[0].style.display='none';
                            tip_popups[1].style.display='none';
                            tip_popups[2].style.display='none';
                            // 获取弹窗中的状态
                            const popup_title=document.querySelector('.popup .title');
                            popup_title.innerHTML=`
                                胜出
                            `
                            money(resultData)
                            topBox.classList.remove('display');
                            bodyBox.classList.remove('display');
                            bottomInputBox.classList.remove('display');
                            popup.style.display='flex';
                        })
                    }
                    tip_popups[2].classList.add('display');
                    setTimeout(function(){
                        tip_popups[2].classList.remove('display');
                    },2500)
                    // 重新定义一个15秒的倒计时
                    let i=15;
                    clearInterval(timer);
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        if(i==-1){
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:beforeuid,
                                        afterUid:afteruid,
                                        outcome:myid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    胜出
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
                else{
                    // 如果是对方发送过来的话，解除自己的输入框限制
                    inputBottom.disabled=false;
                    tip_popups[0].classList.add('display');
                    setTimeout(function(){
                        tip_popups[0].classList.remove('display');
                    },2500)
                    let i=30;
                    clearInterval(timer);
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        if(i==-1){
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:beforeuid,
                                        afterUid:afteruid,
                                        outcome:yourid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    失败
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
            }
            else{
                // 重置yourerror
                yourerror=0;
                // 从本地中拿到自己的头像
                let myicon=localStorage.getItem('usericon');
                // 创建一个新的元素在bodyBox中
                let div=document.createElement('div');
                div.classList.add('userBox');
                if(message.poemTitle==undefined||message.poemTitle==null){
                    div.innerHTML=`
                        <div class="user">
                            <div class="poemBox">
                                <div class="poem">${message.poem}</div>
                                <div class="poemname">
                                    <div class="iconfont icon-cha"></div>
                                </div>
                            </div>
                            <div class="picture">
                                <img src="${myicon}">
                            </div>
                        </div>
                    `
                }
                else{
                    div.innerHTML=`
                        <div class="user">
                            <div class="poemBox">
                                <div class="poem">${message.poem}</div>
                                <div class="poemname">《${message.poemTitle}》</div>
                            </div>
                            <div class="picture">
                                <img src="${myicon}">
                            </div>
                        </div>
                    `
                }
                
                // 在父元素后添加子元素
                bodyBox.appendChild(div);
                // 让写上去的诗句出现
                div.classList.add('display');
                // 让新的内容始终出现，即让滚动条始终在底部
                bodyBox.scrollTop=bodyBox.scrollHeight;
                // 如果我输入的不是正确的含有飞花的诗句
                if(!keyword_test.test(message.poem)){
                    myerror++;
                    if(myerror==2){
                        axios({
                            url:'http://localhost:8080/battles/outcome',
                                method:'POST',
                                data:{ 
                                    beforeUid:beforeuid,
                                    afterUid:afteruid,
                                    outcome:yourid
                                },
                        }).then(result =>{
                            const {data}=result;
                            const {resultData}=data;
                            socket.close();
                            tip_popups[0].style.display='none';
                            tip_popups[1].style.display='none';
                            tip_popups[2].style.display='none';
                            // 获取弹窗中的状态
                            const popup_title=document.querySelector('.popup .title');
                            popup_title.innerHTML=`
                                失败
                            `
                            money(resultData);
                            topBox.classList.remove('display');
                            bodyBox.classList.remove('display');
                            bottomInputBox.classList.remove('display');
                            popup.style.display='flex';
                        })
                    }
                    tip_popups[1].classList.add('display');
                    setTimeout(function(){
                        tip_popups[1].classList.remove('display');
                    },2500)
                    // 重新定义一个15秒的倒计时
                    let i=15;
                    clearInterval(timer);
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        // 如果倒计时到时间，还没有出东西
                        if(i==-1){
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:beforeuid,
                                        afterUid:afteruid,
                                        outcome:yourid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    失败
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
                // 如果是我自己发送出错了
                else if(message.poemTitle==null){
                    // 错误一次，标志+1，达到两次，输掉游戏
                    myerror++;
                    if(myerror==2){
                        axios({
                            url:'http://localhost:8080/battles/outcome',
                                method:'POST',
                                data:{ 
                                    beforeUid:beforeuid,
                                    afterUid:afteruid,
                                    outcome:yourid
                                },
                        }).then(result =>{
                            const {data}=result;
                            const {resultData}=data;
                            socket.close();
                            tip_popups[0].style.display='none';
                            tip_popups[1].style.display='none';
                            tip_popups[2].style.display='none';
                            // 获取弹窗中的状态
                            const popup_title=document.querySelector('.popup .title');
                            popup_title.innerHTML=`
                                失败
                            `
                            money(resultData);
                            topBox.classList.remove('display');
                            bodyBox.classList.remove('display');
                            bottomInputBox.classList.remove('display');
                            popup.style.display='flex';
                        })
                    }
                    tip_popups[1].classList.add('display');
                    setTimeout(function(){
                        tip_popups[1].classList.remove('display');
                    },2500)
                    // 重新定义一个15秒的倒计时
                    let i=15;
                    clearInterval(timer);
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        // 如果倒计时到时间，还没有出东西
                        if(i==-1){
                            console.log(beforeuid)
                            console.log(afteruid)
                            console.log(yourid)
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:beforeuid,
                                        afterUid:afteruid,
                                        outcome:yourid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    失败
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
                else{
                    // 如果我没有出错，那就禁止输入
                    inputBottom.disabled=true;
                    let i=30;
                    clearInterval(timer);
                    timer=setInterval(function(){
                        last_time.innerHTML=`
                            倒计时：${i}s
                        `
                        i--;
                        if(i==-1){
                            clearInterval(timer);
                            axios({
                                url:'http://localhost:8080/battles/outcome',
                                    method:'POST',
                                    data:{ 
                                        beforeUid:beforeuid,
                                        afterUid:afteruid,
                                        outcome:yourid
                                    },
                            }).then(result =>{
                                const {data}=result;
                                const {resultData}=data;
                                socket.close();
                                tip_popups[0].style.display='none';
                                tip_popups[1].style.display='none';
                                tip_popups[2].style.display='none';
                                // 获取弹窗中的状态
                                const popup_title=document.querySelector('.popup .title');
                                popup_title.innerHTML=`
                                    胜出
                                `
                                money(resultData);
                                topBox.classList.remove('display');
                                bodyBox.classList.remove('display');
                                bottomInputBox.classList.remove('display');
                                popup.style.display='flex';
                            })
                        }
                    },1000)
                }
                    
                  
            }
        }
        
        
    }
    // 写一个渲染结算页面的函数
    function money(content){   
        const popup_money_text=document.querySelector('.popup .get');
        popup_money_text.innerHTML=`
            ${content}
        `
    }


socket.onclose=function(e){
    console.log("连接关闭")
    console.log('websocket 断开: ' + e.code + ' ' + e.reason + ' ' + e.wasClean)
    console.log(e)
}

// window.ondblclick=function(e){
//     // if(e.key==='Enter'){
//         let beforeUid=localStorage.getItem('beforeUid');
//         let afterUid=localStorage.getItem('afterUid');
//         axios({
//             url:'http://localhost:8080/battles/outcome',
//                 method:'POST',
//                 data:{ 
//                     beforeUid,
//                     afterUid,
//                     outcome:beforeUid
//                 },
//         }).then(result =>{
//             console.log(result)
//             socket.close();
//             console.log('对战结束，关闭连接')
//         }).catch(error =>{
//             console.log(error);
//         })
//     // }
// }
// 定义一个转义函数，防止xss攻击
function escapeHtml(key){
    // 通过正则替换，将用户输入中的可能是标签的东西正常输出
    key=key.replace(/(<br[^>]*>)/g, '')
            .replace(/\&/g,"&amp;")
            .replace(/\"/g,"&quot;")
            .replace(/\'/g,"&#39;")
            .replace(/\</g,"&lt;")
            .replace(/\>/g,"&gt;");
    return key;
}