/**
 * FoodieTime 食刻 - 訂單確認頁面功能
 * 實現訂單資訊顯示、訂單完成處理
 */

// ========== 全域變數初始化 ==========
/**
 * 購物車數據（從本地存儲載入）
 */
let cart = {
    items: [],
    coupon: null,
    discount: 0,
    subtotal: 0,
    deliveryFee: 60,
    total: 0
};

/**
 * 結帳資訊（從本地存儲載入）
 */
let checkoutInfo = {
    contact: {},
    delivery: {},
    dietary: [],
    payment: {}
};

// ========== 系統初始化 ==========
/**
 * DOM載入完成後的系統初始化
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 FoodieTime 訂單確認頁面開始初始化');

    try {
        // ===== 步驟1：載入數據 =====
        loadDataFromStorage();
        console.log('✅ 步驟1：數據載入完成');

        // ===== 步驟2：更新確認頁面UI =====
        updateConfirmationUI();
        console.log('✅ 步驟2：確認頁面UI更新完成');

        // ===== 步驟3：綁定事件 =====
        bindConfirmationEvents();
        console.log('✅ 步驟3：事件綁定完成');

        console.log('🎉 FoodieTime 訂單確認頁面初始化完成');

    } catch (error) {
        console.error('❌ 系統初始化失敗:', error);
        showToast('系統初始化失敗，請重新整理頁面', 'error');
    }
});

// ========== 數據載入功能 ==========
/**
 * 從本地存儲載入所有必要數據
 */
function loadDataFromStorage() {
    // ===== 載入購物車數據 =====
    const savedCart = localStorage.getItem('foodieTimeCart');
    if (savedCart) {
        try {
            const parsedCart = JSON.parse(savedCart);
            if (parsedCart && Array.isArray(parsedCart.items)) {
                cart = { ...cart, ...parsedCart };
                console.log('📦 購物車數據載入成功');
            }
        } catch (error) {
            console.error('❌ 購物車數據解析錯誤:', error);
        }
    }

    // ===== 載入結帳資訊 =====
    const savedCheckoutInfo = localStorage.getItem('foodieTimeCheckout');
    if (savedCheckoutInfo) {
        try {
            const parsedCheckoutInfo = JSON.parse(savedCheckoutInfo);
            if (parsedCheckoutInfo) {
                checkoutInfo = { ...checkoutInfo, ...parsedCheckoutInfo };
                console.log('📦 結帳資訊載入成功');
            }
        } catch (error) {
            console.error('❌ 結帳資訊解析錯誤:', error);
        }
    }
}

// ========== 訂單確認UI更新 ==========
/**
 * 更新確認頁面UI
 * 顯示完整的訂單資訊和客戶資料
 */
function updateConfirmationUI() {
    console.log('📋 更新訂單確認頁面');

    try {
        // ===== 更新訂單基本資訊 =====
        const orderNumberEl = document.getElementById('order-number');
        const orderDateEl = document.getElementById('order-date');
        const deliveryDateEl = document.getElementById('delivery-date');
        const paymentMethodEl = document.getElementById('payment-method');

        if (orderNumberEl) orderNumberEl.textContent = generateOrderNumber();
        if (orderDateEl) orderDateEl.textContent = formatDate(new Date());
        if (deliveryDateEl) deliveryDateEl.textContent = formatDateTime(checkoutInfo.delivery.time);
        if (paymentMethodEl) paymentMethodEl.textContent = getPaymentMethodName(checkoutInfo.payment.method);

        // ===== 更新訂單項目列表 =====
        updateConfirmationItems();

        // ===== 更新訂單金額摘要 =====
        updateConfirmationSummary();

        // ===== 更新客戶資訊 =====
        updateConfirmationCustomerInfo();

        console.log('✅ 訂單確認頁面更新完成');

    } catch (error) {
        console.error('❌ 更新確認頁面失敗:', error);
        showToast('載入訂單資訊失敗', 'error');
    }
}

/**
 * 更新確認頁面的訂單項目
 */
function updateConfirmationItems() {
    const confirmationItemsEl = document.getElementById('confirmation-items-list');

    if (confirmationItemsEl) {
        confirmationItemsEl.innerHTML = '';

        cart.items.forEach(item => {
            const itemEl = document.createElement('div');
            itemEl.className = 'confirmation-item';

            const optionsHtml = item.options && item.options.length > 0
                ? `<div class="confirmation-item-options">${item.options.join(', ')}</div>`
                : '';

            itemEl.innerHTML = `
                <div class="confirmation-item-details">
                    <div class="confirmation-item-name">${item.name} x ${item.quantity}</div>
                    <div class="confirmation-item-restaurant">${item.restaurant}</div>
                    ${optionsHtml}
                </div>
                <div class="confirmation-item-price">NT$ ${(item.price * item.quantity).toFixed(0)}</div>
            `;

            confirmationItemsEl.appendChild(itemEl);
        });
    }
}

/**
 * 更新確認頁面的金額摘要
 */
function updateConfirmationSummary() {
    const subtotalEl = document.getElementById('confirmation-subtotal');
    const discountEl = document.getElementById('confirmation-discount-row');
    const discountValueEl = document.getElementById('confirmation-discount');
    const deliveryFeeEl = document.getElementById('confirmation-shipping');
    const totalEl = document.getElementById('confirmation-total');

    if (subtotalEl) subtotalEl.textContent = `NT$ ${cart.subtotal.toFixed(0)}`;
    if (deliveryFeeEl) deliveryFeeEl.textContent = `NT$ ${cart.deliveryFee.toFixed(0)}`;
    if (totalEl) totalEl.textContent = `NT$ ${cart.total.toFixed(0)}`;

    if (cart.discount > 0 && discountEl && discountValueEl) {
        discountEl.style.display = 'flex';
        discountValueEl.textContent = `-NT$ ${cart.discount.toFixed(0)}`;
    } else if (discountEl) {
        discountEl.style.display = 'none';
    }
}

/**
 * 更新確認頁面的客戶資訊
 */
function updateConfirmationCustomerInfo() {
    // ===== 更新聯絡資訊 =====
    const nameEl = document.getElementById('recipient-name');
    const phoneEl = document.getElementById('recipient-phone');
    const emailEl = document.getElementById('recipient-email');

    if (nameEl) nameEl.textContent = checkoutInfo.contact.name || '';
    if (phoneEl) phoneEl.textContent = checkoutInfo.contact.phone || '';
    if (emailEl) emailEl.textContent = checkoutInfo.contact.email || '';

    // ===== 更新配送資訊 =====
    const addressEl = document.getElementById('delivery-address');
    const notesEl = document.getElementById('order-notes');

    if (addressEl) {
        const fullAddress = [
            checkoutInfo.delivery.address,
            checkoutInfo.delivery.city,
            checkoutInfo.delivery.zip
        ].filter(part => part && part.trim()).join(', ');
        addressEl.textContent = fullAddress || '';
    }

    if (notesEl) notesEl.textContent = checkoutInfo.delivery.note || '無';
}

// ========== 訂單完成功能 ==========
/**
 * 完成訂單
 * 清空本地存儲並重定向到首頁
 */
function completeOrder() {
    console.log('🎉 完成訂單');

    // ===== 清空本地存儲 =====
    try {
        localStorage.removeItem('foodieTimeCart');
        localStorage.removeItem('foodieTimeCheckout');
        console.log('🗑️ 已清空本地存儲數據');
    } catch (error) {
        console.error('❌ 清空本地存儲失敗:', error);
    }

    showToast('訂單已完成，感謝您的購買！', 'success');

    // ===== 3秒後重定向到首頁 =====
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 3000);
}

/**
 * 繼續購物
 * 清空本地存儲並重定向到首頁
 */
function continueShopping() {
    console.log('🛍️ 繼續購物');

    // ===== 清空本地存儲 =====
    try {
        localStorage.removeItem('foodieTimeCart');
        localStorage.removeItem('foodieTimeCheckout');
        console.log('🗑️ 已清空本地存儲數據');
    } catch (error) {
        console.error('❌ 清空本地存儲失敗:', error);
    }

    // ===== 立即重定向到首頁 =====
    window.location.href = 'index.html';
}

// ========== 工具函數 ==========
/**
 * 生成訂單編號
 * @returns {String} 格式化的訂單編號
 */
function generateOrderNumber() {
    const date = new Date();
    const year = date.getFullYear().toString().substr(-2);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');

    return `FT${year}${month}${day}${hours}${minutes}${random}`;
}

/**
 * 格式化日期
 * @param {Date} date - 要格式化的日期
 * @returns {String} 格式化後的日期字串
 */
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}/${month}/${day}`;
}

/**
 * 格式化日期時間
 * @param {String} dateTimeStr - ISO日期時間字串
 * @returns {String} 格式化後的日期時間
 */
function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '';

    try {
        const date = new Date(dateTimeStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');

        return `${year}/${month}/${day} ${hours}:${minutes}`;
    } catch (error) {
        console.error('❌ 日期格式化失敗:', error);
        return '';
    }
}

/**
 * 獲取付款方式的中文名稱
 * @param {String} method - 付款方式代碼
 * @returns {String} 付款方式中文名稱
 */
function getPaymentMethodName(method) {
    const methodNames = {
        'credit-card': '信用卡',
        'line-pay': 'Line Pay',
        'apple-pay': 'Apple Pay',
        'cash': '貨到付款'
    };

    return methodNames[method] || method || '未選擇';
}

// ========== 事件綁定 ==========
/**
 * 綁定確認頁面相關事件
 */
function bindConfirmationEvents() {
    console.log('🔗 開始綁定確認頁面事件');

    // ===== 繼續購物按鈕 =====
    const continueShoppingBtn = document.getElementById('continue-shopping');
    if (continueShoppingBtn) {
        continueShoppingBtn.addEventListener('click', function(e) {
            e.preventDefault();
            continueShopping();
        });
        console.log('🛍️ 繼續購物按鈕事件綁定完成');
    }

    // ===== 完成訂單按鈕（如果存在） =====
    const completeOrderBtn = document.getElementById('complete-order-btn');
    if (completeOrderBtn) {
        completeOrderBtn.addEventListener('click', function(e) {
            e.preventDefault();
            completeOrder();
        });
        console.log('🎉 完成訂單按鈕事件綁定完成');
    }

    console.log('✅ 所有確認頁面事件綁定完成');
}

// ========== 工具函數 ==========
/**
 * 顯示提示訊息
 * @param {String} message - 提示訊息內容
 * @param {String} type - 訊息類型 ('success', 'error', 'info', 'warning')
 */
function showToast(message, type = 'info') {
    console.log(`📢 顯示提示訊息 [${type}]:`, message);

    // ===== 檢查是否已存在 Toast 元素 =====
    let toastEl = document.querySelector('.toast');

    if (!toastEl) {
        toastEl = document.createElement('div');
        toastEl.className = 'toast';
        document.body.appendChild(toastEl);
    }

    // ===== 設定圖示 =====
    let iconName = 'info';
    if (type === 'success') iconName = 'check_circle';
    else if (type === 'error') iconName = 'error';
    else if (type === 'warning') iconName = 'warning';

    // ===== 設定 Toast 內容 =====
    toastEl.innerHTML = `
        <div class="toast-content">
            <span class="material-icons-outlined toast-icon ${type}">${iconName}</span>
            <span class="toast-message">${message}</span>
        </div>
        <button type="button" class="toast-close" aria-label="關閉提示">
            <span class="material-icons-outlined">close</span>
        </button>
    `;

    // ===== 綁定關閉按鈕事件 =====
    const closeBtn = toastEl.querySelector('.toast-close');
    closeBtn.addEventListener('click', function() {
        toastEl.classList.remove('show');
    });

    // ===== 顯示 Toast =====
    setTimeout(() => {
        toastEl.classList.add('show');
    }, 10);

    // ===== 自動隱藏 Toast =====
    setTimeout(() => {
        toastEl.classList.remove('show');
    }, type === 'error' ? 5000 : 3000);
}

console.log('🎉 FoodieTime 訂單確認頁面JS載入完成');
