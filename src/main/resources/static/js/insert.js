$(() => {
	$("#search-address").on("click", e => {
		var zipcode = $("#zipCode").val();
		var type = "GET";
		var url = "http://zipcoda.net/api";
		$.ajax({
			url,
			type,
			dataType: "jsonp",
			data: {
				zipcode
			},
			async: true
		}).done(data => {
			console.dir(JSON.stringify(data));
			$("#address").val(data.items[0].components.join(""));
		}).fail((XMLHttpRequest, textStatus, errorThrown) => {
			alert("エラーが発生しました");
			console.error("XMLHttpRequest:", XMLHttpRequest.status);
			console.error("textStatus	 :", textStatus);
			console.error("errorThrown   :", errorThrown.message);
		})
	});
})