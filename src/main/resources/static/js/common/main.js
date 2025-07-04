/**
 * FoodieTime 食刻 - 主要JavaScript文件
 * 實現網站的基本互動功能
 */

// 請將這段程式碼加入到您現有的 DOMContentLoaded 事件監聽器中
document.addEventListener("DOMContentLoaded", () => {
    // --- 您原有的搜尋框腳本 (請保留) ---
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.addEventListener("keypress", function (e) {
            if (e.key === "Enter") {
                const keyword = this.value.trim();
                if (keyword !== "") {
                    window.location.href = `/category/search?keyword=${encodeURIComponent(keyword)}`;
                }
            }
        });
    }

    // =====【新增】響應式導覽列 (漢堡選單) 控制 =====
    const menuToggle = document.querySelector('.menu-toggle');
    const mainNav = document.querySelector('.main-nav');

    if (menuToggle && mainNav) {
        menuToggle.addEventListener('click', () => {
            // 在 .main-nav 上切換 is-open class，來觸發 CSS 的顯示/隱藏
            mainNav.classList.toggle('is-open');
        });
    }
});
