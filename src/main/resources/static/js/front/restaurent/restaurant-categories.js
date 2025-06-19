// 餐廳分類頁面的JavaScript功能

document.addEventListener('DOMContentLoaded', function() {
    // 分類切換功能
    const categoryItems = document.querySelectorAll('.category-item');
    const restaurantCards = document.querySelectorAll('.restaurant-card');

    categoryItems.forEach(item => {
        item.addEventListener('click', function() {
            // 更新分類按鈕狀態
            categoryItems.forEach(cat => cat.classList.remove('active'));
            this.classList.add('active');

            // 獲取選中的分類
            const selectedCategory = this.textContent.trim();

            // 篩選餐廳
            filterRestaurants(selectedCategory);
        });
    });

    // 餐廳篩選函數
    function filterRestaurants(category) {
        restaurantCards.forEach(card => {
            const cardCategory = card.querySelector('.cuisine-tag').textContent;
            if (category === '全部餐廳' || cardCategory === category) {
                // 顯示符合分類的餐廳
                card.style.display = '';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            } else {
                // 隱藏不符合分類的餐廳
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    card.style.display = 'none';
                }, 300);
            }
        });
    }

    // 圖片載入錯誤處理
    const restaurantImages = document.querySelectorAll('.restaurant-image');
    restaurantImages.forEach(img => {
        img.onerror = function() {
            this.src = 'images/restaurants/placeholder.svg';
        };
    });

    // 餐廳卡片懸停效果
    restaurantCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // 搜尋功能
    const searchInput = document.querySelector('.search-input');
    let searchTimeout;

    searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            const searchTerm = this.value.toLowerCase();
            
            restaurantCards.forEach(card => {
                const restaurantName = card.querySelector('.restaurant-name').textContent.toLowerCase();
                const restaurantCategory = card.querySelector('.restaurant-category').textContent.toLowerCase();
                
                if (restaurantName.includes(searchTerm) || restaurantCategory.includes(searchTerm)) {
                    card.style.display = '';
                    setTimeout(() => {
                        card.style.opacity = '1';
                        card.style.transform = 'translateY(0)';
                    }, 10);
                } else {
                    card.style.opacity = '0';
                    card.style.transform = 'translateY(20px)';
                    setTimeout(() => {
                        card.style.display = 'none';
                    }, 300);
                }
            });
        }, 300);
    });

    // 評分星星動態效果
    const ratingStars = document.querySelectorAll('.rating-stars');
    ratingStars.forEach(stars => {
        stars.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.1)';
        });

        stars.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    });
});