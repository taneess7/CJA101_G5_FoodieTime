/**
 * 會員優惠券功能
 * 食刻 FoodieTime
 */

document.addEventListener('DOMContentLoaded', function() {
    // 初始化優惠券列表
    initCoupons();
    
    // 綁定事件
    bindEvents();
});

// 模擬從資料庫獲取的優惠券數據
const coupons = [
    {
        id: 'COUPON-001',
        title: '新用戶首單8折',
        merchant: '全平台適用',
        value: '8折優惠',
        description: '新用戶首次下單享受8折優惠，最高折抵100元。',
        startDate: '2023-05-01',
        endDate: '2023-06-30',
        code: 'NEWFOODIE20',
        status: 'available',
        statusText: '可使用',
        minOrderAmount: 200,
        maxDiscount: 100,
        isNew: true,
        isExpiringSoon: false,
        terms: [
            '僅限新用戶首次下單使用',
            '最低消費金額200元',
            '最高折抵100元',
            '不可與其他優惠同時使用',
            '有效期至2023年6月30日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    },
    {
        id: 'COUPON-002',
        title: '生日特別優惠',
        merchant: '全平台適用',
        value: '9折優惠',
        description: '會員生日當月享受9折優惠，最高折抵150元。',
        startDate: '2023-05-01',
        endDate: '2023-05-31',
        code: 'BIRTHDAY10',
        status: 'available',
        statusText: '可使用',
        minOrderAmount: 300,
        maxDiscount: 150,
        isNew: false,
        isExpiringSoon: true,
        terms: [
            '僅限生日當月使用',
            '最低消費金額300元',
            '最高折抵150元',
            '不可與其他優惠同時使用',
            '有效期至2023年5月31日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    },
    {
        id: 'COUPON-003',
        title: '週末限定優惠',
        merchant: '全平台適用',
        value: '滿500元折50元',
        description: '週末訂餐滿500元立即折抵50元，每週五至週日有效。',
        startDate: '2023-05-01',
        endDate: '2023-06-30',
        code: 'WEEKEND50',
        status: 'available',
        statusText: '可使用',
        minOrderAmount: 500,
        maxDiscount: 50,
        isNew: false,
        isExpiringSoon: false,
        terms: [
            '僅限週五、週六、週日使用',
            '最低消費金額500元',
            '固定折抵50元',
            '不可與其他優惠同時使用',
            '有效期至2023年6月30日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    },
    {
        id: 'COUPON-004',
        title: '日式料理專屬',
        merchant: '鮨匠日本料理',
        value: '滿1000元折100元',
        description: '在鮨匠日本料理消費滿1000元立即折抵100元。',
        startDate: '2023-04-15',
        endDate: '2023-05-15',
        code: 'SUSHI100',
        status: 'available',
        statusText: '可使用',
        minOrderAmount: 1000,
        maxDiscount: 100,
        isNew: false,
        isExpiringSoon: true,
        terms: [
            '僅限鮨匠日本料理使用',
            '最低消費金額1000元',
            '固定折抵100元',
            '不可與其他優惠同時使用',
            '有效期至2023年5月15日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    },
    {
        id: 'COUPON-005',
        title: '義式餐廳專屬',
        merchant: '羅馬假期義大利餐廳',
        value: '滿800元折80元',
        description: '在羅馬假期義大利餐廳消費滿800元立即折抵80元。',
        startDate: '2023-04-01',
        endDate: '2023-04-30',
        code: 'PASTA80',
        status: 'used',
        statusText: '已使用',
        usedDate: '2023-04-20',
        minOrderAmount: 800,
        maxDiscount: 80,
        isNew: false,
        isExpiringSoon: false,
        terms: [
            '僅限羅馬假期義大利餐廳使用',
            '最低消費金額800元',
            '固定折抵80元',
            '不可與其他優惠同時使用',
            '有效期至2023年4月30日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    },
    {
        id: 'COUPON-006',
        title: '咖啡店專屬',
        merchant: '晨曦咖啡館',
        value: '第二杯半價',
        description: '在晨曦咖啡館購買任意咖啡，第二杯半價優惠。',
        startDate: '2023-03-15',
        endDate: '2023-04-15',
        code: 'COFFEE50',
        status: 'expired',
        statusText: '已過期',
        minOrderAmount: 0,
        maxDiscount: 0,
        isNew: false,
        isExpiringSoon: false,
        terms: [
            '僅限晨曦咖啡館使用',
            '購買任意咖啡，第二杯半價',
            '不可與其他優惠同時使用',
            '有效期至2023年4月15日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    },
    {
        id: 'COUPON-007',
        title: '泰式料理專屬',
        merchant: '曼谷風情泰式料理',
        value: '滿600元折60元',
        description: '在曼谷風情泰式料理消費滿600元立即折抵60元。',
        startDate: '2023-05-01',
        endDate: '2023-06-15',
        code: 'THAI60',
        status: 'available',
        statusText: '可使用',
        minOrderAmount: 600,
        maxDiscount: 60,
        isNew: true,
        isExpiringSoon: false,
        terms: [
            '僅限曼谷風情泰式料理使用',
            '最低消費金額600元',
            '固定折抵60元',
            '不可與其他優惠同時使用',
            '有效期至2023年6月15日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    },
    {
        id: 'COUPON-008',
        title: '外送免運費',
        merchant: '全平台適用',
        value: '免運費',
        description: '任何訂單免運費優惠，無最低消費限制。',
        startDate: '2023-04-01',
        endDate: '2023-04-10',
        code: 'FREESHIP',
        status: 'expired',
        statusText: '已過期',
        minOrderAmount: 0,
        maxDiscount: 0,
        isNew: false,
        isExpiringSoon: false,
        terms: [
            '全平台適用',
            '無最低消費限制',
            '僅免除運費，不折抵餐點金額',
            '不可與其他優惠同時使用',
            '有效期至2023年4月10日'
        ],
        qrCode: 'img/qrcode-placeholder.svg'
    }
];

/**
 * 初始化優惠券列表
 */
function initCoupons() {
    const couponsContainer = document.getElementById('coupons-container');
    
    // 清空優惠券容器
    couponsContainer.innerHTML = '';
    
    // 獲取篩選條件
    const statusFilter = document.getElementById('coupon-status-filter').value;
    
    // 篩選優惠券
    let filteredCoupons = coupons;
    if (statusFilter !== 'all') {
        filteredCoupons = coupons.filter(coupon => coupon.status === statusFilter);
    }
    
    // 如果沒有優惠券，顯示空狀態
    if (filteredCoupons.length === 0) {
        couponsContainer.innerHTML = `
            <div class="empty-state">
                <i class="material-icons-outlined">local_offer</i>
                <p>沒有符合條件的優惠券</p>
            </div>
        `;
        return;
    }
    
    // 渲染優惠券列表
    filteredCoupons.forEach(coupon => {
        const couponCard = createCouponCard(coupon);
        couponsContainer.appendChild(couponCard);
    });
}

/**
 * 創建優惠券卡片元素
 */
function createCouponCard(coupon) {
    const couponCard = document.createElement('div');
    couponCard.className = `coupon-card ${coupon.status}`;
    couponCard.dataset.couponId = coupon.id;
    
    // 構建優惠券卡片內容
    couponCard.innerHTML = `
        ${coupon.isNew ? `<div class="coupon-tag tag-new">新到優惠</div>` : ''}
        ${coupon.isExpiringSoon ? `<div class="coupon-tag tag-expiring-soon">即將到期</div>` : ''}
        
        <div class="coupon-header">
            <h3 class="coupon-title">${coupon.title}</h3>
            <p class="coupon-merchant">${coupon.merchant}</p>
        </div>
        
        <div class="coupon-body">
            <div class="coupon-value">${coupon.value}</div>
            <p class="coupon-description">${coupon.description}</p>
        </div>
        
        <div class="coupon-footer">
            <div class="coupon-expiry">${formatDateRange(coupon.startDate, coupon.endDate)}</div>
            <div class="coupon-status status-${coupon.status}">${coupon.statusText}</div>
        </div>
    `;
    
    // 添加點擊事件，顯示優惠券詳情
    couponCard.addEventListener('click', () => {
        showCouponDetails(coupon);
    });
    
    return couponCard;
}

/**
 * 顯示優惠券詳情
 */
function showCouponDetails(coupon) {
    const modal = document.getElementById('coupon-details-modal');
    const modalContent = document.getElementById('coupon-details-content');
    
    // 構建優惠券詳情內容
    modalContent.innerHTML = `
        <div class="coupon-detail-header">
            <h3 class="coupon-detail-title">${coupon.title}</h3>
            <p class="coupon-detail-merchant">${coupon.merchant}</p>
            <div class="coupon-detail-value">${coupon.value}</div>
        </div>
        
        <div class="coupon-detail-section">
            <h4 class="coupon-detail-section-title">
                <i class="material-icons-outlined">description</i>
                優惠說明
            </h4>
            <p class="coupon-detail-description">${coupon.description}</p>
        </div>
        
        <div class="coupon-detail-section">
            <h4 class="coupon-detail-section-title">
                <i class="material-icons-outlined">info</i>
                優惠券資訊
            </h4>
            <div class="coupon-detail-info">
                <div class="coupon-detail-info-item">
                    <span class="coupon-detail-info-label">優惠券編號</span>
                    <span class="coupon-detail-info-value">${coupon.id}</span>
                </div>
                <div class="coupon-detail-info-item">
                    <span class="coupon-detail-info-label">優惠券狀態</span>
                    <span class="coupon-detail-info-value status-${coupon.status}">${coupon.statusText}</span>
                </div>
                <div class="coupon-detail-info-item">
                    <span class="coupon-detail-info-label">有效期限</span>
                    <span class="coupon-detail-info-value">${formatDateRange(coupon.startDate, coupon.endDate)}</span>
                </div>
                ${coupon.status === 'used' ? `
                    <div class="coupon-detail-info-item">
                        <span class="coupon-detail-info-label">使用日期</span>
                        <span class="coupon-detail-info-value">${formatDate(coupon.usedDate)}</span>
                    </div>
                ` : ''}
                <div class="coupon-detail-info-item">
                    <span class="coupon-detail-info-label">最低消費金額</span>
                    <span class="coupon-detail-info-value">${coupon.minOrderAmount > 0 ? `NT$${coupon.minOrderAmount}` : '無限制'}</span>
                </div>
                ${coupon.maxDiscount > 0 ? `
                    <div class="coupon-detail-info-item">
                        <span class="coupon-detail-info-label">最高折抵金額</span>
                        <span class="coupon-detail-info-value">NT$${coupon.maxDiscount}</span>
                    </div>
                ` : ''}
            </div>
        </div>
        
        <div class="coupon-detail-section">
            <h4 class="coupon-detail-section-title">
                <i class="material-icons-outlined">rule</i>
                使用條款
            </h4>
            <div class="coupon-detail-terms">
                <ul>
                    ${coupon.terms.map(term => `<li>${term}</li>`).join('')}
                </ul>
            </div>
        </div>
        
        ${coupon.status === 'available' ? `
            <div class="coupon-detail-section">
                <h4 class="coupon-detail-section-title">
                    <i class="material-icons-outlined">qr_code</i>
                    優惠券代碼
                </h4>
                <div class="coupon-detail-qrcode">
                    <div class="qrcode-image">
                        <img src="${coupon.qrCode}" alt="優惠券 QR Code">
                    </div>
                    <div class="qrcode-code">${coupon.code}</div>
                </div>
            </div>
            
            <div class="coupon-detail-actions">
                <button class="coupon-detail-button primary">立即使用</button>
            </div>
        ` : coupon.status === 'used' ? `
            <div class="coupon-detail-actions">
                <button class="coupon-detail-button disabled">已使用</button>
            </div>
        ` : `
            <div class="coupon-detail-actions">
                <button class="coupon-detail-button disabled">已過期</button>
            </div>
        `}
    `;
    
    // 顯示彈窗
    modal.classList.add('active');
    
    // 如果優惠券可用，添加「立即使用」按鈕事件
    if (coupon.status === 'available') {
        const useButton = modalContent.querySelector('.coupon-detail-button.primary');
        useButton.addEventListener('click', () => {
            // 在實際應用中，這裡會跳轉到相應的餐廳或商品頁面
            // 這裡僅做示範
            window.location.href = 'index.html';
        });
    }
}

/**
 * 格式化日期範圍
 */
function formatDateRange(startDate, endDate) {
    const start = formatDate(startDate);
    const end = formatDate(endDate);
    return `${start} - ${end}`;
}

/**
 * 格式化日期
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}/${month}/${day}`;
}

/**
 * 綁定事件
 */
function bindEvents() {
    // 狀態篩選器變更事件
    const statusFilter = document.getElementById('coupon-status-filter');
    statusFilter.addEventListener('change', () => {
        initCoupons();
    });
    
    // 彈窗關閉按鈕點擊事件
    const modalClose = document.querySelector('.modal-close');
    const modal = document.getElementById('coupon-details-modal');
    
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