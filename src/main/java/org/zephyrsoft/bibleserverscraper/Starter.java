package org.zephyrsoft.bibleserverscraper;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter {
	private static final Logger LOG = LoggerFactory.getLogger(Starter.class);

	public static void main(String args[]) {
		if (args.length < 1 || directoryNotCorrect(args[0])) {
			LOG.error("please provide an existing target directory as first parameter");
		} else {
			String mainDirectory = args[0];
			String rawDirectory = subdir(mainDirectory, "raw");
			String mySwordDirectory = subdir(mainDirectory, "mysword");
			String openlpDirectory = subdir(mainDirectory, "openlp");
			String latexDirectory = subdir(mainDirectory, "latex");

			// fetch the data
			Scraper scraper = new Scraper();
			scraper.scrape(rawDirectory);
			
			// export bibles in OpenLP format
			OpenLPExporter openlpExporter = new OpenLPExporter();
			openlpExporter.export(rawDirectory, openlpDirectory);
			
			// export bibles in MySword format
			MySwordExporter mySwordExporter = new MySwordExporter();
			mySwordExporter.export(rawDirectory, mySwordDirectory);
			
			// export LaTeX document for comparing the translations
			//LaTeXExporter latexExporter = new LaTeXExporter();
			//latexExporter.export(rawDirectory, latexDirectory);
		}
	}

	private static boolean directoryNotCorrect(String dir) {
		File directory = new File(dir);
		return !directory.exists() || !directory.isDirectory() || !directory.canWrite();
	}

	private static String subdir(String mainDirectory, String subDirectory) {
		String subdirName = mainDirectory + File.separator + subDirectory;
		File subdir = new File(subdirName);
		subdir.mkdirs();
		return subdirName;
	}

}
