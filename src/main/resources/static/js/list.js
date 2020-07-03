$(() => {	
	$("#searchbar").one("click", () => {
  		var url = "/employee/name";
  		var type = "GET";
		$.ajax({
			url,
			type,
			dataType: "json",
			async: true
		}).done(data => {
			$("#searchbar").autocomplete({
				source: data.empNameList
			});
		}).fail((XMLHttpRequest, textStatus, errorThrown) => {
			alert("エラーが発生しました");
			console.error("XMLHttpRequest:", XMLHttpRequest.status);
			console.error("textStatus	 :", textStatus);
			console.error("errorThrown   :", errorThrown.message);
		});
	});
})