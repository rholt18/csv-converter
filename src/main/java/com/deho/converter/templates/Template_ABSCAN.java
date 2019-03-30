package com.deho.converter.templates;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.deho.converter.CSV;
import com.deho.converter.DataHolder;
import com.deho.converter.FileConverter;
import com.deho.converter.Template;

public class Template_ABSCAN implements Template {

	private static final String FILENAME = "ABSCAN.csv";
	private static final String COMPANY_NAME = FILENAME.substring(0, FILENAME.indexOf("."));

	private static final String STATEMENT_AS_AT_DATE_IDENTIFIER = ",Garbutt QLD 4814,,,,,,,";
	private static final String REC_AS_AT_HB_DATE_IDENTIFIER = "c";
	private static final String TOTAL_OUTSTANDING_BALANCE_IDENTIFIER = ",CURRENT,,,,,,AMOUNT DUE,";
	private static final String TOTAL_OUTSTANDING_BALANCE_KEY = "AMOUNT DUE";
	private static final String SINCE_PAID_BALANCES_IDENTIFIER = ",DATE";
	private static final String SINCE_PAID_BALANCES_END = "**  Interest will be charged on all overdue amounts at the rate of 1.5% per week.";
	
	
	private DataHolder data = new DataHolder(COMPANY_NAME, FILENAME);
	
	@Override
	public DataHolder getData() {
		return data;
	}

	@Override
	public DataHolder parseText(String[] lines) {
		data.setVendorCode("TBC");
		data.setReconciliationAsAtHBPaymentDate("TBC");
		data.setSincePaidBalances(Arrays.asList(new String[] {"TBC"}));

		for (int i = 1, j = 0; i <= lines.length; i++, j++) {
			String trimmed = lines[j].trim();
			if (FileConverter.debugOutput()) {
				System.out.println(trimmed);
			}

			if (trimmed.equals(STATEMENT_AS_AT_DATE_IDENTIFIER)) {
				String[] values = lines[j + 1].trim().split(",");
				for (String value : values) {
					if (StringUtils.isNotBlank(value)) {
						data.setStatementAsAtDate(value);
					}
				}

			}  else if (trimmed.contains(TOTAL_OUTSTANDING_BALANCE_IDENTIFIER)) {
				String keys = trimmed;
				String values = lines[j + 2].trim();
				Map<String, String> keyValuePairs = CSV.createKeyValuePairs(keys, values);
				
				data.setTotalOutstandingBalance(keyValuePairs.get(TOTAL_OUTSTANDING_BALANCE_KEY));
				
			} else if (trimmed.startsWith(SINCE_PAID_BALANCES_IDENTIFIER)) {
//				String keys = trimmed;
//				List<String> values = new ArrayList<String>();
//				
//				String valueLine = lines[++j].trim();
//				while (!valueLine.startsWith(SINCE_PAID_BALANCES_END)) {
//					values.add(valueLine);
//					valueLine = lines[++j].trim();
//				}
//				
//				List<Map<String, String>> keyValuePairs = CSV.createKeyValuePairs(keys, values);
//				System.out.println(keyValuePairs);
			}

		}

		return data;
	}

}
