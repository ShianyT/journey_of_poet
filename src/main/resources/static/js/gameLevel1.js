// 基础数据
const simpleData = [
    { bg: './0emoji/1_0part1.png' },
    { bg: './0emoji/1_1part1.png' },
    { bg: './0emoji/2_0part1.png' },
    { bg: './0emoji/2_1part1.png' },
]
// 卡片大小
const size = 70;
// 行
const rows = 6;
// 列
const cols = 6;
//2个就消除
const oneGroupCount = 2;
// 每个消除有6组
const group = 2;
// 总共6层
const layerCount = 3;
const cellHtml = [];
// 包含所有卡片的数组 Array.from() 静态方法从可迭代或类数组对象创建一个新的浅拷贝的数组实例
// flat()转成一维数组
const renderData = Array.from(new Array(group)).map(v => {
    return simpleData.map(v => ({ ...v }))
}).flat().sort()
// 第一步 绘制表格矩阵
for (let ly = layerCount - 1; ly >= 0; ly--) {
    for (let i = 0; i < rows; i++) {
        for (let j = 0; j < cols; j++) {
            // 设置偏移量 奇数层叠在一起
            let pyStep = (ly + 1) % 2 === 0 ? size / 2 : 0
            // 随机取出卡片放在表面 同时数组少一个
            let item = ((i + ly) % j == 1 && renderData.pop())
            if (item) {
                cellHtml.push(`<div class="item" onclick="move(this)" id="m${ly}-${i}-${j}"
                style="width:${size}px;height:${size}px;left:${size * j + pyStep}px;top:${size * i + pyStep}px;background-image:url(${item.bg || ''})"></div>`)
            }
        }
    }
}
const main = document.querySelector('.main')
const moveList = document.querySelector('.move-list')
// reverse从上往下放进父盒子
main.innerHTML = cellHtml.reverse().join('')
main.style.height = `${size * rows + size * 2}px`
main.style.width = `${size * cols}px`
moveList.style.height = `${size}px`
moveList.style.width = `${size * 6}px`

console.log(document.querySelectorAll('.item').length);

// 第二步 计算出被遮住的底牌 并且标注暗色
const checkDisabled = (items) => {
    (items || main.querySelectorAll('.item')).forEach((v, i) => {
        // 获取卡片的坐标（字符串分割并转为数字）
        const arr = v.id.substring(1).split('-').map(v => Number(v))
        // 判断是否是奇数层
        const isPy = (arr[0] + 1) % 2 === 0
        // 从该层的后几层开始
        for (let i = arr[0] + 1; i <= layerCount - 1; i++) {
            const isPyB = (i + 1) % 2 === 0
            // 都位于偶数层时
            if (isPy === isPyB) {
                const e1 = main.querySelector(`#m${i}-${arr[1]}-${arr[2]}`)
                if (e1) {
                    // 给不同层级的同一坐标的底牌加上遮盖
                    v.classList.add('disabled')
                    break
                }
            }
            // 位于偶数层 对比层处于奇数层时  
            else if (isPy && !isPyB) {
                // 出现交错行为，有4个格子会影响它是否被占用 把4个列出来
                if (![
                    `${i}-${arr[1]}-${arr[2]}`,
                    `${i}-${arr[1]}-${arr[2] + 1}`,
                    `${i}-${arr[1] + 1}-${arr[2]}`,
                    `${i}-${arr[1] + 1}-${arr[2] + 1}`
                ].every(k => {
                    return !main.querySelector('#m' + k)
                })) {
                    v.classList.add('disabled')
                    break;
                } else {
                    v.classList.remove('disabled')
                }
            }
            // 位于奇数层 对比层位于偶数层
            else if (!isPy && isPyB) {
                // 出现交错行为，有4个格子会影响它是否被占用 把4个列出来
                if (![
                    `${i}-${arr[1]}-${arr[2]}`,
                    `${i}-${arr[1]}-${arr[2] - 1}`,
                    `${i}-${arr[1] - 1}-${arr[2]}`,
                    `${i}-${arr[1] - 1}-${arr[2] - 1}`
                ].every(k => {
                    return !main.querySelector('#m' + k)
                })) {
                    v.classList.add('disabled')
                    break;
                } else {
                    v.classList.remove('disabled')
                }
            }
        }
    })
}
// 第三步：点击卡片进行消除计算

console.log(document.querySelectorAll('.item')[0].style.backgroundImage.substring(14, 15));
let canMove = true
// 判断前面的元素是否消除完毕
let deleteok = true
// 待消除元素数组
let findResult = []
// 获取剩余图像个数
const resNum = document.querySelectorAll('.res_num')
const move = (me) => {
    console.log(moveList.offsetLeft)
    let left = moveList.offsetLeft
    let top = moveList.offsetTop
    // 防抖 不能点击被禁用元素 也不能在前面元素未消除完毕时点击
    console.log(findResult.length);
    console.log(canMove);
    if (!canMove || me.className.indexOf('disabled') >= 0 || findResult.length > 0 || !deleteok) {
        return
    }
    canMove = false
    // 新加入的格子放在前一个的后面
    if (moveList.children.length > 0) {
        let e1 = moveList.children[moveList.children.length - 1]
        left = e1.offsetLeft + size
        top = e1.offsetTop
    }
    me.style.top = `${top}px`
    me.style.left = `${left}px`
    // 动画计数器
    me.transitionNamesCount = 0;
    // 监听动画结束
    me.ontransitionend = (e) => {
        // me.transitionNamesCount++
        // transition有两个动画left和top 当两个都执行了才移动
        // if (me.transitionNamesCount === 2) {
        moveEnd(me)
        canMove = true
        // }
        // }
    }
}
// 动画结束后的计算
const poetryTips = document.querySelectorAll('.poetry_tips')
const moveEnd = (me) => {
    console.log(findResult);
    me.ontransitionend = null
    me.setAttribute('onclick', '')
    // 把选择的元素放入下方盒子
    moveList.appendChild(me)
    if (moveList.children.length > 1) {
        // 判断是否与前一个选中的元素相匹配
        let findEle = moveList.children[moveList.children.length - 2]
        // 利用img路径 获取匹配的元素
        if (findEle.style.backgroundImage.substring(14, 15) === me.style.backgroundImage.substring(14, 15) && findEle.style.backgroundImage.substring(16, 17) !== me.style.backgroundImage.substring(16, 17)) {
            // 放入已匹配的数组中
            console.log(findResult);
            findResult.push(findEle)
            findResult.push(me)
            deleteok = false
            const tipImgPart = poetryTips[me.style.backgroundImage.substring(14, 15) - 1].querySelectorAll('.tips_img_part')
            const Resnum = parseInt(document.querySelectorAll('.res_num')[me.style.backgroundImage.substring(14, 15) - 1].innerHTML)
            document.querySelectorAll('.res_num')[me.style.backgroundImage.substring(14, 15) - 1].innerHTML = Resnum - 1
            for (let i = 0; i < tipImgPart.length; i++) {
                tipImgPart[i].style.backgroundImage = `url(./0emoji/${me.style.backgroundImage.substring(14, 15)}_${i}part1.png)`
            }
            // 匹配的就变小
            if (findResult.length === 2) {
                findResult.forEach(v => {
                    v.style.opacity = '.8'
                    v.style.transform = 'scale(.8)'
                })
                // 匹配的结合在一起
                var combind = setTimeout(() => {
                    findResult[1].style.left = findResult[0].offsetLeft
                }, 200)
                // 形成新的图
                var combindResult = setTimeout(() => {
                    moveList.removeChild(findResult[1])
                    // 换成结合后的意象
                    findResult[0].style.backgroundImage = `url(./0emoji/${findResult[0].style.backgroundImage.substring(14, 15)}_2part1.png)`
                }, 200)
                // 删除元素
                setTimeout(() => {
                    console.log("delete")
                    console.log(findResult);
                    findResult.forEach(v => {
                        v.ontransitionend = (e) => {
                            SucOrFail()
                            // 消除元素
                            moveList.removeChild(v);
                            deleteok = true;
                            [...moveList.children].forEach((v, i) => {
                                v.style.left = `${i * size + moveList.offsetLeft}px`
                            })
                        }
                        setTimeout(() => v.style.transform = 'scale(0)')
                    })
                    findResult = []

                }, 500)
            }
        }
    }
    checkDisabled()
}
checkDisabled()
function SucOrFail() {
    if (resNum[0].innerHTML == '0' && resNum[1].innerHTML == '0') {
        document.querySelector('.CombinedResult').style.display = 'block'
        document.querySelector('.game_mark').style.display = 'block'
        gameReward()
    }
}
var kitchen = "kitchen"
function gameReward() {
    axios({
        url: `http://localhost:8080/user/infos/reward/${kitchen}`,
        data: {
            type: kitchen,
        },
    }).then(result => {
        console.log(result);
        document.querySelector('.CombinedResult').innerHTML = `<span class="SucTxt">通关成功！</span>
        <span>${result.data.resultData}</span>
        <i class="iconfont icon-gemini"></i>
        <button class="SucConfirm"><a href="gameSelect.html">确定</a></button>`
    })
}

// 新手引导
const point1 = document.querySelector('#m2-1-2')
const point2 = document.querySelector('#m2-2-3')
let clickAll
var guideRise = function () {
    const gameMark = document.querySelector('.game_mark')
    const gameGuide = document.querySelector('.game_guide')
    const guideOk = document.querySelector('.guideOk')
    const guideSkip = document.querySelector('.guideskip')
    const guideInnerTxt = document.querySelectorAll('.guideinner_txt')
    const guideLight = document.querySelectorAll('.guideLight')
    const guideStart = document.querySelector('.guide_start')
    const guidedEnd = document.querySelector('.guide_end')
    const guideNext = document.querySelectorAll('.guideNext')
    const item = document.querySelectorAll('.item')
    guideStart.style.display = 'flex'
    gameMark.style.display = 'block'
    guideOk.addEventListener('click', () => {
        guideStart.style.display = 'none'
        guideLight[0].style.zIndex = '100'
        guideNext[0].style.display = 'block'
        // 所有元素不能点击
        item.forEach(v => v.style.pointerEvents = 'none')
        guideNext[0].style.pointerEvents = 'none'
        // 提示的图像可以点击
        point1.style.pointerEvents = ''
        point2.style.pointerEvents = ''
        // 指示标注
        point1.innerHTML = `<i class="iconfont icon-map-finger-full"></i>`
        point2.innerHTML = `<i class="iconfont icon-map-finger-full"></i>`
        clickAll = 0
    })
    // 判断用户是否已经点击两个提示的图像
    clickAll = 3
    point1.addEventListener('click', () => { clickAll++; guideClick(clickAll); point1.innerHTML = '' })
    point2.addEventListener('click', () => { clickAll++; guideClick(clickAll); point2.innerHTML = '' })
    // 点击完两个图像 才可进行接下来的提示
    var guideClick = function (clickAll) {
        if (clickAll == 2) {
            item.forEach(v => v.style.pointerEvents = '')
            guideNext[1].style.pointerEvents = ''
            moveList.style.border = '3px dashed #fff'
            guideNext[1].style.display = 'block'
            guideNext[0].style.display = 'none'
            for (let i = 1; i < guideNext.length; i++) {
                guideNext[i].addEventListener('click', () => {
                    console.log(i);
                    moveList.style.border = '3px dashed #000'
                    if (i < 3) guideLight[i].style.zIndex = '100'
                    if (i < 4) {
                        guideNext[i + 1].style.display = 'block'
                        guideLight[i - 1].style.zIndex = '0'
                    }
                    guideNext[i].style.display = 'none'
                    if (i == 4) gameMark.style.display = 'none'
                })
            }
        }
    }
    guideSkip.addEventListener('click', () => {
        gameMark.style.display = 'none'
        guideStart.style.display = 'none'
    })
}
guideRise();