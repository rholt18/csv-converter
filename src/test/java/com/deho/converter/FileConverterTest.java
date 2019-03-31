package com.deho.converter;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class FileConverterTest {

	public static final boolean DEBUG = true;
	
	private String baseDir = "src/test/resources/test-csvs/";
	private String inputDir = baseDir + "input-csvs/";
	private String outputDir = baseDir + "created-csvs/";
	private String expectedDir = baseDir + "output-csvs/";
	private String failedProcessingDir = baseDir + "not-processed/";
	private List<Template> templates;

	@Before
	public void setUp() {
		File createdCSVsDir = new File(outputDir);
		try {
			FileUtils.cleanDirectory(createdCSVsDir);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Couldn't clear down the previous directory for the created output CSVs [" + outputDir + "]");
		}

		if (createdCSVsDir.list().length > 0) {
			fail("Failed to clear down the previous directory for the created output CSVs [" + outputDir + "]");
		}
		
		templates = TemplateFactory.getTemplates(inputDir);
	}

	@Test
	public void testProcessFile() {
		List<Template> templatesFailedDuringProcessing = new ArrayList<Template>();
		for (Template template : templates) {
			FileConverter.processFile(inputDir, outputDir, failedProcessingDir, templatesFailedDuringProcessing, template);
		}
		try {
			compareCreatedCSVsToExpected();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void compareCreatedCSVsToExpected() throws IOException {
		
		for (Template template : templates) {
			String fileName = template.getData().getFileName();
			List<String> expectedLines = readFile(expectedDir + fileName);
			List<String> actualLines = readFile(outputDir + fileName);
			
			StringBuilder expected = new StringBuilder();
			expectedLines.forEach(expected::append);
			
			StringBuilder actual = new StringBuilder();
			actualLines.forEach(actual::append);

			if (DEBUG) {
				System.out.println("Expected output for file " + fileName + ":\n" + expected.toString());
				System.out.println("Actual output for file " + fileName + ":\n" + actual.toString());
			}
			
			if (!actual.toString().equals(expected.toString())) {
				fail("The actual output from processing file " + fileName + " didn't match the expected output");
				System.out.println("Expected:\n" + expected.toString());
				System.out.println("Actual:\n" + actual.toString());
			}
		}
	}

	private List<String> readFile(String fileName) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			return stream.collect(Collectors.toList());
		}
	}

}
