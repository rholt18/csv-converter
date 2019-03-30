package com.deho.converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TemplateFactory {

	private static final String PACKAGE_NAME = "com.deho.converter.templates.";

	private static final List<Template> templates = new ArrayList<Template>();
	
	private static final List<String> templatesNotFound = new ArrayList<>();

	public static List<Template> getTemplates(String inputDirectory) {
		addInputFileNames(inputDirectory);
		return templates;
	}
	
	public static String getPackageName() {
		return PACKAGE_NAME;
	}

	public static List<Template> getTemplates() {
		return templates;
	}

	public static List<String> getTemplatesnotfound() {
		return templatesNotFound;
	}

	public static void addInputFileNames(String inputDirectory) {
		try {
			Files.list(Paths.get(inputDirectory)).filter(Files::isRegularFile).forEach(e -> addTemplate(e));
		} catch (IOException e) {
			System.err.println("Input File Directory [" + inputDirectory + "] can't be found");
			e.printStackTrace();
		}
	}

	private static void addTemplate(Path pathName) {
//		System.out.println(pathName);
		String fileName = pathName.getFileName().toString().replaceAll(" ", "").toUpperCase();
		String name = fileName.substring(0, fileName.indexOf("."));
//		System.out.println(name);
		Template template = fromString(PACKAGE_NAME + "Template_" + name);
		if (template != null) {
			templates.add(template);
		}
	}

	public static Template fromString(String templateName) {
		try {
			return (Template) Class.forName(templateName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			templatesNotFound.add(templateName);
			System.err.println("Couldn't instantiate class " + templateName);
			return null;
		}
	}

}
