const ariseTips = document.querySelectorAll('.tips-arise')
const tt = document.querySelectorAll('.tt')
const ctips = document.querySelector('.col-txt')
const rtips = document.querySelector('.row-txt')
// 出现纵横提示
for (let i = 0; i < ariseTips.length; i++) {
    ariseTips[i].addEventListener('mouseenter', function () {
        if (i == 0) ctips.innerHTML = '面对江川山峦的一种感受'
        if (i == 1) ctips.innerHTML = '律政片中经常会出现的场景'
        if (i == 2) rtips.innerHTML = '现代医学的一种治疗方式'

    })
    ariseTips[i].addEventListener('mouseout', function () {
        tt[0].innerHTML = ''
        tt[1].innerHTML = ''

    })
}
//填字
// 用户点击右侧选择的字，该字底色变化
let cflag = 0
let windex
const choice = document.querySelectorAll('.choice')
for (let i = 0; i < choice.length; i++) {
    // 添加属性 有无选中过 有则在后续填字中为白色
    choice[i].setAttribute('choose', '0')
    choice[i].addEventListener('click', function () {
        // 排他法 只能选中一个
        for (let j = 0; j < choice.length; j++) {
            if (choice[j].getAttribute('choose') === '1') {
                choice[j].style.backgroundColor = '#d9d9d9'
                choice[j].style.color = '#000'
                choice[j].setAttribute('choose', '0')
            }
        }
        if (this.getAttribute('choose') == '0') {
            this.setAttribute('choose', '1')
            this.style.backgroundColor = '#3e586e'
            this.style.color = '#fff'
            // 确定是否有选中字
            cflag = 1
            // 获取字的下标
            windex = i
        }
    })
}
// 填入格中
const fill = document.querySelectorAll('.fill')
for (let j = 0; j < fill.length; j++) {
    fill[j].addEventListener('click', function () {
        // 判断有无选中字
        console.log(cflag);
        if (cflag == 1) {
            fill[j].innerHTML = choice[windex].innerHTML
            // choose[index].
            // windex = null
            cflag = 0
            choice[windex].style.backgroundColor = '#d9d9d9'
            choice[windex].setAttribute('choose', 0)
        }
    })
}
// 选错后想把字放回去（暂未实现）
// 判断全部正确就通关
let result = 1
const succAlert = document.querySelector('.succ-alert')
const success = document.querySelector('.success')
const fail = document.querySelector('.fail')
const confirm = document.querySelector('.confirm-btn')
const failBtn = document.querySelector('.fail-btn')
const ansArr = ['心', '地', '自', '然', '见', '山', '咨', '悠', '叉', '问', '采', '何', '东', '篱', '尔']
function TorF() {
    console.log('ok');
    for (let i = 0; i < fill.length; i++) {
        if (fill[i].innerHTML != ansArr[i]) {
            for (let j = 0; j < fill.length; j++) {
                fill[j].innerHTML = ''
                fail.style.opacity = '1'
                fail.style.visibility = 'visible'
                succAlert.style.opacity = '.2'
                succAlert.style.visibility = 'visible'
            }
            return result = 0
        }
    }
    if (result) {
        succAlert.style.opacity = '.2'
        succAlert.style.visibility = 'visible'
        success.style.opacity = '1'
        success.style.visibility = 'visible'
    }
}
// 确定后调用TorF函数判断
confirm.addEventListener('click', TorF)
// 失败后确定重来
failBtn.addEventListener('click', () => {
    fail.style.opacity = '0'
    fail.style.visibility = 'hidden'
    succAlert.style.opacity = '0'
    succAlert.style.visibility = 'hidden'
    for (let i = 0; i < choice.length; i++) {
        choice[i].style.color = '#000'
    }
})