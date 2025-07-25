// 按讚功能
function toggleLike(button) {
	button.classList.toggle('liked');
	const icon = button.querySelector('i');
	if (button.classList.contains('liked')) {
		icon.textContent = 'thumb_up';
	} else {
		icon.textContent = 'thumb_up';
	}
}

// 收藏功能
function toggleBookmark(button) {
	button.classList.toggle('bookmarked');
	const icon = button.querySelector('i');
	if (button.classList.contains('bookmarked')) {
		icon.textContent = 'bookmark';
	} else {
		icon.textContent = 'bookmark_border';
	}
}
// 留言展開/收起功能
function toggleComments() {
    const commentsList = document.querySelector('.comments-list'); // 留言列表容器
    const expandBtn = document.querySelector('.expand-btn');       // 展開/收起按鈕
    const icon = expandBtn.querySelector('i.material-icons-outlined'); // 獲取按鈕內部的圖標元素
    const commentDynamicTextSpan = expandBtn.querySelector('.comment-dynamic-text'); // 獲取新的動態文字span

    // 總是從顯示總留言數的可靠來源（即留言標題旁的span）獲取最準確的留言數量
    const totalCommentCountSpan = document.querySelector('.comments-title .comments-count');
    const actualTotalCount = totalCommentCountSpan ? totalCommentCountSpan.textContent : '0'; // 修正錯字

    // 切換 'is-visible' Class
    commentsList.classList.toggle('is-visible');

    // 判斷留言列表現在是否可見（透過檢查是否有 'is-visible' Class）
    const isCommentsVisible = commentsList.classList.contains('is-visible');

    // 根據顯示狀態更新圖標和按鈕文字
    if (isCommentsVisible) { // 如果留言列表現在是顯示的
        icon.textContent = 'expand_less';    // 將圖標更新為「收起」圖標
        commentDynamicTextSpan.textContent = '收起留言'; // 設定動態文字內容為「收起留言」

    } else { // 如果留言列表現在是隱藏的
        icon.textContent = 'expand_more';    // 將圖標更新為「展開」圖標

        // 設定動態文字內容為「還有 [留言數] 則留言」
        commentDynamicTextSpan.innerHTML = '還有 <span id="comment-count">' + actualTotalCount + '</span> 則留言';
    }
}
// 留言輸入框功能
document.addEventListener('DOMContentLoaded', function() {
	const textarea = document.querySelector('.comment-textarea');
	const submitBtn = document.querySelector('.submit-btn');
	if (textarea && submitBtn) {
		textarea.addEventListener('input', function() {
			submitBtn.disabled = this.value.trim().length === 0;
		});
	}
	
	// 自動展開留言列表，讓留言列表預設是打開的
	const commentsList = document.querySelector('.comments-list');
	const expandBtn = document.querySelector('.expand-btn');
	if (commentsList && expandBtn) {
		// 如果留言列表還沒有 is-visible class，則自動展開
		if (!commentsList.classList.contains('is-visible')) {
			toggleComments();
		}
	}
	
	// 禁用已刪除留言的檢舉按鈕
	disableDeletedMessageReportButtons();
});

// 禁用已刪除留言的檢舉按鈕
function disableDeletedMessageReportButtons() {
	const commentItems = document.querySelectorAll('.comment-item');
	
	commentItems.forEach(item => {
		const commentText = item.querySelector('.comment-text');
		if (commentText && commentText.textContent.trim() === '[此留言已被管理員刪除]') {
			const reportBtn = item.querySelector('button[onclick*="openReportMessageModal"]');
			if (reportBtn) {
				reportBtn.disabled = true;
				reportBtn.style.opacity = '0.5';
				reportBtn.style.cursor = 'not-allowed';
				reportBtn.title = '此留言已被刪除，無法檢舉';
			}
		}
	});
}

/*<![CDATA[*/
function editPost() {
	// 跳轉到修改頁面，帶上 postId
	var postId = /*[[${postVO.postId}]]*/ 0;
	window.location.href = '/post/edit?postId=' + postId;
}
function reportPost() {
	// 直接打開檢舉 modal，不跳頁
	openReportModal();
}
/*]]>*/
function openReportModal() {
	document.getElementById('reportModal').style.display = 'block';
}
function closeReportModal() {
	document.getElementById('reportModal').style.display = 'none';
}
// 自動隱藏提示
setTimeout(function() {
	var successAlert = document.querySelector('.alert-success');
	var errorAlert = document.querySelector('.alert-danger');
	
	// 從URL中獲取postId
	var urlParams = new URLSearchParams(window.location.search);
	var postId = urlParams.get('postId');
	
	if (successAlert) {
		successAlert.style.display = 'none';
		// 清除session中的成功訊息
		var clearUrl = '/message/clear-session-message?type=success';
		if (postId) {
			clearUrl += '&postId=' + postId;
		}
		fetch(clearUrl, {method: 'POST'});
	}
	if (errorAlert) {
		errorAlert.style.display = 'none';
		// 清除session中的錯誤訊息
		var clearUrl = '/message/clear-session-message?type=error';
		if (postId) {
			clearUrl += '&postId=' + postId;
		}
		fetch(clearUrl, {method: 'POST'});
	}
}, 3000);

function openReportMessageModal(mesId) {
    // 檢查是否為已刪除的留言
    const commentItems = document.querySelectorAll('.comment-item');
    let isDeletedMessage = false;
    
    commentItems.forEach(item => {
        const reportBtn = item.querySelector(`button[onclick*="${mesId}"]`);
        if (reportBtn) {
            const commentText = item.querySelector('.comment-text');
            if (commentText && commentText.textContent.trim() === '[此留言已被管理員刪除]') {
                isDeletedMessage = true;
            }
        }
    });
    
    // 如果是已刪除的留言，顯示提示並返回
    if (isDeletedMessage) {
        alert('此留言已被管理員刪除，無法檢舉');
        return;
    }
    
    document.getElementById('reportMessageId').value = mesId;
    document.getElementById('reportMessageModal').style.display = 'flex';
}
function closeReportMessageModal() {
    document.getElementById('reportMessageModal').style.display = 'none';
}
// 點擊背景關閉模態框
document.getElementById('reportMessageModal').addEventListener('click', function(e) {
    if (e.target === this) closeReportMessageModal();
});

// ESC 鍵關閉
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeReportMessageModal();
});
// document.getElementById('reportMessageModal').style.display = 'flex';
