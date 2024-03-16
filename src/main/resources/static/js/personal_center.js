// 首先是获取选择栏的DOM元素，同时获取DOM元素里面的自定义属性
const personal_wrote=document.querySelector('.box .select .wrote');
const personal_like=document.querySelector('.box .select .like');
const personal_need=document.querySelector('.box .select .need');

// 获取下方盒子的DOM元素
const myWrote=document.querySelector('.box .myWrote');
const myLike=document.querySelector('.box .myLike');
const mtNeed=document.querySelector('.box .myNeed');


// 先给“近作”加上一个点击类,再给近作下方的盒子加上一个出现类
personal_wrote.classList.add('click');
myWrote.classList.add('display');

// 获取选择栏DOM元素，对其添加事件点击，通过事件冒泡来实现选择栏点击效果
const select=document.querySelector('.box .select');
select.addEventListener('click',function(e){
    if(e.target.className=='wrote'||e.target.className=='like'||e.target.className=='need'){
        // 在给点击到的元素添加点击类的时候，先移除其他元素的点击类
        document.querySelector('.click').classList.remove('click');
        e.target.classList.add('click');
        
        // 同时获取点击到的元素的自定义属性
        let i=e.target.dataset.id;
        // 该自定义属性相应的板块就会出现
        // 在给对应板块添加显示类的时候，先移除其他元素的显示类
        document.querySelector('.display').classList.remove('display');
        document.querySelector(`.my${i}`).classList.add('display');
    }
})

// 获取系统弹窗的关闭按钮
const system_close=document.querySelector('.box .system_popup .close');
// 获取系统弹窗的DOM元素,以及出现的遮罩层的DOM元素
const system_mask=document.querySelector('.box .system_mask');
// 获取系统按钮的DOM元素
const system=document.querySelector('.box .head .system');
// 对系统按钮添加一个事件
system.addEventListener('click',function(){
    system_popup.style.display='flex';
    system_mask.style.display='block';
    // 点击系统按钮的时候，禁止整个页面滚动
    document.querySelector('body').style.overflow='hidden';
    // 向后端获取数据来渲染系统页面中的个人信息
    axios({
        url:'http://localhost:8080/users/show',
        method:'GET',
    }).then(result => {
        const {data}=result;
        const {resultData}=data;
        if(data.success==true){

            // 获取头像的DOM元素
            const head=document.querySelector('.head_picture .headPicture');
            head.style.backgroundImage=`url(http://localhost:8080/imgs/thumbnail/icon/${resultData.icon})`;
            // 获取昵称，性别，简介的DOM元素
            const gender_content=document.querySelector('.gender .text .content');
            const nickname_content=document.querySelector('.nickname .text .content');
            const signature_content=document.querySelector('.signature .text .content');
            // 修改其中的内容
            if(resultData.gender==1){
                gender_content.innerHTML=`男`;
            }else{
                gender_content.innerHTML=`女`;
            }
            nickname_content.innerHTML=`${resultData.nickname}`;
            if(resultData.signature===undefined){
                signature_content.innerHTML=`无`;
            }else{
                signature_content.innerHTML=`${resultData.signature}`;
            }
        }
    })
})


// 获取系统弹窗内容的DOM元素
const system_popup_content=document.querySelector('.system_popup .popup_content');
// 获取需要变化的弹窗
const popup_boxs=document.querySelectorAll('.system_popup .popup_box [class$=popup]');

// 获取需点击按钮的父元素popup_content 
const popup_content=document.querySelector('.system_popup .popup_content');

// 通过事件委托实现
popup_content.addEventListener('click',function(e){
    let i=0;
    // 点击的时候，和需要出现的弹窗更换
    if(e.target.className=="bt"){
        i=e.target.dataset.id;
        popup_boxs[i].style.transform='translate(-100%)';
        console.log(popup_boxs[i])
        popup_boxs[i].style.display='flex';
        system_popup_content.style.transform='translate(-100%)';
    }
    else{
        i=e.target.parentElement.dataset.id;
        popup_boxs[i].style.transform='translate(-100%)';
        popup_boxs[i].style.display='flex';
        system_popup_content.style.transform='translate(-100%)';
    }
})


// 获取每一个弹窗的返回按钮
const returns=document.querySelectorAll('.system_popup .popup_box [class$=popup] .return');
// 通过for循环来对每一个return进行事件绑定
for(let j=0;j<returns.length;j++){
    returns[j].addEventListener('click',function(e){
        let i=0;
        if(e.target.className=="return"){
            i=e.target.dataset.id;
            popup_boxs[i].style.transform='translate(0%)';
            popup_boxs[i].style.display='none';
            system_popup_content.style.transform='translate(0%)';
        }
        else{
            i=e.target.parentElement.dataset.id;
            popup_boxs[i].style.transform='translate(0%)';
            popup_boxs[i].style.display='none';
            system_popup_content.style.transform='translate(0%)';
        }
    })
}

const system_popup=document.querySelector('.box .system_popup');
// 对个按钮添加一个点击事件
system_close.addEventListener('click',function(e){
    e.preventDefault();
    system_popup.style.display='none';
    system_mask.style.display='none';
    // 点击关闭按钮之后不管点击的是什么，都要复原
    for(let i=0;i<returns.length;i++){
        popup_boxs[i].style.transform='translate(0%)';
        popup_boxs[i].style.display='none';
        system_popup_content.style.transform='translate(0%)';
    }
    document.querySelector('body').style.overflow='';
})

// 获取上传头像的表单
const head_input=document.querySelector('.head_picture input');
// 监控该表单中的值是否发生变化
head_input.addEventListener('change',function(){
    const file=head_input.files[0];
    let formData=new FormData();
    formData.append('file',file);
    // 对后端发起请求
    axios({
        url:'http://localhost:8080/users/modify/icon',
        method:'POST',
        data:formData,
    }).then(result => {
        const {data}=result;
        const {resultData}=data;
        // 如果接收到的值是ture的话就返回系统弹窗
        if(data.success==true){
            system.click();
            Rendering();
        }
    })
})

// 获取性别弹窗中的男，女盒子的DOM元素
const gender_boy=document.querySelector('.gender_popup .boy');
const gender_girl=document.querySelector('.gender_popup .girl');

gender_boy.addEventListener('click',function(){
    // 获取gender_boy中的自定义属性的值
    let gender_flag=gender_boy.dataset.gd;
   
    // 将获取到的值传给后端进行判断
    axios({
        url:'http://localhost:8080/users/modify/gender',
        method:'POST',
        params:{
            gender:gender_flag
        },
        headers:{
            'Content-Type': 'application/x-www-form-urlencoded',
        }
    }).then(result =>{
        const {data}=result;
        if(data.success==true){
            // 如果返回的值为true的话说明修改成功
            returns[0].click();
            system.click();
            Rendering();
        }
    })
})

gender_girl.addEventListener('click',function(){
    // 获取gender_boy中的自定义属性的值
    let gender_flag=gender_girl.dataset.gd;
    // 将获取到的值传给后端进行判断
    axios({
        url:'http://localhost:8080/users/modify/gender',
        method:'POST',
        params:{
            gender:gender_flag
        },
        headers:{
            'Content-Type': 'application/x-www-form-urlencoded',
        }
    }).then(result =>{
        const {data}=result;
        if(data.success==true){
            // 如果返回的值为true的话说明修改成功
            returns[0].click();
            system.click();
            Rendering();
        }
    })
})


// 获取昵称弹窗的确认按钮
const nickname_confirm=document.querySelector('.nickname_popup .confirm');
// 对该按钮添加一个点击事件
nickname_confirm.addEventListener('click',function(){
    // 判断一下昵称表单中的值是否为空
    const nickname=document.querySelector('.nickname_box .nickname_popup input').value;
    if(nickname==''){
        document.querySelector('.nickname_popup .nickname_tip').style.opacity='1';
        let time=setTimeout(function(){
            document.querySelector('.nickname_popup .nickname_tip').style.opacity='0';
        },1000)
    }
    else{
        // 不为空那就发送请求给后端
        axios({
            url:'http://localhost:8080/users/modify/nickname',
            method:'POST',
            data:{
                newNickname:nickname
            }
        }).then(result => {
            // 获取拿到的信息
            const {data}=result;
            console.log(data.success);
            if(data.success==true){
                // 如果返回为正确，那么提示用户修改昵称成功，返回个人页面
                document.querySelector('.nickname_box .inputBox').style.display='none';
                document.querySelector('.nickname_box .confirm').style.display='none';
                document.querySelector('.nickname_box .success').style.display='flex';
            }
        })
    }
})

// 获取简介弹窗的确认按钮
const signature_confirm=document.querySelector('.signature_box .signature_popup .confirm');
signature_confirm.addEventListener('click',function(){
    const signature=document.querySelector('.signature_box .signature_popup input').value;
    axios({
        url:'http://localhost:8080/users/modify/signature',
            method:'POST',
            params:{
                signature
            },
            headers:{
                'Content-Type': 'application/x-www-form-urlencoded',
            }
    }).then(result =>{
        console.log(result);
        const {data}=result;
        if(data.success==true){
            // 如果返回为正确，那么提示用户修改昵称成功，返回个人页面
            document.querySelector('.signature_box input').style.display='none';
            document.querySelector('.signature_box .confirm').style.display='none';
            document.querySelector('.signature_box .success').style.display='flex';
        }
    })
})


// 获取密码弹窗中的确认按钮
const password_confirm=document.querySelector('.password_popup .confirm');
password_confirm.addEventListener('click',function(){
    // 获取此时密码表单中的值
    const oldpassword=document.querySelector('.password_popup .inputBox .old').value;
    const newfirstword=document.querySelector('.password_popup .inputBox .new').value;
    const newsecondword=document.querySelector('.password_popup .inputBox .new_second').value;
    const new_first_tip=document.querySelector('.password_popup .new_first_tip');
    const new_second_tip=document.querySelector('.password_popup .new_second_tip');
    const old_tip=document.querySelector('.password_popup .old_tip');
    // 先看看密码表单中是否为空
    if(newfirstword==''&&newsecondword==''){
        new_first_tip.style.opacity='1';
        time=setTimeout(function(){
            new_first_tip.style.opacity='0';
        },1000)
    }
    else{
        // 判断新密码的格式是否正确
        // 定义正则表达式
        const judge=/^[a-zA-Z0-9_]{6,12}$/;
        // 如果为真，那就是密码符合格式
        if(judge.test(newfirstword)){
            // 将表单中的值全部传给后端
            // 如果两次输入的密码不一致，那么就出现密码不一致提示
            if(newfirstword!=newsecondword){
                new_second_tip.style.opacity='1';
                time=setTimeout(function(){
                    new_second_tip.style.opacity='0';
                },1000)
                return;
            }
            axios({
                url:'http://localhost:8080/users/modify/pwd',
                method:'POST',
                data:{
                    oldPassword:oldpassword,
                    newPassword:newfirstword
                },
            }).then(result =>{
                // 获取拿到的信息
                const {data}=result;
                console.log(data.success);
                if(data.success==true){
                    // 如果返回为正确，那么提示用户修改密码成功，请重新登录
                    document.querySelector('.password_box .inputBox').style.display='none';
                    document.querySelector('.password_box .confirm').style.display='none';
                    document.querySelector('.password_box .success').style.display='flex';
                }
            })
        }
        // 如果新的密码格式错误，出现提示
        else {
            new_first_tip.style.opacity='1';
            time=setTimeout(function(){
                new_first_tip.style.opacity='0';
            },1000)
        }       
    }
})

// 定义的一个渲染个人主页函数
async function Rendering(){
     // 获取页面中的头像和文本
     const community_headPicture=document.querySelector('.box .head .user_profile');
     const community_name=document.querySelector('.box .head .text .name');
     const community_introduce=document.querySelector('.box .head .text .introduce');
     console.log(community_introduce)
     // 向后端发起请求，渲染页面
     await axios({
         url:'http://localhost:8080/users/show',
         method:'GET',
     }).then(result =>{
         const {data}=result;
         const {resultData}=data;
         if(data.success==true){
             document.querySelector('.box .head .user_profile div').style.display='none';
             community_headPicture.style.backgroundImage=`url(http://localhost:8080/imgs/thumbnail/icon/${resultData.icon})`;
             community_name.innerHTML=`${resultData.nickname}`;
             if(resultData.signature==''){
                 community_introduce.innerHTML=`无`;
             }else{
                 community_introduce.innerHTML=`${resultData.signature}`;
             }
         }
     })
}

first_render();

// 定义一个第一次进入进行页面渲染的函数
async function first_render(){
    // 渲染页面
    await Rendering();
    await axios({
        url:'http://localhost:8080/posts/1',
        method:'GET',
    }).then(result =>{
        const content=document.querySelector('.myWrote .content');
        // 定义一个数组
        let worteids=[];
        Card_Rendering(result,content,worteids); 
        // 拿到本地中的数组
        worteids=localStorage.getItem('arr');
        // 存到本地中去
        localStorage.setItem('worteIds',`${worteids}`);
        // 获取点击删除帖子按钮出现的弹窗的DOM元素
        const delete_popup =document.querySelector('.Wrote_popup');
        // 先获取帖子上的删除按钮
        // 对该按钮添加一个点击事件
        const post_card_deletes=document.querySelectorAll('.post_card .delete');
        // 帖子的id
        let cardId=0;
        for(let i=0;i<post_card_deletes.length;i++){
            post_card_deletes[i].addEventListener('click',function(e){
                e.stopPropagation();
                delete_popup.style.display='flex';
                cardId=post_card_deletes[i].dataset.cardid;
            })
        }
        // 再获取弹窗中的两个按钮
        const delete_confirm=document.querySelector('.Wrote_popup .confirm');
        const delete_cancel=document.querySelector('.Wrote_popup .cancel');
        // 点击取消按钮的时候，隐藏弹窗
        delete_cancel.addEventListener('click',function(){
            delete_popup.style.display='none';
        })   
        // 点击确认按钮的时候，发送数据给后端，删除帖子
        delete_confirm.addEventListener('click',function(){
            axios({
                url:`http://localhost:8080/posts/${cardId}`,
                method:'DELETE',
            }).then(response => {
                const {data}=response;
                if(data.success==true){
                    delete_popup.style.display='none';
                    window.location.href='personal_center.html';
                }
            })
        })
    })
    // 收藏的东西
    await axios({
        url:'http://localhost:8080/posts/collected/1',
        method:'GET',
    }).then(result =>{
        console.log(result)
        const content=document.querySelector('.myNeed .content');
        let needids=[];
        Card_Rendering(result,content,needids);
        // 从本地数据中拿到arr,把arr定义为needs
        needids=localStorage.getItem('arr');
        // 存到本地中
        localStorage.setItem('needIds',`${needids}`);
    })
}


// 页面渲染完成之后
// 获取页面中包含我发过的帖子的父元素
const myworte_content=document.querySelector('.myWrote .content');
// 对这个父元素添加事件
myworte_content.addEventListener('click',function(e){
    e.stopPropagation();
    // 通过点击获取被点击元素的自定义属性
    let worte_postId=e.target.dataset.writeindex;
    // 从本地中拿到我写过的帖子的数组
    let worteId=localStorage.getItem('worteIds');
    // 逗号切割成数组
    worteId=worteId.split(',');
    // 点击到的帖子的id
    let worteid=worteId[worte_postId];
    // 将获得到的帖子id存到本地中去
    localStorage.setItem('post_id',`${worteid}`);
    // 将点击到的自定义属性存储到本地
    localStorage.setItem('card_id',`${worte_postId}`);
    // 将状态值存储到本地，0表示是在推荐模块进入帖子，1表示是在个人主页近作模块进入帖子，2表示在个人主页收藏模块进入帖子，3表示在搜索模块进入帖子
    localStorage.setItem('statu','1');
    // 点击跳转帖子详情
    window.location.href='post_details.html';
})

// 获取我收藏过的帖子的父元素
const myneed_content=document.querySelector('.myNeed .content');
// 对这个父元素添加事件
myneed_content.addEventListener('click',function(e){
    e.stopPropagation();
    // 通过点击获取被点击元素的自定义属性
    let need_postId=e.target.dataset.writeindex;
    // 从本地中拿到我写过的帖子的数组
    let needId=localStorage.getItem('needIds');
    // 逗号切割成数组
    needId=needId.split(',');
    // 点击到的帖子的id
    let needid=needId[need_postId];
    // 将获得到的帖子id存到本地中去
    localStorage.setItem('post_id',`${needid}`);
    // 将点击到的自定义属性存储到本地
    localStorage.setItem('card_id',`${need_postId}`);
    // 将状态值存储到本地，0表示是在推荐模块进入帖子，1表示是在个人主页近作模块进入帖子，2表示在个人主页收藏模块进入帖子，3表示在搜索模块进入帖子
    localStorage.setItem('statu','2');
    // 点击跳转帖子详情
    window.location.href='post_details.html';
})


// 定义一个渲染个人主页帖子的函数
function Card_Rendering(result,content,arr){
    // 获取到从后端拿到的数据
    const {data}=result;
    const {resultData}=data;
    const length=resultData.length;

    for(let i=0;i<length;i++){
        // 接受到的数据根据逗号切割图片
        const str_image=resultData[i].images.split(',');
        // 添加两个判断，点赞和收藏是否处于点亮状态
        let like_class='icon-dianzan';
        let collect_class='icon-shoucang';
        if(resultData[i].isLike==true){
            // 如果是点赞状态
            like_class='icon-dianzan_kuai';
        }
        
        if(resultData[i].isCollection==true){
            // 如果是收藏状态
            collect_class='icon-xingxing';
        }
        let div=document.createElement('div');
        div.classList.add('post_card');
        div.setAttribute("data-writeIndex",`${i}`);
        div.innerHTML=`
            <div class="post_info" data-writeIndex="${i}">
                <div class="post_person" data-writeIndex="${i}">
                    <div class="post_person_avatar data-writeIndex="${i}"">
                        <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData[i].icon}" alt="" data-writeIndex="${i}">
                    </div>
                    <div class="post_person_name" data-writeIndex="${i}">${resultData[i].nickname}</div>
                </div>
                <div class="post_txt" data-writeIndex="${i}">${resultData[i].title}</div>
            </div>
            <div class="post_bgimg" data-writeIndex="${i}">
                <img src="http://localhost:8080/imgs/thumbnail/post/${str_image[0]}" alt="" data-writeIndex="${i}">
            </div>
            <div class="post_data" data-writeIndex="${i}">
                <div class="post_icon" data-writeIndex="${i}">
                    <i class="iconfont ${like_class}" data-writeIndex="${i}"></i>
                    <span class="post_like" data-writeIndex="${i}">${resultData[i].likes}</span>
                </div>
                <div class="post_icon" data-writeIndex="${i}">
                    <i class="iconfont ${collect_class}" data-writeIndex="${i}"></i>
                    <span class="post_comment" data-writeIndex="${i}">${resultData[i].collections}</span>
                </div>
            </div>
            <div class="delete" data-cardid="${resultData[i].id}">
                <div class="close">
                    <div class="iconfont icon-close"></div>
                </div>
            </div>
        `  
        content.appendChild(div);
        // 每一次成功渲染之后，就将此时的帖子id存到数组中去
        arr[i]=resultData[i].id;
    }
    // 循环结束之后，将帖子id放在本地
    localStorage.setItem('arr',`${arr}`);
}
// 获取页面中交子货币需要改变的地方
const money_number=document.querySelector('.money_numberBox .money_number');
// 向后端发送请求
axios({
    url:'http://localhost:8080/user/infos',
    method:'GET'
}).then(result =>{
    console.log(result);
    const {data}=result;
    const {resultData}=data;
    money_number.innerHTML=`${resultData.money}`;
})