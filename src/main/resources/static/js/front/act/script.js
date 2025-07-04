const pathname = window.location.pathname;
const contextPath = window.location.origin + '/';
const container = document.getElementById("act-container");

function formatDateTime(dateString) {
    const date = new Date(dateString);
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mi = String(date.getMinutes()).padStart(2, '0');
    const ss = String(date.getSeconds()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
}


function fetchAllActs() {
	//新增 h1 標題，只加一次
		const title = document.createElement("h1");
		title.textContent = "活動列表";
		title.style.fontWeight = "bold";
		title.style.fontSize = "32px";
		title.style.marginBottom = "24px";
		title.style.color = "gray";

		// 插入 h1 到 container 前面（前提是 container 外層要有父元素）
		const parent = container.parentElement;
		parent.insertBefore(title, container);

		//  發出 fetch 請求
	fetch(contextPath + 'api/store/activities') //fetch發出請求，控制器、service、dao跑完 response回來
	
		.then(response => {
			if (!response.ok) { //response.ok 為 true，HTTP 狀態碼為 200， return json
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
	
		.then(data => {
			//前端F12 console可看到array結果，收到的活動資料
			console.log("收到的活動資料：", data);
			data.forEach(act => { //前端渲染，產生一個div，加上class，加上內文，加上圖片，按鈕設定
				
			
			
			const discountOffText = act.actDiscount === 0
			    ? `${act.actDiscValue}折`
			    : `折 ${act.actDiscValue} 元`;

			const actCard = document.createElement("div");
		    actCard.classList.add("act-card");
			actCard.innerHTML = `
			    <div class="card-left">
					<div class="img-container">
						<div class="img-wrapper">
			        		<img src="${contextPath}api/actpic/${act.actId}" alt="活動圖" style="width: 150px; height: 150px; object-fit: cover; border-radius: 6px; margin-bottom: 8px;"
							 onerror="this.onerror=null; this.src='/images/act/default.png';">
							</div>   
							 <div style="color: white;">${act.actName}</div>
						
					</div>
			    </div>

			    <div class="card-right">
					
			        <div>					
					    
			            <div class="campaign-tag" >限時活動</div>						
						<span class="join-message" style="margin-top: 8px; color: #4CAF50; font-size: 18px;">
						</span>
						<div style="font-size: 12px; color: gray;">活動編號：${act.actId}</div>
						<div style="font-size: 24px; color: gray;">活動內容：${act.actContent}</div>
			            <div style="font-size: 24px; color: gray;">有效期限：${formatDateTime(act.actEndTime)}</div>
			        </div>
					
					<!--按鈕區塊-->
					<div class="button-wrapper">
						<button class="join-btn"
						  style="background-color: red; color: white; width: 120px;padding: 10px 20px;
						         font-size: 15px; border: none; border-radius: 12px; cursor: pointer;">
						  參加
						</button>
						
					</div>
			    </div>
			`;
					            container.appendChild(actCard);		
								/**設定按鈕 -> 可參加活動*/
								// 取得元素
								const joinButton = actCard.querySelector(".join-btn");
								const joinMessage = actCard.querySelector(".join-message");

								// 建立狀態 flag：是否已參加(從後端取得)
								let hasJoined = act.isJoined;
								  
								//根據後端參加狀態套用按鈕樣式
								if (hasJoined){
									joinButton.style.backgroundColor = "#4CAF50";
									joinButton.textContent = "已參加";
									joinMessage.textContent = "您已成功參加活動!";
								} else {
									joinButton.style.backgroundColor = "red";
									joinButton.textContent = "參加";
									joinMessage.textContent = "";
								}

								//綁定點擊事件
								joinButton.addEventListener("click", function(){
									// 決定目前是要「參加」還是「取消參加」
									const isJoining = !hasJoined;
								
								  // 發送參加或取消的 POST 請求
								  fetch(`/api/store/participate`, {
								    method: 'POST',
								    headers: {
								      'Content-Type': 'application/json'
								    },
								    body: JSON.stringify({ actId: act.actId, join: isJoining }) // ➜ 加上 join 旗標
								  })
								  .then(response => {
								    if (!response.ok) {
								      throw new Error("操作失敗");
								    }
								    return response.text();
								  })
								  .then(msg => {
								    hasJoined = isJoining; // ✅ 更新狀態

								    if (hasJoined) {
								      joinButton.style.backgroundColor = "#4CAF50";
								      joinButton.textContent = "已參加";
									  joinButton.disabled = false; // ✅ true = 禁用按鈕，防止重複點擊
								      joinMessage.textContent = msg || "您已成功參加活動！";//如果apiController沒有傳內容，才會用 || 後面的預設值：
								    } else {
								      joinButton.style.backgroundColor = "red";
								      joinButton.textContent = "參加";
								      joinMessage.textContent = msg || " ";
								    }
								  })
								  .catch(error => {
								    console.error("參加活動錯誤", error);
								    joinMessage.textContent = "⚠ 發生錯誤，請稍後再試";
								  });
								  
						 }); // end of click
					}); // end of forEach
				})
				
				.catch(error => {
				            console.error('There was a problem with the fetch "all acts" operation:', error);
				        });
				} // end of fetchAllActs()

				//在函式外呼叫						
				fetchAllActs(); 