const poets=document.querySelectorAll('.box [class^=poet_box]');
    console.log(poets);
    let i=0;
    poets[i].classList.add('boxMove');
    i++;
    let time=setInterval(function(){
        if(i==poets.length){
            clearInterval(time);
        }
        poets[i].classList.add('boxMove'); 
        i++;
    },200)
    
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