package com.deho.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	private static boolean debug = false;

	public static boolean isValidDate(String text) {
		if (debug)
			System.out.println("Input text: " + text);

		if (StringUtils.isNotBlank(text)) {

			LocalDate parsedDate = null;
			String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MM-yy", "dd/MM/yy", "yyyy/MM/dd", "yyyy-MM-dd",
					"yy/MM/dd", "yy-MM-dd" };
			for (String pattern : patterns) {
				try {
					parsedDate = LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
					if (debug)
						System.out.println("parsedDate using pattern [" + pattern + "] " + parsedDate);
					return true;
				} catch (DateTimeParseException e) {
					// Controls flow only
				}
			}
		} else {
			if (debug)
				System.out.println("passed text param was blank [" + text + "]");
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println("Is this date valid: 11/03/2019 => " + isValidDate("11/03/2019"));
		System.out.println("Is this date valid: 11-03-2019 => " + isValidDate("11-03-2019"));
		System.out.println("Is this date valid: 11/03/2019 => " + isValidDate("11/03/19"));
		System.out.println("Is this date valid: 11-03-2019 => " + isValidDate("11-03-19"));
		System.out.println("Is this date valid: 11-03-2019 => " + isValidDate("2019/01/12"));
		System.out.println("Is this date valid: 11-03-2019 => " + isValidDate("2019-01-12"));
		System.out.println("Is this date valid: 11-03-2019 => " + isValidDate("19/01/12"));
		System.out.println("Is this date valid: 11-03-2019 => " + isValidDate("19-01-12"));
	}
}
