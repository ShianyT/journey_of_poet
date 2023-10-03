const ariseTips = document.querySelectorAll('.tips-arise')
const tips = document.querySelector('.col-txt')
for (let i = 0; i < ariseTips.length; i++) {
    ariseTips[i].addEventListener('mouseenter', function () {
        if (i == 0) tips.innerHTML = '面对江川山峦的一种感受'
        if (i == 1) tips.innerHTML = '律政片中经常会出现的场景'
        if (i == 2) tips.innerHTML = '现代医学的一种治疗方式'

    })
    ariseTips[i].addEventListener('mouseout', function () {
        tips.innerHTML = ''
    })
}