package com.mywork.finance.service;

import java.io.File;

public interface CsvParserService {

	public byte[] getParsedCsvStream(byte[] incomeStream);
	
	public File getParsedCsvFile(File incomeFile);

	public String getParsedCsvString(String incomeString);

	public byte[] getParsedStringToBytes(String incomeString);
}
