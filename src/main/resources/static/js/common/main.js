/**
 * FoodieTime 食刻 - 主要JavaScript文件
 * 實現網站的基本互動功能
 */

// 等待DOM完全加載
document.addEventListener('DOMContentLoaded', function() {
    // 初始化所有組件
    initHeader();
    initModals();
    initAnimations();
    initLazyLoading();
    initTooltips();
    
    // 如果在首頁，初始化首頁特定功能
    if (document.querySelector('.hero-section')) {
        initHeroAnimation();
        initFeaturesAnimation();
        initReviewsSlider();
    }
    
    // 註冊Service Worker (PWA支援)
    registerServiceWorker();
});

/**
 * 初始化頁頭功能
 */
function initHeader() {
    const header = document.querySelector('.app-header');
    const menuToggle = document.querySelector('.menu-toggle');
    const mainNav = document.querySelector('.main-nav');
    
    // 滾動時改變頁頭樣式
    window.addEventListener('scroll', function() {
        if (window.scrollY > 50) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });
    
    // 行動裝置選單切換
    if (menuToggle && mainNav) {
        menuToggle.addEventListener('click', function() {
            mainNav.classList.toggle('active');
            menuToggle.setAttribute('aria-expanded', 
                menuToggle.getAttribute('aria-expanded') === 'true' ? 'false' : 'true');
        });
    }
    
    // 搜尋功能
    const searchInput = document.querySelector('.search-input');
    const searchBar = document.querySelector('.search-bar');
    
    if (searchInput && searchBar) {
        // 搜尋輸入事件
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch(searchInput.value);
            }
        });
        
        // 語音搜尋
        const micIcon = document.querySelector('.mic-icon');
        if (micIcon && 'webkitSpeechRecognition' in window) {
            micIcon.addEventListener('click', startVoiceSearch);
        } else if (micIcon) {
            micIcon.style.display = 'none';
        }
        
        // 相機搜尋
        const cameraIcon = document.querySelector('.camera-icon');
        if (cameraIcon) {
            cameraIcon.addEventListener('click', startCameraSearch);
        }
    }
}

/**
 * 執行搜尋
 * @param {string} query - 搜尋關鍵字
 */
function performSearch(query) {
    if (!query.trim()) return;
    
    // 顯示載入指示器
    showLoading();
    
    // 模擬搜尋延遲
    setTimeout(function() {
        // 實際應用中，這裡會發送API請求
        hideLoading();
        
        // 導向搜尋結果頁面
        window.location.href = `map.html?q=${encodeURIComponent(query)}`;
    }, 500);
}

/**
 * 開始語音搜尋
 */
function startVoiceSearch() {
    if (!('webkitSpeechRecognition' in window)) {
        alert('您的瀏覽器不支援語音搜尋功能');
        return;
    }
    
    const recognition = new webkitSpeechRecognition();
    recognition.lang = 'zh-TW';
    recognition.continuous = false;
    recognition.interimResults = false;
    
    recognition.onstart = function() {
        // 顯示語音搜尋中的視覺反饋
        document.querySelector('.mic-icon').classList.add('recording');
    };
    
    recognition.onresult = function(event) {
        const transcript = event.results[0][0].transcript;
        document.querySelector('.search-input').value = transcript;
        performSearch(transcript);
    };
    
    recognition.onerror = function(event) {
        console.error('語音辨識錯誤:', event.error);
        document.querySelector('.mic-icon').classList.remove('recording');
    };
    
    recognition.onend = function() {
        document.querySelector('.mic-icon').classList.remove('recording');
    };
    
    recognition.start();
}

/**
 * 開始相機搜尋
 */
function startCameraSearch() {
    // 檢查瀏覽器支援
    if (!('mediaDevices' in navigator && 'getUserMedia' in navigator.mediaDevices)) {
        alert('您的瀏覽器不支援相機功能');
        return;
    }
    
    // 開啟相機模態框
    const modal = document.getElementById('camera-modal');
    const video = document.getElementById('camera-feed');
    const captureBtn = document.getElementById('capture-photo');
    const closeBtn = document.querySelector('#camera-modal .modal-close');
    
    if (!modal || !video) {
        console.error('相機模態框元素未找到');
        return;
    }
    
    // 開啟模態框
    openModal(modal);
    
    // 獲取相機權限並顯示預覽
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(function(stream) {
            video.srcObject = stream;
            video.play();
            
            // 拍照按鈕事件
            if (captureBtn) {
                captureBtn.addEventListener('click', function() {
                    // 創建畫布並截取影像
                    const canvas = document.createElement('canvas');
                    canvas.width = video.videoWidth;
                    canvas.height = video.videoHeight;
                    canvas.getContext('2d').drawImage(video, 0, 0);
                    
                    // 將影像轉為base64
                    const imageData = canvas.toDataURL('image/jpeg');
                    
                    // 關閉相機
                    const tracks = stream.getTracks();
                    tracks.forEach(track => track.stop());
                    
                    // 關閉模態框
                    closeModal(modal);
                    
                    // 處理圖片搜尋
                    processImageSearch(imageData);
                });
            }
            
            // 關閉按鈕事件
            if (closeBtn) {
                closeBtn.addEventListener('click', function() {
                    // 關閉相機
                    const tracks = stream.getTracks();
                    tracks.forEach(track => track.stop());
                    
                    // 關閉模態框
                    closeModal(modal);
                });
            }
        })
        .catch(function(error) {
            console.error('無法存取相機:', error);
            alert('無法存取相機，請確認您已授予相機權限');
            closeModal(modal);
        });
}

/**
 * 處理圖片搜尋
 * @param {string} imageData - Base64格式的圖片資料
 */
function processImageSearch(imageData) {
    // 顯示載入指示器
    showLoading();
    
    // 模擬圖片處理延遲
    setTimeout(function() {
        // 實際應用中，這裡會發送API請求進行圖像辨識
        hideLoading();
        
        // 模擬辨識結果
        const recognizedFood = '牛肉麵'; // 假設辨識結果
        
        // 更新搜尋框並執行搜尋
        document.querySelector('.search-input').value = recognizedFood;
        performSearch(recognizedFood);
    }, 1500);
}

/**
 * 初始化模態框功能
 */
function initModals() {
    // 獲取所有開啟模態框的按鈕
    const modalTriggers = document.querySelectorAll('[data-modal]');
    
    modalTriggers.forEach(trigger => {
        trigger.addEventListener('click', function() {
            const modalId = this.getAttribute('data-modal');
            const modal = document.getElementById(modalId);
            
            if (modal) {
                openModal(modal);
            }
        });
    });
    
    // 獲取所有關閉模態框的按鈕
    const closeButtons = document.querySelectorAll('.modal-close');
    
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const modal = this.closest('.modal');
            if (modal) {
                closeModal(modal);
            }
        });
    });
    
    // 點擊模態框背景關閉
    const modals = document.querySelectorAll('.modal');
    
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeModal(this);
            }
        });
    });
    
    // ESC鍵關閉模態框
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const activeModal = document.querySelector('.modal.active');
            if (activeModal) {
                closeModal(activeModal);
            }
        }
    });
}

/**
 * 開啟模態框
 * @param {HTMLElement} modal - 模態框元素
 */
function openModal(modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden'; // 防止背景滾動
    
    // 焦點管理 (無障礙)
    const focusableElements = modal.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
    if (focusableElements.length > 0) {
        focusableElements[0].focus();
    }
}

/**
 * 關閉模態框
 * @param {HTMLElement} modal - 模態框元素
 */
function closeModal(modal) {
    modal.classList.remove('active');
    document.body.style.overflow = ''; // 恢復背景滾動
}

/**
 * 顯示載入指示器
 */
function showLoading() {
    const loader = document.querySelector('.loading-indicator');
    if (loader) {
        loader.classList.add('active');
    }
}

/**
 * 隱藏載入指示器
 */
function hideLoading() {
    const loader = document.querySelector('.loading-indicator');
    if (loader) {
        loader.classList.remove('active');
    }
}

/**
 * 初始化首頁英雄區塊動畫
 */
function initHeroAnimation() {
    const heroContent = document.querySelector('.hero-content');
    const heroImage = document.querySelector('.hero-image');
    
    if (heroContent && heroImage) {
        heroContent.classList.add('fade-in');
        heroImage.classList.add('slide-up');
    }
}

/**
 * 初始化特色功能區塊動畫
 */
function initFeaturesAnimation() {
    const featureCards = document.querySelectorAll('.feature-card');
    
    if (featureCards.length > 0) {
        // 使用Intersection Observer API檢測元素進入視窗
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('slide-up');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.2 });
        
        featureCards.forEach(card => {
            observer.observe(card);
        });
    }
}

/**
 * 初始化評論輪播
 */
function initReviewsSlider() {
    const reviewsSlider = document.querySelector('.reviews-slider');
    
    if (reviewsSlider && typeof Swiper !== 'undefined') {
        new Swiper(reviewsSlider, {
            slidesPerView: 1,
            spaceBetween: 20,
            loop: true,
            autoplay: {
                delay: 5000,
                disableOnInteraction: false,
            },
            pagination: {
                el: '.swiper-pagination',
                clickable: true,
            },
            breakpoints: {
                640: {
                    slidesPerView: 2,
                },
                1024: {
                    slidesPerView: 3,
                },
            },
        });
    }
}

/**
 * 初始化懶加載圖片
 */
function initLazyLoading() {
    // 檢查瀏覽器是否支援Intersection Observer API
    if ('IntersectionObserver' in window) {
        const lazyImages = document.querySelectorAll('img[data-src]');
        
        const imageObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                    imageObserver.unobserve(img);
                }
            });
        });
        
        lazyImages.forEach(img => {
            imageObserver.observe(img);
        });
    } else {
        // 不支援Intersection Observer的降級處理
        const lazyImages = document.querySelectorAll('img[data-src]');
        
        function lazyLoad() {
            lazyImages.forEach(img => {
                if (img.getBoundingClientRect().top <= window.innerHeight && img.getBoundingClientRect().bottom >= 0) {
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                }
            });
            
            if (lazyImages.length === 0) { 
                document.removeEventListener('scroll', lazyLoad);
                window.removeEventListener('resize', lazyLoad);
                window.removeEventListener('orientationChange', lazyLoad);
            }
        }
        
        document.addEventListener('scroll', lazyLoad);
        window.addEventListener('resize', lazyLoad);
        window.addEventListener('orientationChange', lazyLoad);
        
        // 初始檢查
        lazyLoad();
    }
}

/**
 * 初始化提示框
 */
function initTooltips() {
    const tooltipTriggers = document.querySelectorAll('[data-tooltip]');
    
    tooltipTriggers.forEach(trigger => {
        trigger.addEventListener('mouseenter', function() {
            const tooltipText = this.getAttribute('data-tooltip');
            
            // 創建提示框元素
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip';
            tooltip.textContent = tooltipText;
            
            // 添加到DOM
            document.body.appendChild(tooltip);
            
            // 計算位置
            const triggerRect = this.getBoundingClientRect();
            const tooltipRect = tooltip.getBoundingClientRect();
            
            const top = triggerRect.top - tooltipRect.height - 10;
            const left = triggerRect.left + (triggerRect.width / 2) - (tooltipRect.width / 2);
            
            // 設置位置
            tooltip.style.top = `${top + window.scrollY}px`;
            tooltip.style.left = `${left}px`;
            
            // 顯示提示框
            setTimeout(() => {
                tooltip.classList.add('active');
            }, 10);
            
            // 儲存提示框參考
            this._tooltip = tooltip;
        });
        
        trigger.addEventListener('mouseleave', function() {
            if (this._tooltip) {
                this._tooltip.classList.remove('active');
                
                // 移除提示框
                setTimeout(() => {
                    if (this._tooltip.parentNode) {
                        this._tooltip.parentNode.removeChild(this._tooltip);
                    }
                    this._tooltip = null;
                }, 200);
            }
        });
    });
}

/**
 * 初始化動畫效果
 */
function initAnimations() {
    // 使用Intersection Observer API檢測元素進入視窗
    if ('IntersectionObserver' in window) {
        const animatedElements = document.querySelectorAll('.animate-on-scroll');
        
        const animationObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const element = entry.target;
                    const animation = element.dataset.animation || 'fade-in';
                    element.classList.add(animation);
                    animationObserver.unobserve(element);
                }
            });
        }, { threshold: 0.2 });
        
        animatedElements.forEach(element => {
            animationObserver.observe(element);
        });
    }
}

/**
 * 註冊Service Worker (PWA支援)
 */
function registerServiceWorker() {
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', function() {
            navigator.serviceWorker.register('./service-worker.js')
                .then(function(registration) {
                    console.log('Service Worker 註冊成功:', registration.scope);
                })
                .catch(function(error) {
                    console.log('Service Worker 註冊失敗:', error);
                });
        });
    }
}