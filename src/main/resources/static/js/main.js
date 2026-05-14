/* ═══════════════════════════════════════════════
   Travel Planner – main.js
   全站共用 JavaScript
   ═══════════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', () => {

  /* ── 1. 日期驗證：結束日期不得早於開始日期 ────── */
  const startInput = document.getElementById('startDate');
  const endInput   = document.getElementById('endDate');

  if (startInput && endInput) {
    // 初始化最小值
    if (startInput.value) endInput.min = startInput.value;

    startInput.addEventListener('change', () => {
      endInput.min = startInput.value;
      if (endInput.value && endInput.value < startInput.value) {
        endInput.value = startInput.value;
      }
      updateDurationHint();
    });

    endInput.addEventListener('change', updateDurationHint);

    function updateDurationHint() {
      if (!startInput.value || !endInput.value) return;
      const start = new Date(startInput.value);
      const end   = new Date(endInput.value);
      const days  = Math.round((end - start) / 86400000) + 1;
      let hint = document.getElementById('durationHint');
      if (!hint) {
        hint = document.createElement('span');
        hint.id = 'durationHint';
        hint.style.cssText = 'font-size:12px;color:#2B6CB0;margin-left:8px;font-weight:600';
        endInput.parentNode.appendChild(hint);
      }
      hint.textContent = days > 0 ? `共 ${days} 天` : '';
    }

    updateDurationHint();
  }

  /* ── 2. Alert 自動淡出消失 ─────────────────────── */
  const alerts = document.querySelectorAll('.alert');
  if (alerts.length) {
    setTimeout(() => {
      alerts.forEach(el => {
        el.style.transition = 'opacity 0.6s ease, max-height 0.6s ease, margin 0.6s ease';
        el.style.opacity    = '0';
        el.style.maxHeight  = '0';
        el.style.margin     = '0';
        el.style.overflow   = 'hidden';
        setTimeout(() => el.remove(), 650);
      });
    }, 4500);
  }

  /* ── 3. PDF 匯出按鈕：點擊後顯示 loading 狀態 ─── */
  const pdfBtn = document.getElementById('pdfBtn');
  if (pdfBtn) {
    pdfBtn.addEventListener('click', () => {
      const original = pdfBtn.innerHTML;
      pdfBtn.classList.add('loading');
      pdfBtn.innerHTML = '⏳ 產生中...';
      // PDF 為檔案下載，瀏覽器不會跳頁，3 秒後自動恢復
      setTimeout(() => {
        pdfBtn.classList.remove('loading');
        pdfBtn.innerHTML = original;
      }, 3500);
    });
  }

  /* ── 4. 行程表單：送出前驗證 ──────────────────── */
  const tripForm = document.getElementById('tripForm');
  if (tripForm) {
    tripForm.addEventListener('submit', e => {
      const start  = document.getElementById('startDate')?.value;
      const end    = document.getElementById('endDate')?.value;
      if (start && end && end < start) {
        e.preventDefault();
        showToast('返回日期不得早於出發日期！', 'error');
      }
    });
  }

  /* ── 5. 新增景點表單：費用欄位格式化 ─────────── */
  const costInput = document.querySelector('[name="cost"]');
  if (costInput) {
    costInput.addEventListener('blur', () => {
      if (costInput.value < 0) costInput.value = 0;
    });
  }

  /* ── 6. 行動版導覽：點擊外側關閉選單 ──────────── */
  document.addEventListener('click', e => {
    const navbar = document.querySelector('.navbar');
    if (navbar && !navbar.contains(e.target)) {
      navbar.classList.remove('open');
    }
  });

  /* ── 7. 通用 Toast 通知函式 ────────────────────── */
  window.showToast = function(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type}`;
    toast.textContent = message;
    toast.style.cssText = `
      position: fixed;
      top: 72px;
      right: 20px;
      z-index: 9999;
      min-width: 260px;
      max-width: 380px;
      box-shadow: 0 4px 16px rgba(0,0,0,0.15);
      animation: slideIn 0.3s ease;
    `;
    document.body.appendChild(toast);
    setTimeout(() => {
      toast.style.transition = 'opacity 0.5s';
      toast.style.opacity = '0';
      setTimeout(() => toast.remove(), 500);
    }, 3000);
  };

  /* ── 8. 動態加入 CSS keyframes (for Toast) ───── */
  if (!document.getElementById('toastStyle')) {
    const style = document.createElement('style');
    style.id = 'toastStyle';
    style.textContent = `
      @keyframes slideIn {
        from { transform: translateX(30px); opacity: 0; }
        to   { transform: translateX(0);   opacity: 1; }
      }
    `;
    document.head.appendChild(style);
  }

});
