document.addEventListener('DOMContentLoaded', function() {

    // ================== 【核心功能】動態更新優惠券折扣與總金額 (偵錯版) ==================
    console.log("【偵錯】checkout.js 已載入。"); // 檢查 JS 是否有被執行

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

        function updatePrice() {
            console.log("【偵錯】updatePrice 函數被呼叫。"); // 檢查事件是否有觸發

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
                    console.log("【偵錯】條件不符，顯示提示訊息。");
                    discountDisplayElem.innerHTML = `
                        <span></span>
                        <span class="dynamic-error-text">未達最低消費 NT$ ${minSpendAmount}</span>
                    `;
                    const errorTextElem = discountDisplayElem.querySelector('.dynamic-error-text');
                    errorTextElem.style.color = 'red';
                    errorTextElem.style.fontSize = '0.9em';
                }
            } else {
                console.log("【偵錯】未選擇優惠券或折扣為 0。");
            }

            hiddenCouponInput.value = couponSelect.value;
            const newTotal = originalSubtotal + originalShipping - finalDiscount;
            totalElem.textContent = `NT$ ${newTotal >= 0 ? newTotal : 0}`;
            console.log(`【偵錯】更新後總金額為: ${newTotal}`);
        }

        couponSelect.addEventListener('change', updatePrice);
        updatePrice();
    } else {
        // 如果有元素找不到，這裡會告訴我們是哪個
        console.error("【錯誤】找不到必要的 HTML 元素！請檢查以下 ID 是否存在: 'coupon-select', 'selected-coupon-id-input', 'checkout-subtotal', 'checkout-shipping', 'dynamic-discount-display', 'checkout-total'");
    }
    // ================================================================


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
        // 初始化顯示狀態
        toggleCreditCardDetails();
    }
    // ================================================================


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
    // ================================================================
    /**
     * 處理「一鍵帶入會員地址」按鈕的點擊事件。
     * 使用 data 屬性方案：從按鈕的 data-* 屬性讀取會員資料。
     */
    const fillAddressBtn = document.getElementById('fill-member-address-btn');
    const ordAddrInput = document.getElementById('ordAddr');

    if (fillAddressBtn) {
        fillAddressBtn.addEventListener('click', function() {

            // ★★★ 【核心邏輯】★★★
            // 從按鈕的 data 屬性讀取會員資料
            // this.dataset 會自動將 data-city 轉換為 dataset.city
            const city = this.dataset.city;
            const cityArea = this.dataset.cityarea;  // data-cityarea -> dataset.cityarea
            const address = this.dataset.address;

            // 檢查會員地址資料是否都存在且不為空
            if (city && cityArea && address &&
                city.trim() !== '' && cityArea.trim() !== '' && address.trim() !== '') {

                // 組合完整的地址字串
                const fullAddress = `${city}${cityArea}${address}`;

                // 將組合好的地址填入 "外送地址" 輸入框
                ordAddrInput.value = fullAddress;

                // 可選：給使用者一個成功的視覺回饋
                console.log('✅ 成功帶入會員地址:', fullAddress);

            } else {
                alert('無法獲取會員資料，請確認您已登入。');
                console.warn('❌ 會員資料不完整:', { city, cityArea, address });
            }
        });
    }
    // ====================================================================
});

