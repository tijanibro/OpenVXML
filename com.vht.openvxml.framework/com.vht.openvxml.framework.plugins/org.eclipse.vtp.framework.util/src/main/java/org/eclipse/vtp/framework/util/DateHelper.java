package org.eclipse.vtp.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateHelper {
	private static String[] datePatterns = new String[] { "M/d/yyyy",
			"M-d-yyyy", "M.d.yyyy" };
	private static String[] timePatterns = new String[] { "h:mm:ss a z",
			"H:mm:ss z", "h:mm:ss a", "H:mm:ss", "h:mm a z", "H:mm z",
			"h:mm a", "H:mm" };

	public static Calendar parseDate(String dateString) {
		Calendar cal = parseDate0(dateString);
		if (cal != null) {
			int index = dateString.indexOf("GMT");
			if (index >= 0) {
				String tzOffsetString = dateString.substring(index);
				TimeZone tzOffset = TimeZone.getTimeZone(tzOffsetString);
				cal.setTimeZone(tzOffset);
			}
		}
		return cal;
	}

	public static Calendar toCalendar(ZonedDateTime zdt) {
		return GregorianCalendar.from(zdt);
	}

	public static ZonedDateTime toZonedDateTime(Calendar cal) {
		return ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone()
				.toZoneId());
	}

	public static ZonedDateTime parseDateZDT(String dateString) {
		for (String datePattern : datePatterns) {
			for (String timePattern : timePatterns) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern
						+ " " + timePattern);
				return ZonedDateTime.parse(dateString, dtf);
			}
		}
		for (String datePattern : datePatterns) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
			return ZonedDateTime.parse(dateString, dtf);
		}
		for (String timePattern : timePatterns) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timePattern);
			return ZonedDateTime.parse(dateString, dtf);
		}
		return null;
	}

	private static Calendar parseDate0(String dateString) {
		for (String datePattern : datePatterns) {
			for (String timePattern : timePatterns) {
				SimpleDateFormat sdf = new SimpleDateFormat(datePattern + " "
						+ timePattern);
				try {
					sdf.parse(dateString);
					return sdf.getCalendar();
				} catch (ParseException e) {
				}
			}
		}
		for (String datePattern : datePatterns) {
			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			try {
				sdf.parse(dateString);
				return sdf.getCalendar();
			} catch (ParseException e) {
			}
		}
		for (String timePattern : timePatterns) {
			SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
			try {
				sdf.parse(dateString);
				return sdf.getCalendar();
			} catch (ParseException e) {
			}
		}
		return null;
	}

	@Test
	public void testParseDateZdt() {
		System.out.println(parseDateZDT("7/9/2020 1:44:45 PM IST"));
	}

	public static String toDateString(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat(datePatterns[0] + " "
				+ timePatterns[0]);
		sdf.setTimeZone(cal.getTimeZone());
		return sdf.format(cal.getTime());
	}

	public static String toDateString(ZonedDateTime zdt) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePatterns[0]
				+ " " + timePatterns[0]);
		return zdt.format(dtf);
	}

	@Test
	public void testToDateString() {
		System.out
				.println("With Cal:\t" + toDateString(Calendar.getInstance()));
		System.out.println("With Zdt:\t" + toDateString(ZonedDateTime.now()));
	}
}
