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
});

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
	var alert = document.querySelector('.alert-success');
	if (alert) alert.style.display = 'none';
}, 2000);

function openReportMessageModal(mesId) {
    document.getElementById('reportMessageId').value = mesId;
    document.getElementById('reportMessageModal').style.display = 'block';
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
document.getElementById('reportMessageModal').style.display = 'flex';
