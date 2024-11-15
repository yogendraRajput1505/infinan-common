package com.infinan.common.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class LastSecondCandleData {
	//Need to check if it is working or not
	public static Date getLastSecondTime(long sec) {
		Date time = Calendar.getInstance().getTime();
		time.setTime(time.getTime()-sec*1000); // if we replace 1000 by 60000 then it will give last minute candle data
		return time;
	}
	
	public static String getCurrentDateAndTimeInKiteFormat() {
		OffsetDateTime  dateTime = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        return dateTime.format(formatter);
	}
	
}
