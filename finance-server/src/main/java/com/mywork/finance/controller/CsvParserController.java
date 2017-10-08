package com.mywork.finance.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mywork.finance.service.CsvParserService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "/csv-parser")
@Slf4j
public class CsvParserController {

	@Autowired
	CsvParserService csvParserService;

	@PostMapping(value = "/bytes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<byte[]> parseBytes(@RequestBody byte[] incomeStream) {
		if(incomeStream == null || incomeStream.length == 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		log.info("Response for bytes parsing recieved!");
		byte[] outStream = csvParserService.getParsedCsvStream(incomeStream);
		if (outStream == null || outStream.length == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		}
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("Content-Length", Long.toString(outStream.length));
		log.debug("Byte's array was read and parsed.");
		return new ResponseEntity<byte[]>(outStream, headers, HttpStatus.OK);
	}
	
	@PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<File> parseFile(@RequestBody File incomeFile) {
		log.info("Response for file parsing recieved!");
		if(incomeFile == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		File outFile = csvParserService.getParsedCsvFile(incomeFile);
		log.debug("File was read and parsed.");
		return new ResponseEntity<File>(outFile, HttpStatus.OK);
	}
	
	@PostMapping(value = "/string", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> parseString(@RequestBody String incomeString) {
		
		if (incomeString.contains("data:application/vnd.ms-excel;base64,")) {
			incomeString = incomeString.replaceAll("data:application/vnd.ms-excel;base64,", "");
		}
		log.info("Response for string parsing recieved!");
		if(incomeString == null || incomeString.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		String outString = csvParserService.getParsedCsvString(incomeString);
		log.debug("File was read and parsed.");
		return new ResponseEntity<String>(outString, HttpStatus.OK);
	}
	
	@PostMapping(value = "/stream", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<byte[]> parseStringToBytes(@RequestBody String incomeString) {
		log.info("Response for string to bytes parsing to bytes recieved!");
		if(incomeString == null || incomeString.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (incomeString.contains("data:application/vnd.ms-excel;base64,")) {
			incomeString = incomeString.replaceAll("data:application/vnd.ms-excel;base64,", "");
		}
		byte[] outStream = csvParserService.getParsedStringToBytes(incomeString);
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("Content-Length", Long.toString(outStream.length));
		log.debug("Byte's array was read and parsed.");
		return new ResponseEntity<byte[]>(outStream, headers, HttpStatus.OK);
	}
}