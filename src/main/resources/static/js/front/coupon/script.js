
function loadCoupons() {
  fetch('/store/api/coupons')
    .then(res => {
      if (!res.ok) throw new Error("錯誤：" + res.status);
      return res.json();
    })
    .then(data => {
      const grid = document.getElementById('couponGrid');
      if (!grid) {
        console.error("找不到 couponGrid 元素！");
        return;
      }

      grid.innerHTML = ''; // 清空內容

      if (data.length === 0) {
        grid.innerHTML = '<p>目前尚無優惠券</p>';
        return;
      }

      data.forEach(cou => {
        const div = document.createElement('div');
        div.className = 'coupon-card';
        div.innerHTML = `
          <div class="coupon-title">${cou.couName}</div>
          <div class="coupon-detail">類型：${cou.couType}</div>
          <div class="coupon-detail">說明：${cou.couDes}</div>
          <div class="coupon-detail">最低消費：NT$${cou.couMinOrd}</div>
          <div class="coupon-detail">折扣額度：${cou.couDiscount}</div>
          <div class="coupon-date">使用期限：${formatDate(cou.couStartDate)} - ${formatDate(cou.couEndDate)}</div>
        `;
        grid.appendChild(div);
      });
    })
    .catch(err => {
      console.error("發生錯誤", err);
    });
}

function formatDate(str) {
  const d = new Date(str);
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  const hh = String(d.getHours()).padStart(2, '0');
  const mi = String(d.getMinutes()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${mi}`;
}
