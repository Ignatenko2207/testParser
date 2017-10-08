package com.mywork.finance.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mywork.finance.model.CsvBO;
import com.mywork.finance.model.CsvOutObj;
import com.mywork.finance.service.CsvParserService;
import com.opencsv.CSVReader;

@Service
public class CsvParserServiceImpl implements CsvParserService{

//	static String charset = "UTF-8";
	String charset;
	
	@Override
	public byte[] getParsedStringToBytes(String incomeString) {
		String outText = "Дата документу, Сума, Контрагент, Призначення платежу\n";
		byte[] decodedBytes = Base64.getDecoder().decode(incomeString);
		InputStreamReader r = new InputStreamReader(new ByteArrayInputStream(decodedBytes));
		this.charset = r.getEncoding();
		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String decodedString = "";
		try {
			decodedString = new String(decodedBytes, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] records = decodedString.split("\n");
		for (String row : records) {
			if(row.equals(records[0])) {
				continue;
			}
			String[] fields = row.split(";");
			CsvBO csvBO = convertToBo(fields);
			CsvOutObj csvOut = convertToOut(csvBO);
			outText += makeTextToWrite(csvOut);
		}
		byte[] outBytes = null;
		outBytes = outText.getBytes();
		try {
			outBytes = outText.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return outBytes;
	}
	
	@Override
	public File getParsedCsvFile(File incomeFile) {
		InputStreamReader r;
		try {
			r = new InputStreamReader(new FileInputStream(incomeFile));
			this.charset = r.getEncoding();
			r.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		File outFile = new File("files/outFile.csv");
		try {
			FileWriter writer = new FileWriter(outFile);
			CSVReader reader = new CSVReader(new FileReader(incomeFile), ';');
			String outText = "Дата документу, Сума, Контрагент, Призначення платежу\n";
			List<String[]> rows = reader.readAll();
			for (String[] row : rows) {
				if(row.equals(rows.get(0))) { //remove headers
					continue;
				}
				CsvBO csvBO = convertToBo(row);
				CsvOutObj csvOut = convertToOut(csvBO);
				outText += makeTextToWrite(csvOut);
			}
			writer.write(outText);
			writer.flush();			
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outFile;
	}
	
	@Override
	public String getParsedCsvString(String incomeString) {
		String outText = "Дата документу, Сума, Контрагент, Призначення платежу\n";
		byte[] decodedBytes = Base64.getDecoder().decode(incomeString);
		InputStreamReader r = new InputStreamReader(new ByteArrayInputStream(decodedBytes));
		this.charset = r.getEncoding();
		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String decodedString = "";
		try {
			decodedString = new String(decodedBytes, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] records = decodedString.split("\n");
		for (String row : records) {
			if(row.equals(records[0])) {
				continue;
			}
			String[] fields = row.split(";");
			CsvBO csvBO = convertToBo(fields);
			CsvOutObj csvOut = convertToOut(csvBO);
			outText += makeTextToWrite(csvOut);
		}
		String encodedString = "";
		try {
			encodedString = Base64.getEncoder().encodeToString(outText.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedString;
	}
	
	@Override
	public byte[] getParsedCsvStream(byte[] incomeStream) {
		InputStreamReader r = new InputStreamReader(new ByteArrayInputStream(incomeStream));
		this.charset = r.getEncoding();
		try {
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String outText = "Дата документу, Сума, Контрагент, Призначення платежу\n";
		if (incomeStream == null || incomeStream.length == 0) {
			return new byte[0];
		}
		String decodedString = "";
		try {
			decodedString = new String(incomeStream, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] records = decodedString.split("\n");
		for (String row : records) {
			if(row.equals(records[0])) {
				continue;
			}
			String[] fields = row.split(";");
			CsvBO csvBO = convertToBo(fields);
			CsvOutObj csvOut = convertToOut(csvBO);
			outText += makeTextToWrite(csvOut);
		}
		byte[] outStream = null;
		try {
			outStream = outText.getBytes(charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outStream;
	}
	
	private String makeTextToWrite(CsvOutObj csvOut) {
		String corFormated = csvOut.getCorName().replaceAll("\"\"", "");
		return csvOut.getDocDate() + ", " + csvOut.getAmount() + ", " + corFormated + ", "
				+ csvOut.getPaymentDescription() + "\n";
	}

	private CsvOutObj convertToOut(CsvBO csvBO) {
		CsvOutObj csvOutObj = new CsvOutObj();
		csvOutObj.setCorName(csvBO.getCorName());
		csvOutObj.setDocDate(csvBO.getDocDate());
		csvOutObj.setPaymentDescription(csvBO.getPaymentDescription());
		if (csvBO.getDebet() == null || csvBO.getDebet().isEmpty())
			csvBO.setDebet("0");
		if (csvBO.getCredit() == null || csvBO.getCredit().isEmpty())
			csvBO.setCredit("0");
		double debet = Double.parseDouble(csvBO.getDebet());
		double credit = Double.parseDouble(csvBO.getCredit());
		double amount = credit - debet;
		String amountInString = String.valueOf(amount);
		csvOutObj.setAmount(amountInString.replaceAll(",", "."));
		return csvOutObj;
	}

	private CsvBO convertToBo(String[] fields) {
		CsvBO csvBO = new CsvBO();
		csvBO.setUSREOU(fields[0]);
		csvBO.setMFO(fields[1]);
		csvBO.setCurrentAccount(fields[2]);
		csvBO.setCurrency(fields[3]);
		csvBO.setOperationDate(fields[4]);
		csvBO.setOperationCode(fields[5]);
		csvBO.setCorBankMFO(fields[6]);
		csvBO.setCorBankName(fields[7]);
		csvBO.setCorCurrentAccount(fields[8]);
		csvBO.setCorUSREOU(fields[9]);
		csvBO.setCorName(fields[10]);
		csvBO.setDocNumber(fields[11]);
		csvBO.setDocDate(fields[12]);
		csvBO.setDebet(fields[13]);
		csvBO.setCredit(fields[14]);
		csvBO.setPaymentDescription(fields[15]);
		csvBO.setUAHCoverage(fields[16]);
		return csvBO;
	}
}
