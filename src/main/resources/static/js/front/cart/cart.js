// public/js/front/cart/cart.js

document.addEventListener('DOMContentLoaded', () => {

    // ================== 步驟1：獲取所有需要的 DOM 元素 ==================
    const cartForm = document.getElementById('cart-form');
    if (!cartForm) return;

    // ... (其他既有元素宣告維持不變) ...
    const deleteButtons = document.querySelectorAll('.delete-btn');

    // ★★【新增】獲取確認燈箱相關元素 ★★
    const deleteConfirmModal = document.getElementById('delete-confirm-modal');
    const cancelDeleteBtn = document.getElementById('cancel-delete-btn');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');

    // 用於暫存將要刪除的 shopId
    let shopIdToDelete = null;

    // ... (updateSummary, updateItemUI 等函式維持不變) ...
    function updateSummary() {
        let selectedCount = 0;
        let selectedSubtotal = 0;
        const currentItemCheckboxes = document.querySelectorAll('.item-checkbox');
        currentItemCheckboxes.forEach(checkbox => {
            if (checkbox.checked) {
                const item = checkbox.closest('.cart-item');
                const price = parseFloat(item.dataset.price);
                const quantity = parseInt(item.dataset.quantity, 10);
                selectedCount += quantity;
                selectedSubtotal += price * quantity;
            }
        });
        if (currentItemCheckboxes.length === 0) {
            const container = document.querySelector('.cart-items-container');
            const sidebar = document.querySelector('.cart-sidebar');
            if (container) {
                container.innerHTML = `<div class="empty-cart-message"><i class="material-icons-outlined empty-cart-icon">shopping_cart</i><h2>您的購物車是空的</h2><a href="/category/food-categories" class="btn btn-primary">開始探索</a></div>`;
            }
            if (sidebar) sidebar.style.display = 'none';
            return;
        }
        const shippingFee = selectedSubtotal > 0 && selectedSubtotal < 500 ? 60 : 0;
        const finalTotal = selectedSubtotal + shippingFee;
        const selectedCountEl = document.getElementById('selected-items-count');
        const selectedSubtotalEl = document.getElementById('selected-subtotal');
        const shippingFeeEl = document.getElementById('shipping-fee');
        const finalTotalEl = document.getElementById('final-total');
        const checkoutBtn = document.getElementById('checkout-btn');
        selectedCountEl.textContent = selectedCount;
        selectedSubtotalEl.textContent = `NT$ ${selectedSubtotal.toLocaleString()}`;
        shippingFeeEl.textContent = `NT$ ${shippingFee}`;
        finalTotalEl.textContent = `NT$ ${finalTotal.toLocaleString()}`;
        checkoutBtn.disabled = selectedSubtotal <= 0;
    }


    /**
     * ================== ★★【修改】事件監聽器綁定 ★★ ==================
     */

    // ... (複選框、數量按鈕的監聽器維持不變) ...

    // 監聽所有刪除按鈕的點擊事件
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 暫存 shopId
            shopIdToDelete = this.dataset.shopId;
            // 顯示燈箱
            deleteConfirmModal.classList.add('active');
        });
    });

    // 監聽燈箱的「取消」按鈕
    cancelDeleteBtn.addEventListener('click', () => {
        deleteConfirmModal.classList.remove('active');
        shopIdToDelete = null; // 清除暫存的ID
    });

    // 監聽燈箱的「確認刪除」按鈕
    confirmDeleteBtn.addEventListener('click', () => {
        if (!shopIdToDelete) return; // 如果沒有ID，不執行任何操作

        fetch('/cart/delete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ shopId: shopIdToDelete })
        })
            .then(response => {
                if (response.ok) {
                    // 從DOM中找到對應的商品元素並移除
                    const itemToRemove = document.querySelector(`.delete-btn[data-shop-id="${shopIdToDelete}"]`).closest('.cart-item');
                    if (itemToRemove) {
                        itemToRemove.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
                        itemToRemove.style.opacity = '0';
                        itemToRemove.style.transform = 'translateX(-20px)';

                        setTimeout(() => {
                            const shopGroup = itemToRemove.closest('.shop-group');
                            itemToRemove.remove();
                            // 檢查店家是否還有商品
                            if (shopGroup && shopGroup.querySelectorAll('.cart-item').length === 0) {
                                shopGroup.remove();
                            }
                            updateSummary(); // 更新總計
                        }, 300);
                    }
                } else {
                    alert('刪除失敗，請稍後再試。');
                }
            })
            .catch(error => {
                console.error('刪除錯誤:', error);
                alert('網路錯誤，刪除失敗。');
            })
            .finally(() => {
                // 無論成功或失敗，都關閉燈箱並清除ID
                deleteConfirmModal.classList.remove('active');
                shopIdToDelete = null;
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
