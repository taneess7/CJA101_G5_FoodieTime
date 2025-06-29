// 引入 SweetAlert2（如果頁面沒引入，這裡自動加）
(function() {
    if (!window.Swal) {
        var script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/sweetalert2@11';
        document.head.appendChild(script);
    }
})();

// 通知中心類別
class NotificationCenter {
    constructor() {
        this.notifications = [];
        this.createNotificationCenter();
        this.requestNotificationPermission();
    }
    
    createNotificationCenter() {
        const center = document.createElement('div');
        center.id = 'notification-center';
        center.innerHTML = `
            <div class="notification-header">
                <h3>🔔 通知中心</h3>
                <span class="notification-count">0</span>
                <button class="notification-toggle">📋</button>
            </div>
            <div class="notification-list"></div>
        `;
        center.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            width: 350px;
            max-height: 500px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            z-index: 9999;
            font-family: Arial, sans-serif;
            display: none;
        `;
        document.body.appendChild(center);
        
        // 切換顯示/隱藏
        center.querySelector('.notification-toggle').onclick = () => {
            center.style.display = center.style.display === 'none' ? 'block' : 'none';
        };
    }
    
    requestNotificationPermission() {
        if (Notification.permission === "default") {
            Notification.requestPermission();
        }
    }
    
    addNotification(title, text, type = 'info') {
        const notification = {
            id: Date.now(),
            title,
            text,
            type,
            time: new Date()
        };
        
        this.notifications.unshift(notification);
        if (this.notifications.length > 10) {
            this.notifications.pop(); // 只保留最近10條
        }
        
        this.updateDisplay();
        this.showToast(title, text, type);
        this.showBrowserNotification(title, text);
        this.playNotificationSound();
    }
    
    updateDisplay() {
        const list = document.querySelector('.notification-list');
        const count = document.querySelector('.notification-count');
        
        count.textContent = this.notifications.length;
        list.innerHTML = this.notifications.map(n => `
            <div class="notification-item ${n.type}">
                <div class="notification-title">${n.title}</div>
                <div class="notification-text">${n.text}</div>
                <div class="notification-time">${n.time.toLocaleTimeString()}</div>
            </div>
        `).join('');
    }
    
    showToast(title, text, type = 'info') {
        if (window.Swal) {
            const icons = {
                'info': 'info',
                'success': 'success',
                'warning': 'warning',
                'error': 'error'
            };
            
            Swal.fire({
                icon: icons[type] || 'info',
                title: title,
                text: text,
                // timer: 4000,
                toast: true,
                position: 'top-end',
                showConfirmButton: true,
                timerProgressBar: true,
                background: '#fff',
                backdrop: false
            });
        } else {
            alert(title + ': ' + text);
        }
    }
    
    showBrowserNotification(title, text) {
        if (Notification.permission === "granted") {
            new Notification(title, {
                body: text,
                icon: '/images/icons/logo.png',
                badge: '/images/icons/logo.png',
                tag: 'foodietime-notification'
            });
        }
    }
    
    playNotificationSound() {
        try {
            const audio = new Audio('data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBSuBzvLZiTYIG2m98OScTgwOUarm7blmGgU7k9n1unEiBC13yO/eizEIHWq+8+OWT');
            audio.volume = 0.3;
            audio.play();
        } catch (e) {
            // 忽略音效錯誤
        }
    }
}

// 全域通知中心實例
let notificationCenter;

function connectWebSocketNotify(options) {
    // 初始化通知中心
    if (!notificationCenter) {
        notificationCenter = new NotificationCenter();
        window.notificationCenter = notificationCenter;
    }
    
    // options: {storeId, memId, gbId}
    var socket = new SockJS('/ws');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        if (options.storeId) {
            stompClient.subscribe('/topic/store/' + options.storeId, function (message) {
                notificationCenter.addNotification('商家通知', message.body, 'info');
            });
        }
        if (options.memId) {
            stompClient.subscribe('/topic/member/' + options.memId, function (message) {
                notificationCenter.addNotification('團購通知', message.body, 'success');
            });
        }
        if (options.gbId) {
            stompClient.subscribe('/topic/group/' + options.gbId, function (message) {
                notificationCenter.addNotification('團員通知', message.body, 'info');
            });
        }
    });
}

// 保留舊的 showNotify 函數以向後相容
function showNotify(title, text) {
    if (!notificationCenter) {
        notificationCenter = new NotificationCenter();
    }
    notificationCenter.addNotification(title, text, 'info');
}

document.getElementById('notification-center').style.display = 'block'; 