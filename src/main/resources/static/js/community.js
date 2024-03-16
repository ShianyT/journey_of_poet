// let index=1;
// 定义一个判断页面渲染了多少个帖子的变量
var post_number=1;
// 页面打开的时候向后端获取数据对页面进行渲染
window.onload=Render();
async function Render(){
    await axios({
        url:'http://localhost:8080/users/show',
        method:'GET',
    }).then(result =>{
        // 先获取页面中需要改变的DOM元素
        const community_user_profile=document.querySelector('.community_Page .aside_me img');
        const community_user_nickname=document.querySelector('.community_Page .aside_me_name');
        console.log(result);
        const {data}=result;
        const {resultData}=data;
        community_user_profile.src=`http://localhost:8080/imgs/thumbnail/icon/${resultData.icon}`;
        community_user_nickname.innerHTML=`${resultData.nickname}`;
    })
    await axios({
        url:`http://localhost:8080/posts/hot/${post_number}`,
        method:'GET',
    }).then(response =>{
        // 获取要在里面进行渲染的DOM元素
        const inner=document.querySelector('.post_wrapper .post_inner:nth-child(1)');
        const {data}=response;
        console.log(response)
        const {resultData}=data;
        community_rending(inner,resultData);
        console.log(post_number);
    })
}
// 自定义一个渲染页面函数
async function community_rending(domFather,result){
    // 初始化一个数组
    let arrid=[];
    for(let i=0;i<result.length;i++){
        div=document.createElement('div');
        div.classList.add('post_card');
        // 对div添加一个自定义属性，方便帖子详情页的渲染
        div.setAttribute('data-index',`${i}`);
        const str=result[i].images.split(',');

        // 添加两个判断，点赞和收藏是否处于点亮状态
        let like_class='icon-dianzan';
        let collect_class='icon-shoucang';
        if(result[i].isLike==true){
            // 如果是点赞状态
            like_class='icon-dianzan_kuai';
        }
        
        if(result[i].isCollection==true){
            // 如果是收藏状态
            collect_class='icon-xingxing';
        }

        div.innerHTML=`
            <div class="post_info" data-index="${i}">
                <div class="post_person" data-index="${i}">
                    <div class="post_person_avatar" data-index="${i}">
                        <img src="http://localhost:8080/imgs/thumbnail/icon/${result[i].icon}" alt="" data-index="${i}">
                    </div>
                    <div class="post_person_name" data-index="${i}">${result[i].nickname}</div>
                </div>
                <div class="post_txt" data-index="${i}">${result[i].title}</div>
            </div>
            <div class="post_bgimg" data-index="${i}">
                <img src="http://localhost:8080/imgs/thumbnail/post/${str[0]}" alt="" data-index="${i}">
            </div>
            <div class="post_data" data-index="${i}" data-isLike="${result[i].isLike}">
                <div class="post_icon" data-index="${i}" data-isLike="${result[i].isLike}">
                    <i class="iconfont ${like_class}" data-index="${i}" data-isLike="${result[i].isLike}"></i>
                    <span class="post_like" data-index="${i}" data-isLike="${result[i].isLike}">${result[i].likes}</span>
                </div>
                <div class="post_icon" data-index="${i}" data-isCollection="${result[i].isCollection}">
                    <i class="iconfont ${collect_class}" data-index="${i}" data-isCollection="${result[i].isCollection}"></i>
                    <span class="post_comment" data-index="${i}" data-isCollection="${result[i].isCollection}">${result[i].collections}</span>
                </div>
            </div>
        `
        domFather.appendChild(div);
        // 将帖子的id存储到本地存储中去
        arrid[i]=result[i].id;
        // 判断是否达到20条
        if(i==20){
            post_number++;
        }
    }
    
    // 将数组存储到本地储存中去
    sessionStorage.setItem('post_ids',`${arrid}`);
}
// 对页面中的帖子添加一个点击事件
const post_inner=document.querySelector('.community_Page .post_part .post_wrapper .post_inner');
console.log(post_inner);
post_inner.addEventListener('click',function(e){
    e.stopPropagation();
    // 从本地存储中拿到帖子id的数组
    let post_ids=sessionStorage.getItem('post_ids');
    let post_ids_arr=post_ids.split(',');

    let index=e.target.dataset.index;
    // 通过点击获得的自定义属性，来获取帖子数组中的帖子id
    let id=post_ids_arr[index];
    // 将获得到的帖子id存储到本地存储中去
    localStorage.setItem('post_id',`${id}`);
    // 将点击到的自定义属性存储到本地中
    localStorage.setItem('card_id',`${index}`);
    // 此时是由推荐模块进入帖子，statu为0
    localStorage.setItem('statu','0');
    
    if(e.target.className=='iconfont icon-dianzan'||e.target.className=='post_like'||e.target.className=='iconfont icon-dianzan_kuai'){
        e.stopPropagation();
        // 获取自定义属性中的点赞标志
        let isLike=e.target.dataset.islike;
        // 如果点赞的状态是true，那么再次点击就会取消点赞
        console.log(isLike)
        let status;
        if(isLike=="true"){
            status=0;
        }
        else{
            status=1;
        }
        console.log(isLike)
        console.log(id)
        console.log(status)
        axios({
            url:`http://localhost:8080/posts/likes`,
            method:'POST',
            data:{
                postId:id,
                status
            },
        }).then(result => {
            const {data}=result;
            if(data.success==true){
                axios({
                    url:`http://localhost:8080/posts/hot/1`,
                    method:'GET',
                }).then(result =>{
                    const {data}=result;
                    const {resultData}=data;
                    // 获得拿到的id对应的收藏数
                    const number=resultData[index].likes;
                    if(isLike=="false"){
                        // 没有点赞
                        // 再获取页面中的对应的需要改变的点赞盒子
                        if(e.target.className=='iconfont icon-dianzan'){
                            e.target.classList.remove('icon-dianzan');
                            // 将点击到的元素中的类名变更
                            e.target.classList.add('icon-dianzan_kuai');
                            e.target.nextElementSibling.innerHTML=`
                                ${number}
                            `
                            // 同时需要去改变他的自定义属性
                            e.target.setAttribute("data-isLike","true")
                        }
                        else {
                            e.target.classList.remove('icon-dianzan');
                            e.target.classList.add('icon-dianzan_kuai');
                            e.target.innerHTML=`
                                ${number}
                            `
                             // 同时需要去改变他的自定义属性
                             e.target.setAttribute("data-isLike","true")
                        }
                    }
                    else{
                        // 有点赞
                        // 再获取页面中的对应的需要改变的点赞盒子
                        console.log(e.target)

                        if(e.target.className=='iconfont icon-dianzan_kuai'){
                            console.log(e.target)
                            e.target.classList.remove('icon-dianzan_kuai');
                            // 将点击到的元素中的类名变更
                            e.target.classList.add('icon-dianzan');
                            e.target.nextElementSibling.innerHTML=`
                                ${number}
                            `
                             // 同时需要去改变他的自定义属性
                             e.target.setAttribute("data-isLike","false")
                        }
                        else {
                            e.target.classList.remove('icon-dianzan_kuai');
                            e.target.classList.add('icon-dianzan');
                            e.target.innerHTML=`
                                ${number}
                            `
                             // 同时需要去改变他的自定义属性
                             e.target.setAttribute("data-isLike","false")
                        }
                    }
                })
            }
        })
        return;
    }

    if(e.target.className=='iconfont icon-shoucang'||e.target.className=='post_comment'||e.target.className=='iconfont icon-xingxing'){
        e.stopPropagation();
        // 获得自定义属性中的收藏标志
        let isCollection=e.target.dataset.iscollection;
        let status;
        if(isCollection=='true'){
            status=0;
        }
        else {
            status=1;
        }
        axios({
            url:`http://localhost:8080/posts/collections`,
            method:'POST',
            data:{
                postId:id,
                status
            },
        }).then(result => {
            const {data}=result;
            if(data.success==true){
                axios({
                    url:`http://localhost:8080/posts/hot/1`,
                    method:'GET',
                }).then(result =>{
                    console.log(result)
                    const {data}=result;
                    const {resultData}=data;
                    // 获得拿到的id对应的收藏数
                    const number=resultData[index].collections;
                    // 如果拿到的标志值是假的
                    if(isCollection=="false"){
                        // 没有收藏
                        // 再获取页面中的对应的需要改变的收藏盒子
                        if(e.target.className=='iconfont icon-shoucang'){
                            e.target.classList.remove('icon-shoucang');
                            // 将点击到的元素中的类名变更
                            e.target.classList.add('icon-xingxing');
                            e.target.nextElementSibling.innerHTML=`
                                ${number}
                            `
                            // 改变自定义属性
                            e.target.setAttribute("data-isCollection","true");
                        }
                        else {
                            e.target.classList.remove('icon-shoucang');
                            e.target.classList.add('icon-xingxing');
                            e.target.innerHTML=`
                                ${number}
                            `
                            // 改变自定义属性
                            e.target.setAttribute("data-isCollection","true");
                        }
                    }
                    else{
                        // 如果收藏了
                        // 再获取页面中的对应的需要改变的收藏盒子
                        if(e.target.className=='iconfont icon-xingxing'){
                            e.target.classList.remove('icon-xingxing');
                            // 将点击到的元素中的类名变更
                            e.target.classList.add('icon-shoucang');
                            e.target.nextElementSibling.innerHTML=`
                                ${number}
                            `
                            // 改变自定义属性
                            e.target.setAttribute("data-isCollection","false");
                        }
                        else {
                            e.target.classList.remove('icon-xingxing');
                            e.target.classList.add('icon-shoucang');
                            e.target.innerHTML=`
                                ${number}
                            `
                            // 改变自定义属性
                            e.target.setAttribute("data-isCollection","false");
                        }
                    }
                })
            }
        })
        return;
    }
    // 跳转页面
    window.location.href='post_details.html';
})

// 关于搜索之后的，页面变化：
// 1.首先是创建一个新的内容元素盒子，让原本的元素盒子移动到左侧。（该过程没有动画效果，直接出现）
// 2.内容变化完之后，顶部导航栏的东西发生变化（返回推荐内容），同时搜索栏中保留搜索的东西
// 3.注意取消跳转，搜索之后，会有一个标志，表示现在是在搜索内容中，此时点击返回推荐内容，取消跳转

// 点击返回推荐内容：
// 1.页面原本的元素盒子向右移动，同时搜索出现的盒子在移动的对应的位置之后，消失
// 2.顶部导航栏中的内容变更（返回主页）
// 3.退出搜索内容之后，标志值发生变化，此时再次点击返回主页就会出现跳转



// 将currentPage属性设置为全局变量，如果在一次遍历中达到了15次，那就+1.看下一页中的内容
var currentPage;
// 是否进入搜索内容的标志值，默认为0，表示没有进入搜索内容
var in_search=0;
// 判断是否是在这个页面中的第一次搜索
var search_flag=0;

// 获取页面顶部中的搜索框
const top_input=document.querySelector('.post_nav .post_search input');
// 获取页面顶部搜索框中的搜索按钮
const top_input_bt=document.querySelector('.post_nav .post_search button');
// 获取页面中帖子内容的父元素盒子
const post_wrapper=document.querySelector('.post_wrapper');
// 获取页面中帖子内容盒子
const post_recommend=document.querySelector('.recommend');
// 获取页面中的返回主页的文字DOM元素
const top_text=document.querySelector('.post_nav a .text');

// 对搜索框中添加一个事件，当在其中按下回车键时
top_input.addEventListener('keyup',function(e){
    // 如果按下的是回车键
    if(e.key==='Enter'){
        search();
        // 搜索盒子渲染完成之后
        // 获取搜索盒子的DOM元素
        const searchBox=document.querySelector('.search');
        searchBox.addEventListener('click',function(e){
            
            // 通过点击拿到元素中的自定义属性
            let recommend_postId=e.target.dataset.index;
            
            // 从本地中拿到搜索的帖子数组
            let recommend_ids=localStorage.getItem('recommend_ids');
            // 逗号切割成数组
            recommend_ids=recommend_ids.split(',');
            // 点击到的帖子的id
            let recommendid=recommend_ids[recommend_postId];
            // 将拿到的帖子的id存到本地中去
            localStorage.setItem('recommend_id',`${recommendid}`);
            // 将点击到的自定义属性存到本地
            localStorage.setItem('card_id',`${recommend_postId}`);
            // 将状态码存储到本地，搜索盒子的状态码为3
            localStorage.setItem('statu','3');
            window.location.href='post_details.html'
        })
    }
})

// 对搜索框中的按钮添加一个点击事件
top_input_bt.addEventListener('click',function(){
    search();
    // 搜索盒子渲染完成之后
    // 获取搜索盒子的DOM元素
    const searchBox=document.querySelector('.search');
    searchBox.addEventListener('click',function(e){
        
        // 通过点击拿到元素中的自定义属性
        let recommend_postId=e.target.dataset.index;
     
        // 从本地中拿到搜索的帖子数组
        let recommend_ids=localStorage.getItem('recommend_ids');
        // 逗号切割成数组
        recommend_ids=recommend_ids.split(',');
        // 点击到的帖子的id
        let recommendid=recommend_ids[recommend_postId];
        // 将拿到的帖子的id存到本地中去
        localStorage.setItem('post_id',`${recommendid}`);
        // 将点击到的自定义属性存到本地
        localStorage.setItem('card_id',`${recommend_postId}`);
        // 将状态码存储到本地，搜索盒子的状态码为3
        localStorage.setItem('statu','3');
        window.location.href='post_details.html'
    })
})

// 获取页面顶部的返回主页按钮
const top_bt=document.querySelector('.post_nav a');
top_bt.addEventListener('click',function(e){
    // 判断标志值是否是1；即是否在搜索内容中
    if(in_search==1){
        // 阻止页面跳转
        e.preventDefault();
        // 判断一下是否在搜索的时候没有搜索到东西，把之前的提示内容删除
        if(no_search==1){
            // 获取提示的盒子
            const tip=document.querySelector('.tip');
            tip.classList.remove('tip_change');
            // 搜索不到，标志变更
            no_search=1;
        }
        // 获取内容盒子的DOM元素
        const search=document.querySelector('.search');
        // 在搜索内容中，那么点击原先盒子出现，搜索内容盒子消失
        post_recommend.classList.add('recommend_remove');
        search.classList.add('post_remove');
        // 对搜索内容盒子添加一个事件监听
        search.addEventListener('animationend',function(){
            // 拿到父盒子中的最后一个元素
            let lastChild=post_wrapper.lastElementChild;
            // 当动画结束，把搜索内容盒子删除
            post_wrapper.removeChild(lastChild);
            // 将标志值重置为0
            in_search=0;
            // 对顶部返回按钮中的文字内容进行修改
            top_text.innerHTML=`
                返回主页
            `
            post_recommend.style.left='50%';
            post_recommend.style.transform='translateX(-50%)';
        })
        // 让保持搜索状态的标志值变为0
        localStorage.setItem('keep_search','0');
        // 清除上一次留下的记录
        sessionStorage.setItem('post_search_content','');
        // 同时清除一下表单中的值
        let post_search_value=document.querySelector('.post_search input');
        post_search_value.value='';
        // 重置状态
        search_flag=0;
    }
})

// 进行搜索之后执行的函数
async function search(){
    // 如果是第一次搜索
    if(search_flag!=1){
        let content;
        if(keep_search=="1"){
            content=sessionStorage.getItem('post_search_content');
        }
        else {
            // 拿到此时搜索框中的内容
            content=top_input.value;
        }
        content=escapeHtml(content);
        // 将拿到的搜索框中的内容存到本地中
        sessionStorage.setItem('post_search_content',`${content}`);
        // 每进行一次搜索就对currentPage进行一次重置
        currentPage=1;
        // 点击搜索的时候，创建一个新的内容盒子在原先内容盒子的后面
        div=document.createElement('div');
        div.classList.add('post_inner');
        div.classList.add('search');
        // 在父盒子后面添加一个子元素
        post_wrapper.appendChild(div);
        // 获取添加后的子元素的DOM元素
        const search=document.querySelector('.search');

        // 接帖子搜索的函数
        post_search(content,search);
        // 将原先的内容盒子移动
        post_recommend.style.left='-100%';
        post_recommend.style.transform='translateX(0)';
        
        // 将搜索的内容存到本地
        localStorage.setItem('search_keyword',`${content}`);
    }
    // 如果不是第一次搜索
    else{
        // 从本地中拿到上一次搜索的内容
        const oldcontent=sessionStorage.getItem('post_search_content');
        // 拿到此时搜索框中的内容
        let content=top_input.value;
        content=escapeHtml(content);
        // 判断搜索的内容与上一次搜索的内容是不是一致的，如果是一致的，不做处理
        if(content!=oldcontent){
            // 拿到父盒子中的最后一个元素
            let lastChild=post_wrapper.lastElementChild;
            // 把搜索内容盒子删除
            post_wrapper.removeChild(lastChild);
            // 将拿到的搜索框中的内容存到本地中,更新一次上一次搜索内容在本地中的信息
            sessionStorage.setItem('post_search_content',`${content}`);
            // 每进行一次搜索就对currentPage进行一次重置
            currentPage=1;
            // 点击搜索的时候，创建一个新的内容盒子在原先内容盒子的后面
            div=document.createElement('div');
            div.classList.add('post_inner');
            div.classList.add('search');
            // 在父盒子后面添加一个子元素
            post_wrapper.appendChild(div);
            // 获取添加后的子元素的DOM元素
            const search=document.querySelector('.search');

            // 接帖子搜索的函数
            post_search(content,search);

            // 将搜索的内容存到本地
            localStorage.setItem('search_keyword',`${content}`);
        }   
        // post_recommend.classList.remove('recommend_remove');
    }
    
    // 此时进行一次搜索，进入到搜索内容中，标志值发生变化
    in_search=1;
    // 进行了一次搜索，标志值为1
    search_flag=1;
}
// 放置一个搜索不到东西的标志，标志为1，没搜到
var no_search=0;
// 从本地中拿到是否保持搜索状态的值
let keep_search=localStorage.getItem('keep_search');
if(keep_search=="1"){
    top_input_bt.click();
}

// 帖子搜索的函数
async function post_search(keywords,fatherBox){

    axios({
        url:`http://localhost:8080/posts/search/${keywords}/${currentPage}`,
        method:'GET',
    }).then(result =>{
        console.log(result)
        const {data}=result;
        const {resultData}=data;
        // 先判断一下之前是不是没有搜索到内容，再次搜索的时候，把之前的提示删除
        if(no_search==1){
            // 获取提示的盒子
            const tip=document.querySelector('.tip');
            tip.classList.remove('tip_change');
            // 搜索不到，标志变更
            no_search=1;
        }
        // 判断一下搜索到的帖子是否有内容
        if(resultData[0]==null){
            // 获取提示的盒子
            const tip=document.querySelector('.tip');
            tip.classList.add('tip_change');
            // 搜索不到，标志变更
            no_search=1;
        }
        
        // 初始化一个搜索帖子的id数组
        let recommend_postId=[];
        for(let i=0;i<resultData.length;i++){
            // 先将拿到的帖子id存储到数组中去
            recommend_postId[i]=resultData[i].id;
            // 对拿到的数据中的图片进行字符串切割
            const img=resultData[i].images.split(',');
            // 创建一个新的div
            div=document.createElement('div');
            div.classList.add('post_card');
            // 添加两个判断，点赞和收藏是否处于点亮状态
            let like_class='icon-dianzan';
            let collect_class='icon-shoucang';
            if(resultData[i].isLike==true){
                // 如果是点赞状态
                like_class='icon-dianzan_kuai';
            }
            
            if(resultData[i].isCollection==true){
                // 如果是收藏状态
                collect_class='icon-xingxing';
            }
            div.innerHTML=`
                <div class="post_info" data-index="${i}">
                    <div class="post_person" data-index="${i}">
                        <div class="post_person_avatar" data-index="${i}">
                            <img src="http://localhost:8080/imgs/thumbnail/icon/${resultData[i].icon}" alt="" data-index="${i}">
                        </div>
                        <div class="post_person_name" data-index="${i}">${resultData[i].nickname}</div>
                    </div>
                    <div class="post_txt" data-index="${i}">${resultData[i].title}</div>
                </div>
                <div class="post_bgimg" data-index="${i}">
                    <img src="http://localhost:8080/imgs/thumbnail/post/${img[0]}" alt="" data-index="${i}">
                </div>
                <div class="post_data" data-index="${i}" data-isLike="${resultData[i].isLike}">
                    <div class="post_icon" data-index="${i}" data-isLike="${resultData[i].isLike}">
                        <i class="iconfont ${like_class}" data-index="${i}" data-isLike="${resultData[i].isLike}"></i>
                        <span class="post_like" data-index="${i}" data-isLike="${resultData[i].isLike}">${resultData[i].likes}</span>
                    </div>
                    <div class="post_icon" data-index="${i}">
                        <i class="iconfont ${collect_class}" data-index="${i}"></i>
                        <span class="post_comment" data-index="${i}" data-isLike="${resultData[i].isCollection}">${resultData[i].collections}</span>
                    </div>
                </div>
            `
            // 上面的帖子渲染完，在父盒子的后面添加
            fatherBox.appendChild(div);
        }
        // 全部渲染完之后，将拿到的数据储存到本地中去
        localStorage.setItem('recommend_ids',`${recommend_postId}`);

        // 对顶部返回按钮中的文字内容进行修改
        top_text.innerHTML=`
            返回推荐内容
        `
    }).catch(error =>{
        console.log(error);
    })
}
// 定义一个转义函数，防止xss攻击
function escapeHtml(key){
    // 通过正则替换，将用户输入中的可能是标签的东西正常输出
    key=key.replace(/(<br[^>]*>)/g, '')
            .replace(/\&/g,"&amp;")
            .replace(/\"/g,"&quot;")
            .replace(/\'/g,"&#39;")
            .replace(/\</g,"&lt;")
            .replace(/\>/g,"&gt;");
    return key;
}
