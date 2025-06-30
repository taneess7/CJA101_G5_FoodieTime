// 篩選訂單
function filterOrders() {
    const status = document.getElementById('status-filter').value;
    window.location.href = `/store/orders/manage?status=${status}`;
}

// 顯示拒絕訂單彈窗
function showRejectModal(orderId) {
    const modal = document.getElementById('reject-order-modal');
    const form = document.getElementById('reject-order-form');
    form.action = `/store/orders/reject/${orderId}`;
    modal.style.display = 'block';
}

// 關閉拒絕訂單彈窗
function closeRejectModal() {
    const modal = document.getElementById('reject-order-modal');
    modal.style.display = 'none';

    // 重置表單
    const form = document.getElementById('reject-order-form');
    form.reset();
    document.getElementById('custom-reason-container').style.display = 'none';
}

// 監聽拒絕原因選項變化
document.addEventListener('DOMContentLoaded', function() {
    const reasonRadios = document.querySelectorAll('input[name="cancelReason"]');
    const customReasonContainer = document.getElementById('custom-reason-container');

    reasonRadios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.value === '其他') {
                customReasonContainer.style.display = 'block';
                document.getElementById('customReason').required = true;
            } else {
                customReasonContainer.style.display = 'none';
                document.getElementById('customReason').required = false;
            }
        });
    });


    // ================= WebSocket 即時通知邏輯 (STOMP 版本) =================
    const body = document.querySelector('body');
    const storeId = body.dataset.storeId; // 從 <body> 的 data-store-id 屬性獲取商家ID (memId)
    let stompClient = null; // 將 stompClient 設為全域變數

    if (storeId) {
        connectWebSocket(storeId);
    }

    function connectWebSocket(storeId) {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({}, function(frame) {
            console.log('STOMP 連線成功: ' + frame);

            // 訂閱邏輯
            const subscriptionUrl = `/topic/orders/${storeId}`;
            stompClient.subscribe(subscriptionUrl, function(notification) {
                console.log('收到新訂單通知:', notification.body);
                try {
                    const newOrder = JSON.parse(notification.body);
                    addNewOrderCard(newOrder);
                } catch (e) {
                    console.error('解析新訂單資料失敗:', e);
                }
            });

        }, function(error) {
            console.error('STOMP 連線失敗，將觸發 onclose 事件進行重連:', error);
        });

        socket.onclose = function() {
            console.log('SockJS 連線已關閉，將在 5 秒後嘗試重連...');
            if (stompClient) {
                stompClient.disconnect();
            }
            setTimeout(() => {
                console.log('正在嘗試重新連線...');
                connectWebSocket(storeId);
            }, 5000);
        };
    }

    /**
     * 動態建立並在頁面頂部插入新的訂單卡片...
     * @param {object} order - 從 WebSocket 收到的新訂單物件 (NewOrderNotificationDTO)
     */
    function addNewOrderCard(order) {
        const ordersList = document.querySelector('.orders-list');
        if (!ordersList) return;

        // 移除「目前沒有訂單」的提示 (如果存在)
        const noOrdersMessage = ordersList.querySelector('.no-orders');
        if (noOrdersMessage) {
            noOrdersMessage.remove();
        }

        // --- 步驟 1: 動態生成訂單項目的 HTML 列表 ---
        const itemsHtml = order.items.map(item => `
        <li>
            <span>${item.productName}</span> ×
            <span>${item.quantity}</span> =
            NT$<span>${item.price * item.quantity}</span>
            ${item.note ? `<div class="item-note">備註：${item.note}</div>` : ''}
        </li>
    `).join('');

        // --- 步驟 2: 根據訂單狀態，動態生成操作按鈕的 HTML ---
        // 新訂單一定是待處理 (status == 0)，所以直接生成對應的按鈕
        const actionsHtml = `
        <div class="order-actions">
            <div>
                <form action="/store/orders/accept/${order.ordId}" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-success">
                        <i class="material-icons-outlined">check</i>
                        接受訂單
                    </button>
                </form>
                <button type="button" class="btn btn-danger" onclick="showRejectModal(${order.ordId})">
                    <i class="material-icons-outlined">close</i>
                    拒絕訂單
                </button>
            </div>
        </div>
    `;

        // --- 步驟 3: 建立新的訂單卡片 div，並組裝所有 HTML ---
        const newCard = document.createElement('div');
        newCard.className = 'order-card pending'; // 新訂單固定是 pending 狀態

        newCard.innerHTML = `
        <div class="order-header">
            <div class="order-info">
                <h3 class="order-id">訂單 #${order.ordId}</h3>
                <span class="order-time">${new Date(order.ordDate).toLocaleString('zh-TW', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }).replace(/\//g, '/')}</span>
            </div>
            <div class="order-status">
                <span class="status-badge pending">${order.orderStatusText}</span>
            </div>
        </div>

        <div class="order-details">
            <div class="customer-info">
                <p><strong>客戶：</strong><span>${order.memName}</span></p>
                <p><strong>電話：</strong><span>${order.memPhone}</span></p>
                ${order.ordAddr ? `<p class="delivery-address"><strong>外送地址：</strong><span>${order.ordAddr}</span></p>` : ''}
            </div>

            <div class="order-items">
                <h4>訂購項目：</h4>
                <ul>
                    ${itemsHtml}
                </ul>
            </div>

            <div class="order-total">
                <p><strong>訂單總額：NT$<span>${order.actualPayment}</span></strong></p>
                <p class="order-comment"><strong>備註：</strong><span>${order.comment}</span></p>
            </div>
        </div>
        
        ${actionsHtml}
    `;

        // 將新卡片插入到列表的最前面
        ordersList.prepend(newCard);

        // 可選：新增音效提示或視覺效果
        newCard.style.backgroundColor = '#fff3cd'; // 臨時高亮
        setTimeout(() => { newCard.style.backgroundColor = ''; }, 3000);

        // 可選：播放提示音
        // const audio = new Audio('/sounds/notification.mp3');
        // audio.play();
    }

    window.addEventListener('beforeunload', function() {
        if (stompClient && stompClient.connected) {
            stompClient.disconnect(function() {
                console.log('STOMP 連線已主動關閉。');
            });
        }
    });
});