/**
 * FoodieTime 食刻 - 結帳頁面功能
 * 實現結帳表單驗證、付款方式處理、結帳摘要顯示
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
 * 結帳資訊數據結構
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
    console.log('🚀 FoodieTime 結帳頁面開始初始化');

    try {
        // ===== 步驟1：載入購物車數據 =====
        loadCartFromStorage();
        console.log('✅ 步驟1：購物車數據載入完成');

        // ===== 步驟2：初始化結帳表單 =====
        initCheckoutForm();
        console.log('✅ 步驟2：結帳表單初始化完成');

        // ===== 步驟3：更新結帳摘要 =====
        updateCheckoutSummary();
        console.log('✅ 步驟3：結帳摘要更新完成');

        // ===== 步驟4：綁定表單事件 =====
        bindFormEvents();
        console.log('✅ 步驟4：表單事件綁定完成');

        console.log('🎉 FoodieTime 結帳頁面初始化完成');

    } catch (error) {
        console.error('❌ 系統初始化失敗:', error);
        showToast('系統初始化失敗，請重新整理頁面', 'error');
    }
});

// ========== 數據載入功能 ==========
/**
 * 從本地存儲載入購物車數據
 */
function loadCartFromStorage() {
    const savedCart = localStorage.getItem('foodieTimeCart');
    if (savedCart) {
        try {
            const parsedCart = JSON.parse(savedCart);
            if (parsedCart && Array.isArray(parsedCart.items)) {
                cart = { ...cart, ...parsedCart };
                console.log('📦 從本地存儲載入購物車數據成功');
            } else {
                throw new Error('購物車數據結構不完整');
            }
        } catch (error) {
            console.error('❌ 購物車數據解析錯誤:', error);
            showToast('載入購物車數據失敗', 'error');
        }
    } else {
        console.log('📦 本地存儲中無購物車數據');
        showToast('未找到購物車數據，請返回購物車頁面', 'warning');
    }
}

// ========== 結帳表單初始化 ==========
/**
 * 初始化結帳表單
 */
function initCheckoutForm() {
    console.log('📝 開始初始化結帳表單');

    // ===== 設定最小配送時間（當前時間+1小時） =====
    const deliveryTimeInput = document.getElementById('delivery-time');
    if (deliveryTimeInput) {
        const now = new Date();
        now.setHours(now.getHours() + 1);
        const minDateTime = now.toISOString().slice(0, 16);
        deliveryTimeInput.min = minDateTime;
        console.log('⏰ 配送時間最小值設定為:', minDateTime);
    }

    // ===== 初始化付款方式切換功能 =====
    initPaymentMethodToggle();

    // ===== 信用卡輸入格式化 =====
    initCreditCardFormatting();

    console.log('✅ 結帳表單初始化完成');
}

/**
 * 初始化付款方式切換功能
 */
function initPaymentMethodToggle() {
    const paymentMethodInputs = document.querySelectorAll('input[name="payment-method"]');
    const creditCardForm = document.getElementById('credit-card-form');
    const linePayForm = document.getElementById('line-pay-form');
    const applePayForm = document.getElementById('apple-pay-form');

    if (paymentMethodInputs.length > 0 && creditCardForm && linePayForm && applePayForm) {
        console.log('💳 初始化付款方式切換功能');

        paymentMethodInputs.forEach(input => {
            input.addEventListener('change', function() {
                // 隱藏所有支付表單
                creditCardForm.style.display = 'none';
                linePayForm.style.display = 'none';
                applePayForm.style.display = 'none';

                // 顯示選中的支付表單
                if (this.value === 'credit-card') {
                    creditCardForm.style.display = 'block';
                } else if (this.value === 'line-pay') {
                    linePayForm.style.display = 'block';
                } else if (this.value === 'apple-pay') {
                    applePayForm.style.display = 'block';
                }

                console.log('💳 切換付款方式至:', this.value);
            });
        });

        // 初始化顯示信用卡表單（默認選中）
        creditCardForm.style.display = 'block';
    }
}

/**
 * 初始化信用卡輸入格式化
 */
function initCreditCardFormatting() {
    // ===== 信用卡號碼格式化 =====
    const cardNumberInput = document.getElementById('card-number');
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function() {
            let value = this.value.replace(/\D/g, ''); // 移除非數字字符

            if (value.length > 16) {
                value = value.substr(0, 16); // 限制16位
            }

            // 每4位添加空格
            let formattedValue = '';
            for (let i = 0; i < value.length; i++) {
                if (i > 0 && i % 4 === 0) {
                    formattedValue += ' ';
                }
                formattedValue += value[i];
            }

            this.value = formattedValue;
        });
    }

    // ===== 到期日格式化 =====
    const expiryDateInput = document.getElementById('expiry-date');
    if (expiryDateInput) {
        expiryDateInput.addEventListener('input', function() {
            let value = this.value.replace(/\D/g, ''); // 移除非數字字符

            if (value.length > 4) {
                value = value.substr(0, 4); // 限制4位
            }

            // 格式化為 MM/YY
            if (value.length > 2) {
                value = value.substr(0, 2) + '/' + value.substr(2);
            }

            this.value = value;
        });
    }

    console.log('💳 信用卡輸入格式化初始化完成');
}

// ========== 結帳摘要更新 ==========
/**
 * 更新結帳摘要
 */
function updateCheckoutSummary() {
    console.log('📋 更新結帳摘要');

    const checkoutItemsEl = document.getElementById('checkout-items');
    const subtotalEl = document.getElementById('checkout-subtotal');
    const discountEl = document.getElementById('checkout-discount-row');
    const discountValueEl = document.getElementById('checkout-discount');
    const deliveryFeeEl = document.getElementById('checkout-shipping');
    const totalEl = document.getElementById('checkout-total');

    // ===== 渲染結帳項目列表 =====
    if (checkoutItemsEl) {
        checkoutItemsEl.innerHTML = '';

        cart.items.forEach(item => {
            const itemEl = document.createElement('div');
            itemEl.className = 'checkout-item';
            itemEl.innerHTML = `
                <div class="checkout-item-name">${item.name}</div>
                <div class="checkout-item-quantity">x${item.quantity}</div>
                <div class="checkout-item-price">NT$ ${(item.price * item.quantity).toFixed(0)}</div>
            `;
            checkoutItemsEl.appendChild(itemEl);
        });
    }

    // ===== 更新金額摘要 =====
    if (subtotalEl) subtotalEl.textContent = `NT$ ${cart.subtotal.toFixed(0)}`;
    if (deliveryFeeEl) deliveryFeeEl.textContent = `NT$ ${cart.deliveryFee.toFixed(0)}`;
    if (totalEl) totalEl.textContent = `NT$ ${cart.total.toFixed(0)}`;

    // ===== 處理折扣顯示 =====
    if (cart.discount > 0 && discountEl && discountValueEl) {
        discountEl.style.display = 'flex';
        discountValueEl.textContent = `-NT$ ${cart.discount.toFixed(0)}`;
    } else if (discountEl) {
        discountEl.style.display = 'none';
    }

    console.log('✅ 結帳摘要更新完成');
}

// ========== 表單驗證功能 ==========
/**
 * 驗證結帳表單的所有必填欄位
 * @returns {Boolean} 表單是否有效
 */
function validateCheckoutForm() {
    console.log('✔️ 開始驗證結帳表單');

    // ===== 獲取表單元素 =====
    const nameInput = document.getElementById('contact-name');
    const emailInput = document.getElementById('contact-email');
    const phoneInput = document.getElementById('contact-phone');
    const addressInput = document.getElementById('delivery-address');
    const deliveryTimeInput = document.getElementById('delivery-time');
    const paymentMethodInputs = document.querySelectorAll('input[name="payment-method"]');

    // ===== 驗證聯絡人姓名 =====
    if (!nameInput || !nameInput.value.trim()) {
        showToast('請輸入聯絡人姓名', 'error');
        if (nameInput) nameInput.focus();
        return false;
    }

    // ===== 驗證電子郵件 =====
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailInput || !emailRegex.test(emailInput.value.trim())) {
        showToast('請輸入有效的電子郵件地址', 'error');
        if (emailInput) emailInput.focus();
        return false;
    }

    // ===== 驗證電話號碼 =====
    const phoneRegex = /^[0-9]{8,10}$/;
    if (!phoneInput || !phoneRegex.test(phoneInput.value.trim().replace(/[\s-]/g, ''))) {
        showToast('請輸入有效的電話號碼', 'error');
        if (phoneInput) phoneInput.focus();
        return false;
    }

    // ===== 驗證配送地址 =====
    if (!addressInput || !addressInput.value.trim()) {
        showToast('請輸入配送地址', 'error');
        if (addressInput) addressInput.focus();
        return false;
    }

    // ===== 驗證配送時間 =====
    if (!deliveryTimeInput || !deliveryTimeInput.value) {
        showToast('請選擇配送時間', 'error');
        if (deliveryTimeInput) deliveryTimeInput.focus();
        return false;
    }

    // ===== 驗證付款方式 =====
    let selectedPaymentMethod = '';
    paymentMethodInputs.forEach(input => {
        if (input.checked) {
            selectedPaymentMethod = input.value;
        }
    });

    if (!selectedPaymentMethod) {
        showToast('請選擇付款方式', 'error');
        return false;
    }

    // ===== 如果選擇信用卡，驗證信用卡信息 =====
    if (selectedPaymentMethod === 'credit-card') {
        if (!validateCreditCardInfo()) {
            return false;
        }
    }

    console.log('✅ 結帳表單驗證通過');
    return true;
}

/**
 * 驗證信用卡資訊
 * @returns {Boolean} 信用卡資訊是否有效
 */
function validateCreditCardInfo() {
    const cardNumberInput = document.getElementById('card-number');
    const cardHolderInput = document.getElementById('card-holder');
    const expiryDateInput = document.getElementById('expiry-date');
    const cvvInput = document.getElementById('cvv');

    // ===== 驗證卡號 =====
    const cardNumberRegex = /^[0-9\s]{19}$/; // 考慮空格格式化後的長度
    if (!cardNumberInput || !cardNumberRegex.test(cardNumberInput.value.trim())) {
        showToast('請輸入有效的信用卡號碼', 'error');
        if (cardNumberInput) cardNumberInput.focus();
        return false;
    }

    // ===== 驗證持卡人姓名 =====
    if (!cardHolderInput || !cardHolderInput.value.trim()) {
        showToast('請輸入持卡人姓名', 'error');
        if (cardHolderInput) cardHolderInput.focus();
        return false;
    }

    // ===== 驗證到期日 =====
    const expiryDateRegex = /^(0[1-9]|1[0-2])\/([0-9]{2})$/;
    if (!expiryDateInput || !expiryDateRegex.test(expiryDateInput.value.trim())) {
        showToast('請輸入有效的到期日 (MM/YY)', 'error');
        if (expiryDateInput) expiryDateInput.focus();
        return false;
    }

    // ===== 驗證 CVV =====
    const cvvRegex = /^[0-9]{3,4}$/;
    if (!cvvInput || !cvvRegex.test(cvvInput.value.trim())) {
        showToast('請輸入有效的安全碼', 'error');
        if (cvvInput) cvvInput.focus();
        return false;
    }

    return true;
}

/**
 * 收集結帳資訊
 */
function collectCheckoutInfo() {
    console.log('📝 收集結帳資訊');

    // ===== 收集聯絡資訊 =====
    const nameInput = document.getElementById('contact-name');
    const emailInput = document.getElementById('contact-email');
    const phoneInput = document.getElementById('contact-phone');

    checkoutInfo.contact = {
        name: nameInput ? nameInput.value.trim() : '',
        email: emailInput ? emailInput.value.trim() : '',
        phone: phoneInput ? phoneInput.value.trim() : ''
    };

    // ===== 收集配送資訊 =====
    const addressInput = document.getElementById('delivery-address');
    const cityInput = document.getElementById('delivery-city');
    const zipInput = document.getElementById('delivery-zip');
    const deliveryTimeInput = document.getElementById('delivery-time');
    const noteInput = document.getElementById('delivery-note');

    checkoutInfo.delivery = {
        address: addressInput ? addressInput.value.trim() : '',
        city: cityInput ? cityInput.value.trim() : '',
        zip: zipInput ? zipInput.value.trim() : '',
        time: deliveryTimeInput ? deliveryTimeInput.value : '',
        note: noteInput ? noteInput.value.trim() : ''
    };

    // ===== 收集飲食需求 =====
    checkoutInfo.dietary = [];
    const dietaryOptions = document.querySelectorAll('input[name="dietary"]:checked');
    dietaryOptions.forEach(option => {
        checkoutInfo.dietary.push(option.value);
    });

    // ===== 收集付款資訊 =====
    const paymentMethodChecked = document.querySelector('input[name="payment-method"]:checked');
    const paymentMethod = paymentMethodChecked ? paymentMethodChecked.value : '';

    checkoutInfo.payment = {
        method: paymentMethod
    };

    // ===== 如果是信用卡，收集信用卡資訊 =====
    if (paymentMethod === 'credit-card') {
        const cardNumberInput = document.getElementById('card-number');
        const cardHolderInput = document.getElementById('card-holder');
        const expiryDateInput = document.getElementById('expiry-date');
        const cvvInput = document.getElementById('cvv');

        checkoutInfo.payment.card = {
            number: cardNumberInput ? cardNumberInput.value.trim().replace(/\s/g, '') : '',
            holder: cardHolderInput ? cardHolderInput.value.trim() : '',
            expiry: expiryDateInput ? expiryDateInput.value.trim() : '',
            cvv: cvvInput ? cvvInput.value.trim() : ''
        };
    }

    // ===== 保存到本地存儲 =====
    try {
        localStorage.setItem('foodieTimeCheckout', JSON.stringify(checkoutInfo));
        console.log('✅ 結帳資訊收集完成並已保存', checkoutInfo);
    } catch (error) {
        console.error('❌ 保存結帳資訊失敗:', error);
    }
}

// ========== 事件綁定 ==========
/**
 * 綁定表單相關事件
 */
function bindFormEvents() {
    console.log('🔗 開始綁定表單事件');

    // ===== 表單提交事件 =====
    const checkoutForm = document.getElementById('checkout-form');
    if (checkoutForm) {
        checkoutForm.addEventListener('submit', function(e) {
            e.preventDefault(); // 阻止默認提交

            console.log('📋 表單提交事件觸發');

            if (validateCheckoutForm()) {
                collectCheckoutInfo();
                // 表單驗證通過，可以提交到下一頁
                // 這裡可以添加額外的處理邏輯，然後跳轉
                console.log('✅ 表單驗證成功，準備跳轉到確認頁面');

                // 手動跳轉（因為我們阻止了默認提交）
                window.location.href = '/front/order-confirmation.html';
            }
        });

        console.log('✅ 表單提交事件綁定完成');
    }

    console.log('✅ 所有表單事件綁定完成');
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

console.log('🎉 FoodieTime 結帳頁面JS載入完成');
