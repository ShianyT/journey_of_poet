
//获取
let scene = 1
let currentPage = 1
function UseMonment(scene, currentPage) {
    axios({
        url: `http://localhost:8080/games/moments/${scene}/${currentPage}`,
        method: 'GET',
        data: {
            scene,
            currentPage,
        }
    }).then(result => {
        console.log(result);
        const moments = result.data.resultData
        for (let i = 0; i < moments.length; i++) {
            const momentsHtml = document.createElement('div')
            momentsHtml.classList.add('moments')
            // 创建评论的盒子
            momentsHtml.innerHTML = `<div class="poet_avatar">
                <img src="./images/mon${moments[i].poet == '苏轼' ? 1 : 2}.jpg" alt="">
            </div>
            <div class="moments_inner">
                <div class="poet_name">${moments[i].poet}</div>
                <div class="moments_txt">${moments[i].content}
                </div>
                <div class="moments_location">${moments[i].location}</div>
                <div class="moments_releaseTime">${moments[i].timeDifference}</div></div>`
            // 判断有无评论
            if (moments[i].gameComments !== undefined) {
                console.log(moments[i].gameComments !== undefined);
                const comments = document.createElement('div')
                let poet = ''
                let reply
                comments.classList.add('moments_chat')
                // 创建回复
                const replyFun = function () {
                    const comment_part = momentsHtml.querySelector('.moments_comment')
                    console.log(comment_part);
                    for (let m = 0; m < moments[i].gameComments.length; m++) {
                        const comDiv = document.createElement('div')
                        comDiv.classList.add('comment_part')
                        let comPoet
                        // 判断有无回复诗人 补充评论文本
                        if (moments[i].gameComments[m].replyPoet === undefined) {
                            comPoet = `<span class="comment_poet">${moments[i].gameComments[m].poet}:</span>
                            <span class="comment_txt">${moments[i].gameComments[m].comment}</span>`

                        }
                        else {
                            comPoet = `<span class="comment_poet">
                            <span class="thepoet">${moments[i].gameComments[m].poet}</span>
                            <span class="reply">回复</span>
                            <span class="thepoet">${moments[i].gameComments[m].replyPoet}:</span>
                        </span>
                        <span class="comment_txt">${moments[i].gameComments[m].comment}</span>`
                        }
                        // 插入评论盒子
                        comDiv.innerHTML = comPoet
                        momentsHtml.querySelector('.moments_comment').append(comDiv)

                    }
                }
                // 点赞的诗人
                const likeFun = function () {
                    for (let n = 0; n < moments[i].gameLiked.length; n++) {
                        if (n == 0) poet += moments[i].gameLiked[n].name;
                        else poet += ', ' + moments[i].gameLiked[n].name;
                    }
                    console.log(poet);
                    momentsHtml.querySelector('.like_poet').innerHTML = poet
                }
                comments.innerHTML = `
                        <div class="moments_like">
                            <i class="iconfont icon-like"></i>
                            <span class="like_poet"></span>
                        </div>
                        <div class="moments_comment">
                        </div>`
                console.log(momentsHtml.querySelector('.moments_inner'));
                momentsHtml.querySelector('.moments_inner').append(comments)
                likeFun()
                replyFun()

            }
            // 整个评论区插入
            document.querySelector('.body_content').append(momentsHtml)
        }
    })
}
UseMonment(scene, currentPage)