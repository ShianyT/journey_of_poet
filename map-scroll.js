// 地图切换部分
const map_part = document.querySelector('.map-part')
let mtimer = null
let mflag = true
let m = 0
const mheight = map_part.clientHeight
const pic = document.querySelectorAll('.map-pic')
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
    e.stopPropagation();
    if (mflag) arise(e)
    { passive: false }
})