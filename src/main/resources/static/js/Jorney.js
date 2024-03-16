const scene = document.querySelector('.scene1')
const say = document.querySelectorAll('.say')
const eyeSwitch = document.querySelector('.switch')
const eye = document.querySelector('.eye')
// 鼠标移动会跟随
scene.addEventListener('mousemove', (e) => {
    // 定义参数
    const range = 20
    let horizontal = (e.clientX / parseInt(getComputedStyle(scene).width, 10)) * range - range / 2
    let vertical = (e.clientY / parseInt(getComputedStyle(scene).height, 10)) * range - range / 2
    // 调用函数，传入offset近大远小
    document.querySelector('.sce_bg').style.transform = calcFun(horizontal, vertical, 0)
    document.querySelector('.sce_poet').style.transform = calcFun(horizontal, vertical, 3)
    // 每个对话都会移动
    for (let i = 0; i < say.length; i++) {
        say[i].style.transform = calcFun(horizontal, vertical, 2)
    }
    document.querySelector('.sce_tree').style.transform = calcFun(horizontal, vertical, 4)
})
// 封装元素移动旋转的计算函数 输出transform值
function calcFun(horizontal, vertical, offset) {
    return `translate3d(${horizontal * offset}px,${vertical * offset}px,0) rotateX(${-vertical}deg) rotateY(${horizontal}deg)`
}
// 接口部分
// 定义场景序号
let k = 1;
// 判断一个场景触发多少次点击事件
let click_num = 0
// 新场景完善文本
function UseScene(k) {
    axios({
        url: `http://localhost:8080/plots/content/${k}`,
        data: {
            scene: k,
        }
    }).then(result => {
        console.log(result);
        const saydata = result.data.resultData
        // 有多少个对话，就有多少次点击
        click_num = saydata.length
        console.log(click_num);
        for (let i = 0; i < saydata.length; i++) {
            // 判断是否有旁白
            if (saydata[i].form == 0) {
                const NarraHtml = document.createElement('div')
                NarraHtml.classList.add('narration', 'say')
                NarraHtml.innerHTML = `<span
                    class="narration_txt">${saydata[i].contentArray[0]}</span>
                <div class="keep_tips">继续>></div>`
                document.querySelector('.poet').append(NarraHtml)
            }
            // 判断是否是选项
            else if (saydata[i].form == 2) {
                // 创建选项文本
                if (saydata[i].contentArray.length == 2) {
                    const SelectHtml = document.createElement('div')
                    SelectHtml.classList.add('select_part', 'mysay', 'say', 'saymove')
                    SelectHtml.innerHTML = `<span class="selecttips">你会怎么选择？</span>
                <div class="keep_tips selection_inner">
                    <div class="select">${saydata[i].contentArray[0]}</div>
                    <div class="select">${saydata[i].contentArray[1]}</div>
                </div>`
                    document.querySelector('.poet').append(SelectHtml)
                }
                // console.log("选项长度是" + saydata[i].contentArray.length);
                else if (saydata[i].contentArray.length == 3) {
                    const SelectHtml = document.createElement('div')
                    SelectHtml.classList.add('select_part', 'mysay', 'say', 'saymove')
                    SelectHtml.innerHTML = `<span class="selecttips">你会怎么选择？</span>
                <div class="keep_tips selection_inner">
                    <div class="select">${saydata[i].contentArray[0]}</div>
                    <div class="select">${saydata[i].contentArray[1]}</div>
                    <div class="select">${saydata[i].contentArray[2]}</div>
                </div>`
                    document.querySelector('.poet').append(SelectHtml)
                }
            }
            //判断是否有对话
            else if (saydata[i].form == 1) {
                const SayHtml = document.createElement('div')
                SayHtml.classList.add('yoursay', 'say', 'bubble', 'saymove')
                SayHtml.innerHTML = `
                <span>${saydata[i].contentArray[0]}</span >
                    <div class="keep_tips">继续>></div>`
                document.querySelector('.poet').append(SayHtml)
            }
        }
    })
}
// 对话消失和出现部分
// 封装函数让上一句消失，下一句出现
function textArise(eleDisappear, eleArise) {
    eleDisappear.parentNode.classList.add('saydisappear')
    // 对第一个文字框添加一个动画结束监听
    eleDisappear.parentNode.addEventListener('animationend', function () {
        // 第一个文字框的动画结束后，第二个文字框就出现
        eleArise.parentNode.classList.add('sayMove');
    })
}
// 从场景第一句话开始
let i = 0
UseScene(k)
scene.addEventListener('click', () => {
    console.log(document.querySelectorAll('.sce_part')[0]);
    // 如果是第一句就让它出现
    if (i == 0) document.querySelectorAll('.say')[0].classList.add('sayMove');
    else {
        // 调用函数 传入消失的对话（第一个参数），和出现的对话（第二个参数）
        textArise(document.querySelectorAll('.keep_tips')[i - 1], document.querySelectorAll('.keep_tips')[i])
    }
    // 再递增到下一句
    i++;
    console.log("第几句：" + i);
    console.log("click_num是" + click_num);
    // 一个场景点击完毕，跳转到下一个
    if (i > click_num) {
        // eyeSwitch.style.opacity = '.8'
        // eyeSwitch.style.visibility = 'visible'
        // eye.style.animationName = 'open'
        k++;
        console.log("第几个场景：" + k);
        i = 0
        // 看完的场景移掉
        document.querySelectorAll('.sce_part')[0].style.left = '100%'
        // var openeye = setTimeout(function () {
        //     eye.style.animationName = ''
        //     eyeSwitch.style.opacity = 0
        //     eyeSwitch.style.visibility = 'hidden'
        // })
        // 删除前一个场景
        document.querySelectorAll('.sce_part')[0].parentNode.removeChild(document.querySelectorAll('.sce_part')[0]);
        console.log(document.querySelectorAll('.sce_part')[0]);
        // 给新出现的场景接口渲染
        UseScene(k)
        document.querySelectorAll('.sce_part')[0].style.opacity = '1'
        document.querySelectorAll('.sce_part')[0].style.visibility = 'visible'
        // 创建下一个场景
        CreateEle(k + 1);
    }
})
// 创建元素 即创建下一个场景
function CreateEle(n) {
    console.log(n);
    const newEle = document.createElement('div')
    newEle.classList.add('sce_part')
    newEle.innerHTML = `<img class="sce_bg" src = "./images/${n}_1.png" alt = "bg">
                                    <div class="poet">
                                        <img class="sce_poet" src="./images/${n}_2.png" alt="poet"></div>
                                            <img class="sce_tree" src="./images/${n}_3.png" alt="tree">`
    document.querySelector('.scene1').append(newEle)
}

