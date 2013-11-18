function calculateTimeZone() {
	var rightNow = new Date();
	var jan1 = new Date(rightNow.getFullYear(), 0, 1, 0, 0, 0, 0);  // jan 1st
	var june1 = new Date(rightNow.getFullYear(), 6, 1, 0, 0, 0, 0); // june 1st
	var temp = jan1.toGMTString();
	var jan2 = new Date(temp.substring(0, temp.lastIndexOf(" ") - 1));
	temp = june1.toGMTString();
	var june2 = new Date(temp.substring(0, temp.lastIndexOf(" ") - 1));
	var standardTimeOffset = (jan1 - jan2) / (1000 * 60 * 60);
	var daylightTimeOffset = (june1 - june2) / (1000 * 60 * 60);
	var dst;
	if (standardTimeOffset == daylightTimeOffset) {
		dst = "0"; // daylight savings time is NOT observed
	} else {
		// positive is southern, negative is northern hemisphere
		var hemisphere = standardTimeOffset - daylightTimeOffset;
		if (hemisphere >= 0)
			standardTimeOffset = daylightTimeOffset;
		dst = "1"; // daylight savings time is observed
	}
	var actualTimeZoneOffset = convert(standardTimeOffset);
	return "GMT" + actualTimeZoneOffset;
}
//This function is to convert the timezoneoffset to Standard format
function convert(value) {
	var hours = parseInt(value);
	value -= parseInt(value);
	value *= 60;
	var mins = parseInt(value);
	value -= parseInt(value);
	value *= 60;
	var secs = parseInt(value);
	var displayHours = hours;
	// handle GMT case (00:00)
	if (hours == 0) {
		displayHours = "00";
	} else if (hours > 0) {
		// add a plus sign and perhaps an extra 0
		displayHours = (hours < 10) ? "+0" + hours : "+" + hours;
	} else {
		// add an extra 0 if needed
		displayHours = (hours > -10) ? "-0" + Math.abs(hours) : hours;
	}
	mins = (mins < 10) ? "0" + mins : mins;
	return displayHours + ":" + mins;
}

$(window).load(function() {
	$.get("/home/trackUsersTimeZone/" + encodeURIComponent(calculateTimeZone()));
})