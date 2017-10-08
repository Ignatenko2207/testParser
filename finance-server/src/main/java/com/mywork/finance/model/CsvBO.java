package com.mywork.finance.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CsvBO {

	private String USREOU; //Unified State Register of enterprises and organizations of Ukraine
	private String MFO; // MFO (Bank branch location code )
	private String currentAccount;
	private String currency;
	private String operationDate;
	private String operationCode;
	private String corBankMFO;
	private String corBankName;
	private String corCurrentAccount;
	private String corUSREOU;
	private String corName;
	private String docNumber;
	private String docDate;
	private String debet;
	private String credit;
	private String paymentDescription;
	private String UAHCoverage;
	
}
