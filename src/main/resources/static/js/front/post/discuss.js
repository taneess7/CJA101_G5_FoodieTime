// 簡單的互動功能
document.addEventListener('DOMContentLoaded', function() {
	// 分頁按鈕點擊效果
	const pageButtons = document.querySelectorAll('.btn');
	pageButtons.forEach(button => {
		button.addEventListener('click', function() {
			if (this.textContent.match(/^\d+$/)) {
				// 移除其他按鈕的 active 狀態
				pageButtons.forEach(btn => {
					if (btn.textContent.match(/^\d+$/)) {
						btn.className = btn.className.replace('btn-primary', 'btn-ghost');
					}
				});
				// 添加當前按鈕的 active 狀態
				this.className = this.className.replace('btn-ghost', 'btn-primary');
			}
		});
	});

	// 分類按鈕點擊效果
	const categoryButtons = document.querySelectorAll('[class*="btn-ghost"]:not([class*="px-3"])');
	categoryButtons.forEach(button => {
		button.addEventListener('click', function() {
			// 移除兄弟元素的 active 狀態
			const siblings = this.parentElement.children;
			Array.from(siblings).forEach(sibling => {
				if (sibling !== this) {
					sibling.className = sibling.className.replace('btn-primary', 'btn-ghost');
				}
			});
			// 添加當前按鈕的 active 狀態
			this.className = this.className.replace('btn-ghost', 'btn-primary');
			console.log('點擊分類：', this.textContent);
		});
	});

	// 討論串點擊效果
	const threadRows = document.querySelectorAll('.forum-thread-row');
	threadRows.forEach(row => {
		row.addEventListener('click', function() {
			const title = this.querySelector('h3').textContent;
			console.log('點擊討論串：', title);
		});
	});

	// 搜尋功能（點按鈕）
	document.getElementById('searchPostBtn').addEventListener('click', function() {
		const keyword = document.getElementById('searchTitle').value.trim();
		if (keyword.length === 0) {
			alert('請輸入關鍵字');
			return;
		}
		// 檢查是否在收藏頁面
		const isFavoritePage = window.location.pathname.includes('/FavoritePost/');
		const searchUrl = isFavoritePage ? '/FavoritePost/search?title=' : '/post/search?title=';
		window.location.href = searchUrl + encodeURIComponent(keyword);
	});

	// 搜尋功能（按 Enter）
	document.getElementById('searchTitle').addEventListener('keydown', function(e) {
		if (e.key === 'Enter') {
			e.preventDefault();
			document.getElementById('searchPostBtn').click();
		}
	});

	//點擊「發布貼文」時會員登入判斷
	//	var loggedIn = /*[[${loggedIn}]]*/ false;
	//	document.getElementById('addPostBtn').addEventListener('click', function() {
	//		if (loggedIn) {
	//			window.location.href = /*[[@{/post/addPost}]]*/ '/post/addPost';
	//		} else {
	//			alert('請先登入才能發佈貼文！');
	//			window.location.href = /*[[@{/login}]]*/ '/login';
	//		}
	//	});

	//為了省略登入的判斷(有會員時可刪)
	document.getElementById('addPostBtn').addEventListener('click', function() {
		window.location.href = '/post/addPost';
	});
});