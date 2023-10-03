// 页面滚动部分
let timer = null
let flag = true
let n = 0
const body = document.querySelector('body')
const height = document.documentElement.clientHeight
const page = document.querySelectorAll('body>div')
function scroll(e) {
    flag = false
    // 防抖
    timer = setTimeout(function () {
        flag = true
    }, 500)
    if (e.wheelDelta > 0) {
        if (n == 0) return
        n--
    }
    else {
        if (n >= page.length - 1) return
        n++
    }
    window.scrollTo({
        top: n * height,
        behavior: "smooth"
    })
}
// 禁止鼠标事件
document.addEventListener('mousewheel', function (e) {
    e.preventDefault()
},
    { passive: false }
)
body.addEventListener('wheel', (e) => {
    if (flag) scroll(e)
})