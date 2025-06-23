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
	fetch(contextPath + 'api/allActs') //fetch發出請求，控制器、service、dao跑完 response回來
	
	.then(response => {
		if (!response.ok) { //response.ok 為 true，HTTP 狀態碼為 200， return json
			throw new Error('Network response was not ok');
		}
		return response.json();
	})
	
	.then(data => {
		console.log(data); //前端F12 console可看到array結果
		console.log("收到的活動資料：", data);
		data.forEach(act => { //前端渲染，產生一個div，加上class，加上內文，加上圖片
			const discountOffText = act.actDiscount === 0
			    ? `${act.actDiscValue}折`
			    : `折 ${act.actDiscValue} 元`;

			const actCard = document.createElement("div");
		    actCard.classList.add("act-card");
			actCard.innerHTML = `
			    <div class="card-left">
					<div class="img-container">
						<div class="img-wrapper">
			        		<img src="${contextPath}api/actpic/${act.actId}" alt="活動圖" style="width: 150px; height: 150px; object-fit: cover; border-radius: 6px; margin-bottom: 8px;">
					    </div>   
							 <div>${act.actName}</div>
						
					</div>
			    </div>

			    <div class="card-right">
			        <div>
					    <div class="price" style="font-size: 24px; color:green;>${discountOffText}</div>
			            <div class="campaign-tag" >限時活動</div>
						<div style="font-size: 24px; color: gray;">活動內容：${act.actContent}</div>
			            <div style="font-size: 24px; color: gray;">有效期限：${formatDateTime(act.actEndTime)}</div>
			        </div>
			        <button>領取</button>
			    </div>
			`;
					            container.appendChild(actCard);			
		});
	})
	
	.catch(error => {
		console.error('There was a problem with the fetch "all acts" operation:', error);
	})
	
}


fetchAllActs(); //在函式外呼叫