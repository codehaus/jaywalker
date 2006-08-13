
function addClassName(el, sClassName) {
	var s = el.className;
	var p = s.split(" ");
	var l = p.length;
	for (var i = 0; i < l; i++) {
		if (p[i] == sClassName)
			return;
	}
	p[p.length] = sClassName;
	el.className = p.join(" ");

}

function removeClassName(el, sClassName) {
	var s = el.className;
	var p = s.split(" ");
	var np = [];
	var l = p.length;
	var j = 0;
	for (var i = 0; i < l; i++) {
		if (p[i] != sClassName)
			np[j++] = p[i];
	}
	el.className = np.join(" ");
}

var st = new SortableTable(document.getElementById("table-3"),
	["String", "Number", "Date", "None"]);

// restore the class names
st.onsort = function () {
		var rows = st.tBody.rows;
		var l = rows.length;
		for (var i = 0; i < l; i++) {
			removeClassName(rows[i], i % 2 ? "odd" : "even");
			addClassName(rows[i], i % 2 ? "even" : "odd");
		}
};
