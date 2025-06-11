/**
 * FoodieTime 食刻 - 購物車頁面功能
 * 實現購物車管理、商品操作、優惠碼和推薦商品功能
 */

// ========== 全域變數初始化 ==========
/**
 * 購物車數據結構
 * @property {Array} items - 購物車商品列表
 * @property {String} coupon - 當前使用的優惠碼
 * @property {Number} discount - 折扣金額
 * @property {Number} subtotal - 小計金額
 * @property {Number} deliveryFee - 運費
 * @property {Number} total - 總計金額
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
 * 推薦商品數據
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
 * 執行順序：購物車 -> 推薦商品 -> 事件綁定
 */
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 FoodieTime 購物車頁面開始初始化');

    try {
        // ===== 步驟1：初始化購物車功能 =====
        initCart();
        console.log('✅ 步驟1：購物車功能初始化完成');

        // ===== 步驟2：初始化推薦商品 =====
        initRecommendations();
        console.log('✅ 步驟2：推薦商品初始化完成');

        // ===== 步驟3：綁定所有事件 =====
        bindAllEvents();
        console.log('✅ 步驟3：事件綁定完成');

        console.log('🎉 FoodieTime 購物車頁面初始化完成');

    } catch (error) {
        console.error('❌ 系統初始化失敗:', error);
        showToast('系統初始化失敗，請重新整理頁面', 'error');
    }
});

// ========== 購物車核心功能 ==========
/**
 * 初始化購物車功能
 * 包含：數據載入、界面更新、總額計算
 */
function initCart() {
    console.log('🛒 開始初始化購物車功能');

    // ===== 步驟1：從本地存儲載入購物車數據 =====
    loadCartFromStorage();

    // ===== 步驟2：如果購物車為空，添加測試數據 =====
    if (cart.items.length === 0) {
        console.log('購物車為空，添加測試商品');
        cart.items.push({
            id: 'test1',
            name: '經典牛肉麵',
            restaurant: '老張牛肉麵',
            price: 180,
            quantity: 1,
            image: 'img/food/beef-noodle.svg',
            options: ['大碗', '微辣']
        });
    }

    // ===== 步驟3：計算購物車總額 =====
    calculateCartTotals();

    // ===== 步驟4：更新購物車界面 =====
    updateCartUI();

    console.log('✅ 購物車功能初始化完成，當前商品數量:', cart.items.length);
}

/**
 * 從本地存儲載入購物車數據
 * 如果數據損壞則重置為預設值
 */
function loadCartFromStorage() {
    const savedCart = localStorage.getItem('foodieTimeCart');
    if (savedCart) {
        try {
            const parsedCart = JSON.parse(savedCart);
            // 驗證數據結構完整性
            if (parsedCart && Array.isArray(parsedCart.items)) {
                cart = { ...cart, ...parsedCart };
                console.log('📦 從本地存儲載入購物車數據成功');
            } else {
                throw new Error('購物車數據結構不完整');
            }
        } catch (error) {
            console.error('❌ 購物車數據解析錯誤:', error);
            resetCart();
        }
    } else {
        console.log('📦 本地存儲中無購物車數據');
    }
}

/**
 * 重置購物車為預設狀態
 */
function resetCart() {
    cart = {
        items: [],
        coupon: null,
        discount: 0,
        subtotal: 0,
        deliveryFee: 60,
        total: 0
    };
    console.log('🔄 購物車已重置為預設狀態');
}

/**
 * 保存購物車數據到本地存儲
 */
function saveCartToStorage() {
    try {
        localStorage.setItem('foodieTimeCart', JSON.stringify(cart));
        console.log('💾 購物車數據已保存到本地存儲');
    } catch (error) {
        console.error('❌ 保存購物車數據失敗:', error);
    }
}

/**
 * 更新購物車界面
 * 根據購物車商品數量決定顯示空購物車提示或商品列表
 */
function updateCartUI() {
    console.log('🖼️ 開始更新購物車界面');

    const emptyCartEl = document.querySelector('.empty-cart');
    const cartContentEl = document.querySelector('.cart-content');
    const cartItemsEl = document.querySelector('.cart-items');

    // ===== 元素存在性檢查 =====
    if (!emptyCartEl || !cartContentEl || !cartItemsEl) {
        console.error('❌ 找不到必要的購物車界面元素');
        return;
    }

    // ===== 判斷購物車是否為空 =====
    if (cart.items.length === 0) {
        emptyCartEl.style.display = 'flex';
        cartContentEl.style.display = 'none';
        console.log('📭 顯示空購物車提示');
        return;
    }

    // ===== 顯示購物車內容 =====
    emptyCartEl.style.display = 'none';
    cartContentEl.style.display = 'grid';

    // ===== 清空並重新渲染購物車項目 =====
    cartItemsEl.innerHTML = '';

    cart.items.forEach((item, index) => {
        const itemEl = createCartItemElement(item, index);
        cartItemsEl.appendChild(itemEl);
    });

    // ===== 更新購物車摘要 =====
    updateCartSummary();

    console.log('✅ 購物車界面更新完成，顯示', cart.items.length, '個商品');
}

/**
 * 創建購物車項目元素
 * @param {Object} item - 商品資訊
 * @param {Number} index - 商品在購物車中的索引
 * @returns {HTMLElement} 購物車項目DOM元素
 */
function createCartItemElement(item, index) {
    const itemEl = document.createElement('div');
    itemEl.className = 'cart-item';
    itemEl.dataset.index = index;

    // ===== 處理商品選項顯示 =====
    const optionsHtml = item.options && item.options.length > 0
        ? item.options.map(opt => `<span class="item-option">${opt}</span>`).join('')
        : '';

    // ===== 構建項目HTML結構 =====
    itemEl.innerHTML = `
        <div class="item-image-container">
            <img src="${item.image}" alt="${item.name}" class="item-image" 
                 onerror="this.src='img/placeholder-food.svg'">
        </div>
        <div class="item-details">
            <div class="item-header">
                <h3 class="item-name">${item.name}</h3>
                <button type="button" class="item-remove" aria-label="移除項目">
                    <span class="material-icons-outlined">close</span>
                </button>
            </div>
            <div class="item-restaurant">${item.restaurant}</div>
            <div class="item-options">${optionsHtml}</div>
            <div class="item-actions">
                <div class="quantity-control">
                    <button type="button" class="quantity-btn decrease" 
                            aria-label="減少數量" ${item.quantity <= 1 ? 'disabled' : ''}>
                        <span class="material-icons-outlined">remove</span>
                    </button>
                    <input type="number" class="quantity-input" value="${item.quantity}" 
                           min="1" max="99" aria-label="數量">
                    <button type="button" class="quantity-btn increase" aria-label="增加數量">
                        <span class="material-icons-outlined">add</span>
                    </button>
                </div>
                <div class="item-price">NT$ ${(item.price * item.quantity).toFixed(0)}</div>
            </div>
        </div>
    `;

    // ===== 綁定項目相關事件 =====
    bindCartItemEvents(itemEl, index);

    return itemEl;
}

/**
 * 綁定購物車項目的所有事件
 * @param {HTMLElement} itemEl - 購物車項目DOM元素
 * @param {Number} index - 項目索引
 */
function bindCartItemEvents(itemEl, index) {
    // ===== 移除按鈕事件 =====
    const removeBtn = itemEl.querySelector('.item-remove');
    removeBtn.addEventListener('click', function() {
        console.log('🗑️ 移除購物車項目:', index);
        removeCartItem(index);
    });

    // ===== 減少數量按鈕事件 =====
    const decreaseBtn = itemEl.querySelector('.quantity-btn.decrease');
    decreaseBtn.addEventListener('click', function() {
        const newQuantity = cart.items[index].quantity - 1;
        console.log('➖ 減少商品數量:', index, '新數量:', newQuantity);
        updateCartItemQuantity(index, newQuantity);
    });

    // ===== 增加數量按鈕事件 =====
    const increaseBtn = itemEl.querySelector('.quantity-btn.increase');
    increaseBtn.addEventListener('click', function() {
        const newQuantity = cart.items[index].quantity + 1;
        console.log('➕ 增加商品數量:', index, '新數量:', newQuantity);
        updateCartItemQuantity(index, newQuantity);
    });

    // ===== 數量輸入框事件 =====
    const quantityInput = itemEl.querySelector('.quantity-input');
    quantityInput.addEventListener('change', function() {
        let quantity = parseInt(this.value);

        // 數量驗證與修正
        if (isNaN(quantity) || quantity < 1) quantity = 1;
        if (quantity > 99) quantity = 99;

        console.log('🔢 手動修改商品數量:', index, '新數量:', quantity);
        updateCartItemQuantity(index, quantity);
    });
}

/**
 * 移除購物車項目
 * @param {Number} index - 要移除的項目索引
 */
function removeCartItem(index) {
    if (index >= 0 && index < cart.items.length) {
        const item = cart.items[index];
        cart.items.splice(index, 1);

        // 重新計算總額並更新界面
        calculateCartTotals();
        updateCartUI();

        showToast(`已移除 ${item.name}`, 'info');
        console.log('✅ 成功移除購物車項目:', item.name);
    } else {
        console.error('❌ 無效的項目索引:', index);
    }
}

/**
 * 更新購物車項目數量
 * @param {Number} index - 項目索引
 * @param {Number} quantity - 新數量
 */
function updateCartItemQuantity(index, quantity) {
    if (index >= 0 && index < cart.items.length && quantity >= 1 && quantity <= 99) {
        cart.items[index].quantity = quantity;

        // 重新計算總額並更新界面
        calculateCartTotals();
        updateCartUI();

        console.log('✅ 成功更新商品數量:', cart.items[index].name, '新數量:', quantity);
    } else {
        console.error('❌ 無效的參數 - 索引:', index, '數量:', quantity);
    }
}

/**
 * 計算購物車總額
 * 包含：小計、折扣、運費、總計
 */
function calculateCartTotals() {
    console.log('🧮 開始計算購物車總額');

    // ===== 計算小計 =====
    cart.subtotal = cart.items.reduce((total, item) => {
        return total + (item.price * item.quantity);
    }, 0);

    // ===== 計算折扣 =====
    if (cart.coupon) {
        cart.discount = calculateDiscount(cart.coupon, cart.subtotal, cart.deliveryFee);
    } else {
        cart.discount = 0;
    }

    // ===== 計算總計 =====
    cart.total = Math.max(0, cart.subtotal + cart.deliveryFee - cart.discount);

    // ===== 保存到本地存儲 =====
    saveCartToStorage();

    console.log('✅ 購物車總額計算完成 - 小計:', cart.subtotal, '折扣:', cart.discount, '總計:', cart.total);
}

/**
 * 根據優惠碼計算折扣金額
 * @param {String} couponCode - 優惠碼
 * @param {Number} subtotal - 小計金額
 * @param {Number} deliveryFee - 運費
 * @returns {Number} 折扣金額
 */
function calculateDiscount(couponCode, subtotal, deliveryFee) {
    const discountRules = {
        'WELCOME10': () => subtotal * 0.1,          // 10% 折扣
        'FOOD20': () => subtotal * 0.2,             // 20% 折扣
        'FREESHIP': () => deliveryFee,              // 免運費
        'SAVE50': () => Math.min(50, subtotal * 0.1), // 最多折50元
        'VIP30': () => subtotal >= 300 ? subtotal * 0.3 : 0 // 滿300享3折
    };

    const discountFunction = discountRules[couponCode];
    return discountFunction ? discountFunction() : 0;
}

/**
 * 更新購物車摘要
 * 顯示小計、折扣、運費、總計等資訊
 */
function updateCartSummary() {
    const subtotalEl = document.getElementById('subtotal');
    const discountEl = document.getElementById('discount-row');
    const discountValueEl = document.getElementById('discount');
    const deliveryFeeEl = document.getElementById('shipping');
    const totalEl = document.getElementById('total');

    if (subtotalEl) subtotalEl.textContent = `NT$ ${cart.subtotal.toFixed(0)}`;
    if (deliveryFeeEl) deliveryFeeEl.textContent = `NT$ ${cart.deliveryFee.toFixed(0)}`;
    if (totalEl) totalEl.textContent = `NT$ ${cart.total.toFixed(0)}`;

    // ===== 折扣顯示處理 =====
    if (cart.discount > 0 && discountEl && discountValueEl) {
        discountEl.style.display = 'flex';
        discountValueEl.textContent = `-NT$ ${cart.discount.toFixed(0)}`;
    } else if (discountEl) {
        discountEl.style.display = 'none';
    }

    // ===== 更新優惠碼訊息 =====
    updateCouponMessage();
}

/**
 * 更新優惠碼訊息顯示
 */
function updateCouponMessage() {
    const couponMessageEl = document.getElementById('coupon-message');

    if (couponMessageEl) {
        if (cart.coupon) {
            couponMessageEl.textContent = `優惠碼 "${cart.coupon}" 已套用`;
            couponMessageEl.className = 'coupon-message success';
        } else {
            couponMessageEl.textContent = '';
            couponMessageEl.className = 'coupon-message';
        }
    }
}

/**
 * 應用優惠碼
 * @param {String} couponCode - 要應用的優惠碼
 * @returns {Boolean} 是否成功應用
 */
function applyCoupon(couponCode) {
    console.log('🎫 嘗試應用優惠碼:', couponCode);

    const validCoupons = ['WELCOME10', 'FOOD20', 'FREESHIP', 'SAVE50', 'VIP30'];

    if (validCoupons.includes(couponCode.toUpperCase())) {
        cart.coupon = couponCode.toUpperCase();
        calculateCartTotals();
        updateCartSummary();

        showToast(`優惠碼 "${couponCode}" 已套用`, 'success');
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

// ========== 推薦商品功能 ==========
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
            addToCart(recommendedItem);
            console.log('➕ 從推薦商品加入購物車:', recommendedItem.name);
        }
    });

    return itemEl;
}

/**
 * 添加商品到購物車
 * @param {Object} item - 要添加的商品資訊
 */
function addToCart(item) {
    console.log('🛒 添加商品到購物車:', item.name);

    // ===== 檢查商品是否已在購物車中 =====
    const existingItemIndex = cart.items.findIndex(cartItem =>
        cartItem.id === item.id &&
        JSON.stringify(cartItem.options || []) === JSON.stringify(item.options || [])
    );

    if (existingItemIndex !== -1) {
        // ===== 如果已存在，增加數量 =====
        updateCartItemQuantity(existingItemIndex, cart.items[existingItemIndex].quantity + 1);
        showToast(`${item.name} 數量已增加`, 'success');
    } else {
        // ===== 如果不存在，添加新項目 =====
        cart.items.push({
            ...item,
            quantity: 1
        });

        calculateCartTotals();
        updateCartUI();
        showToast(`${item.name} 已加入購物車`, 'success');
    }

    console.log('✅ 商品已加入購物車，當前購物車商品數:', cart.items.length);
}

// ========== 事件綁定管理 ==========
/**
 * 綁定所有系統事件
 * 包含：按鈕點擊、優惠碼應用等
 */
function bindAllEvents() {
    console.log('🔗 開始綁定所有事件');

    // ===== 優惠碼相關事件 =====
    bindCouponEvents();

    console.log('✅ 所有事件綁定完成');
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

console.log('🎉 FoodieTime 購物車頁面JS載入完成');
