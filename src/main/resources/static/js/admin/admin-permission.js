// ✅ admin-permission.js
// 專門處理後台會員權限變更（更新、歷史、localStorage）邏輯

const AdminPermissionManager = (() => {
    function savePermissionChanges() {
        const memId = document.getElementById('modalMemId').value;
        const memStatus = document.getElementById('memStatus').value;
        const memNoSpeak = document.getElementById('memNoSpeak').value;
        const memNoPost = document.getElementById('memNoPost').value;
        const memNoGroup = document.getElementById('memNoGroup').value;
        const memNoJoingroup = document.getElementById('memNoJoingroup').value;
        const permissionReason = document.getElementById('permissionReason').value;

        const formData = new URLSearchParams({
            memId, memStatus, memNoSpeak, memNoPost, memNoGroup, memNoJoingroup, permissionReason
        });

        fetch('/smg/admin-members-permissions/update-status', {
            method: 'POST',
            body: formData,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => {
            const contentType = response.headers.get("content-type");
            if (!response.ok || !contentType || !contentType.includes("application/json")) {
                if (response.status === 401 || response.redirected) {
                    alert('請重新登入管理員帳號！');
                    window.location.href = '/smg/login';
                    return Promise.reject();
                }
                throw new Error('伺服器回應格式錯誤');
            }
            return response.json();
        })
        .then(data => {
            if (!data || typeof data !== 'object') return;
            if (!data.success) {
                alert(data.message || '變更失敗');
                return;
            }

            alert('更新成功');

            updateRowStatus(memId, data);

            const historyRow = `
                <tr>
                    <td>${data.lastModifiedDate}</td>
                    <td>
                        <ul style="padding-left: 1em;">
                            <li>狀態: ${formatStatus(data.oldStatus)} ➔ ${formatStatus(data.memStatus)}</li>
                            <li>禁言: ${formatYN(data.oldNoSpeak)} ➔ ${formatYN(data.memNoSpeak)}</li>
                            <li>發文: ${formatYN(data.oldNoPost)} ➔ ${formatYN(data.memNoPost)}</li>
                            <li>開團: ${formatYN(data.oldNoGroup, true)} ➔ ${formatYN(data.memNoGroup, true)}</li>
                            <li>參團: ${formatYN(data.oldNoJoingroup, true)} ➔ ${formatYN(data.memNoJoingroup, true)}</li>
                        </ul>
                    </td>
                    <td>${data.lastModifiedBy}</td>
                    <td>${permissionReason}</td>
                </tr>
            `;

            const historyTable = document.getElementById('permissionHistoryTable');
            if (historyTable) {
                historyTable.insertAdjacentHTML('afterbegin', historyRow);
            }
            saveHistoryToLocal(memId, historyRow);

            bootstrap.Modal.getInstance(document.getElementById('editPermissionsModal')).hide();
        })
        .catch(err => {
            console.error('更新失敗:', err);
            alert('變更時發生錯誤');
        });
    }

    function updateRowStatus(memId, data) {
        const row = document.querySelector(`input[data-member-id="${memId}"]`)?.closest('tr');
        if (!row) return;

        const statusSpan = row.querySelector('td:nth-child(5) span');
        if (statusSpan) {
            statusSpan.className = data.memStatus == 1 ? 'badge bg-success' : data.memStatus == 2 ? 'badge bg-danger' : 'badge bg-secondary';
            statusSpan.textContent = formatStatus(data.memStatus);
        }

        const dateCell = row.querySelector('td:nth-child(6) div');
        const byCell = row.querySelector('td:nth-child(6) small');
        if (dateCell) dateCell.textContent = `${data.lastModifiedDate}（即時）`;
        if (byCell) byCell.textContent = data.lastModifiedBy;
    }

    function formatStatus(value) {
        return value == 1 ? '啟用' : value == 2 ? '停權' : '尚未啟用';
    }

    function formatYN(value, isGroup = false) {
        if (isGroup) return value == 1 ? '禁' + (value === 1 ? '止' : '') + '開團/參團' : '正常';
        return value == 1 ? '禁止發言/發文' : '允許';
    }

    function saveHistoryToLocal(memId, html) {
        const key = `memberHistory_${memId}`;
        let historyArr = JSON.parse(localStorage.getItem(key)) || [];
        historyArr.unshift(html);
        localStorage.setItem(key, JSON.stringify(historyArr));
    }

    return {
        savePermissionChanges
    };
})();
