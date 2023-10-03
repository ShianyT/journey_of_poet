// 地图切换部分
const map_part = document.querySelector('.map-part')
let mtimer = null
let mflag = true
let m = 0
const mheight = map_part.clientHeight
const pic = document.querySelectorAll('.map-pic')
const dynasty = document.querySelectorAll('.dynasty_entry')
function arise(e) {
    mflag = false
    // 防抖
    mtimer = setTimeout(function () {
        mflag = true
    }, 500)
    if (e.wheelDelta > 0) {
        if (m == 0) return
        m--
    }
    else {
        if (m >= pic.length - 1) return
        m++
    }
    for (let i = 0; i < pic.length; i++) {
        pic[i].style.opacity = '0'
        pic[i].style.visibility = 'hidden'
    }
    pic[m].style.opacity = '1'
    pic[m].style.visibility = 'visible'
}
map_part.addEventListener('wheel', (e) => {
    // 阻止事件冒泡
    e.stopPropagation()
    if (mflag) arise(e)
    for (let i = 0; i < dynasty.length; i++) {
        dynasty[i].style.opacity = '0'
        dynasty[i].style.visibility = 'hidden'
    }
    dynasty[m].style.opacity = '1'
    dynasty[m].style.visibility = 'visible'
    { passive: false }
})

// 侧边栏词条
// 获取词条按钮，对按钮添加一个点击事件，点击之后词条就开始隐藏（收到页面中之外），同时按钮反向。（此时需要有一个flag去判断词条是隐藏了还是消失了）
const entryButtons = document.querySelectorAll('.button');
const btn = document.querySelectorAll('.btn-part')
// 获取词条的父元素
const entryBox = document.querySelector('.entryBox');
// 获取地图元素
const map = document.querySelector('.map-content')
// 获取点
const point = document.querySelectorAll('.point')
// 第一次进入页面的时候词条肯定是显示的，那么就先定义flag为1
flag = 1;
// 定义一个词条变化函数
function entryChange() {
    // 先判断此时词条是处于什么样的状态，flag为1表示显示，flag为0表示隐藏
    if (flag == 1) {
        entryBox.style.transform = 'translateX(100%)';
        map.style.width = '100%'
        point[m].style.left = "10%"
        for (let j = 0; j < entryButtons.length; j++) {
            entryButtons[j].style.transform = 'rotate(135deg)';
        }
        flag = 0;
    }
    else {
        entryBox.style.transform = 'translateX(0)';
        map.style.width = '75%'
        point[m].style.left = "0"
        for (let j = 0; j < entryButtons.length; j++) {
            entryButtons[j].style.transform = 'rotate(-45deg)';
        }
        flag = 1;
    }
}

// 对按钮都添加点击事件
for (let i = 0; i < btn.length; i++) {
    btn[i].addEventListener('click', entryChange);

}