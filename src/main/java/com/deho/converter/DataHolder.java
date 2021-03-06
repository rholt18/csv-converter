package com.deho.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DataHolder {

	private final String businessName;
	private final String fileName;
	
	private String vendorCode = "";
	private String reconciliationAsAtHBPaymentDate = "";
	private String statementAsAtDate = "";
	private String totalOutstandingBalance = "";
	private List<String> sincePaidBalances = new ArrayList<>();
	
	public DataHolder(String businessName, String fileName) {
		this.businessName = businessName;
		this.fileName = fileName;
	}

	public static String addQuotes(String raw) {
		if (raw == null) {
			return "";
		} else if (raw.contains(",")) {
			return "\"" + raw + "\""; 
		} else {
			return raw;
		}
	}
	
	public boolean isComplete() {
		boolean validBusName = StringUtils.isNotBlank(this.businessName);
		boolean validReconcilliationDate = StringUtils.isNotBlank(this.reconciliationAsAtHBPaymentDate);
		boolean validStatementDate = StringUtils.isNotBlank(this.statementAsAtDate);
		boolean validOutstandingBalance = StringUtils.isNotBlank(this.totalOutstandingBalance);
		boolean validVendorCode = StringUtils.isNotBlank(this.vendorCode);
		boolean validPaidBalances = this.sincePaidBalances.size() > 0;

		if (!validBusName) {
			System.err.println("\tThe Business Name has not been discovered for " + this.fileName);
		}
		if (!validVendorCode) {
			System.err.println("\tThe Vendor Code has not been discovered for " + this.fileName);
		}
		if (!validStatementDate) {
			System.err.println("\tThe Statement Date has not been discovered for " + this.fileName);
		}
		if (!validReconcilliationDate) {
			System.err.println("\tThe Reconcilliation Date has not been discovered for " + this.fileName);
		}
		if (!validOutstandingBalance) {
			System.err.println("\tThe Outstanding Balance has not been discovered for " + this.fileName);
		}
		if (!validPaidBalances) {
			System.err.println("\tThe Since Paid Balances have not been discovered for " + this.fileName);
		}
		
		return validBusName && validOutstandingBalance && validReconcilliationDate && validStatementDate && validVendorCode && validPaidBalances;
	}

	@Override
	public String toString() {
		return "\tDataHolder ["
				+ "vendorCode=[" + vendorCode + "] " 
				+ "businessName=[" + businessName + "] "
				+ "reconciliationAsAtHBPaymentDate=[" + reconciliationAsAtHBPaymentDate + "] " 
				+ "statementAsAtDate=[" + statementAsAtDate + "] " 
				+ "totalOutstandingBalance=[" + totalOutstandingBalance + "] "
				+ "sincePaidBalances=[" + sincePaidBalances + "] " 
				+ "]";
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getBusinessName() {
		return businessName;
	}
	
	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = addQuotes(vendorCode);
	}

	public String getReconciliationAsAtHBPaymentDate() {
		return reconciliationAsAtHBPaymentDate;
	}

	public void setReconciliationAsAtHBPaymentDate(String reconciliationAsAtHBPaymentDate) {
		this.reconciliationAsAtHBPaymentDate = addQuotes(reconciliationAsAtHBPaymentDate);
	}

	public String getStatementAsAtDate() {
		return statementAsAtDate;
	}

	public void setStatementAsAtDate(String statementAsAtDate) {
		if (Utils.isValidDate(statementAsAtDate)) {
			this.statementAsAtDate = addQuotes(statementAsAtDate);
		} else {
			System.err.println("Couldn't parse the statementAsAtDate [" + statementAsAtDate
					+ "]. Is the field right or has the format changed?");
		}
	}

	public String getTotalOutstandingBalance() {
		return totalOutstandingBalance;
	}

	public void setTotalOutstandingBalance(String totalOutstandingBalance) {
		this.totalOutstandingBalance = addQuotes(totalOutstandingBalance);
	}

	public List<String> getSincePaidBalances() {
		return sincePaidBalances;
	}

	public void setSincePaidBalances(List<String> sincePaidBalances) {
		sincePaidBalances.forEach(a -> addQuotes(a));
		this.sincePaidBalances = sincePaidBalances;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statementAsAtDate == null) ? 0 : statementAsAtDate.hashCode());
		result = prime * result + ((businessName == null) ? 0 : businessName.hashCode());
		result = prime * result
				+ ((reconciliationAsAtHBPaymentDate == null) ? 0 : reconciliationAsAtHBPaymentDate.hashCode());
		result = prime * result + ((sincePaidBalances == null) ? 0 : sincePaidBalances.hashCode());
		result = prime * result + ((totalOutstandingBalance == null) ? 0 : totalOutstandingBalance.hashCode());
		result = prime * result + ((vendorCode == null) ? 0 : vendorCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataHolder other = (DataHolder) obj;
		if (statementAsAtDate == null) {
			if (other.statementAsAtDate != null)
				return false;
		} else if (!statementAsAtDate.equals(other.statementAsAtDate))
			return false;
		if (businessName == null) {
			if (other.businessName != null)
				return false;
		} else if (!businessName.equals(other.businessName))
			return false;
		if (reconciliationAsAtHBPaymentDate == null) {
			if (other.reconciliationAsAtHBPaymentDate != null)
				return false;
		} else if (!reconciliationAsAtHBPaymentDate.equals(other.reconciliationAsAtHBPaymentDate))
			return false;
		if (sincePaidBalances == null) {
			if (other.sincePaidBalances != null)
				return false;
		} else if (!sincePaidBalances.equals(other.sincePaidBalances))
			return false;
		if (totalOutstandingBalance == null) {
			if (other.totalOutstandingBalance != null)
				return false;
		} else if (!totalOutstandingBalance.equals(other.totalOutstandingBalance))
			return false;
		if (vendorCode == null) {
			if (other.vendorCode != null)
				return false;
		} else if (!vendorCode.equals(other.vendorCode))
			return false;
		return true;
	}

}
