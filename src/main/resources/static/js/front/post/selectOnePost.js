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
           const commentsList = document.querySelector('.comments-list');
           const expandBtn = document.querySelector('.expand-btn');

           if (commentsList.style.display === 'none') {
               commentsList.style.display = 'block';
               expandBtn.innerHTML = '<i class="material-icons-outlined">expand_less</i>收起留言';
           } else {
               commentsList.style.display = 'none';
               expandBtn.innerHTML = '<i class="material-icons-outlined">expand_more</i>還有 10 則留言';
           }
       }

       // 留言輸入框功能
       document.addEventListener('DOMContentLoaded', function () {
           const textarea = document.querySelector('.comment-textarea');
           const submitBtn = document.querySelector('.submit-btn');

           textarea.addEventListener('input', function () {
               if (this.value.trim().length > 0) {
                   submitBtn.disabled = false;
               } else {
                   submitBtn.disabled = true;
               }
           });
       });
	   
	   /*<![CDATA[*/
	      function editPost() {
	          // 跳轉到修改頁面，帶上 postId
	          var postId = /*[[${postVO.postId}]]*/ 0;
	          window.location.href = '/post/edit?postId=' + postId;
	      }
	      function reportPost() {
	          // 彈出檢舉視窗或跳轉到檢舉頁面
	          var postId = /*[[${postVO.postId}]]*/ 0;
	          if(confirm('確定要檢舉這篇貼文嗎？')) {
	              window.location.href = '/reportPost/add?postId=' + postId;
	          }
	      }
	      /*]]>*/
	   