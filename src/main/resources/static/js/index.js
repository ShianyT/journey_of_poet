// 主页第一个板块的动画效果
// 首先是获取两座山，一条船和两只鸟的DOM元素
const index_mountain_left = document.querySelector('.poets_Journey_introduce .left');
const index_mountain_right = document.querySelector('.poets_Journey_introduce .right');
const index_boat = document.querySelector('.poets_Journey_introduce .boat');
const index_bird_after = document.querySelector('.poets_Journey_introduce .birdAfter');
const index_bird_before = document.querySelector('.poets_Journey_introduce .birdBefore');


// 通过类的添加来实现动画
window.onload = function () {
    index_boat.classList.add('index_boat');

    index_mountain_left.classList.add('index_mountain_left');
    index_mountain_right.classList.add('index_mountain_right');

    index_bird_before.classList.add('index_bird');

    index_bird_after.classList.add('index_bird');
}
// 获取第一板块中的文字的DOM元素
const index_poets_Journey_introduceTexts = document.querySelectorAll('.poets_Journey_introduce .textBox .text');

// 对船添加一个动画结束监听
index_boat.addEventListener('animationend', function () {
    let i = 1;
    index_poets_Journey_introduceTexts[0].style.opacity = '1';
    let index_time = setInterval(function () {
        index_poets_Journey_introduceTexts[i].style.opacity = '1';
        i++;
        if (i >= index_poets_Journey_introduceTexts.length) {
            clearInterval(index_time);
        }
    }, 500)
})
// 获取第一个板块导航栏中的两只鹤
const index_nav_bird_1 = document.querySelector('.nav .rightBird_1');
const index_nav_bird_2 = document.querySelector('.nav .rightBird_2');
console.log(index_nav_bird_2)
// 对其添加类来实现动画
index_nav_bird_1.classList.add('navBird_1');
index_nav_bird_2.classList.add('navBird_2');

// 第三板块切换卡片部分
var left = document.querySelector(".left_one");
var right = document.querySelector(".right_one");
var wrap = document.querySelector(".example_wrap");
var card = wrap.querySelectorAll(".post_card")[0];
const marginLeft = getComputedStyle(card, null).marginLeft.toString().slice(0, 2)
console.log(marginLeft);
const width = card.offsetWidth + marginLeft * 2;
console.log(width);
console.log(getComputedStyle(card, null).marginLeft);
console.log(card.clientWidth);
let num = 0;
console.log(num);
left.style.cursor = "not-allowed";
left.style.opacity = '.5'
left.addEventListener('click', function () {
    console.log("left:" + num);
    if (num == 0) {
        left.style.cursor = "not-allowed";
        left.style.opacity = '.5'
    }
    else {
        num--
        left.style.cursor = "pointer";
        left.style.opacity = '1'
        wrap.style.transform = 'translateX(-' + width * num + 'px)'
        console.log(width * num);
        if (num == 0) {
            left.style.cursor = "not-allowed";
            left.style.opacity = '.5'
        }
    }
})
right.addEventListener('click', function () {
    console.log(num);
    left.style.cursor = "pointer";
    left.style.opacity = '1'
    if (num == 5) {
        left.style.opacity = '.5'
        right.style.cursor = "not-allowed";
    }
    else {
        num++
        right.style.cursor = "pointer";
        wrap.style.transform = 'translateX(-' + width * num + 'px)'
        if (num == 5) {
            right.style.opacity = '.5'
            right.style.cursor = "not-allowed";
        }
    }
})

// 在进入主页的时候，发送一次请求给后端，获得自己的头像，昵称，用户id等信息储存在本地
axios({
    url:'http://localhost:8080/users/show',
    method:'GET',
}).then(result => {
    const {data}=result;
    const {resultData}=data;
    console.log(resultData);
    let userIcon=resultData.icon;
    let username=resultData.nickname;
    let userid=resultData.uid;

    
        // 对导航栏的用户头像进行一个渲染
        let user=document.querySelector('.user div');
        user.innerHTML=`
            <img src="http://localhost:8080/imgs/thumbnail/icon/${userIcon}">
        `
        user.classList.add('usericon');

    // 将获得到的数据储存到本地中
    localStorage.setItem('usericon',`http://localhost:8080/imgs/thumbnail/icon/${userIcon}`);
    localStorage.setItem('username',`${username}`);
    localStorage.setItem('userid',`${userid}`);
}).catch(error =>{
    console.log(error)
})