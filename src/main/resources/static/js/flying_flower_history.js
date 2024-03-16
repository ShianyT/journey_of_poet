// 第一次进入这个历史记录的页面的时候，就发起一个请求，接后端的数据
var currentpage=1;
const left=document.querySelector('.left');
const rightBox=document.querySelector('.right .bodyBox');

// 从本地存储中拿到自己的用户id
const userid=localStorage.getItem('userid');
// 定义一个是否再次渲染的标志
var again=0;

// 定义一个储存渲染内容多少的变量，方便点击切换
let content_num=0;
// 进入页面调用的函数
async function entry(){
    await first_entry();
    await scroll_entry();
    
    // 对整一个left添加一个事件监听
    left.addEventListener('click',function(e){
        // 如果点击到的是想要的元素
        if(e.target.className=='historyBox'||e.target.className=='statu false'||e.target.className=='statu true'||e.target.className=='keyword'||e.target.className=='time'||e.target.className=='iconfont icon-xiangyoujiantou'){
            // 点击的时候，根据content_num的值进行一次对right中内容的删除
            for(let j=0;j<content_num;j++){
                rightBox.removeChild(rightBox.children[0]);
            }
            // 获取点击到的元素的自定义属性index，但是因为接口没有0，所以需要有+1
            let index=e.target.dataset.index;
            // index++;
            console.log(index)
            // 拿到发送给后端的页码
            let page=e.target.dataset.page;
            console.log(page)
            // 给后端发送数据并对右侧进行渲染
            axios({
                url:`http://localhost:8080/battles/history/${page}`,
                method:'GET',  
            }).then(result =>{
                console.log(result);
                const {data}=result;
                const {resultData}=data;
                console.log(resultData[index])
                // 拿到对方用户的头像
                let otheruserIcon;
                let userIcon;
                if(resultData[index].beforeUid!=userid){
                    otheruserIcon=resultData[index].beforeIcon;
                    userIcon=resultData[index].afterIcon;
                }
                else{
                    otheruserIcon=resultData[index].afterIcon;
                    userIcon=resultData[index].beforeIcon;
                }
                // 先对拿到的数据的长度进行一个判断，通过这个数据的长度进行一个for循环遍历
                poems=resultData[index].poetryBattleDetails;
                console.log(poems);
                // 定义一个数字作为原来父元素中的内容多少
                content_num=poems.length;
                for(let i=0;i<poems.length;i++){
                    const div=document.createElement('div')
                    // 如果数据中的Uid不是自己的用户id
                    if(poems[i].uid!=userid){
                        div.classList.add('otheruserBox');
                        if(poems[i].poemTitle==undefined||poems[i].poemTitle==null){
                            div.innerHTML=`
                                <div class="otheruser">
                                    <div class="picture">
                                        <img src="http://localhost:8080/imgs/thumbnail/icon/${otheruserIcon}">
                                    </div>
                                    <div class="poemBox">
                                        <div class="poem">${poems[i].poem}</div>
                                        <div class="poemname">
                                            <div class="iconfont icon-cha"></div>
                                        </div>
                                    </div>
                                </div>
                            `
                        }
                        else{
                            div.innerHTML=`
                                <div class="otheruser">
                                    <div class="picture">
                                        <img src="http://localhost:8080/imgs/thumbnail/icon/${otheruserIcon}">
                                    </div>
                                    <div class="poemBox">
                                        <div class="poem">${poems[i].poem}</div>
                                        <div class="poemname">《${poems[i].poemTitle}》</div>
                                    </div>
                                </div>
                            `
                        }
                        
                        rightBox.appendChild(div);
                    }
                    else{
                        div.classList.add('userBox');
                        if(poems[i].poemTitle==undefined||poems[i].poemTitle==null){
                            div.innerHTML=`
                            <div class="user">
                                <div class="poemBox">
                                    <div class="poem">${poems[i].poem}</div>
                                    <div class="poemname">
                                        <div class="iconfont icon-cha"></div>
                                    </div>
                                </div>
                                <div class="picture">
                                    <img src="http://localhost:8080/imgs/thumbnail/icon/${userIcon}">
                                </div>
                            </div>
                        `
                        }
                        else{
                            div.innerHTML=`
                            <div class="user">
                                <div class="poemBox">
                                    <div class="poem">${poems[i].poem}</div>
                                    <div class="poemname">《${poems[i].poemTitle}》</div>
                                </div>
                                <div class="picture">
                                    <img src="http://localhost:8080/imgs/thumbnail/icon/${userIcon}">
                                </div>
                            </div>
                        `
                        }
                        rightBox.appendChild(div);
                    }
                }
                rightBox.style.display='flex';
            })
        }
    })
}
entry();

// 定义一个刚进来就开始渲染的函数
async function first_entry(){
    await axios({
        url:`http://localhost:8080/battles/history/${currentpage}`,
        method:'GET',
    }).then(result =>{
        battle_history(result);
    })
}

// 定义一个渲染历史对战记录的函数
function battle_history(result){
    const{data}=result;
    const{resultData}=data;
    console.log(resultData);
    
    // 计算出索引值要添加的数值
    let number=(currentpage-1)*10;
    for(let i=0;i<resultData.length;i++){
        // 对获取到的时间进行切割
        let createime=resultData[i].createTime.replace(/T/i,' ');
        let time=createime.substr(0,19);
        div=document.createElement('div');
        div.classList.add('historyBox');
        div.setAttribute('data-index',`${number+i}`);
        div.setAttribute('data-page',`${currentpage}`);
        if(resultData[i].outcome==userid){
            div.innerHTML=`
                <div class="statu true" data-index="${i}" data-page="${currentpage}">胜</div>
                <div class="keyword" data-index="${i}" data-page="${currentpage}">${resultData[i].keyword}</div>
                <div class="time" data-index="${i}" data-page="${currentpage}">${time}</div>
                <div class="iconBox" data-index="${i}" data-page="${currentpage}">
                    <div class="iconfont icon-xiangyoujiantou" data-index="${i}" data-page="${currentpage}"></div>
                </div>
            `
        }
        else{
            div.innerHTML=`
                <div class="statu false" data-index="${i}" data-page="${currentpage}">败</div>
                <div class="keyword" data-index="${i}" data-page="${currentpage}">${resultData[i].keyword}</div>
                <div class="time" data-index="${i}" data-page="${currentpage}">${time}</div>
                <div class="iconBox" data-index="${i}" data-page="${currentpage}">
                    <div class="iconfont icon-xiangyoujiantou" data-index="${i}" data-page="${currentpage}"></div>
                </div>
            `
        }
        left.appendChild(div)
    }
    if(resultData.length==10){
        currentpage++;
    }
    else{
        again=1;
    }
}

// 定义一个函数
async function scroll_entry(){
    left.addEventListener('scroll',debounce(again_response,500));
}

async function again_response(){
    // 获取最后一个元素的DOM元素
    const historyBoxs=document.querySelectorAll('.box .left .historyBox');
    // 获取最后一个元素
    let last=historyBoxs[historyBoxs.length-1];
    
    // 获得最后一个元素自身的高度
    let last_height=last.clientHeight;
    // 获得最后一个元素到自身父元素的高度
    let last_clientheight=last.offsetTop;
    // 获得滑动的距离
    let scrollTop=left.scrollTop;
    // 获取父元素自身的高度
    let clientHeight=left.clientHeight;
    if(last_clientheight+last_height/2<clientHeight+scrollTop){
        if(again==0){
            //调用一次函数和后端拿数据
            await axios({
                url:`http://localhost:8080/battles/history/${currentpage}`,
                method:'GET',
            }).then(result =>{
                battle_history(result);
            })
        }
    }
}

//  手写一个防抖函数
function debounce(fn,t){
    let resonseTime;
    return function(){
        if(resonseTime) clearTimeout(resonseTime)
        resonseTime=setTimeout(function(){
            fn();
        },t)
    }
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