/**
 * FoodieTime 食刻 - 商家訂單管理功能
 * 實現訂單列表顯示、訂單詳情查看和訂單處理功能
 */

document.addEventListener('DOMContentLoaded', function() {
    // 初始化訂單列表
    initOrdersList();
    
    // 綁定事件
    bindEvents();
});

// 模擬訂單數據
const orders = [
    {
        id: 'ORD-20230501-001',
        status: 'pending',
        statusText: '待處理',
        customer: {
            name: '王小明',
            phone: '0912-345-678',
            email: 'wang@example.com'
        },
        orderTime: '2023-05-01 12:30',
        deliveryTime: '2023-05-01 13:15',
        deliveryMethod: '外送',
        deliveryAddress: '台北市信義區松高路68號7樓',
        paymentMethod: '信用卡',
        items: [
            {
                name: '牛肉麵',
                price: 180,
                quantity: 2,
                options: '大辣、加麵',
                image: 'img/food/beef-noodle.svg'
            },
            {
                name: '小菜拼盤',
                price: 120,
                quantity: 1,
                options: '木耳、豆干、海帶',
                image: 'img/food/placeholder.svg'
            },
            {
                name: '紅茶',
                price: 40,
                quantity: 2,
                options: '去冰、半糖',
                image: 'img/food/placeholder.svg'
            }
        ],
        subtotal: 560,
        deliveryFee: 60,
        discount: 50,
        total: 570,
        specialInstructions: '請提供餐具和紙巾，謝謝！'
    },
    {
        id: 'ORD-20230501-002',
        status: 'accepted',
        statusText: '已接單',
        customer: {
            name: '林小美',
            phone: '0923-456-789',
            email: 'lin@example.com'
        },
        orderTime: '2023-05-01 12:45',
        deliveryTime: '2023-05-01 13:30',
        deliveryMethod: '自取',
        deliveryAddress: '',
        paymentMethod: 'LINE Pay',
        items: [
            {
                name: '義大利麵',
                price: 220,
                quantity: 1,
                options: '白醬、加起司',
                image: 'img/food/pasta.svg'
            },
            {
                name: '沙拉',
                price: 100,
                quantity: 1,
                options: '凱薩醬',
                image: 'img/food/placeholder.svg'
            },
            {
                name: '可樂',
                price: 45,
                quantity: 1,
                options: '加冰',
                image: 'img/food/placeholder.svg'
            }
        ],
        subtotal: 365,
        deliveryFee: 0,
        discount: 0,
        total: 365,
        specialInstructions: ''
    },
    {
        id: 'ORD-20230501-003',
        status: 'preparing',
        statusText: '準備中',
        customer: {
            name: '陳大華',
            phone: '0934-567-890',
            email: 'chen@example.com'
        },
        orderTime: '2023-05-01 13:00',
        deliveryTime: '2023-05-01 13:45',
        deliveryMethod: '外送',
        deliveryAddress: '台北市大安區復興南路一段390號',
        paymentMethod: '現金',
        items: [
            {
                name: '瑪格麗特披薩',
                price: 320,
                quantity: 1,
                options: '薄皮',
                image: 'img/food/pizza.svg'
            },
            {
                name: '炸雞翅',
                price: 150,
                quantity: 1,
                options: '辣味',
                image: 'img/food/placeholder.svg'
            },
            {
                name: '檸檬紅茶',
                price: 50,
                quantity: 2,
                options: '少冰、無糖',
                image: 'img/food/placeholder.svg'
            }
        ],
        subtotal: 570,
        deliveryFee: 70,
        discount: 0,
        total: 640,
        specialInstructions: '請不要放筷子，我自己有環保餐具'
    },
    {
        id: 'ORD-20230501-004',
        status: 'ready',
        statusText: '可取餐',
        customer: {
            name: '張小芳',
            phone: '0945-678-901',
            email: 'chang@example.com'
        },
        orderTime: '2023-05-01 13:15',
        deliveryTime: '2023-05-01 14:00',
        deliveryMethod: '自取',
        deliveryAddress: '',
        paymentMethod: '信用卡',
        items: [
            {
                name: '泰式綠咖哩雞',
                price: 250,
                quantity: 1,
                options: '中辣、加飯',
                image: 'img/food/green-curry.svg'
            },
            {
                name: '泰式奶茶',
                price: 60,
                quantity: 1,
                options: '正常冰、全糖',
                image: 'img/food/placeholder.svg'
            }
        ],
        subtotal: 310,
        deliveryFee: 0,
        discount: 30,
        total: 280,
        specialInstructions: ''
    },
    {
        id: 'ORD-20230501-005',
        status: 'completed',
        statusText: '已完成',
        customer: {
            name: '李小龍',
            phone: '0956-789-012',
            email: 'lee@example.com'
        },
        orderTime: '2023-05-01 11:30',
        deliveryTime: '2023-05-01 12:15',
        deliveryMethod: '外送',
        deliveryAddress: '台北市中山區南京東路三段219號',
        paymentMethod: 'LINE Pay',
        items: [
            {
                name: '香草冰淇淋',
                price: 120,
                quantity: 2,
                options: '加巧克力醬',
                image: 'img/food/ice-cream.svg'
            },
            {
                name: '巧克力蛋糕',
                price: 180,
                quantity: 1,
                options: '',
                image: 'img/food/placeholder.svg'
            }
        ],
        subtotal: 420,
        deliveryFee: 60,
        discount: 0,
        total: 480,
        specialInstructions: '請保持冰淇淋冷凍狀態'
    },
    {
        id: 'ORD-20230501-006',
        status: 'cancelled',
        statusText: '已取消',
        customer: {
            name: '吳小倩',
            phone: '0967-890-123',
            email: 'wu@example.com'
        },
        orderTime: '2023-05-01 10:45',
        deliveryTime: '2023-05-01 11:30',
        deliveryMethod: '外送',
        deliveryAddress: '台北市松山區民生東路四段133號',
        paymentMethod: '信用卡',
        items: [
            {
                name: '珍珠奶茶',
                price: 80,
                quantity: 3,
                options: '少冰、半糖',
                image: 'img/food/bubble-tea.svg'
            },
            {
                name: '鹹酥雞',
                price: 160,
                quantity: 1,
                options: '微辣、加蒜',
                image: 'img/food/placeholder.svg'
            }
        ],
        subtotal: 400,
        deliveryFee: 60,
        discount: 0,
        total: 460,
        specialInstructions: '',
        cancellationReason: '顧客要求取消'
    }
];

/**
 * 初始化訂單列表
 */
function initOrdersList() {
    const ordersContainer = document.getElementById('orders-container');
    
    // 清空訂單容器
    ordersContainer.innerHTML = '';
    
    // 獲取篩選條件
    const statusFilter = document.getElementById('status-filter').value;
    
    // 篩選訂單
    let filteredOrders = orders;
    if (statusFilter !== 'all') {
        filteredOrders = orders.filter(order => order.status === statusFilter);
    }
    
    // 如果沒有訂單，顯示空狀態
    if (filteredOrders.length === 0) {
        ordersContainer.innerHTML = `
            <div class="empty-state">
                <i class="material-icons-outlined">receipt_long</i>
                <p>沒有符合條件的訂單</p>
            </div>
        `;
        return;
    }
    
    // 渲染訂單列表
    filteredOrders.forEach(order => {
        const orderCard = createOrderCard(order);
        ordersContainer.appendChild(orderCard);
    });
}

/**
 * 創建訂單卡片元素
 */
function createOrderCard(order) {
    const orderCard = document.createElement('div');
    orderCard.className = 'order-card';
    orderCard.dataset.orderId = order.id;
    
    // 計算訂單總項目數
    const totalItems = order.items.reduce((sum, item) => sum + item.quantity, 0);
    
    // 構建訂單卡片內容
    orderCard.innerHTML = `
        <div class="order-header">
            <span class="order-id">${order.id}</span>
            <span class="order-status status-${order.status}">${order.statusText}</span>
        </div>
        <div class="order-info">
            <div class="order-info-item">
                <span class="order-info-label">顧客:</span>
                <span class="order-info-value">${order.customer.name}</span>
            </div>
            <div class="order-info-item">
                <span class="order-info-label">訂單時間:</span>
                <span class="order-info-value">${order.orderTime}</span>
            </div>
            <div class="order-info-item">
                <span class="order-info-label">取餐方式:</span>
                <span class="order-info-value">${order.deliveryMethod}</span>
            </div>
        </div>
        <div class="order-items">
            <div class="order-items-title">訂單項目 (${totalItems})</div>
            ${order.items.slice(0, 2).map(item => `
                <div class="order-item">
                    <span class="order-item-name">${item.name}</span>
                    <span class="order-item-quantity">x${item.quantity}</span>
                </div>
            `).join('')}
            ${order.items.length > 2 ? `<div class="order-item"><span class="order-item-name">...還有 ${order.items.length - 2} 項</span></div>` : ''}
        </div>
        <div class="order-total">
            <span>總計</span>
            <span>NT$${order.total}</span>
        </div>
    `;
    
    // 添加點擊事件，顯示訂單詳情
    orderCard.addEventListener('click', () => {
        showOrderDetails(order);
    });
    
    return orderCard;
}

/**
 * 顯示訂單詳情
 */
function showOrderDetails(order) {
    const modal = document.getElementById('order-details-modal');
    const modalContent = document.getElementById('order-details-content');
    const acceptBtn = document.getElementById('accept-order-btn');
    const rejectBtn = document.getElementById('reject-order-btn');
    
    // 構建訂單詳情內容
    modalContent.innerHTML = `
        <div class="order-detail-section">
            <div class="order-detail-title">
                <i class="material-icons-outlined">receipt</i>
                訂單資訊
            </div>
            <div class="customer-info">
                <div class="customer-info-item">
                    <span class="customer-info-label">訂單編號</span>
                    <span class="customer-info-value">${order.id}</span>
                </div>
                <div class="customer-info-item">
                    <span class="customer-info-label">訂單狀態</span>
                    <span class="customer-info-value status-${order.status}">${order.statusText}</span>
                </div>
                <div class="customer-info-item">
                    <span class="customer-info-label">訂單時間</span>
                    <span class="customer-info-value">${order.orderTime}</span>
                </div>
                <div class="customer-info-item">
                    <span class="customer-info-label">預計取餐時間</span>
                    <span class="customer-info-value">${order.deliveryTime}</span>
                </div>
                <div class="customer-info-item">
                    <span class="customer-info-label">付款方式</span>
                    <span class="customer-info-value">${order.paymentMethod}</span>
                </div>
                <div class="customer-info-item">
                    <span class="customer-info-label">取餐方式</span>
                    <span class="customer-info-value">${order.deliveryMethod}</span>
                </div>
            </div>
        </div>
        
        <div class="order-detail-section">
            <div class="order-detail-title">
                <i class="material-icons-outlined">person</i>
                顧客資訊
            </div>
            <div class="customer-info">
                <div class="customer-info-item">
                    <span class="customer-info-label">姓名</span>
                    <span class="customer-info-value">${order.customer.name}</span>
                </div>
                <div class="customer-info-item">
                    <span class="customer-info-label">電話</span>
                    <span class="customer-info-value">${order.customer.phone}</span>
                </div>
                <div class="customer-info-item">
                    <span class="customer-info-label">Email</span>
                    <span class="customer-info-value">${order.customer.email}</span>
                </div>
            </div>
        </div>
        
        <div class="order-detail-section">
            <div class="order-detail-title">
                <i class="material-icons-outlined">restaurant</i>
                訂單項目
            </div>
            <div class="order-items-list">
                ${order.items.map(item => `
                    <div class="order-item-detail">
                        <img src="${item.image}" alt="${item.name}" class="order-item-image">
                        <div class="order-item-info">
                            <div class="order-item-name-price">
                                <span class="order-item-name-detail">${item.name}</span>
                                <span class="order-item-price">NT$${item.price}</span>
                            </div>
                            ${item.options ? `<div class="order-item-options">${item.options}</div>` : ''}
                            <div class="order-item-quantity-price">
                                <span class="order-item-quantity-detail">數量: ${item.quantity}</span>
                                <span class="order-item-total">NT$${item.price * item.quantity}</span>
                            </div>
                        </div>
                    </div>
                `).join('')}
            </div>
            
            <div class="order-summary">
                <div class="summary-row">
                    <span class="summary-label">小計</span>
                    <span class="summary-value">NT$${order.subtotal}</span>
                </div>
                <div class="summary-row">
                    <span class="summary-label">運費</span>
                    <span class="summary-value">NT$${order.deliveryFee}</span>
                </div>
                ${order.discount > 0 ? `
                    <div class="summary-row">
                        <span class="summary-label">折扣</span>
                        <span class="summary-value">-NT$${order.discount}</span>
                    </div>
                ` : ''}
                <div class="summary-total">
                    <span class="summary-total-label">總計</span>
                    <span class="summary-total-value">NT$${order.total}</span>
                </div>
            </div>
        </div>
        
        ${order.deliveryMethod === '外送' ? `
            <div class="order-detail-section">
                <div class="order-detail-title">
                    <i class="material-icons-outlined">local_shipping</i>
                    配送資訊
                </div>
                <div class="delivery-info">
                    <div class="customer-info-item delivery-address">
                        <span class="customer-info-label">配送地址</span>
                        <span class="customer-info-value">${order.deliveryAddress}</span>
                    </div>
                </div>
            </div>
        ` : ''}
        
        ${order.specialInstructions ? `
            <div class="order-detail-section">
                <div class="order-detail-title">
                    <i class="material-icons-outlined">note</i>
                    特殊要求
                </div>
                <div class="special-instructions">${order.specialInstructions}</div>
            </div>
        ` : ''}
        
        ${order.status === 'cancelled' ? `
            <div class="order-detail-section">
                <div class="order-detail-title">
                    <i class="material-icons-outlined">cancel</i>
                    取消原因
                </div>
                <div class="special-instructions">${order.cancellationReason}</div>
            </div>
        ` : ''}
    `;
    
    // 根據訂單狀態顯示或隱藏按鈕
    if (order.status === 'pending') {
        acceptBtn.style.display = 'block';
        rejectBtn.style.display = 'block';
        
        // 設置按鈕事件
        acceptBtn.onclick = () => {
            acceptOrder(order.id);
            modal.classList.remove('active');
        };
        
        rejectBtn.onclick = () => {
            rejectOrder(order.id);
            modal.classList.remove('active');
        };
    } else {
        acceptBtn.style.display = 'none';
        rejectBtn.style.display = 'none';
    }
    
    // 顯示彈窗
    modal.classList.add('active');
}

/**
 * 接受訂單
 */
function acceptOrder(orderId) {
    // 在實際應用中，這裡會發送請求到後端
    // 這裡僅做前端模擬
    const orderIndex = orders.findIndex(order => order.id === orderId);
    if (orderIndex !== -1) {
        orders[orderIndex].status = 'accepted';
        orders[orderIndex].statusText = '已接單';
        
        // 更新訂單列表
        initOrdersList();
        
        // 顯示成功提示
        showNotification('訂單已接受', 'success');
    }
}

/**
 * 拒絕訂單
 */
function rejectOrder(orderId) {
    // 在實際應用中，這裡會發送請求到後端
    // 這裡僅做前端模擬
    const orderIndex = orders.findIndex(order => order.id === orderId);
    if (orderIndex !== -1) {
        orders[orderIndex].status = 'cancelled';
        orders[orderIndex].statusText = '已取消';
        orders[orderIndex].cancellationReason = '商家拒絕訂單';
        
        // 更新訂單列表
        initOrdersList();
        
        // 顯示成功提示
        showNotification('訂單已拒絕', 'error');
    }
}

/**
 * 顯示通知
 */
function showNotification(message, type) {
    // 創建通知元素
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="material-icons-outlined">${type === 'success' ? 'check_circle' : 'error'}</i>
        <span>${message}</span>
    `;
    
    // 添加到頁面
    document.body.appendChild(notification);
    
    // 顯示通知
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);
    
    // 自動關閉通知
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

/**
 * 綁定事件
 */
function bindEvents() {
    // 篩選按鈕點擊事件
    const filterBtn = document.querySelector('.filter-btn');
    filterBtn.addEventListener('click', () => {
        initOrdersList();
    });
    
    // 狀態篩選器變更事件
    const statusFilter = document.getElementById('status-filter');
    statusFilter.addEventListener('change', () => {
        initOrdersList();
    });
    
    // 彈窗關閉按鈕點擊事件
    const modalClose = document.querySelector('.modal-close');
    const modal = document.getElementById('order-details-modal');
    
    modalClose.addEventListener('click', () => {
        modal.classList.remove('active');
    });
    
    // 點擊彈窗外部關閉彈窗
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
}