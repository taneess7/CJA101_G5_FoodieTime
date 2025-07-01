// FoodieTime - checkout.js
document.addEventListener('DOMContentLoaded', function() {
    const paymentMethodRadios = document.querySelectorAll('input[name="payMethod"]');
    const creditCardDetailsDiv = document.getElementById('credit-card-details');

    function toggleCreditCardDetails() {
        // 假設信用卡 radio 的 value 是 "0"
        if (document.querySelector('input[name="payMethod"]:checked').value === '0') {
            creditCardDetailsDiv.style.display = 'block';
        } else {
            creditCardDetailsDiv.style.display = 'none';
        }
    }

    paymentMethodRadios.forEach(radio => {
        radio.addEventListener('change', toggleCreditCardDetails);
    });

    // --- Credit Card Validation using creditcard.js ---
    const ccNumberInput = document.getElementById('cc-number');
    const ccNumberError = document.getElementById('cc-number-error');

    ccNumberInput.addEventListener('input', function(e) {
        // 自動加入空格，提升 UX
        e.target.value = e.target.value.replace(/[^\d]/g, '').replace(/(.{4})/g, '$1 ').trim();

        const isValid = creditcard.isValid(e.target.value);
        if (isValid) {
            ccNumberError.textContent = '卡號有效';
            ccNumberError.style.color = 'green';
        } else {
            ccNumberError.textContent = '無效的卡號';
            ccNumberError.style.color = 'red';
        }
    });

    // 初始化顯示狀態
    toggleCreditCardDetails();
});
