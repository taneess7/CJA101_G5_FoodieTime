/**
 * FoodieTime 食刻 - 購物車頁面功能（修改版）
 * 移除動態商品生成，專注於互動功能和推薦商品
 */

// ========== 全域變數初始化 ==========
// ========== 全域變數初始化 ==========
/**
 * 推薦商品數據（保留）
 */
const recommendations = [
    {
        id: 'rec1',
        name: '牛肉麵',
        restaurant: '台北牛肉麵',
        price: 180,
        image: 'img/food/beef-noodle.svg'
    },
    {
        id: 'rec2',
        name: '義大利麵',
        restaurant: '義式廚房',
        price: 220,
        image: 'img/food/pasta.svg'
    },
    {
        id: 'rec3',
        name: '瑪格麗特披薩',
        restaurant: '披薩工坊',
        price: 320,
        image: 'img/food/pizza.svg'
    },
    {
        id: 'rec4',
        name: '泰式綠咖哩雞',
        restaurant: '泰好吃',
        price: 250,
        image: 'img/food/green-curry.svg'
    },
    {
        id: 'rec5',
        name: '香草冰淇淋',
        restaurant: '甜點天堂',
        price: 120,
        image: 'img/food/ice-cream.svg'
    },
    {
        id: 'rec6',
        name: '珍珠奶茶',
        restaurant: '手搖飲品',
        price: 80,
        image: 'img/food/bubble-tea.svg'
    }
];

// ========== 系統初始化 ==========
/**
 * DOM載入完成後的系統初始化
 * 修改：移除購物車動態生成，專注於事件綁定
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 FoodieTime 購物車頁面開始初始化（修改版）');

    try {
        // ===== 步驟1：綁定已存在的購物車項目事件 =====
        bindExistingCartItemEvents();
        console.log('✅ 步驟1：購物車事件綁定完成');

        // ===== 步驟2：初始化推薦商品 =====
        initRecommendations();
        console.log('✅ 步驟2：推薦商品初始化完成');

        // ===== 步驟3：綁定其他功能事件 =====
        bindOtherEvents();
        console.log('✅ 步驟3：其他事件綁定完成');

        // ===== 步驟4：從 DOM 更新購物車摘要 =====
        updateCartSummaryFromDOM();
        console.log('✅ 步驟4：購物車摘要更新完成');

        console.log('🎉 FoodieTime 購物車頁面初始化完成');

    } catch (error) {
        console.error('❌ 系統初始化失敗:', error);
        showToast('系統初始化失敗，請重新整理頁面', 'error');
    }
});

// ========== 購物車互動功能（修改版） ==========
/**
 * 綁定已存在的購物車項目事件
 * 針對 Thymeleaf 渲染的元素綁定交互功能
 */
function bindExistingCartItemEvents() {
    console.log('🔗 開始綁定現有購物車項目事件');

    // ===== 綁定數量增加按鈕 =====
    document.querySelectorAll('.quantity-btn.plus').forEach(btn => {
        btn.addEventListener('click', function() {
            increaseQuantity(this);
        });
    });

    // ===== 綁定數量減少按鈕 =====
    document.querySelectorAll('.quantity-btn.minus').forEach(btn => {
        btn.addEventListener('click', function() {
            decreaseQuantity(this);
        });
    });

    // ===== 綁定數量輸入框事件 =====
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            validateQuantityInput(this);
        });
    });

    console.log('✅ 購物車項目事件綁定完成');
}

/**
 * 增加商品數量
 * @param {HTMLElement} button - 點擊的增加按鈕
 */
function increaseQuantity(button) {
    const input = button.previousElementSibling;
    let currentValue = parseInt(input.value);

    if (currentValue < 99) {
        input.value = currentValue + 1;
        // 觸發 change 事件以便表單提交時使用新值
        input.dispatchEvent(new Event('change'));
    }

    console.log('➕ 數量增加至:', input.value);
}

/**
 * 減少商品數量
 * @param {HTMLElement} button - 點擊的減少按鈕
 */
function decreaseQuantity(button) {
    const input = button.nextElementSibling;
    let currentValue = parseInt(input.value);

    if (currentValue > 1) {
        input.value = currentValue - 1;
        // 觸發 change 事件
        input.dispatchEvent(new Event('change'));
    }

    console.log('➖ 數量減少至:', input.value);
}

/**
 * 驗證數量輸入
 * @param {HTMLElement} input - 數量輸入框
 */
function validateQuantityInput(input) {
    let quantity = parseInt(input.value);

    // 數量驗證與修正
    if (isNaN(quantity) || quantity < 1) {
        quantity = 1;
    }
    if (quantity > 99) {
        quantity = 99;
    }

    input.value = quantity;
    console.log('🔢 數量驗證完成:', quantity);
}

/**
 * 從 DOM 更新購物車摘要
 * 讀取 Thymeleaf 渲染的數據並更新顯示
 */
function updateCartSummaryFromDOM() {
    console.log('📊 從 DOM 更新購物車摘要');

    // 這裡的數據已經由後端計算並通過 Thymeleaf 渲染
    // JavaScript 主要負責優惠碼等前端互動功能

    // 如果有優惠碼功能，可以在這裡處理
    updateCouponDisplay();
}

// ========== 優惠碼功能（保留並修改） ==========
/**
 * 更新優惠碼顯示
 */
function updateCouponDisplay() {
    const couponMessageEl = document.getElementById('coupon-message');
    const appliedCoupon = localStorage.getItem('appliedCoupon');

    if (couponMessageEl && appliedCoupon) {
        couponMessageEl.textContent = `優惠碼 "${appliedCoupon}" 已套用`;
        couponMessageEl.className = 'coupon-message success';
    }
}

/**
 * 應用優惠碼（前端驗證）
 * @param {String} couponCode - 要應用的優惠碼
 * @returns {Boolean} 是否為有效的優惠碼
 */
function applyCoupon(couponCode) {
    console.log('🎫 嘗試應用優惠碼:', couponCode);

    const validCoupons = ['WELCOME10', 'FOOD20', 'FREESHIP', 'SAVE50', 'VIP30'];

    if (validCoupons.includes(couponCode.toUpperCase())) {
        // 保存到本地存儲
        localStorage.setItem('appliedCoupon', couponCode.toUpperCase());

        // 更新顯示
        const couponMessageEl = document.getElementById('coupon-message');
        if (couponMessageEl) {
            couponMessageEl.textContent = `優惠碼 "${couponCode}" 已套用`;
            couponMessageEl.className = 'coupon-message success';
        }

        showToast(`優惠碼 "${couponCode}" 已套用`, 'success');

        // 實際的折扣計算應該在後端處理
        // 這裡可以發送 AJAX 請求到後端重新計算價格

        console.log('✅ 優惠碼應用成功');
        return true;
    } else {
        const couponMessageEl = document.getElementById('coupon-message');
        if (couponMessageEl) {
            couponMessageEl.textContent = '無效的優惠碼';
            couponMessageEl.className = 'coupon-message error';
        }

        showToast('無效的優惠碼', 'error');
        console.log('❌ 優惠碼無效');
        return false;
    }
}

// ========== 推薦商品功能（保留） ==========
/**
 * 初始化推薦商品
 * 渲染推薦商品列表並綁定加入購物車事件
 */
function initRecommendations() {
    console.log('⭐ 開始初始化推薦商品');

    const recommendationsGrid = document.getElementById('recommendations');

    if (!recommendationsGrid) {
        console.error('❌ 找不到推薦商品容器');
        return;
    }

    // ===== 清空容器並渲染推薦商品 =====
    recommendationsGrid.innerHTML = '';

    recommendations.forEach(item => {
        const itemEl = createRecommendationElement(item);
        recommendationsGrid.appendChild(itemEl);
    });

    console.log('✅ 推薦商品初始化完成，共', recommendations.length, '個商品');
}

/**
 * 創建推薦商品元素
 * @param {Object} item - 推薦商品資訊
 * @returns {HTMLElement} 推薦商品DOM元素
 */
function createRecommendationElement(item) {
    const itemEl = document.createElement('div');
    itemEl.className = 'recommendation-card';

    itemEl.innerHTML = `
        <div class="recommendation-image-container">
            <img src="${item.image}" alt="${item.name}" class="recommendation-image"
                 onerror="this.src='img/placeholder-food.svg'">
            <button type="button" class="add-to-cart-btn" aria-label="添加到購物車" data-id="${item.id}">
                <span class="material-icons-outlined">add_shopping_cart</span>
            </button>
        </div>
        <div class="recommendation-details">
            <h3 class="recommendation-name">${item.name}</h3>
            <div class="recommendation-restaurant">${item.restaurant}</div>
            <div class="recommendation-price">NT$ ${item.price.toFixed(0)}</div>
        </div>
    `;

    // ===== 綁定加入購物車事件 =====
    const addToCartBtn = itemEl.querySelector('.add-to-cart-btn');
    addToCartBtn.addEventListener('click', function() {
        const itemId = this.dataset.id;
        const recommendedItem = recommendations.find(rec => rec.id === itemId);

        if (recommendedItem) {
            // 發送 AJAX 請求到後端添加商品
            addToCartViaAjax(recommendedItem);
            console.log('➕ 從推薦商品加入購物車:', recommendedItem.name);
        }
    });

    return itemEl;
}

/**
 * 通過 AJAX 添加商品到購物車
 * @param {Object} item - 要添加的商品資訊
 */
function addToCartViaAjax(item) {
    console.log('🛒 發送 AJAX 請求添加商品:', item.name);

    // 這裡應該發送實際的 AJAX 請求到後端
    // 示例代碼（需要根據實際後端 API 調整）
    /*
    fetch('/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            prodId: item.id,
            quantity: 1
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showToast(`${item.name} 已加入購物車`, 'success');
            // 重新載入頁面或更新購物車顯示
            window.location.reload();
        } else {
            showToast('加入購物車失敗', 'error');
        }
    })
    .catch(error => {
        console.error('❌ 加入購物車失敗:', error);
        showToast('加入購物車失敗', 'error');
    });
    */

    // 暫時的成功提示
    showToast(`${item.name} 已加入購物車`, 'success');
}

// ========== 事件綁定管理 ==========
/**
 * 綁定其他功能事件
 */
function bindOtherEvents() {
    console.log('🔗 開始綁定其他功能事件');

    // ===== 優惠碼相關事件 =====
    bindCouponEvents();

    // ===== 繼續購物按鈕（如果有的話） =====
    const continueShoppingBtns = document.querySelectorAll('.btn-text');
    continueShoppingBtns.forEach(btn => {
        if (btn.textContent.includes('繼續購物')) {
            btn.addEventListener('click', function(e) {
                console.log('🛍️ 點擊繼續購物');
            });
        }
    });

    console.log('✅ 其他功能事件綁定完成');
}

/**
 * 綁定優惠碼相關事件
 */
function bindCouponEvents() {
    const applyCouponBtn = document.getElementById('apply-coupon');
    const couponInput = document.getElementById('coupon-code');

    if (applyCouponBtn) {
        applyCouponBtn.addEventListener('click', function() {
            const couponCode = couponInput ? couponInput.value.trim() : '';

            if (couponCode) {
                applyCoupon(couponCode);
                if (couponInput) couponInput.value = ''; // 清空輸入框
            } else {
                showToast('請輸入優惠碼', 'error');
            }
        });

        console.log('🎫 優惠碼按鈕事件綁定完成');
    }

    // ===== 優惠碼輸入框回車事件 =====
    if (couponInput) {
        couponInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                applyCouponBtn.click();
            }
        });
    }
}

// ========== 工具函數（保留） ==========
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
    }, type === 'error' ? 5000 : 3000); // 錯誤訊息顯示較久
}

// ========== CSS 樣式（Toast 提示訊息） ==========
if (!document.querySelector('style[data-toast-styles]')) {
    const toastStyles = document.createElement('style');
    toastStyles.setAttribute('data-toast-styles', 'true');
    toastStyles.textContent = `
        .toast {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            padding: 16px;
            display: flex;
            align-items: center;
            gap: 12px;
            max-width: 400px;
            transform: translateX(100%);
            opacity: 0;
            transition: all 0.3s ease;
        }
        
        .toast.show {
            transform: translateX(0);
            opacity: 1;
        }
        
        .toast-content {
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;
        }
        
        .toast-icon.success { color: #4CAF50; }
        .toast-icon.error { color: #F44336; }
        .toast-icon.warning { color: #FF9800; }
        .toast-icon.info { color: #2196F3; }
        
        .toast-message {
            font-size: 14px;
            color: #333;
        }
        
        .toast-close {
            background: none;
            border: none;
            cursor: pointer;
            padding: 4px;
            border-radius: 4px;
            color: #666;
        }
        
        .toast-close:hover {
            background: #f5f5f5;
        }
    `;
    document.head.appendChild(toastStyles);
}

console.log('🎉 FoodieTime 購物車頁面JS載入完成（修改版）');
