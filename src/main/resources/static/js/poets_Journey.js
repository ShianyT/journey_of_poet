// 先获取两个字幕文本的盒子元素
const pt_1=document.querySelector('.scene_1 .startTextBox .prompt');
const txt_1=document.querySelector('.scene_1 .startTextBox .text');

// 通过设置一个固定时间来确保两个字幕出现的顺序以及后续动画的连贯
window.onload=function(){
    // 设置一个延时函数来让第二个字幕出现
    let time=setTimeout(function(){
        txt_1.classList.add('textMove');
    },1500)
    pt_1.classList.add('textMove');
}
// 获取需要做类添加的白色分割线的DOM元素
const change1_2=document.querySelector('.change1_2')
// 对第二个字幕的动画添加一个动画结束监听
txt_1.addEventListener('animationend',function(){
    // 设置一个延时函数，对分隔动画做一秒的延迟
    time=setTimeout(function(){
        pt_1.classList.remove('textMove');
        txt_1.classList.remove('textMove');
        change1_2.classList.add('change1_2Move');
    },500)
})

// 黑色幕布拉开效果
// 获取需要做类添加的黑色背景的DOM元素
const scene_1LeftBack=document.querySelector('.scene_1 .left');
const scene_1RightBack=document.querySelector('.scene_1 .right');
// 对分隔动画添加一个动画结束监听
change1_2.addEventListener('animationend',function(){
    scene_1LeftBack.classList.add('scene_1LeftBack_move');
    scene_1RightBack.classList.add('scene_1RightBack_move');
})

// 文字缓缓出现效果
// 获取第一个场景中文字的遮罩层
// 获取文字的DOM元素
const scene_1Texts=document.querySelectorAll('.scene_1TextBox .text')
const scene_1Backs=document.querySelectorAll('.scene_1TextBox .text .back');
// 对每一个元素进行类添加
// 对黑色背景变化动画添加一个动画结束监听
scene_1LeftBack.addEventListener('animationend',function(){
    // 对第一个文字遮罩层添加一个类
    scene_1Backs[0].classList.add('scene_1Textchange');
    let i=1;
    // 通过定时器对随后的几个文字遮罩层进行类添加
    let timer=setInterval(function(){
        scene_1Backs[i].classList.add('scene_1Textchange');
        i++;
        if(i>=scene_1Backs.length){
            clearInterval(timer);
        }
    },1000)
})




// 换场动画结束后
// 对最后一行文字添加一个动画结束监听
scene_1Backs[scene_1Backs.length-1].addEventListener('animationend',function(){
    // 动画结束的时候，半秒之后对换场动画进行操作
    // 半秒的时间来清空前面文字遮罩层
    for(let i=0;i<scene_1Backs.length;i++){
        scene_1Backs[i].style.display='none';
        scene_1Texts[i].style.display='none';
    }
    // 获取第一个场景的DOM元素
    const scene_1=document.querySelector('.scene_1');
    const scene_2=document.querySelector('.scene_2');
    upMove(scene_1,scene_2);
    
    time=setTimeout(function(){
        // 1秒的切换场景之后，发送请求去渲染对话框
        for(let i=1;i<3;i++){
            axios({
                url:`http://localhost:8080/game/content/${i}`,
                method:"GET"
            }).then(result =>{
                let {data:{resultData}}=result;
                console.log(resultData.form)
                if(resultData.form==0){
                    // 如果是yoursay
                    // 获取yoursay的DOM元素
                    document.querySelector('.sayBox .say .yoursay').innerHTML=`
                        ${resultData.content}
                    `;
                }
                else{
                    // 如果是mysay
                    // 获取mysay的DOM元素
                    let{contentArray}=resultData;
                    let j=0;
                    // console.log(contentArray[j])
                    // console.log(contentArray[++j])
                    document.querySelector('.sayBox .say .mysay').innerHTML=`
                        <div class="select">${contentArray[j]}</div>
                        <div class="select">${contentArray[++j]}</div>
                    `
                    
                }
            }).catch(error =>{
                console.log(error.msg);
            })
        }
        // 获取第四个场景中的文字框的DOM元素
        const scene_2_yoursay=document.querySelector('.scene_2 .yoursay');
        const scene_2_mysay=document.querySelector('.scene_2 .mysay');
        textDisplay(scene_2_yoursay,scene_2_mysay);
        scene_2_mysay.addEventListener('click',function(e){
            // e.stopPropagation();
            // 在这个文字出现的时候，清空前面的页面
            textDisappear(scene_2_yoursay,scene_2_mysay);       
        })  
    },1000)
    // time =setTimeout(function(){
    //     textDisplay(scene_2_yoursay,scene_2_mysay);
    //     scene_2_mysay.addEventListener('click',function(e){
    //         e.stopPropagation();
    //         // 在这个文字出现的时候，清空前面的页面
    //         document.querySelector('.scene_1').style.display='none';
    //         document.querySelector('.scene_1TextBox').style.display='none';
           
    //         textDisappear(scene_2_yoursay,scene_2_mysay);       
    //     })  
    // },500)
})


// 封装一个场景向上移动的函数
function upMove(scene1,scene2){
    scene1.style.transform='translateY(-100vh)';
    scene2.style.transform='translateY(-100vh)';
}
// 封装一个场景向右移动的函数
function rightMove(scene1,scene2){
    scene1.style.transform='translateX(100vh)';
    scene2.style.transform='translateX(100vh)';
}
// 封装一个场景向左移动的函数
function leftMove(scene1,scene2){
    scene1.style.transform='translateX(-100vh)';
    scene2.style.transform='translateX(-100vh)';
}
// 封装对话框动画出现函数
function textDisplay(yoursay,mysay){
    // 添加类让第一个文字框出现
    yoursay.classList.add('yoursayMove');
    // 对第一个文字框添加一个动画结束监听
    yoursay.addEventListener('animationend',function(){
        // 让第一个文字框定格在动画结束位置
        yoursay.style.top='-8vh';
        yoursay.style.opacity='1';
        // 移除第一个文字框的动画，方便下一个动画的出现
        yoursay.classList.remove('yoursayMove');
        // 第一个文字框的动画结束后，第二个文字框就出现
        mysay.classList.add('mysayMove');
        // 对第二个文字框添加一个动画结束监听
        mysay.addEventListener('animationend',function(){
            // 让第二个文字框定格在动画结束位置
            mysay.style.top='10vh';
            mysay.style.opacity='1';
            mysay.classList.remove('mysayMove');
        })
    })
}
// 对话框消失动画封装为一个函数
function textDisappear(yoursay,mysay){
    yoursay.classList.add('yoursaydisappear');
    yoursay.addEventListener('animationend',function(){
        mysay.classList.add('mysaydisappear');
    })
}