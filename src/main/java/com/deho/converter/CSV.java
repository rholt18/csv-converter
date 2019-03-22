package com.deho.converter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CSV {

	public static void main(String[] args) {
		String keysLine = ",,CURRENT,,,Jan Overdue,,Dec Overdue,,,Pre-Dec Overdue,,AMOUNT DUE,,";
		String valuesLine = ",,\"$2,216.50\",,,$247.50,,$811.11,,,\"$2,805.14\",,\"$6,080.25\",,";
		
		System.out.println(createKeyValuePairs(keysLine, valuesLine));
	}
	
	public static String[] removeBlankLines(List<String> allLines) {
		List<String> cleanedLines = new ArrayList<>();
		for (String line : allLines) {
			String lineWithoutCommas = line.replaceAll(",", "");
			if (!lineWithoutCommas.isEmpty()) {
				cleanedLines.add(line);
			}
		}
		return cleanedLines.stream().toArray(String[]::new);
	}
	
	public static Map<String, String> createKeyValuePairs(String keysLine, String valuesLine) {
		List<String> keys = removeBlankFields(keysLine);
		List<String> values = removeBlankFields(valuesLine);
		
		Map<String, String> map = IntStream.range(0, keys.size())
	            .boxed()
	            .collect(Collectors.toMap(i -> keys.get(i), i -> values.get(i)));
		
		return map;
	}
	
	// TODO - not working
	@Deprecated
	public static List<Map<String, String>> createKeyValuePairs(String keysLine, List<String> valuesLines) {
		List<Map<String, String>> result = new ArrayList<>();
		
		List<String> keys = removeBlankFields(keysLine);
		
		for(String valuesLine : valuesLines) {
			List<String> values = removeBlankFields(valuesLine);
			Map<String, String> map = IntStream.range(0, keys.size())
		            .boxed()
		            .collect(Collectors.toMap(i -> keys.get(i), i -> values.get(i)));
			
			result.add(map);
		}
		
		return result;
	}
	
	public static List<String> removeBlankFields(String rawLine) {
		List<String> rawFields = Arrays.asList(split(rawLine));
		ArrayList<String> fields = new ArrayList<String>(rawFields);
		fields.removeIf(String::isEmpty);
		return fields;
	}

	public static String[] split(final String text) {
		return (text.indexOf('"') < 0) ? text.split(",")
				: splitCSV(Stream.<String>builder(), text, 0).build().toArray(String[]::new);
	}

	private static Stream.Builder<String> splitCSV(final Stream.Builder<String> accum, final String text, int start) {
		final int length = text.length();
		if (start >= length)
			return accum;

		final StringBuilder buf = new StringBuilder(length);
		boolean inquote = false;

		if (text.charAt(start) == '"') {
			inquote = true;
			start++;
		}

		int i = start;

		loop: for (; i < length; i++) {
			final char c = text.charAt(i);
			switch (c) {
			case '"':
				if (inquote) {
					if (i + 1 < length && text.charAt(i + 1) == '"') {
						i++;
						break;
					} else {
						inquote = false;
						continue loop;
					}
				}
				break;

			case '\\':
				if (i + 1 < length) {
					final char next = text.charAt(i + 1);
					switch (next) {
					case 'b':
						buf.append('\b');
						i++;
						continue loop;
					case 'f':
						buf.append('\f');
						i++;
						continue loop;
					case 'r':
						buf.append('\r');
						i++;
						continue loop;
					case 'n':
						buf.append('\n');
						i++;
						continue loop;
					case 't':
						buf.append('\t');
						i++;
						continue loop;
					case '"':
						buf.append('"');
						i++;
						continue loop;
					}
				}
				break;

			case ',':
				if (!inquote) {
					// value termination
					i++;
					break loop;
				}
				break;
			}

			buf.append(c);
		}

		return splitCSV(accum.add(buf.toString()), text, i);
	}
}