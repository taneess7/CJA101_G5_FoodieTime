// public/js/front/cart/cart.js

document.addEventListener('DOMContentLoaded', () => {

    // ================== 步驟1：獲取所有需要的 DOM 元素 ==================
    const cartForm = document.getElementById('cart-form');
    if (!cartForm) return; // 如果不是購物車頁面，直接退出

    // 複選框相關元素
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    const shopSelectAllCheckboxes = document.querySelectorAll('.shop-select-all');

    // 訂單摘要元素
    const selectedCountEl = document.getElementById('selected-items-count');
    const selectedSubtotalEl = document.getElementById('selected-subtotal');
    const shippingFeeEl = document.getElementById('shipping-fee');
    const finalTotalEl = document.getElementById('final-total');
    const checkoutBtn = document.getElementById('checkout-btn');

    // (新增) 數量控制按鈕
    const quantityButtons = document.querySelectorAll('.quantity-btn');

    /**
     * ================== 核心功能 A：更新訂單摘要 (維持不變) ==================
     * 遍歷所有已勾選的商品，重新計算總數量、小計、運費和總金額。
     */
    function updateSummary() {
        let selectedCount = 0;
        let selectedSubtotal = 0;

        itemCheckboxes.forEach(checkbox => {
            if (checkbox.checked) {
                const item = checkbox.closest('.cart-item');
                // 從 data-* 屬性讀取最新的數據
                const price = parseFloat(item.dataset.price);
                const quantity = parseInt(item.dataset.quantity, 10);

                selectedCount += quantity;
                selectedSubtotal += price * quantity;
            }
        });

        const shippingFee = selectedSubtotal > 0 && selectedSubtotal < 500 ? 60 : 0;
        const finalTotal = selectedSubtotal + shippingFee;

        selectedCountEl.textContent = selectedCount;
        selectedSubtotalEl.textContent = `NT$ ${selectedSubtotal.toLocaleString()}`;
        shippingFeeEl.textContent = `NT$ ${shippingFee}`;
        finalTotalEl.textContent = `NT$ ${finalTotal.toLocaleString()}`;
        checkoutBtn.disabled = selectedSubtotal <= 0;
    }

    /**
     * ================== 核心功能 B：(新增) 更新商品項目的 UI ==================
     * @param {HTMLElement} itemEl - .cart-item 元素
     * @param {number} newQuantity - 新的商品數量
     */
    function updateItemUI(itemEl, newQuantity) {
        const price = parseFloat(itemEl.dataset.price);
        const quantityInput = itemEl.querySelector('.quantity-input');
        const subtotalValue = itemEl.querySelector('.subtotal-value');

        // 1. 更新 data-quantity 屬性，這是 updateSummary 的數據來源
        itemEl.dataset.quantity = newQuantity;

        // 2. 更新輸入框中顯示的數字
        quantityInput.value = newQuantity;

        // 3. 更新該項目的小計
        subtotalValue.textContent = `NT$ ${(price * newQuantity).toLocaleString()}`;

        // 4. 重新計算整個訂單的總計
        updateSummary();
    }


    /**
     * ================== 事件監聽器綁定 ==================
     */

    // 1. 監聽單一商品複選框 (維持不變)
    itemCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            updateSummary();
            updateShopSelectAllState(checkbox.closest('.shop-group'));
        });
    });

    // 2. 監聽店家的"全選"複選框 (維持不變)
    shopSelectAllCheckboxes.forEach(shopSelectAll => {
        shopSelectAll.addEventListener('change', (event) => {
            const shopGroup = event.target.closest('.shop-group');
            const itemsInShop = shopGroup.querySelectorAll('.item-checkbox');
            itemsInShop.forEach(itemCheckbox => {
                itemCheckbox.checked = event.target.checked;
            });
            updateSummary();
        });
    });

    // 3. 監聽店家標題點擊 (維持不變)
    document.querySelectorAll('.shop-header').forEach(header => {
        header.addEventListener('click', event => {
            if (event.target.tagName !== 'INPUT') {
                const checkbox = header.querySelector('.shop-select-all');
                checkbox.checked = !checkbox.checked;
                checkbox.dispatchEvent(new Event('change'));
            }
        });
    });

    // 4. 監聽所有 "+" 和 "-" 按鈕的點擊事件
    quantityButtons.forEach(button => {
        button.addEventListener('click', () => {
            const itemEl = button.closest('.cart-item');
            const quantityInput = itemEl.querySelector('.quantity-input');
            const currentQuantity = parseInt(quantityInput.value, 10);

            let newQuantity = currentQuantity;

            // 判斷是加還是減
            if (button.classList.contains('plus')) {
                newQuantity = Math.min(99, currentQuantity + 1); // 最多99
            } else if (button.classList.contains('minus')) {
                newQuantity = Math.max(1, currentQuantity - 1); // 最少1
            }

            // 如果數量沒有變化，則不做任何事
            if (newQuantity === currentQuantity) {
                return;
            }

            // 使用 AJAX 在背景更新後端資料
            const shopId = button.dataset.shopId;
            const formData = new FormData();
            formData.append('shopId', shopId);
            formData.append('newQuantity', newQuantity);

            fetch('/cart/updateQuantity', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        // 如果後端成功更新，才更新前端UI
                        updateItemUI(itemEl, newQuantity);
                    } else {
                        // 如果後端更新失敗，可以給出提示
                        alert('更新數量失敗，請稍後再試！');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('網路錯誤，更新數量失敗！');
                });
        });
    });


    /**
     * ================== 輔助函式 (維持不變) ==================
     */
    function updateShopSelectAllState(shopGroup) {
        const shopSelectAll = shopGroup.querySelector('.shop-select-all');
        const allItemsInShop = shopGroup.querySelectorAll('.item-checkbox');
        const allChecked = Array.from(allItemsInShop).every(cb => cb.checked);
        shopSelectAll.checked = allChecked;
    }

    // ================== 頁面初始加載 ==================
    updateSummary();
});
