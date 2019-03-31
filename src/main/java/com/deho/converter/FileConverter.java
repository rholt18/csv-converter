package com.deho.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileConverter {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			System.err.println("Need to supply the base directory of the files");
			System.exit(-1);
		}
		String baseDir = args[0];
		String inputDir = baseDir + "input-csvs/"; // "/apps/converter/input-csvs/"
		String datetimeExt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + "/";
		String outputDir = baseDir + "output-csvs/"	+ datetimeExt;
		String failedProcessingDir = baseDir + "not-processed/"	+ datetimeExt;
		
		createDirIfMissing(outputDir);
		createDirIfMissing(failedProcessingDir);
		
		parseCSVDocument(inputDir, outputDir, failedProcessingDir);
		
	}
	
	public static boolean debugOutput() {
		return false;
	}

	private static void createDirIfMissing(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			boolean mkdirs = dir.mkdirs();
			if (mkdirs) {
				System.out.println("Created directory " + dir);
			} else {
				System.out.println("FAILED to create directory " + dir + ". So TERMINATING run");
				System.exit(-1);
			}
		}
	}

	private static void parseCSVDocument(String inputDir, String outputDir, String failedProcessingDir) {
		List<Template> templatesFailedDuringProcessing = new ArrayList<Template>();
		List<Template> templates = TemplateFactory.getTemplates(inputDir);
		for (Template template : templates) {
			try (Stream<String> stream = Files
					.lines(Paths.get(inputDir + template.getData().getFileName()))) {

				List<String> linesList = stream.collect(Collectors.toList());
//				String[] lines = linesList.toArray(new String[linesList.size()]);
				String[] lines = CSV.removeEmptyLines(linesList);
				DataHolder data = template.parseText(lines);
//				System.out.println(data);
				if (data.isComplete()) {
					createOutput(data, outputDir);
				} else {
					System.err.println("The data was not completed for file [" + template.getData().getFileName() + "] so adding it to the Files Not Processed folder");
					templatesFailedDuringProcessing.add(template);
					try {
						copyCSVToDir(failedProcessingDir, inputDir, template.getData().getFileName());
					} catch (IOException e1) {
						System.err.println("Failed to copy the CSV that failed processing into the failed processing folder, blimey!");
						System.err.println("Failed processing folder [" + failedProcessingDir + "]");
						System.err.println("Failed file that was trying to copy [" + (inputDir + template.getData().getFileName()) + "]");
						e1.printStackTrace();
					}
				}

			} catch (IOException e) {
				templatesFailedDuringProcessing.add(template);
				e.printStackTrace();
				try {
					copyCSVToDir(failedProcessingDir, inputDir, template.getData().getFileName());
				} catch (IOException e1) {
					System.err.println("Failed to copy the CSV that failed processing into the failed processing folder, blimey!");
					System.err.println("Failed processing folder [" + failedProcessingDir + "]");
					System.err.println("Failed file that was trying to copy [" + (inputDir + template.getData().getFileName()) + "]");
					e1.printStackTrace();
				}
			}
		}
		List<String> templatesnotfound = TemplateFactory.getTemplatesnotfound();
		int totalTemplatesToProcess = templates.size() + templatesnotfound.size();
		int totalTemplatesProcessed = templates.size() - templatesFailedDuringProcessing.size();

		System.out.println("---- SUMMARY ----");
		System.out.println("Input directory for the files [" + inputDir + "]");
		System.out.println("[" + totalTemplatesToProcess + "] input files found to process");
		System.out.println("[" + templatesnotfound.size() + "] input files not yet automated");
		System.out.println("[" + totalTemplatesProcessed + "] input files successfully processed and moved to [" + outputDir + "]");
		System.out.println("[" + templatesFailedDuringProcessing.size() + "] input files failed processing and moved to [" + failedProcessingDir + "]");
		System.out.println("\t---- Failed file conversions ----");
		for (Template template : templatesFailedDuringProcessing) {
			System.out.println(template.getData().getFileName());
		}
	}

	private static void copyCSVToDir(String failedProcessingDir, String inputDir, String fileName) throws IOException {
		Files.copy(Paths.get(inputDir + fileName), Paths.get(failedProcessingDir + fileName), StandardCopyOption.REPLACE_EXISTING);
	}

	private static void createOutput(DataHolder data, String outputDir) throws FileNotFoundException {
		System.out.println(data);
		convertToCSV(data, outputDir);
	}

	private static void convertToCSV(DataHolder data, String outputDir) throws FileNotFoundException {
		File outputFileName = new File(data.getFileName());
		try (PrintWriter pw = new PrintWriter(outputDir + outputFileName)) {
			pw.println(
					"Vendor Code, Business Name, Reconciliation As At HB Date, Statement As At Date, Total Outstanding Balance, Since Paid Balance");

			List<String> sincePaidBalances = data.getSincePaidBalances();
			if (sincePaidBalances.isEmpty()) {
				pw.println(getCSVOutputLine(data, ""));
			} else {
				for (String sincePaidBalance : sincePaidBalances) {
					pw.println(getCSVOutputLine(data, sincePaidBalance));
				}
			}
			pw.flush();
			pw.close();
		}

		System.out.println("Created output CSV " + outputDir + outputFileName);
	}

	private static String getCSVOutputLine(DataHolder data, String sincePaidBalance) {
		return data.getVendorCode() + "," + data.getBusinessName() + "," + data.getReconciliationAsAtHBPaymentDate()
				+ "," + data.getStatementAsAtDate() + "," + data.getTotalOutstandingBalance() + "," + sincePaidBalance;
	}

}
