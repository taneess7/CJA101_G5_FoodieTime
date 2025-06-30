// FoodieTime - payment.js
document.addEventListener('DOMContentLoaded', () => {
    // 變數來自 payment-processing.html 中 th:inline="javascript"
    // const orderId = ...;
    // const linePayUrl = ...;

    // 1. 生成 QR Code
    QRCode.toCanvas(document.getElementById('qrcode-canvas'), linePayUrl, { width: 200 }, function (error) {
        if (error) console.error(error);
        console.log('QR Code generated successfully!');
    });

    // 2. 啟動 5 分鐘倒數計時器
    let timeLeft = 300; // 5分鐘 = 300秒
    const countdownElement = document.getElementById('countdown');
    const timerInterval = setInterval(() => {
        timeLeft--;
        const minutes = Math.floor(timeLeft / 60).toString().padStart(2, '0');
        const seconds = (timeLeft % 60).toString().padStart(2, '0');
        countdownElement.textContent = `${minutes}:${seconds}`;

        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            clearInterval(pollingInterval); // 停止輪詢
            countdownElement.textContent = "00:00";
            document.querySelector('#payment-status span').textContent = '付款已逾時，請重新下單。';
            document.querySelector('.spinner').style.display = 'none';
        }
    }, 1000);

    // 3. 啟動付款狀態輪詢 (每 3 秒一次)
    const pollingInterval = setInterval(() => {
        fetch(`/orders/api/order/${orderId}/status`)
            .then(response => response.json())
            .then(data => {
                // paymentStatus: 0 (待付款), 1 (已付款)
                if (data.paymentStatus === 1) {
                    clearInterval(timerInterval);
                    clearInterval(pollingInterval);

                    // 付款成功，跳轉到訂單確認頁
                    document.querySelector('#payment-status span').textContent = '付款成功！正在為您跳轉...';
                    document.querySelector('.spinner').style.display = 'none';

                    window.location.href = `/orders/order-confirmation?orderId=${orderId}`;
                }
            })
            .catch(err => {
                console.error('Polling error:', err);
                // 可選擇在此處停止輪詢以避免無限錯誤
            });
    }, 3000);
});
