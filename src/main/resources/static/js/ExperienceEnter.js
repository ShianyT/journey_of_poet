const PlotScrollBtn = document.querySelector('.plot_scroll_btn')
const PlotScrollTrack = document.querySelector('.plot_scroll_track')
const PlotInner = document.querySelector('.plotinner')
const PlotVisibleInner = document.querySelector('.plot_visible_inner')
const step = (PlotInner.getBoundingClientRect().width - PlotVisibleInner.getBoundingClientRect().width) / 240
// 滚动条按下会跳到指定位置
PlotScrollTrack.addEventListener('mousedown', (e) => {
    console.log("触发down");
    var width1 = PlotScrollTrack.getBoundingClientRect().width - (e.clientX - PlotScrollTrack.getBoundingClientRect().left) - PlotScrollBtn.getBoundingClientRect().width / 2;
    console.log("width1:" + width1);
    if (width1 < 0) {
        width1 = 0;
    } else if (width1 > PlotScrollTrack.getBoundingClientRect().width) {
        width1 = PlotScrollTrack.getBoundingClientRect().width;
    }
    PlotScrollBtn.style.right = width1 + 'px';
    console.log("PlotScrollBtn" + width1);
    PlotInner.style.right = "-" + width1 * step + "px";
})
let Btnmove
// 按钮按下
function handleMouseDown(event) {
    Btnmove = event.clientX - PlotScrollBtn.getBoundingClientRect().left;
    event.preventDefault()
    PlotScrollBtn.addEventListener('mousemove', handleMouseMove);
}
// 防止出界
function arrived() {
    console.log("right:" + PlotScrollBtn.getBoundingClientRect().right);
    console.log("length:" + PlotScrollBtn.style.right.length);
    console.log("realright:" + PlotScrollBtn.style.right.substring(0, PlotScrollBtn.style.right.length - 2));
    console.log("firstRI:" + PlotScrollTrack.getBoundingClientRect().right);
    console.log("PlotInner:" + PlotInner.style.right);
    if (PlotScrollBtn.style.right.substring(0, PlotScrollBtn.style.right.length - 2) <= 0) {
        PlotScrollBtn.style.right = '0px'
        console.log("arrive:");
    }
    if (PlotScrollBtn.offsetLeft <= 0) {
        console.log(" PlotScrollTrack.width " + PlotScrollTrack.getBoundingClientRect().width);
        console.log("btn Width" + PlotScrollBtn.getBoundingClientRect().width);
        PlotScrollBtn.style.right = PlotScrollTrack.getBoundingClientRect().width - PlotScrollBtn.getBoundingClientRect().width + 'px'
        console.log("左边" + PlotScrollBtn.style.right);
    }
    if (PlotInner.offsetLeft >= 0) {
        PlotInner.style.right = "-" + (PlotInner.getBoundingClientRect().width - PlotVisibleInner.getBoundingClientRect().width) + 'px'
        console.log("inner right: " + PlotInner.style.right);
    }
    if (PlotInner.style.right.substring(0, PlotInner.style.right.length - 2) >= 0) {
        PlotInner.style.right = '0px'
    }
}
// 鼠标拖动
function handleMouseMove(e) {
    console.log("触发move");

    // 取消过渡效果
    PlotInner.style.transition = "all 0s";
    PlotScrollBtn.style.transition = 'all 0s';
    var width2 = PlotScrollTrack.getBoundingClientRect().width - (e.clientX - PlotScrollTrack.getBoundingClientRect().left) - PlotScrollBtn.getBoundingClientRect().width / 2;  //鼠标在滚动条内的横坐标
    // var width2 =
    console.log("rightWIdth2" + width2);
    PlotScrollBtn.style.right = width2 + "px"; //更改滑块的right值，实现滑块跟着鼠标移动
    PlotInner.style.right = "-" + width2 * step + "px";
    arrived()
}
// 鼠标松开
function handleMouseUp(e) {
    console.log("触发up");

    PlotScrollBtn.removeEventListener('mousemove', handleMouseMove);
    //恢复过渡效果
    PlotScrollBtn.style.transition = 'all .1s';
    arrived()
}
// 鼠标点击
function handleClick(event) {
    console.log("触发click");
    const clickX = event.clientX;  //获取鼠标点击的位置
    const distance = PlotScrollTrack.getBoundingClientRect().width - (clickX - PlotScrollTrack.getBoundingClientRect().left);  //计算点击的位置在最左距离
    const gap = PlotScrollTrack.offsetWidth / 10
    const count = Math.floor(distance / gap)
    console.log("count:" + count);
    console.log(PlotScrollBtn.offsetWidth * count);
    PlotScrollBtn.style.right = PlotScrollBtn.offsetWidth * count + 'px'
    setTimeout(arrived, 100)
}

PlotScrollBtn.addEventListener('mousedown', handleMouseDown);
PlotScrollBtn.addEventListener('mouseup', handleMouseUp);
PlotScrollTrack.addEventListener('mouseup', handleMouseUp)

// -----接口-----
const unlock = document.querySelector('.icon-jiesuo')
const unlockedbtn = document.querySelector('.unlocked')
const lockedbtn = document.querySelector('.locked')
const unlockedTips = document.querySelector('.unlockedTips')
const enterMark = document.querySelector('.enter_mark')
// 渲染事件轴
const detailPlot = document.querySelector('.detail_plot')
function Fillaxis() {
    axios({
        url: `http://localhost:8080/plots/event`,
    }).then(result => {
        const eventArray = result.data.resultData
        for (let i = 0; i < eventArray.length; i++) {
            detailPlot.insertAdjacentHTML("beforeend",
                `<div class="plot_detail_part" id="${eventArray[i].id}">
            <a onclick="goTo(${eventArray[i].id})" class="plot_detail_inner">${eventArray[i].title}</a>
        </div>`)
        }
        chapterlight()
    })
}
Fillaxis()
// 初始 事件轴点亮经过的事件

// 点击解锁事件
unlock.addEventListener('click', () => {
    unlockedTips.style.display = 'block'
    enterMark.style.display = 'block'
})
// 第4章
let chapterId = 4
// 取消解锁
lockedbtn.addEventListener('click', () => {
    unlockedTips.style.display = 'none'
    enterMark.style.display = 'none'
})
// 确定解锁 判断交子个数
unlockedbtn.addEventListener('click', () => {
    axios({
        url: `http://localhost:8080/plots/unlock/${chapterId}`,
        data: {
            chapterId,
        },
    }).then(result => {
        console.log(result);
        if (result.data.success) {
            unlockedTips.style.display = 'none'
            enterMark.style.display = 'none'
            unlock.style.display = 'none'
            document.querySelector('.plot_main_part').style.opacity = '1'
            document.querySelector('.plot_main_part').style.pointerEvents = 'all'
            document.querySelectorAll('.plot_detail_part')[0].style.opacity = '1'
            document.querySelectorAll('.plot_detail_part')[0].style.pointerEvents = 'all'
            // chapterlight()
        }
        else{
            unlockedTips.style.display = 'none'
            document.querySelector('.failTips').style.display = 'block'
            document.querySelector('.failOk').addEventListener('click', () => {
                document.querySelector('.failTips').style.display = 'none'
                enterMark.style.display = 'none'
            })
        }
    })
})
// function plotHighlight() {
//     axios({
//         url: `http://localhost:8080/plots/progress`,
//     }).then(result => {
//         console.log(result);
//         if (result.success) {
//             unlockedTips.style.display = 'none'
//             enterMark.style.display = 'none'
//             unlock.style.display = 'none'
//             document.querySelector('.plot_main_part').style.opacity = '1'
//             document.querySelector('.plot_main_part').style.pointerEvents = 'all'
//             document.querySelectorAll('.plot_detail_part')[0].style.opacity = '1'
//             document.querySelectorAll('.plot_detail_part')[0].style.pointerEvents = 'all'
//             document.querySelectorAll('.plot_detail_part').forEach(v => {
//                 if (v.id <= result.data.resultData.id) {
//                     v.style.opacity = '1'
//                     v.style.pointerEvents = 'all'
//                 }
//             })
//         }
//     })
// }
// 

function chapterlight() {
    // 点亮事件函数
    axios({
        url: `http://localhost:8080/plots/progress`,
    }).then(result => {
        console.log(result);
        if (result.data.resultData == undefined) { return; }
        // if (result.data.success) {
        unlockedTips.style.display = 'none'
        enterMark.style.display = 'none'
        unlock.style.display = 'none'
        document.querySelector('.plot_main_part').style.opacity = '1'
        document.querySelector('.plot_main_part').style.pointerEvents = 'all'
        document.querySelectorAll('.plot_detail_part')[0].style.opacity = '1'
        document.querySelectorAll('.plot_detail_part')[0].style.pointerEvents = 'all'
        console.log(result.data.resultData);
        let id = result.data.resultData.id
        document.querySelectorAll('.plot_detail_part').forEach(v => {
            if (v.id <= id) {
                v.style.opacity = '1'
                v.style.pointerEvents = 'all'
            }
        })
        if (id > 4) { document.querySelector('.kitchen').style.display = 'block' }
        if (result.data.resultData !== undefined) {
            let id = result.data.resultData.id
            document.querySelectorAll('.plot_detail_part').forEach(v => {
                if (v.id <= id) {
                    v.style.opacity = '1'
                    v.style.pointerEvents = 'all'
                }
            })
        }
    }
    )

}
// 跳转到点击的事件
function goTo(id) {
    window.location.href = "Jorney.html?id=" + id;
}
// 获取页面中交子货币需要改变的地方
const money_number=document.querySelector('.money_numberBox .money_number');
// 向后端发送请求
axios({
    url:'http://localhost:8080/user/infos',
    method:'GET'
}).then(result =>{
    console.log(result);
    const {data}=result;
    const {resultData}=data;
    money_number.innerHTML=`${resultData.money}`;
})