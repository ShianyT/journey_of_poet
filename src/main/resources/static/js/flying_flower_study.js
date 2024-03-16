const inputBox=document.querySelector('.topInputBox input');
const tip_texts=document.querySelectorAll('.box .tip_text');
const allPoemBox=document.querySelector('.left .allPoemBox');
const poem=document.querySelector('.right .poem');
const explain=document.querySelector('.right .explain');
const appreciation=document.querySelector('.right .appreciation');

// 在进入这个页面的时候，首先需要去本地储存中拿到一个标志，标志：是否是第一次进入这个页面

let firstEntry=localStorage.getItem('firstEntry');

// 获取页面中的关键字按钮
const keyword_bt=document.querySelector('.keyword_bt');
// 获取页面中的关键字弹窗
const keyword_bt_content=document.querySelector('.keyword_content');
var down=0;
var color=0;
var render=0;
// 对关键字按钮添加一个点击事件
keyword_bt.addEventListener('click',function(){
    if(down==0){
        // 点击到按钮的时候，通过更改类名让箭头变化
        keyword_bt.classList.remove('up');
        keyword_bt.classList.add('down');
        // 同时让弹窗出现,通过类添加来实现
        keyword_bt_content.classList.add('display');
        // 让down增加，表示此时箭头向下
        if(render==0){
            // 接后端接口，渲染关键字
            axios({
                url:'http://localhost:8080/battles/keywords',
                method:'GET'
            }).then(result =>{
                console.log(result)
                const {data}=result;
                const {resultData}=data;
                for(let i=0;i<resultData.length;i++){
                    // 创建一个新的盒子
                    div=document.createElement('div');
                    div.classList.add('key');
                    div.innerHTML=`${resultData[i]}`;
                    // 在父盒子的最后添加
                    keyword_bt_content.appendChild(div);
                }
            })
            render=1;
        }
        down++;
    }
    else {
        // 点击到按钮的时候，通过更改类名让箭头变化
        keyword_bt.classList.remove('down');
        keyword_bt.classList.add('up');
        // 同时让弹窗消失，通过类的删除实现
        keyword_bt_content.classList.remove('display');
        // 让down减小，表示此时箭头向上
        down--;
    }
})

keyword_bt_content.addEventListener('click',function(e){
    // 阻止事件冒泡不要让点击到父元素
    e.stopPropagation();
    // 判断点击到的是不是里面的关键字
    if(e.target.className=='key'){
        // 先判断是不是第一次对页面中的元素添加颜色类
        if(color==0){
            // 判断在这之前是不是在搜索框中搜索过
            if(search!=0){
                // 获取父元素allPoemBox中的最后一个子元素
                const lastChild=allPoemBox.lastElementChild;
                // // 每进行一次搜索就对页面中的元素进行一次删除
                allPoemBox.removeChild(lastChild);
                // 重置search的值
                search=0;
            }
            e.target.classList.add('active');
            // 点击完之后，等个半秒让弹窗收起来
            setTimeout(function(){
                keyword_bt.click();
            },200)
            color++;
            // 点击的时候，获取此时点击到的元素中的文本值
            let keyword=e.target.innerText;
            key_search(keyword,tip_texts);
        }
        else
        {
            // 拿到最后一个子元素
            const last=allPoemBox.lastElementChild;
            // 先删除父元素中已有的搜索盒子
            allPoemBox.removeChild(last);
            // 如果点击到的是关键字就对那一个关键字添加类，即颜色
            // 先删除有的颜色类
            document.querySelector('.active').classList.remove('active');
            e.target.classList.add('active');
            // 点击完之后，等个半秒让弹窗收起来
            setTimeout(function(){
                keyword_bt.click();
            },200)
            // 点击的时候，获取此时点击到的元素中的文本值
            let keyword=e.target.innerText;
            key_search(keyword,tip_texts);
        }
    }
})
// 封装一个关键字搜索函数
function key_search(keyword,tip_texts){
    // 通过循环让提示框中的文字消失
    for(let i=0;i<tip_texts.length;i++){
        tip_texts[i].style.display='none';
    }
    // 创建一个搜索盒子
    div=document.createElement('div');
    div.classList.add('searchPoemBox');
    // 搜索盒子放到allPoemBox中
    allPoemBox.appendChild(div);
    // 获取搜索盒子的元素
    const searchBox=document.querySelector('.searchPoemBox');

    // 后面接一个学习接口的函数
    axios({
        url:`http://localhost:8080/battles/learn/${keyword}`,
        method:'GET',
    }).then(result =>{
        const {data}=result;
        const {resultData}=data;
        console.log(resultData)
        // 定义一个储存诗句id的数组
        let poem=[];
        // 定义一个for循环对左侧的东西进行渲染
        for(let i=0;i<resultData.length;i++){
            div=document.createElement('div');
            div.classList.add('poemBox');
            div.setAttribute('data-index',`${i}`);
            div.innerHTML=`
                <div class="poem" data-index="${i}">${resultData[i].poem}</div>
                <div class="number" data-index="${i}">使用次数：${resultData[i].count}</div>
            `
            searchBox.appendChild(div);
            poem[i]=resultData[i].poemId;
        }
        // 整个循环结束之后，将拿到数据的数组放到本地中
        sessionStorage.setItem('poemIds',`${poem}`);
        // 让左侧的元素盒子出现
        searchBox.style.display='flex';
    }).catch(error =>{
        console.log(error);
    })
}


// 获取三个提示的DOM元素
const left_tip=document.querySelector('.left_tip');
const right_tip=document.querySelector('.right_tip');
const all_tip=document.querySelector('.all_tip');
const mask=document.querySelector('.mask');

// 第一次进入这个页面
if(firstEntry==0){
   
    mask.classList.add('undisplay')
    left_tip.classList.add('change');
    left_tip.addEventListener('click',function(){
        left_tip.classList.add('display');
        right_tip.classList.add('change');
    })
    right_tip.addEventListener('click',function(){
        right_tip.classList.add('display');
        all_tip.classList.add('change');
    })
    all_tip.addEventListener('click',function(){
        all_tip.classList.add('display');
        mask.classList.remove('undisplay');
        mask.classList.add('display');
    })
    // 将标记值更改
    firstEntry=1;
    localStorage.setItem('firstEntry',`${firstEntry}`);
}
else {
    // 将所有提示框删除
    left_tip.classList.add('display');
    right_tip.classList.add('display');
    all_tip.classList.add('display');
    mask.classList.add('display');
}

// 定义一个转义函数，防止xss攻击
function escapeHtml(key){
    // 通过正则替换，将用户输入中的可能是标签的东西正常输出
    key=key.replace(/(<br[^>]*>| |\s*)/g, '')
            .replace(/\&/g,"&amp;")
            .replace(/\"/g,"&quot;")
            .replace(/\'/g,"&#39;")
            .replace(/\</g,"&lt;")
            .replace(/\>/g,"&gt;");
    return key;
}

// 定义一个搜索的标志致，0为第一次搜索
var search=0;
inputBox.addEventListener('keyup',function(e){
    if(e.key==='Enter'){
        if(inputBox.value.trim()!=''){
            
            let keyword=inputBox.value;

            // 在这里需要做一个转义函数
            keyword=escapeHtml(keyword);
            // 如果是第一次搜索
            if(search==0){
                // 如果关键字中的颜色标记值不为0
                if(color!=0){
                    // 获取父元素allPoemBox中的最后一个子元素
                    const lastChild=allPoemBox.lastElementChild;
                    // // 每进行一次搜索就对页面中的元素进行一次删除
                    allPoemBox.removeChild(lastChild);
                    // 删除完成之后，对关键字中的颜色类进行一次删除
                    document.querySelector('.active').classList.remove('active');
                    // 删除之后，重置一次color的值
                    color=0;
                }
                // 创建一个搜索盒子
                div=document.createElement('div');
                div.classList.add('searchPoemBox');
                // 搜索盒子放到allPoemBox中
                allPoemBox.appendChild(div);
                // 获取搜索盒子的元素
                const searchBox=document.querySelector('.searchPoemBox');
                // 不相同，发起请求，相同不做处理
                poem_search(tip_texts,keyword,searchBox);
            }
            // 如果不是第一次搜索
            else{
                // 从本地中拿到上一次输入的关键字
                let oldkeyword=sessionStorage.getItem('keyword');
                // 判断新输入的关键字和老关键字是否相同
                if(oldkeyword!=keyword){
                    // 获取父元素allPoemBox中的最后一个子元素
                    const lastChild=allPoemBox.lastElementChild;
                    // // 每进行一次搜索就对页面中的元素进行一次删除
                    allPoemBox.removeChild(lastChild)
                    // 创建一个搜索盒子
                    div=document.createElement('div');
                    div.classList.add('searchPoemBox');
                    // 搜索盒子放到allPoemBox中
                    allPoemBox.appendChild(div);
                    // 获取搜索盒子的元素
                    const searchBox=document.querySelector('.searchPoemBox');
                    // 不相同，发起请求，相同不做处理
                    poem_search(tip_texts,keyword,searchBox);
                }
            }
            
        }
    }
})

// 定义一个搜索函数
async function poem_search(tip_texts,keyword,searchBox){
        // 通过循环让提示框中的文字消失
        for(let i=0;i<tip_texts.length;i++){
            tip_texts[i].style.display='none';
        }
        
        // 如果表单输入的不是关键字搜索
        
            let page=1;
            // 将页码数转化为数字
            let currentPage=Number(page);
            axios({
                url:`http://localhost:8080/battles/search`,
                method:'POST',
                // 直接传数据不转JSON
                params:{
                    keywords:keyword,
                    currentPage,
                },
            }).then(result =>{
                const {data}=result;
                const {resultData}=data;
                console.log(resultData)
                // 定义一个储存诗句id的数组
                let poem=[];
                // 定义一个for循环对左侧的东西进行渲染
                for(let i=0;i<resultData.length;i++){
                    div=document.createElement('div');
                    div.classList.add('poemBox');
                    div.setAttribute('data-index',`${i}`);
                    div.innerHTML=`
                        <div class="poem" data-index="${i}">${resultData[i].name}&nbsp;&nbsp;${resultData[i].dynasty}&nbsp;${resultData[i].poet}</div>
                        <div class="block" data-index="${i}"></div>
                    `
                    searchBox.appendChild(div);
                    poem[i]=resultData[i].id;
                }
                // 整个循环结束之后，将拿到数据的数组放到本地中
                sessionStorage.setItem('poemIds',`${poem}`);
                // 让左侧的元素盒子出现
                searchBox.style.display='flex';
                // 整个页面渲染完成之后，将该次输入的内容储存到本地
                sessionStorage.setItem('keyword',`${keyword}`);
                // 同时将搜索的标志值更改为1
                search=1;
            }).catch(error =>{
                console.log(error);
            })
}


// 对左侧的搜索出来的父盒子做一个事件委托处理
allPoemBox.addEventListener('click',function(e){
    // 判断点击到的是什么子元素
    if(e.target.className=='poemBox'||e.target.className=='poem'||e.target.className=='number'){
        // 获取点击到的子元素的自定义属性，方便从本地数组中拿到具体的诗词的id
        let index=e.target.dataset.index;
        // 从本地中拿到诗词id数组
        let poems=sessionStorage.getItem('poemIds');
        console.log(poems);
        // 将拿到的数组字符串切割成数组
        let poemid=poems.split(',');
        // 发送给后端的数据
        let id=poemid[index];
        console.log(id);
        axios({
            url:`http://localhost:8080/battles/poem/${id}`,
            method:'GET',
        }).then(result =>{
            console.log(result);
            const {data}=result;
            const {resultData}=data;
            // 对右侧中的诗词详情进行渲染
            poem.innerHTML=`
                <div class="poem_title">${resultData.name}</div>
                <div class="poet">${resultData.poet}</div>
                <div class="poem_content">${resultData.content}</div>
            `
            // 判断拿到的数据是否为空
            if(resultData.jsonOther.译文及注释!=null){
                // 对右侧诗词的诗词的注释进行渲染
                explain.innerHTML=`
                    <div class="explain_title">译文及注释</div>
                    <div class="explain_content">${resultData.jsonOther.译文及注释}</div>
                `
            }
            else{
                explain.innerHTML=`
                    <div class="explain_title">译文及注释</div>
                    <div class="explain_content">暂无译文及注释</div>
                `
            }
            // 判断拿到的数据是否为空
            if(resultData.jsonOther.赏析!=null||resultData.jsonOther.简析!=null||resultData.jsonOther.鉴赏!=null){
                if(resultData.jsonOther.赏析!=null){
                    // 对右侧中的诗词的赏析进行渲染
                    appreciation.innerHTML=`
                        <div class="appreciation_title">解析</div>
                        <div class="appreciation_content">${resultData.jsonOther.赏析}</div>
                    `
                }
                else{
                    // 如果赏析是null，那就判断一下有没有下位替代
                    if(resultData.jsonOther.鉴赏!=null){
                        // 对右侧中的诗词的赏析进行渲染
                        appreciation.innerHTML=`
                            <div class="appreciation_title">解析</div>
                            <div class="appreciation_content">${resultData.jsonOther.鉴赏}</div>
                        `
                    }
                    else {
                        // 如果鉴赏为null，再次判断一下有没有下位替代
                        if(resultData.jsonOther.简析!=null){
                            // 对右侧中的诗词的赏析进行渲染
                            appreciation.innerHTML=`
                                <div class="appreciation_title">解析</div>
                                <div class="appreciation_content">${resultData.jsonOther.简析}</div>
                            `
                        }
                    }
                }
            }
            else{
                appreciation.innerHTML=`
                    <div class="appreciation_title">解析</div>
                    <div class="appreciation_content">暂无解析</div>
                `
            }
            poem.style.display='flex';
            explain.style.display='flex';
            appreciation.style.display='flex';
        }).catch(error =>{
            console.log(error)
        })
    }
})