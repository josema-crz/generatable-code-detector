jQuery(document).ready(function($) {
	$(".clickableRow").click(function() {
		window.document.location = $(this).data("href");
	});
});

jQuery.fn.dataTableExt.oSort['num_ignore_text-asc'] = function(x, y) {
	if (isNaN(x) && isNaN(y))
		return ((x < y) ? 1 : ((x > y) ? -1 : 0));

	if (isNaN(x))
		return 1;
	if (isNaN(y))
		return -1;

	x = parseFloat(x);
	y = parseFloat(y);
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
};

jQuery.fn.dataTableExt.oSort['num_ignore_text-desc'] = function(x, y) {
	if (isNaN(x) && isNaN(y))
		return ((x < y) ? 1 : ((x > y) ? -1 : 0));

	if (isNaN(x))
		return 1;
	if (isNaN(y))
		return -1;

	x = parseFloat(x);
	y = parseFloat(y);
	return ((x < y) ? 1 : ((x > y) ? -1 : 0));
};

jQuery.fn.dataTableExt.oSort['num_clone_set-asc'] = function(x, y) {
	// Extract the numbers from the string
	var n1 = x.substring(10, x.length);
	var n2 = y.substring(10, y.length);
	if (isNaN(n1) && isNaN(n2))
		return ((n1 < n2) ? 1 : ((n1 > n2) ? -1 : 0));

	if (isNaN(n1))
		return 1;
	if (isNaN(n2))
		return -1;

	n1 = parseInt(n1);
	n2 = parseInt(n2);
	return ((n1 < n2) ? -1 : ((n1 > n2) ? 1 : 0));
};

jQuery.fn.dataTableExt.oSort['num_clone_set-desc'] = function(x, y) {
	// Extract the numbers from the string
	var n1 = x.substring(10, x.length);
	var n2 = y.substring(10, y.length);
	if (isNaN(n1) && isNaN(n2))
		return ((n1 < n2) ? 1 : ((n1 > n2) ? -1 : 0));

	if (isNaN(n1))
		return 1;
	if (isNaN(n2))
		return -1;

	return ((n1 < n2) ? 1 : ((n1 > n2) ? -1 : 0));
};