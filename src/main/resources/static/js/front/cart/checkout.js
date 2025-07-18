document.addEventListener('DOMContentLoaded', function() {

    // ================== 【核心功能】動態更新優惠券折扣與總金額 ==================
    console.log("【偵錯】checkout.js 已載入。");

    const couponSelect = document.getElementById('coupon-select');
    const hiddenCouponInput = document.getElementById('selected-coupon-id-input');
    const subtotalElem = document.getElementById('checkout-subtotal');
    const shippingElem = document.getElementById('checkout-shipping');
    const discountDisplayElem = document.getElementById('dynamic-discount-display');
    const totalElem = document.getElementById('checkout-total');

    // 檢查所有需要的元素是否都被找到了
    if (couponSelect && subtotalElem && shippingElem && discountDisplayElem && totalElem && hiddenCouponInput) {
        console.log("【偵錯】所有 HTML 元素都已成功找到。");

        const originalSubtotal = parseInt(subtotalElem.textContent.replace(/[^0-9]/g, '')) || 0;
        const originalShipping = parseInt(shippingElem.textContent.replace(/[^0-9]/g, '')) || 0;
        console.log(`【偵錯】讀取到的原始金額 -> 小計: ${originalSubtotal}, 運費: ${originalShipping}`);

        // ★★★ 存取 originalSubtotal ★★★
        function updateCouponAvailability() {
            const options = couponSelect.querySelectorAll('option');
            options.forEach(option => {
                const minSpend = parseInt(option.getAttribute('data-min-spend')) || 0;
                if (minSpend > 0 && originalSubtotal < minSpend) {
                    option.disabled = true;
                    // 避免重複添加提示文字
                    if (!option.textContent.includes('(未達低消)')) {
                        option.textContent = option.textContent + ' (未達低消)';
                    }
                } else {
                    option.disabled = false;
                    // 移除提示文字
                    option.textContent = option.textContent.replace(' (未達低消)', '');
                }
            });
        }

        function updatePrice() {
            console.log("【偵錯】updatePrice 函數被呼叫。");

            const selectedOption = couponSelect.options[couponSelect.selectedIndex];
            const discountAmount = parseInt(selectedOption.getAttribute('data-discount')) || 0;
            const minSpendAmount = parseInt(selectedOption.getAttribute('data-min-spend')) || 0;

            console.log(`【偵錯】選擇的優惠券 -> 折扣金額: ${discountAmount}, 最低消費: ${minSpendAmount}`);

            let finalDiscount = 0;
            discountDisplayElem.innerHTML = '';

            if (discountAmount > 0) {
                if (originalSubtotal >= minSpendAmount) {
                    console.log("【偵錯】條件符合，套用折扣。");
                    finalDiscount = discountAmount;
                    discountDisplayElem.innerHTML = `
                        <span>折扣</span>
                        <span class="dynamic-discount-text">-NT$ ${finalDiscount}</span>
                    `;
                    const discountTextElem = discountDisplayElem.querySelector('.dynamic-discount-text');
                    discountTextElem.style.backgroundColor = 'red';
                    discountTextElem.style.color = 'white';
                    discountTextElem.style.padding = '2px 8px';
                    discountTextElem.style.borderRadius = '4px';
                    discountTextElem.style.fontWeight = 'bold';
                } else {
                    console.log("【偵錯】條件不符，自動重置為不使用優惠券。");

                    // ★★★ 【核心邏輯】自動重置為「不使用優惠券」★★★
                    couponSelect.value = "0";

                    // 顯示友善的提示訊息
                    discountDisplayElem.innerHTML = `
                        <span></span>
                        <span class="dynamic-warning-text">此優惠券需消費滿 NT$ ${minSpendAmount}，已自動取消選擇</span>
                    `;
                    const warningTextElem = discountDisplayElem.querySelector('.dynamic-warning-text');
                    warningTextElem.style.color = '#ff9800';
                    warningTextElem.style.fontSize = '0.9em';
                    warningTextElem.style.fontWeight = 'bold';

                    // 3秒後清除警告訊息
                    setTimeout(() => {
                        discountDisplayElem.innerHTML = '';
                    }, 3000);
                }
            } else {
                console.log("【偵錯】未選擇優惠券或折扣為 0。");
            }

            // 更新隱藏輸入框的值
            hiddenCouponInput.value = couponSelect.value;
            const newTotal = originalSubtotal + originalShipping - finalDiscount;
            totalElem.textContent = `NT$ ${newTotal >= 0 ? newTotal : 0}`;
            console.log(`【偵錯】更新後總金額為: ${newTotal}`);
        }

        // 初始化優惠券可用性
        updateCouponAvailability();

        // 綁定事件監聽器
        couponSelect.addEventListener('change', updatePrice);

        // 初始化價格顯示
        updatePrice();

    } else {
        console.error("【錯誤】找不到必要的 HTML 元素！請檢查以下 ID 是否存在: 'coupon-select', 'selected-coupon-id-input', 'checkout-subtotal', 'checkout-shipping', 'dynamic-discount-display', 'checkout-total'");
    }

    // ================== 處理付款方式切換 ==================
    const paymentMethodRadios = document.querySelectorAll('input[name="payMethod"]');
    const creditCardDetailsDiv = document.getElementById('credit-card-details');

    function toggleCreditCardDetails() {
        const checkedRadio = document.querySelector('input[name="payMethod"]:checked');
        if (checkedRadio && checkedRadio.value === '0') {
            creditCardDetailsDiv.style.display = 'block';
        } else {
            creditCardDetailsDiv.style.display = 'none';
        }
    }

    if (paymentMethodRadios.length > 0) {
        paymentMethodRadios.forEach(radio => {
            radio.addEventListener('change', toggleCreditCardDetails);
        });
        toggleCreditCardDetails();
    }

    // ================== 處理信用卡驗證 ==================
    const ccNumberInput = document.getElementById('cc-number');
    const ccNumberError = document.getElementById('cc-number-error');

    if (ccNumberInput) {
        ccNumberInput.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/[^\d]/g, '').replace(/(.{4})/g, '$1 ').trim();
            const isValid = creditcard.isValid(e.target.value);
            if (isValid) {
                ccNumberError.textContent = '卡號有效';
                ccNumberError.style.color = 'green';
            } else {
                ccNumberError.textContent = '無效的卡號';
                ccNumberError.style.color = 'red';
            }
        });
    }

    // ================== 處理會員地址帶入 ==================
    const fillAddressBtn = document.getElementById('fill-member-address-btn');
    const ordAddrInput = document.getElementById('ordAddr');

    if (fillAddressBtn) {
        fillAddressBtn.addEventListener('click', function() {
            const city = this.dataset.city;
            const cityArea = this.dataset.cityarea;
            const address = this.dataset.address;

            if (city && cityArea && address &&
                city.trim() !== '' && cityArea.trim() !== '' && address.trim() !== '') {

                const fullAddress = `${city}${cityArea}${address}`;
                ordAddrInput.value = fullAddress;
                console.log('✅ 成功帶入會員地址:', fullAddress);
            } else {
                alert('無法獲取會員資料，請確認您已登入。');
                console.warn('❌ 會員資料不完整:', { city, cityArea, address });
            }
        });
    }
});
