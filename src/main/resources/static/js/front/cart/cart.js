// public/js/front/cart/cart.js
document.addEventListener('DOMContentLoaded', () => {

    // ================== 步驟1：獲取所有需要的 DOM 元素 (維持不變) ==================
    const cartForm = document.getElementById('cart-form');
    if (!cartForm) return;

    const shopGroups = document.querySelectorAll('.shop-group');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    const shopSelectAllCheckboxes = document.querySelectorAll('.shop-select-all');
    const quantityButtons = document.querySelectorAll('.quantity-btn');
    const deleteButtons = document.querySelectorAll('.delete-btn');
    const deleteConfirmModal = document.getElementById('delete-confirm-modal');
    const cancelDeleteBtn = document.getElementById('cancel-delete-btn');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');

    // 【修正 1】: 變數更名，以符合 prodId 的語意
    let prodIdToDelete = null;

    // ================== 核心功能區 (維持不變) ==================

    function updateSummary() {
        let selectedCount = 0;
        let selectedSubtotal = 0;

        document.querySelectorAll('.item-checkbox:checked').forEach(checkbox => {
            const item = checkbox.closest('.cart-item');
            const price = parseFloat(item.dataset.price);
            const quantity = parseInt(item.dataset.quantity, 10);
            selectedCount += quantity;
            selectedSubtotal += price * quantity;
        });

        const shippingFee = selectedSubtotal > 0 && selectedSubtotal < 500 ? 60 : 0;
        const finalTotal = selectedSubtotal + shippingFee;

        document.getElementById('selected-items-count').textContent = selectedCount;
        document.getElementById('selected-subtotal').textContent = `NT$ ${selectedSubtotal.toLocaleString()}`;
        document.getElementById('shipping-fee').textContent = `NT$ ${shippingFee}`;
        document.getElementById('final-total').textContent = `NT$ ${finalTotal.toLocaleString()}`;

        const checkoutBtn = document.getElementById('checkout-btn');
        if (checkoutBtn) {
            checkoutBtn.disabled = selectedSubtotal <= 0;
        }
    }

    function manageShopSelectionLock() {
        const firstSelectedCheckbox = document.querySelector('.item-checkbox:checked');
        const activeShopGroup = firstSelectedCheckbox ? firstSelectedCheckbox.closest('.shop-group') : null;

        shopGroups.forEach(group => {
            const isGroupActive = (group === activeShopGroup);
            const shouldBeEnabled = !activeShopGroup || isGroupActive;

            group.style.opacity = shouldBeEnabled ? '1' : '0.5';
            group.style.pointerEvents = shouldBeEnabled ? 'auto' : 'none';
            group.querySelectorAll('input[type="checkbox"]').forEach(cb => {
                if (!shouldBeEnabled) {
                    cb.disabled = true;
                } else if (group.style.pointerEvents === 'auto') { // 確保重新啟用
                    cb.disabled = false;
                }
            });
        });
    }

    function updateShopSelectAllState(shopGroup) {
        if (!shopGroup) return;
        const shopSelectAll = shopGroup.querySelector('.shop-select-all');
        const allItemsInShop = shopGroup.querySelectorAll('.item-checkbox');
        const allChecked = Array.from(allItemsInShop).every(cb => cb.checked);
        shopSelectAll.checked = allChecked;
    }

    // ================== 事件監聽器綁定 (checkbox 部分維持不變) ==================

    itemCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            updateShopSelectAllState(checkbox.closest('.shop-group'));
            manageShopSelectionLock();
            updateSummary();
        });
    });

    shopSelectAllCheckboxes.forEach(shopSelectAll => {
        shopSelectAll.addEventListener('change', (event) => {
            const shopGroup = event.target.closest('.shop-group');
            const itemsInShop = shopGroup.querySelectorAll('.item-checkbox');
            itemsInShop.forEach(itemCheckbox => {
                itemCheckbox.checked = event.target.checked;
            });
            manageShopSelectionLock();
            updateSummary();
        });
    });

    // ================== 【核心修正區域】 ==================

    // 監聽數量按鈕
    quantityButtons.forEach(button => {
        button.addEventListener('click', function() {
            const itemEl = this.closest('.cart-item');
            const quantityInput = itemEl.querySelector('.quantity-input');
            let newQuantity = parseInt(quantityInput.value, 10);

            if (this.classList.contains('plus')) newQuantity = Math.min(99, newQuantity + 1);
            else if (this.classList.contains('minus')) newQuantity = Math.max(1, newQuantity - 1);

            if (newQuantity === parseInt(quantityInput.value, 10)) return;

            // 【修正 2】: 從 data-shop-id 改為 data-prod-id
            const prodId = this.dataset.prodId;
            // 【修正 3】: 送出的參數名稱改為 prodId 與 newQuantity
            const formData = new URLSearchParams({
                prodId: prodId,
                newQuantity: newQuantity
            });

            // 使用 fetch 非同步更新後端
            fetch('/cart/updateQuantity', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData
            })
                .then(res => {
                    if (res.ok) {
                        // 非同步更新成功後，直接更新前端 UI，無需重新載入頁面
                        itemEl.dataset.quantity = newQuantity;
                        quantityInput.value = newQuantity;
                        const price = parseFloat(itemEl.dataset.price);
                        itemEl.querySelector('.subtotal-value').textContent = `NT$ ${(price * newQuantity).toLocaleString()}`;
                        updateSummary(); // 重新計算總計
                    } else {
                        alert('更新數量失敗，請重試');
                        // 如果失敗，可以將數量恢復原狀
                        quantityInput.value = itemEl.dataset.quantity;
                    }
                })
                .catch(() => alert('網路連線錯誤，請稍後再試'));
        });
    });

    // 監聽刪除按鈕與燈箱
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 【修正 4】: 從 data-shop-id 改為 data-prod-id，並賦值給新變數
            prodIdToDelete = this.dataset.prodId;
            deleteConfirmModal.classList.add('active');
        });
    });

    cancelDeleteBtn.addEventListener('click', () => {
        deleteConfirmModal.classList.remove('active');
        prodIdToDelete = null; // 清空待刪除ID
    });

    confirmDeleteBtn.addEventListener('click', () => {
        if (!prodIdToDelete) return;
        // 【修正 5】: 送出的參數名稱改為 prodId
        const formData = new URLSearchParams({ prodId: prodIdToDelete });

        fetch('/cart/delete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    // 【修正 6】: DOM 選擇器從 data-shop-id 改為 data-prod-id
                    const itemToRemove = document.querySelector(`.delete-btn[data-prod-id="${prodIdToDelete}"]`).closest('.cart-item');
                    if (itemToRemove) {
                        const shopGroup = itemToRemove.closest('.shop-group');
                        itemToRemove.remove();
                        // 檢查店家是否還有其他商品
                        if (shopGroup && shopGroup.querySelectorAll('.cart-item').length === 0) {
                            shopGroup.remove(); // 如果店家已空，整個移除
                        }
                        updateSummary(); // 更新總計
                        manageShopSelectionLock(); // 重新檢查跨店鎖定狀態
                    }
                } else {
                    alert('刪除失敗，請重試');
                }
            })
            .catch(() => alert('網路連線錯誤，請稍後再試'))
            .finally(() => {
                deleteConfirmModal.classList.remove('active');
                prodIdToDelete = null; // 無論成功失敗都清空ID
            });
    });

    // ================== 頁面初始加載 ==================
    updateSummary();
    manageShopSelectionLock();
});
