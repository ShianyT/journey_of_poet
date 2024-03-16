// 获取上传的文件内容
const input_file=document.querySelector('.release_part .picture_content .picture input');
// 对表单进行监控，如果表单中的内容发生变化，那么就开始将拿到的文件发送给后端
let flag=0;
input_file.addEventListener('change',function(){
    if(flag==3){
        // 当标记值达到3的时候，就需要吧上传图片的盒子清除
        const input_picture=document.querySelector('.release_part .picture_content .inputBox');
        input_picture.style.display='none';
    }
    const file=input_file.files[0];
    let formData=new FormData();
    formData.append('file',file);
    // 对后端发起请求
    axios({
        url:'http://localhost:8080/posts/image',
        method:'POST',
        data:formData
    }).then(result =>{
        const {data}=result;
        if(data.success==true){
            const {resultData}=data;
            console.log(flag)
            console.log(resultData)
            // 获取页面中的图片的DOM元素
            const picture_content=document.querySelector('.upload_body .picture_content');
            // 在该元素的最后添加一个子元素
            let div=document.createElement('div');
            div.classList.add('picture');
            div.innerHTML=`
                <div class="iconBox">
                    <div class="iconfont icon-jianhao"></div>
                </div>
            `
            picture_content.insertBefore(div,picture_content.children[0]);
            div.style.backgroundImage=`url(http://localhost:8080/imgs/thumbnail/post/${resultData[flag]})`;  
            flag++;

            // 每创建完一个新的div盒子出现，就获取一次数组，对整个数组的成员添加一次自定义属性
            const arr=document.querySelectorAll('.upload_body .picture_content .icon-jianhao');
            let index=arr.length-1;
            for(let i=0;i<arr.length;i++){
                // 设置发送给后端的index值
                arr[i].setAttribute("data-index",`${index}`);
                // 设置自己删除所需的索引值
                arr[i].setAttribute("data-removenum",`${i}`);
                index--;
            }
        }
    })
    
})


// 我删除之后，点击加号发送过去的应该是和原来的一样的，但是，后端返回给我的数据却不是原来的数组，

// 获取图片上的减号
const picture_content=document.querySelector('.upload_body .pictureBox .picture_content');
// 对该按钮添加一个点击事件
picture_content.addEventListener('click',function(e){
    console.log(e.target.className)
    if(e.target.className=='iconfont icon-jianhao'){
        let removeNumber=e.target.dataset.removenum;
        console.log(removeNumber)
        let index=e.target.dataset.index;
        console.log(index)
        // 发送数据给后端
        axios({
            url:`http://localhost:8080/posts/image/${index}`,
            method:'DELETE'
        }).then(result =>{
            console.log(result)
            console.log(picture_content.children[removeNumber])
            picture_content.removeChild(picture_content.children[removeNumber]);
            console.log(flag)
            flag--;
            console.log(flag)
        })
    }
})

// 获取页面中的返回按钮
const return_community=document.querySelector('.release_nav .return');
return_community.addEventListener('click',function(e){
    e.preventDefault();
    axios({
        url:'http://localhost:8080/posts/cancel',
        method:'GET'
    }).then(result =>{
       const {data}=result;
       if(data.success==true){
            window.location.href='community.html';
       }
    })
})
// 获取页面中的取消按钮
const cancell=document.querySelector('.release_part .upload_body .bt .cancell');
cancell.addEventListener('click',function(){
    axios({
        url:'http://localhost:8080/posts/cancel',
        method:'GET'
    }).then(result =>{
       const {data}=result;
       if(data.success==true){
            window.location.href='community.html';
       }
    })
})

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

// 获取页面中的发布按钮
const publish=document.querySelector('.release_part .upload_body .bt .confirm');
publish.addEventListener('click',function(){
    // 先获取页面中标题表单中的值和文本内容的值
    let title=document.querySelector('.release_part .upload_body .title_inputBox input').value;
    let content=document.querySelector('.release_part .upload_body .content').value;
    
    title=escapeHtml(title);
    content=escapeHtml(content);
    // 然后将表单中的值发送的后端
    axios({
        url:'http://localhost:8080/posts/save',
        method:'POST',
        data:{
            title,
            content
        }
    }).then(result => {
        const {data}=result;
        if(data.success==true){
            // 获取遮罩层DOM元素和弹窗DOM元素
            const release_mask=document.querySelector('.popup_mask');
            const release_popup=document.querySelector('.popup');
            release_mask.style.display='block';
            release_popup.style.display='flex';
        }
    })
})


