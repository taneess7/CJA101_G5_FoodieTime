/**
 * FoodieTime 食刻 - 結帳頁面功能
 * 實現結帳表單驗證、付款方式處理、結帳摘要顯示
 */

// ========== 全域變數初始化 ==========
/**
 * 購物車數據（此處不再從本地存儲載入，主要由後端Thymeleaf渲染）
 * 保留此結構以便其他JS功能可能需要參考，但其內容將不再影響Thymeleaf渲染的數據。
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
 * 此部分用於收集表單數據，以備提交。
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
        // 購物車數據現在由後端Thymeleaf渲染，JS無需再次載入或更新其顯示。
        // loadCartFromStorage(); // <-- 註解掉此行，因為後端已渲染
        console.log('✅ 步驟1：購物車數據（由後端提供）處理完成'); // 修改日誌訊息

        // ===== 步驟2：初始化結帳表單 =====
        initCheckoutForm();
        console.log('✅ 步驟2：結帳表單初始化完成');

        // ===== 步驟3：更新結帳摘要 =====
        // 結帳摘要現在由後端Thymeleaf渲染，JS無需再次更新其顯示。
        // updateCheckoutSummary(); // <-- 註解掉此行，因為後端已渲染
        console.log('✅ 步驟3：結帳摘要（由後端渲染）已顯示'); // 修改日誌訊息

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
 * 此函數已不再需要，因為購物車數據顯示由Thymeleaf處理。
 */
// function loadCartFromStorage() { // <-- 註解掉整個函數
//     const savedCart = localStorage.getItem('foodieTimeCart');
//     if (savedCart) {
//         try {
//             const parsedCart = JSON.parse(savedCart);
//             if (parsedCart && Array.isArray(parsedCart.items)) {
//                 cart = { ...cart, ...parsedCart };
//                 console.log('📦 從本地存儲載入購物車數據成功');
//             } else {
//                 throw new Error('購物車數據結構不完整');
//             }
//         } catch (error) {
//             console.error('❌ 購物車數據解析錯誤:', error);
//             showToast('載入購物車數據失敗', 'error');
//         }
//     } else {
//         console.log('📦 本地存儲中無購物車數據');
//         showToast('未找到購物車數據，請返回購物車頁面', 'warning');
//     }
// }

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
    // 這裡的 payment-method 對應的是前端 HTML 中的 radio input 的 name 屬性
    // 由於您的OrdersVO中只有payMethod (1:信用卡, 2:貨到付款)，
    // 且checkout.html已簡化，所以這裡的邏輯需要與實際HTML結構匹配。
    // 如果您的HTML只有兩個radio按鈕且name是`payMethod`，則需要調整選擇器。
    const paymentMethodInputs = document.querySelectorAll('input[name="payMethod"]'); // <-- 修改為匹配HTML中radio按鈕的name屬性
    const creditCardForm = document.getElementById('credit-card-form'); // 假設此ID仍存在
    const linePayForm = document.getElementById('line-pay-form');     // 假設此ID仍存在
    const applePayForm = document.getElementById('apple-pay-form');   // 假設此ID仍存在

    if (paymentMethodInputs.length > 0 && creditCardForm && linePayForm && applePayForm) {
        console.log('💳 初始化付款方式切換功能');

        paymentMethodInputs.forEach(input => {
            input.addEventListener('change', function() {
                // 隱藏所有支付表單
                if (creditCardForm) creditCardForm.style.display = 'none';
                if (linePayForm) linePayForm.style.display = 'none';
                if (applePayForm) applePayForm.style.display = 'none';

                // 顯示選中的支付表單
                if (this.value === '1') { // <-- 根據您的 value="1" (信用卡)
                    if (creditCardForm) creditCardForm.style.display = 'block';
                } else if (this.value === '2') { // <-- 根據您的 value="2" (貨到付款)
                    // 貨到付款通常沒有額外表單，可以不顯示任何表單
                }
                // 根據需要添加 Line Pay, Apple Pay 等邏輯
                // else if (this.value === 'line-pay') {
                //     if (linePayForm) linePayForm.style.display = 'block';
                // } else if (this.value === 'apple-pay') {
                //     if (applePayForm) applePayForm.style.display = 'block';
                // }

                console.log('💳 切換付款方式至:', this.value);
            });
        });

        // 初始化顯示信用卡表單（默認選中），假設其 value="1"
        const defaultSelected = document.querySelector('input[name="payMethod"]:checked');
        if (defaultSelected && defaultSelected.value === '1' && creditCardForm) {
            creditCardForm.style.display = 'block';
        }
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
 * 此函數已不再需要，因為結帳摘要顯示由Thymeleaf處理。
 */
// function updateCheckoutSummary() { // <-- 註解掉整個函數
//     console.log('📋 更新結帳摘要');

//     const checkoutItemsEl = document.getElementById('checkout-items');
//     const subtotalEl = document.getElementById('checkout-subtotal');
//     const discountEl = document.getElementById('checkout-discount-row');
//     const discountValueEl = document.getElementById('checkout-discount');
//     const deliveryFeeEl = document.getElementById('checkout-shipping');
//     const totalEl = document.getElementById('checkout-total');

//     // ===== 渲染結帳項目列表 =====
//     if (checkoutItemsEl) {
//         checkoutItemsEl.innerHTML = ''; // 清空現有內容

//         // 這裡不再從JS的cart變數中取數據，因為數據由Thymeleaf渲染
//         // 如果需要，這部分應該從HTML中讀取數據或後端提供API
//         // cart.items.forEach(item => {
//         //     const itemEl = document.createElement('div');
//         //     itemEl.className = 'checkout-item';
//         //     itemEl.innerHTML = `
//         //         <div class="checkout-item-name">${item.name}</div>
//         //         <div class="checkout-item-quantity">x${item.quantity}</div>
//         //         <div class="checkout-item-price">NT$ ${(item.price * item.quantity).toFixed(0)}</div>
//         //     `;
//         //     checkoutItemsEl.appendChild(itemEl);
//         // });
//     }

//     // ===== 更新金額摘要 =====
//     // 這些元素由Thymeleaf直接設定，JS無需再操作
//     // if (subtotalEl) subtotalEl.textContent = `NT$ ${cart.subtotal.toFixed(0)}`;
//     // if (deliveryFeeEl) deliveryFeeEl.textContent = `NT$ ${cart.deliveryFee.toFixed(0)}`;
//     // if (totalEl) totalEl.textContent = `NT$ ${cart.total.toFixed(0)}`;

//     // ===== 處理折扣顯示 =====
//     // 這部分也由Thymeleaf的th:if來處理
//     // if (cart.discount > 0 && discountEl && discountValueEl) {
//     //     discountEl.style.display = 'flex';
//     //     discountValueEl.textContent = `-NT$ ${cart.discount.toFixed(0)}`;
//     // } else if (discountEl) {
//     //     discountEl.style.display = 'none';
//     // }

//     console.log('✅ 結帳摘要更新完成');
// }

// ========== 表單驗證功能 ==========
/**
 * 驗證結帳表單的所有必填欄位
 * @returns {Boolean} 表單是否有效
 */
function validateCheckoutForm() {
    console.log('✔️ 開始驗證結帳表單');

    // ===== 獲取表單元素 =====
    // 根據您目前的 `checkout.html`，聯繫資訊和配送資訊的輸入欄位可能已簡化或改變ID
    // 請確保這些ID與您的HTML中實際使用的ID相匹配。
    // 如果您HTML中沒有這些ID，請將它們從這裡移除或修改為正確的ID。
    // 例如：您的HTML表單只要求 `ordAddr` 和 `comment`，且沒有 `contact-name`, `contact-email` 等
    const ordAddrInput = document.getElementById('ordAddr'); // 外送地址

    // 這些元素在您簡化的 checkout.html 中可能不存在，如果不存在，請從驗證中移除
    // const nameInput = document.getElementById('contact-name');
    // const emailInput = document.getElementById('contact-email');
    // const phoneInput = document.getElementById('contact-phone');
    // const deliveryTimeInput = document.getElementById('delivery-time');

    const paymentMethodInputs = document.querySelectorAll('input[name="payMethod"]'); // 修改為匹配HTML中radio按鈕的name屬性
    const deliverMethodInputs = document.querySelectorAll('input[name="deliver"]'); // 添加對取餐方式的驗證

    // ===== 驗證外送地址 =====
    if (!ordAddrInput || !ordAddrInput.value.trim()) {
        showToast('請輸入外送地址', 'error');
        if (ordAddrInput) ordAddrInput.focus();
        return false;
    }

    // ===== 驗證付款方式 =====
    let selectedPaymentMethod = false;
    paymentMethodInputs.forEach(input => {
        if (input.checked) {
            selectedPaymentMethod = true;
        }
    });
    if (!selectedPaymentMethod) {
        showToast('請選擇付款方式', 'error');
        return false;
    }

    // ===== 驗證取餐方式 =====
    let selectedDeliverMethod = false;
    deliverMethodInputs.forEach(input => {
        if (input.checked) {
            selectedDeliverMethod = true;
        }
    });
    if (!selectedDeliverMethod) {
        showToast('請選擇取餐方式', 'error');
        return false;
    }


    // ===== 如果選擇信用卡 (value="1")，驗證信用卡信息 =====
    const selectedPayMethodValue = document.querySelector('input[name="payMethod"]:checked')?.value;
    if (selectedPayMethodValue === '1') {
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
    // 假設這些ID仍在HTML中
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
 * 收集結帳資訊 (此函數主要用於前端日誌或未來AJAX提交，對Thymeleaf表單的自然提交影響不大)
 */
function collectCheckoutInfo() {
    console.log('📝 收集結帳資訊');

    // ===== 收集聯絡資訊 (如果HTML中有這些欄位) =====
    // const nameInput = document.getElementById('contact-name');
    // const emailInput = document.getElementById('contact-email');
    // const phoneInput = document.getElementById('contact-phone');

    // checkoutInfo.contact = {
    //     name: nameInput ? nameInput.value.trim() : '',
    //     email: emailInput ? emailInput.value.trim() : '',
    //     phone: phoneInput ? phoneInput.value.trim() : ''
    // };

    // ===== 收集配送資訊 =====
    const ordAddrInput = document.getElementById('ordAddr');
    const commentInput = document.getElementById('comment'); // 訂單備註

    checkoutInfo.delivery = {
        address: ordAddrInput ? ordAddrInput.value.trim() : '',
        note: commentInput ? commentInput.value.trim() : ''
    };

    // ===== 收集飲食需求 (如果HTML中有這些欄位) =====
    // checkoutInfo.dietary = [];
    // const dietaryOptions = document.querySelectorAll('input[name="dietary"]:checked');
    // dietaryOptions.forEach(option => {
    //     checkoutInfo.dietary.push(option.value);
    // });

    // ===== 收集付款資訊 =====
    const paymentMethodChecked = document.querySelector('input[name="payMethod"]:checked');
    const paymentMethod = paymentMethodChecked ? paymentMethodChecked.value : '';

    const deliverMethodChecked = document.querySelector('input[name="deliver"]:checked');
    const deliverMethod = deliverMethodChecked ? deliverMethodChecked.value : '';

    checkoutInfo.payment = {
        method: paymentMethod,
        deliver: deliverMethod
    };

    // // ===== 如果是信用卡，收集信用卡資訊 =====
    // if (paymentMethod === '1') { // 值為 '1' 代表信用卡
    //     const cardNumberInput = document.getElementById('card-number');
    //     const cardHolderInput = document.getElementById('card-holder');
    //     const expiryDateInput = document.getElementById('expiry-date');
    //     const cvvInput = document.getElementById('cvv');
    //
    //     checkoutInfo.payment.card = {
    //         number: cardNumberInput ? cardNumberInput.value.trim().replace(/\s/g, '') : '',
    //         holder: cardHolderInput ? cardHolderInput.value.trim() : '',
    //         expiry: expiryDateInput ? expiryDateInput.value.trim() : '',
    //         cvv: cvvInput ? cvvInput.value.trim() : ''
    //     };
    // }

    // ===== 保存到本地存儲 (如果需要) =====
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
// function bindFormEvents() {
//     console.log('🔗 開始綁定表單事件');
//
//     // ===== 表單提交事件 =====
//     const checkoutForm = document.getElementById('checkout-form');
//     if (checkoutForm) {
//         checkoutForm.addEventListener('submit', function(e) {
//             // e.preventDefault(); // <-- 移除此行，讓表單自然提交到後端
//
//             console.log('📋 表單提交事件觸發');
//
//             if (validateCheckoutForm()) {
//                 collectCheckoutInfo();
//                 // 表單驗證通過，會自動提交到 th:action 指定的 URL
//                 console.log('✅ 表單驗證成功，表單將提交至後端...');
//
//                 // 手動跳轉已被移除，因為讓表單自然提交
//                 // window.location.href = '/front/order-confirmation.html'; // <-- 移除此行
//             } else {
//                 e.preventDefault(); // 如果驗證不通過，則阻止表單提交
//                 console.log('❌ 表單驗證失敗，阻止提交');
//             }
//         });
//
//         console.log('✅ 表單提交事件綁定完成');
//     }
//
//     console.log('✅ 所有表單事件綁定完成');
// }

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
