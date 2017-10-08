package com.mywork.finance.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:/webapp/app-context.xml" })
@Configurable
public class CsvParserServiceTest {

	String charset;
	String filepath = "files/test/testFileUTF8.csv";
	@Autowired
	CsvParserService csvFileParser;
	
	@Test
	public void testGetParsedFile() throws IOException {
		File file = new File(filepath);
		InputStreamReader r = new InputStreamReader(new FileInputStream(file));
		this.charset = r.getEncoding();
		r.close();
		File outFile = csvFileParser.getParsedCsvFile(file);
		assertNotNull(outFile);
		String sep = System.getProperty("file.separator");
		assertEquals("files"+sep+"outFile.csv", outFile.getPath());
	}
	
	@Test
	public void testGetParsedString() throws IOException {
		File file = new File(filepath);
		InputStreamReader r = new InputStreamReader(new FileInputStream(file));
		this.charset = r.getEncoding();
		r.close();
		byte[] data = Files.readAllBytes(file.toPath());
		String incomeString = Base64.getEncoder().encodeToString(data);
		String parsedString = csvFileParser.getParsedCsvString(incomeString);	
		assertNotNull(parsedString);
		byte[] decodedBytes = Base64.getDecoder().decode(parsedString);
		String decodedString = new String(decodedBytes, charset);
		assertTrue(decodedString.contains("ДПІ у Переському р-ні ГУ ДКСУ у м.Києві"));
	}
	
	@Test
	public void testGetParsedBytes() throws IOException {
		File file = new File(filepath);
		InputStreamReader r = new InputStreamReader(new FileInputStream(file));
		this.charset = r.getEncoding();
		r.close();
		byte[] data = Files.readAllBytes(file.toPath());
		byte[] outData = csvFileParser.getParsedCsvStream(data);
		assertNotNull(outData);
		String outString = new String(outData, charset);
		assertTrue(outString.contains("ДПІ у Переському р-ні ГУ ДКСУ у м.Києві"));
	}
	
	@Test
	public void testGetParsedStringToBytes() throws IOException {
		File file = new File(filepath);
		InputStreamReader r = new InputStreamReader(new FileInputStream(file));
		this.charset = r.getEncoding();
		r.close();
		byte[] data = Files.readAllBytes(file.toPath());
		String incomeString = Base64.getEncoder().encodeToString(data);
		byte[] outData = csvFileParser.getParsedStringToBytes(incomeString);
		assertNotNull(outData);
		String outString = new String(outData, charset);
		assertTrue(outString.contains("ДПІ у Переському р-ні ГУ ДКСУ у м.Києві"));
	}
}