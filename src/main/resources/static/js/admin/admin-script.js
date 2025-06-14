// 後台管理系統 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 側邊欄切換功能
    const sidebarToggle = document.querySelector('.sidebar-toggle');
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');
    
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('show');
        });
    }
    
    // 響應式側邊欄處理
    function handleResize() {
        if (window.innerWidth < 992) {
            if (sidebar) sidebar.classList.remove('show');
            if (mainContent) mainContent.style.marginLeft = '0';
        } else {
            if (mainContent) mainContent.style.marginLeft = '250px';
        }
    }
    
    window.addEventListener('resize', handleResize);
    handleResize();
    
    
   
    
    // 登出功能
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            // 清除 sessionStorage 中的 adminInfo
            sessionStorage.removeItem('adminInfo');
            sessionStorage.removeItem('loginRedirectedFromLoginPage');
            window.location.href = 'admin-login.html';
        });
    }
});