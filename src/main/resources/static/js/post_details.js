    let index=1;

    // 从本地中拿到status状态值，判断要接哪一个接口
    let statu=localStorage.getItem('statu');
    // 从本地存储中拿到id
    let id=localStorage.getItem('card_id');

    if(statu==0){
        // 对后端发送请求
        axios({
            url:`http://localhost:8080/posts/hot/${index}`,
            method:'GET'
        }).then(result => {
            console.log(result)  
            post_render(result);
        })
    }
    if(statu==1){
        axios({
            url:'http://localhost:8080/posts/1',
            method:'GET'
        }).then(result =>{
            console.log(result);
            post_render(result);
        })
    }
    if(statu==2){
        axios({
            url:'http://localhost:8080/posts/collected/1',
            method:'GET'
        }).then(result =>{
            console.log(result);
            post_render(result);
        })
    }
    if(statu==3){
        let currentPage=1;
        // 如果是等于3，需要从本地中拿到搜索词
        let keywords=localStorage.getItem('search_keyword')
        axios({
            url:`http://localhost:8080/posts/search/${keywords}/${currentPage}`,
            method:'GET'
        }).then(result =>{
            console.log(result);
            post_render(result);
        })
    }
    

function post_render(result){
        const {data}=result;
        const {resultData}=data;
        console.log(resultData[id])
        // 对拿到的时间进行正则替换
        let createTime=resultData[id].createTime.replace(/T/i,' ');
        createTime=createTime.slice(0,19);
        // 将返回来的图片数组做一个切割
        const str=resultData[id].images.split(', ');
        const box=document.querySelector('.box');
        // 获取该帖子渲染评论的DOM元素
        const comment_title=document.querySelector('.comment_title');
        let comments=resultData[id].comments-1;
        if(resultData[id].comments!=undefined){
            comment_title.innerHTML=`评论区（${comments}）`;
        }
        else{
            comment_title.innerHTML=`评论区（0）`;
        }
        box.innerHTML=`
        <div class="titleBox">
            <div class="return"></div>
            <div class="userBox">
                <div class="user_picture">
                    <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData[id].icon}">
                </div>
                <div class="user">${resultData[id].nickname}</div>
            </div>
        </div>
        <div class="playBox">
            <div class="left">
                <div class="iconbox">
                    <i class="iconfont icon-xiangzuojiantou"></i>
                </div>
            </div>
            <div class="pictureBox">
                <div class="picture display">
                    <img src="http://localhost:8080/imgs/thumbnail/post/${str[0]}">
                </div>
                <div class="picture">
                    <img src="http://localhost:8080/imgs/thumbnail/post/${str[1]}">
                </div>
                <div class="picture">
                    <img src="http://localhost:8080/imgs/thumbnail/post/${str[2]}">
                </div>
                <div class="picture">
                    <img src="http://localhost:8080/imgs/thumbnail/post/${str[3]}">
                </div>
            </div>
            <div class="right">
                <div class="iconbox">
                    <i class="iconfont icon-xiangyoujiantou"></i>
                </div>
            </div>
        </div>
        <div class="contentBox">
            <div class="title">${resultData[id].title}</div>
            <div class="content">${resultData[id].content}</div>
        </div>
        <div class="timeBox">
            <div class="text">${resultData[id].nickname}</div>
            <div class="time">${createTime}</div>
        </div>
        <div class="btBox">
            <div class="likeBox">
                <div class="iconbox">
                    <i class="iconfont icon-dianzan"></i>
                </div>
                <div class="number">${resultData[id].likes}</div>
            </div>
            <div class="collectBox">
                <div class="iconbox">
                    <i class="iconfont icon-shoucang"></i>
                </div>
                <div class="number">${resultData[id].collections}</div>
            </div>
        </div>
        `
        // 渲染完成之后，获取点赞和收藏两个DOM元素
        const likeBt=document.querySelector('.icon-dianzan');
        const collectBt=document.querySelector('.icon-shoucang');
        let isLike;
        let isCollection;
        // 添加两个判断，点赞和收藏是否处于点亮状态
        if(resultData[id].isLike==true){
            // 如果是点赞状态
            // 先移除原先点赞中的类
            likeBt.classList.remove('icon-dianzan');
            likeBt.classList.add('icon-dianzan_kuai');
            isLike=1;
        }
        if(resultData[id].isCollection==true){
            // 如果是收藏状态
            // 先移除原先收藏中的类
            collectBt.classList.remove('icon-shoucang');
            collectBt.classList.add('icon-xingxing');
            isCollection=1;
        }
        // 获取页面中的点赞盒子，收藏盒子
        const likeBox=document.querySelector('.likeBox');
        const collectBox=document.querySelector('.collectBox');
        // 对两个盒子添加点击事件
        likeBox.addEventListener('click',function(e){
            // 如果点击到的元素是icon-dianzan 就说明还没有点赞
            if(e.target.className=='iconfont icon-dianzan'){  
                // 同时需要发一个请求给后端告诉后端这个帖子被点赞了
                axios({
                    url:`http://localhost:8080/posts/likes`,
                    method:'POST',
                    data:{
                        postId:id,
                        status:'1'
                    }
                }).then(result =>{
                    const {data}=result;
                    // 如果点赞成功
                    if(data.success==true){
                        // 删除元素中的icon-dianzan类
                        e.target.classList.remove('icon-dianzan');
                        e.target.classList.add('icon-dianzan_kuai');
                        // 获取此时页面中的点赞数
                        let likes=e.target.parentNode.nextElementSibling.innerText;
                        likes++;
                        e.target.parentNode.nextElementSibling.innerHTML=`
                            ${likes}
                        `
                    }
                })
            }
            else{
                // 同时需要发一个请求给后端告诉后端这个帖子被点赞了
                axios({
                    url:`http://localhost:8080/posts/likes`,
                    method:'POST',
                    data:{
                        postId:id,
                        status:'0'
                    }
                }).then(result =>{
                    console.log(result)
                    const {data}=result;
                    // 取消点赞
                    if(data.success==true){
                        // 删除元素中的icon-dianzan_kuai类
                        e.target.classList.remove('icon-dianzan_kuai');
                        e.target.classList.add('icon-dianzan');
                        // 获取此时页面中的点赞数
                        let likes=e.target.parentNode.nextElementSibling.innerText;
                        likes--;
                        e.target.parentNode.nextElementSibling.innerHTML=`
                            ${likes}
                        `
                    }
                })
            }
        })
        collectBox.addEventListener('click',function(e){
            // 如果点击到的元素是icon-shoucang 就说明还没有收藏
            if(e.target.className=='iconfont icon-shoucang'){
                // 同时需要发一个请求给后端告诉后端这个帖子被点赞了
                axios({
                    url:`http://localhost:8080/posts/collections`,
                    method:'POST',
                    data:{
                        postId:id,
                        status:'1'
                    }
                }).then(result =>{
                    const {data}=result;
                    // 如果收藏成功
                    if(data.success==true){
                        // 删除元素中的icon-shoucang类
                        e.target.classList.remove('icon-shoucang');
                        e.target.classList.add('icon-xingxing');
                        // 获取此时页面中的收藏数
                        let collects=e.target.parentNode.nextElementSibling.innerText;
                        collects++;
                        e.target.parentNode.nextElementSibling.innerHTML=`
                            ${collects}
                        `
                    }
                })
            }
            else{
                // 同时需要发一个请求给后端告诉后端这个帖子被点赞了
                axios({
                    url:`http://localhost:8080/posts/collections`,
                    method:'POST',
                    data:{
                        postId:id,
                        status:'0'
                    }
                }).then(result =>{
                    const {data}=result;
                    // 取消收藏
                    if(data.success==true){
                        // 删除元素中的icon-shoucang类
                        e.target.classList.remove('icon-xingxing');
                        e.target.classList.add('icon-shoucang');
                        // 获取此时页面中的收藏数
                        let collects=e.target.parentNode.nextElementSibling.innerText;
                        collects--;
                        e.target.parentNode.nextElementSibling.innerHTML=`
                            ${collects}
                        `
                    }
                })
            }
        })


       const left=document.querySelector('.playBox .left');
       const right=document.querySelector('.playBox .right');
       let i=1;
        // 当点击向左箭头的时候
       left.addEventListener('click',function(){
        if(i==1){
            return;
        }
        else {
            i--;
            document.querySelector('.display').classList.remove('display');
            document.querySelector(`.playBox .pictureBox .picture:nth-child(${i})`).classList.add('display');
            
        }
       })
       //    当点击向右箭头的时候
       right.addEventListener('click',function(){
        if(i==str.length){
            return;
        }
        else {
            i++;
            document.querySelector('.display').classList.remove('display');
            document.querySelector(`.playBox .pictureBox .picture:nth-child(${i})`).classList.add('display');
            
        }
       })
       // 获取页面中的顶部的返回按钮
       const post_return=document.querySelector('.box .titleBox .return');
       post_return.addEventListener('click',function(){
            if(statu==1||statu==2){
                window.location.href='personal_center.html';
            }
            if(statu==0){
                window.location.href='community.html';
            }
            if(statu==3){
                window.history.back();
                // 回到页面的时候保持搜索状态
                localStorage.setItem('keep_search','1');
            }
       })
       // 获取要进行事件绑定的父元素
        const btBox=document.querySelector('.box .btBox');
        // btBox.addEventListener('click',function(e){
        //     if(e.target.className=='iconfont icon-dianzan'||e.target.className=='iconfont icon-shoucang'||e.target.className=='iconfont icon-dianzan_kuai'||e.target.className=='iconfont icon-xingxing'){
        //         // 如果点击到的是点赞的话
        //         if(e.target.className=='iconfont icon-dianzan'||e.target.className=='iconfont icon-dianzan_kuai'){
                    
        //             axios({
        //                 url:`http://localhost:8080/posts/likes`,
        //                 method:'POST',
        //                 data:{
        //                     postId:resultData[id].id,
        //                     status:'1'
        //                 }
        //             }).then(result =>{
        //                 const {data}=result;
        //                 if(data.success==false){
        //                     axios({
        //                         url:`http://localhost:8080/posts/hot/${index}`,
        //                          method:'GET'
        //                     }).then(result => {  
        //                         const {data}=result;
        //                         const {resultData}=data;
        //                         if(e.target.className=='iconfont icon-dianzan'){
        //                             e.target.parentNode.nextElementSibling.innerHTML=`
        //                                 ${resultData[id].likes}
        //                             `
        //                         }
        //                     })
        //                 }
        //             })
        //         }
        //         else {
        //             axios({
        //                 url:`http://localhost:8080/posts/collections`,
        //                 method:'POST',
        //                 data:{
        //                     postId:resultData[id].id,
        //                     status:'1'
        //                 }
        //             }).then(result =>{
        //                 const {data}=result;
        //                 if(data.success==true){
        //                     axios({
        //                         url:`http://localhost:8080/posts/hot/${index}`,
        //                         method:'GET',
        //                     }).then(result => {  
        //                         const {data}=result;
        //                         const {resultData}=data;
        //                         if(e.target.className=='iconfont icon-shoucang'){
        //                             e.target.parentNode.nextElementSibling.innerHTML=`
        //                                 ${resultData[id].collections}
        //                             `
        //                         }
        //                     })
        //                 }
        //             })
        //         }
        //     }
        // })
}

//    对发表评论处的头像进行渲染
const comment_userPicture=document.querySelector('.comment_inputBox .user_picture');
axios({
    url:'http://localhost:8080/users/show',
    method:'GET'
}).then(result =>{
    console.log(result)
    // 获取到从后端拿到的数据
    const {data}=result;
    const {resultData}=data;
    comment_userPicture.innerHTML=`
        <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData.icon}">
    `
    // 获取底部评论的头像
    const sub_user_picture=document.querySelector('.subComment_inputBox .user_picture');
    sub_user_picture.innerHTML=`
        <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData.icon}">
    `
})

// 评论的页数
var currentpage=1;
// 从本地存储中拿到帖子id
let post_id=localStorage.getItem('post_id');
console.log(post_id)
// 放一个标志flag,值为1，需要换页(即多次调用comment_content函数)，值为0不需要
var flag=0;
// 新增加的评论的条数
var newCommentIndex=0;
// 这是第几条评论的标志值
var number=0;

// 获取评论区的表单
const comment_text=document.querySelector('.comment_inputBox .text');
const comment_bt=document.querySelector('.comment_inputBox button');
// 当用户点击发送的时候，给后端发送数据
comment_bt.addEventListener('click',function(){
    // 获取此时表单中的值
    let value=comment_text.value;
    value=escapeHtml(value);
    axios({
        url:'http://localhost:8080/posts/comments/save',
        method:'POST',
        data:{
            postId:post_id,
            content:value
        }
    }).then(result =>{
        const {data}=result;
        if(data.success==true){
            // 重新定义的一个新发布评论的函数
            newComment();
        }
    })
    // 点击之后，清空表单中的内容
    comment_text.value=''; 
}) 

// 当用户按下回车的时候，给后端发送数据
comment_text.addEventListener('keyup',function(e){
    if(e.key==='Enter'){
        // 获取此时表单中的值
        let value=comment_text.value;
        value=escapeHtml(value);
        if(value.trim()!==''){
            axios({
                url:'http://localhost:8080/posts/comments/save',
                method:'POST',
                data:{
                    postId:post_id,
                    content:value
                }
            }).then(result =>{
                const {data}=result;
                if(data.success==true){
                    newComment();
                }
            })
            // 点击之后，清空表单中的内容
            comment_text.value='';
        }
    }  
}) 

let subComment;
// 页面渲染函数代码
function render(subComment,resultData,j){
    console.log(resultData);
    // 获取数据中的创建时间，对其通过正则替换的方式，变成需要的样子
    let createime=resultData[j].createTime.replace(/T/i,' ');
    // 先获取该条评论中是否有回复
    subComment=resultData[j].postSubCommentNum;
    div=document.createElement('div');
    number++;
    div.classList.add('comment');
    if(subComment==0){
        div.innerHTML=`
            <div class="head">
                <div class="user_picture">
                    <img src="http://localhost:8080/imgs/icon/${resultData[j].icon}">
                </div>
                <div class="username">${resultData[j].nickname}</div>
            </div>
            <div class="body">
                <div class="content">${resultData[j].content}</div>
                <div class="more">
                    <div class="createtime">${createime}</div>
                    <div class="message" data-commentid="${resultData[j].id}" data-uid="${resultData[j].uid}" data-num="${number}" data-username="${resultData[j].nickname}">
                        <div class="iconfont icon-pinglun" data-commentid="${resultData[j].id}" data-uid="${resultData[j].uid}" data-num="${number}" data-username="${resultData[j].nickname}"></div>
                        <div class="number" data-commentid="${resultData[j].id}" data-uid="${resultData[j].uid}" data-num="${number}" data-username="${resultData[j].nickname}"></div>
                    </div>
                </div>
            </div>
            <div class="sonBodyBox">
            </div>
        `
    }
    else {
        div.innerHTML=`
            <div class="head">
                <div class="user_picture">
                    <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData[j].icon}">
                </div>
                <div class="username">${resultData[j].nickname}</div>
            </div>
            <div class="body">
                <div class="content">${resultData[j].content}</div>
                <div class="more">
                    <div class="createtime">${createime}</div>
                    <div class="message" data-commentid="${resultData[j].id}" data-uid="${resultData[j].uid}" data-num="${number}" data-username="${resultData[j].nickname}">
                        <div class="iconfont icon-pinglun" data-commentid="${resultData[j].id}" data-uid="${resultData[j].uid}" data-num="${number}" data-username="${resultData[j].nickname}"></div>
                        <div class="number" data-commentid="${resultData[j].id}" data-uid="${resultData[j].uid}" data-num="${number}" data-username="${resultData[j].nickname}">${resultData[j].postSubCommentNum}</div>
                    </div>
                </div>
            </div>
            <div class="sonBodyBox">
            </div>
        `
    }
}


// 新的发布评论的函数
async function newComment(){
    // 对页面中的评论区进行渲染
    console.log(currentpage)
    const comment_content=document.querySelector('.comment_content');
    await axios({
        url:`http://localhost:8080/posts/comments/${post_id}/1`,
        method:'GET'
    }).then(result => {
        const {data}=result;
        const {resultData}=data;
        console.log(resultData[0])
        
        // 固定一个参数为0
        const intial_index=0;
        render(subComment,resultData,intial_index)
        // 再发起一次请求渲染评论区评论数
        axios({
            url:`http://localhost:8080/posts/hot/${index}`,
            method:'GET'
        }).then(result =>{
            const {data}=result;
            const {resultData}=data;
            console.log(resultData)
            // 拿到的评论个数-1
            let comments=resultData[id].comments-1;
            // 获取该帖子渲染评论的DOM元素
            const comment_title=document.querySelector('.comment_title');
            comment_title.innerHTML=`评论区（${comments}）`;
        })
        comment_content.insertBefore(div,comment_content.children[0]);
        newCommentIndex++;
    })  
}


// 渲染评论区函数
async function comment_render(){
    // 如果新添加的评论有20条，那么再次渲染的时候就需要跳过一张表
    if(newCommentIndex==20){
        currentpage++;
    }
    // 对页面中的评论区进行渲染
    const comment_content=document.querySelector('.comment_content');
    // 获取评论区尾部的盒子
    const bottom=document.querySelector('.bottom');
    await axios({
        url:`http://localhost:8080/posts/comments/${post_id}/${currentpage}`,
        method:'GET'
    }).then(result =>{
        const {data}=result;
        const {resultData}=data;
        console.log(resultData)
        // console.log(resultData)
        
        // 如果评论到底了
        if(data.success==false){
            bottom.style.opacity='1';
        }

        // 通过循环来对页面进行渲染
        // j是第几条评论的标志
        let j=newCommentIndex;
        while(j<resultData.length){
            render(subComment,resultData,j)
            comment_content.append(div);
            j++;
            console.log(newCommentIndex)
        }
        if(j<20){
            bottom.style.opacity='1';
            flag=0;
        }
        else{
            // 该次请求达到最大20条信息，返回currepage+1，进行下一次请求
            currentpage++;
            flag=1;
        }
    })
}


async function second_render(){
    // 获得最后一条评论，当评论在可视窗口出现的时候，向后端发送一次请求，再获取20条评论
    const comments=document.querySelectorAll('.comment_content .comment');

    // 拿到最后一个评论
    let last_comment=comments[comments.length-1];
    // 获得最后一个评论自身的高度
    let last_commentHeight=last_comment.clientHeight;
    // 评论到最页面最上面的高度
    let last_commentTop=last_comment.offsetTop;
    // 获取可视区域的高度
    let clientHeight=document.documentElement.clientHeight;
    // 获得滑动距离
    let scrollTop=document.documentElement.scrollTop;
   
    if(last_commentTop+last_commentHeight/2<scrollTop+clientHeight){
        again_response();
    }
}

//  手写一个防抖函数
function debounce(fn,t){
    let resonseTime;
    return function(){
        if(resonseTime) clearTimeout(resonseTime)
        resonseTime=setTimeout(function(){
            fn();
        },t)
    }
}

// 发抖实现刷新页面，向后端发送请求，new为新输入的评论，如果new达到20，页数+1，实现页面无缝刷新
function again_response(){
    // 如果评论的最后一个出现在可视窗口，向后端发送请求
    if(flag==1){
        comment_render();
    }
}

// 获取页面底部的输入框
const bottomBox=document.querySelector('.subComment_inputBox');
const bottomBox_txt=document.querySelector('.subComment_inputBox .text');

// 定义新增子评论的标志值
var newSonCommentIndex=0;
var sonCurrentpage=1;
let sonFlag=0;

again_render();


async function again_render(){
    // 等待页面中的评论区全部做好，执行后面的操作
    await comment_render();
    window.addEventListener('scroll',debounce(second_render,1000))
    // 获取页面中的评论的回复按钮
    const comment_content=document.querySelector('.comment_content');
    let comment_id;
    let commentedUid;

    // 点击到达的是第几个主评论的评论区
    let num;
    // 对该按钮添加一个点击事件
    comment_content.addEventListener('click',function(e){
        // 如果点击到的是主评论中的回复按钮
        if(e.target.className=='message'||e.target.className=='iconfont icon-pinglun'||e.target.className=='number'){
            num=e.target.dataset.num;
            // 判断点击的是不是和上一次是同一个
            let old_num=localStorage.getItem('num');
            if(num!=old_num){
                // 重置newSonCommentIndex的值
                newSonCommentIndex=0;
                // 获取点击得到的自定义属性中的用户名
                const username=e.target.dataset.username;
                bottomBox_txt.setAttribute('placeholder',`回复 ${username}`);
                bottomBox.style.display='flex';
                // 同时获取点击到的评论的id和被评论的用户id
                comment_id=e.target.dataset.commentid;
                commentedUid=e.target.dataset.uid; 
                console.log(comment_id)
                
                // 将点击拿到的值num存到本地中
                localStorage.setItem('num',`${num}`);
                // 点击之后开始渲染子评论
                sonComment_render(comment_id,num);
            }
        }
        // 如果点击到的是子评论中的回复按钮
        if(e.target.className=='sonMessage'||e.target.className=='sonIcon iconfont icon-pinglun'){
            // 获取点击得到的自定义属性中的用户名
            const username=e.target.dataset.user;
            bottomBox_txt.setAttribute('placeholder',`回复 ${username}`);
            bottomBox.style.display='flex';
        }
    })
    // 对底部输入框添加一个事件
    bottomBox.addEventListener('keyup',function(e){
        if(e.key==='Enter'){
            if(bottomBox_txt.value.trim()!==''){
                inputSonComment(comment_id,commentedUid);
                newSonComment(comment_id,num);
            }
        }
    })
    // 获取底部输入框旁边的发送按钮
    const bottomBox_bt=document.querySelector('.subComment_inputBox button');
    bottomBox_bt.addEventListener('click',function(){
        inputSonComment(comment_id,commentedUid);
        newSonComment(comment_id,num);
    })
}



// 输入子评论函数
function inputSonComment(comment_id,commentedUid){
    let bottomBox_value=bottomBox_txt.value;
    bottomBox_value=escapeHtml(bottomBox_value);
    axios({
        url:'http://localhost:8080/posts/comments/sub/save',
        method:'POST',
        data:{
            commentId:comment_id,
            commentedUid:commentedUid,
            content:bottomBox_value
        }
    }).then(result =>{   
        if(result.data.success==true){
            console.log("子评论输入成功")
        }
    }).catch(error =>{
        console.log(error);
    })
    bottomBox_txt.value='';
}

// 获取回复的输入框的DOM元素
const sub_user_picture=document.querySelector('.subComment_inputBox .user_picture');
const sub_text_input=document.querySelector('.subComment_inputBox .text');
// 对输入框添加一个事件
sub_text_input.addEventListener('focus',function(){
    sub_user_picture.style.height='100px';
})
sub_text_input.addEventListener('blur',function(){
    sub_user_picture.style.height='70px';
    bottomBox.style.display='none';
})

// 输入新的子评论的渲染函数
function newSonComment(comment_id,num){
    // 对页面中的评论区进行渲染
    console.log(currentpage)
    const sonComment_content=document.querySelector(`.comment_content .comment:nth-child(${num}) .sonBodyBox`);
    console.log(sonComment_content);
    // 获取主评论中的回复数字
    const sonComment_number=document.querySelector(`.comment_content .comment:nth-child(${num}) .body .more .message .number`);
    let number=sonComment_number.innerText;
    axios({
        url:`http://localhost:8080/posts/comments/sub/${comment_id}/1`,
        method:'GET'
    }).then(result => {
        const {data}=result;
        const {resultData}=data;
        console.log(resultData[0])
        
        // 固定一个参数为0
        const intial_index=0;
        sonrender(resultData,intial_index);
        // 渲染一次子评论，评论数+1
        number++;
        sonComment_number.innerHTML=`${number}`;
        sonComment_content.insertBefore(div,sonComment_content.children[0]);
        newSonCommentIndex++;
    })  
}

// 子评论的渲染代码
function sonrender(resultData,j){
    // 获取数据中的创建时间，对其通过正则替换的方式，变成需要的样子
    let createime=resultData[j].createTime.replace(/T/i,' ');
    div=document.createElement('div');
    div.classList.add('sonComment');
    if(resultData[j].commentedNickname===resultData[j].nickname){
        div.innerHTML=`
            <div class="sonHead">
                <div class="user_picture">
                    <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData[j].icon}">
                </div>
                <div class="username">${resultData[j].nickname}</div>
            </div>
            <div class="sonBody">
                <div class="content">${resultData[j].content}</div>
                <div class="more">
                    <div class="createtime">${createime}</div>
                    <div class="sonMessage" data-user="${resultData[j].nickname}">
                        <div class="sonIcon iconfont icon-pinglun" data-user="${resultData[j].nickname}"></div>
                    </div>
                </div>
            </div>
        `
    }
    else{
        div.innerHTML=`
            <div class="sonHead">
                <div class="user_picture">
                    <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData[j].icon}">
                </div>
                <div class="username">${resultData[j].nickname}</div>
                <div class="text">回复</div>
                <div class="otherUsername">${resultData[j].commentedNickname}</div>
            </div>
            <div class="sonBody">
                <div class="content">${resultData[j].content}</div>
                <div class="more">
                    <div class="createtime">${createime}</div>
                    <div class="sonMessage" data-user="${resultData[j].nickname}">
                        <div class="sonIcon iconfont icon-pinglun" data-user="${resultData[j].nickname}"></div>
                    </div>
                </div>
            </div>
        `
    }  
}

// 渲染子评论的函数
function sonComment_render(comment_id,num){
    // 如果新添加的评论有20条，那么再次渲染的时候就需要跳过一张表
    if(newSonCommentIndex==10){
        sonCurrentpage++;
    }
    // 对页面中的评论区进行渲染
    const sonComment_content=document.querySelector(`.comment_content .comment:nth-child(${num}) .sonBodyBox`);

    axios({
        url:`http://localhost:8080/posts/comments/sub/${comment_id}/${sonCurrentpage}`,
        method:'GET'
    }).then(result =>{
        const {data}=result;
        const {resultData}=data;
        console.log(resultData)
        
        // 通过循环来对页面进行渲染
        // j是第几条评论的标志
        let j=newSonCommentIndex;
        while(j<resultData.length){
            sonrender(resultData,j)
            sonComment_content.append(div);
            j++;
            console.log(newSonCommentIndex)
        }
        if(j<10){
            flag=0;
        }
        else{
            // 该次请求达到最大20条信息，返回currepage+1，进行下一次请求
            sonCurrentpage++;
            flag=1;
        }
    })
}
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