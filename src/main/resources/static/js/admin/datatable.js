$(document).ready(function() {
		$('.datatable').DataTable({
			"lengthMenu": [3 ,5, 10, 20, 50, 100],
			"searching": true,  //搜尋功能, 預設是開啟
		    "paging": true,     //分頁功能, 預設是開啟
		    "ordering": true,   //排序功能, 預設是開啟
		    "language": {
		        "processing": "處理中...",
		        "loadingRecords": "載入中...",
		        "lengthMenu": "顯示 _MENU_ 筆結果",
		        "zeroRecords": "沒有符合的結果",
		        "info": "顯示第 _START_ 至 _END_ 筆結果，共<font color=red> _TOTAL_ </font>筆",
		        "infoEmpty": "顯示第 0 至 0 筆結果，共 0 筆",
		        "infoFiltered": "(從 _MAX_ 筆結果中過濾)",
		        "infoPostFix": "",
		        "search": "搜尋:",
		        "paginate": {
		            "first": "第一頁",
		            "previous": "上一頁",
		            "next": "下一頁",
		            "last": "最後一頁"
		        },
		        "aria": {
		            "sortAscending":  ": 升冪排列",
		            "sortDescending": ": 降冪排列"
		        }
		    }
		});
	});