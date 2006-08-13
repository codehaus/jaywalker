//<![CDATA[

// help tips
htDom = "Document Object Model 1 is a standard developed by the W3C.<br />" +
		"<a href=\"http://www.w3.org/DOM/\" target=\"_blank\">http://www.w3.org/DOM/</a>";

//]]>

function getRandomDate() {
	var d = new Date(Math.random() * Math.pow(10, 12));
	return d.getFullYear() + "-" +
		(d.getMonth() < 10 ? "0" : "") +
		d.getMonth() + "-" +
		(d.getDate() < 10 ? "0" : "") +
		d.getDate();
}

ROWS = 500;
COLS = 4;

var sb = new StringBuilder();
sb.append("<table id=\"table-3\" class=\"sort-table\"><thead><tr>");
for (var x = 0; x < COLS; x++) {
	if (x == 1)
		sb.append("<td>Number</td>");
	else if (x == 2)
		sb.append("<td>Date</td>");
	else if (x == 3)
		sb.append("<td>No sort</td>");
	else
		sb.append("<td>Head " + x + "</td>");
}

sb.append("</tr></thead><tbody>");
for (var y = 0; y < ROWS; y++) {
	sb.append("<tr class=\"");
	sb.append(y % 2 ? "even" : "odd");
	sb.append("\">");
	for (var x = 0; x < COLS; x++) {
		if (x == 1)
			sb.append("<td>" + Math.round(Math.random() * Math.pow(10,5)) + "</td>");
		else if (x == 2)
			sb.append("<td>" + getRandomDate() + "</td>");
		else
			sb.append("<td>Item " + y + "." + x + "</td>");
	}
	sb.append("</tr>");
}

sb.append("</tbody></table>");
