const rankingDetail = document.querySelectorAll('.rankings_detail')
const nowRankingListInner = document.querySelector('.now_ranking_list_inner')
// console.log(rankingDetail);
for (let i = 0; i < rankingDetail.length; i++) {
    rankingDetail[i].addEventListener('mouseover', function () {

    })
}
let AriseFlag = true
function AriseBtn() {
    // 事件委托
    nowRankingListInner.addEventListener('mouseover', (e) => {
        console.log(AriseFlag);
        // 防抖
        var AriseflagTimer = setTimeout(() => {
            AriseFlag = false
        }, 500)
        const t = e.target
        if (t.className == 'rankings_detail') {
            if (AriseFlag) {
                rankingDetail.forEach(v => {
                    v.style.backgroundColor = 'transparent'
                    v.children[v.children.length - 1].style.height = '0'
                    v.children[v.children.length - 1].style.scale = '0'
                    v.children[v.children.length - 1].style.animationName = ''
                    v.children[v.children.length - 2].style.height = '0'
                    v.children[v.children.length - 2].style.scale = '0'
                    v.children[v.children.length - 2].style.animationName = ''
                })
                t.style.backgroundColor = '#fff'
                t.children[t.children.length - 1].style.height = '40px'
                t.children[t.children.length - 1].style.scale = '1'
                t.children[t.children.length - 1].style.animationName = 'rankingBtnHover'
                t.children[t.children.length - 2].style.height = '40px'
                t.children[t.children.length - 2].style.scale = '1'
                t.children[t.children.length - 2].style.animationName = 'rankingBtnHover'
            }
        }
        // t.ontransitionend = () => {
        AriseFlag = true
        clearTimeout(AriseflagTimer)
        // }
    })
}
AriseBtn();