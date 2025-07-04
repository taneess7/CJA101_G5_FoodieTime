// public/js/front/cart/cart.js
document.addEventListener('DOMContentLoaded', () => {

    // ================== 步驟1：獲取所有需要的 DOM 元素 ==================
    const cartForm = document.getElementById('cart-form');
    if (!cartForm) return;

    // 將所有需要操作的元素集中宣告
    const shopGroups = document.querySelectorAll('.shop-group');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    const shopSelectAllCheckboxes = document.querySelectorAll('.shop-select-all');
    const quantityButtons = document.querySelectorAll('.quantity-btn');
    const deleteButtons = document.querySelectorAll('.delete-btn');
    const deleteConfirmModal = document.getElementById('delete-confirm-modal');
    const cancelDeleteBtn = document.getElementById('cancel-delete-btn');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');

    let shopIdToDelete = null;

    // ================== 核心功能區 ==================

    /**
     * 更新訂單摘要（小計、運費、總計）
     */
    function updateSummary() {
        let selectedCount = 0;
        let selectedSubtotal = 0;

        // 只計算已勾選的商品
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
        document.getElementById('checkout-btn').disabled = selectedSubtotal <= 0;
    }

    /**
     * ★★【新增】管理跨店勾選限制的核心函式 ★★
     */
    function manageShopSelectionLock() {
        // 1. 找出第一個被勾選的商品屬於哪個店家
        const firstSelectedCheckbox = document.querySelector('.item-checkbox:checked');
        const activeShopGroup = firstSelectedCheckbox ? firstSelectedCheckbox.closest('.shop-group') : null;

        // 2. 遍歷所有店家群組
        shopGroups.forEach(group => {
            const isGroupActive = (group === activeShopGroup);

            // 如果沒有任何店家被選中，或當前店家是活躍店家，則啟用
            if (!activeShopGroup || isGroupActive) {
                group.style.opacity = '1';
                group.style.pointerEvents = 'auto';
                group.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.disabled = false);
            } else { // 否則，禁用非活躍的店家
                group.style.opacity = '0.5';
                group.style.pointerEvents = 'none'; // 讓整個區塊不可點擊
                group.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.disabled = true);
            }
        });
    }

    /**
     * 更新指定店家「全選」按鈕的狀態
     * @param {HTMLElement} shopGroup - 要檢查的店家群組元素
     */
    function updateShopSelectAllState(shopGroup) {
        if (!shopGroup) return;
        const shopSelectAll = shopGroup.querySelector('.shop-select-all');
        const allItemsInShop = shopGroup.querySelectorAll('.item-checkbox');
        // 檢查該店所有商品是否都已勾選
        const allChecked = Array.from(allItemsInShop).every(cb => cb.checked);
        shopSelectAll.checked = allChecked;
    }


    // ================== 事件監聽器綁定 ==================

    // 【修正】監聽單一商品複選框
    itemCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            updateShopSelectAllState(checkbox.closest('.shop-group'));
            manageShopSelectionLock();
            updateSummary();
        });
    });

    // 【修正】監聽店家「全選」複選框
    shopSelectAllCheckboxes.forEach(shopSelectAll => {
        shopSelectAll.addEventListener('change', (event) => {
            const shopGroup = event.target.closest('.shop-group');
            const itemsInShop = shopGroup.querySelectorAll('.item-checkbox');
            // 將該店所有商品的勾選狀態與「全選」按鈕同步
            itemsInShop.forEach(itemCheckbox => {
                itemCheckbox.checked = event.target.checked;
            });
            manageShopSelectionLock();
            updateSummary();
        });
    });

    // 監聽數量按鈕 (維持不變)
    quantityButtons.forEach(button => {
        button.addEventListener('click', function() {
            const itemEl = this.closest('.cart-item');
            const quantityInput = itemEl.querySelector('.quantity-input');
            let newQuantity = parseInt(quantityInput.value, 10);

            if (this.classList.contains('plus')) newQuantity = Math.min(99, newQuantity + 1);
            else if (this.classList.contains('minus')) newQuantity = Math.max(1, newQuantity - 1);

            if (newQuantity === parseInt(quantityInput.value, 10)) return;

            const shopId = this.dataset.shopId;
            fetch('/cart/updateQuantity', {
                method: 'POST',
                body: new URLSearchParams({ shopId: shopId, newQuantity: newQuantity })
            })
                .then(res => {
                    if (res.ok) {
                        // 更新成功後，更新UI
                        itemEl.dataset.quantity = newQuantity;
                        quantityInput.value = newQuantity;
                        const price = parseFloat(itemEl.dataset.price);
                        itemEl.querySelector('.subtotal-value').textContent = `NT$ ${(price * newQuantity).toLocaleString()}`;
                        updateSummary();
                    } else {
                        alert('更新數量失敗');
                    }
                })
                .catch(() => alert('網路錯誤'));
        });
    });

    // 監聽刪除按鈕與燈箱 (維持不變)
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            shopIdToDelete = this.dataset.shopId;
            deleteConfirmModal.classList.add('active');
        });
    });

    cancelDeleteBtn.addEventListener('click', () => {
        deleteConfirmModal.classList.remove('active');
        shopIdToDelete = null;
    });

    confirmDeleteBtn.addEventListener('click', () => {
        if (!shopIdToDelete) return;

        fetch('/cart/delete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ shopId: shopIdToDelete })
        })
            .then(response => {
                if (response.ok) {
                    const itemToRemove = document.querySelector(`.delete-btn[data-shop-id="${shopIdToDelete}"]`).closest('.cart-item');
                    if (itemToRemove) {
                        const shopGroup = itemToRemove.closest('.shop-group');
                        itemToRemove.remove(); // 直接移除商品
                        if (shopGroup && shopGroup.querySelectorAll('.cart-item').length === 0) {
                            shopGroup.remove(); // 如果店家已空，移除店家
                        }
                        updateSummary(); // 更新總計
                        manageShopSelectionLock();
                    }
                } else {
                    alert('刪除失敗');
                }
            })
            .catch(() => alert('網路錯誤'))
            .finally(() => {
                deleteConfirmModal.classList.remove('active');
                shopIdToDelete = null;
            });
    });

    // ================== 頁面初始加載 ==================
    updateSummary();
    manageShopSelectionLock();
});
