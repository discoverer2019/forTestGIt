$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// 在编辑好要发送得内容之后，点击发送按钮，首先会关闭弹出框，然后获取要发送得私信内容，然后包装为json对象，使用ajax发送，在回调函数中设置成功或者失败事得提示框，最后是刷新页面
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data){
			data = $.parseJSON(data);
			if(data.code == 0){
				$("#hintBody").text("发送成功！");
			}else{
				$("#hintBody").text(data.msg);
			}
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();                    //重载当前页面
			}, 2000);
		}
	);




}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}