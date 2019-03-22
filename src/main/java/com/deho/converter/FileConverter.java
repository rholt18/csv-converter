package com.deho.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileConverter {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			System.err.println("Need to supply the dirctory of the input files");
			System.exit(-1);
		}
		String inputDirectory = args[0];
//			String inputDirectory = "/apps/converter/test/input/";
		String outputDirectory = inputDirectory + "output_"
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + "/";

		System.out.println("Input directory for the files is " + inputDirectory);
		System.out.println("Output directory for the files will be " + outputDirectory);

		File outputDir = new File(outputDirectory);
		if (!outputDir.exists()) {
			boolean mkdirs = outputDir.mkdirs();
			if (mkdirs) {
				System.out.println("Created output directory " + outputDirectory);
			} else {
				System.out.println("FAILED to create output directory " + outputDirectory + ". So TERMINATING run");
				System.exit(-1);
			}

			parseCSVDocument(inputDirectory, outputDirectory);

		}
	}

	private static void parseCSVDocument(String inputDirectory, String outputDirectory) {
		List<Template> templatesFailedDuringProcessing = new ArrayList<Template>();
		List<Template> templates = TemplateFactory.getTemplates(inputDirectory);
		for (Template template : templates) {
			try (Stream<String> stream = Files.lines(Paths.get(inputDirectory + template.getData().getInputFileName()))) {

				List<String> linesList = stream.collect(Collectors.toList());
//				String[] lines = linesList.toArray(new String[linesList.size()]);
				String[] lines = CSV.removeBlankLines(linesList);
				DataHolder data = template.parseText(lines);
				System.out.println(data);
				createOutput(data, outputDirectory);

			} catch (IOException e) {
				templatesFailedDuringProcessing.add(template);
				e.printStackTrace();
			}
		}
		List<String> templatesnotfound = TemplateFactory.getTemplatesnotfound();
		int totalTemplatesToProcess = templates.size() + templatesnotfound.size();
		int totalTemplatesProcessed = templates.size() - templatesFailedDuringProcessing.size();
		
		System.out.println("---- SUMMARY ----");
		System.out.println("File input directory [" + inputDirectory + "]");
		System.out.println("[" + totalTemplatesToProcess + "] input files found to process");
		System.out.println("[" + totalTemplatesProcessed + "] input files successfully processed");
		System.out.println("[" + templatesnotfound.size() + "] input files not yet automated");
		System.out.println("[" + templatesFailedDuringProcessing.size() + "] input files failed processing");
		System.out.println("\t---- Failed file conversions ----");
		for(Template template : templatesFailedDuringProcessing) {
			System.out.println(template.getData().getInputFileName());
		}
	}
	
	private static void createOutput(DataHolder data, String outputDirectory) throws FileNotFoundException {
		System.out.println(data);
		convertToCSV(data, outputDirectory);
	}

	private static void convertToCSV(DataHolder data, String outputDirectory) throws FileNotFoundException {
		File outputFileName = new File(data.getOutputFileName());
		try (PrintWriter pw = new PrintWriter(outputDirectory + outputFileName)) {
			pw.println("Vendor Code, Business Name, Reconciliation As At HB Date, Statement As At Date, Total Outstanding Balance, Since Paid Balance");
			
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
		
		System.out.println("Wrote output CSV " + outputDirectory + outputFileName);
	}

	private static String getCSVOutputLine(DataHolder data, String sincePaidBalance) {
		return data.getVendorCode() + ","
		+ data.getBusinessName() + ","
		+ data.getReconciliationAsAtHBPaymentDate() + ","
		+ data.getStatementAsAtDate() + ","
		+ data.getTotalOutstandingBalance() + ","
		+ sincePaidBalance;
	}
	
}
