// 按照体验过的场景更新朋友圈
let usertoken = localStorage.getItem("token");
console.log(usertoken);

const scene = document.querySelectorAll(".sce_part")[0];
const sayInner = document.querySelector(".say_inner");
// 鼠标移动会跟随
scene.addEventListener("mousemove", (e) => {
  // 定义参数
  const range = 13;
  let horizontal = (e.clientX / parseInt(getComputedStyle(scene).width, 10)) * range - range / 2;
  let vertical = (e.clientY / parseInt(getComputedStyle(scene).height, 10)) * range - range / 2;
  // 调用函数，传入offset近大远小
  document.querySelector(".sce_bg").style.transform = calcFun(
    horizontal,
    vertical,
    0
  );
  document.querySelector(".sce_poet").style.transform = calcFun(
    horizontal,
    vertical,
    0
  );
  document.querySelector(".sce_tree").style.transform = calcFun(
    horizontal,
    vertical,
    2
  );
  document.querySelector(".sce_four").style.transform = calcFun(
    horizontal,
    vertical,
    3
  );
});
// 封装元素移动旋转的计算函数 输出transform值
function calcFun(horizontal, vertical, offset) {
  return `translate3d(${horizontal * offset}px,${vertical * offset
    }px,0) rotateX(${-vertical}deg) rotateY(${horizontal}deg)`;
}

// 接口部分
// 获取上一个页面跳转传递的参数
var receiveData = decodeURI(window.location.search);
var valueid = receiveData.substring(receiveData.indexOf("=") + 1);
// 定义场景序号
let eventId = valueid;
let nextPlotId = "0";
let saydata;
var sceneID
var nowSceneId
var paintingId = ""
var nowPaint = ""
// 从场景第一句话开始
UseScene(eventId, nextPlotId);
// 新场景完善文本
async function UseScene() {
  nowSceneId = sceneID
  console.log("nowsce" + nowSceneId);
  savePlot();
  try {
    const result = await axios({
      url: `http://localhost:8080/plots/content/${eventId}/${nextPlotId}`,
      data: {
        eventId,
        nextPlotId,
      },
    });
    saydata = result.data.resultData;
    // 如果下一个场景不是一个数，则为选项分支剧情，创建选项
    if (saydata.length != 1) {
      if (saydata.length == 2) {
        sayInner.innerHTML = `<div class="select_tips_txt">你会说:(请选择)</div>
    <div class="select_btn">
        <button class="selectPlotBtn" id="${saydata[0].nextPlotId}">${saydata[0].content}</button>
        <button class="selectPlotBtn" id="${saydata[1].nextPlotId}">${saydata[1].content}</button>
    </div>`;
      } else if (saydata.length == 3) {
        sayInner.innerHTML = `<div class="select_tips_txt">你会说：（请选择）</div>
    <div class="select_btn">
        <button class="selectPlotBtn" id="${saydata[0].nextPlotId}">${saydata[0].content}</button>
        <button class="selectPlotBtn" id="${saydata[1].nextPlotId}">${saydata[1].content}</button>
        <button class="selectPlotBtn" id="${saydata[2].nextPlotId}">${saydata[2].content}</button>
    </div>`;
      }
    } else sayTextType(saydata[0].type);
    // 更新 nextPlotId 和 eventId
    eventId = saydata[0].eventId;
    nextPlotId = saydata[0].nextPlotId;
    sceneID = saydata[0].sceneId
    if (saydata[0].paintingId !== undefined && saydata[0].paintingId != nowPaint) {
      paintingId = saydata[0].paintingId;
      personSay();
    }
    if (saydata[0].paintingId == undefined) {
      document.querySelector('.sayPerson').style.display = 'none'
    }
    CreateEle(sceneID)
    console.log("完善文本时：" + eventId + " " + nextPlotId);
  } catch (error) {
    console.error("API 请求失败:", error);
  }
}
keepPlot();
// 人物立绘
function personSay() {
  document.querySelector('.sayPerson').style.display = 'block'
  nowPaint = paintingId.charAt(0)
  document.querySelector('.sayPerson').innerHTML = `<img src="./images/${nowPaint}.png" class="${nowPaint}" alt="">`
}
// 更换下一个场景
function CreateEle() {
  if (sceneID == nowSceneId) return;
  console.log(sceneID);
  if (sceneID == undefined) return;
  document.querySelectorAll('.sce_part')[0].innerHTML = `<img class="sce_bg" src = "./images/${sceneID}-4.png" alt = "bg">
                                    <div class="poet">
                                        <img class="sce_poet" src="./images/${sceneID}-3.png" alt="poet"></div>
                                            <img class="sce_tree" src="./images/${sceneID}-2.png" alt="tree">
                                            <img class="sce_four" src="./images/${sceneID}-1.png" alt="tree">`;
  // document.querySelector(".scene1").append(newEle);
}
// 判断是旁白、对话或结局
function sayTextType(TextType) {
  if (TextType == 0) {
    sayInner.innerHTML = `<div class="narration_txt">${saydata[0].content}</div><div class="keep_tips">继续>></div>`;
  } else if (TextType == 1) {
    sayInner.innerHTML = `<div class="narration_txt">${saydata[0].content}</div><div class="keep_tips">继续>></div>`;
  } else if (TextType == 3) {
    console.log("recite");
    CreateRecite();
    const poemId = saydata[0].content.replace(/[^\d]/g, "");
    console.log("poemid:" + poemId);
    // 渲染数据
    reciteFun(poemId);
    console.log("Ok");
    // fillTxt(poemId);
    // 获取所需元素
    // const recite = document.querySelector(".recite");
    // SelectFill();
    TorF();
  } else if (TextType == 4) {
    // sayInner.innerHTML = `<div class="narration_txt">${saydata[0].content}</div><div class="keep_tips">继续>></div>`;
    document.querySelector('.Journey').insertAdjacentHTML("beforeend", `<div class="box">
    <div class="over_text">
        <div class="over_text_content">
            <div class="text_title">
                <div class="content">结局：厨艺不精</div>
            </div>
            <div class="text_content">&nbsp;&nbsp;${saydata[0].content}</div>
            <div class="buttonBox">
                <div class="again"><a href="ExperienceEnter.html">重新开始</a></div>
                <div class="out"><a href="index.html">退出重溯</a></div>
            </div>
        </div>
        <div class="realHistory">
        <video src="./video/outcome1.mp4" controls></video>
        <span class="history_txt">(史料鉴赏)</span>
    </div>
    </div>
</div>`)
  }
}
// 播放点击音效
const opendoorVoice=document.querySelector('.opendoor');
// const food=document.querySelector('.food');
const clickVoice=document.querySelector('.clickVoice');
// 事件委托 继续剧情
function keepPlot() {
  sayInner.addEventListener("click", async (e) => {
    clickVoice.play();
    console.log("点击了继续");
    //点击继续后，更改文本
    if (e.target.className == "keep_tips") {
      console.log(sayInner.children);
      console.log("继续" + nextPlotId);
      console.log("ok");
      console.log("点击继续后eventid:" + eventId + " nextplod:" + nextPlotId);
      // 播放开门音效
      if(nextPlotId==12){opendoorVoice.play()}
      if(nextPlotId!=12){opendoorVoice.pause()}
      // 播放美食音效
      // if(nextPlotId>14&&nextPlotId<20){food.play()}
      // else{food.pause()}
      // if(nextPlotId>29&&nextPlotId<32){
      //   document.querySelector('.sea').play();
      // }
      // else{
      //   document.querySelector('.sea').pause();
      // }
      // 该场景完毕，到下一个场景
      if (nextPlotId === undefined) {
        document.querySelector('.transitionTips').style.display = 'flex'
        eventId++; nextPlotId = "0";
        UseScene(eventId, nextPlotId)
      }
      // eventId++; nextPlotId = "0" 
      document.querySelector('.keepExperience').addEventListener('click', () => {
        // eventId++; nextPlotId = "0";
        document.querySelector('.transitionTips').style.display = 'none'
        // UseScene(eventId, nextPlotId);
        return;
      })
      await UseScene(eventId, nextPlotId);
    }
    // 点击选项，走分支剧情
    else if (e.target.className == "selectPlotBtn") {
      console.log("btn:" + e.target);
      nextPlotId = e.target.id;
      console.log(nextPlotId);
      await UseScene(eventId, nextPlotId);
    }
  });
}
// 保存进度
function savePlot() {
  axios({
    method: "POST",
    url: `http://localhost:8080/plots/save`,
    data: {
      eventId,
    },
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      Authorization: usertoken,
    },
  }).then((result) => {
    console.log(result.data.errorMsg);
  });
}
// 过渡时 点击继续
document.querySelector('.keepExperience').addEventListener('click', () => [

])
// ----背诗部分-----
// 判断给哪条横线补充句子
let whichAdd;
// 用户填的答案
let FillAnswer;
// 定义所需数据
var fillSelection
var reciteFill
var fillSelTxt
var rightFill
var fillSelect
var reciteConfirm
var reciteResult
var reciteResultSucceed
var reciteResultFail
var reciteResultMark
function CreateRecite() {
  console.log("创建recite盒子");
  const Journey = document.querySelector(".Journey");
  Journey.insertAdjacentHTML(
    "beforeend",
    `<div class="recite_mark"></div>
    <div class="recite_inner">
        <div class="recite_tips">[回忆在赤壁赋写的诗，点击选词补充诗句吧]<br>（注：一条横线为两个词语）</div>
        <div class="recite_part">
            <div class="recite_poetry">
                
            </div>
            <div class="fill_select">
            </div>
        </div>
        <div class="recite_result_btn">
            <button class="reciteConfirm">确认</button>
            <button class="reciteRefill">重来</button>
        </div>
    </div>
    <div class="recite_result_mark"></div>
    <div class="resetConfirm">
        <span>你确定重来吗？</span>
        <div class="resetBtn">
            <button class="resetConfirmBtn">确认</button>
            <button class="resetCancelBtn">取消</button>
        </div>
    </div>
    <div class="recite_result">
        <div class="recite_result_succeed recite_result_box">
        </div>
        <div class="recite_result_fail  recite_result_box">
            <span>不对哦，重来一遍吧！</span>
            <button class="recite_again">重来</button>
        </div>
</div>`
  );
}
// 接口渲染左侧原诗句
async function reciteFun(poemId) {
  let i = 0
  const recitePoetry = document.querySelector(".recite_poetry");
  const result = await axios({
    url: `http://localhost:8080/plots/recite/${poemId}`,
    data: {
      poemId,
    },
    headers: {
      Authorization: usertoken,
    },
  })
  console.log(result.data.resultData.hollowedPoem);
  const hollow = result.data.resultData.hollowedPoem;
  hollow.forEach((v) => {
    const reciteNewEle = document.createElement("span");
    // 判断是否为数字，添加空位填诗
    if (!isNaN(v.charAt(0))) {
      reciteNewEle.classList.add("recite_fill");
      reciteNewEle.id = i++
      reciteNewEle.style.minWidth = (32 * v.charAt(0)) + "px";
      recitePoetry.appendChild(reciteNewEle);
      // 标点符号放后面
      const pointChar = document.createElement("span");
      pointChar.classList.add("recite_txt");
      pointChar.innerHTML = `${v.charAt(1)}`;
      recitePoetry.appendChild(pointChar);
    } else {
      reciteNewEle.classList.add("recite_txt");
      reciteNewEle.innerHTML = `${v}`;
      recitePoetry.appendChild(reciteNewEle);
    }
    // 判断是否换行
    if (v.charAt(v.length - 1) == "。") {
      recitePoetry.append(document.createElement("br"));
    }
  });
  // 正序
  rightFill = result.data.resultData.positiveOrder;
  console.log(rightFill);
  FillAnswer = new Array(rightFill.length)
  for (let i = 0; i < rightFill.length; i++) {
    FillAnswer[i] = new Array();
  }
  // 获取乱序
  fillSelTxt = result.data.resultData.outOfOrder;
  console.log(fillSelTxt);
  reciteFill = document.querySelectorAll('.recite_fill')
  fillTxt()
  fillPlace()
}
// 接口渲染右侧填空词语
function fillTxt() {
  fillSelect = document.querySelector(".fill_select");
  console.log("右侧：" + fillSelTxt);
  fillSelTxt.forEach((v) => {
    const fillSelection = document.createElement("div");
    fillSelection.classList.add("fill_selection");
    fillSelection.innerHTML = `${v}`;
    fillSelect.appendChild(fillSelection);
  });
  fillSelection = document.querySelectorAll(".fill_selection");
  // SelectFill();
  // return fillSelection
  console.log("await:" + fillSelection);
}
// 选择横线
let fillele
function fillPlace() {
  console.log(reciteFill);
  for (let i = 0; i < reciteFill.length; i++) {
    reciteFill[i].addEventListener('click', () => {
      reciteFill.forEach(v => { v.style.borderBottom = '1px solid #000'; v.style.backgroundColor = 'transparent' })
      reciteFill[i].style.backgroundColor = '#afafaf'
      // reciteFill[i].style.borderBottom = '1px solid red'
      fillele = i
      SelectFill()
    })
  }
}

// 选择填入的词语
function SelectFill() {
  console.log(fillele);
  console.log("调用选择词语的函数");
  for (let i = 0; i < fillSelection.length; i++) {
    fillSelection[i].style.pointerEvents = 'all'
    // 监听词语的点击事件
    fillSelection[i].addEventListener('click', function () {
      console.log(reciteFill[fillele]);
      console.log("选项个数");
      fillSelection.forEach(v => { v.style.pointerEvents = 'none' })
      if (reciteFill[fillele].childNodes.length == 3) {
        // for (let i = 0; i < fillSelection.length; i++) {
        //   // fillSelection[i].style.pointerEvents = 'none'
        // }
        console.log("3");
        reciteFill[fillele].style.pointerEvents = 'none'
        reciteFill[fillele].style.animationName = 'Worse'
        reciteFill[fillele].style.animationDuration = '.5s'
        return;
      }
      else {
        // fillSelection.forEach(v => { v.style.pointerEvents = 'none' })
        console.log(this.getBoundingClientRect().left);
        // 获取点击的元素视口的距离
        let FillMoveLeft = this.getBoundingClientRect().left
        let FillMoveTop = this.getBoundingClientRect().top
        // 调用函数
        this.style.transform = 'translate(' + FillSelectionMove(FillMoveLeft, FillMoveTop, reciteFill[fillele]) + ')'
        // 监听元素移动完毕
        // console.log("id " + FillAnswer[reciteFill[fillele].id]);
        fillSelection[i].ontransitionend = (e) => {
          fillSelection.forEach(v => { v.style.pointerEvents = 'all' })
          // reciteFill.forEach(v => { v.style.borderBottom = '1px solid #000'; v.style.backgroundColor = 'transparent' })
          console.log('ok');
          //给横线补充诗句 
          reciteFill[fillele].appendChild(fillSelection[i])
          fillSelection[i].style.transform = 'translate(0,0)'
          // 取消外边距
          fillSelection[i].style.marginBottom = '0px'
          fillSelection[i].style.marginLeft = '0px'
          FillAnswer[reciteFill[fillele].id].push(fillSelection[i].innerHTML)
        }
        // 点击过的不能再点击
        fillSelection[i].style.pointerEvents = 'none'
      }
    })
  }
}
// 定义函数 移动用户选择填入的词
function FillSelectionMove(Left, Top, FillWhere) {
  console.log(FillWhere.childNodes);
  // 如果该横线没有补充诗句
  if (FillWhere.childNodes.length == 0) {
    // 获取横线到视口的距离并相减 得出词语需要的偏移量
    let FillMoveX = -(Left - FillWhere.getBoundingClientRect().left)
    let FillMoveY = -(Top - FillWhere.getBoundingClientRect().top)
    return `${FillMoveX}px,${FillMoveY}px`
  }
  else {
    // 如果横线上已有一个词
    if (FillWhere.childNodes.length == 1) {
      console.log(FillWhere.children);
      // 获取第一个子元素到视口的距离并相减
      let FillMoveX = -(Left - FillWhere.children[0].getBoundingClientRect().left-FillWhere.children[0].getBoundingClientRect().width)
      let FillMoveY = -(Top - FillWhere.children[0].getBoundingClientRect().top)

      return `${FillMoveX}px,${FillMoveY}px`
    }
    else if (FillWhere.childNodes.length == 2) {
      console.log(FillWhere.children);
      // 获取第一个子元素到视口的距离并相减
      let FillMoveX = -(Left - FillWhere.children[1].getBoundingClientRect().left-FillWhere.children[1].getBoundingClientRect().width)
      let FillMoveY = -(Top - FillWhere.children[1].getBoundingClientRect().top)

      return `${FillMoveX}px,${FillMoveY}px`
    }
    // else {
    //   for (let i = 0; i < fillSelection.length; i++) {
    //     fillSelection[i].style.pointerEvents = 'none'
    //   }
    //   FillWhere.style.pointerEvents = 'none'
    //   FillWhere.style.animationName = 'Worse'
    //   FillWhere.style.animationDuration = '.5s'
    // }
  }
}
// 判断诗句正误
function TorF() {
  reciteConfirm = document.querySelector(".reciteConfirm");
  reciteResult = document.querySelector(".recite_result");
  reciteResultSucceed = document.querySelector(".recite_result_succeed");
  reciteResultFail = document.querySelector(".recite_result_fail");
  reciteResultMark = document.querySelector(".recite_result_mark");
  reciteConfirm.addEventListener("click", () => {
    reciteResult.style.display = "block";
    reciteResultMark.style.display = "block";
    // 判断和正确答案是否一致
    console.log(FillAnswer);
    for (let i = 0; i < rightFill.length; i++) {
      if (FillAnswer[i].toString() !== rightFill[i].toString()) {
        // 弹出失败
        reciteResultFail.style.display = "block";
        return;
      }
    }
    // 弹出成功
    if (nextPlotId === undefined) { eventId++; nextPlotId = "0" }
    UseScene(eventId, nextPlotId)
    reciteResultSucceed.style.display = "block";
    reciteReward();
  });
  // 中途填错 点击重来
  document.querySelector(".reciteRefill").addEventListener("click", () => {
    document.querySelector(".resetConfirm").style.display = "block";
    reciteResultMark.style.display = "block";
  });
  // 确定重来
  document.querySelector(".resetConfirmBtn").addEventListener("click", () => {
    document.querySelector(".resetConfirm").style.display = "none";
    reciteResultMark.style.display = "block";
    reciteReset();
  });
  // 取消重来
  document.querySelector(".resetCancelBtn").addEventListener("click", () => {
    reciteResultMark.style.display = "none";
    document.querySelector(".resetConfirm").style.display = "none";
  });
  // 失败后重来
  const reciteAgain = document.querySelector(".recite_again");
  reciteAgain.addEventListener("click", () => {
    reciteAgain.style.animationName = "btnActive";
    reciteReset();
  });

}
// 重置函数
function reciteReset() {
  reciteResultFail.style.display = 'none'
  reciteResult.style.display = 'none'
  reciteResultMark.style.display = 'none'
  // FillAnswer.forEach(v => { v = new Array() })
  FillAnswer = new Array(rightFill.length)
  for (let i = 0; i < rightFill.length; i++) {
    FillAnswer[i] = new Array();
  }
  console.log(FillAnswer);
  reciteFill.forEach(v => { v.innerHTML = ''; v.style.pointerEvents = 'all' })
  reciteFill.forEach(v => v.style.backgroundColor = 'transparent')
  fillSelection.forEach(v => v.style.pointerEvents = 'all')
  fillSelect.innerHTML = ''
  fillTxt();
  fillSelection = document.querySelectorAll('.fill_selection');
  SelectFill(fillSelection);
}
// 奖励部分
var type = "recite"
function reciteReward() {
  axios({
    url: `http://localhost:8080/user/infos/reward/${type}`,
    data: {
      type,
    },
  }).then(result => {
    console.log(result);
    reciteResultSucceed.innerHTML = `<span>补充诗句完成！</span>
    <span>${result.data.resultData}</span>
        <i class="iconfont icon-gemini"></i>
    <button class="continue_game">继续体验</button>`
    // 答对后继续体验
    const continueGame = document.querySelector(".continue_game");
    continueGame.addEventListener("click", () => {
      const Journey = document.querySelector('.Journey')
      for (let i = 0; i < 5; i++) {
        Journey.removeChild(Journey.children[Journey.children.length - 1])
      }
      // if (nextPlotId === undefined) { eventId++; nextPlotId = "0" }
      // UseScene(eventId, nextPlotId)
    });
  })
}